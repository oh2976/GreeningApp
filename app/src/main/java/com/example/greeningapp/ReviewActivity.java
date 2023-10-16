package com.example.greeningapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.greeningapp.DonationMainActivity;
import com.example.greeningapp.OrderHistoryActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
public class ReviewActivity extends AppCompatActivity {

    //전체리뷰
    private RecyclerView fullreviewrecyclerView;
    //    private RecyclerView.Adapter adapter;
    private ReviewAdapter reviewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Review> dataList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private ImageButton back_review;

    private int pid;


    //하단바
    private BottomNavigationView bottomNavigationView;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        //상단바
        toolbar = findViewById(R.id.review_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //전체리뷰
        fullreviewrecyclerView = findViewById(R.id.fullrecyclerView); //어디연결
        fullreviewrecyclerView.setHasFixedSize(true); //리사이클뷰 성능강화
        layoutManager = new LinearLayoutManager(this);
        fullreviewrecyclerView.setLayoutManager(layoutManager);
        dataList = new ArrayList<>(); //Product객체를 담을 ArrayList(어댑터쪽으로)

        database = FirebaseDatabase.getInstance(); //파이어베이스 연동
        databaseReference = database.getReference("Review");//db데이터연결


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("pid")) {
            pid = intent.getIntExtra("pid", 0);
            Log.d("pid",pid +"가져왔음");
        }

        // pid가 일치하는 상품 리뷰만 가져오기
        Query reviewQuery = databaseReference.orderByChild("pid").equalTo(pid);

        reviewQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터베이스의 데이터를 받아오는곳
                dataList.clear(); //기준 배열리스트가 존재하지않게 초기화(데이터가 쌓이기때문)
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { //반복문으로 데이터리스트 추출
                    Review review = snapshot.getValue(Review.class);  //만들어뒀던 review객체에 데이터를 담는다( 리뷰작성시 )
                    Log.d("pid",review.getRcontent() +"가져왔음");
                    dataList.add(review); //담은 데이터들을 배열리스트에 넣고 리사이클뷰로 보낼준비
                }
                reviewAdapter.notifyDataSetChanged(); //리스트저장 및 새로고침
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ReviewActivity", String.valueOf(databaseError.toException())); //에러문출력
            }
        });
        reviewAdapter = new ReviewAdapter(dataList, this);
        fullreviewrecyclerView.setAdapter(reviewAdapter);  //리사이클뷰에 어댑터연결

        // 파이어베이스 데이터베이스 참조 설정 (레이팅바 총점)
        FirebaseDatabase mRef = FirebaseDatabase.getInstance();
        DatabaseReference ratingsRef = mRef.getReference("Review");

        // 파이어베이스 데이터베이스에서 데이터 읽기(레이팅바 총점)
        ratingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float totalRating = 0;
                int ratingCount = 0;

                for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()) {
                    float rating = ratingSnapshot.child("rscore").getValue(Float.class);
                    totalRating += rating;
                    ratingCount++;
                }

                float averageRating = 0;
                if (ratingCount != 0) {
                    averageRating = totalRating / ratingCount;
                }
                //총점과 개수
                String formattedRating = String.format("%.2f (%d)", averageRating , ratingCount);

                TextView reviewRating = findViewById(R.id.value);
                reviewRating.setText(formattedRating);

                // 계산된 평점 값을 레이팅바에 표시
                RatingBar ratingBar = findViewById(R.id.reviewRating);
                float scaledRating = Math.round(averageRating * 5 / 5.0f);  // 평점 값을 5로 스케일링하고 소수점 자리 반올림
                ratingBar.setRating(scaledRating);

                // ratingBar.setRating(averageRating);

                //잠시 메인엑티비티 총점시도로 추가해봄
//                Intent intent = new Intent(ReviewActivity.this, MainActivity.class);
//                intent.putExtra("averageRating", averageRating); // 평점
//                intent.putExtra("ratingCount", ratingCount);     // 평점 수
//                startActivity(intent);



            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 처리 오류가 발생한 경우에 대한 예외 처리를 수행할 수 있습니다.
            }
        });

        // 하단바 구현
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation_review);
        // 초기 선택 항목 설정
        bottomNavigationView.setSelectedItemId(R.id.tab_shopping);

        // BottomNavigationView의 아이템 클릭 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.tab_home) {
                    // Home 액티비티로 이동
                    startActivity(new Intent(ReviewActivity.this, MainActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.tab_shopping) {
                    // Category 액티비티로 이동
                    startActivity(new Intent(ReviewActivity.this, CategoryActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.tab_donation) {
                    // Donation 액티비티로 이동
                    startActivity(new Intent(ReviewActivity.this, DonationMainActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.tab_mypage) {
                    // My Page 액티비티로 이동
                    startActivity(new Intent(ReviewActivity.this, MyPageActivity.class));
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