package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DecimalFormat;

public class DonationCompleteActivity extends AppCompatActivity {
    DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private TextView completeuPoint, completement, completeDoName, completeDoDate, completeDoPoint;
    private String donationName, donationDate, userName, donationImg;
    private int donationPoint;
    private Button goToMain;
    private BottomNavigationView bottomNavigationView;
    private ImageView donationImg_complete;

    // 화폐 단위 형식 객체 생성
    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_complete);

        // 툴바
        Toolbar mToolbar = findViewById(R.id.toolbar_donation);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        // 파이어베이스 경로 설정
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("User");

        // 레이아웃 요소 설정
        completeuPoint = (TextView) findViewById(R.id.complete_point);
        completeDoName = findViewById(R.id.completeDoName);
        completeDoDate = findViewById(R.id.completeDoDate);
        completeDoPoint = findViewById(R.id.completeDoPoint);
        completement = (TextView) findViewById(R.id.completement);
        donationImg_complete = (ImageView) findViewById(R.id.donation_img_complete);

        // 회원 데이블 데이터 불러오기
        databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // User 객체에 데이터 담기, 포인트 데이터 넣기
                User user = dataSnapshot.getValue(User.class);
                completeuPoint.setText(decimalFormat.format(user.getSpoint()) + " 씨드");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // DonationDetailActivity에서 보낸 bundle이 있다면 보낸 회원, 프로젝트 정보 받기
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            userName = bundle.getString("userName");
            donationName = bundle.getString("donationName");
            donationPoint = bundle.getInt("donationPoint");
            donationDate = bundle.getString("donationDate");
            donationImg = bundle.getString("donationImg");

            Log.d("DonationCompleteActivity", userName + donationName + donationPoint + donationDate);
        }

        // 받은 정보 레이아웃에 데이터 넣기
        completement.setText("총 " + String.valueOf(donationPoint) + " 씨드로");
        completeDoName.setText(donationName);
        completeDoDate.setText(donationDate);
        completeDoPoint.setText(String.valueOf(decimalFormat.format(donationPoint)));
        Glide.with(getApplicationContext()).load(donationImg).into(donationImg_complete);

        goToMain = (Button) findViewById(R.id.goToMain);
        goToMain.setOnClickListener(new View.OnClickListener() {
            // 메인으로 가는 버튼을 누르면 메인으로 이동
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DonationCompleteActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });



        // 하단바 구현
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation_docomplete);

        // 초기 선택 항목 설정
        bottomNavigationView.setSelectedItemId(R.id.tab_donation);

        // BottomNavigationView의 아이템 클릭 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.tab_home) {
                    // Home 액티비티로 이동
                    startActivity(new Intent(DonationCompleteActivity.this, MainActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_shopping) {
                    // Category 액티비티로 이동
                    startActivity(new Intent(DonationCompleteActivity.this, CategoryActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_donation) {
                    // Donation 액티비티로 이동
                    startActivity(new Intent(DonationCompleteActivity.this, DonationMainActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_mypage) {
                    // My Page 액티비티로 이동
                    startActivity(new Intent(DonationCompleteActivity.this, MyPageActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });


    }

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