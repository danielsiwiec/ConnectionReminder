package com.dansiwiec.connectionremider;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;

public class SwipeListUtils {

    private static final String TAG = "SwipeListUtils";

    public static void setUpRecyclerView(RecyclerView mRecyclerView, AppCompatActivity activity) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        mRecyclerView.setAdapter(new TestAdapter());
        mRecyclerView.setHasFixedSize(true);
        setUpItemTouchHelper(mRecyclerView, activity);
        setUpAnimationDecoratorHelper(mRecyclerView);
    }

    /**
     * This is the standard support library way of implementing "swipe to delete" feature. You can do custom drawing in onChildDraw method
     * but whatever you draw will disappear once the swipe is over, and while the items are animating to their new position the recycler view
     * background will be visible. That is rarely an desired effect.
     *
     * @param mRecyclerView
     * @param activity
     */
    private static void setUpItemTouchHelper(final RecyclerView mRecyclerView, final AppCompatActivity activity) {

        int iconMargin = (int) activity.getResources().getDimension(R.dimen.ic_margin);

        Consumer<RecyclerView.ViewHolder> onSwipe = (RecyclerView.ViewHolder viewHolder) -> {
            int swipedPosition = viewHolder.getAdapterPosition();
            TestAdapter adapter = (TestAdapter) mRecyclerView.getAdapter();
            boolean undoOn = adapter.isUndoOn();
            if (undoOn) {
                adapter.pendingRemoval(swipedPosition);
            } else {
                adapter.remove(swipedPosition);
            }
        };

        ItemTouchHelper rightTouchHelper = ItemTouchFactory.create(
                onSwipe,
                RIGHT,
                Color.RED,
                ContextCompat.getDrawable(activity, R.drawable.ic_clear_24dp),
                iconMargin);

        ItemTouchHelper leftTouchHelper = ItemTouchFactory.create(
                onSwipe,
                LEFT,
                Color.GREEN,
                ContextCompat.getDrawable(activity, R.drawable.ic_check_24dp),
                iconMargin);

        rightTouchHelper.attachToRecyclerView(mRecyclerView);
        leftTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    /**
     * We're gonna setup another ItemDecorator that will draw the red background in the empty space while the items are animating to thier new positions
     * after an item is removed.
     *
     * @param mRecyclerView
     */
    private static void setUpAnimationDecoratorHelper(RecyclerView mRecyclerView) {
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
    }


}
