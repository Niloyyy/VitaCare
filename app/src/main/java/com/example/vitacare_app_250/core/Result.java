package com.example.vitacare_app_250.core;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * The outcome of an asynchronous operation: either a value or a typed failure.
 *
 * <p>Every repository call reports through this type, which is what lets the UI
 * layer handle success and failure at a single place instead of threading two
 * callbacks (or a nullable value plus a stringly-typed error) through each
 * screen.
 *
 * @param <T> type of the value carried on success
 */
public final class Result<T> {

    private final @Nullable T value;
    private final @Nullable AppError error;

    private Result(@Nullable T value, @Nullable AppError error) {
        this.value = value;
        this.error = error;
    }

    public static <T> Result<T> success(@Nullable T value) {
        return new Result<>(value, null);
    }

    public static <T> Result<T> failure(@NonNull AppError error) {
        return new Result<>(null, Objects.requireNonNull(error, "error"));
    }

    public boolean isSuccess() {
        return error == null;
    }

    /**
     * @return the success value, or {@code null} when the operation failed or
     *         succeeded with no payload
     */
    public @Nullable T value() {
        return value;
    }

    /** @return the failure, or {@code null} when the operation succeeded */
    public @Nullable AppError error() {
        return error;
    }

    /** Receives a {@link Result} once the operation finishes. */
    public interface Callback<T> {
        void onResult(@NonNull Result<T> result);
    }
}
