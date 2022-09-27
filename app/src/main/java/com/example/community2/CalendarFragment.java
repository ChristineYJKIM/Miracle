package com.example.community2;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener {
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;
    private String todayD;
    private Button previousMonthBtn, nextMonthBtn;
    private CalendarAdapter calendarAdapter;
    private LinearLayout linearLayout;
    private EditText task1, task2;
    private CheckBox check1, check2;
    private HabitAdapter habitAdapter = new HabitAdapter(new HabitUtil());



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



        previousMonthBtn.setOnClickListener(view -> previousMonthAction());
        nextMonthBtn.setOnClickListener(view -> nextMonthAction());


        selectedDate = LocalDate.now();
        LocalDate now = LocalDate.now();
        setMonthView();
        return v;



        
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView habitRecyclerView = view.findViewById(R.id.HabitRecyclerView);
        // recyclerView의 리스트 형태를 세로 목록형으로 지정

        habitRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false));
        // recyclerView에 어댑터 설정
        habitRecyclerView.setAdapter(habitAdapter);


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
    public void getHabit() {
        Log.d("selectedDate", String.valueOf(selectedDate));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        int year = selectedDate.getYear();
        int month = selectedDate.getMonthValue();
        int day = selectedDate.getDayOfMonth();


        //날짜가 고정값이라서 다른 날짜 클릭이 안 됨.


        String monthS;
        month = month;
        if(month < 10) {
            monthS = "0" + String.valueOf(month);
        }else {
            monthS = String.valueOf(month);
        }


        String dayS;
        day = day;
        if(day < 10) {
            dayS = "0" + String.valueOf(day);
        }else {
            dayS = String.valueOf(day);
        }



        String date = new StringBuilder().append(year).append("-")
                .append(monthS).append("-")
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

                        Log.d("넌 뭐니", String.valueOf(datasnapshot.getValue()));

                        String check = ""; //체크값 초기화

                        ArrayList<String> calendar_habit = new ArrayList<>();

                        //todo ArrayList<Model> calendar_habit = new ArrayList<>();

                        for (DataSnapshot item : datasnapshot.getChildren()) {


                            Model model = item.getValue(Model.class);
                            Log.d("모델", item.getValue(Model.class).getDate1());
                            LocalDate localDate1 = LocalDate.parse(model.getDate1(), formatter);
                            LocalDate localDate2 = LocalDate.parse(model.getDate2(), formatter);

                            //LocalDate selectedDay = LocalDate.parse(LocalDate(selected, formatter);

                            if (selectedDate.compareTo(localDate1) < 0 || selectedDate.compareTo(localDate2) > 0) {
                            } else {
                                Log.d("선택날짜할일", item.toString());

                                //check.add(getTask);
                                //할 일들을 한 스트링으로 만든 거
                                //check = check + item.getValue(Model.class).getTask();
                                calendar_habit.add(item.getValue(Model.class).getTask());
                                //calendar_habit.add(item.getValue(Model.class) - model생성

                                //calendar_habit
                                //각각의 task 를 calendar_habbit에 그려짐 -> 어댑터에 담음 -> recyclerview에서 보여짐

                                Log.d("check", check.toString());

                                //할 일 안에 날짜가 하나 저장


                            }
                        }
//                        task1.setText(check);
//                        task2.setText(check);
                        Log.d("습관창", String.valueOf(calendar_habit));

                        habitAdapter.submitList(calendar_habit);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }

                });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position, String dayText) {

        if (!dayText.equals("")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-" + dayText);
            //selectedDate = monthYearFromDate(selectedDate);
            Log.d("'일'만 찍혀라", dayText);
            //LocalDate.parse(String.valueOf(selectedDate), formatter);
            selectedDate = LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(), Integer.parseInt(dayText));

            Log.d("날짜형식", String.valueOf(LocalDate.of(selectedDate.getYear(),
                    selectedDate.getMonth(), Integer.parseInt(dayText))));

            //Log.d("날짜형식", String.valueOf(LocalDate.parse(String.valueOf(selectedDate), formatter)));
            getHabit();



//            Intent intent = new Intent(getActivity(), DailyActivity.class);
//            intent.putExtra("day", dayText);
//            intent.putExtra("clickMonth", monthYearFromDate(selectedDate) );
//            startActivity(intent);


        }
    }
}
