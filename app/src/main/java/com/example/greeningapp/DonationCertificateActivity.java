package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DonationCertificateActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Donation> donationArrayList;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_certificate);

        // 툴바
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        // 기부 증명서 내역 리사이클러뷰 설정
        recyclerView = (RecyclerView) findViewById(R.id.doCertiRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // 기부 프로젝트 정보 list 객체 생성
        donationArrayList = new ArrayList<>();

        // 파이어베이스 경로 설정
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Donation");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                donationArrayList.clear();

                long currentTimeMillis = System.currentTimeMillis();

                for(DataSnapshot snapshot : datasnapshot.getChildren()){
                    Donation donation = snapshot.getValue(Donation.class);

                    // 기부 시작 날짜와 끝나는 날짜 가져오기
                    String donationStartDateString = donation.getDonationstart();
                    String donationEndDateString = donation.getDonationend();

                    // SimpleDateFormat을 사용하여 문자열을 Date 객체로 변환
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    try {
                        Date donationStartDate = dateFormat.parse(donationStartDateString);
                        Date donationEndDate = dateFormat.parse(donationEndDateString);

                        // 기부 시작 시간 및 끝나는 시간 가져오기
                        long donationStartTime = donationStartDate.getTime();
                        long donationEndTime = donationEndDate.getTime();

                        if (!(currentTimeMillis >= donationStartTime && currentTimeMillis <= donationEndTime)) {
                            // 기부 가능한 시간인 경우에만 리스트에 추가
                            donationArrayList.add(donation);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 오류 처리
            }
        });

        // 기부 증명서 어댑터 생성 후 리사이클러뷰에 어댑터 연결
        adapter = new CertificateAdapter(donationArrayList, this);
        recyclerView.setAdapter(adapter);
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