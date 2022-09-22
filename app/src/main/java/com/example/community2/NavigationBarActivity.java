package com.example.community2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.community2.fragment.ChatFragment;
import com.example.community2.fragment.CommunityFragment;
import com.example.community2.fragment.MyPageFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class NavigationBarActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_bar);

        bottomNavigationView = findViewById(R.id.nav_test);

        //처음화면
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame,new CalendarFragment()).commit(); //FrameLayout에 fragment.xml 띄우기

        //바텀 네비게이션뷰 안의 아이템 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    //item을 클릭시 id값을 가져와 FrameLayout에 fragment.xml띄우기
                    case R.id.nav_calendar:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new CalendarFragment()).commit();
                        return true;
                    case R.id.nav_community:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new CommunityFragment()).commit();
                        break;
                    case R.id.nav_chat:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new ChatFragment()).commit();
                        break;
                    case R.id.nav_myPage:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new MyPageFragment()).commit();
                        break;
                }
                return true;
            }
        });
    }
}