package com.example.please_last_java_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class calendarActivity extends AppCompatActivity {

    /*todo, 일기장 : 날짜 설정만 하면 된다? - 타임스탬프 기능 이용*/
    //자바 투두 클래스 만들기 + 일기 클래스 만들기!
    //

    private RecyclerView recyclerView;
    private CheckBox CheckBox;

    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        TextView text = findViewById(R.id.go_to_calendar);

        recyclerView = findViewById(R.id.calendar_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID);

        CheckBox = findViewById(R.id.todoCheckBox);
        FirebaseRecyclerOptions<MModel> options = new FirebaseRecyclerOptions.Builder<MModel>()
                .setQuery(reference, MModel.class)
                .build();

        FirebaseRecyclerAdapter<MModel, homeActivity.MyViewHolder> adapter = new FirebaseRecyclerAdapter<MModel, homeActivity.MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull homeActivity.MyViewHolder holder, int position, @NonNull MModel model) {
                //TODO 할 일만 갖고오기//
                holder.setTask(model.getTask());
            }

            @NonNull
            @Override
            public homeActivity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }
        };



    }

}

