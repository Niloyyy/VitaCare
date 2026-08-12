package com.example.vitacare_app_250.data.repo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.core.AppError;
import com.example.vitacare_app_250.core.Result;
import com.example.vitacare_app_250.data.DbPaths;
import com.example.vitacare_app_250.data.FirebaseProvider;
import com.example.vitacare_app_250.data.model.Role;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Sign-in, registration and role lookup.
 *
 * <p>The role check is the important part. Both login screens previously
 * defaulted a missing {@code users/<uid>/role} to {@code "patient"}, so an
 * account whose role record failed to write during signup — a real possibility,
 * since signup created the auth user and the role record as two independent
 * operations with no rollback — silently became a patient account. Here a
 * missing role is an error, and the user is signed back out rather than let
 * through with a guessed privilege level.
 */
public final class AuthRepository {

    private final FirebaseAuth auth;

    public AuthRepository() {
        this.auth = FirebaseProvider.auth();
    }

    /**
     * Signs in and confirms the account's role matches the screen the user
     * came from.
     *
     * <p>On any failure — bad credentials, missing role record, or the wrong
     * role — the user is signed out again so a half-established session cannot
     * be picked up by the next launch.
     *
     * @param expectedRole the role this entry point serves
     */
    public void signIn(@NonNull String email,
                       @NonNull String password,
                       @NonNull Role expectedRole,
                       @NonNull Result.Callback<SignedInUser> callback) {

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        callback.onResult(Result.failure(
                                AppError.fromAuthException(task.getException())));
                        return;
                    }
                    FirebaseUser user = auth.getCurrentUser();
                    if (user == null) {
                        callback.onResult(Result.failure(
                                AppError.of(AppError.Kind.AUTH, R.string.auth_invalid_credentials)));
                        return;
                    }
                    confirmRole(user, expectedRole, callback);
                });
    }

    private void confirmRole(@NonNull FirebaseUser user,
                             @NonNull Role expectedRole,
                             @NonNull Result.Callback<SignedInUser> callback) {

        if (!FirebaseProvider.isAvailable()) {
            auth.signOut();
            callback.onResult(Result.failure(
                    AppError.of(AppError.Kind.UNKNOWN, R.string.error_database_unavailable)));
            return;
        }

        FirebaseProvider.reference(DbPaths.USERS)
                .child(user.getUid())
                .child(DbPaths.FIELD_ROLE)
                .get()
                .addOnCompleteListener(roleTask -> {
                    if (!roleTask.isSuccessful()) {
                        auth.signOut();
                        callback.onResult(Result.failure(
                                AppError.of(AppError.Kind.UNKNOWN, R.string.auth_role_lookup_failed)));
                        return;
                    }

                    DataSnapshot snapshot = roleTask.getResult();
                    Role actual = snapshot == null
                            ? null
                            : Role.fromKey(snapshot.getValue(String.class));

                    if (actual == null) {
                        // No role on the account. Refusing is the safe reading:
                        // guessing here is what silently created patient
                        // accounts out of failed doctor registrations.
                        auth.signOut();
                        callback.onResult(Result.failure(
                                AppError.of(AppError.Kind.AUTH, R.string.auth_role_lookup_failed)));
                        return;
                    }

                    if (actual != expectedRole) {
                        auth.signOut();
                        int message = actual == Role.DOCTOR
                                ? R.string.auth_wrong_role_doctor
                                : R.string.auth_wrong_role_patient;
                        callback.onResult(Result.failure(
                                AppError.of(AppError.Kind.AUTH, message)));
                        return;
                    }

                    callback.onResult(Result.success(
                            new SignedInUser(user.getUid(), actual, user.getEmail())));
                });
    }

    /**
     * Creates an account and writes its role record.
     *
     * <p>If the role write fails the freshly created auth user is deleted, so a
     * retry is not blocked by "email already in use" against an account that
     * can never sign in. That rollback is the closest thing to a transaction
     * available across Auth and the database from a client.
     */
    public void register(@NonNull String email,
                         @NonNull String password,
                         @NonNull Role role,
                         @NonNull Result.Callback<Void> callback) {

        if (!FirebaseProvider.isAvailable()) {
            callback.onResult(Result.failure(
                    AppError.of(AppError.Kind.UNKNOWN, R.string.error_database_unavailable)));
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        callback.onResult(Result.failure(
                                AppError.fromAuthException(task.getException())));
                        return;
                    }
                    AuthResult authResult = task.getResult();
                    FirebaseUser user = authResult == null ? null : authResult.getUser();
                    if (user == null) {
                        callback.onResult(Result.failure(
                                AppError.of(AppError.Kind.UNKNOWN, R.string.error_generic_title)));
                        return;
                    }
                    writeRoleRecord(user, email, role, callback);
                });
    }

    private void writeRoleRecord(@NonNull FirebaseUser user,
                                 @NonNull String email,
                                 @NonNull Role role,
                                 @NonNull Result.Callback<Void> callback) {

        Map<String, Object> record = new LinkedHashMap<>();
        record.put(DbPaths.FIELD_EMAIL, email);
        record.put(DbPaths.FIELD_ROLE, role.key());

        FirebaseProvider.reference(DbPaths.USERS)
                .child(user.getUid())
                .setValue(record)
                .addOnCompleteListener(dbTask -> {
                    if (dbTask.isSuccessful()) {
                        // Registration is complete but the user should sign in
                        // explicitly, so drop the implicit session Firebase
                        // opened on account creation.
                        auth.signOut();
                        callback.onResult(Result.success(null));
                        return;
                    }
                    AppError error = AppError.fromDatabaseException(dbTask.getException());
                    user.delete().addOnCompleteListener(ignored ->
                            callback.onResult(Result.failure(error)));
                });
    }

    /** The outcome of a successful, role-confirmed sign-in. */
    public static final class SignedInUser {
        private final String uid;
        private final Role role;
        private final @Nullable String email;

        SignedInUser(@NonNull String uid, @NonNull Role role, @Nullable String email) {
            this.uid = uid;
            this.role = role;
            this.email = email;
        }

        public @NonNull String uid() {
            return uid;
        }

        public @NonNull Role role() {
            return role;
        }

        public @Nullable String email() {
            return email;
        }
    }
}
