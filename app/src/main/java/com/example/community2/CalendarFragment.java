package com.example.community2;

import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.community2.model.DayModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener {
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private String todayD;
    private Button previousMonthBtn, nextMonthBtn;
    private CalendarAdapter calendarAdapter;
    private LinearLayout linearLayout;
    private EditText task1, task2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarRecyclerView = v.findViewById(R.id.calendarRecyclerView);
        monthYearText = v.findViewById(R.id.monthYearTV);
        previousMonthBtn = v.findViewById(R.id.previousMonthBtn);
        nextMonthBtn = v.findViewById(R.id.nextMonthBtn);
        linearLayout = v.findViewById(R.id.calendarFragment_linearLayout);
        task1 = v.findViewById(R.id.calendarFragment_habit_edittext1);
        task2 = v.findViewById(R.id.calendarFragment_habit_edittext2);

        previousMonthBtn.setOnClickListener(view -> previousMonthAction());
        nextMonthBtn.setOnClickListener(view -> nextMonthAction());


        selectedDate = LocalDate.now();
        LocalDate now = LocalDate.now();
        setMonthView();
        return v;
    }

    private void setMonthView() {
        todayD = todayDate(selectedDate);
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        calendarAdapter = new CalendarAdapter(linearLayout, daysInMonth, todayD, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
/*        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);*/
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("");
            } else {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return daysInMonthArray;
    }

    private String todayDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd");
        return date.format(formatter);
    }

    public String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    public void previousMonthAction() {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction() {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position, String dayText) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        int year = selectedDate.getYear();
        int month = selectedDate.getMonthValue();

        String monthS = String.valueOf(month);
        String dayS;
        if(Integer.parseInt(dayText) < 10) {
            dayS = "0" + dayText;
        }else {
            dayS = dayText;
        }

        String date = new StringBuilder().append(year).append("-")
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
                        task1.setText(check);

                        //recyclerview 로 바꿀라면, check를 하려면 필요할 덧
                        //리스트 -> 스트링타입?
                        // 체크변수 타입을 List<String> 해야함 (122번줄을)



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }

                });
        if (!dayText.equals("")) {
            Intent intent = new Intent(getActivity(), DailyActivity.class);
            intent.putExtra("day", dayText);
            intent.putExtra("clickMonth", monthYearFromDate(selectedDate) );
            startActivity(intent);
        }
    }
}
