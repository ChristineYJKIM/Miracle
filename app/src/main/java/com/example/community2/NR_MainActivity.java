package com.example.community2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

public class NR_MainActivity extends AppCompatActivity {

    private static final int SPLASH = 3300;

    ImageView opening;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.intro_splash);





        opening = findViewById(R.id.opening);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(NR_MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH);
    }
}