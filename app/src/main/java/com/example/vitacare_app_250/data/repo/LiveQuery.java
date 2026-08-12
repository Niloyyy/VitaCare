package com.example.vitacare_app_250.data.repo;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.vitacare_app_250.core.AppError;
import com.example.vitacare_app_250.core.Result;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Bridges a Realtime Database query to {@link LiveData}.
 *
 * <p>This exists to fix a leak that ran through every list screen. They each
 * called {@code addValueEventListener} in {@code onCreate} and never removed
 * it, so the listener — and through it the activity — stayed referenced by the
 * Firebase client for the life of the process. Rotating the hospital screen a
 * few times left several dead activities alive, each still parsing snapshots
 * and calling {@code notifyDataSetChanged} on a detached adapter.
 *
 * <p>{@code LiveData} solves it structurally: the listener is attached in
 * {@link #onActive()} and removed in {@link #onInactive()}, which the framework
 * drives from the observing lifecycle. Nothing listens while no screen is
 * watching, and nothing has to remember to unsubscribe.
 *
 * @param <T> element type produced from each child snapshot
 */
public final class LiveQuery<T> extends LiveData<Result<List<T>>> {

    /** Converts a whole snapshot into the list the UI consumes. */
    public interface Mapper<T> {
        @NonNull
        List<T> map(@NonNull DataSnapshot snapshot);
    }

    private final Query query;
    private final Mapper<T> mapper;

    private final ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            setValue(Result.success(mapper.map(snapshot)));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            setValue(Result.failure(AppError.fromDatabaseError(error)));
        }
    };

    public LiveQuery(@NonNull Query query, @NonNull Mapper<T> mapper) {
        this.query = query;
        this.mapper = mapper;
    }

    @Override
    protected void onActive() {
        query.addValueEventListener(listener);
    }

    @Override
    protected void onInactive() {
        query.removeEventListener(listener);
    }
}
