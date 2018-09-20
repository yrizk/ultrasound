package com.example.yrizk.ultrasound;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UltrasoundSurfaceView mainView = new UltrasoundSurfaceView(this);
        setContentView(mainView);
    }
}