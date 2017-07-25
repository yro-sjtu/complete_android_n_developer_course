package com.yellowredorange.languagepreferences;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SharedPreferences data;
    String language;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = this.getSharedPreferences(getPackageName(), MODE_PRIVATE);
        textView = (TextView) findViewById(R.id.textView);

        if(data.getString("language", null) == null) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_menu_preferences)
                    .setTitle("Choose a language")
                    .setMessage("Which language would you like?")
                    .setPositiveButton("English", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setLanguage("English");
                        }
                    })
                    .setNegativeButton("Spanish", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setLanguage("Spanish");
                        }
                    })
                    .show();
        } else {
            setLanguage(data.getString("language", null));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        data.edit()
                .putString("language", language)
                .apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {
            case R.id.english:
                setLanguage("English");
                return true;
            case R.id.spanish:
                setLanguage("Spanish");
                return true;
            default:
                return false;
        }
    }

    private void setLanguage(String language) {
        this.language = language;
        textView.setText(language);
    }
}
