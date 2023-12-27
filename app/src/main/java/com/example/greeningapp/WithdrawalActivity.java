package com.example.greeningapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WithdrawalActivity extends AppCompatActivity{
    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증 처리
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private RadioButton radioButton;
    private Button cancelButton, withdrawalButton;
    Toolbar toolbar;
    Dialog dialog, dialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목 삭제.
        actionBar.setDisplayHomeAsUpEnabled(true);

        ///다이얼로그
        dialog = new Dialog(WithdrawalActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.customdialog);

        dialog2 = new Dialog(WithdrawalActivity.this);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.setContentView(R.layout.dialog_confirm);

        // 파이어베이스 초기화
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("User");

        // XML 레이아웃의 요소와 연결
        radioButton = findViewById(R.id.radioButton);
        cancelButton = findViewById(R.id.wbtn1);
        withdrawalButton = findViewById(R.id.wbtn2);

        // 취소하기 버튼 클릭
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // RadioButton 상태 변경 시
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioButton.isChecked()) {
                    withdrawalButton.setEnabled(true);
                } else {
                    withdrawalButton.setEnabled(false);
                }
            }
        });

        // 탈퇴하기 버튼 클릭
        withdrawalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioButton.isChecked()) {
                    showConfirmationDialog();
                } else {
                    showFaildialog();
                }
            }
        });
    }

    // 실패 다이얼로그 표시
    public void showFaildialog() {
        dialog2.show();

        TextView confirmTextView = dialog2.findViewById(R.id.confirmTextView);
        confirmTextView.setText("유의사항 확인 후 동의하셔야 회원탈퇴가 가능합니다.");

        Button btnOk = dialog2.findViewById(R.id.btn_ok);
        btnOk.setText("확인");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });
    }

    // 확인 다이얼로그 표시
    private void showConfirmationDialog() {
        dialog.show();

        TextView confirmTextView = dialog.findViewById(R.id.say);
        confirmTextView.setText("앱을 탈퇴하시겠습니까?");

        Button btnno = dialog.findViewById(R.id.btnNo);
        Button btnok = dialog.findViewById(R.id.btnOk);
        btnno.setText("아니요");
        btnok.setText("예");

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자 계정 삭제 함수 호출
                deleteAccount();
                dialog.dismiss();
            }
        });

        btnno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    private void deleteAccount () {
        mFirebaseAuth = mFirebaseAuth.getInstance();
        if (mFirebaseAuth != null) {
            mFirebaseAuth.getCurrentUser().delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(WithdrawalActivity.this, LoginActivity.class);
                                startActivity(intent);
                                // 계정 삭제가 성공한 경우
                                dialog2.show();

                                TextView confirmTextView = dialog2.findViewById(R.id.confirmTextView);
                                confirmTextView.setText("계정이 성공적으로 삭제되었습니다.");

                                Button btnOk = dialog2.findViewById(R.id.btn_ok);
                                btnOk.setText("확인");
                                btnOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog2.dismiss();
                                    }
                                });
                                finish();
                            } else {
                                // 계정 삭제가 실패한 경우
                                dialog2.show();

                                TextView confirmTextView = dialog2.findViewById(R.id.confirmTextView);
                                confirmTextView.setText("계정 삭제를 실패했습니다. 다시 시도해주세요");

                                Button btnOk = dialog2.findViewById(R.id.btn_ok);
                                btnOk.setText("확인");
                                btnOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog2.dismiss();
                                    }
                                });
                            }
                        }
                    });
        }
    }

    // 옵션 메뉴 아이템 선택 이벤트 처리
    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) { //뒤로가기
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}