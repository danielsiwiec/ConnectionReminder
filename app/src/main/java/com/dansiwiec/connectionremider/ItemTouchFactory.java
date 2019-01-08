package com.dansiwiec.connectionremider;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import java.util.function.Consumer;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemTouchFactory {

    public static ItemTouchHelper create(Consumer<RecyclerView.ViewHolder> onSwipe, final int swipeDirections, final int backgroundColor, final Drawable icon, final int iconMargin) {
        ColorDrawable background = new ColorDrawable(backgroundColor);
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, swipeDirections) {

            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                TestAdapter testAdapter = (TestAdapter) recyclerView.getAdapter();
                if (testAdapter.isUndoOn() && testAdapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                onSwipe.accept(viewHolder);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                boolean leftSwipe = dX < 0;

                drawBackground(c, (int) dX, itemView, leftSwipe);

                addIcon(itemView, leftSwipe, c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            private void drawBackground(Canvas c, int dX, View itemView, boolean leftSwipe) {
                if (leftSwipe) {
                    background.setBounds(itemView.getRight() + dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else {
                    background.setBounds(itemView.getLeft() + dX, itemView.getTop(), itemView.getLeft(), itemView.getBottom());
                }
                background.draw(c);
            }

            private void addIcon(View itemView, boolean leftSwipe, Canvas c) {
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = icon.getIntrinsicWidth();
                int intrinsicHeight = icon.getIntrinsicWidth();

                int iconLeft;
                int iconRight;

                if (leftSwipe) {
                    iconLeft = itemView.getRight() - iconMargin - intrinsicWidth;
                    iconRight = itemView.getRight() - iconMargin;
                } else {
                    iconLeft = itemView.getLeft() + iconMargin;
                    iconRight = itemView.getLeft() + iconMargin + intrinsicWidth;
                }

                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                icon.setBounds(iconLeft, xMarkTop, iconRight, xMarkBottom);

                icon.draw(c);
            }

        });
    }
}
