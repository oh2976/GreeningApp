package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import me.relex.circleindicator.CircleIndicator3;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends FragmentActivity {
    private long backpressedTime = 0;

    private ViewPager2 mPager;     //큰 광고
    private ViewPager2 mPager01;   //작은 광고

    //광고 타이머
    private int currentPage = 0;
    private int currentPage01 = 0;
    private final long DELAY_MS = 3000; // 광고 자동 넘길 시간 간격(3초)
    private final long PERIOD_MS = 3000; // 타이머(3초)

    private final int num_page = 3;    //큰 광고 3개
    private final int num_page01 = 3;  //작은 광고 3개

    //광고 인디케이터
    private CircleIndicator3 mIndicator;
    private CircleIndicator3 mIndicator01;
    //상품목록
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private ArrayList<Product> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private TextView main_addbtn;
    //하단바
    private BottomNavigationView bottomNavigationView;

    //큰 광고
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPager = findViewById(R.id.viewpager);
        mPager01 = findViewById(R.id.viewpager01);

        FragmentStateAdapter pagerAdapter = new MainAdapter(this, num_page);
        mPager.setAdapter(pagerAdapter);

        FragmentStateAdapter pagerAdapter01 = new MainAdapter01(this, num_page01);
        mPager01.setAdapter(pagerAdapter01);

        // Indicator 초기화 및 설정
        mIndicator = findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);     //indicator와 ViewPager이 연동됨
        mIndicator.createIndicators(num_page, 0);

        mIndicator01 = findViewById(R.id.indicator01);
        mIndicator01.setViewPager(mPager01);     //indicator와 ViewPager01이 연동됨
        mIndicator01.createIndicators(num_page01, 0);

        // ViewPager(큰 광고) 설정
        mPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);  //슬라이드방향(수평)
        mPager.setCurrentItem(1000);           // 시작 지점
        mPager.setOffscreenPageLimit(2);       // 최대 이미지 수

        // ViewPager01(작은 광고) 설정
        mPager01.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        mPager01.setCurrentItem(1000);
        mPager01.setOffscreenPageLimit(2);

        // 타이머 설정(큰광고)
        final Handler handler = new Handler(Looper.getMainLooper());
        //큰광고 타이머
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

        // 타이머 설정(작은 광고)
        final Handler handler01 = new Handler(Looper.getMainLooper());
        //작은광고 타이머
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


        //인기 상품목록
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_main);
        recyclerView.setHasFixedSize(true);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3); //가로 3개씩
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        //Product 파이어베이스 연동
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Product");

        // populstock 내림차순 최대 10개상품만 가져옴
        Query query = databaseReference.orderByChild("populstock").limitToLast(10);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    arrayList.add(product);

                    //arrayList를 populstock 내림차순으로 정렬
                    Collections.sort(arrayList, new Comparator<Product>() {
                        @Override
                        public int compare(Product product1, Product product2) {
                            return Integer.compare(product2.getPopulstock(), product1.getPopulstock());
                        }
                    });

                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", String.valueOf(databaseError.toException()));
            }
        });
        adapter = new MainProductAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);


        //큰 광고
        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                //큰 광고페이지 완전히 스크롤될시 현재 페이지를 설정
                if (positionOffsetPixels == 0) {
                    mPager.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //페이지가 설정될떄마다 인디케이터 변경
                mIndicator.animatePageSelected(position % num_page);
            }
        });

        //작은 광고
        mPager01.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                //작은 광고페이지 완전히 스크롤될시 현재 페이지를 설정
                if (positionOffsetPixels == 0) {
                    mPager01.setCurrentItem(position);
                }
            }
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //페이지가 설정될떄마다 인디케이터 변경
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

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backpressedTime + 2000) {
            backpressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() <= backpressedTime + 2000) {
            super.onBackPressed();
            finish();
        }

    }
}