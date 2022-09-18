package com.example.please_last_java_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class loginActivity extends AppCompatActivity {

    private EditText loginEmail, loginPwd;
    private Button loginBtn;
    private TextView loginQn;

    private FirebaseAuth mAuth;
    private ProgressDialog loader;


    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.loginEmail);
        loginPwd = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginButton);
        loginQn = findViewById(R.id.loginPageQuestion);

        //firebase 데이터 접속 되게끔 하는거!
        mAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);


        //로그아웃 안했으면~바로 창이 home으로 넘어가진당!
       /* if(mAuth !=null){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }*/


        //회원가입할? 버튼 누르면
        loginQn.setOnClickListener(new View.OnClickListener() {
            @Override

            //클릭 눌러따!!



            public void onClick(View view) {

                // 회원가입하실래요? 버튼 누르면 회원가입창으로 넘어감~
                Intent intent = new Intent(loginActivity.this, registrationActivity.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    String email = loginEmail.getText().toString().trim();
                    String password = loginPwd.getText().toString().trim();

                    if (TextUtils.isEmpty(email)) {
                        loginEmail.setError("이메일 입력해주세요");
                        return;
                    }
                    if (TextUtils.isEmpty(password)) {
                        loginPwd.setError("페스워드 입력해주세요");
                        return;
                    }else {
                        loader.setMessage("로그인 진행 중입니다");
                        loader.setCanceledOnTouchOutside(false);
                        loader.show();



                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Intent intent = new Intent(loginActivity.this, homeActivity.class);
                                    startActivity(intent);
                                    finish();

                                }else{
                                    String error = task.getException().toString();
                                    Toast.makeText(loginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();

                                }
                                loader.dismiss();
                            }
                        });

                    }

                }

        });

    }
}