package com.example.vitacare_app_250.ui.patient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.data.model.Role;
import com.example.vitacare_app_250.databinding.ActivityPatientHomeBinding;
import com.example.vitacare_app_250.databinding.ItemFeatureCardBinding;
import com.example.vitacare_app_250.ui.auth.LandingActivity;
import com.example.vitacare_app_250.ui.base.BaseActivity;
import com.example.vitacare_app_250.ui.blood.BloodHubActivity;
import com.example.vitacare_app_250.ui.directory.DoctorListActivity;
import com.example.vitacare_app_250.ui.directory.SpecialtyActivity;
import com.example.vitacare_app_250.ui.doctor.DoctorHomeActivity;
import com.example.vitacare_app_250.ui.hospital.HospitalListActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * The patient dashboard.
 *
 * <p>Access control changed here. The old screen admitted anyone holding a
 * cached token string and, on finding a {@code "doctor"} user type, forwarded
 * to the doctor dashboard — meaning the check ran after the activity had
 * already been created and could be reached with no Firebase session at all.
 * {@link #enforceAccess()} now requires a live, role-matched session and
 * returns to the landing screen otherwise.
 */
public class PatientHomeActivity extends BaseActivity {

    private ActivityPatientHomeBinding views;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!enforceAccess()) {
            return;
        }

        views = ActivityPatientHomeBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        applyInsets(views.appBar, Edge.TOP, Edge.LEFT, Edge.RIGHT);
        applyInsets(views.scroll, Edge.BOTTOM, Edge.LEFT, Edge.RIGHT);

        setSupportActionBar(views.toolbar);
        views.toolbar.setOnMenuItemClickListener(this::onMenuItemSelected);

        bindGreeting();
        bindServices();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The session can end while this screen is backgrounded — signed out on
        // another device, or the account deleted.
        enforceAccess();
    }

    /** @return true when the caller may stay on this screen */
    private boolean enforceAccess() {
        Role role = session().currentRole();
        if (role == Role.PATIENT) {
            return true;
        }
        if (role == Role.DOCTOR) {
            restartAt(DoctorHomeActivity.class);
        } else {
            showMessage(R.string.auth_session_expired);
            restartAt(LandingActivity.class);
        }
        return false;
    }

    private void bindGreeting() {
        String name = session().displayName();
        views.greeting.setText(name == null
                ? getString(R.string.patient_home_greeting_generic)
                : getString(R.string.patient_home_greeting, ", " + name));
    }

    private void bindServices() {
        bindService(views.actionSpecialist,
                R.drawable.ic_stethoscope_24,
                R.color.specialty_cardiology_container,
                R.color.specialty_cardiology,
                R.string.feature_find_specialist,
                R.string.feature_find_specialist_detail,
                v -> startActivity(new Intent(this, SpecialtyActivity.class)));

        bindService(views.actionDirectory,
                R.drawable.ic_person_24,
                R.color.specialty_neurology_container,
                R.color.specialty_neurology,
                R.string.feature_doctor_directory,
                R.string.feature_doctor_directory_detail,
                v -> startActivity(DoctorListActivity.allDoctors(this)));

        bindService(views.actionHospitals,
                R.drawable.ic_hospital_24,
                R.color.specialty_nephrology_container,
                R.color.specialty_nephrology,
                R.string.feature_hospitals,
                R.string.feature_hospitals_detail,
                v -> startActivity(new Intent(this, HospitalListActivity.class)));

        bindService(views.actionBlood,
                R.drawable.ic_blood_drop_24,
                R.color.brand_blood_container,
                R.color.brand_blood,
                R.string.feature_blood_link,
                R.string.feature_blood_link_detail,
                v -> startActivity(new Intent(this, BloodHubActivity.class)));
    }

    private void bindService(@NonNull ItemFeatureCardBinding card,
                             @DrawableRes int iconRes,
                             @ColorRes int containerRes,
                             @ColorRes int accentRes,
                             @StringRes int titleRes,
                             @StringRes int detailRes,
                             @NonNull View.OnClickListener onClick) {
        card.featureIcon.setImageResource(iconRes);
        card.featureIcon.setBackgroundTintList(ContextCompat.getColorStateList(this, containerRes));
        card.featureIcon.setImageTintList(ContextCompat.getColorStateList(this, accentRes));
        card.featureTitle.setText(titleRes);
        card.featureDetail.setText(detailRes);
        card.featureCard.setOnClickListener(onClick);
        // The row is one target; announce it as one label rather than letting
        // TalkBack read the title and detail as unrelated fragments.
        card.featureCard.setContentDescription(
                getString(titleRes) + ". " + getString(detailRes));
    }

    private boolean onMenuItemSelected(@NonNull android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_profile) {
            startActivity(new Intent(this, PatientProfileActivity.class));
            return true;
        }
        if (id == R.id.action_sign_out) {
            confirmSignOut();
            return true;
        }
        return false;
    }

    /**
     * Signing out is not reversible from the UI, so it asks first. The old
     * dashboard signed out on a single tap of a red button with black text.
     */
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
