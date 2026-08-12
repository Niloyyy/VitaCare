package com.example.vitacare_app_250.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.core.Result;
import com.example.vitacare_app_250.data.model.Role;
import com.example.vitacare_app_250.data.repo.AuthRepository;
import com.example.vitacare_app_250.databinding.ActivityAuthLoginBinding;
import com.example.vitacare_app_250.ui.base.BaseActivity;
import com.example.vitacare_app_250.ui.common.FormField;
import com.example.vitacare_app_250.util.Validators;

/**
 * Sign-in, shared by both roles.
 *
 * <p>{@code MainActivity} and {@code DoctorLogin} were near-identical
 * hundred-line copies of each other; the only real differences were the role
 * checked, the destination, and three strings. Those are the abstract methods
 * below. A fix to the sign-in flow now happens once instead of twice, or —
 * as had already happened with the disabled-button handling — once and not the
 * other.
 */
abstract class LoginActivity extends BaseActivity {

    private ActivityAuthLoginBinding views;
    private AuthRepository authRepository;
    private FormField.Form form;
    private FormField emailField;
    private FormField passwordField;
    private boolean submitting;

    // ---------------------------------------------------------------
    // Role-specific configuration
    // ---------------------------------------------------------------

    /** Role this entry point admits. */
    protected abstract @NonNull Role role();

    @StringRes
    protected abstract int toolbarTitleRes();

    @StringRes
    protected abstract int headingRes();

    @StringRes
    protected abstract int subheadingRes();

    @StringRes
    protected abstract int switchPromptRes();

    /** Registration screen for this role. */
    protected abstract @NonNull Class<?> signUpActivity();

    /** Dashboard to open once signed in. */
    protected abstract @NonNull Class<?> homeActivity();

    // ---------------------------------------------------------------

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityAuthLoginBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        applyInsets(views.appBar, Edge.TOP, Edge.LEFT, Edge.RIGHT);
        applyInsets(views.scroll, Edge.BOTTOM, Edge.LEFT, Edge.RIGHT);
        setUpToolbar(views.toolbar, toolbarTitleRes());

        views.heading.setText(headingRes());
        views.subheading.setText(subheadingRes());
        views.switchPrompt.setText(switchPromptRes());

        authRepository = new AuthRepository();

        form = new FormField.Form();
        emailField = form.addField(views.emailLayout, Validators::email);
        // Only "is something there" on sign-in: rejecting an existing password
        // for failing today's strength rule would lock the owner out of their
        // own account.
        passwordField = form.addField(views.passwordLayout, Validators::required);

        views.submit.setOnClickListener(v -> submit());
        views.switchAction.setOnClickListener(v ->
                startActivity(new Intent(this, signUpActivity())));
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
        authRepository.signIn(
                emailField.value(),
                passwordField.rawValue(),
                role(),
                this::onSignInResult);
    }

    private void onSignInResult(@NonNull Result<AuthRepository.SignedInUser> result) {
        // The sign-in continues on Firebase's thread and can land after the
        // user has rotated or backed out. Touching views then crashed the old
        // implementation.
        if (!isAlive()) {
            return;
        }
        setSubmitting(false);

        if (!result.isSuccess()) {
            showError(result.error(), null);
            return;
        }

        AuthRepository.SignedInUser user = result.value();
        if (user == null) {
            showMessage(R.string.error_generic_title);
            return;
        }

        session().onSignedIn(user.uid(), user.role(), null);
        restartAt(homeActivity());
    }

    /** Locks the form and shows progress for the duration of the request. */
    private void setSubmitting(boolean value) {
        submitting = value;
        views.submit.setEnabled(!value);
        views.switchAction.setEnabled(!value);
        views.emailLayout.setEnabled(!value);
        views.passwordLayout.setEnabled(!value);
        views.progress.setVisibility(value ? View.VISIBLE : View.INVISIBLE);
    }
}
