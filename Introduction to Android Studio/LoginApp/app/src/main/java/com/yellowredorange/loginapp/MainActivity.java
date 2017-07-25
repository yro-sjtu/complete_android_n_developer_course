package com.yellowredorange.loginapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public void clickFunction(View view) {
        EditText userName = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        Log.i("username", userName.getText().toString());
        Log.i("password", password.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
