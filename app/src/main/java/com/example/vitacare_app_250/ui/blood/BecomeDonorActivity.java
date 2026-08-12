package com.example.vitacare_app_250.ui.blood;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.core.AppError;
import com.example.vitacare_app_250.core.Result;
import com.example.vitacare_app_250.data.model.BloodDonor;
import com.example.vitacare_app_250.data.model.BloodGroup;
import com.example.vitacare_app_250.data.repo.DonorRepository;
import com.example.vitacare_app_250.databinding.ActivityBecomeDonorBinding;
import com.example.vitacare_app_250.ui.base.BaseActivity;
import com.example.vitacare_app_250.ui.common.FormField;
import com.example.vitacare_app_250.util.Texts;
import com.example.vitacare_app_250.util.Validators;

import java.util.List;

/**
 * Adds the user to the volunteer donor register.
 *
 * <p>The blood group is chosen from a list and validated against
 * {@link BloodGroup}, so the register cannot accumulate free-text values that
 * the group filter on Find a Donor will never match.
 */
public class BecomeDonorActivity extends BaseActivity {

    private ActivityBecomeDonorBinding views;
    private DonorRepository donorRepository;
    private FormField.Form form;
    private FormField nameField;
    private FormField phoneField;
    private FormField locationField;
    private @Nullable BloodGroup selectedGroup;
    private boolean submitting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityBecomeDonorBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        applyInsets(views.appBar, Edge.TOP, Edge.LEFT, Edge.RIGHT);
        applyInsets(views.scroll, Edge.BOTTOM, Edge.LEFT, Edge.RIGHT);
        setUpToolbar(views.toolbar, R.string.become_donor_title);

        donorRepository = new DonorRepository();

        setUpGroupPicker();

        form = new FormField.Form();
        nameField = form.addField(views.nameLayout, Validators::fullName);
        phoneField = form.addField(views.phoneLayout, Validators::phone);
        locationField = form.addField(views.locationLayout, Validators::required);

        views.submit.setOnClickListener(v -> submit());
    }

    private void setUpGroupPicker() {
        List<String> labels = BloodGroup.labels();
        views.group.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, labels));
        views.group.setOnItemClickListener((parent, view, position, id) -> {
            selectedGroup = BloodGroup.values()[position];
            views.groupLayout.setError(null);
        });
    }

    private void submit() {
        if (submitting) {
            return;
        }
        hideKeyboard();

        boolean fieldsValid = form.validate();
        boolean groupValid = selectedGroup != null;
        if (!groupValid) {
            views.groupLayout.setError(getString(R.string.error_invalid_blood_group));
        }
        if (!fieldsValid || !groupValid) {
            return;
        }

        BloodDonor donor = new BloodDonor(
                "",
                Texts.collapseWhitespace(nameField.value()),
                Texts.toDialable(phoneField.value()),
                selectedGroup.label(),
                Texts.collapseWhitespace(locationField.value()));

        setSubmitting(true);
        donorRepository.register(donor, this::onRegistered);
    }

    private void onRegistered(@NonNull Result<Void> result) {
        if (!isAlive()) {
            return;
        }
        setSubmitting(false);

        if (result.isSuccess()) {
            showMessage(R.string.become_donor_success);
            finish();
            return;
        }

        AppError error = result.error();
        if (error != null && error.messageRes() == R.string.become_donor_duplicate) {
            // Point at the field that identifies the duplicate.
            phoneField.setError(R.string.become_donor_duplicate);
            return;
        }
        showError(error, this::submit);
    }

    private void setSubmitting(boolean value) {
        submitting = value;
        views.submit.setEnabled(!value);
        views.nameLayout.setEnabled(!value);
        views.phoneLayout.setEnabled(!value);
        views.groupLayout.setEnabled(!value);
        views.locationLayout.setEnabled(!value);
        views.progress.setVisibility(value ? View.VISIBLE : View.INVISIBLE);
    }
}
