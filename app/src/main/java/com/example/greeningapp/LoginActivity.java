package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증 처리
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private EditText mEtEmail, mEtPwd; // 로그인 입력필드
    private CheckBox mCheckBoxSaveId;

    private SharedPreferences sharedPreferences;

    String strEmail;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("User");

        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mCheckBoxSaveId = findViewById(R.id.checkbox_saveId);

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

        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);

        boolean autoLoginEnabled = sharedPreferences.getBoolean("save_id", false);
        mCheckBoxSaveId.setChecked(autoLoginEnabled);

        String savedEmail = sharedPreferences.getString("email", "");
        if (!TextUtils.isEmpty(savedEmail)) {
            mEtEmail.setText(savedEmail);
        }

        dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);

        Intent receivedIntent = getIntent();
        if (receivedIntent != null && receivedIntent.hasExtra("userEmail")) {
            strEmail = receivedIntent.getStringExtra("userEmail");
            mEtEmail.setText(strEmail);
        }

        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그인 요청
                strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();

                mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            if (mCheckBoxSaveId.isChecked()) {
                                // 로그인 성공 후, Firebase Uid를 사용하여 사용자 정보를 불러옴
                                // 사용자 정보를 SharedPreferences에 저장할 수 있음
                                String uid = mFirebaseAuth.getCurrentUser().getUid();
                                mDatabaseRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            // 사용자 정보를 불러와 SharedPreferences에 저장
                                            User user = snapshot.getValue(User.class);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("email", user.getEmailId());
                                            // 다른 사용자 정보도 저장할 수 있음
                                            editor.putBoolean("save_id", true);
                                            editor.apply();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // 처리 중 에러 발생 시 처리
                                    }
                                });
                            } else {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove("email");
                                editor.remove("save_id");
                                editor.apply();
                            }

                            //로그인 성공
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();


                        } else{
                            showDialog();
                        }
                    }
                });
            }
        });

        TextView txtRegister = (TextView) findViewById(R.id.LoginTxtRegister);

        String mystring = txtRegister.getText().toString();
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        txtRegister.setText(content);
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