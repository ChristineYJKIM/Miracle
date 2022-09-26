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



        //recyclerView2 = findViewById(R.id.recyclerview2);




        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setReverseLayout(true);
        //linearLayoutManager.setStackFromEnd(true);
        //recyclerView2.setHasFixedSize(true);
        //recyclerView2.setLayoutManager(linearLayoutManager);


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
