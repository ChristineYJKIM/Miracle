package com.example.community2;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class HomeActivity2 extends AppCompatActivity {



    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private FloatingActionButton getout;


    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String onlineUserID;

    private ProgressDialog loader;
    private FloatingActionButton calendarBtn;


    private EditText datestart;
    private EditText dateclose;



    private String key = "";
    private String task;
    private String description;
    private String date1;
    private String date2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        recyclerView = findViewById(R.id.recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loader = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID);

        floatingActionButton = findViewById(R.id.fab);
        getout = findViewById(R.id.getoutBtn);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add();
            }

        });

        getout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity2.this, NavigationBarMainActivity.class);
                startActivity(intent);
                finishAffinity();
            }

        });

    }


    private void add() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View myView = inflater.inflate(R.layout.input_file, null);

        myDialog.setView(myView);
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
                String Format = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.KOREA);

                datestart.setText(sdf.format(calendar.getTime()));
                date1 = sdf.format(calendar.getTime());
            }
        };

        datestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(HomeActivity2.this, datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
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
                String Format = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.KOREA);

                dateclose.setText(sdf.format(calendar2.getTime()));
                date2 = sdf.format(calendar2.getTime());
            }
        };

        dateclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(HomeActivity2.this, datePicker2, calendar2.get(Calendar.YEAR), calendar2.get(Calendar.MONTH),
                        calendar2.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


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

            String id = reference.push().getKey();
            String date = DateFormat.getDateInstance().format(new Date());



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
                            Toast.makeText(HomeActivity2.this, "루틴이 추가되었습니다", Toast.LENGTH_SHORT).show();
                            loader.dismiss();
                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(HomeActivity2.this, "저장실패", Toast.LENGTH_SHORT).show();
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

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieved_layoute, parent, false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public void showCalendar(View view) {
    }


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
        EditText datestart2 = view.findViewById(R.id.dateStart2);
        EditText dateclose2 = view.findViewById(R.id.dateClose2);

        mTask.setText(task);
        mTask.setSelection(task.length());

        mDescription.setText(description);
        mDescription.setSelection(description.length());

        datestart2.setText(date1);
        dateclose2.setText(date2);



        Calendar mcalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
                mcalendar.set(Calendar.YEAR, year);
                mcalendar.set(Calendar.MONTH, month);
                mcalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateCalendar();
            }
            private void updateCalendar(){
                String Format = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.KOREA);

                datestart2.setText(sdf.format(mcalendar.getTime()));
                date1 = sdf.format(mcalendar.getTime());
            }
        };

        datestart2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(HomeActivity2.this, datePicker, mcalendar.get(Calendar.YEAR), mcalendar.get(Calendar.MONTH),
                        mcalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        Calendar mcalendar2 = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener datePicker2 = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
                mcalendar2.set(Calendar.YEAR, year);
                mcalendar2.set(Calendar.MONTH, month);
                mcalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateCalendar();
            }
            private void updateCalendar(){
                String Format = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.KOREA);

                dateclose2.setText(sdf.format(mcalendar2.getTime()));
                date2 = sdf.format(mcalendar2.getTime());
            }
        };


        dateclose2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(HomeActivity2.this, datePicker2, mcalendar2.get(Calendar.YEAR), mcalendar2.get(Calendar.MONTH),
                        mcalendar2.get(Calendar.DAY_OF_MONTH)).show();
            }
        });




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
                            Toast.makeText(HomeActivity2.this, "업데이트 성공", Toast.LENGTH_SHORT).show();
                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(HomeActivity2.this, "업데이트에 실패했습니다", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

                dialog.dismiss();
            }
        });



        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(HomeActivity2.this, "루틴 삭제 성공", Toast.LENGTH_SHORT).show();
                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(HomeActivity2.this, "루틴 삭제 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.dismiss();
            }
        });

        dialog.show();

    }



//
//
//    @Override
//    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
//
//        getMenuInflater().(R.menu.main_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//
//
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//
//
//            case R.id.logout :
//                mAuth.signOut();
//                Intent intent = new Intent(HomeActivity2.this, LoginActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                finish();
//
//
//        }
//        return super.onOptionsItemSelected(item);
//    }
}







