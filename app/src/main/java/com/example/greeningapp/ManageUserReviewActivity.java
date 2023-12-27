package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ManageUserReviewActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Review> arrayList;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    Button btnManageMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user_review);

        // 툴바
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        recyclerView = findViewById(R.id.recyclerView); // 리사이클러뷰 레이아웃 연결
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존 성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        arrayList = new ArrayList<>(); // Review 객체를 담을 어레이리스트(어댑터 쪽으로 날릴 거임)

        // 파이어베이스 경로 지정
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Review");

        // 회원 후기 데이터베이스 가져오기
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                arrayList.clear(); //기존 배열 리스트가 존재하지 않게 남아 있는 데이터 초기화
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    // 반복문으로 데이터 List를 추출해냄
                    Review review = snapshot.getValue(Review.class); //  만들어 뒀던 Review 객체에 데이터를 담는다.
                    arrayList.add(review); // 담은 데이터들을 배열 리스트에 넣고 리사이클러뷰로 보낼 준비
                    Log.d("ManageUserReviewActivity", snapshot.getKey()+"");

                }

                // 후기 작성 일자가 최신순으로 데이터 정렬 처리
                Collections.sort(arrayList, new Comparator<Review>() {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    @Override
                    public int compare(Review review1, Review review2) {
                        try {
                            Date date1 = dateFormat.parse(review1.getRdatetime());
                            Date date2 = dateFormat.parse(review2.getRdatetime());
                            return date2.compareTo(date1);
                        } catch (Exception e) {
                            return 0;
                        }
                    }
                });
                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 디비를 가져오던 중 에러 발생 시
                Log.e("ManageUserActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        // 관리자 회원 후기 어댑터 생성 후 리사이클러뷰에 어댑터 연결
        adapter = new ManageUserReviewAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);

        // 관리자 메인으로 이동
        btnManageMain = (Button) findViewById(R.id.btnManageMain_Review);
        btnManageMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageUserReviewActivity.this, ManagerMainActivity.class);
                startActivity(intent);
                finish();
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