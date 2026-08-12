package com.example.vitacare_app_250.ui.directory;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.data.model.Specialty;
import com.example.vitacare_app_250.databinding.ActivitySpecialtyBinding;
import com.example.vitacare_app_250.databinding.ItemFeatureCardBinding;
import com.example.vitacare_app_250.ui.base.BaseActivity;

/**
 * Choose a specialty to browse.
 *
 * <p>The four options are built from {@link Specialty}, which fixes a real
 * routing bug: the old screen labelled its third button "Neurologist" but a
 * fourth called "Medicine" elsewhere in the flow, and each button carried its
 * database key as a hand-typed string literal in the click handler. One of
 * those literals had already been wrong long enough to earn a "✅ fixed"
 * comment. Here the label and the key come from the same enum constant.
 */
public class SpecialtyActivity extends BaseActivity {

    private ActivitySpecialtyBinding views;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivitySpecialtyBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        applyInsets(views.appBar, Edge.TOP, Edge.LEFT, Edge.RIGHT);
        applyInsets(views.scroll, Edge.BOTTOM, Edge.LEFT, Edge.RIGHT);
        setUpToolbar(views.toolbar, R.string.specialty_title);

        for (Specialty specialty : Specialty.values()) {
            views.content.addView(buildCard(specialty).getRoot());
        }
    }

    private ItemFeatureCardBinding buildCard(Specialty specialty) {
        ItemFeatureCardBinding card =
                ItemFeatureCardBinding.inflate(getLayoutInflater(), views.content, false);

        card.featureIcon.setImageResource(specialty.iconRes());
        card.featureIcon.setBackgroundTintList(
                ContextCompat.getColorStateList(this, specialty.containerRes()));
        card.featureIcon.setImageTintList(
                ContextCompat.getColorStateList(this, specialty.accentRes()));

        card.featureTitle.setText(specialty.labelRes());
        card.featureDetail.setText(specialty.descriptionRes());
        card.featureCard.setContentDescription(
                getString(specialty.labelRes()) + ". " + getString(specialty.descriptionRes()));
        card.featureCard.setOnClickListener(v ->
                startActivity(DoctorListActivity.forSpecialty(this, specialty)));

        return card;
    }
}
