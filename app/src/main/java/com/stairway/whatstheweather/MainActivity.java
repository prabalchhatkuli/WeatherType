package com.stairway.whatstheweather;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
//嗨，你好吗？
    EditText cityName;
    TextView resultTextView;
//api key = 3b3692e06266e1668ecd87b56c5bd18f
    //backup:   ef444bae7edcdfae521a301f1e8a6ab6
    public void findWeather(View view) {
        Log.i("cityName", cityName.getText().toString());

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);
        try
        {
            String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");

            DownloadTask task = new DownloadTask();
            //  task.execute("https://samples.openweathermap.org/data/2.5/weather?q=London,uk&appid=b6907d289e10d714a6e88b30761fae22");
            //task.execute("https://api.openweathermap.org/data/2.5/weather?q=London&APPID=3b3692e06266e1668ecd87b56c5bd18f");
            task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&APPID=3b3692e06266e1668ecd87b56c5bd18f");
        }
        catch (UnsupportedEncodingException e)
        {
            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        cityName = (EditText) findViewById((R.id.cityName));
        resultTextView  =(TextView) findViewById(R.id.resultTextView);

    }

    public class DownloadTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data =  reader.read();

                while(data!= -1)
                {
                    char current = (char) data;
                    result += current;

                    data = reader.read();
                }
                return result;
            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
            }

            return null;
        }

        //called when background method is completed
        //doInBackground method cannot interact with UI at all
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                String message = "";
                //this can fail if the result string is empty or this is a malformed json
                JSONObject jsonObject =  new JSONObject(result);//creates json object from a string(result)
                String weatherInfo = jsonObject.getString("weather");
                Log.i("weather content", weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);
                for(int i=0; i<arr.length(); i++)
                {
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = "";
                    String description = "";

                    main =  jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if(main!= "" &description!="")
                    {
                        message += main + ": " + description + "\r\n";
                    }
                }

                if(message!="")
                {
                    resultTextView.setText(message);
                }

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
            }


            Log.i("Website content", result);

        }
    }
}
