package com.yellowredorange.timestableapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int base = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ListView multiples = (ListView) findViewById(R.id.multiples);
        SeekBar number = (SeekBar) findViewById(R.id.seekBar);
        number.setMax(20);
        number.setProgress(10);
        number.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int min = 1;
                base = progress < min? min : progress;
                showListView(base, multiples);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        showListView(10, multiples);
    }

    private void showListView(int base, ListView listView) {
        ArrayList<Integer> timesTableContent = new ArrayList<>();
        for(int i = 1; i <= 10; i++) {
            timesTableContent.add(base * i);
        }
        listView.setAdapter(new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, timesTableContent));
    }
}
