package com.example.vitacare_app_250.data.repo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.core.AppError;
import com.example.vitacare_app_250.core.Result;
import com.example.vitacare_app_250.data.DbPaths;
import com.example.vitacare_app_250.data.FirebaseProvider;
import com.example.vitacare_app_250.data.model.Doctor;
import com.example.vitacare_app_250.data.model.Specialty;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reads and writes published doctor profiles under {@code docs/}.
 */
public final class DoctorRepository {

    /**
     * Doctors within one specialty, kept live.
     *
     * @param specialty which node under {@code docs/} to read
     */
    public @NonNull LiveData<Result<List<Doctor>>> observeBySpecialty(@NonNull Specialty specialty) {
        if (!FirebaseProvider.isAvailable()) {
            return unavailable();
        }
        return new LiveQuery<>(
                FirebaseProvider.reference(DbPaths.DOCTORS).child(specialty.key()),
                snapshot -> {
                    List<Doctor> doctors = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Doctor doctor = Doctor.fromSnapshot(child);
                        if (doctor != null) {
                            doctors.add(doctor);
                        }
                    }
                    sortByName(doctors);
                    return doctors;
                });
    }

    /**
     * Every doctor across every specialty, kept live.
     *
     * <p>Backs the "All doctors" directory, which previously showed a bare
     * {@code ListView} of names with no contact details and no way to open a
     * profile.
     */
    public @NonNull LiveData<Result<List<Doctor>>> observeAll() {
        if (!FirebaseProvider.isAvailable()) {
            return unavailable();
        }
        return new LiveQuery<>(
                FirebaseProvider.reference(DbPaths.DOCTORS),
                snapshot -> {
                    List<Doctor> doctors = new ArrayList<>();
                    for (DataSnapshot specialtyNode : snapshot.getChildren()) {
                        for (DataSnapshot child : specialtyNode.getChildren()) {
                            Doctor doctor = Doctor.fromSnapshot(child);
                            if (doctor != null) {
                                doctors.add(doctor);
                            }
                        }
                    }
                    sortByName(doctors);
                    return doctors;
                });
    }

    /**
     * Loads the practice profile belonging to {@code uid}, if one is published.
     *
     * <p>Profiles are keyed by uid but filed under a specialty node, and the
     * caller does not know which. This scans the specialty nodes rather than
     * storing a redundant index.
     *
     * @param callback receives the profile, or a success with {@code null} when
     *                 the doctor has not published one yet
     */
    public void loadOwnProfile(@NonNull String uid, @NonNull Result.Callback<Doctor> callback) {
        if (!FirebaseProvider.isAvailable()) {
            callback.onResult(Result.failure(
                    AppError.of(AppError.Kind.UNKNOWN, R.string.error_database_unavailable)));
            return;
        }
        FirebaseProvider.reference(DbPaths.DOCTORS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot specialtyNode : snapshot.getChildren()) {
                            DataSnapshot own = specialtyNode.child(uid);
                            if (own.exists()) {
                                callback.onResult(Result.success(Doctor.fromSnapshot(own)));
                                return;
                            }
                        }
                        callback.onResult(Result.success(null));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onResult(Result.failure(AppError.fromDatabaseError(error)));
                    }
                });
    }

    /**
     * Publishes or updates a doctor's practice profile.
     *
     * <p>The record is keyed by the doctor's uid, not a fresh
     * {@code UUID.randomUUID()} as before. That change is what stops every save
     * from appending another copy of the same doctor to the directory — the old
     * form produced a new listing on each submit, with no way to edit or remove
     * the earlier ones.
     *
     * <p>When the specialty changes the profile is written under the new node
     * and removed from the old one, so a doctor never appears in two
     * specialties at once.
     *
     * @param previousSpecialty specialty the profile was filed under before, or
     *                          {@code null} if this is the first publish
     */
    public void saveOwnProfile(@NonNull String uid,
                               @NonNull Doctor doctor,
                               @NonNull Specialty specialty,
                               @Nullable Specialty previousSpecialty,
                               @NonNull Result.Callback<Void> callback) {

        if (!FirebaseProvider.isAvailable()) {
            callback.onResult(Result.failure(
                    AppError.of(AppError.Kind.UNKNOWN, R.string.error_database_unavailable)));
            return;
        }

        FirebaseProvider.reference(DbPaths.DOCTORS)
                .child(specialty.key())
                .child(uid)
                .setValue(doctor.toMap())
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        callback.onResult(Result.failure(
                                AppError.fromDatabaseException(task.getException())));
                        return;
                    }
                    if (previousSpecialty == null || previousSpecialty == specialty) {
                        callback.onResult(Result.success(null));
                        return;
                    }
                    // Best effort: the new record is already live, so a failure
                    // to delete the stale one must not be reported as a failed
                    // save. It leaves a duplicate, not a lost profile.
                    FirebaseProvider.reference(DbPaths.DOCTORS)
                            .child(previousSpecialty.key())
                            .child(uid)
                            .removeValue()
                            .addOnCompleteListener(ignored ->
                                    callback.onResult(Result.success(null)));
                });
    }

    /**
     * Checks a name, email and licence against the register at
     * {@code doctors/}.
     *
     * <p>The old implementation downloaded the entire register and compared
     * every entry on the device. That reads every registered doctor's licence
     * number onto any handset that opens the verification screen. Here the
     * query is narrowed server-side to the single record matching the licence
     * number, and only that record's name and email are compared.
     *
     * <p>This still relies on database rules to restrict reads — a client-side
     * check cannot be an authorisation boundary. See the note in README.
     */
    public void verifyLicence(@NonNull String name,
                              @NonNull String email,
                              @NonNull String licence,
                              @NonNull Result.Callback<Boolean> callback) {

        if (!FirebaseProvider.isAvailable()) {
            callback.onResult(Result.failure(
                    AppError.of(AppError.Kind.UNKNOWN, R.string.error_database_unavailable)));
            return;
        }

        FirebaseProvider.reference(DbPaths.DOCTOR_REGISTER)
                .orderByChild(DbPaths.FIELD_LICENSE)
                .equalTo(licence.trim())
                .limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot record : snapshot.getChildren()) {
                            String registeredName = record.child(DbPaths.FIELD_NAME)
                                    .getValue(String.class);
                            String registeredEmail = record.child(DbPaths.FIELD_EMAIL)
                                    .getValue(String.class);
                            boolean matches = equalsIgnoringCase(registeredName, name)
                                    && equalsIgnoringCase(registeredEmail, email);
                            callback.onResult(Result.success(matches));
                            return;
                        }
                        callback.onResult(Result.success(false));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onResult(Result.failure(AppError.fromDatabaseError(error)));
                    }
                });
    }

    private static boolean equalsIgnoringCase(@Nullable String a, @Nullable String b) {
        return a != null && b != null && a.trim().equalsIgnoreCase(b.trim());
    }

    private static void sortByName(@NonNull List<Doctor> doctors) {
        Collections.sort(doctors, (a, b) ->
                a.getName().compareToIgnoreCase(b.getName()));
    }

    private static <T> LiveData<Result<List<T>>> unavailable() {
        MutableLiveData<Result<List<T>>> data = new MutableLiveData<>();
        data.setValue(Result.failure(
                AppError.of(AppError.Kind.UNKNOWN, R.string.error_database_unavailable)));
        return data;
    }
}
