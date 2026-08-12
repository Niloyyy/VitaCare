package com.example.vitacare_app_250.ui.common;

import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.example.vitacare_app_250.util.Validators;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A text field bound to a validation rule.
 *
 * <p>Fixes how forms reported problems. Validation used to run as a chain of
 * early returns, so submitting a blank form surfaced only the first error;
 * fixing it revealed the next, one modal Toast at a time. Some errors went to
 * {@code setError} on the widget and others to a Toast, so where a message
 * appeared depended on which check tripped.
 *
 * <p>Here {@link Form#validate()} evaluates every field, marks all of the
 * failures at once against their own inputs, and moves focus to the first.
 * Errors also clear as the user types rather than lingering under corrected
 * input.
 */
public final class FormField {

    /** A validation rule: text in, {@code @StringRes} message or VALID out. */
    public interface Rule {
        @StringRes
        int check(@Nullable String value);
    }

    private final TextInputLayout layout;
    private final Rule rule;
    private boolean touched;

    private FormField(@NonNull TextInputLayout layout, @NonNull Rule rule) {
        this.layout = layout;
        this.rule = rule;

        EditText editText = layout.getEditText();
        if (editText != null) {
            TextChangeListener.onChange(editText, text -> {
                if (touched) {
                    // Re-check as they type, but only after a failed submit —
                    // flagging a password as too short while it is being typed
                    // is noise, not help.
                    applyResult(rule.check(text));
                }
            });
        }
    }

    public static @NonNull FormField of(@NonNull TextInputLayout layout, @NonNull Rule rule) {
        return new FormField(layout, rule);
    }

    /** The field's trimmed text. */
    public @NonNull String value() {
        EditText editText = layout.getEditText();
        return editText == null ? "" : editText.getText().toString().trim();
    }

    /** The field's text with no trimming; for passwords, where spaces count. */
    public @NonNull String rawValue() {
        EditText editText = layout.getEditText();
        return editText == null ? "" : editText.getText().toString();
    }

    public void setText(@Nullable String text) {
        EditText editText = layout.getEditText();
        if (editText != null) {
            editText.setText(text);
        }
    }

    /** Marks a failure reported by the server, e.g. "email already in use". */
    public void setError(@StringRes int messageRes) {
        touched = true;
        applyResult(messageRes);
        layout.requestFocus();
    }

    public void clearError() {
        layout.setError(null);
    }

    /** Validates and marks this field. @return true when it passes */
    public boolean validate() {
        touched = true;
        int result = rule.check(rawValue());
        applyResult(result);
        return Validators.isValid(result);
    }

    private void applyResult(@StringRes int result) {
        layout.setError(Validators.isValid(result)
                ? null
                : layout.getContext().getString(result));
    }

    private void focus() {
        EditText editText = layout.getEditText();
        if (editText != null) {
            editText.requestFocus();
        } else {
            layout.requestFocus();
        }
    }

    /** A set of fields validated together. */
    public static final class Form {
        private final List<FormField> fields = new ArrayList<>();

        public @NonNull Form add(@NonNull FormField field) {
            fields.add(field);
            return this;
        }

        public @NonNull FormField addField(@NonNull TextInputLayout layout, @NonNull Rule rule) {
            FormField field = FormField.of(layout, rule);
            fields.add(field);
            return field;
        }

        /**
         * Validates every field, marking all failures, and focuses the first
         * one that failed.
         *
         * @return true when every field passes
         */
        public boolean validate() {
            FormField firstFailure = null;
            for (FormField field : fields) {
                if (!field.validate() && firstFailure == null) {
                    firstFailure = field;
                }
            }
            if (firstFailure != null) {
                firstFailure.focus();
                return false;
            }
            return true;
        }

        public void clearErrors() {
            for (FormField field : fields) {
                field.clearError();
            }
        }
    }
}
