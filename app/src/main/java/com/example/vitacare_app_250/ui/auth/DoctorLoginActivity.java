package com.example.vitacare_app_250.ui.auth;

import androidx.annotation.NonNull;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.data.model.Role;
import com.example.vitacare_app_250.ui.doctor.DoctorHomeActivity;

/** Doctor sign-in. Reached only after licence verification. */
public class DoctorLoginActivity extends LoginActivity {

    @Override
    protected @NonNull Role role() {
        return Role.DOCTOR;
    }

    @Override
    protected int toolbarTitleRes() {
        return R.string.doctor_login_title;
    }

    @Override
    protected int headingRes() {
        return R.string.doctor_login_heading;
    }

    @Override
    protected int subheadingRes() {
        return R.string.doctor_login_subheading;
    }

    @Override
    protected int switchPromptRes() {
        return R.string.patient_signup_prompt;
    }

    @Override
    protected @NonNull Class<?> signUpActivity() {
        return DoctorSignUpActivity.class;
    }

    @Override
    protected @NonNull Class<?> homeActivity() {
        return DoctorHomeActivity.class;
    }
}
