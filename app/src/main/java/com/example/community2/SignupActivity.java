package com.example.community2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.community2.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private EditText name;
    private Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email = (EditText) findViewById(R.id.signupActivity_edittext_id);
        password = (EditText) findViewById(R.id.signupActivity_edittext_password);
        name = (EditText) findViewById(R.id.signupActivity_edittext_name);
        signup = (Button) findViewById(R.id.signupActivity_button_signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent= new Intent(getApplicationContext(), NavigationBarMainActivity.class);
//                startActivity(intent);

                if(email.getText().toString() == null || password.getText().toString() == null || name.getText().toString() == null) {
                    return;
                }

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    Log.d("회원가입 확인", String.valueOf(task));

                                    Intent intent = new Intent(SignupActivity.this, HomeActivity2.class);
                                    intent.putExtra("isLogin", false); //넘어갈 때 로그인 값 = false 을 넣어줌
                                    startActivity(intent);
                                    finish();


//                                UserModel userModel = new UserModel();
//                                String uid = task.getResult().getUser().getUid();
//                                userModel.userName = name.getText().toString();
//                                userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//                                if (task.isSuccessful()) {
//                                    Intent intent = new Intent(SignupActivity.this, HomeActivity2.class);
//                                    startActivity(intent);
//                                    finish();

                                }
                                //FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            }
//                            @Override
//                            public void onSuccess(Void unused) {
//                               // SignupActivity.this.finish();
//                                Intent intent = new Intent(SignupActivity.this, HomeActivity2.class);
//                                startActivity(intent);
//                            }



                        });
                    }
                });
            }

    }