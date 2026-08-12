package com.example.vitacare_app_250.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vitacare_app_250.util.Texts;

/**
 * Which side of the app an account belongs to.
 *
 * <p>Previously this was the bare string {@code "patient"} / {@code "doctor"}
 * compared by hand at five call sites, with an unwritten default of "patient"
 * when the field was missing. That default was a privilege question dressed up
 * as a typo: it decided what an account could do. It is now explicit and
 * appears exactly once, in {@link #fromKey}.
 */
public enum Role {

    PATIENT("patient"),
    DOCTOR("doctor");

    private final String key;

    Role(String key) {
        this.key = key;
    }

    /** The value persisted at {@code users/<uid>/role}. */
    public @NonNull String key() {
        return key;
    }

    /**
     * Parses a stored role.
     *
     * @return the matching role, or {@code null} when the value is absent or
     *         unrecognised. Callers must decide what an unknown role means
     *         rather than silently receiving patient access.
     */
    public static @Nullable Role fromKey(@Nullable String key) {
        if (key == null) {
            return null;
        }
        String normalised = Texts.foldKey(key);
        for (Role role : values()) {
            if (role.key.equals(normalised)) {
                return role;
            }
        }
        return null;
    }
}
