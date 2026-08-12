package com.example.vitacare_app_250.ui.patient;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.databinding.ActivityPatientProfileBinding;
import com.example.vitacare_app_250.ui.auth.LandingActivity;
import com.example.vitacare_app_250.ui.base.BaseActivity;
import com.example.vitacare_app_250.ui.common.InfoRow;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseUser;

/**
 * The signed-in patient's account details.
 *
 * <p>Read-only, as before — the underlying data is whatever Firebase Auth
 * holds, and this app never collects a name or phone number for a patient. The
 * difference is that a field with no value now says so explicitly instead of
 * rendering the literal text "Name: Not set" as though it were the name.
 */
public class PatientProfileActivity extends BaseActivity {

    private ActivityPatientProfileBinding views;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = session().currentUser();
        if (user == null) {
            showMessage(R.string.auth_session_expired);
            restartAt(LandingActivity.class);
            return;
        }

        views = ActivityPatientProfileBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        applyInsets(views.appBar, Edge.TOP, Edge.LEFT, Edge.RIGHT);
        applyInsets(views.scroll, Edge.BOTTOM, Edge.LEFT, Edge.RIGHT);
        setUpToolbar(views.toolbar, R.string.profile_title);

        String name = session().displayName();
        views.displayName.setText(name != null ? name : getString(R.string.profile_not_set));

        InfoRow.bindOrNotSet(views.rowEmail,
                R.drawable.ic_email_24, R.string.field_email, user.getEmail());
        InfoRow.bindOrNotSet(views.rowPhone,
                R.drawable.ic_phone_24, R.string.field_phone, user.getPhoneNumber());

        views.signOut.setOnClickListener(v -> confirmSignOut());
    }

    private void confirmSignOut() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.auth_sign_out_confirm_title)
                .setMessage(R.string.auth_sign_out_confirm_body)
                .setNegativeButton(R.string.action_cancel, null)
                .setPositiveButton(R.string.action_sign_out, (dialog, which) -> {
                    session().signOut();
                    restartAt(LandingActivity.class);
                })
                .show();
    }
}
