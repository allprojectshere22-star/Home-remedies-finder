package com.example.remedy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {

            SharedPreferences userSp = getSharedPreferences("UserSession", MODE_PRIVATE);
            SharedPreferences adminSp = getSharedPreferences("AdminSession", MODE_PRIVATE);

            Intent intent;
            if (userSp.getBoolean("isLoggedIn", false)) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else if (adminSp.getBoolean("isAdminLoggedIn", false)) {
                intent = new Intent(SplashActivity.this, AdminPanelActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, WelcomeActivity.class);
            }
            startActivity(intent);
            finish();

        }, 2000);

    }
}