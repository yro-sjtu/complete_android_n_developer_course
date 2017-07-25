package com.yellowredorange.listviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.listView);
        final ArrayList<String> myFamily = new ArrayList<String>();
        myFamily.add("Frank");
        myFamily.add("Amy");
        myFamily.add("Tom");
        myFamily.add("Ross");
        myFamily.add("Rachel");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myFamily);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Here, parent: listView; view: row of listView; position: 0,1,2...; id: row id of item clicked
                // reference: https://developer.android.com/reference/android/widget/AdapterView.OnItemClickListener.html
                Log.i("person name", myFamily.get(position));
                Log.i("id: ", String.valueOf(id));

                Toast.makeText(MainActivity.this, "Hello " + myFamily.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
