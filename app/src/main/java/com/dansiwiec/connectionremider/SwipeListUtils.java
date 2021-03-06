package com.dansiwiec.connectionremider;

import android.graphics.Color;

import java.util.function.Consumer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;
import static com.dansiwiec.connectionremider.ItemTouchBuilder.itemTouchBuilder;

public class SwipeListUtils {

    public static void setUpRecyclerView(RecyclerView mRecyclerView, AppCompatActivity activity, PersonsViewModel viewModel) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        TestAdapter adapter = new TestAdapter(viewModel);
        viewModel.list().observe(activity, persons -> {
            adapter.setData(persons);
        });
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setHasFixedSize(true);
        setUpItemTouchHelper(mRecyclerView, activity);
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

        Consumer<RecyclerView.ViewHolder> deleteItem = (RecyclerView.ViewHolder viewHolder) -> {
            int swipedPosition = viewHolder.getAdapterPosition();
            TestAdapter adapter = (TestAdapter) mRecyclerView.getAdapter();
            adapter.pendingRemoval(swipedPosition);
        };

        Consumer<RecyclerView.ViewHolder> pushToBottom = (RecyclerView.ViewHolder viewHolder) -> {
            int swipedPosition = viewHolder.getAdapterPosition();
            TestAdapter adapter = (TestAdapter) mRecyclerView.getAdapter();
            adapter.pushToBottom(swipedPosition);
        };

        ItemTouchHelper rightTouchHelper = itemTouchBuilder()
                .onSwipe(deleteItem)
                .backgroundColor(Color.RED)
                .swipeDirections(RIGHT)
                .icon(ContextCompat.getDrawable(activity, R.drawable.ic_clear_24dp))
                .iconMargin(iconMargin)
                .build();

        ItemTouchHelper leftTouchHelper = itemTouchBuilder()
                .onSwipe(pushToBottom)
                .backgroundColor(Color.GREEN)
                .swipeDirections(LEFT)
                .icon(ContextCompat.getDrawable(activity, R.drawable.ic_check_24dp))
                .iconMargin(iconMargin)
                .build();

        rightTouchHelper.attachToRecyclerView(mRecyclerView);
        leftTouchHelper.attachToRecyclerView(mRecyclerView);
    }

}
