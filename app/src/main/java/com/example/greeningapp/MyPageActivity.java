package com.example.greeningapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("User");

        Tv_my_name = findViewById(R.id.my_name);
        myPageSeed = (TextView) findViewById(R.id.myPageSeed);

        // 사용자 정보 가져오기, 이름 표시
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference userRef = mDatabaseRef.child(uid);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("username").getValue(String.class) + "님"; // "님"을 추가하여 표시 이름 생성;
                        Tv_my_name.setText(name);
                        String Seed = String.valueOf(dataSnapshot.child("spoint").getValue()) + "씨드"; // "님"을 추가하여 표시 이름 생성;
                        myPageSeed.setText(Seed);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MyPageActivity.this, "회원정보를 불러오는데에 실패했습니다.", Toast.LENGTH_SHORT).show();
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

        ImageButton donationBtn = findViewById(R.id.gv_move);
        donationBtn.setOnClickListener(this);

        ImageButton ChangeBtn = findViewById(R.id.change_move);
        ChangeBtn.setOnClickListener(this);

        ImageButton orderBtn = findViewById(R.id.jmny_move);
        orderBtn.setOnClickListener(this);

        ImageButton reviewBtn = findViewById(R.id.rvlist_move);
        reviewBtn.setOnClickListener(this);


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
        } else if (id == R.id.gv_move) {
            intent = new Intent(MyPageActivity.this, DonationCertificateActivity.class);
            startActivity(intent);
        } else if (id == R.id.change_move) {
            intent = new Intent(MyPageActivity.this, ChangeActivity.class);
            startActivity(intent);
        } else if (id == R.id.jmny_move) {
            intent = new Intent(MyPageActivity.this, OrderHistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.rvlist_move) {
            intent = new Intent(MyPageActivity.this, ReviewHistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.tt_move) {
            intent = new Intent(MyPageActivity.this, WithdrawalActivity.class);
            startActivity(intent);
            //이용 약관 보류
        }
    }

    //로그아웃ㅅ 확인
    public void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyPageActivity.this);
        builder.setTitle("로그아웃");
        builder.setMessage("정말로 로그아웃하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 취소 버튼 클릭 시 아무 작업도 수행하지 않음
            }
        });
        builder.create().show();
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