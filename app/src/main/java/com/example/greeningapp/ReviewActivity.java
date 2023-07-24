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
//    private RecyclerView.Adapter adapter;
    private ReviewAdapter reviewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Review> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private int pid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);


        //버튼클릭 -> 리뷰 쓰기 페이지로 이동
        Button button = findViewById(R.id.button);    //터치x

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewActivity.this, ReviewWriteActivity.class);         //터치 x
                startActivity(intent);
            }
        });

        //전체리뷰
        fullreviewrecyclerView = findViewById(R.id.fullrecyclerView); //어디연결
        fullreviewrecyclerView.setHasFixedSize(true); //리사이클뷰 성능강화
        layoutManager = new LinearLayoutManager(this);
        fullreviewrecyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); //Product객체를 담을 ArrayList(어댑터쪽으로)

        database = FirebaseDatabase.getInstance(); //파이어베이스 연동

        databaseReference = database.getReference("Review");//db데이터연결

        // 상품 상세 페이지에서 인텐트를 통해서 전달한 상품Id 가져오기
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("pid")) {
            pid = intent.getIntExtra("pid", 0);
        }

        // pid가 일치하는 상품 리뷰만 가져오기
        Query reviewQuery = database.getReference("Review").orderByChild("pid").equalTo(pid);

        reviewQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // 데이터베이스에서 리뷰 객체 가져오기
                    Review review = snapshot.getValue(Review.class);
                    arrayList.add(review);
                }
                reviewAdapter.notifyDataSetChanged();    // 어댑터에 데이터 변경 알림
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 조회 중 에러 발생 시
                Log.e("ReviewActivity", databaseError.getMessage());
            }
        });

        reviewAdapter = new ReviewAdapter(arrayList, this);
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





    }



}