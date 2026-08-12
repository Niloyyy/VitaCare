package com.example.vitacare_app_250.ui.common;

import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.databinding.ItemInfoRowBinding;

/**
 * Populates an {@code item_info_row} include.
 *
 * <p>Detail screens previously built each line as
 * {@code textView.setText("Contact: " + value)}, which glued the label to the
 * value in one string. That is unlocalisable, reads as a single run to a screen
 * reader, and produced output like "Contact: null" whenever the value was
 * missing — the old doctor profile printed "No Contact", "No Degree" and
 * "No Chamber Info" as if they were data.
 */
public final class InfoRow {

    private InfoRow() {
    }

    /**
     * Fills a row, hiding it when there is nothing to show.
     *
     * @param row      the included binding
     * @param iconRes  leading icon
     * @param labelRes field label
     * @param value    field value; blank or null hides the whole row
     */
    public static void bind(@NonNull ItemInfoRowBinding row,
                            @DrawableRes int iconRes,
                            @StringRes int labelRes,
                            @Nullable String value) {
        bind(row, iconRes, labelRes, value, null);
    }

    /**
     * Fills a row and, when {@code onAction} is supplied, shows the trailing
     * action button.
     *
     * @param onAction handler for the trailing button, or {@code null} for none
     */
    public static void bind(@NonNull ItemInfoRowBinding row,
                            @DrawableRes int iconRes,
                            @StringRes int labelRes,
                            @Nullable String value,
                            @Nullable View.OnClickListener onAction) {

        String text = value == null ? "" : value.trim();
        if (text.isEmpty()) {
            // Better to omit the row than to render a placeholder that reads
            // like content.
            row.getRoot().setVisibility(View.GONE);
            return;
        }

        row.getRoot().setVisibility(View.VISIBLE);
        row.infoIcon.setImageResource(iconRes);
        row.infoLabel.setText(labelRes);
        row.infoValue.setText(text);

        if (onAction == null) {
            row.infoAction.setVisibility(View.GONE);
            row.infoAction.setOnClickListener(null);
        } else {
            row.infoAction.setVisibility(View.VISIBLE);
            row.infoAction.setOnClickListener(onAction);
        }
    }

    /**
     * Fills a row, substituting a "not set" placeholder for a blank value
     * instead of hiding it. For the profile screen, where the absence of a
     * value is itself information.
     */
    public static void bindOrNotSet(@NonNull ItemInfoRowBinding row,
                                    @DrawableRes int iconRes,
                                    @StringRes int labelRes,
                                    @Nullable String value) {
        String text = value == null || value.trim().isEmpty()
                ? row.getRoot().getContext().getString(R.string.profile_not_set)
                : value.trim();
        row.getRoot().setVisibility(View.VISIBLE);
        row.infoIcon.setImageResource(iconRes);
        row.infoLabel.setText(labelRes);
        row.infoValue.setText(text);
        row.infoAction.setVisibility(View.GONE);
    }

    /** Sets the trailing button's icon and its spoken description. */
    public static void setAction(@NonNull ItemInfoRowBinding row,
                                 @DrawableRes int iconRes,
                                 @NonNull CharSequence contentDescription) {
        row.infoAction.setIconResource(iconRes);
        row.infoAction.setContentDescription(contentDescription);
    }
}
