package com.example.greeningapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greeningapp.CategoryActivity;
import com.example.greeningapp.DonationMainActivity;
import com.example.greeningapp.MainActivity;
import com.example.greeningapp.MyPageActivity;
import com.example.greeningapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class OrderHistoryActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private RecyclerView parentRecyclerView;

    private ArrayList<MyOrder> parentModelArrayList;
    private RecyclerView.Adapter ParentAdapter;

    private RecyclerView.LayoutManager parentLayoutManager;

    //하단바
    private BottomNavigationView bottomNavigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("CurrentUser").child(firebaseUser.getUid()).child("MyOrder");

        parentRecyclerView = findViewById(R.id.Parent_recyclerView);
        parentRecyclerView.setHasFixedSize(true);
        parentLayoutManager = new LinearLayoutManager(this);
        parentRecyclerView.setLayoutManager(parentLayoutManager);

        //상단바
        toolbar = findViewById(R.id.ordh_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);



        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                parentModelArrayList = new ArrayList<>();
                parentModelArrayList.clear();

                for (DataSnapshot parentSnapshot : dataSnapshot.getChildren()) {
                    ArrayList<MyOrder> childModelArrayList = new ArrayList<>();

                    for (DataSnapshot childSnapshot : parentSnapshot.getChildren()) {
                        MyOrder childOrder = childSnapshot.getValue(MyOrder.class);
                        childModelArrayList.add(childOrder);
                    }

                    if (!childModelArrayList.isEmpty()) {
                        MyOrder parentOrder = childModelArrayList.get(0);
                        parentOrder.setChildModelArrayList(childModelArrayList);
                        parentModelArrayList.add(parentOrder);
                    }
                }


                Collections.sort(parentModelArrayList, new Comparator<MyOrder>() {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    @Override
                    public int compare(MyOrder myOrder1, MyOrder myOrder2) {
                        try {
                            Date date1 = dateFormat.parse(myOrder1.getOrderDate());
                            Date date2 = dateFormat.parse(myOrder2.getOrderDate());
                            return date2.compareTo(date1);
                        } catch (Exception e) {
                            return 0;
                        }
                    }
                });


//                ParentAdapter = new OrderHistoryParentRcyAdapter(new ArrayList<>(), OrderHistoryActivity.this);
                ParentAdapter = new OrderHistoryParentRcyAdapter(parentModelArrayList, OrderHistoryActivity.this);
                parentRecyclerView.setAdapter(ParentAdapter);
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("OrderHistoryActivity", String.valueOf(databaseError.toException()));
            }
        });

        // 하단바 구현
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation_orderhistory);
        // 초기 선택 항목 설정
        bottomNavigationView.setSelectedItemId(R.id.tab_mypage);

        // BottomNavigationView의 아이템 클릭 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.tab_home) {
                    // Home 액티비티로 이동
                    startActivity(new Intent(OrderHistoryActivity.this, MainActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.tab_shopping) {
                    // Category 액티비티로 이동
                    startActivity(new Intent(OrderHistoryActivity.this, CategoryActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.tab_donation) {
                    // Donation 액티비티로 이동
                    startActivity(new Intent(OrderHistoryActivity.this, DonationMainActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.tab_mypage) {
                    // My Page 액티비티로 이동
                    startActivity(new Intent(OrderHistoryActivity.this, MyPageActivity.class));
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