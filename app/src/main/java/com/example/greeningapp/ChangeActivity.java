package com.example.greeningapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangeActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증 처리
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private EditText mEtName, mEtPostcode, mEtAddress, mEtEmail, mEtPhone;
    private Button mBtnSave;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);

        // 액션바 숨기기
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        backButton = findViewById(R.id.back_ic);

        Button resetPwButton = findViewById(R.id.btnPassword);
        resetPwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpdatePasswordBtn();
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        //뒤로가기

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("User");

        mEtName = findViewById(R.id.et_name);
        mEtPostcode = findViewById(R.id.et_postcode);
        mEtAddress = findViewById(R.id.et_address);
        mEtEmail = findViewById(R.id.et_email);
        mEtPhone = findViewById(R.id.et_phone);
        mBtnSave = findViewById(R.id.save_btn);

        // 사용자 정보 가져오기
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference userRef = mDatabaseRef.child(uid);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("username").getValue(String.class);
                        String phone = dataSnapshot.child("phone").getValue(String.class);
                        String email = dataSnapshot.child("emailId").getValue(String.class);
                        String postcode = dataSnapshot.child("postcode").getValue(String.class);
                        String address = dataSnapshot.child("address").getValue(String.class);

                        mEtName.setText(name);
                        mEtPhone.setText(phone);
                        mEtEmail.setText(email);
                        mEtPostcode.setText(postcode);
                        mEtAddress.setText(address);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ChangeActivity.this, "회원정보를 불러오는데에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // 저장 버튼 클릭 이벤트 처리
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
    }

    private void saveChanges() {
        // 변경된 정보 가져오기
        String name = mEtName.getText().toString().trim();
        String postcode = mEtPostcode.getText().toString().trim();
        String address = mEtAddress.getText().toString().trim();

        // 변경된 정보 업데이트
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference userRef = mDatabaseRef.child(uid);
            userRef.child("username").setValue(name);
            userRef.child("postcode").setValue(postcode);
            userRef.child("address").setValue(address);


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("회원정보 수정완료");
            builder.setMessage("회원정보가 성공적으로 수정되었습니다");

            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(ChangeActivity.this, MyPageActivity.class);
                    startActivity(intent);
                }
            });
            builder.show();
        }
    }
    private void setUpdatePasswordBtn() {

        // 팝업 다이얼로그
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("비밀번호 재설정");
        builder.setMessage("비밀번호 재설정 이메일을 보내시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sendEmailForPasswordUpdate();
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 아무 작업도 수행하지 않음
            }
        });

        builder.show();
    }


    // 비밀번호 재설정 이메일 보내기
    private void sendEmailForPasswordUpdate() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = getEmail();

        if (email != null) {
            auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "이메일이 전송되었습니다", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "이메일 전송 실패", Snackbar.LENGTH_LONG).show();
                }
            });
        } else {
            Snackbar.make(getWindow().getDecorView().getRootView(), "해당 이메일이 존재하지 않습니다", Snackbar.LENGTH_LONG).show();
        }
    }

    // 사용자 이메일 가져오기
    private String getEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String email = user.getEmail();
            return email != null ? email.toString() : null;
        } else {
            // 사용자가 로그인하지 않음
            return null;
        }
    }
}