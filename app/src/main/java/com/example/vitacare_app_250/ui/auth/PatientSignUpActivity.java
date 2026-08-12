package com.example.vitacare_app_250.ui.auth;

import androidx.annotation.NonNull;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.data.model.Role;

/** Patient registration. */
public class PatientSignUpActivity extends SignUpActivity {

    @Override
    protected @NonNull Role role() {
        return Role.PATIENT;
    }

    @Override
    protected int toolbarTitleRes() {
        return R.string.patient_signup_title;
    }

    @Override
    protected int headingRes() {
        return R.string.patient_signup_heading;
    }

    @Override
    protected int subheadingRes() {
        return R.string.patient_signup_subheading;
    }
}
