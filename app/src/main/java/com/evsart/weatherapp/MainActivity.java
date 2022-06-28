package com.evsart.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText input;
    private Button MainButton;
    private TextView ResultOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = findViewById(R.id.input);
        MainButton = findViewById(R.id.MainButton);
        ResultOutput = findViewById(R.id.ResultOutput);

        MainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, R.string.inputYourCity, Toast.LENGTH_LONG).show();
                } else {
                    String city = input.getText().toString();
                    String key = "943b7d93d1e35187f63502d4af0ec283";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city +
                            "&appid=" + key + "&units=metric&lang=" + Locale.getDefault().getLanguage();
                    new GetURLData().execute(url);
                }
            }
        });
    }

    private class GetURLData extends AsyncTask<String, String, String> {

        protected void onPreExecute(){
            super.onPreExecute();
            ResultOutput.setText(R.string.Loading);
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection= (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();
                String line = "";

                while((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null)
                    connection.disconnect();
                try{
                    if(reader != null)
                        reader.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try {
                JSONObject JSONobj = new JSONObject(result);

                String deskription = JSONobj.getJSONArray("weather").getJSONObject(0).getString("description");
                    deskription = deskription.substring(0,1).toUpperCase() + deskription.substring(1);
                double temp = JSONobj.getJSONObject("main").getDouble("temp");
                double feels_like = JSONobj.getJSONObject("main").getDouble("feels_like");
                double speed = JSONobj.getJSONObject("wind").getDouble("speed");

                ResultOutput.setText(getString(R.string.temp) + temp + "\n" +
                                    getString(R.string.feels_like) + feels_like + "\n" +
                                    getString(R.string.speed) + speed + getString(R.string.MetersPerSecond) + '\n' +
                                    deskription);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
