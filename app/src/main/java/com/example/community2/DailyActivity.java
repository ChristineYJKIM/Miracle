package com.example.community2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.example.community2.model.DayModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
    public CheckBox task1, task2, task3;
    public String clickMonth, day;
    DayModel dayModel = new DayModel();

    private long presstime = 0;
    private final long finishtimeed = 1000;
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        TextView textView = findViewById(R.id.textview);
        todo1 = findViewById(R.id.dailyActivity_todo_edittext1);
        todo2 = findViewById(R.id.dailyActivity_todo_edittext2);
        todo3 = findViewById(R.id.dailyActivity_todo_edittext3);
        diary = findViewById(R.id.dailyActivity_diary_edittext);
        task1 = findViewById(R.id.dailyActivity_checkbox_task1);
        task2 = findViewById(R.id.dailyActivity_checkbox_task2);
        task3 = findViewById(R.id.dailyActivity_checkbox_task3);

        task1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(task1.isChecked()) {
                    dayModel.task1 = 1;
                } else {
                    dayModel.task1 = 0;
                }
            }
        });

        task2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(task2.isChecked()) {
                    dayModel.task2 = 1;
                } else {
                    dayModel.task2 = 0;
                }
            }
        });

        task3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(task3.isChecked()) {
                    dayModel.task3 = 1;
                } else {
                    dayModel.task3 = 0;
                }
            }
        });

        clickMonth= getIntent().getStringExtra("clickMonth");
        day = getIntent().getStringExtra("day");

        FirebaseDatabase.getInstance().getReference().child("daily").child(uid).child(day + " " + clickMonth).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DayModel dayModel = snapshot.getValue(DayModel.class);
                if(dayModel != null) {
                    todo1.setText(dayModel.todo1);
                    todo2.setText(dayModel.todo2);
                    todo3.setText(dayModel.todo3);
                    diary.setText(dayModel.diary);
                    if(dayModel.task1 == 1) {
                        task1.setChecked(true);
                    } else {
                        task1.setChecked(false);
                    }
                    if(dayModel.task2 == 1) {
                        task2.setChecked(true);
                    } else {
                        task2.setChecked(false);
                    }
                    if(dayModel.task3 == 1) {
                        task3.setChecked(true);
                    } else {
                        task3.setChecked(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        textView.setText(day + " / " + clickMonth);
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
            dayModel.todo1 = todo1.getText().toString();
            dayModel.todo2 = todo2.getText().toString();
            dayModel.todo3 = todo3.getText().toString();
            dayModel.diary = diary.getText().toString();
            FirebaseDatabase.getInstance().getReference().child("daily").child(uid).child(day + " " + clickMonth).setValue(dayModel);
        }
    }
}