package com.example.vitacare_app_250.ui.auth;

import androidx.annotation.NonNull;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.data.model.Role;
import com.example.vitacare_app_250.ui.patient.PatientHomeActivity;

/** Patient sign-in. Copy and destinations only; behaviour is {@link LoginActivity}. */
public class PatientLoginActivity extends LoginActivity {

    @Override
    protected @NonNull Role role() {
        return Role.PATIENT;
    }

    @Override
    protected int toolbarTitleRes() {
        return R.string.patient_login_title;
    }

    @Override
    protected int headingRes() {
        return R.string.patient_login_heading;
    }

    @Override
    protected int subheadingRes() {
        return R.string.patient_login_subheading;
    }

    @Override
    protected int switchPromptRes() {
        return R.string.patient_signup_prompt;
    }

    @Override
    protected @NonNull Class<?> signUpActivity() {
        return PatientSignUpActivity.class;
    }

    @Override
    protected @NonNull Class<?> homeActivity() {
        return PatientHomeActivity.class;
    }
}
