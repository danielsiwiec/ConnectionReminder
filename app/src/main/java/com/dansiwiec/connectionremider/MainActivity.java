package com.dansiwiec.connectionremider;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.dansiwiec.connectionremider.persistance.PersonsFileRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Sample activity demonstrating swipe to remove on recycler view functionality.
 * The interesting parts are drawing while items are animating to their new positions after some items is removed
 * and a possibility to undo the removal.
 */
public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    private PersonsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler_view);

        PersonsFileRepository repository = new PersonsFileRepository(getFilesDir());
        viewModel = new PersonsViewModel(repository);
        viewModel.init();

        SwipeListUtils.setUpRecyclerView(mRecyclerView, this, viewModel);

        FloatingActionButton addPersonButton = findViewById(R.id.add_person);
        addPersonButton.setOnClickListener(view -> showAddItemDialog(MainActivity.this));
    }

    private void showAddItemDialog(Context c) {
        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setMessage("Person name")
                .setView(taskEditText)
                .setPositiveButton("Add", (dialog1, which) -> {
                    String person = String.valueOf(taskEditText.getText());
                    viewModel.add(person);
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}