package com.example.vitacare_app_250.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

/**
 * Coercion helpers for values read out of the Realtime Database.
 *
 * <p>The database is schemaless: the same child can be a String in one record
 * and a Long or Double in the next, depending on which client wrote it. These
 * helpers absorb that instead of letting a {@code ClassCastException} or
 * {@code NumberFormatException} reach the UI.
 */
public final class Texts {

    private Texts() {
    }

    /** {@code value} with null folded to the empty string. */
    public static @NonNull String orEmpty(@Nullable String value) {
        return value == null ? "" : value;
    }

    /**
     * Renders any snapshot value as a trimmed string.
     *
     * <p>Numbers stored without a decimal part render without a trailing
     * ".0", so a phone number written as a number still reads correctly.
     *
     * @return the text, or {@code ""} for null
     */
    public static @NonNull String asString(@Nullable Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof String) {
            return ((String) value).trim();
        }
        if (value instanceof Long || value instanceof Integer) {
            return String.valueOf(value);
        }
        if (value instanceof Double || value instanceof Float) {
            double d = ((Number) value).doubleValue();
            if (d == Math.floor(d) && !Double.isInfinite(d)) {
                return String.valueOf((long) d);
            }
            return String.valueOf(d);
        }
        if (value instanceof Boolean) {
            return String.valueOf(value);
        }
        return value.toString().trim();
    }

    /**
     * Reads a non-negative count from a snapshot value.
     *
     * <p>Accepts numbers and numeric strings, and tolerates decoration such as
     * {@code "12 beds"} by taking the leading digits. Anything without a
     * leading number — {@code ""}, {@code "N/A"}, {@code "unknown"} — yields
     * {@code fallback}.
     *
     * @param fallback value to use when nothing numeric can be read
     */
    public static int asCount(@Nullable Object value, int fallback) {
        if (value == null) {
            return fallback;
        }
        if (value instanceof Number) {
            long asLong = ((Number) value).longValue();
            if (asLong < 0) {
                return fallback;
            }
            return (int) Math.min(asLong, Integer.MAX_VALUE);
        }

        String text = asString(value);
        if (text.isEmpty()) {
            return fallback;
        }

        int end = 0;
        while (end < text.length() && Character.isDigit(text.charAt(end))) {
            end++;
        }
        if (end == 0) {
            return fallback;
        }
        try {
            long parsed = Long.parseLong(text.substring(0, end));
            return (int) Math.min(parsed, Integer.MAX_VALUE);
        } catch (NumberFormatException e) {
            // Only reachable for digit runs longer than a long; treat as unknown.
            return fallback;
        }
    }

    /**
     * Case-folds a stored identifier — a role or specialty key.
     *
     * <p>Pinned to {@link Locale#ROOT} because these values are compared
     * against fixed ASCII constants. Under the default locale on a Turkish
     * device {@code "CARDIOLOGY".toLowerCase()} yields a dotless {@code ı},
     * which would stop matching the {@code "cardiology"} node and make every
     * doctor in that specialty invisible.
     */
    public static @NonNull String foldKey(@Nullable String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    /**
     * Case-folds text for search.
     *
     * <p>Uses {@link Locale#getDefault()}, unlike {@link #foldKey}: here both
     * sides of the comparison are human-language text the user typed and reads,
     * so folding should follow their language's rules.
     */
    public static @NonNull String foldForSearch(@Nullable String value) {
        return value == null ? "" : value.toLowerCase(Locale.getDefault());
    }

    /**
     * Collapses runs of whitespace and trims. Used on names and addresses so
     * that "Dhaka   Medical " and "Dhaka Medical" are the same value.
     */
    public static @NonNull String collapseWhitespace(@Nullable String value) {
        if (value == null) {
            return "";
        }
        return value.trim().replaceAll("\\s+", " ");
    }

    /**
     * Strips everything but digits and a leading {@code +}, giving a form
     * suitable for a {@code tel:} URI.
     */
    public static @NonNull String toDialable(@Nullable String phone) {
        if (phone == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(phone.length());
        for (int i = 0; i < phone.length(); i++) {
            char c = phone.charAt(i);
            if (Character.isDigit(c)) {
                sb.append(c);
            } else if (c == '+' && sb.length() == 0) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
