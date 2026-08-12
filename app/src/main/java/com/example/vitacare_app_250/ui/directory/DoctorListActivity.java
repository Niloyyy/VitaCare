package com.example.vitacare_app_250.ui.directory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.core.Result;
import com.example.vitacare_app_250.data.model.Doctor;
import com.example.vitacare_app_250.data.model.Specialty;
import com.example.vitacare_app_250.databinding.ActivityDoctorListBinding;
import com.example.vitacare_app_250.ui.base.BaseActivity;
import com.example.vitacare_app_250.ui.common.SpacingItemDecoration;
import com.example.vitacare_app_250.ui.common.TextChangeListener;

import java.util.List;

/**
 * The doctor directory, either for one specialty or across all of them.
 *
 * <p>Replaces two separate screens that did overlapping jobs: a per-specialty
 * {@code RecyclerView} and an "all doctors" {@code ListView} that showed names
 * only, as {@code simple_list_item_1} rows with no contact details and nothing
 * to tap. Both are this screen now, differing only in what they observe.
 */
public class DoctorListActivity extends BaseActivity implements DoctorAdapter.Listener {

    private static final String EXTRA_SPECIALTY = "specialty";

    private ActivityDoctorListBinding views;
    private DoctorListViewModel viewModel;
    private DoctorAdapter adapter;
    private @Nullable Specialty specialty;

    /** Opens the directory filtered to one specialty. */
    public static @NonNull Intent forSpecialty(@NonNull Context context,
                                               @NonNull Specialty specialty) {
        return new Intent(context, DoctorListActivity.class)
                .putExtra(EXTRA_SPECIALTY, specialty.key());
    }

    /** Opens the directory across every specialty. */
    public static @NonNull Intent allDoctors(@NonNull Context context) {
        return new Intent(context, DoctorListActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityDoctorListBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        applyInsets(views.appBar, Edge.TOP, Edge.LEFT, Edge.RIGHT);
        applyInsets(views.listContainer, Edge.LEFT, Edge.RIGHT);

        specialty = Specialty.fromKey(getIntent().getStringExtra(EXTRA_SPECIALTY));
        setUpToolbar(views.toolbar, specialty == null
                ? getString(R.string.doctor_list_title_all)
                : getString(specialty.labelRes()));

        setUpList();
        setUpSearch();

        viewModel = new ViewModelProvider(this, new DoctorListViewModel.Factory(specialty))
                .get(DoctorListViewModel.class);
        viewModel.doctors().observe(this, this::render);
    }

    private void setUpList() {
        adapter = new DoctorAdapter(this);
        views.list.setLayoutManager(new LinearLayoutManager(this));
        views.list.setAdapter(adapter);
        views.list.addItemDecoration(new SpacingItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.list_item_spacing),
                getResources().getDimensionPixelSize(R.dimen.list_edge_spacing),
                getResources().getDimensionPixelSize(R.dimen.list_bottom_padding)));
        views.state.showLoading();
    }

    private void setUpSearch() {
        TextChangeListener.onChange(views.search, text -> viewModel.setQuery(text));
    }

    private void render(@NonNull Result<List<Doctor>> result) {
        if (!result.isSuccess()) {
            views.list.setVisibility(View.GONE);
            views.resultCount.setVisibility(View.GONE);
            views.state.showError(result.error(), null);
            return;
        }

        List<Doctor> doctors = result.value();
        if (doctors == null || doctors.isEmpty()) {
            views.list.setVisibility(View.GONE);
            views.resultCount.setVisibility(View.GONE);
            showEmptyState();
            return;
        }

        views.state.showContent();
        views.list.setVisibility(View.VISIBLE);
        views.resultCount.setVisibility(View.VISIBLE);
        views.resultCount.setText(getResources().getQuantityString(
                R.plurals.doctor_list_count, doctors.size(), doctors.size()));
        adapter.submitList(doctors);
    }

    /**
     * Distinguishes "this specialty has no doctors" from "your search matched
     * nothing" — the same blank screen previously covered both, leaving the
     * user unsure whether to clear the search or go back.
     */
    private void showEmptyState() {
        if (viewModel.isSearching()) {
            String query = views.search.getText() == null
                    ? "" : views.search.getText().toString();
            views.state.showEmpty(
                    R.drawable.ic_empty_search_48,
                    R.string.empty_doctors_search_title,
                    getString(R.string.empty_search_body, query));
        } else {
            views.state.showEmpty(
                    R.drawable.ic_empty_inbox_48,
                    R.string.empty_doctors_title,
                    R.string.empty_doctors_body);
        }
    }

    @Override
    public void onDoctorClicked(@NonNull Doctor doctor) {
        startActivity(DoctorDetailActivity.create(this, doctor));
    }

    @Override
    public void onCallClicked(@NonNull Doctor doctor) {
        dial(doctor.getContact());
    }
}
