package com.example.weatherapp_java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


public class MainActivity extends AppCompatActivity {
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}