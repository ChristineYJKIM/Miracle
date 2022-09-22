package com.example.community2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.example.community2.model.dayModel;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;

public class DailyActivity extends AppCompatActivity {
    public EditText todo1, todo2, todo3, diary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        TextView textView = findViewById(R.id.textview);
        todo1 = findViewById(R.id.dailyActivity_todo_edittext1);
        todo2 = findViewById(R.id.dailyActivity_todo_edittext2);
        todo3 = findViewById(R.id.dailyActivity_todo_edittext3);
        diary = findViewById(R.id.dailyActivity_diary_edittext);

        String clickMonth= getIntent().getStringExtra("clickMonth");
        String day = getIntent().getStringExtra("day");
        String task1 = getIntent().getStringExtra("todo1");
        String task2 = getIntent().getStringExtra("todo2");
        String task3 = getIntent().getStringExtra("todo3");
        String daily = getIntent().getStringExtra("diary");

        textView.setText(day + " . " + clickMonth);
        if(task1 != null) {
            todo1.setText(task1);
        }
        if(task2 != null) {
            todo2.setText(task2);
        }
        if(task3 != null) {
            todo3.setText(task3);
        }
        if(daily != null) {
            diary.setText(daily);
        }

        dayModel dayModel = new dayModel();
        dayModel.todo1 = todo1.getText().toString();
        dayModel.todo2 = todo2.getText().toString();
        dayModel.todo3 = todo3.getText().toString();
        dayModel.diary = diary.getText().toString();
        FirebaseDatabase.getInstance().getReference().child("daily").push().setValue(dayModel);
    }
}