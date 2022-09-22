package com.example.community2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.community2.fragment.ChatFragment;
import com.example.community2.fragment.CommunityFragment;
import com.example.community2.fragment.MyPageFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class NavigationBarMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_bar);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.mainActivity_bottomnavigationview);

        getSupportFragmentManager().beginTransaction().add(R.id.mainActivity_framelayout,new CalendarFragment()).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_community:
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainActivity_framelayout, new CommunityFragment()).commit();
                        return true;
                    case R.id.nav_chat:
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainActivity_framelayout, new ChatFragment()).commit();
                        return true;
                    case R.id.nav_calendar:
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainActivity_framelayout, new CalendarFragment()).commit();
                        return true;
                    case R.id.nav_myPage:
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainActivity_framelayout, new MyPageFragment()).commit();
                        return true;
                }
                return false;
            }
        });
        passPushTokenServer();
    }

    void passPushTokenServer() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> map = new HashMap<>();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()) {
                    String token = task.getResult();
                    map.put("pushToken", token);
                    FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);
                }
            }
        });
    }
}