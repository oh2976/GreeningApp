package com.example.greeningapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

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
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Review> dataList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ImageButton back_review;

    //하단바 버튼
    private ImageButton navMain, navCategory, navDonation, navMypage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);


        back_review = findViewById(R.id.back_review);

        back_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewActivity.this, OrderHistoryActivity.class);
                startActivity(intent);
            }
        });

        //전체리뷰
        fullreviewrecyclerView = findViewById(R.id.fullrecyclerView); //어디연결
        fullreviewrecyclerView.setHasFixedSize(true); //리사이클뷰 성능강화
        layoutManager = new LinearLayoutManager(this);
        fullreviewrecyclerView.setLayoutManager(layoutManager);
        dataList = new ArrayList<>(); //Product객체를 담을 ArrayList(어댑터쪽으로)

        database = FirebaseDatabase.getInstance(); //파이어베이스 연동
        databaseReference = database.getReference("Review");//db데이터연결

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터베이스의 데이터를 받아오는곳
                dataList.clear(); //기준 배열리스트가 존재하지않게 초기화(데이터가 쌓이기때문)
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { //반복문으로 데이터리스트 추출
                    Review review = snapshot.getValue(Review.class);  //만들어뒀던 review객체에 데이터를 담는다( 리뷰작성시 )
                    dataList.add(review); //담은 데이터들을 배열리스트에 넣고 리사이클뷰로 보낼준비
                }
                adapter.notifyDataSetChanged(); //리스트저장 및 새로고침
                //db가져오던중 에러발생시
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ReviewActivity", String.valueOf(databaseError.toException())); //에러문출력
            }
        });
        adapter = new ReviewAdapter(dataList, this);
        fullreviewrecyclerView.setAdapter(adapter);  //리사이클뷰에 어댑터연결

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
                    float rating = ratingSnapshot.child("Rating").getValue(Float.class);
                    totalRating += rating;
                    ratingCount++;
                }

                float averageRating = 0;
                if (ratingCount != 0) {
                    averageRating = totalRating / ratingCount;
                }
                String formattedRating = String.format("%.2f", averageRating);

                TextView reviewRating = findViewById(R.id.value);
                reviewRating.setText(formattedRating);

                // 계산된 평점 값을 레이팅바에 표시
                RatingBar ratingBar = findViewById(R.id.reviewRating);
                float scaledRating = Math.round(averageRating * 5 / 5.0f);  // 평점 값을 5로 스케일링하고 소수점 자리 반올림
                ratingBar.setRating(scaledRating);
                // ratingBar.setRating(averageRating);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 처리 오류가 발생한 경우에 대한 예외 처리를 수행할 수 있습니다.
            }
        });

        // 하단바 아이콘 초기화
        navMain = findViewById(R.id.navMain_review);
        navCategory = findViewById(R.id.navCategory_review);
        navDonation = findViewById(R.id.navDonation_review);
        navMypage = findViewById(R.id.navMypage_review);

        // 각 아이콘 클릭 이벤트 처리
        navMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 홈 아이콘 클릭 시 처리할 내용
                Intent intent = new Intent(ReviewActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        navCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 카테고리 아이콘 클릭 시 처리할 내용
                Intent intent = new Intent(ReviewActivity.this, CategoryActivity.class);
                startActivity(intent);
            }
        });

        navDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 기부 아이콘 클릭 시 처리할 내용
                Intent intent = new Intent(ReviewActivity.this, DonationMainActivity.class);
                startActivity(intent);
            }
        });

        navMypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 마이페이지 아이콘 클릭 시 처리할 내용
                Intent intent = new Intent(ReviewActivity.this, MyPageActivity.class);
                startActivity(intent);
            }
        });
    }

}