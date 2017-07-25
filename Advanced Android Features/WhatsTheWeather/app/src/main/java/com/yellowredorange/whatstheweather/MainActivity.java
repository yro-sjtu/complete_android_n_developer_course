package com.yellowredorange.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText inputCity;
    TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputCity = (EditText) findViewById(R.id.editText);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
    }

    public void findWeather(View view) {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(inputCity.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

        WeatherFinder finder = new WeatherFinder();
        String encodedCityName = null;
        try {
            encodedCityName = URLEncoder.encode(inputCity.getText().toString(), "UTF-8");
            finder.execute(encodedCityName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Couldn't find weather...", Toast.LENGTH_LONG).show();
        }

    }

    public class WeatherFinder extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... cities) {
            String city = null;
            URL url = null;
            HttpURLConnection connection = null;
            StringBuilder result = new StringBuilder();

            try {

                city = cities[0];
                url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&APPID=94ef3a850a02c5df1a4eba4ccf983abe");
                Log.i("URL", url.toString());
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1) {
                    char ch = (char) data;
                    result.append(ch);
                    data = reader.read();
                }

                return result.toString();

            } catch(Exception e) {

                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Couldn't find weather...", Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObj = new JSONObject(s);
                String weatherInfo = jsonObj.getString("weather");
                JSONArray arr = new JSONArray(weatherInfo);

                String message = "";

                for(int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);

                    String main = null, description = null;
                    main = obj.getString("main");
                    description = obj.getString("description");
                    if(main != null  && description != null) {
                        message += main + ": " + description + "\n";
                    }
                }

                Log.i("weather info", message);

                if(!message.equals("")) {
                    resultTextView.setText(message);
                } else {
                    Toast.makeText(getApplicationContext(), "Couldn't find weather...", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Couldn't find weather...", Toast.LENGTH_LONG).show();
            }

        }
    }

}
