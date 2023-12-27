package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.ArrayList;

public class PointHistoryActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private PointHistoryAdapter adapter;
    private ArrayList<MyPoint> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_history);

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar_pointHistory);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 파이어베이스 설정
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // 리사이클러뷰 초기화
        recyclerView = findViewById(R.id.recyclerview_pointHistory);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        arrayList = new ArrayList<>();

        // 파이어베이스에서 씨드 내역 데이터 읽어오기
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            databaseReference = firebaseDatabase.getReference("CurrentUser")
                    .child(currentUserId)
                    .child("MyPoint");

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    arrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        MyPoint myPoint = dataSnapshot.getValue(MyPoint.class);
                        arrayList.add(myPoint);
                    }

                    // 적립 날짜 기준으로 내림차순 정렬
                    Collections.sort(arrayList, new Comparator<MyPoint>() {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        @Override
                        public int compare(MyPoint point1, MyPoint point2) {
                            try {
                                Date date1 = dateFormat.parse(point1.getPointDate());
                                Date date2 = dateFormat.parse(point2.getPointDate());
                                return date2.compareTo(date1);
                            } catch (Exception e) {
                                return 0;
                            }
                        }
                    });

                    // 어댑터 초기화 밑 리사이클러뷰에 어댑터 설정
                    adapter = new PointHistoryAdapter(PointHistoryActivity.this, arrayList);
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // 씨드 내역 데이터 로드 오류 시 에러 메세지 출력
                    Log.e("PointHistoryActivity", "데이터 로드 오류 : " + String.valueOf(databaseError.toException()));
                }
            });
        }
    }

    // 뒤로가기 처리
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}