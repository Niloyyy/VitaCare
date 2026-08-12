package com.example.vitacare_app_250.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.example.vitacare_app_250.R;

import java.util.regex.Pattern;

/**
 * Input validation rules.
 *
 * <p>Two changes from the old {@code ValidationUtils}.
 *
 * <p>It no longer takes {@code EditText} arguments. Coupling the rules to a
 * widget meant they could only run on the main thread with a live view
 * attached, which is why none of them were unit tested. Every method here takes
 * a String and returns a value, so {@code ValidatorsTest} can cover them.
 *
 * <p>It no longer uses {@code android.util.Patterns}. Those are compiled from
 * the platform and return default values under a plain JVM unit test, so tests
 * against them silently pass. {@link #EMAIL} is an explicit pattern instead.
 *
 * <p>Each rule returns a {@code @StringRes} message, or {@link #VALID} when the
 * input is acceptable, so the caller sets an error without re-deciding copy.
 */
public final class Validators {

    /** Returned when input passes. */
    public static final int VALID = 0;

    /** Minimum characters in a password. Mirrors Firebase Auth's own floor. */
    public static final int MIN_PASSWORD_LENGTH = 8;

    /** Licence numbers in the register are fixed-width. */
    public static final int LICENCE_LENGTH = 7;

    private static final int MIN_PHONE_DIGITS = 10;
    private static final int MAX_PHONE_DIGITS = 15;

    /**
     * Pragmatic email shape check: one @, a non-empty local part, and a dotted
     * domain. Deliberately not RFC 5322 — the authoritative check is whether
     * the address can receive mail, which only the server can answer.
     */
    private static final Pattern EMAIL = Pattern.compile(
            "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$");

    /** At least one digit, one symbol, and no whitespace. */
    private static final Pattern PASSWORD_DIGIT = Pattern.compile(".*[0-9].*");
    private static final Pattern PASSWORD_SYMBOL = Pattern.compile(".*[^A-Za-z0-9].*");

    private Validators() {
    }

    /** @return {@link #VALID}, or the message describing what is wrong */
    @StringRes
    public static int email(@Nullable String value) {
        String trimmed = trim(value);
        if (trimmed.isEmpty()) {
            return R.string.error_required;
        }
        return EMAIL.matcher(trimmed).matches() ? VALID : R.string.error_invalid_email;
    }

    @StringRes
    public static int password(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return R.string.error_required;
        }
        if (value.length() < MIN_PASSWORD_LENGTH) {
            return R.string.error_weak_password;
        }
        if (containsWhitespace(value)) {
            return R.string.error_weak_password;
        }
        if (!PASSWORD_DIGIT.matcher(value).matches()) {
            return R.string.error_weak_password;
        }
        return PASSWORD_SYMBOL.matcher(value).matches() ? VALID : R.string.error_weak_password;
    }

    /**
     * Checks a password against its confirmation. Only reports a mismatch —
     * the strength of the first field is {@link #password}'s job.
     */
    @StringRes
    public static int passwordConfirmation(@Nullable String password, @Nullable String confirmation) {
        if (confirmation == null || confirmation.isEmpty()) {
            return R.string.error_required;
        }
        return equal(password, confirmation) ? VALID : R.string.error_password_mismatch;
    }

    /**
     * Checks a contact number by digit count rather than by locale-specific
     * format, so international numbers are not rejected.
     */
    @StringRes
    public static int phone(@Nullable String value) {
        String trimmed = trim(value);
        if (trimmed.isEmpty()) {
            return R.string.error_required;
        }
        int digits = 0;
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (Character.isDigit(c)) {
                digits++;
            } else if (c != '+' && c != '-' && c != ' ' && c != '(' && c != ')') {
                return R.string.error_invalid_phone;
            }
        }
        return digits >= MIN_PHONE_DIGITS && digits <= MAX_PHONE_DIGITS
                ? VALID
                : R.string.error_invalid_phone;
    }

    /** A person's name: present, and long enough to be a real one. */
    @StringRes
    public static int fullName(@Nullable String value) {
        String trimmed = Texts.collapseWhitespace(value);
        if (trimmed.isEmpty()) {
            return R.string.error_required;
        }
        return trimmed.length() >= 2 ? VALID : R.string.error_name_too_short;
    }

    @StringRes
    public static int licence(@Nullable String value) {
        String trimmed = trim(value);
        if (trimmed.isEmpty()) {
            return R.string.error_required;
        }
        return trimmed.length() == LICENCE_LENGTH ? VALID : R.string.error_invalid_licence;
    }

    /** Any field that simply must not be blank. */
    @StringRes
    public static int required(@Nullable String value) {
        return trim(value).isEmpty() ? R.string.error_required : VALID;
    }

    /** True when a rule returned {@link #VALID}. */
    public static boolean isValid(@StringRes int result) {
        return result == VALID;
    }

    private static @NonNull String trim(@Nullable String value) {
        return value == null ? "" : value.trim();
    }

    private static boolean equal(@Nullable String a, @Nullable String b) {
        return a == null ? b == null : a.equals(b);
    }

    private static boolean containsWhitespace(@NonNull String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isWhitespace(value.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
