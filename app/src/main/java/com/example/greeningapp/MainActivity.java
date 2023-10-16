package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import me.relex.circleindicator.CircleIndicator3;

//상품진열
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.greeningapp.Product;
import com.example.greeningapp.DonationMainActivity;
import com.example.greeningapp.BuyNowActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends FragmentActivity {

    private ViewPager2 mPager;
    private ViewPager2 mPager01;   //01붙은거는 슬라이드2변수
    //광고 타이머
    private int currentPage = 0;
    private int currentPage01 = 0;
    private final long DELAY_MS = 3000; // 광고를 자동으로 넘길 시간 간격 (3초)
    private final long PERIOD_MS = 3000; // 타이머 주기 (3초)

    private FragmentStateAdapter pagerAdapter;
    private FragmentStateAdapter pagerAdapter01;
    private final int num_page = 3;    //viewpager2에 4개의 페이지가 표시됨.
    private final int num_page01 = 3;
    private CircleIndicator3 mIndicator;
    private CircleIndicator3 mIndicator01;
    //상품목록
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Product> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private TextView main_addbtn;

    //하단바
    private BottomNavigationView bottomNavigationView;



    //슬라이드1 화면
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ViewPager2
        mPager = findViewById(R.id.viewpager);
        mPager01 = findViewById(R.id.viewpager01);
        // Adapter
        FragmentStateAdapter pagerAdapter = new MainAdapter(this, num_page);
        mPager.setAdapter(pagerAdapter);

        FragmentStateAdapter pagerAdapter01 = new MainAdapter01(this, num_page01);
        mPager01.setAdapter(pagerAdapter01);

        // Indicator 초기화 및 설정
        mIndicator = findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);     //indicator와 ViewPager2이 연동됨
        mIndicator.createIndicators(num_page, 0);

        mIndicator01 = findViewById(R.id.indicator01);
        mIndicator01.setViewPager(mPager01);     //indicator와 ViewPager2이 연동됨
        mIndicator01.createIndicators(num_page01, 0);
        // ViewPager2 설정
        mPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);  //슬라이드방향(수평)
        mPager.setCurrentItem(1000);           // 시작 지점
        mPager.setOffscreenPageLimit(2);       // 최대 이미지 수

        mPager01.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        mPager01.setCurrentItem(1000);           // 시작 지점
        mPager01.setOffscreenPageLimit(2);

        // 광고 타이머 설정(베너1)
        final Handler handler = new Handler(Looper.getMainLooper());
        //베너 01 타이머
        final Runnable update = new Runnable() {
            public void run() {
                if (currentPage == pagerAdapter.getItemCount()) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++);
            }
        };
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, DELAY_MS, PERIOD_MS);

        // 광고 타이머 설정(베너1)
        final Handler handler01 = new Handler(Looper.getMainLooper());
        //베너 02 타이머
        final Runnable update01 = new Runnable() {
            public void run() {
                if (currentPage01 == pagerAdapter01.getItemCount()) {
                    currentPage01 = 0;
                }
                mPager01.setCurrentItem(currentPage01++);
            }
        };
        Timer timer01 = new Timer();
        timer01.schedule(new TimerTask() {
            @Override
            public void run() {
                handler01.post(update01);
            }
        }, DELAY_MS, PERIOD_MS);


        //상품목록
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_main);
        recyclerView.setHasFixedSize(true); //리사이클뷰 성능강화
        layoutManager = new LinearLayoutManager(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3); //가로 3개
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); //Product객체를 담을 ArrayList(어댑터쪽으로)

        database = FirebaseDatabase.getInstance(); //파이어베이스 연동

        databaseReference = database.getReference("Product");//db데이터연결
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터베이스의 데이터를 받아오는곳
                arrayList.clear(); //기준 배열리스트가 존재하지않게 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { //반복문으로 데이터리스트 추출
                    Product product = snapshot.getValue(Product.class);  //만들어뒀던 Product에 데이터를 담음
                    arrayList.add(product); //담은 데이터들을 배열리스트에 넣고 리사이클뷰로 보낼준비
//                    tv_pprice.setText(String.valueOf(decimalFormat.format(product.getPprice())) + "원");

                }
                adapter.notifyDataSetChanged(); //리스트저장 및 새로고침
                //db가져오던중 에러발생시
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", String.valueOf(databaseError.toException()));
            }
        });
        adapter = new MainProductAdapter(arrayList, this);
//        adapter = new ProductAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);  //리사이클뷰에 어댑터연결



        // 리뷰엑티비티 인텐트 잠시 시도 (레이팅바 총점)
//        float averageRating = getIntent().getFloatExtra("averageRating", 0);
//        int ratingCount = getIntent().getIntExtra("ratingCount", 0);








        //페이지 변경 이벤트 리스너 등록
        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    mPager.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mIndicator.animatePageSelected(position % num_page);
            }
        });

        //슬라이드2
        mPager01.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    mPager01.setCurrentItem(position);
                }
            }
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mIndicator01.animatePageSelected(position % num_page01);
            }
        });

        //상품더보기 버튼
        main_addbtn =  findViewById(R.id.main_addbtn);
        main_addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                startActivity(intent);
            }
        });

        // 하단바 구현
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation_main);
        // 초기 선택 항목 설정
        bottomNavigationView.setSelectedItemId(R.id.tab_home);

        // BottomNavigationView의 아이템 클릭 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.tab_home) {
                    // Home 액티비티로 이동
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.tab_shopping) {
                    // Category 액티비티로 이동
                    startActivity(new Intent(MainActivity.this, CategoryActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.tab_donation) {
                    // Donation 액티비티로 이동
                    startActivity(new Intent(MainActivity.this, DonationMainActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.tab_mypage) {
                    // My Page 액티비티로 이동
                    startActivity(new Intent(MainActivity.this, MyPageActivity.class));
                    return true;
                }
                return false;
            }
        });

    }
}