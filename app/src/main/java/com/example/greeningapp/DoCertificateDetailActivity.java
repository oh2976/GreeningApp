package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DoCertificateDetailActivity extends AppCompatActivity {
    private ImageView CERTdetailed_img, CERTdetail_longimg;
    private TextView CERTdetailed_name, CERTdetail_start, CERTdetail_end;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    Donation donation;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_certificate_detail);

        // 파이어베이스 경로 설정
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Donation");

        // CertificateAdapter.java 파일에서 보낸 데이터 받음
        final Object object = getIntent().getSerializableExtra("CertificateDetail");
        // 받은 데이터를 Donation 객체로 형변환
        if(object instanceof Donation){
            donation = (Donation) object;
        }

        // 레이아웃 요소 설정
        CERTdetailed_img = (ImageView) findViewById(R.id.CERTdetailed_img);
        CERTdetail_longimg = (ImageView) findViewById(R.id.CERTdetail_longimg);
        CERTdetailed_name = (TextView) findViewById(R.id.CERTdetailed_name);
        CERTdetail_start = (TextView) findViewById(R.id.CERTdetail_start);
        CERTdetail_end = (TextView) findViewById(R.id.CERTdetail_end);

        // 객체가 null이 아니라면 레이아웃에 데이터 넣기
        if(donation != null) {
            Glide.with(getApplicationContext()).load(donation.getDonationimg()).into(CERTdetailed_img);
            Glide.with(getApplicationContext()).load(donation.getDonationdetailimg()).into(CERTdetail_longimg);
            CERTdetailed_name.setText(donation.getDonationname() + " 기부 결산 내역");
            CERTdetail_start.setText(donation.getDonationstart());
            CERTdetail_end.setText(donation.getDonationend());
        }

        // 툴바
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        // 하단바 구현
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation_docertifi);

        // 초기 선택 항목 설정
        bottomNavigationView.setSelectedItemId(R.id.tab_donation);

        // BottomNavigationView의 아이템 클릭 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.tab_home) {
                    // Home 액티비티로 이동
                    startActivity(new Intent(DoCertificateDetailActivity.this, MainActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_shopping) {
                    // Category 액티비티로 이동
                    startActivity(new Intent(DoCertificateDetailActivity.this, CategoryActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_donation) {
                    // Donation 액티비티로 이동
                    startActivity(new Intent(DoCertificateDetailActivity.this, DonationMainActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_mypage) {
                    // My Page 액티비티로 이동
                    startActivity(new Intent(DoCertificateDetailActivity.this, MyPageActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }
}