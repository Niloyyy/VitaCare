package com.example.vitacare_app_250.ui.auth;

import androidx.annotation.NonNull;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.data.model.Role;

/** Doctor registration. */
public class DoctorSignUpActivity extends SignUpActivity {

    @Override
    protected @NonNull Role role() {
        return Role.DOCTOR;
    }

    @Override
    protected int toolbarTitleRes() {
        return R.string.doctor_signup_title;
    }

    @Override
    protected int headingRes() {
        return R.string.doctor_signup_heading;
    }

    @Override
    protected int subheadingRes() {
        return R.string.doctor_signup_subheading;
    }
}
