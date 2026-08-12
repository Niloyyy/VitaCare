package com.example.vitacare_app_250.ui.common;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Even vertical spacing between list rows, with a larger gap at the two ends.
 *
 * <p>Replaces the old decoration, which applied its spacing as {@code left},
 * {@code right} and {@code bottom} offsets on every item. Because the values
 * were hardcoded to physical left/right rather than start/end they did not
 * mirror under RTL, and the last row had no bottom gap distinct from the
 * inter-row gap, so the list ran straight into the navigation bar.
 */
public final class SpacingItemDecoration extends RecyclerView.ItemDecoration {

    private final int gap;
    private final int edgeGap;
    private final int bottomGap;

    /**
     * @param gap       vertical space between adjacent rows
     * @param edgeGap   space above the first row
     * @param bottomGap space below the last row; size it to clear the system
     *                  navigation bar
     */
    public SpacingItemDecoration(int gap, int edgeGap, int bottomGap) {
        this.gap = gap;
        this.edgeGap = edgeGap;
        this.bottomGap = bottomGap;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect,
                               @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == RecyclerView.NO_POSITION) {
            return;
        }
        int lastIndex = state.getItemCount() - 1;
        outRect.top = position == 0 ? edgeGap : gap;
        outRect.bottom = position == lastIndex ? bottomGap : 0;
    }
}
