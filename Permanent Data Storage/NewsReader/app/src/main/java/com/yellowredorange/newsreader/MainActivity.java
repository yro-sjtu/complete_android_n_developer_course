package com.yellowredorange.newsreader;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> titles;
    ArrayList<String> contents;
    ArrayList<String> URLs;
    ArrayAdapter<String> adapter;
    SQLiteDatabase storyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.listView);
        titles = new ArrayList<>();
        contents = new ArrayList<>();
        URLs = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titles);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, StoryActivity.class);
                intent.putExtra("html", contents.get(position));
                intent.putExtra("url", URLs.get(position));
                startActivity(intent);
            }
        });

        storyDB = this.openOrCreateDatabase("Story", MODE_PRIVATE, null);
        storyDB.execSQL("CREATE TABLE IF NOT EXISTS story (id INT PRIMARY KEY, title VARCHAR, content VARCHAR, url VARCHAR)");

        try {
            initStoryDB();
        } catch(Exception e) {
            e.printStackTrace();
        }

        updateListView();

    }

    private void initStoryDB() throws Exception {
        // clear all entries in storyDB
        storyDB.execSQL("DELETE FROM story");

        DownloadTask task = new DownloadTask();
        String res = task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty").get();
        JSONArray arr = new JSONArray(res);
        int numberOfStories = Math.min(20, arr.length());

        for(int i = 0; i < numberOfStories; i++) {
            String id = arr.getString(i);
            String url = "https://hacker-news.firebaseio.com/v0/item/" + id + ".json?print=pretty";
            DownloadTask storyLoader = new DownloadTask();
            String ret = storyLoader.execute(url).get();
            JSONObject obj = new JSONObject(ret);
            if(!obj.isNull("title") && !obj.isNull("url")) {
                String storyTitle = obj.getString("title");
                String storyUrl = obj.getString("url");

                DownloadTask dl = new DownloadTask();
                String storyContent = dl.execute(storyUrl).get();

                String sql = "INSERT INTO story (id, title, content, url) VALUES ( ?, ?, ?, ?)";
                SQLiteStatement statement = storyDB.compileStatement(sql);
                statement.bindString(1, id);
                statement.bindString(2, storyTitle);
                statement.bindString(3, storyContent);
                statement.bindString(4, storyUrl);
                statement.execute();
            }
        }

        Log.i("Info", String.valueOf(numberOfStories));
    }

    private void updateListView() {
        Cursor c = storyDB.rawQuery("SELECT * FROM story", null);
        int titleIdx = c.getColumnIndex("title");
        int contentIdx = c.getColumnIndex("content");
        int urlIdx = c.getColumnIndex("url");

//        Log.i("Info", Integer.toString(titles.size()));
//        Log.i("Info", Integer.toString(contents.size()));

        if(c.moveToFirst()) {
            titles.clear();
            contents.clear();
            do {
                titles.add(c.getString(titleIdx));
                contents.add(c.getString(contentIdx));
                URLs.add(c.getString(urlIdx));
            } while(c.moveToNext());
        }

//        Log.i("Info", Integer.toString(titles.size()));
//        Log.i("Info", Integer.toString(contents.size()));

        adapter.notifyDataSetChanged();
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                StringBuilder sb = new StringBuilder();

                int data = reader.read();
                while(data != -1) {
                    char ch = (char) data;
                    sb.append(ch);
                    data = reader.read();
                }

                return sb.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
