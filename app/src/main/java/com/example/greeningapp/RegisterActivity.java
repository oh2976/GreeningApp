package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
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
    Dialog dialog;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private EditText mEtEmail, mEtPwd;
    private EditText mEtName, mEtPhone, mEtPostcode, mEtAddress;
    private CheckBox checkBoxAll;
    private CheckBox checkBox1;
    private CheckBox checkBox2;
    private Button mBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar_register);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 다이얼로그 설정
        dialog = new Dialog(RegisterActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);

        // 파이어베이스 설정
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("User");

        // 레이아웃 요소
        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mBtnRegister = findViewById(R.id.btn_register);
        mEtName = findViewById(R.id.et_name);
        mEtPhone = findViewById(R.id.et_phone);
        mEtPostcode = findViewById(R.id.et_postcode);
        mEtAddress = findViewById(R.id.et_address);
        checkBoxAll = findViewById(R.id.checkbox_all);
        checkBox1 = findViewById(R.id.checkbox1);
        checkBox2 = findViewById(R.id.checkbox2);

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

        checkBoxAll.setButtonTintList(colorStateList);
        checkBox1.setButtonTintList(colorStateList);
        checkBox2.setButtonTintList(colorStateList);

        // 체크 박스 클릭 이벤트 설정
        checkBoxAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckChanged(checkBoxAll);
            }
        });

        checkBox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckChanged(checkBox1);
            }
        });

        checkBox2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckChanged(checkBox2);
            }
        });

        // 내용보기 텍스트뷰에 밑줄 추가
        TextView tvContent1 = (TextView) findViewById(R.id.tv_content1);
        TextView tvContent2 = (TextView) findViewById(R.id.tv_content2);

        String mystring = tvContent1.getText().toString();
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        tvContent1.setText(content);

        String mystring2 = tvContent2.getText().toString();
        SpannableString content2 = new SpannableString(mystring2);
        content2.setSpan(new UnderlineSpan(), 0, mystring2.length(), 0);
        tvContent2.setText(content2);

        // 내용보기 텍스트뷰 클릭 이벤트 설정
        tvContent1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, TermsOfUseActivity.class);
                intent.putExtra("selectedTab", "left");
                startActivity(intent);
            }
        });

        tvContent2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, TermsOfUseActivity.class);
                intent.putExtra("selectedTab", "right");
                startActivity(intent);
            }
        });

        // 가입하기 버튼 클릭 이벤트 리스너
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력 값 확인
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();
                String strName = mEtName.getText().toString();
                String strPhone = mEtPhone.getText().toString();
                String strPostcode = mEtPostcode.getText().toString();
                String strAddress = mEtAddress.getText().toString();

                // 입력창이 하나라도 비어 있을 경우
                if (strEmail.isEmpty() || strPwd.isEmpty() || strName.isEmpty() || strPhone.isEmpty() || strPostcode.isEmpty() || strAddress.isEmpty()) {
                    showDialog2();  // 다이얼로그 생성
                    return;
                }

                // 모든 이용약관에 동의한 경우에만 회원가입 승인
                boolean allChecked = checkBoxAll.isChecked() && checkBox1.isChecked() && checkBox2.isChecked();
                if (!allChecked) {
                    showDialog();  // 다이얼로그 생성
                    return;
                }

                // 파이어베이스 인증 및 데이터베이스에 사용자 정보 저장
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

                            mDatabaseRef.child(firebaseUser.getUid()).setValue(user);

                            // 가입 후 로그인 화면으로 이동 (가입된 아이디 자동 적용)
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.putExtra("userEmail", firebaseUser.getEmail());
                            startActivity(intent);
                            finish();
                        } else {
                            // 가입 실패 시 다이얼로그 생성
                            showDialog3();
                        }
                    }
                });
            }
        });
    }

    // 모든 이용약관 동의 체크 시 모든 체크박스 상태 변경
    private void onCheckChanged(CompoundButton compoundButton) {
        int id = compoundButton.getId();

        if (id == R.id.checkbox_all) {
            if (checkBoxAll.isChecked()) {
                checkBox1.setChecked(true);
                checkBox2.setChecked(true);
            } else {
                checkBox1.setChecked(false);
                checkBox2.setChecked(false);
            }
        } else {
            checkBoxAll.setChecked(checkBox1.isChecked() && checkBox2.isChecked());
        }
    }

    // 현재 시간 문자열로 반환
    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    // 뒤로가기 처리
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void showDialog() {
        dialog.show();

        TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
        confirmTextView.setText("이용약관에 모두 동의해야 회원가입이 가능합니다.");

        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnOk.setText("확인");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void showDialog2() {
        dialog.show();

        TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
        confirmTextView.setText("사용자 정보를 모두 입력하셔야\n회원가입이 가능합니다.");

        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnOk.setText("확인");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void showDialog3() {
        dialog.show();

        TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
        confirmTextView.setText("회원가입에 실패했습니다.\n다시 시도 해주십시오.");

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