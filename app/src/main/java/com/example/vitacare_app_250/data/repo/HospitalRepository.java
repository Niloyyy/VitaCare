package com.example.vitacare_app_250.data.repo;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.core.AppError;
import com.example.vitacare_app_250.core.Result;
import com.example.vitacare_app_250.data.DbPaths;
import com.example.vitacare_app_250.data.FirebaseProvider;
import com.example.vitacare_app_250.data.model.Hospital;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

/** Reads hospital availability from {@code hospitals/}. */
public final class HospitalRepository {

    /** Every hospital, kept live so bed counts update as they are reported. */
    public @NonNull LiveData<Result<List<Hospital>>> observeAll() {
        if (!FirebaseProvider.isAvailable()) {
            MutableLiveData<Result<List<Hospital>>> data = new MutableLiveData<>();
            data.setValue(Result.failure(
                    AppError.of(AppError.Kind.UNKNOWN, R.string.error_database_unavailable)));
            return data;
        }
        return new LiveQuery<>(
                FirebaseProvider.reference(DbPaths.HOSPITALS),
                snapshot -> {
                    List<Hospital> hospitals = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Hospital hospital = Hospital.fromSnapshot(child);
                        if (hospital != null) {
                            hospitals.add(hospital);
                        }
                    }
                    return hospitals;
                });
    }
}
