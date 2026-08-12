package com.example.vitacare_app_250.ui.hospital;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vitacare_app_250.core.Result;
import com.example.vitacare_app_250.data.model.Hospital;
import com.example.vitacare_app_250.data.repo.HospitalRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds the hospital list, its search query and its sort order.
 *
 * <p>Sorting is the substantive fix. The old screen sorted the same list the
 * adapter and the live listener both wrote to, so the order survived only until
 * the next snapshot arrived — which, with a value listener on a node that
 * updates as beds are reported, could be seconds. Sorting also silently
 * discarded any active search, because it reordered the filtered list while the
 * next snapshot refilled it from the unfiltered source.
 *
 * <p>Here sort order is state, and the displayed list is recomputed from
 * source + query + order whenever any of the three changes.
 */
public class HospitalListViewModel extends ViewModel {

    /** How the list is ordered. */
    public enum SortOrder {
        NAME,
        BEDS_DESC,
        ICU_DESC
    }

    private final MutableLiveData<String> query = new MutableLiveData<>("");
    private final MutableLiveData<SortOrder> sortOrder = new MutableLiveData<>(SortOrder.NAME);
    private final LiveData<Result<List<Hospital>>> source;
    private final MediatorLiveData<Result<List<Hospital>>> displayed = new MediatorLiveData<>();

    public HospitalListViewModel() {
        this(new HospitalRepository());
    }

    HospitalListViewModel(@NonNull HospitalRepository repository) {
        this.source = repository.observeAll();
        displayed.addSource(source, result -> recompute());
        displayed.addSource(query, text -> recompute());
        displayed.addSource(sortOrder, order -> recompute());
    }

    public @NonNull LiveData<Result<List<Hospital>>> hospitals() {
        return displayed;
    }

    public void setQuery(@Nullable String text) {
        String next = text == null ? "" : text;
        if (!next.equals(query.getValue())) {
            query.setValue(next);
        }
    }

    public void setSortOrder(@NonNull SortOrder order) {
        if (order != sortOrder.getValue()) {
            sortOrder.setValue(order);
        }
    }

    public @NonNull SortOrder currentSortOrder() {
        SortOrder order = sortOrder.getValue();
        return order == null ? SortOrder.NAME : order;
    }

    public boolean isSearching() {
        String text = query.getValue();
        return text != null && !text.trim().isEmpty();
    }

    private void recompute() {
        Result<List<Hospital>> result = source.getValue();
        if (result == null) {
            return;
        }
        if (!result.isSuccess()) {
            displayed.setValue(result);
            return;
        }

        List<Hospital> all = result.value();
        if (all == null) {
            displayed.setValue(Result.success(Collections.emptyList()));
            return;
        }

        String text = query.getValue();
        List<Hospital> working = new ArrayList<>(all.size());
        for (Hospital hospital : all) {
            if (hospital.matches(text)) {
                working.add(hospital);
            }
        }

        sort(working, currentSortOrder());
        displayed.setValue(Result.success(working));
    }

    private static void sort(@NonNull List<Hospital> hospitals, @NonNull SortOrder order) {
        switch (order) {
            case BEDS_DESC:
                // Descending by count, then by name so equal counts have a
                // stable, meaningful order rather than snapshot order.
                Collections.sort(hospitals, (a, b) -> {
                    int byBeds = Integer.compare(b.bedsSortKey(), a.bedsSortKey());
                    return byBeds != 0 ? byBeds : a.getName().compareToIgnoreCase(b.getName());
                });
                break;
            case ICU_DESC:
                Collections.sort(hospitals, (a, b) -> {
                    int byIcu = Integer.compare(b.icuSortKey(), a.icuSortKey());
                    return byIcu != 0 ? byIcu : a.getName().compareToIgnoreCase(b.getName());
                });
                break;
            case NAME:
            default:
                Collections.sort(hospitals, (a, b) ->
                        a.getName().compareToIgnoreCase(b.getName()));
                break;
        }
    }
}
