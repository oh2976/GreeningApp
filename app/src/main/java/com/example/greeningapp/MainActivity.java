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
import android.widget.Toast;

import com.example.greeningapp.Product;
import com.example.greeningapp.DonationMainActivity;
import com.example.greeningapp.BuyNowActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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

    private FragmentStateAdapter pagerAdapter;
    private FragmentStateAdapter pagerAdapter01;
    private final int num_page = 3;    //큰 슬라이드 3개만.
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
    private DatabaseReference dreviewReference; //리뷰연동


    private TextView main_addbtn;

    //하단바
    private BottomNavigationView bottomNavigationView;

    //총점


    int pid;

    //큰 광고
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


        //상품목록
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_main);
        recyclerView.setHasFixedSize(true); //리사이클뷰 성능강화
        layoutManager = new LinearLayoutManager(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3); //가로 3개
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); //Product객체를 담을 ArrayList(어댑터쪽으로)

        database = FirebaseDatabase.getInstance();

        databaseReference = database.getReference("Product");

        dreviewReference =FirebaseDatabase.getInstance().getReference("Review");
        Query query = databaseReference.orderByChild("populstock").limitToLast(10); // populstock 내림차순 최대 10개상품가져옴

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터베이스의 데이터를 받아오는곳
                arrayList.clear(); //기준 배열리스트가 존재하지않게 초기화

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    arrayList.add(product);

//                    pid = product.getPid();
//                    Log.d("MainActivity", "pid = " + pid);

                    //arrayList를 populstock 내림차순 정렬, 값 없는건 pid순
                    Collections.sort(arrayList, new Comparator<Product>() {
                        @Override
                        public int compare(Product product1, Product product2) {
                            return Integer.compare(product2.getPopulstock(), product1.getPopulstock());
                        }
                    });



                    //인텐트코드 잠시추가 10.22
//                    Intent intent = getIntent();
//                    if (intent != null && intent.hasExtra("pid")) {
//                        pid = intent.getIntExtra("pid", 0);
//                        Log.d("pid02",pid +"가져왔음");
//                    }
//
//                    // pid가 일치하는 상품 평점만 가져오기
//                    Query mainscoreQuery = dreviewReference.orderByChild("pid").equalTo(pid);
//                    mainscoreQuery.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            float totalRating = 0;
//                            int ratingCount = 0;
//
//                            Log.d("MainActivity", pid + " = " + dataSnapshot);
//
//                            for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()) {
//                                float rating = ratingSnapshot.child("rscore").getValue(Float.class);
////                                Review review = new Review();
////                                float rscore = review.getRscore();
////                                Log.d("rscore " + pid, "rscore: " + rscore);
//                                totalRating += rating;
//                                ratingCount++;
//                            }
//
//                            float averageRating = 0;
//                            if (ratingCount != 0) {
//                                averageRating = totalRating / ratingCount;
//                            }
//
//                            //총점 개수
//                            String formattedRating = String.format("(%d)",  ratingCount);
//
//                            TextView mainRating = findViewById(R.id.star);
//                            mainRating.setText(formattedRating);
//
//                            // 계산된 평점 값을 레이팅바에 표시
//                            RatingBar ratingBar = findViewById(R.id.reviewRating01);
//                            float scaledRating = Math.round(averageRating * 5 / 5.0f);  // 평점 값을 5로 스케일링하고 소수점 자리 반올림
//                            ratingBar.setRating(scaledRating);
//
//
//                            DatabaseReference reviewpid = dreviewReference.child(String.valueOf(pid)).child("pscore");
//                            reviewpid.setValue(averageRating);
//
//                        }
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                            // 처리 오류가 발생한 경우에 대한 예외 처리를 수행할 수 있습니다.
//                        }
//                    });


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
        recyclerView.setAdapter(adapter);


        //큰 광고
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

        //작은 광고
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

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() > backpressedTime + 2000) {
            backpressedTime = System.currentTimeMillis();
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() <= backpressedTime + 2000) {
            finish();
        }

    }



}