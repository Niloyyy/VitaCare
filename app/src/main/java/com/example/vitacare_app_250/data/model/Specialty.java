package com.example.vitacare_app_250.data.model;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.util.Texts;

/**
 * The medical specialties the directory is partitioned by.
 *
 * <p>The old code spread this across three places that disagreed: the picker
 * screen hardcoded four labels and mapped "Medicine" to the {@code neurology}
 * node, the doctor form built its key by lower-casing whatever the spinner
 * showed, and {@code strings.xml} held a separate array whose first entry was
 * the prompt "Select Specialist". A doctor who registered under a label that
 * did not lower-case to one of the four keys became invisible to patients.
 *
 * <p>Key, label, icon and accent now travel together, and the database key is
 * fixed rather than derived from display text.
 */
public enum Specialty {

    CARDIOLOGY(
            "cardiology",
            R.string.specialty_cardiology,
            R.string.specialty_cardiology_detail,
            R.drawable.ic_specialty_cardiology_24,
            R.color.specialty_cardiology,
            R.color.specialty_cardiology_container),

    ORTHOPEDICS(
            "orthopedics",
            R.string.specialty_orthopedics,
            R.string.specialty_orthopedics_detail,
            R.drawable.ic_specialty_orthopedics_24,
            R.color.specialty_orthopedics,
            R.color.specialty_orthopedics_container),

    NEUROLOGY(
            "neurology",
            R.string.specialty_neurology,
            R.string.specialty_neurology_detail,
            R.drawable.ic_specialty_neurology_24,
            R.color.specialty_neurology,
            R.color.specialty_neurology_container),

    NEPHROLOGY(
            "nephrology",
            R.string.specialty_nephrology,
            R.string.specialty_nephrology_detail,
            R.drawable.ic_specialty_nephrology_24,
            R.color.specialty_nephrology,
            R.color.specialty_nephrology_container);

    private final String key;
    private final @StringRes int labelRes;
    private final @StringRes int descriptionRes;
    private final @DrawableRes int iconRes;
    private final @ColorRes int accentRes;
    private final @ColorRes int containerRes;

    Specialty(String key,
              @StringRes int labelRes,
              @StringRes int descriptionRes,
              @DrawableRes int iconRes,
              @ColorRes int accentRes,
              @ColorRes int containerRes) {
        this.key = key;
        this.labelRes = labelRes;
        this.descriptionRes = descriptionRes;
        this.iconRes = iconRes;
        this.accentRes = accentRes;
        this.containerRes = containerRes;
    }

    /** The child node under {@code docs/} that holds this specialty's doctors. */
    public @NonNull String key() {
        return key;
    }

    public @StringRes int labelRes() {
        return labelRes;
    }

    public @StringRes int descriptionRes() {
        return descriptionRes;
    }

    public @DrawableRes int iconRes() {
        return iconRes;
    }

    /** Icon tint. */
    public @ColorRes int accentRes() {
        return accentRes;
    }

    /** Fill behind the icon. */
    public @ColorRes int containerRes() {
        return containerRes;
    }

    /**
     * Parses a stored specialty key, tolerating the casing and whitespace
     * variations already present in the database.
     *
     * @return the matching specialty, or {@code null} if unrecognised
     */
    public static @Nullable Specialty fromKey(@Nullable String key) {
        if (key == null) {
            return null;
        }
        String normalised = Texts.foldKey(key);
        for (Specialty specialty : values()) {
            if (specialty.key.equals(normalised)) {
                return specialty;
            }
        }
        return null;
    }
}
