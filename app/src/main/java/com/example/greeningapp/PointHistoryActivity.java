package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        Toolbar toolbar = findViewById(R.id.toolbar_pointHistory);
        setSupportActionBar(toolbar);    // 액티비티의 앱바로 지정
        ActionBar actionBar = getSupportActionBar();    // 앱바 제어를 위해 툴바 액세스
        actionBar.setTitle("");    // 툴바 제목 설정
        actionBar.setDisplayHomeAsUpEnabled(true);    // 앱바에 뒤로가기 버튼 만들기

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerview_pointHistory);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        arrayList = new ArrayList<>();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // 현재 로그인된 사용자의 UID 가져오기
            String currentUserId = currentUser.getUid();

            // MyPoint 노드에서 현재 사용자의 UID를 기반으로 데이터 조회
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
                    adapter = new PointHistoryAdapter(PointHistoryActivity.this, arrayList);
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // DB를 가져오던 중 에러 발생 시
                    Log.e("PointHistoryActivity", String.valueOf(databaseError.toException()));    // 에러문 출력
                }
            });
        }
    }
}