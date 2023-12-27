package com.example.greeningapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;
import java.text.DecimalFormat;

public class ReviewHistoryActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    FirebaseDatabase database;
    private RecyclerView recyclerView;
    private ArrayList<Review> reviewhistoryList;
    private ReviewHistoryAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference databaseReference2;
    private String idToken;
    private BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_history);

        // 툴바 설정
        toolbar = findViewById(R.id.ReviewHistoryToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        recyclerView = findViewById(R.id.reviewHisyoryRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        layoutManager = new LinearLayoutManager(this);
        reviewhistoryList = new ArrayList<>();

        // 파이어베이스 설정
        database = FirebaseDatabase.getInstance(); //파이어베이스 연동
        databaseReference = database.getReference("Review"); // Firebase Realtime Database에서 "Review" 항목을 가져오기
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference2 = FirebaseDatabase.getInstance().getReference("User");

        databaseReference2.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    idToken = user.getIdToken(); // user idToken 가져오기

                    Query reviewhistoryQuery = databaseReference.orderByChild("idToken").equalTo(idToken); // idToken을 사용하여 본인이 쓴 후기 가져오기
                    reviewhistoryQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            reviewhistoryList.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Review review = snapshot.getValue(Review.class);
                                reviewhistoryList.add(review);
                                Log.d("useridtoken", review.getIdToken() + "가져왔음");
                            }
                            adapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("ReviewHistoryActivity", String.valueOf(databaseError.toException())); // 에러 메시지 출력
                        }
                    });
                    // 어댑터 초기화, 어댑터 설정
                    adapter = new ReviewHistoryAdapter(reviewhistoryList, ReviewHistoryActivity.this);
                    recyclerView.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // 하단바 설정
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation_reviewhistory);
        bottomNavigationView.setSelectedItemId(R.id.tab_mypage);

        // 하단바 아이템 클릭
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.tab_home) {
                    // Home 액티비티로 이동
                    startActivity(new Intent(ReviewHistoryActivity.this, MainActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.tab_shopping) {
                    // Category 액티비티로 이동
                    startActivity(new Intent(ReviewHistoryActivity.this, CategoryActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.tab_donation) {
                    // Donation 액티비티로 이동
                    startActivity(new Intent(ReviewHistoryActivity.this, DonationMainActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.tab_mypage) {
                    // MyPage 액티비티로 이동
                    startActivity(new Intent(ReviewHistoryActivity.this, MyPageActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    // 뒤로가기
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}