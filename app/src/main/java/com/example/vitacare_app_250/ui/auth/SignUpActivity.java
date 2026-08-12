package com.example.vitacare_app_250.ui.auth;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.core.AppError;
import com.example.vitacare_app_250.core.Result;
import com.example.vitacare_app_250.data.model.Role;
import com.example.vitacare_app_250.data.repo.AuthRepository;
import com.example.vitacare_app_250.databinding.ActivityAuthSignupBinding;
import com.example.vitacare_app_250.ui.base.BaseActivity;
import com.example.vitacare_app_250.ui.common.FormField;
import com.example.vitacare_app_250.util.Validators;

/**
 * Account registration, shared by both roles.
 *
 * <p>On success the screen finishes and returns to sign-in, which is what the
 * old code did too — but it now does so having signed the implicit session out
 * (see {@code AuthRepository.register}). Previously account creation left the
 * user authenticated with no local role record, a state the landing screen
 * then had to detect and unpick.
 */
abstract class SignUpActivity extends BaseActivity {

    private ActivityAuthSignupBinding views;
    private AuthRepository authRepository;
    private FormField.Form form;
    private FormField emailField;
    private FormField passwordField;
    private boolean submitting;

    /** Role to record for the new account. */
    protected abstract @NonNull Role role();

    @StringRes
    protected abstract int toolbarTitleRes();

    @StringRes
    protected abstract int headingRes();

    @StringRes
    protected abstract int subheadingRes();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityAuthSignupBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        applyInsets(views.appBar, Edge.TOP, Edge.LEFT, Edge.RIGHT);
        applyInsets(views.scroll, Edge.BOTTOM, Edge.LEFT, Edge.RIGHT);
        setUpToolbar(views.toolbar, toolbarTitleRes());

        views.heading.setText(headingRes());
        views.subheading.setText(subheadingRes());

        authRepository = new AuthRepository();

        form = new FormField.Form();
        emailField = form.addField(views.emailLayout, Validators::email);
        passwordField = form.addField(views.passwordLayout, Validators::password);
        form.addField(views.confirmLayout,
                value -> Validators.passwordConfirmation(passwordField.rawValue(), value));

        views.submit.setOnClickListener(v -> submit());
        views.switchAction.setOnClickListener(v -> finish());
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
        authRepository.register(
                emailField.value(),
                passwordField.rawValue(),
                role(),
                this::onRegisterResult);
    }

    private void onRegisterResult(@NonNull Result<Void> result) {
        if (!isAlive()) {
            return;
        }
        setSubmitting(false);

        if (result.isSuccess()) {
            showMessage(R.string.auth_account_created);
            finish();
            return;
        }

        AppError error = result.error();
        // "Email already in use" belongs on the email field, not in a
        // transient message the user has to remember while retyping.
        if (error != null && error.messageRes() == R.string.auth_email_in_use) {
            emailField.setError(R.string.auth_email_in_use);
            return;
        }
        if (error != null && error.messageRes() == R.string.auth_weak_password_server) {
            passwordField.setError(R.string.auth_weak_password_server);
            return;
        }
        showError(error, null);
    }

    private void setSubmitting(boolean value) {
        submitting = value;
        views.submit.setEnabled(!value);
        views.switchAction.setEnabled(!value);
        views.emailLayout.setEnabled(!value);
        views.passwordLayout.setEnabled(!value);
        views.confirmLayout.setEnabled(!value);
        views.progress.setVisibility(value ? View.VISIBLE : View.INVISIBLE);
    }
}
