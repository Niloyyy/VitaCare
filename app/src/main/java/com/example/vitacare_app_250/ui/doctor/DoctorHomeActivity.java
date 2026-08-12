package com.example.vitacare_app_250.ui.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.core.Result;
import com.example.vitacare_app_250.data.model.Doctor;
import com.example.vitacare_app_250.data.model.Role;
import com.example.vitacare_app_250.data.model.Specialty;
import com.example.vitacare_app_250.data.repo.DoctorRepository;
import com.example.vitacare_app_250.databinding.ActivityDoctorHomeBinding;
import com.example.vitacare_app_250.ui.auth.LandingActivity;
import com.example.vitacare_app_250.ui.base.BaseActivity;
import com.example.vitacare_app_250.ui.common.InfoRow;
import com.example.vitacare_app_250.ui.directory.DoctorDetailActivity;
import com.example.vitacare_app_250.ui.patient.PatientHomeActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * The doctor's view of their own listing.
 *
 * <p>Loads the profile filed under their uid and presents one of two states:
 * not yet published, or published with the details currently visible to
 * patients. Both offer the single action that makes sense from there.
 */
public class DoctorHomeActivity extends BaseActivity {

    private ActivityDoctorHomeBinding views;
    private DoctorRepository doctorRepository;
    private @Nullable Doctor profile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!enforceAccess()) {
            return;
        }

        views = ActivityDoctorHomeBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        applyInsets(views.appBar, Edge.TOP, Edge.LEFT, Edge.RIGHT);
        applyInsets(views.scroll, Edge.BOTTOM, Edge.LEFT, Edge.RIGHT);

        setSupportActionBar(views.toolbar);
        views.toolbar.setOnMenuItemClickListener(this::onMenuItemSelected);

        doctorRepository = new DoctorRepository();
        bindGreeting();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (enforceAccess()) {
            // Reload on every entry so returning from the form shows the
            // profile as it was actually saved, not as it was submitted.
            loadProfile();
        }
    }

    private boolean enforceAccess() {
        Role role = session().currentRole();
        if (role == Role.DOCTOR) {
            return true;
        }
        if (role == Role.PATIENT) {
            restartAt(PatientHomeActivity.class);
        } else {
            showMessage(R.string.auth_session_expired);
            restartAt(LandingActivity.class);
        }
        return false;
    }

    private void bindGreeting() {
        String name = session().displayName();
        views.greeting.setText(name == null
                ? getString(R.string.doctor_home_greeting_generic)
                : getString(R.string.doctor_home_greeting, name));
    }

    private void loadProfile() {
        String uid = session().uid();
        if (uid == null) {
            return;
        }
        views.state.showLoading();
        views.statusCard.setVisibility(View.GONE);
        views.profileGroup.setVisibility(View.GONE);

        doctorRepository.loadOwnProfile(uid, this::onProfileLoaded);
    }

    private void onProfileLoaded(@NonNull Result<Doctor> result) {
        if (!isAlive()) {
            return;
        }
        if (!result.isSuccess()) {
            views.state.showError(result.error(), this::loadProfile);
            return;
        }

        views.state.showContent();
        views.statusCard.setVisibility(View.VISIBLE);
        profile = result.value();

        if (profile == null) {
            showUnpublished();
        } else {
            showPublished(profile);
        }
    }

    private void showUnpublished() {
        views.statusIcon.setImageResource(R.drawable.ic_stethoscope_24);
        views.statusIcon.setBackgroundTintList(
                ContextCompat.getColorStateList(this, R.color.md_surface_container_high));
        views.statusIcon.setImageTintList(
                ContextCompat.getColorStateList(this, R.color.md_on_surface_variant));

        views.statusTitle.setText(R.string.doctor_home_no_profile_title);
        views.statusBody.setText(R.string.doctor_home_no_profile_body);

        views.primaryAction.setText(R.string.doctor_home_publish);
        views.primaryAction.setOnClickListener(v ->
                startActivity(DoctorPracticeFormActivity.create(this)));

        views.secondaryAction.setVisibility(View.GONE);
        views.profileGroup.setVisibility(View.GONE);
    }

    private void showPublished(@NonNull Doctor doctor) {
        Specialty specialty = doctor.specialty();

        views.statusIcon.setImageResource(R.drawable.ic_verified_24);
        views.statusIcon.setBackgroundTintList(
                ContextCompat.getColorStateList(this, R.color.md_primary_container));
        views.statusIcon.setImageTintList(
                ContextCompat.getColorStateList(this, R.color.md_on_primary_container));

        views.statusTitle.setText(R.string.doctor_home_listed_title);
        views.statusBody.setText(getString(R.string.doctor_home_listed_body,
                specialtyLabel(specialty)));

        views.primaryAction.setText(R.string.doctor_home_edit);
        views.primaryAction.setOnClickListener(v ->
                startActivity(DoctorPracticeFormActivity.edit(this, doctor)));

        views.secondaryAction.setVisibility(View.VISIBLE);
        views.secondaryAction.setOnClickListener(v ->
                startActivity(DoctorDetailActivity.create(this, doctor)));

        // Keep the greeting in step with the name on the listing.
        session().cacheDisplayName(doctor.getName());
        bindGreeting();

        views.profileGroup.setVisibility(View.VISIBLE);
        InfoRow.bind(views.rowSpecialty, R.drawable.ic_stethoscope_24,
                R.string.field_specialty, specialtyLabel(specialty));
        InfoRow.bind(views.rowFocus, R.drawable.ic_verified_24,
                R.string.field_focus_area, doctor.getSpeciality());
        InfoRow.bind(views.rowDegree, R.drawable.ic_school_24,
                R.string.field_degree, doctor.getDegree());
        InfoRow.bind(views.rowContact, R.drawable.ic_phone_24,
                R.string.field_phone, doctor.getContact());
        InfoRow.bind(views.rowChamber, R.drawable.ic_location_24,
                R.string.field_chamber, doctor.getChamber());
    }

    private @NonNull String specialtyLabel(@Nullable Specialty specialty) {
        return getString(specialty == null
                ? R.string.specialty_unspecified
                : specialty.labelRes());
    }

    private boolean onMenuItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_sign_out) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.auth_sign_out_confirm_title)
                    .setMessage(R.string.auth_sign_out_confirm_body)
                    .setNegativeButton(R.string.action_cancel, null)
                    .setPositiveButton(R.string.action_sign_out, (dialog, which) -> {
                        session().signOut();
                        restartAt(LandingActivity.class);
                    })
                    .show();
            return true;
        }
        return false;
    }

    /** Convenience for navigating here from elsewhere. */
    public static @NonNull Intent create(@NonNull android.content.Context context) {
        return new Intent(context, DoctorHomeActivity.class);
    }
}
