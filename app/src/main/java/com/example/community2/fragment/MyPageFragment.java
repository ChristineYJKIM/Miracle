package com.example.community2.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.community2.LoginActivity;
import com.example.community2.Model;
import com.example.community2.R;
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
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyPageFragment extends Fragment {


    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private FloatingActionButton logoutBtn;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_page, container, false);
       // return inflater.inflate(R.layout.fragment_my_page, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        super.onCreate(savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loader = new ProgressDialog(getActivity());

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(onlineUserID);

        floatingActionButton = view.findViewById(R.id.fab);
         logoutBtn = view.findViewById(R.id.logoutBtn);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                add();
            }

        });

        logoutBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(mAuth.getCurrentUser() !=null)
                    mAuth.signOut();
                Intent intent = new Intent(getActivity(),  LoginActivity.class);
                startActivity(intent);
            }
        });


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void add() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = LayoutInflater.from(requireContext());

        View myView = inflater.inflate(R.layout.input_file, null);

        myDialog.setView(myView);

        AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        datestart = myView.findViewById(R.id.dateStart);
        dateclose = myView.findViewById(R.id.dateClose);


        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateCalendar();
            }

            private void updateCalendar() {
                String Format = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.KOREA);

                datestart.setText(sdf.format(calendar.getTime()));
                date1 = sdf.format(calendar.getTime());
            }
        };

        datestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Calendar calendar2 = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener datePicker2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar2.set(Calendar.YEAR, year);
                calendar2.set(Calendar.MONTH, month);
                calendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateCalendar();
            }

            private void updateCalendar() {
                String Format = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.KOREA);

                dateclose.setText(sdf.format(calendar2.getTime()));
                date2 = sdf.format(calendar2.getTime());
            }
        };

        dateclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), datePicker2, calendar2.get(Calendar.YEAR), calendar2.get(Calendar.MONTH),
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


                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

//                for (LocalDate Date = LocalDate.parse(date1, formatter);
//                     Date.compareTo(LocalDate.parse(date2, formatter)) <= 0; Date = Date.plusDays(1)) {
//
//                    Log.d("날짜쓰나와라", String.valueOf(Date));
//                    String itemId;
//                    itemId = reference.push().getKey();
//
//
//                }


                // if (selectedDate.compareTo(localDate1) < 0 || selectedDate.compareTo(localDate2) > 0) {

                //두 개 날짜가 for 문을 돌고 - 각 날짜가 돌면서 체크박스가 각 날짜마다
                // 새롭게 갱신되어야하는데 그걸 내가 어떻게 해
                //for(LocalDate date = date1; date != date2; date.plusday(1))
                //
                //}

                Model model = new Model(mTask, mDescription, id, date, date1, date2);
                reference.child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "루틴이 추가되었습니다", Toast.LENGTH_SHORT).show();
                            loader.dismiss();
                            Log.d("루틴나와라", "나와라");


                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(getActivity(), "저장실패", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(requireContext(), year + "년" + monthOfYear + "월" + dayOfMonth + "일", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(reference, Model.class)
                .build();

        FirebaseRecyclerAdapter<Model, MyPageFragment.MyViewHolder> adapter = new FirebaseRecyclerAdapter<Model, MyPageFragment.MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyPageFragment.MyViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Model model) {
                holder.setDate(model.getDate());
                holder.setTask(model.getTask());
                holder.setDesc(model.getDescription());


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
            public MyPageFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieved_layoute, parent, false);
                return new MyPageFragment.MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public void showCalendar(View view) {
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

        public void setDate2(String date2) {

        }
    }


    //할 일 업데이트하기
    private void updateTask() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = LayoutInflater.from(requireContext());
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
        DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mcalendar.set(Calendar.YEAR, year);
                mcalendar.set(Calendar.MONTH, month);
                mcalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateCalendar();
            }

            private void updateCalendar() {
                String Format = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.KOREA);

                datestart2.setText(sdf.format(mcalendar.getTime()));
                date1 = sdf.format(mcalendar.getTime());
            }
        };

        datestart2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), datePicker, mcalendar.get(Calendar.YEAR), mcalendar.get(Calendar.MONTH),
                        mcalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        Calendar mcalendar2 = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener datePicker2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mcalendar2.set(Calendar.YEAR, year);
                mcalendar2.set(Calendar.MONTH, month);
                mcalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateCalendar();
            }

            private void updateCalendar() {
                String Format = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.KOREA);

                dateclose2.setText(sdf.format(mcalendar2.getTime()));
                date2 = sdf.format(mcalendar2.getTime());
            }
        };


        dateclose2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), datePicker2, mcalendar2.get(Calendar.YEAR), mcalendar2.get(Calendar.MONTH),
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
                //입력값을 하나로 뭉쳐서 Firebase에 넘긴다


                reference.child(key).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "업데이트 성공", Toast.LENGTH_SHORT).show();
                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(getActivity(), "업데이트에 실패했습니다", Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(getActivity(), "루틴 삭제 성공", Toast.LENGTH_SHORT).show();
                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(getActivity(), "루틴 삭제 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.dismiss();
            }
        });

        dialog.show();

    }

}


