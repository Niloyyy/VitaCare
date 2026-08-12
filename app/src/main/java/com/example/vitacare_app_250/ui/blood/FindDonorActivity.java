package com.example.vitacare_app_250.ui.blood;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.core.Result;
import com.example.vitacare_app_250.data.model.BloodDonor;
import com.example.vitacare_app_250.data.model.BloodGroup;
import com.example.vitacare_app_250.databinding.ActivityFindDonorBinding;
import com.example.vitacare_app_250.ui.base.BaseActivity;
import com.example.vitacare_app_250.ui.common.SpacingItemDecoration;
import com.example.vitacare_app_250.ui.common.TextChangeListener;
import com.google.android.material.chip.Chip;

import java.util.List;

/** Search the donor register by blood group and area. */
public class FindDonorActivity extends BaseActivity implements DonorAdapter.Listener {

    /** Chip id for "all groups"; other chips use their ordinal offset by 1. */
    private static final int CHIP_ID_ALL = 1;

    private ActivityFindDonorBinding views;
    private FindDonorViewModel viewModel;
    private DonorAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityFindDonorBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        applyInsets(views.appBar, Edge.TOP, Edge.LEFT, Edge.RIGHT);
        applyInsets(views.listContainer, Edge.LEFT, Edge.RIGHT);
        setUpToolbar(views.toolbar, R.string.find_donor_title);

        adapter = new DonorAdapter(this);
        views.list.setLayoutManager(new LinearLayoutManager(this));
        views.list.setAdapter(adapter);
        views.list.addItemDecoration(new SpacingItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.list_item_spacing),
                getResources().getDimensionPixelSize(R.dimen.list_edge_spacing),
                getResources().getDimensionPixelSize(R.dimen.list_bottom_padding)));
        views.state.showLoading();

        viewModel = new ViewModelProvider(this).get(FindDonorViewModel.class);

        buildGroupFilter();
        TextChangeListener.onChange(views.search, text -> viewModel.setArea(text));

        viewModel.donors().observe(this, this::render);
    }

    /**
     * Builds the group filter from {@link BloodGroup}, so the chips and the
     * values compared against donor records come from the same source. The old
     * screen kept a {@code String[]} of nine labels in the activity and matched
     * on {@code "All"} by string equality.
     */
    private void buildGroupFilter() {
        views.groupFilter.addView(buildChip(CHIP_ID_ALL, getString(R.string.find_donor_group_all)));
        for (BloodGroup group : BloodGroup.values()) {
            views.groupFilter.addView(buildChip(chipIdFor(group), group.label()));
        }
        views.groupFilter.check(CHIP_ID_ALL);

        views.groupFilter.setOnCheckedStateChangeListener((chipGroup, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                viewModel.setGroup(null);
                return;
            }
            int checkedId = checkedIds.get(0);
            viewModel.setGroup(checkedId == CHIP_ID_ALL ? null : groupForChipId(checkedId));
            views.list.scrollToPosition(0);
        });
    }

    private Chip buildChip(int id, @NonNull String label) {
        Chip chip = new Chip(this, null, com.google.android.material.R.attr.chipStyle);
        chip.setId(id);
        chip.setText(label);
        chip.setCheckable(true);
        chip.setCheckedIconVisible(false);
        return chip;
    }

    private static int chipIdFor(@NonNull BloodGroup group) {
        return group.ordinal() + 2;
    }

    private static @Nullable BloodGroup groupForChipId(int chipId) {
        int index = chipId - 2;
        BloodGroup[] values = BloodGroup.values();
        return index >= 0 && index < values.length ? values[index] : null;
    }

    private void render(@NonNull Result<List<BloodDonor>> result) {
        if (!result.isSuccess()) {
            views.list.setVisibility(View.GONE);
            views.resultCount.setVisibility(View.GONE);
            views.state.showError(result.error(), null);
            return;
        }

        List<BloodDonor> donors = result.value();
        if (donors == null || donors.isEmpty()) {
            views.list.setVisibility(View.GONE);
            views.resultCount.setVisibility(View.GONE);
            if (viewModel.isFiltering()) {
                views.state.showEmpty(
                        R.drawable.ic_empty_search_48,
                        R.string.empty_donors_filtered_title,
                        R.string.empty_donors_filtered_body);
            } else {
                views.state.showEmpty(
                        R.drawable.ic_empty_inbox_48,
                        R.string.empty_donors_title,
                        R.string.empty_donors_body);
            }
            return;
        }

        views.state.showContent();
        views.list.setVisibility(View.VISIBLE);
        views.resultCount.setVisibility(View.VISIBLE);
        views.resultCount.setText(getResources().getQuantityString(
                R.plurals.find_donor_count, donors.size(), donors.size()));
        adapter.submitList(donors);
    }

    @Override
    public void onCallClicked(@NonNull BloodDonor donor) {
        dial(donor.getPhone());
    }
}
