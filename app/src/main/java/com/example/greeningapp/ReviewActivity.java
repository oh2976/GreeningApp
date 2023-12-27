package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity {
    private RecyclerView fullreviewrecyclerView;
    private ReviewAdapter reviewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Review> dataList;
    private DatabaseReference databaseReference;
    private int pid;
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

        fullreviewrecyclerView = findViewById(R.id.fullrecyclerView);
        fullreviewrecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        fullreviewrecyclerView.setLayoutManager(layoutManager);
        dataList = new ArrayList<>();

        //파이어베이스 Review연동
        databaseReference =FirebaseDatabase.getInstance().getReference("Review");

        //pid키에 해당하는 데이터 읽어옴
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("pid")) {
            pid = intent.getIntExtra("pid", 0);
            Log.d("pid",pid +"가져왔음");
        }

        //pid와 일치한 데이터를 쿼리하고 어댑터 설정
        Query reviewQuery = databaseReference.orderByChild("pid").equalTo(pid);
        reviewQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataList.clear();
                // 데이터 스냅샷을 돌면서 리뷰 데이터를 읽어와 리스트에 추가
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Review review = snapshot.getValue(Review.class);
                    dataList.add(review);
                }
                reviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ReviewActivity", "데이터 가져오기 실패: " + databaseError.getMessage());
            }
        });

        reviewAdapter = new ReviewAdapter(dataList, FirebaseAuth.getInstance(), FirebaseDatabase.getInstance().getReference("User"));
        fullreviewrecyclerView.setAdapter(reviewAdapter);

        // 파이어베이스 레이팅바 총점계산
        reviewQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //초기값 0으로 설정
                float totalRating = 0;  //총점
                int ratingCount = 0;    //평점 개수

                // 데이터 스냅샷을 돌면서 총 평점과 평점 개수를 계산
                for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()) {
                    float rating = ratingSnapshot.child("rscore").getValue(Float.class);
                    totalRating += rating;
                    ratingCount++;
                }

                float averageRating = 0;   // 평균 평점 초기화

                // 평점이 존재하는 경우, 총 평점을 평점 개수로 나누어 평균 평점 계산
                if (ratingCount != 0) {
                    averageRating = totalRating / ratingCount;
                }

                //총점과 개수를 문자열 형식으로 바꿔줌
                String formattedRating = String.format("%.2f (%d)", averageRating , ratingCount);
                TextView reviewRating = findViewById(R.id.value);
                reviewRating.setText(formattedRating);

                // 계산된 평점 값을 레이팅바에 표시
                RatingBar ratingBar = findViewById(R.id.reviewRating);
                float scaledRating = Math.round(averageRating * 5 / 5.0f);  // 평점 값을 5로 스케일링하고 소수점 자리 반올림
                ratingBar.setRating(scaledRating);


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // 하단바
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation_review);
        // 초기 선택 항목 설정
        bottomNavigationView.setSelectedItemId(R.id.tab_shopping);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.tab_home) {
                    // Home 액티비티로 이동
                    startActivity(new Intent(ReviewActivity.this, MainActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_shopping) {
                    // Category 액티비티로 이동
                    startActivity(new Intent(ReviewActivity.this, CategoryActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_donation) {
                    // Donation 액티비티로 이동
                    startActivity(new Intent(ReviewActivity.this, DonationMainActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_mypage) {
                    // My Page 액티비티로 이동
                    startActivity(new Intent(ReviewActivity.this, MyPageActivity.class));
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