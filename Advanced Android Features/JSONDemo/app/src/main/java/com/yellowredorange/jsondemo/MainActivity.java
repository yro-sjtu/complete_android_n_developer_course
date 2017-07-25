package com.yellowredorange.jsondemo;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadTask task = new DownloadTask();
        task.execute("http://api.openweathermap.org/data/2.5//weather?q=London,uk&APPID=94ef3a850a02c5df1a4eba4ccf983abe");
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            URL url = null;
            HttpURLConnection connection = null;
            String result = "";

            try {

                url = new URL(urls[0]);

                connection = (HttpURLConnection) url.openConnection();

                InputStream in = connection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1) {

                    char ch = (char) data;

                    result += ch;

                    data = reader.read();
                }

                return result;

            } catch (Exception e) {

                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObj = new JSONObject(s);
                String weatherInfo = jsonObj.getString("weather");
                Log.i("weather content", weatherInfo);
                JSONArray arr = new JSONArray(weatherInfo);

                for(int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Log.i("main", obj.getString("main"));
                    Log.i("description", obj.getString("description"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
