package com.example.greeningapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");

    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증 처리
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private EditText mEtEmail, mEtPwd; // 회원가입 입력필드

    private EditText mEtName, mEtPhone, mEtPostcode, mEtAddress;
    private Button mBtnRegister; // 회원가입 입력 버튼



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("User");

        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mBtnRegister = findViewById(R.id.btn_register);

        mEtName = findViewById(R.id.et_name);
        mEtPhone = findViewById(R.id.et_phone);
        mEtPostcode = findViewById(R.id.et_postcode);
        mEtAddress = findViewById(R.id.et_address);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 처리 시작
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();

                String strName = mEtName.getText().toString();
                String strPhone = mEtPhone.getText().toString();
                String strPostcode = mEtPostcode.getText().toString();
                String strAddress = mEtAddress.getText().toString();

                // Firebase auth 진행
                mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                            User user = new User();
                            user.setIdToken(firebaseUser.getUid());
                            user.setEmailId(firebaseUser.getEmail());
                            user.setPassword(strPwd);

                            user.setUsername(strName);
                            user.setPhone(strPhone);
                            user.setPostcode(strPostcode);
                            user.setAddress(strAddress);
                            user.setRegdate(getTime());
                            user.setUpoint(0);
                            user.setSpoint(0);
                            user.setDoquiz("No");

                            user.getAttendance();


                            //setValue : database에 insert(삽입) 행위
                            // 회원 정보 데이터베이스에 저장
                            mDatabaseRef.child(firebaseUser.getUid()).setValue(user);

                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.putExtra("userEmail", firebaseUser.getEmail());
                            startActivity(intent);

                            Toast.makeText(RegisterActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }
}