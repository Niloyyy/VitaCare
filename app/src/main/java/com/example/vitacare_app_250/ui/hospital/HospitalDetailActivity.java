package com.example.vitacare_app_250.ui.hospital;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.data.model.Hospital;
import com.example.vitacare_app_250.databinding.ActivityHospitalDetailBinding;
import com.example.vitacare_app_250.ui.base.BaseActivity;
import com.example.vitacare_app_250.ui.common.InfoRow;
import com.example.vitacare_app_250.util.Texts;

/**
 * One hospital's details and current availability.
 *
 * <p>The old version showed a "Contact Us:" heading above a hardcoded
 * placeholder number, {@code +8801XXXXXXXXX}, which was never replaced with
 * data — it was a layout literal that no code ever wrote to. The row is now
 * bound from the record and hidden when the hospital has published no number.
 */
public class HospitalDetailActivity extends BaseActivity {

    private static final String EXTRA_NAME = "name";
    private static final String EXTRA_ADDRESS = "address";
    private static final String EXTRA_CONTACT = "contact";
    private static final String EXTRA_BEDS = "beds";
    private static final String EXTRA_ICU = "icu";

    private ActivityHospitalDetailBinding views;

    public static @NonNull Intent create(@NonNull Context context, @NonNull Hospital hospital) {
        return new Intent(context, HospitalDetailActivity.class)
                .putExtra(EXTRA_NAME, hospital.getName())
                .putExtra(EXTRA_ADDRESS, hospital.getAddress())
                .putExtra(EXTRA_CONTACT, hospital.getContact())
                .putExtra(EXTRA_BEDS, hospital.availableBeds())
                .putExtra(EXTRA_ICU, hospital.availableIcu());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityHospitalDetailBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        applyInsets(views.appBar, Edge.TOP, Edge.LEFT, Edge.RIGHT);
        applyInsets(views.scroll, Edge.BOTTOM, Edge.LEFT, Edge.RIGHT);
        setUpToolbar(views.toolbar, R.string.hospital_detail_title);

        Intent intent = getIntent();
        String name = Texts.orEmpty(intent.getStringExtra(EXTRA_NAME));
        String address = Texts.orEmpty(intent.getStringExtra(EXTRA_ADDRESS));
        String contact = Texts.orEmpty(intent.getStringExtra(EXTRA_CONTACT));
        int beds = intent.getIntExtra(EXTRA_BEDS, Hospital.UNKNOWN);
        int icu = intent.getIntExtra(EXTRA_ICU, Hospital.UNKNOWN);

        views.name.setText(name);
        views.hero.setContentDescription(getString(R.string.hospital_photo_description, name));

        // Rebuild a Hospital so availability banding lives in one place rather
        // than being reimplemented per screen.
        Hospital hospital = new Hospital("", name, address, contact, beds, icu);
        bindStatus(hospital);

        views.metrics.setVisibility(hospital.hasBedData() ? View.VISIBLE : View.GONE);
        bindMetric(views.bedsCard, views.bedsValue, beds);
        bindMetric(views.icuCard, views.icuValue, icu);

        InfoRow.bind(views.rowAddress, R.drawable.ic_location_24,
                R.string.hospital_address_label, address);
        InfoRow.bind(views.rowContact, R.drawable.ic_phone_24,
                R.string.hospital_contact_label, contact,
                contact.isEmpty() ? null : v -> dial(contact));
        if (!contact.isEmpty()) {
            InfoRow.setAction(views.rowContact, R.drawable.ic_phone_24,
                    getString(R.string.doctor_call_description, name));
        }
    }

    private void bindStatus(@NonNull Hospital hospital) {
        @StringRes int label;
        @ColorRes int container;
        @ColorRes int text;

        switch (hospital.availability()) {
            case AVAILABLE:
                label = R.string.hospital_status_available;
                container = R.color.status_available_container;
                text = R.color.status_available;
                break;
            case LIMITED:
                label = R.string.hospital_status_limited;
                container = R.color.status_limited_container;
                text = R.color.status_limited;
                break;
            case FULL:
                label = R.string.hospital_status_full;
                container = R.color.status_full_container;
                text = R.color.status_full;
                break;
            default:
                label = R.string.hospital_availability_unknown;
                container = R.color.md_surface_container_high;
                text = R.color.md_on_surface_variant;
                break;
        }

        views.statusChip.setText(label);
        views.statusChip.setChipBackgroundColor(ContextCompat.getColorStateList(this, container));
        ColorStateList textColor = ContextCompat.getColorStateList(this, text);
        if (textColor != null) {
            views.statusChip.setTextColor(textColor);
        }
    }

    private static void bindMetric(@NonNull View card, @NonNull android.widget.TextView value, int count) {
        if (count == Hospital.UNKNOWN) {
            card.setVisibility(View.GONE);
        } else {
            card.setVisibility(View.VISIBLE);
            value.setText(String.valueOf(count));
        }
    }
}
