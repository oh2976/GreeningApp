package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
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
import java.util.ArrayList;

public class DonationMainActivity extends AppCompatActivity {

//    private RecyclerView recyclerView;
//    private RecyclerView.Adapter adapter;
//    private RecyclerView.LayoutManager layoutManager;
//    private ArrayList<Donation> arrayList;
    private FirebaseDatabase firebaseDatabase;
//    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference2;

    private FirebaseAuth firebaseAuth;

    TextView donationPoint;

    TabLayout tab_donate;
    ViewPager viewPager_donate;
    Toolbar toolbar;

    private BottomNavigationView bottomNavigationView;


    private ImageButton navMain, navCategory, navDonation, navMypage;


    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_main);

        toolbar = findViewById(R.id.toolbar_donation);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목 삭제.
        actionBar.setDisplayHomeAsUpEnabled(true);

//        recyclerView = findViewById(R.id.donationRecyclerView);
//        recyclerView.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);

//        arrayList = new ArrayList<>();

//        firebaseDatabase = FirebaseDatabase.getInstance();
//        databaseReference = firebaseDatabase.getReference("Donation");

//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
//                arrayList.clear();
//                for(DataSnapshot snapshot : datasnapshot.getChildren()){
//                    Donation donation = snapshot.getValue(Donation.class);
//                    arrayList.add(donation);
//                    Log.d("DonationMainActivity", snapshot.getKey()+"");
//
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.d("DonationMainActivity", String.valueOf(error.toException()));
//
//            }
//        });
//
//        adapter = new DonationAdapter(arrayList, this);
//        recyclerView.setAdapter(adapter);
//
        donationPoint = (TextView) findViewById(R.id.donation_point);


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        databaseReference2 = FirebaseDatabase.getInstance().getReference("User");

        databaseReference2.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                // 회원 정보 테이블에서 sPoint 데이터 가져와서 뿌리기

                User user = dataSnapshot.getValue(User.class); //  만들어 뒀던 Product 객체에 데이터를 담는다.
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
                    return true;
                } else if (item.getItemId() == R.id.tab_shopping) {
                    // Category 액티비티로 이동
                    startActivity(new Intent(DonationMainActivity.this, CategoryActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.tab_donation) {
                    // Donation 액티비티로 이동
                    startActivity(new Intent(DonationMainActivity.this, DonationMainActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.tab_mypage) {
                    // My Page 액티비티로 이동
                    startActivity(new Intent(DonationMainActivity.this, MyPageActivity.class));
                    return true;
                }
                return false;
            }
        });




//        navMain = findViewById(R.id.navMain_doMain);
//        navCategory = findViewById(R.id.navCategory_doMain);
//        navDonation = findViewById(R.id.navDonation_doMain);
//        navMypage = findViewById(R.id.navMypage_doMain);

//        // 각 아이콘 클릭 이벤트 처리
//        navMain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 홈 아이콘 클릭 시 처리할 내용
//                Intent intent = new Intent(DonationMainActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        navCategory.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 카테고리 아이콘 클릭 시 처리할 내용
//                Intent intent = new Intent(DonationMainActivity.this, CategoryActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        navDonation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 기부 아이콘 클릭 시 처리할 내용
//                Intent intent = new Intent(DonationMainActivity.this, DonationMainActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        navMypage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 마이페이지 아이콘 클릭 시 처리할 내용
//                Intent intent = new Intent(DonationMainActivity.this, MyPageActivity.class);
//                startActivity(intent);
//            }
//        });

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