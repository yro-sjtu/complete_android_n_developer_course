package com.yellowredorange.multipleactivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView friendListView = (ListView) findViewById(R.id.friendListView);
        final List<String> myFriends = Arrays.asList("Tommy", "Frank", "Jenny", "Mike");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myFriends);
        friendListView.setAdapter(arrayAdapter);

        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(getApplicationContext(), SecondActivity.class);
                String selected = myFriends.get(position);
                intent.putExtra("friend", selected);
                startActivity(intent);
            }
        });
    }
}
