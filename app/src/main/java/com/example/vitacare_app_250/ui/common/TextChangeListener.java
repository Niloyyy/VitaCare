package com.example.vitacare_app_250.ui.common;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.NonNull;

/**
 * Single-method {@link TextWatcher}.
 *
 * <p>Saves the six lines of empty overrides that appeared verbatim at four
 * call sites in the old code.
 */
public abstract class TextChangeListener implements TextWatcher {

    /** Attaches a listener that runs {@code action} on each change. */
    public static void onChange(@NonNull EditText field, @NonNull Consumer action) {
        field.addTextChangedListener(new TextChangeListener() {
            @Override
            public void onTextChanged(@NonNull String text) {
                action.accept(text);
            }
        });
    }

    /** Receives the field's current text. */
    public interface Consumer {
        void accept(@NonNull String text);
    }

    public abstract void onTextChanged(@NonNull String text);

    @Override
    public final void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // No-op.
    }

    @Override
    public final void onTextChanged(CharSequence s, int start, int before, int count) {
        // No-op; the value is read from afterTextChanged, once the edit has
        // been applied.
    }

    @Override
    public final void afterTextChanged(Editable s) {
        onTextChanged(s == null ? "" : s.toString());
    }
}
