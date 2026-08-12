package com.example.vitacare_app_250.ui.base;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.core.AppError;
import com.example.vitacare_app_250.session.SessionManager;
import com.example.vitacare_app_250.util.Texts;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

/**
 * Shared behaviour for every screen.
 *
 * <p>Collects the things each activity previously did by hand, inconsistently
 * or not at all: edge-to-edge inset handling, toolbar wiring, user feedback,
 * and the guard against touching views after the activity has gone away.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // LandingActivity launches under the splash theme; restore the real one
        // so it does not keep the splash window background.
        setTheme(R.style.Theme_VitaCare);
        applyWindowBackground();
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
    }

    /**
     * Paints the window itself in the theme background colour.
     *
     * <p>This is what lets every screen's root layout omit an
     * {@code android:background}: without it each root would paint a
     * full-screen rectangle on top of an identical one already drawn by the
     * window, doubling the fill rate on the largest surface in the app.
     *
     * <p>Setting it explicitly rather than relying on the theme is necessary
     * because {@code setTheme()} after the window exists does not reliably
     * re-resolve {@code android:windowBackground} — LandingActivity would keep
     * the splash drawable behind its content.
     */
    private void applyWindowBackground() {
        TypedValue value = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.colorBackground, value, true)) {
            getWindow().setBackgroundDrawable(new ColorDrawable(value.data));
        }
    }

    protected @NonNull SessionManager session() {
        if (sessionManager == null) {
            sessionManager = new SessionManager(this);
        }
        return sessionManager;
    }

    /**
     * Wires a toolbar as the app bar with an up affordance.
     *
     * <p>Replaces the hand-placed {@code ImageButton} with
     * {@code @android:drawable/ic_media_previous} — a media skip-back icon —
     * that every screen used as a back button. Beyond looking wrong, those
     * buttons were 20dp on several screens, less than half the 48dp minimum
     * touch target, and were absent entirely from three screens that could
     * still be navigated into.
     */
    protected void setUpToolbar(@NonNull MaterialToolbar toolbar, @StringRes int titleRes) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(titleRes);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    /** As {@link #setUpToolbar(MaterialToolbar, int)}, with a runtime title. */
    protected void setUpToolbar(@NonNull MaterialToolbar toolbar, @Nullable CharSequence title) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    /**
     * Pads {@code view} by the system bar insets.
     *
     * <p>{@code EdgeToEdge.enable()} was called on three screens but no screen
     * consumed the resulting insets, so content sat under the status bar and
     * the gesture handle on every device with either.
     *
     * @param view  the view to pad, usually the app bar or root
     * @param sides which edges to apply
     */
    protected void applyInsets(@NonNull View view, @NonNull Edge... sides) {
        final int startPadding = view.getPaddingLeft();
        final int topPadding = view.getPaddingTop();
        final int endPadding = view.getPaddingRight();
        final int bottomPadding = view.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(view, (target, windowInsets) -> {
            Insets bars = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            target.setPadding(
                    startPadding + (has(sides, Edge.LEFT) ? bars.left : 0),
                    topPadding + (has(sides, Edge.TOP) ? bars.top : 0),
                    endPadding + (has(sides, Edge.RIGHT) ? bars.right : 0),
                    bottomPadding + (has(sides, Edge.BOTTOM) ? bars.bottom : 0));
            return windowInsets;
        });
        ViewCompat.requestApplyInsets(view);
    }

    /** Which edges {@link #applyInsets} should pad. */
    protected enum Edge {
        LEFT, TOP, RIGHT, BOTTOM
    }

    private static boolean has(Edge[] sides, Edge edge) {
        for (Edge side : sides) {
            if (side == edge) {
                return true;
            }
        }
        return false;
    }

    /**
     * True while it is still safe to touch this activity's views.
     *
     * <p>Every Firebase callback in the old code updated the UI unconditionally.
     * Sign in, rotate before the role lookup returns, and the completion handler
     * ran against a destroyed activity. Async handlers should return early when
     * this is false.
     */
    protected boolean isAlive() {
        return !isFinishing() && !isDestroyed();
    }

    /** Shows a transient message. No-op once the activity is gone. */
    protected void showMessage(@StringRes int messageRes) {
        if (isAlive()) {
            Snackbar.make(rootView(), messageRes, Snackbar.LENGTH_LONG).show();
        }
    }

    protected void showMessage(@NonNull CharSequence message) {
        if (isAlive()) {
            Snackbar.make(rootView(), message, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Reports a failure, with a Retry action when one is offered.
     *
     * @param onRetry action to run if the user retries, or {@code null} for none
     */
    protected void showError(@NonNull AppError error, @Nullable Runnable onRetry) {
        if (!isAlive()) {
            return;
        }
        Snackbar snackbar = Snackbar.make(rootView(), error.messageRes(), Snackbar.LENGTH_LONG);
        if (onRetry != null) {
            snackbar.setAction(R.string.action_retry, v -> onRetry.run());
        }
        snackbar.show();
    }

    /** The view Snackbars anchor to. */
    protected @NonNull View rootView() {
        return findViewById(android.R.id.content);
    }

    /**
     * Opens the dialer for {@code phone}, pre-filled but not dialling.
     *
     * <p>{@code ACTION_DIAL} needs no permission and leaves the user in
     * control, which is the right default for calling a stranger's number off a
     * public register.
     */
    protected void dial(@Nullable String phone) {
        String dialable = Texts.toDialable(phone);
        if (dialable.isEmpty()) {
            showMessage(R.string.error_invalid_phone);
            return;
        }
        try {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + dialable)));
        } catch (ActivityNotFoundException e) {
            showMessage(R.string.error_no_dialer);
        }
    }

    /** Dismisses the soft keyboard, e.g. before submitting a form. */
    protected void hideKeyboard() {
        View focused = getCurrentFocus();
        if (focused == null) {
            return;
        }
        InputMethodManager imm = getSystemService(InputMethodManager.class);
        if (imm != null) {
            imm.hideSoftInputFromWindow(focused.getWindowToken(), 0);
        }
        focused.clearFocus();
    }

    /**
     * Clears the back stack and starts {@code target}.
     *
     * <p>Used at the boundaries between signed-out and signed-in states so that
     * pressing Back after signing in cannot return to the login screen, and
     * signing out cannot return to the dashboard.
     */
    protected void restartAt(@NonNull Class<?> target) {
        Intent intent = new Intent(this, target)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
