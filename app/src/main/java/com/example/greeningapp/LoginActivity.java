package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private EditText mEtEmail, mEtPwd;
    private CheckBox mCheckBoxSaveId;
    private SharedPreferences sharedPreferences;
    String strEmail;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 파이어베이스 초기화
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("User");

        // 레이아웃 요소
        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mCheckBoxSaveId = findViewById(R.id.checkbox_saveId);

        // 체크박스 색상 설정
        ColorStateList colorStateList = new ColorStateList(
                new int[][] {
                        new int[] { android.R.attr.state_checked },
                        new int[] { -android.R.attr.state_checked }
                },
                new int[] {
                        getResources().getColor(R.color.colorPrimaryDark),
                        getResources().getColor(R.color.textColorGray)
                }
        );

        mCheckBoxSaveId.setButtonTintList(colorStateList);

        // sharedPreferences 초기화
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);

        // 자동 로그인 설정
        boolean autoLoginEnabled = sharedPreferences.getBoolean("save_id", false);
        mCheckBoxSaveId.setChecked(autoLoginEnabled);

        // 저장된 이메일 아이디가 있다면 입력 필드에 설정
        String savedEmail = sharedPreferences.getString("email", "");
        if (!TextUtils.isEmpty(savedEmail)) {
            mEtEmail.setText(savedEmail);
        }

        // 다이얼로그 설정
        dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);

        // 회원가입 후 가입된 이메일 아이디 전달 받아 입력 필드에 설정
        Intent receivedIntent = getIntent();
        if (receivedIntent != null && receivedIntent.hasExtra("userEmail")) {
            strEmail = receivedIntent.getStringExtra("userEmail");
            mEtEmail.setText(strEmail);
        }

        // 로그인 버튼 클릭 이벤트 설정
        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력 값 확인
                strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();

                // 파이어베이스로 로그인 시도
                mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // 자동 로그인 체크박스 체크 시 이메일 아이디 저장
                            if (mCheckBoxSaveId.isChecked()) {
                                String uid = mFirebaseAuth.getCurrentUser().getUid();
                                mDatabaseRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            User user = snapshot.getValue(User.class);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("email", user.getEmailId());
                                            editor.putBoolean("save_id", true);
                                            editor.apply();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // 로그인 오류 시 에러 메세지 출력
                                        Log.e("LoginActivity, 로그인 오류", String.valueOf(error.toException()));
                                    }
                                });
                            } else {
                                // 자동 로그인 체크박스 미체크 시 저장된 정보 제거
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove("email");
                                editor.remove("save_id");
                                editor.apply();
                            }

                            // 관리자 계정 로그인 처리
                            if("test@gmail.com".equals(strEmail) && "123456".equals(strPwd)){
                                Intent intent = new Intent(LoginActivity.this, ManagerMainActivity.class);
                                startActivity(intent);
                                finish();
                            } else{
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        } else{
                            // 로그인 실패 시
                            showDialog();  // 다이얼로그 생성
                        }
                    }
                });
            }
        });

        // 회원가입 텍스트뷰에 밑출 추가
        TextView txtRegister = (TextView) findViewById(R.id.LoginTxtRegister);

        String mystring = txtRegister.getText().toString();
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        txtRegister.setText(content);

        // 회원가입 버튼 클릭 이벤트 설정
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showDialog() {
        dialog.show();

        TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
        confirmTextView.setText("아이디 또는 비밀번호가 일치하지 않습니다.");

        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnOk.setText("확인");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}