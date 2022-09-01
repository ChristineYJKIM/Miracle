package com.example.please_last_java_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CalendarActivity extends AppCompatActivity {

    private RecyclerView recyclerView;



    //파이어베이스에서 데이터를 갖고와야 하니까
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserId;

    private String key = "";
    private String task;

    private TextView listCalendar;



    //이렇게 코드 짜면 안되나봐..!
    //1. firebase 에서 data 불러오는 거  특히 task 어떻게 가져올지 생각 -> 코드짜기
    //2. intent로 화면 전환 어떻게 할건지 생각해보기!!!
    // 난 그래도 코딩이 재밌다,,ㅎㅎㅎㅎ
    //생각할 수 있잖아. 계속해서. 내 천직이야. ㅎㅎ
    //더 기발하고 재밌는 생각 많이 해야징!! 신난다~
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);



        //calendar_task를 리사이클러뷰에 띄워서 실행시켜야해
        //여기에 파이어베이스 데이터값을 띄우고싶어
        recyclerView = findViewById(R.id.calendar_recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserId = mUser.getUid();
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserId);
        listCalendar = findViewById(R.id.go_to_calendar);

    }


    //단추를 잘못 끼웠다..
    //캘린더 구현하고 거기 날짜마다 데이터를 넣는 방법을 나는 알지 못한다
    //그리고 그거에 맞춰서 테스크를 만들어야 하는데...
    //테스크를 먼저 만들어버려서 일이 꼬였다
    //그럼 엎어야지
    //그렇다면 지금 내가 노력하는 파트는, 파이어베이스에서 어떻게 데이터값을 가져오느냐!
    //여기에 집중해야겠구나.


    //데이터를 어떻게 가져올까
    private void dataShow(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.todo_retrive, null);
        myDialog.setView(myView);

        AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        CheckBox check = myView.findViewById(R.id.todoCheckBox);



        check.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>()
                        .setQuery(reference, Model.class)
                        .build();

                FirebaseRecyclerAdapter<Model, HomeActivity.MyViewHolder> adapter = new FirebaseRecyclerAdapter<Model, HomeActivity.MyViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull HomeActivity.MyViewHolder holder, int position, @NonNull Model model) {
                        holder.setDate(model.getDate());
                        holder.setTask(model.getTask());
                        holder.setDesc(model.getDescription());
                    }

                    @NonNull
                    @Override
                    public HomeActivity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        return null;
                    }
                };

                listCalendar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CalendarActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });

            }
        });


    }


}