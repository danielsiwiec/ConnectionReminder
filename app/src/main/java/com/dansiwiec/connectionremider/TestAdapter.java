package com.dansiwiec.connectionremider;


import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dansiwiec.connectionremider.persistance.FileStorageHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerView adapter enabling undo on a swiped away item.
 */
public class TestAdapter extends RecyclerView.Adapter {

    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
    private final FileStorageHelper fileStorageHelper;

    List<String> items;
    List<String> itemsPendingRemoval;

    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<String, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be

    public TestAdapter(FileStorageHelper fileStorageHelper) {
        items = fileStorageHelper.readItems();
        itemsPendingRemoval = new ArrayList<>();
        this.fileStorageHelper = fileStorageHelper;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TestViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TestViewHolder viewHolder = (TestViewHolder) holder;
        final String item = items.get(position);

        if (itemsPendingRemoval.contains(item)) {
            // we need to show the "undo" state of the row
            viewHolder.itemView.setBackgroundColor(Color.RED);
            viewHolder.titleTextView.setVisibility(View.GONE);
            viewHolder.undoButton.setVisibility(View.VISIBLE);
            viewHolder.undoButton.setOnClickListener(v -> {
                // user wants to undo the removal, let's cancel the pending task
                Runnable pendingRemovalRunnable = pendingRunnables.get(item);
                pendingRunnables.remove(item);
                if (pendingRemovalRunnable != null) handler.removeCallbacks(pendingRemovalRunnable);
                itemsPendingRemoval.remove(item);
                // this will rebind the row in "normal" state
                notifyItemChanged(items.indexOf(item));
            });
        } else {
            // we need to show the "normal" state
            viewHolder.itemView.setBackgroundColor(Color.WHITE);
            viewHolder.titleTextView.setVisibility(View.VISIBLE);
            viewHolder.titleTextView.setText(item);
            viewHolder.undoButton.setVisibility(View.GONE);
            viewHolder.undoButton.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void pendingRemoval(int position) {
        final String item = items.get(position);
        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = () -> remove(items.indexOf(item));
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        String item = items.get(position);
        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.remove(item);
        }
        if (items.contains(item)) {
            items.remove(position);
            fileStorageHelper.writeItems(items);

            notifyItemRemoved(position);
        }
    }

    public boolean isPendingRemoval(int position) {
        String item = items.get(position);
        return itemsPendingRemoval.contains(item);
    }

    public void pushToBottom(int position) {
        String item = items.get(position);
        if (items.contains(item)) {
            items.remove(position);
            items.add(item);
            fileStorageHelper.writeItems(items);
            notifyItemChanged(position);
            notifyItemMoved(position, items.size() - 1);
        }
    }

    public void addItem(String person) {
        items.add(person);
        fileStorageHelper.writeItems(items);
        notifyItemInserted(items.size() - 1);
    }

    /**
     * ViewHolder capable of presenting two states: "normal" and "undo" state.
     */
    static class TestViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        Button undoButton;

        public TestViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view, parent, false));
            titleTextView = itemView.findViewById(R.id.title_text_view);
            undoButton = itemView.findViewById(R.id.undo_button);
        }

    }
}

