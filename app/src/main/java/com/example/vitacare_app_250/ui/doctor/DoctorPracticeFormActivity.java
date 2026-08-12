package com.example.vitacare_app_250.ui.doctor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.core.Result;
import com.example.vitacare_app_250.data.model.Doctor;
import com.example.vitacare_app_250.data.model.Role;
import com.example.vitacare_app_250.data.model.Specialty;
import com.example.vitacare_app_250.data.repo.DoctorRepository;
import com.example.vitacare_app_250.databinding.ActivityDoctorPracticeFormBinding;
import com.example.vitacare_app_250.ui.auth.LandingActivity;
import com.example.vitacare_app_250.ui.base.BaseActivity;
import com.example.vitacare_app_250.ui.common.FormField;
import com.example.vitacare_app_250.util.Validators;

import java.util.ArrayList;
import java.util.List;

/**
 * Publishes or edits the signed-in doctor's practice profile.
 *
 * <p>This is the screen that was previously {@code DoctorDashboard}. Three
 * things about it were wrong beyond presentation:
 *
 * <ul>
 *   <li>It wrote to {@code docs/<specialty>/<random UUID>} on every submit, so
 *       each save created a new listing. The record is now keyed by uid.
 *   <li>It never loaded existing data, so there was no way to correct a typo —
 *       only to publish another copy. It now prefills from the current profile.
 *   <li>Its validation required name, contact and focus area but not the
 *       specialty in a usable way: the check compared the spinner's label
 *       against "select specialist" while the value written was that label
 *       lower-cased, so any other unmapped label wrote a listing to a specialty
 *       node nothing reads.
 * </ul>
 */
public class DoctorPracticeFormActivity extends BaseActivity {

    private static final String EXTRA_DOCTOR_ID = "doctor_id";
    private static final String EXTRA_NAME = "name";
    private static final String EXTRA_CONTACT = "contact";
    private static final String EXTRA_DEGREE = "degree";
    private static final String EXTRA_CHAMBER = "chamber";
    private static final String EXTRA_SPECIALTY = "specialty";
    private static final String EXTRA_FOCUS = "focus";

    private ActivityDoctorPracticeFormBinding views;
    private DoctorRepository doctorRepository;
    private FormField.Form form;
    private FormField nameField;
    private FormField focusField;
    private FormField contactField;
    private FormField degreeField;
    private FormField chamberField;

    private final List<Specialty> specialties = new ArrayList<>();
    private @Nullable Specialty selectedSpecialty;
    /** Where the profile was filed before an edit, so a move can clean up. */
    private @Nullable Specialty originalSpecialty;
    private boolean editing;
    private boolean submitting;

    /** Opens the form empty, to publish a first profile. */
    public static @NonNull Intent create(@NonNull Context context) {
        return new Intent(context, DoctorPracticeFormActivity.class);
    }

    /** Opens the form prefilled with {@code doctor}'s current details. */
    public static @NonNull Intent edit(@NonNull Context context, @NonNull Doctor doctor) {
        return new Intent(context, DoctorPracticeFormActivity.class)
                .putExtra(EXTRA_DOCTOR_ID, doctor.getId())
                .putExtra(EXTRA_NAME, doctor.getName())
                .putExtra(EXTRA_CONTACT, doctor.getContact())
                .putExtra(EXTRA_DEGREE, doctor.getDegree())
                .putExtra(EXTRA_CHAMBER, doctor.getChamber())
                .putExtra(EXTRA_SPECIALTY, doctor.getSpecialist())
                .putExtra(EXTRA_FOCUS, doctor.getSpeciality());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (session().currentRole() != Role.DOCTOR) {
            showMessage(R.string.auth_session_expired);
            restartAt(LandingActivity.class);
            return;
        }

        views = ActivityDoctorPracticeFormBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        applyInsets(views.appBar, Edge.TOP, Edge.LEFT, Edge.RIGHT);
        applyInsets(views.scroll, Edge.BOTTOM, Edge.LEFT, Edge.RIGHT);

        editing = getIntent().hasExtra(EXTRA_DOCTOR_ID);
        setUpToolbar(views.toolbar, editing
                ? R.string.doctor_form_title_edit
                : R.string.doctor_form_title_create);

        doctorRepository = new DoctorRepository();

        setUpSpecialtyPicker();
        setUpValidation();
        prefillFromIntent();

        views.submit.setOnClickListener(v -> submit());
    }

    private void setUpSpecialtyPicker() {
        specialties.clear();
        List<String> labels = new ArrayList<>();
        for (Specialty specialty : Specialty.values()) {
            specialties.add(specialty);
            labels.add(getString(specialty.labelRes()));
        }
        views.specialty.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, labels));
        views.specialty.setOnItemClickListener((parent, view, position, id) -> {
            selectedSpecialty = specialties.get(position);
            views.specialtyLayout.setError(null);
        });
    }

    private void setUpValidation() {
        form = new FormField.Form();
        nameField = form.addField(views.nameLayout, Validators::fullName);
        focusField = form.addField(views.focusLayout, Validators::required);
        contactField = form.addField(views.contactLayout, Validators::phone);
        // Qualifications and chamber address are genuinely optional — a doctor
        // may not hold a listed degree, and not every practice has a chamber.
        degreeField = FormField.of(views.degreeLayout, value -> Validators.VALID);
        chamberField = FormField.of(views.chamberLayout, value -> Validators.VALID);
    }

    private void prefillFromIntent() {
        Intent intent = getIntent();
        nameField.setText(intent.getStringExtra(EXTRA_NAME));
        focusField.setText(intent.getStringExtra(EXTRA_FOCUS));
        contactField.setText(intent.getStringExtra(EXTRA_CONTACT));
        degreeField.setText(intent.getStringExtra(EXTRA_DEGREE));
        chamberField.setText(intent.getStringExtra(EXTRA_CHAMBER));

        originalSpecialty = Specialty.fromKey(intent.getStringExtra(EXTRA_SPECIALTY));
        selectedSpecialty = originalSpecialty;
        if (selectedSpecialty != null) {
            // false: do not filter the dropdown down to the current value.
            views.specialty.setText(getString(selectedSpecialty.labelRes()), false);
        }

        if (!editing) {
            // Seed the name from the account so the common case is one tap
            // less; still editable.
            String cached = session().displayName();
            if (cached != null && nameField.value().isEmpty()) {
                nameField.setText(cached);
            }
        }
    }

    private void submit() {
        if (submitting) {
            return;
        }
        hideKeyboard();

        boolean fieldsValid = form.validate();
        boolean specialtyValid = selectedSpecialty != null;
        if (!specialtyValid) {
            views.specialtyLayout.setError(getString(R.string.error_select_specialty));
        }
        if (!fieldsValid || !specialtyValid) {
            return;
        }

        String uid = session().uid();
        if (uid == null) {
            showMessage(R.string.auth_session_expired);
            restartAt(LandingActivity.class);
            return;
        }

        Doctor doctor = new Doctor(
                uid,
                nameField.value(),
                contactField.value(),
                degreeField.value(),
                chamberField.value(),
                selectedSpecialty.key(),
                focusField.value());

        setSubmitting(true);
        doctorRepository.saveOwnProfile(
                uid, doctor, selectedSpecialty, originalSpecialty, this::onSaved);
    }

    private void onSaved(@NonNull Result<Void> result) {
        if (!isAlive()) {
            return;
        }
        setSubmitting(false);

        if (result.isSuccess()) {
            session().cacheDisplayName(nameField.value());
            showMessage(R.string.doctor_form_saved);
            finish();
        } else {
            showError(result.error(), this::submit);
        }
    }

    private void setSubmitting(boolean value) {
        submitting = value;
        views.submit.setEnabled(!value);
        views.nameLayout.setEnabled(!value);
        views.specialtyLayout.setEnabled(!value);
        views.focusLayout.setEnabled(!value);
        views.contactLayout.setEnabled(!value);
        views.degreeLayout.setEnabled(!value);
        views.chamberLayout.setEnabled(!value);
        views.progress.setVisibility(value ? View.VISIBLE : View.INVISIBLE);
    }
}
