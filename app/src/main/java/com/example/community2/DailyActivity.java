package com.example.community2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.example.community2.model.DayModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DailyActivity extends AppCompatActivity {
    public EditText todo1, todo2, todo3, diary;
    public String clickMonth, day;

    private long presstime = 0;
    private final long finishtimeed = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        TextView textView = findViewById(R.id.textview);
        todo1 = findViewById(R.id.dailyActivity_todo_edittext1);
        todo2 = findViewById(R.id.dailyActivity_todo_edittext2);
        todo3 = findViewById(R.id.dailyActivity_todo_edittext3);
        diary = findViewById(R.id.dailyActivity_diary_edittext);

        clickMonth= getIntent().getStringExtra("clickMonth");
        day = getIntent().getStringExtra("day");

        FirebaseDatabase.getInstance().getReference().child("daily").child(day + " " + clickMonth).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DayModel dayModel = snapshot.getValue(DayModel.class);
                todo1.setText(dayModel.todo1);
                todo2.setText(dayModel.todo2);
                todo3.setText(dayModel.todo3);
                diary.setText(dayModel.diary);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        textView.setText(day + " . " + clickMonth);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - presstime;

        if(0 <= intervalTime && finishtimeed >= intervalTime) {
            finish();
        } else {
            presstime = tempTime;
            DayModel dayModel = new DayModel();
            dayModel.todo1 = todo1.getText().toString();
            dayModel.todo2 = todo2.getText().toString();
            dayModel.todo3 = todo3.getText().toString();
            dayModel.diary = diary.getText().toString();
            FirebaseDatabase.getInstance().getReference().child("daily").child(day + " " + clickMonth).setValue(dayModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(DailyActivity.this, "작성내용이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}