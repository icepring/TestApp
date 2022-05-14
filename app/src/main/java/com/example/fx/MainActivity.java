package com.example.fx;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.fx.button.CFDSpeedButton;
import com.example.fx.button.CFDSpeedShadowButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CFDSpeedButton sellSpeedButton=findViewById(R.id.speed_sell);
        CFDSpeedShadowButton buySpeedButton=findViewById(R.id.speed_buy);
        sellSpeedButton.setRate(2728.400d);
        buySpeedButton.setRate(9527.431d);
    }
}