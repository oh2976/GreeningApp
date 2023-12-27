package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DecimalFormat;

public class DonationMainActivity extends AppCompatActivity {
    private DatabaseReference databaseReference2;
    private FirebaseAuth firebaseAuth;
    TextView donationPoint;
    TabLayout tab_donate;
    ViewPager viewPager_donate;
    Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    // 화폐 단위 객체 생성
    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_main);

        // 툴바
        toolbar = findViewById(R.id.toolbar_donation);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); //기본 제목 삭제.
        actionBar.setDisplayHomeAsUpEnabled(true);

        donationPoint = (TextView) findViewById(R.id.donation_point);

        // 파이어베이스 경로 설정
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference2 = FirebaseDatabase.getInstance().getReference("User");

        databaseReference2.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                // 회원 정보 테이블에서 sPoint 데이터 가져와서 뿌리기
                User user = dataSnapshot.getValue(User.class); //  만들어 뒀던 User 객체에 데이터를 담는다.
                donationPoint.setText(decimalFormat.format(user.getSpoint()) + " 씨드");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tab_donate = (TabLayout) findViewById(R.id.tab_donate);
        viewPager_donate = (ViewPager) findViewById(R.id.viewPager_donate);

        ViewPagerDonationAdapter viewPagerDonationAdapter = new ViewPagerDonationAdapter(getSupportFragmentManager());
        viewPager_donate.setAdapter(viewPagerDonationAdapter);

        tab_donate.setupWithViewPager(viewPager_donate);

        // 하단바 구현
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation_doantionMain);

        // 초기 선택 항목 설정
        bottomNavigationView.setSelectedItemId(R.id.tab_donation);

        // BottomNavigationView의 아이템 클릭 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.tab_home) {
                    // Home 액티비티로 이동
                    startActivity(new Intent(DonationMainActivity.this, MainActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_shopping) {
                    // Category 액티비티로 이동
                    startActivity(new Intent(DonationMainActivity.this, CategoryActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_donation) {
                    // Donation 액티비티로 이동
                    startActivity(new Intent(DonationMainActivity.this, DonationMainActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_mypage) {
                    // My Page 액티비티로 이동
                    startActivity(new Intent(DonationMainActivity.this, MyPageActivity.class));
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