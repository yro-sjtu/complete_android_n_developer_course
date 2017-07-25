package com.yellowredorange.notes;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class NoteEditorActivity extends AppCompatActivity {

    int noteId;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        editText = (EditText) findViewById(R.id.editText);

        Intent intent = getIntent();
        noteId = intent.getIntExtra("noteId", -1);
        String noteContent = intent.getStringExtra("noteContent");
        editText.setText(noteContent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("newContent", editText.getText().toString());
        intent.putExtra("noteId", noteId);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
