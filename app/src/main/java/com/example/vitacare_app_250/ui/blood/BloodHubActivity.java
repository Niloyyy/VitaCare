package com.example.vitacare_app_250.ui.blood;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.databinding.ActivityBloodHubBinding;
import com.example.vitacare_app_250.ui.base.BaseActivity;

/** Entry point to the two Blood Link flows. */
public class BloodHubActivity extends BaseActivity {

    private ActivityBloodHubBinding views;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityBloodHubBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        applyInsets(views.appBar, Edge.TOP, Edge.LEFT, Edge.RIGHT);
        applyInsets(views.scroll, Edge.BOTTOM, Edge.LEFT, Edge.RIGHT);
        setUpToolbar(views.toolbar, R.string.blood_hub_title);

        views.actionBecomeDonor.featureIcon.setImageResource(R.drawable.ic_blood_drop_24);
        views.actionBecomeDonor.featureIcon.setBackgroundTintList(
                ContextCompat.getColorStateList(this, R.color.brand_blood_container));
        views.actionBecomeDonor.featureIcon.setImageTintList(
                ContextCompat.getColorStateList(this, R.color.brand_blood));
        views.actionBecomeDonor.featureTitle.setText(R.string.blood_hub_become_donor);
        views.actionBecomeDonor.featureDetail.setText(R.string.blood_hub_become_donor_detail);
        views.actionBecomeDonor.featureCard.setContentDescription(
                getString(R.string.blood_hub_become_donor) + ". "
                        + getString(R.string.blood_hub_become_donor_detail));
        views.actionBecomeDonor.featureCard.setOnClickListener(v ->
                startActivity(new Intent(this, BecomeDonorActivity.class)));

        views.actionFindDonor.featureIcon.setImageResource(R.drawable.ic_search_24);
        views.actionFindDonor.featureIcon.setBackgroundTintList(
                ContextCompat.getColorStateList(this, R.color.md_secondary_container));
        views.actionFindDonor.featureIcon.setImageTintList(
                ContextCompat.getColorStateList(this, R.color.md_on_secondary_container));
        views.actionFindDonor.featureTitle.setText(R.string.blood_hub_find_donor);
        views.actionFindDonor.featureDetail.setText(R.string.blood_hub_find_donor_detail);
        views.actionFindDonor.featureCard.setContentDescription(
                getString(R.string.blood_hub_find_donor) + ". "
                        + getString(R.string.blood_hub_find_donor_detail));
        views.actionFindDonor.featureCard.setOnClickListener(v ->
                startActivity(new Intent(this, FindDonorActivity.class)));
    }
}
