package com.example.vitacare_app_250.ui.directory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.data.model.Doctor;
import com.example.vitacare_app_250.data.model.Specialty;
import com.example.vitacare_app_250.databinding.ActivityDoctorDetailBinding;
import com.example.vitacare_app_250.ui.base.BaseActivity;
import com.example.vitacare_app_250.ui.common.InfoRow;
import com.example.vitacare_app_250.util.Texts;

/**
 * A doctor's public profile.
 *
 * <p>The details arrive as intent extras rather than being re-fetched: the
 * caller already has the record, and a second read would only introduce a
 * loading state and a way to fail. {@link #create} keeps the extra keys in one
 * place, replacing the six loose {@code putExtra("name", …)} literals that had
 * to be kept in sync by hand between the adapter and this screen — one of
 * which, {@code "field"}, was written but never read.
 */
public class DoctorDetailActivity extends BaseActivity {

    private static final String EXTRA_NAME = "name";
    private static final String EXTRA_CONTACT = "contact";
    private static final String EXTRA_DEGREE = "degree";
    private static final String EXTRA_CHAMBER = "chamber";
    private static final String EXTRA_SPECIALTY = "specialty";
    private static final String EXTRA_FOCUS = "focus";

    private ActivityDoctorDetailBinding views;

    /** Builds the intent that opens {@code doctor}'s profile. */
    public static @NonNull Intent create(@NonNull Context context, @NonNull Doctor doctor) {
        return new Intent(context, DoctorDetailActivity.class)
                .putExtra(EXTRA_NAME, doctor.getName())
                .putExtra(EXTRA_CONTACT, doctor.getContact())
                .putExtra(EXTRA_DEGREE, doctor.getDegree())
                .putExtra(EXTRA_CHAMBER, doctor.getChamber())
                .putExtra(EXTRA_SPECIALTY, doctor.getSpecialist())
                .putExtra(EXTRA_FOCUS, doctor.getSpeciality());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityDoctorDetailBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        applyInsets(views.appBar, Edge.TOP, Edge.LEFT, Edge.RIGHT);
        applyInsets(views.scroll, Edge.BOTTOM, Edge.LEFT, Edge.RIGHT);
        setUpToolbar(views.toolbar, R.string.doctor_detail_title);

        Intent intent = getIntent();
        String name = Texts.orEmpty(intent.getStringExtra(EXTRA_NAME));
        String contact = Texts.orEmpty(intent.getStringExtra(EXTRA_CONTACT));
        String degree = Texts.orEmpty(intent.getStringExtra(EXTRA_DEGREE));
        String chamber = Texts.orEmpty(intent.getStringExtra(EXTRA_CHAMBER));
        String focus = Texts.orEmpty(intent.getStringExtra(EXTRA_FOCUS));
        Specialty specialty = Specialty.fromKey(intent.getStringExtra(EXTRA_SPECIALTY));

        String displayName = getString(R.string.doctor_name_format, name);
        views.name.setText(displayName);
        views.avatar.setContentDescription(
                getString(R.string.doctor_photo_description, displayName));

        if (specialty == null) {
            views.specialtyChip.setVisibility(View.GONE);
        } else {
            views.specialtyChip.setVisibility(View.VISIBLE);
            views.specialtyChip.setText(specialty.labelRes());
        }

        if (focus.isEmpty()) {
            views.focus.setVisibility(View.GONE);
        } else {
            views.focus.setVisibility(View.VISIBLE);
            views.focus.setText(focus);
        }

        if (contact.isEmpty()) {
            views.call.setVisibility(View.GONE);
        } else {
            views.call.setVisibility(View.VISIBLE);
            views.call.setContentDescription(
                    getString(R.string.doctor_call_description, displayName));
            views.call.setOnClickListener(v -> dial(contact));
        }

        // Rows hide themselves when empty, so a doctor who left the chamber
        // address blank gets a shorter card rather than "Chamber: No Chamber
        // Info".
        InfoRow.bind(views.rowContact, R.drawable.ic_phone_24,
                R.string.field_phone, contact);
        InfoRow.bind(views.rowDegree, R.drawable.ic_school_24,
                R.string.field_degree, degree);
        InfoRow.bind(views.rowChamber, R.drawable.ic_location_24,
                R.string.field_chamber, chamber);
    }
}
