package com.example.please_last_java_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class registrationActivity extends AppCompatActivity {

    //지역변수 설정해준다~~여기서만 사용되는 액희덜~ㅋ
    private EditText RegEmail, RegPwd, RegPwd2;
    private Button RegBtn;
    private TextView RegQn;

    //자 이제 회원가입 데이터를 저장시키러 가볼까?^^
    private FirebaseAuth mAuth;

    private ProgressDialog loader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);


        //각각 목록에 대한 양식들을 보여주는 고에요
        RegEmail = findViewById(R.id.RegisterEmail);
        RegPwd = findViewById(R.id.RegisterPassword);
        RegPwd2 = findViewById(R.id.RegisterPassword2);
        RegBtn = findViewById(R.id.RegisterButton);
        RegQn = findViewById(R.id.RegisterQuestion);


        //돌아가기 버튼 누르면, 다시 로그인창으로 이동한다~
        RegQn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(registrationActivity.this, loginActivity.class);
                startActivity(intent);
            }
        });

        //회원가입 버튼 누르면!! 어케 될까~? 당근 이메일, 비번 데이터가 firebase에 저장되겠지~ㅋ
        //그리고!! 제대로 입력 안하면 fail 메시지 띄워줘야지!!
        //ㅇㅋ 화내지마러~
        RegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = RegEmail.getText().toString().trim();
                String password = RegPwd.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    RegEmail.setError("이메일 주소 적어주세요.");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    RegPwd.setError("패스워드 입력해주세요.");
                    return;
                }

                if(RegPwd2.getText().toString().length() == 0) {
                    Toast.makeText(registrationActivity.this, "패스워드 재확인 필수 입력", Toast.LENGTH_SHORT).show();
                    RegPwd2.requestFocus();
                    return;
                }

                if(password.length()<6){
                    Toast.makeText(registrationActivity.this, "비밀번호를 6자리 이상 입력해주세요", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!RegPwd.getText().toString().equals(RegPwd2.getText().toString())){
                    Toast.makeText(registrationActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_LONG).show();
                    RegPwd.setText("");
                    RegPwd2.setText("");
                    RegPwd.requestFocus();
                    return;
                } else{
                    loader.setMessage("회원가입 진행 중");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();


                    //회원가입 내용을 모두 입력했으면~파이어베이스에 데이터 저장은 필수!
                    //그 이후에 뭐 해야겠심? 바로바로~회원가입창으로 돌아가야제!!
                    //ㅇㅋ~
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(registrationActivity.this, "회원가입 성공", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(registrationActivity.this, loginActivity.class);
                                startActivity(intent);
                                finish();
                                loader.dismiss();

                            } else {
                                String error = task.getException().toString();
                                Toast.makeText(registrationActivity.this, "회원가입 실패", Toast.LENGTH_LONG).show();

                            }


                        }

                    });


                }
            }
        });


    }

}
