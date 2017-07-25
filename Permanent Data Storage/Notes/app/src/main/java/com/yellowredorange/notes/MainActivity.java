package com.yellowredorange.notes;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> notes;
    ArrayAdapter<String> adapter;
    static final int REQUEST_CODE = 1;
    SharedPreferences data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.listView);
        data = this.getSharedPreferences(getPackageName(), MODE_PRIVATE);

        try {
            notes = (ArrayList<String>) ObjectSerializer.deserialize(data.getString("notes", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (Exception e) {
            notes = new ArrayList<>();
        }

        if(notes.size() == 0) {
            notes.add("Example Notes");
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notes);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), NoteEditorActivity.class);
                intent.putExtra("noteId", position);
                intent.putExtra("noteContent", notes.get(position));
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int itemToDelete = position;
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Are you sure to delete the item?")
                        .setMessage("Once deleted, this item cannot be retrieved.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notes.remove(itemToDelete);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.addNote:
                Intent newNote = new Intent(this, NoteEditorActivity.class);
                newNote.putExtra("noteId", notes.size());
                newNote.putExtra("noteContent", "");
                startActivityForResult(newNote, REQUEST_CODE);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                int id = data.getIntExtra("noteId", 0);
                String newContent = data.getStringExtra("newContent");
                if(newContent == null || newContent.equals("")) {
                    notes.remove(id);
                } else {
                    if (id < notes.size()) {
                        notes.set(id, newContent);
                    } else {
                        notes.add(newContent);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            data.edit()
                    .putString("notes", ObjectSerializer.serialize(notes))
                    .apply();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
