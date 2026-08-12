package com.example.vitacare_app_250.ui.blood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vitacare_app_250.core.Result;
import com.example.vitacare_app_250.data.model.BloodDonor;
import com.example.vitacare_app_250.data.model.BloodGroup;
import com.example.vitacare_app_250.data.repo.DonorRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds the donor list and its two filters across configuration changes.
 */
public class FindDonorViewModel extends ViewModel {

    private final MutableLiveData<BloodGroup> group = new MutableLiveData<>(null);
    private final MutableLiveData<String> area = new MutableLiveData<>("");
    private final LiveData<Result<List<BloodDonor>>> source;
    private final MediatorLiveData<Result<List<BloodDonor>>> displayed = new MediatorLiveData<>();

    public FindDonorViewModel() {
        this(new DonorRepository());
    }

    FindDonorViewModel(@NonNull DonorRepository repository) {
        this.source = repository.observeAll();
        displayed.addSource(source, result -> recompute());
        displayed.addSource(group, value -> recompute());
        displayed.addSource(area, value -> recompute());
    }

    public @NonNull LiveData<Result<List<BloodDonor>>> donors() {
        return displayed;
    }

    /** @param value the group to filter by, or {@code null} for all groups */
    public void setGroup(@Nullable BloodGroup value) {
        if (value != group.getValue()) {
            group.setValue(value);
        }
    }

    public void setArea(@Nullable String value) {
        String next = value == null ? "" : value;
        if (!next.equals(area.getValue())) {
            area.setValue(next);
        }
    }

    /** True when either filter is narrowing the list. */
    public boolean isFiltering() {
        String text = area.getValue();
        return group.getValue() != null || (text != null && !text.trim().isEmpty());
    }

    private void recompute() {
        Result<List<BloodDonor>> result = source.getValue();
        if (result == null) {
            return;
        }
        if (!result.isSuccess()) {
            displayed.setValue(result);
            return;
        }

        List<BloodDonor> all = result.value();
        if (all == null) {
            displayed.setValue(Result.success(Collections.emptyList()));
            return;
        }

        BloodGroup selected = group.getValue();
        String areaText = area.getValue();

        List<BloodDonor> matches = new ArrayList<>();
        for (BloodDonor donor : all) {
            if (donor.matches(selected, areaText)) {
                matches.add(donor);
            }
        }
        Collections.sort(matches, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        displayed.setValue(Result.success(matches));
    }
}
