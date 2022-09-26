package com.example.community2;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class RealCalendarActivity extends AppCompatActivity {

    public CalendarView calendarView;
    private RecyclerView recyclerView2;

    private CheckBox chk_select_all;
    private Button btn_delete_all;

    private ArrayList<Model> item_list = new ArrayList<>();
    //private ModelAdapter mAdapter;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_calendar);
        TextView task = findViewById(R.id.routineTextView);
        TextView routine = findViewById(R.id.txt_Name);
        CheckBox routineBtn = findViewById(R.id.chk_selected);



        recyclerView2 = findViewById(R.id.recyclerview2);




        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(linearLayoutManager);


        //캘린더에 띄우기
        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.KOREA);
                SimpleDateFormat sdf2 = new SimpleDateFormat("MM", Locale.KOREA);
                SimpleDateFormat sdf3 = new SimpleDateFormat("dd", Locale.KOREA);



                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

                //10월보다 작으면 한 자리 -> 앞에 0을 붙여준다
                String monthS;
                month = month+1;
                if(month < 10) {
                    monthS = "0" + String.valueOf(month);
                }else {
                    monthS = String.valueOf(month);
                }


                String dayS;
                if(day < 10) {
                    dayS = "0" + String.valueOf(day);
                }else {
                    dayS = String.valueOf(day);
                }

                String date = new StringBuilder().append(year).append("/")
                        .append(monthS).append("/")
                        .append(dayS).toString();
                LocalDate selected = LocalDate.parse(date, formatter);
                Log.d("selected", selected.toString());
                Log.d("year", String.valueOf(year));




                final Model[] model = {new Model()};
                String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference().child("tasks").child(Uid)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                               // Log.d("넌 뭐니", String.valueOf(datasnapshot.getValue()));

                                String check = ""; //체크값 초기화

                                for (DataSnapshot item : datasnapshot.getChildren()) {

                                    Model model = item.getValue(Model.class);
                                    Log.d("모델", item.getValue(Model.class).getDate1());
                                    LocalDate localDate1 = LocalDate.parse(model.getDate1(), formatter);
                                    LocalDate localDate2 = LocalDate.parse(model.getDate2(), formatter);

                                    //LocalDate selectedDay = LocalDate.parse(LocalDate(selected, formatter);

                                    if (selected.compareTo(localDate1) < 0 || selected.compareTo(localDate2) > 0) {
                                    } else {
                                        Log.d("선택날짜할일", item.toString());

                                        //check.add(getTask);
                                        check = check + item.getValue(Model.class).getTask();
                                        Log.d("check", check.toString());

                                        //체크박스뷰가 떠야한다 -> 리사이클러뷰 필요

                                    }
                                }


                                //recyclerView.adapter.submitlist(check);
                                //list adapter를 사용해서 구현해야할 것이다.

                                 task.setText(check);

                                //recyclerview 로 바꿀라면, check를 하려면 필요할 덧
                                //리스트 -> 스트링타입?
                                // 체크변수 타입을 List<String> 해야함 (122번줄을)



                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }

                        });

            }
        });


    }


//    public class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.ViewHolder> {
//
//        public ArrayList<Model>
//                item_list;
//
//
//        public ModelAdapter(ArrayList<Model> arrayList) {
//
//            item_list = arrayList;
//        }
//
//        @Override
//        public ModelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
//
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_retrive, parent, false);
//            return new ViewHolder(view);
//        }
//
//        public void onBindViewHolder(ModelAdapter.ViewHolder holder, int position){
//            final int pos = position;
//
//            holder.item_
//        }
//
//        public static class ViewHolder extends RecyclerView.ViewHolder{
//            public TextView routine;
//            public CheckBox routineBtn;
//
//            public RecyclerView.ViewHolder(View itemLayoutView){
//
//                super(itemLayoutView);
//
//                TextView routine = findViewById(R.id.txt_Name);
//                CheckBox routineBtn = findViewById(R.id.chk_selected);
//
//            }
//        }
//    }


}
