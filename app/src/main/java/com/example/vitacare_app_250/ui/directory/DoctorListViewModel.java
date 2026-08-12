package com.example.vitacare_app_250.ui.directory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.vitacare_app_250.core.Result;
import com.example.vitacare_app_250.data.model.Doctor;
import com.example.vitacare_app_250.data.model.Specialty;
import com.example.vitacare_app_250.data.repo.DoctorRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds the doctor list and its search query across configuration changes.
 *
 * <p>Two behaviours here were previously broken in the activity. Rotating lost
 * the loaded list and the search text and re-fetched from scratch. And the
 * filter mutated the same {@code ArrayList} the adapter was reading, so the
 * live listener — which cleared and refilled that list on every remote change —
 * would intermittently wipe an active search and show the unfiltered set.
 *
 * <p>Here the source list and the query are separate inputs and the filtered
 * list is derived from both, so neither can clobber the other.
 */
public class DoctorListViewModel extends ViewModel {

    private final MutableLiveData<String> query = new MutableLiveData<>("");
    private final LiveData<Result<List<Doctor>>> source;
    private final MediatorLiveData<Result<List<Doctor>>> filtered = new MediatorLiveData<>();

    DoctorListViewModel(@NonNull DoctorRepository repository, @Nullable Specialty specialty) {
        this.source = specialty == null
                ? repository.observeAll()
                : repository.observeBySpecialty(specialty);

        filtered.addSource(source, result -> recompute());
        filtered.addSource(query, text -> recompute());
    }

    /** The list to display: loaded, then filtered by the current query. */
    public @NonNull LiveData<Result<List<Doctor>>> doctors() {
        return filtered;
    }

    /** The current search text. */
    public @NonNull LiveData<String> query() {
        return query;
    }

    public void setQuery(@Nullable String text) {
        String next = text == null ? "" : text;
        if (!next.equals(query.getValue())) {
            query.setValue(next);
        }
    }

    /** True when a search is narrowing the list, which changes the empty copy. */
    public boolean isSearching() {
        String text = query.getValue();
        return text != null && !text.trim().isEmpty();
    }

    private void recompute() {
        Result<List<Doctor>> result = source.getValue();
        if (result == null) {
            return;
        }
        if (!result.isSuccess()) {
            filtered.setValue(result);
            return;
        }

        List<Doctor> all = result.value();
        if (all == null) {
            filtered.setValue(Result.success(Collections.emptyList()));
            return;
        }

        String text = query.getValue();
        if (text == null || text.trim().isEmpty()) {
            filtered.setValue(Result.success(all));
            return;
        }

        List<Doctor> matches = new ArrayList<>();
        for (Doctor doctor : all) {
            if (doctor.matches(text)) {
                matches.add(doctor);
            }
        }
        filtered.setValue(Result.success(matches));
    }

    /** Supplies the specialty the view model was opened for. */
    static final class Factory implements ViewModelProvider.Factory {
        private final @Nullable Specialty specialty;

        Factory(@Nullable Specialty specialty) {
            this.specialty = specialty;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new DoctorListViewModel(new DoctorRepository(), specialty);
        }
    }
}
