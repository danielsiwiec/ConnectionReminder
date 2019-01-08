package com.dansiwiec.connectionremider;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Sample activity demonstrating swipe to remove on recycler view functionality.
 * The interesting parts are drawing while items are animating to their new positions after some items is removed
 * and a possibility to undo the removal.
 */
public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRecyclerView = findViewById(R.id.recycler_view);
        SwipeListUtils.setUpRecyclerView(mRecyclerView, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_undo_checkbox) {
            item.setChecked(!item.isChecked());
            ((TestAdapter)mRecyclerView.getAdapter()).setUndoOn(item.isChecked());
        }
        if (item.getItemId() == R.id.menu_item_add_5_items) {
            ((TestAdapter)mRecyclerView.getAdapter()).addItems(5);
        }
        return super.onOptionsItemSelected(item);
    }

}