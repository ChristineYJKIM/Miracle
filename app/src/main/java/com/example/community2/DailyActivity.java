package com.example.community2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.time.LocalDate;

public class DailyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        TextView textView = findViewById(R.id.textview);

        String clickMonth= getIntent().getStringExtra("clickMonth");
        String day = getIntent().getStringExtra("day");
        textView.setText(day + " . " + clickMonth);
    }
}