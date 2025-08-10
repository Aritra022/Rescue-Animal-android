package com.example.rescue_pets;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rescue_pets.R;
import com.example.rescue_pets.user.User_login_activity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, ChooseActivity.class);
                startActivity(i);
                finish(); // close splash so user can't go back to it
            }
        }, SPLASH_TIME_OUT);
    }
}
