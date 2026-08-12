package com.example.vitacare_app_250.data.repo;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.core.AppError;
import com.example.vitacare_app_250.core.Result;
import com.example.vitacare_app_250.data.DbPaths;
import com.example.vitacare_app_250.data.FirebaseProvider;
import com.example.vitacare_app_250.data.model.BloodDonor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/** Reads and writes the volunteer donor register at {@code blood_donors/}. */
public final class DonorRepository {

    /** Every registered donor, kept live. */
    public @NonNull LiveData<Result<List<BloodDonor>>> observeAll() {
        if (!FirebaseProvider.isAvailable()) {
            MutableLiveData<Result<List<BloodDonor>>> data = new MutableLiveData<>();
            data.setValue(Result.failure(
                    AppError.of(AppError.Kind.UNKNOWN, R.string.error_database_unavailable)));
            return data;
        }
        return new LiveQuery<>(
                FirebaseProvider.reference(DbPaths.BLOOD_DONORS),
                snapshot -> {
                    List<BloodDonor> donors = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        BloodDonor donor = BloodDonor.fromSnapshot(child);
                        if (donor != null) {
                            donors.add(donor);
                        }
                    }
                    return donors;
                });
    }

    /**
     * Adds a donor, refusing a second registration for the same phone number.
     *
     * <p>The duplicate check queries on {@code Phone} alone. The old check also
     * required the name and blood group to match before treating a record as a
     * duplicate, which meant the same number could be registered repeatedly
     * under slightly different spellings — exactly the case that produces a
     * register full of the same person.
     *
     * <p>A client-side check races against concurrent writers. The register
     * tolerates that: a rare duplicate is a cosmetic problem, and enforcing
     * uniqueness properly would mean keying records by phone number, which
     * changes the storage layout for existing data.
     */
    public void register(@NonNull BloodDonor donor, @NonNull Result.Callback<Void> callback) {
        if (!FirebaseProvider.isAvailable()) {
            callback.onResult(Result.failure(
                    AppError.of(AppError.Kind.UNKNOWN, R.string.error_database_unavailable)));
            return;
        }

        DatabaseReference register = FirebaseProvider.reference(DbPaths.BLOOD_DONORS);
        register.orderByChild(DbPaths.FIELD_PHONE)
                .equalTo(donor.getPhone())
                .limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                            callback.onResult(Result.failure(AppError.of(
                                    AppError.Kind.UNKNOWN, R.string.become_donor_duplicate)));
                            return;
                        }
                        write(register, donor, callback);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onResult(Result.failure(AppError.fromDatabaseError(error)));
                    }
                });
    }

    private void write(@NonNull DatabaseReference register,
                       @NonNull BloodDonor donor,
                       @NonNull Result.Callback<Void> callback) {
        register.push()
                .setValue(donor.toMap())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onResult(Result.success(null));
                    } else {
                        callback.onResult(Result.failure(
                                AppError.fromDatabaseException(task.getException())));
                    }
                });
    }
}
