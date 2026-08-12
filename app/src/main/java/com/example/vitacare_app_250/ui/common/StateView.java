package com.example.vitacare_app_250.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.example.vitacare_app_250.R;
import com.example.vitacare_app_250.core.AppError;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

/**
 * The loading / empty / error placeholder that sits behind every list.
 *
 * <p>The old screens had none of this. A slow load showed a bare
 * {@code ProgressBar} floating in the middle of a blank screen; an empty result
 * showed nothing at all, indistinguishable from a load that had not finished;
 * and a failure showed a Toast with the raw Firebase message that vanished
 * after two seconds and left the same blank screen behind.
 *
 * <p>One view handles all four states so a screen calls exactly one method and
 * cannot leave two of them visible at once.
 */
public final class StateView extends FrameLayout {

    private final CircularProgressIndicator progress;
    private final View messageGroup;
    private final ImageView icon;
    private final TextView title;
    private final TextView body;
    private final MaterialButton action;

    public StateView(@NonNull Context context) {
        this(context, null);
    }

    public StateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_state, this, true);
        progress = findViewById(R.id.state_progress);
        messageGroup = findViewById(R.id.state_message_group);
        icon = findViewById(R.id.state_icon);
        title = findViewById(R.id.state_title);
        body = findViewById(R.id.state_body);
        action = findViewById(R.id.state_action);
        showContent();
    }

    /** Hides the placeholder entirely; the screen's real content is showing. */
    public void showContent() {
        setVisibility(GONE);
        progress.setVisibility(GONE);
        messageGroup.setVisibility(GONE);
    }

    /** Shows a spinner. Used only before the first result arrives. */
    public void showLoading() {
        setVisibility(VISIBLE);
        progress.setVisibility(VISIBLE);
        messageGroup.setVisibility(GONE);
        setContentDescription(getContext().getString(R.string.cd_loading));
    }

    /**
     * Shows an empty result.
     *
     * @param iconRes  illustration to display
     * @param titleRes short statement of what is missing
     * @param bodyText what the user can do about it
     */
    public void showEmpty(@DrawableRes int iconRes,
                          @StringRes int titleRes,
                          @NonNull CharSequence bodyText) {
        setVisibility(VISIBLE);
        progress.setVisibility(GONE);
        messageGroup.setVisibility(VISIBLE);
        icon.setImageResource(iconRes);
        icon.setContentDescription(getContext().getString(R.string.cd_empty_illustration));
        title.setText(titleRes);
        body.setText(bodyText);
        action.setVisibility(GONE);
        setContentDescription(title.getText() + ". " + bodyText);
    }

    public void showEmpty(@DrawableRes int iconRes, @StringRes int titleRes, @StringRes int bodyRes) {
        showEmpty(iconRes, titleRes, getContext().getString(bodyRes));
    }

    /**
     * Shows a failure, with a retry affordance when one makes sense.
     *
     * @param onRetry action for the retry button, or {@code null} to hide it
     */
    public void showError(@NonNull AppError error, @Nullable Runnable onRetry) {
        setVisibility(VISIBLE);
        progress.setVisibility(GONE);
        messageGroup.setVisibility(VISIBLE);
        icon.setImageResource(error.kind() == AppError.Kind.NETWORK
                ? R.drawable.ic_cloud_off_48
                : R.drawable.ic_empty_inbox_48);
        icon.setContentDescription(null);
        title.setText(error.titleRes());
        body.setText(error.messageRes());
        if (onRetry == null) {
            action.setVisibility(GONE);
        } else {
            action.setVisibility(VISIBLE);
            action.setText(R.string.action_retry);
            action.setOnClickListener(v -> onRetry.run());
        }
        setContentDescription(title.getText() + ". " + body.getText());
    }
}
