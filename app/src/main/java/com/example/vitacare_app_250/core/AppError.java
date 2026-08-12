package com.example.vitacare_app_250.core;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.example.vitacare_app_250.R;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseError;

/**
 * A failure, classified into something the UI can act on and already carrying
 * the message to show.
 *
 * <p>The old code surfaced raw SDK text straight to the user — strings like
 * "There is no user record corresponding to this identifier" or a bare
 * {@code DatabaseError.getMessage()}. Those leak implementation detail, cannot
 * be localised, and read as a bug report rather than an instruction. Mapping
 * happens once, here.
 */
public final class AppError {

    /** Broad failure categories the UI treats differently. */
    public enum Kind {
        /** No usable connection; retrying later is likely to work. */
        NETWORK,
        /** Credentials were rejected. */
        AUTH,
        /** Signed in, but not allowed to read or write this location. */
        PERMISSION,
        /** Anything else. */
        UNKNOWN
    }

    private final @NonNull Kind kind;
    private final @StringRes int messageRes;
    private final @Nullable String detail;

    private AppError(@NonNull Kind kind, @StringRes int messageRes, @Nullable String detail) {
        this.kind = kind;
        this.messageRes = messageRes;
        this.detail = detail;
    }

    public static AppError of(@NonNull Kind kind, @StringRes int messageRes) {
        return new AppError(kind, messageRes, null);
    }

    /**
     * Classifies an exception thrown by Firebase Auth or a Play Services task.
     */
    public static AppError fromAuthException(@Nullable Throwable throwable) {
        if (throwable instanceof FirebaseNetworkException) {
            return new AppError(Kind.NETWORK, R.string.error_network_body, message(throwable));
        }
        if (throwable instanceof FirebaseAuthWeakPasswordException) {
            return new AppError(Kind.AUTH, R.string.auth_weak_password_server, message(throwable));
        }
        if (throwable instanceof FirebaseAuthUserCollisionException) {
            return new AppError(Kind.AUTH, R.string.auth_email_in_use, message(throwable));
        }
        if (throwable instanceof FirebaseAuthInvalidUserException
                || throwable instanceof FirebaseAuthInvalidCredentialsException) {
            // Deliberately one message for "no such user" and "wrong password":
            // distinguishing them tells an attacker which emails are registered.
            return new AppError(Kind.AUTH, R.string.auth_invalid_credentials, message(throwable));
        }
        String raw = message(throwable);
        if (raw != null && raw.contains("TOO_MANY_ATTEMPTS")) {
            return new AppError(Kind.AUTH, R.string.auth_too_many_requests, raw);
        }
        return new AppError(Kind.UNKNOWN, R.string.error_generic_title, raw);
    }

    /** Classifies a Realtime Database listener cancellation. */
    public static AppError fromDatabaseError(@Nullable DatabaseError error) {
        if (error == null) {
            return new AppError(Kind.UNKNOWN, R.string.error_load_failed, null);
        }
        switch (error.getCode()) {
            case DatabaseError.PERMISSION_DENIED:
                return new AppError(Kind.PERMISSION, R.string.error_permission_body, error.getMessage());
            case DatabaseError.NETWORK_ERROR:
            case DatabaseError.DISCONNECTED:
            case DatabaseError.UNAVAILABLE:
                return new AppError(Kind.NETWORK, R.string.error_network_body, error.getMessage());
            default:
                return new AppError(Kind.UNKNOWN, R.string.error_load_failed, error.getMessage());
        }
    }

    /** Classifies a failure from a database write task. */
    public static AppError fromDatabaseException(@Nullable Throwable throwable) {
        String raw = message(throwable);
        // Locale.ROOT: this matches against the SDK's own English message, not
        // anything the user typed.
        if (raw != null && raw.toLowerCase(java.util.Locale.ROOT).contains("permission")) {
            return new AppError(Kind.PERMISSION, R.string.error_permission_body, raw);
        }
        if (throwable instanceof FirebaseNetworkException) {
            return new AppError(Kind.NETWORK, R.string.error_network_body, raw);
        }
        return new AppError(Kind.UNKNOWN, R.string.error_load_failed, raw);
    }

    public @NonNull Kind kind() {
        return kind;
    }

    /** The user-facing message for this failure. */
    public @StringRes int messageRes() {
        return messageRes;
    }

    /**
     * The underlying SDK text. Logged for diagnosis, never shown to the user.
     */
    public @Nullable String detail() {
        return detail;
    }

    /** Title to pair with {@link #messageRes()} in a full-screen error state. */
    public @StringRes int titleRes() {
        switch (kind) {
            case NETWORK:
                return R.string.error_network_title;
            case PERMISSION:
                return R.string.error_permission_title;
            default:
                return R.string.error_generic_title;
        }
    }

    private static @Nullable String message(@Nullable Throwable throwable) {
        return throwable == null ? null : throwable.getMessage();
    }

    @Override
    public @NonNull String toString() {
        return "AppError{" + kind + ", detail=" + detail + '}';
    }
}
