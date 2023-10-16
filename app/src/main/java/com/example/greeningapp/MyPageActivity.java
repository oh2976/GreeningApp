package com.example.greeningapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyPageActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView Tv_my_name, myPageSeed;
    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증 처리
    private DatabaseReference mDatabaseRef;
    Toolbar toolbar;
    Dialog dialog;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목 삭제.
        actionBar.setDisplayHomeAsUpEnabled(true);

        dialog = new Dialog(MyPageActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.customdialog);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("User");

        Tv_my_name = findViewById(R.id.my_name);
        myPageSeed = (TextView) findViewById(R.id.myPageSeed);

        // 하단바 구현
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation);
        // 초기 선택 항목 설정
        bottomNavigationView.setSelectedItemId(R.id.tab_mypage);

        // BottomNavigationView의 아이템 클릭 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.tab_home) {
                    // Home 액티비티로 이동
                    startActivity(new Intent(MyPageActivity.this, MainActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.tab_shopping) {
                    // Category 액티비티로 이동
                    startActivity(new Intent(MyPageActivity.this, CategoryActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.tab_donation) {
                    // Donation 액티비티로 이동
                    startActivity(new Intent(MyPageActivity.this, DonationMainActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.tab_mypage) {
                    // My Page 액티비티로 이동
                    startActivity(new Intent(MyPageActivity.this, MyPageActivity.class));
                    return true;
                }
                return false;
            }
        });

        // 사용자 정보 가져오기, 이름 표시
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference userRef = mDatabaseRef.child(uid);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("username").getValue(String.class) + "님" ; // "님"을 추가하여 표시 이름 생성;
                        Tv_my_name.setText(name);
                        String Seed = String.valueOf(dataSnapshot.child("spoint").getValue()) + "씨드";
                        myPageSeed.setText(Seed);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    Toast.makeText(MyPageActivity.this, "회원정보를 불러오는데에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        ImageButton pointBtn = findViewById(R.id.pn_move);
        pointBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, PointHistoryActivity.class);
                startActivity(intent);
            }
        });

        ImageButton checkInBtn = findViewById(R.id.cc_move);
        checkInBtn.setOnClickListener(this);

        ImageButton quizBtn = findViewById(R.id.qz_move);
        quizBtn.setOnClickListener(this);

        ImageButton ChangeBtn = findViewById(R.id.change_move);
        ChangeBtn.setOnClickListener(this);

        ImageButton orderBtn = findViewById(R.id.orderhistory_move);
        orderBtn.setOnClickListener(this);

        ImageButton ReviewBtn = findViewById(R.id.hg_move);
        ReviewBtn.setOnClickListener(this);

        ImageButton withdrawalBtn = findViewById(R.id.tt_move);
        withdrawalBtn.setOnClickListener(this);

        ImageButton logoutBtn = findViewById(R.id.out_move);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showLogoutConfirmationDialog();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        int id = v.getId();

        //씨드 X
        if (id == R.id.pn_move) {
            intent = new Intent(MyPageActivity.this, PointHistoryActivity.class);
            startActivity(intent);

        } else if (id == R.id.cc_move) {
            intent = new Intent(MyPageActivity.this, AttendanceActivity.class);
            startActivity(intent);

        }else if (id == R.id.qz_move) {
            intent = new Intent(MyPageActivity.this, QuizActivity.class);
            startActivity(intent);

        } else if (id == R.id.change_move) {
            intent = new Intent(MyPageActivity.this, ChangeActivity.class);
            startActivity(intent);

        } else if (id == R.id.orderhistory_move) {
            intent = new Intent(MyPageActivity.this, OrderHistoryActivity.class);
            startActivity(intent);

        } else if (id == R.id.hg_move) {
            intent = new Intent(MyPageActivity.this, ReviewHistoryActivity.class);
            startActivity(intent);

        } else if (id == R.id.tt_move) {
            intent = new Intent(MyPageActivity.this, WithdrawalActivity.class);
            startActivity(intent);
        }
    }

    //로그아웃ㅅ 확인
    public void showLogoutConfirmationDialog() {

        dialog.show();

        TextView confirmTextView = dialog.findViewById(R.id.say);
        confirmTextView.setText("로그아웃하시겠습니까?");

        Button btnno = dialog.findViewById(R.id.btnNo);
        Button btnok = dialog.findViewById(R.id.btnOk);
        btnno.setText("아니요");
        btnok.setText("예");

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자 계정 삭제
                logout();
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

    private void logout() {
        mFirebaseAuth.signOut();
        Intent intent = new Intent(MyPageActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) { //뒤로가기
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}