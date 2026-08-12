package com.example.vitacare_app_250.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.vitacare_app_250.data.model.Role;
import com.example.vitacare_app_250.databinding.ActivityLandingBinding;
import com.example.vitacare_app_250.ui.base.BaseActivity;
import com.example.vitacare_app_250.ui.doctor.DoctorHomeActivity;
import com.example.vitacare_app_250.ui.patient.PatientHomeActivity;

/**
 * Launcher screen: routes an existing session to its dashboard, or offers the
 * two ways in.
 *
 * <p>The routing decision is the fix here. The old version read a role string
 * out of SharedPreferences, and if a Firebase user existed with no stored role
 * it signed them out — but only after having already decided, on the previous
 * branch, that a stored role with no Firebase user was good enough to enter.
 * {@link com.example.vitacare_app_250.session.SessionManager#currentRole()}
 * requires both to agree and to refer to the same uid.
 */
public class LandingActivity extends BaseActivity {

    private ActivityLandingBinding views;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (routeExistingSession()) {
            return;
        }

        views = ActivityLandingBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());
        applyInsets(views.scroll, Edge.TOP, Edge.BOTTOM, Edge.LEFT, Edge.RIGHT);

        views.cardPatient.setOnClickListener(v ->
                startActivity(new Intent(this, PatientLoginActivity.class)));
        views.cardDoctor.setOnClickListener(v ->
                startActivity(new Intent(this, DoctorVerificationActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Covers returning here after a sign-in completed in another task.
        routeExistingSession();
    }

    /**
     * Sends an already-signed-in user straight to their dashboard.
     *
     * @return true if the screen finished and no further setup should run
     */
    private boolean routeExistingSession() {
        Role role = session().currentRole();
        if (role == null) {
            // Firebase may still hold a user whose local record is gone or
            // belongs to a different uid. Clear it so the next sign-in starts
            // from a known state.
            if (session().currentUser() != null) {
                session().signOut();
            }
            return false;
        }
        restartAt(role == Role.DOCTOR ? DoctorHomeActivity.class : PatientHomeActivity.class);
        return true;
    }
}
