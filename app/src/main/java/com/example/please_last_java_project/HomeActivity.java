package com.example.please_last_java_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;


public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;


    //여기서부턴 9강
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;

    private ProgressDialog loader;

    private TextView listBtn;
    private EditText datestart;
    private EditText dateclose;


    //14강..이걸 왜 선언한걸까?
    private String key = "";
    private String task;
    private String description;
    private String date1;
    private String date2;



    //앱을 나가더라도 데이터 값이 저장될 수 있게끔 해주는 함수입니다~
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //리사이클러뷰를 띄워줍니다
        recyclerView = findViewById(R.id.recyclerview);

        //새로운 리니어 레이아웃 메니저 객체를 선언한다 = 수직, 수평 스크롤 리스트를 의미함!
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //역순으로 리스트 출력하기? 왜 이걸 쓰는건데?
        //일단 오케이..
        linearLayoutManager.setReverseLayout(true);
        //제일 끝 아이템 밑에 내용이 추가되게끔 해준다
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loader = new ProgressDialog(this);

        listBtn = findViewById(R.id.list_todo);

        //여기가 문제네...문제해결했어!
        mAuth = FirebaseAuth.getInstance(); //이 친구를 잊지마!! 데이터 연동시켜주는 아이야~중요함!
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID);

        //버튼 누르면 액션 실행할 수 있게끔 코딩
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add();
            }

        });



    }


    //데이터를 추가하는 거구나. 버튼을 클릭했어.
    private void add() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View myView = inflater.inflate(R.layout.input_file, null);

        //다이얼로그 위젯 보이게끔 하기
        myDialog.setView(myView);

        //다이얼로그 이외의 창 만져도 괜찮게 설정
        AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        datestart = myView.findViewById(R.id.dateStart);
        dateclose = myView.findViewById(R.id.dateClose);


        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateCalendar();
            }
            private void updateCalendar(){
                String Format = "yyyy/MM/dd";
                SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.KOREA);

                datestart.setText(sdf.format(calendar.getTime()));
                date1 = sdf.format(calendar.getTime());
            }
        };

        datestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(HomeActivity.this, datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Calendar calendar2 = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener datePicker2 = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
                calendar2.set(Calendar.YEAR, year);
                calendar2.set(Calendar.MONTH, month);
                calendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateCalendar();
            }
            private void updateCalendar(){
                String Format = "yyyy/MM/dd";
                SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.KOREA);

                dateclose.setText(sdf.format(calendar2.getTime()));
                date2 = sdf.format(calendar2.getTime());
            }
        };

        dateclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(HomeActivity.this, datePicker2, calendar2.get(Calendar.YEAR), calendar2.get(Calendar.MONTH),
                        calendar2.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        //가망이 없어 지금..
        final EditText task = myView.findViewById(R.id.task);
        final EditText description = myView.findViewById(R.id.description);

        Button save = myView.findViewById(R.id.saveBtn);
        Button cancel = myView.findViewById(R.id.cancelBtn);

        cancel.setOnClickListener((view -> {
            dialog.dismiss();
        }));

        save.setOnClickListener((view -> {
            String mTask = task.getText().toString().trim();
            String mDescription = description.getText().toString().trim();


            //이걸 추가해야 밑에 에러가 사라짐
            //firebase에 데이터 입력시킬라고!
            String id = reference.push().getKey();
            String date = DateFormat.getDateInstance().format(new Date());


            //이게 무슨 의미인지 확인해보자.


            if (TextUtils.isEmpty(mTask)) {
                task.setError("루틴을 적어주세요");
                return;
            }

            if (TextUtils.isEmpty(mDescription)) {
                description.setError("설명도 적어주세요");
                return;
            } else {
                loader.setMessage("데이터를 추가합니다~");
                loader.setCanceledOnTouchOutside(false);
                loader.show();

                Model model = new Model(mTask, mDescription, id, date, date1, date2);
                reference.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(HomeActivity.this, "루틴이 추가되었습니다", Toast.LENGTH_SHORT).show();
                            loader.dismiss();
                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(HomeActivity.this, "저장실패", Toast.LENGTH_SHORT).show();
                            loader.dismiss();
                        }
                    }
                });
            }
            dialog.dismiss();
        }));
        dialog.show();

    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Toast.makeText(getApplicationContext(), year + "년" + monthOfYear + "월" + dayOfMonth +"일", Toast.LENGTH_SHORT).show();
        }
    };




    //여기서 조심해야한다.생성자 작성할 때 에러가 뜨면 그 이유 원인 파악할 것
    //그리고 이게 왜 필요한지 생각하면서 코드치길 바람 :)

    //12강..firebase 실시간 데이터베이스 넣는 거 진행했어
    //파이어베이스 리사이클러뷰는 options(쿼리문)으로 DB에서 값을 자동으로 불러와준다
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(reference, Model.class)
                .build();

        FirebaseRecyclerAdapter<Model, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Model, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Model model) {
                holder.setDate(model.getDate());
                holder.setTask(model.getTask());
                holder.setDesc(model.getDescription());
            //    holder.date1(model.getDate());


                //휴...14강이다..!! 한 번 해보자구!!
                //position을 적어버리면 위에 INT position에서 에러가 떠버리는 걸?
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        key = getRef(position).getKey();
                        task = model.getTask();
                        description = model.getDescription();
                        date1 = model.getDate1();
                        date2 = model.getDate2();

                        updateTask();


                    }
                });

            }

            //데이터피커 - 날짜 가져오기 - 파이어베이스에 데이터 날짜별로 넣기
            //캘린더에 어떻게 띄우지?

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieved_layout, parent, false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    //리사이클러뷰가 나오게끔 클래스와 객체 생성하고 세팅함...
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTask(String task) {
            TextView taskTextView = mView.findViewById(R.id.taskTv);
            taskTextView.setText(task);
        }

        public void setDesc(String desc) {
            TextView descTextView = mView.findViewById(R.id.descriptionTv);
            descTextView.setText(desc);
        }

        public void setDate(String date) {
            TextView dateTextView = mView.findViewById(R.id.dateTv);

        }

        public void setDate2(String date2){
            //여기에 캘린더 데이터값을 저장시킨다.

        }
    }


    //할 일 업데이트하기
    private void updateTask() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.update_date, null);
        myDialog.setView(view);


        AlertDialog dialog = myDialog.create();

        EditText mTask = view.findViewById(R.id.mEditTextTask);
        EditText mDescription = view.findViewById(R.id.mEditTextDescription);

        mTask.setText(task);
        mTask.setSelection(task.length());

        mDescription.setText(description);
        mDescription.setSelection(description.length());

        Button delButton = view.findViewById(R.id.btnDelete);
        Button updateButton = view.findViewById(R.id.btnUpdate);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task = mTask.getText().toString().trim();
                description = mDescription.getText().toString().trim();

                String date = DateFormat.getDateInstance().format(new Date());


                Model model = new Model(task, description, key, date, date1, date2);

                reference.child(key).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(HomeActivity.this, "Date has been update successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(HomeActivity.this, "update failed", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

                dialog.dismiss();
            }
        });

        //14강...삭제버튼 누르면 액션 취하게끔 작성해야함!
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(HomeActivity.this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(HomeActivity.this, "Failed to delete task", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.dismiss();
            }
        });

        dialog.show();

    }

    //15강~~ 로그아웃 메뉴를 만들구에영!
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {


          /*  case R.id.go_calendar:
                Intent intent = new Intent(HomeActivity.this, CalendarActivity.class);
                startActivity(intent); */


            case R.id.logout :
                mAuth.signOut();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();


        }
        return super.onOptionsItemSelected(item);
    }
}







