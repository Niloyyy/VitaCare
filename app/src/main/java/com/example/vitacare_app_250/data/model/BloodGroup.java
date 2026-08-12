package com.example.vitacare_app_250.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The eight ABO/Rh blood groups.
 *
 * <p>Donor records store the label verbatim, so parsing has to tolerate what is
 * already there: mixed case, stray whitespace, and the typographic minus sign
 * that phone keyboards produce in place of a hyphen. Getting this wrong makes a
 * donor unreachable through the group filter, which is the one thing the
 * feature exists to do.
 *
 * <p>These constants are the canonical list; {@link #labels()} feeds both the
 * picker and the filter chips.
 */
public enum BloodGroup {

    A_POSITIVE("A+"),
    A_NEGATIVE("A-"),
    B_POSITIVE("B+"),
    B_NEGATIVE("B-"),
    AB_POSITIVE("AB+"),
    AB_NEGATIVE("AB-"),
    O_POSITIVE("O+"),
    O_NEGATIVE("O-");

    private final String label;

    BloodGroup(String label) {
        this.label = label;
    }

    /** The canonical display and storage form, e.g. {@code "AB-"}. */
    public @NonNull String label() {
        return label;
    }

    /**
     * Parses a stored or user-entered blood group.
     *
     * @return the matching group, or {@code null} if the text is not one of the
     *         eight valid groups
     */
    public static @Nullable BloodGroup fromLabel(@Nullable String raw) {
        String normalised = normalise(raw);
        if (normalised == null) {
            return null;
        }
        for (BloodGroup group : values()) {
            if (group.label.equals(normalised)) {
                return group;
            }
        }
        return null;
    }

    public static boolean isValid(@Nullable String raw) {
        return fromLabel(raw) != null;
    }

    /**
     * All eight labels, in clinical order.
     *
     * <p>This is the single source for both the picker on Become a Donor and
     * the filter chips on Find a Donor. Deliberately not a {@code string-array}
     * resource: the labels are also compared against values already stored in
     * the database, so a translated or re-punctuated copy would stop matching
     * existing donor records.
     */
    public static @NonNull List<String> labels() {
        List<String> out = new ArrayList<>(values().length);
        for (BloodGroup group : values()) {
            out.add(group.label);
        }
        return out;
    }

    /**
     * Strips whitespace, upper-cases, and folds the Unicode minus sign
     * (U+2212), en dash (U+2013) and em dash (U+2014) onto a plain hyphen.
     */
    private static @Nullable String normalise(@Nullable String raw) {
        if (raw == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(raw.length());
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (Character.isWhitespace(c)) {
                continue;
            }
            if (c == '−' || c == '–' || c == '—') {
                sb.append('-');
            } else {
                sb.append(Character.toUpperCase(c));
            }
        }
        return sb.length() == 0 ? null : sb.toString();
    }
}
