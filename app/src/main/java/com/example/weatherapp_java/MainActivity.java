package com.example.weatherapp_java;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalTime;


public class MainActivity extends Activity {
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/"; //setting base url
    CardView refreshCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);

        refreshCardView = findViewById(R.id.refreshCardView);

        new WeatherTask(this, "Balti").execute();

        refreshCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new WeatherTask(MainActivity.this, "Balti").execute();
            }
        });
    }
    @SuppressLint("StaticFieldLeak")
    private static class WeatherTask extends AsyncTask<String, Void, String> {
        ProgressBar progressBar;
        LinearLayout mainWindow;
        CardView refreshCardView;
        TextView errorText;
        TextView cityView;
        TextView windSpeedView;
        TextView minTemperatureView;
        TextView weatherValueView;
        TextView maxTemperatureView;
        TextView weatherStatusView;
        TextView updatedAtView;
        MainActivity currentActivity;
        private String CITY;
        private WeakReference<MainActivity> activityReference;

        WeatherTask(MainActivity context, String city) {
            this.CITY = city;
            this.activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            currentActivity = activityReference.get();
            if (currentActivity == null || currentActivity.isFinishing()) return;

            mainWindow = currentActivity.findViewById(R.id.mainWindow);
            progressBar = currentActivity.findViewById(R.id.loaderView);
            errorText = currentActivity.findViewById(R.id.errorText);
            mainWindow.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            errorText.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder content = new StringBuilder();
            try {
                URL url = new URL(
                        BASE_URL +
                                "weather?q=" +
                                this.CITY +
                                "&units=metric&appid=0dbb8c4b976379671419e66c70c93ded");

                URLConnection connection = url.openConnection();
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) content.append(line).append("\n");

            } catch (IOException e) {
                return e.toString();
            }
            return content.toString();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                final JSONObject jsonObj = new JSONObject(s);
                final JSONObject main = jsonObj.getJSONObject("main");
                final JSONObject sys = jsonObj.getJSONObject("sys");
                final JSONObject wind = jsonObj.getJSONObject("wind");
                final JSONObject weather = jsonObj.getJSONArray("weather")
                        .getJSONObject(0);
                final String updatedAtText = "Updated At:" +
                        LocalTime.now().getHour() + ":" + LocalTime.now().getMinute();
                final String temp = main.getString("temp") + "°C";
                final String tempMin = "Min Temp: " + main.getString("temp_min") + "°C";
                final String tempMax = "Max Temp: " + main.getString("temp_max") + "°C";
                final String windSpeed = wind.getString("speed");
                final String weatherDescription = weather.getString("description");
                final String address = jsonObj.getString("name") + ", " +
                        sys.getString("country");

                windSpeedView = currentActivity.findViewById(R.id.windSpeedView);
                minTemperatureView = currentActivity.findViewById(R.id.minTemperatureView);
                maxTemperatureView = currentActivity.findViewById(R.id.maxTemperatureView);
                weatherStatusView = currentActivity.findViewById(R.id.weatherStatus);
                updatedAtView = currentActivity.findViewById(R.id.updatedView);
                cityView = currentActivity.findViewById(R.id.cityView);
                weatherValueView = currentActivity.findViewById(R.id.weatherValueView);
                refreshCardView = currentActivity.findViewById(R.id.refreshCardView);

                cityView.setText(address);
                windSpeedView.setText(windSpeed);
                minTemperatureView.setText(tempMin);
                weatherValueView.setText(temp);
                maxTemperatureView.setText(tempMax);
                weatherStatusView.setText(weatherDescription);
                updatedAtView.setText(updatedAtText);
                mainWindow.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } catch (Exception e) {
                progressBar.setVisibility(View.VISIBLE);
                errorText.setVisibility(View.VISIBLE);
            }
        }
    }
}