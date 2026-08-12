package com.example.vitacare_app_250.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.core.Result;
import com.example.vitacare_app_250.data.repo.DoctorRepository;
import com.example.vitacare_app_250.databinding.ActivityDoctorVerificationBinding;
import com.example.vitacare_app_250.ui.base.BaseActivity;
import com.example.vitacare_app_250.ui.common.FormField;
import com.example.vitacare_app_250.util.Validators;

/**
 * Checks a doctor's details against the licence register before letting them
 * register an account.
 *
 * <p>Worth being clear about what this does and does not do. It is a
 * convenience check that keeps honest users from registering with a typo; it is
 * not an authorisation boundary, because nothing stops a determined caller from
 * navigating past it to the sign-up screen or writing to the database directly.
 * The real control has to be Realtime Database security rules — see README.
 *
 * <p>What did change is the blast radius. The old check pulled the entire
 * {@code doctors} register onto the device and looped over it in Java, so every
 * registered doctor's licence number was readable by anyone who opened this
 * screen. The lookup is now a server-side query for the one matching licence.
 */
public class DoctorVerificationActivity extends BaseActivity {

    private ActivityDoctorVerificationBinding views;
    private DoctorRepository doctorRepository;
    private FormField.Form form;
    private FormField nameField;
    private FormField emailField;
    private FormField licenceField;
    private boolean submitting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityDoctorVerificationBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        applyInsets(views.appBar, Edge.TOP, Edge.LEFT, Edge.RIGHT);
        applyInsets(views.scroll, Edge.BOTTOM, Edge.LEFT, Edge.RIGHT);
        setUpToolbar(views.toolbar, R.string.doctor_verify_title);

        doctorRepository = new DoctorRepository();

        form = new FormField.Form();
        nameField = form.addField(views.nameLayout, Validators::fullName);
        emailField = form.addField(views.emailLayout, Validators::email);
        licenceField = form.addField(views.licenceLayout, Validators::licence);

        views.submit.setOnClickListener(v -> submit());
    }

    private void submit() {
        if (submitting) {
            return;
        }
        hideKeyboard();
        if (!form.validate()) {
            return;
        }

        setSubmitting(true);
        doctorRepository.verifyLicence(
                nameField.value(),
                emailField.value(),
                licenceField.value(),
                this::onVerificationResult);
    }

    private void onVerificationResult(@NonNull Result<Boolean> result) {
        if (!isAlive()) {
            return;
        }
        setSubmitting(false);

        if (!result.isSuccess()) {
            showError(result.error(), this::submit);
            return;
        }

        if (Boolean.TRUE.equals(result.value())) {
            showMessage(R.string.doctor_verify_success);
            startActivity(new Intent(this, DoctorLoginActivity.class));
            finish();
        } else {
            // One message for all three fields rather than pointing at the
            // field that failed: saying which detail was wrong would let this
            // screen be used to probe the register.
            showMessage(R.string.doctor_verify_failed);
        }
    }

    private void setSubmitting(boolean value) {
        submitting = value;
        views.submit.setEnabled(!value);
        views.nameLayout.setEnabled(!value);
        views.emailLayout.setEnabled(!value);
        views.licenceLayout.setEnabled(!value);
        views.progress.setVisibility(value ? View.VISIBLE : View.INVISIBLE);
    }
}
