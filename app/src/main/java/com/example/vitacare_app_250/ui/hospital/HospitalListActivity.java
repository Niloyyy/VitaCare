package com.example.vitacare_app_250.ui.hospital;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.core.Result;
import com.example.vitacare_app_250.data.model.Hospital;
import com.example.vitacare_app_250.databinding.ActivityHospitalListBinding;
import com.example.vitacare_app_250.ui.base.BaseActivity;
import com.example.vitacare_app_250.ui.common.SpacingItemDecoration;
import com.example.vitacare_app_250.ui.common.TextChangeListener;

import java.util.List;

/** Hospitals with live bed and ICU availability. */
public class HospitalListActivity extends BaseActivity implements HospitalAdapter.Listener {

    private ActivityHospitalListBinding views;
    private HospitalListViewModel viewModel;
    private HospitalAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityHospitalListBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        applyInsets(views.appBar, Edge.TOP, Edge.LEFT, Edge.RIGHT);
        applyInsets(views.listContainer, Edge.LEFT, Edge.RIGHT);

        setUpToolbar(views.toolbar, R.string.hospital_list_title);
        views.toolbar.setOnMenuItemClickListener(this::onSortSelected);

        adapter = new HospitalAdapter(this);
        views.list.setLayoutManager(new LinearLayoutManager(this));
        views.list.setAdapter(adapter);
        views.list.addItemDecoration(new SpacingItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.list_item_spacing),
                getResources().getDimensionPixelSize(R.dimen.list_edge_spacing),
                getResources().getDimensionPixelSize(R.dimen.list_bottom_padding)));
        views.state.showLoading();

        TextChangeListener.onChange(views.search, text -> viewModel.setQuery(text));

        viewModel = new ViewModelProvider(this).get(HospitalListViewModel.class);
        viewModel.hospitals().observe(this, this::render);

        syncSortMenu();
    }

    private boolean onSortSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_name) {
            viewModel.setSortOrder(HospitalListViewModel.SortOrder.NAME);
        } else if (id == R.id.sort_beds) {
            viewModel.setSortOrder(HospitalListViewModel.SortOrder.BEDS_DESC);
        } else if (id == R.id.sort_icu) {
            viewModel.setSortOrder(HospitalListViewModel.SortOrder.ICU_DESC);
        } else {
            return false;
        }
        item.setChecked(true);
        // Return to the top: the row the user was looking at has moved.
        views.list.scrollToPosition(0);
        return true;
    }

    /** Restores the checked sort item after a configuration change. */
    private void syncSortMenu() {
        int checkedId;
        switch (viewModel.currentSortOrder()) {
            case BEDS_DESC:
                checkedId = R.id.sort_beds;
                break;
            case ICU_DESC:
                checkedId = R.id.sort_icu;
                break;
            case NAME:
            default:
                checkedId = R.id.sort_name;
                break;
        }
        MenuItem item = views.toolbar.getMenu().findItem(checkedId);
        if (item != null) {
            item.setChecked(true);
        }
    }

    private void render(@NonNull Result<List<Hospital>> result) {
        if (!result.isSuccess()) {
            views.list.setVisibility(View.GONE);
            views.resultCount.setVisibility(View.GONE);
            views.state.showError(result.error(), null);
            return;
        }

        List<Hospital> hospitals = result.value();
        if (hospitals == null || hospitals.isEmpty()) {
            views.list.setVisibility(View.GONE);
            views.resultCount.setVisibility(View.GONE);
            if (viewModel.isSearching()) {
                String query = views.search.getText() == null
                        ? "" : views.search.getText().toString();
                views.state.showEmpty(
                        R.drawable.ic_empty_search_48,
                        R.string.empty_hospitals_search_title,
                        getString(R.string.empty_search_body, query));
            } else {
                views.state.showEmpty(
                        R.drawable.ic_empty_inbox_48,
                        R.string.empty_hospitals_title,
                        R.string.empty_hospitals_body);
            }
            return;
        }

        views.state.showContent();
        views.list.setVisibility(View.VISIBLE);
        views.resultCount.setVisibility(View.VISIBLE);
        views.resultCount.setText(getResources().getQuantityString(
                R.plurals.hospital_list_count, hospitals.size(), hospitals.size()));
        adapter.submitList(hospitals);
    }

    @Override
    public void onHospitalClicked(@NonNull Hospital hospital) {
        startActivity(HospitalDetailActivity.create(this, hospital));
    }
}
