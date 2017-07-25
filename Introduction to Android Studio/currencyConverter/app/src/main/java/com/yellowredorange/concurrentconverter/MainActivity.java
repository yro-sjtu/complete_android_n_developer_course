package com.yellowredorange.concurrentconverter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public void converter(View view) {
        EditText dollarInput = (EditText) findViewById(R.id.dollarInput);
        Double poundAmount = Double.valueOf(dollarInput.getText().toString()) * 0.79;
        Toast.makeText(MainActivity.this, String.format("%.2f", poundAmount), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
