package com.example.greeningapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;


import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greeningapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderHistoryActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private RecyclerView parentRecyclerView;

    private ArrayList<MyOrder> parentModelArrayList;
    private RecyclerView.Adapter ParentAdapter;

    private RecyclerView.LayoutManager parentLayoutManager;

    private ImageButton back_orderhistory;

    //하단바 버튼
    private ImageButton navMain, navCategory, navDonation, navMypage;

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


        //주문내역 뒤로가기버튼
        back_orderhistory = findViewById(R.id.back_orderhistory);

        back_orderhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderHistoryActivity.this, OrderCompleteActivity.class);
                startActivity(intent);
            }
        });

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

                ParentAdapter = new OrderHistoryParentRcyAdapter(parentModelArrayList, OrderHistoryActivity.this);
                parentRecyclerView.setAdapter(ParentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("OrderHistoryActivity", String.valueOf(databaseError.toException()));
            }
        });

        // 하단바 아이콘 초기화
        navMain = findViewById(R.id.navMain_odhistory);
        navCategory = findViewById(R.id.navCategory_odhistory);
        navDonation = findViewById(R.id.navDonation_odhistory);
        navMypage = findViewById(R.id.navMypage_odhistory);

        // 각 아이콘 클릭 이벤트 처리
        navMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 홈 아이콘 클릭 시 처리할 내용
                Intent intent = new Intent(OrderHistoryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        navCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 카테고리 아이콘 클릭 시 처리할 내용
                Intent intent = new Intent(OrderHistoryActivity.this, CategoryActivity.class);
                startActivity(intent);
            }
        });

        navDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 기부 아이콘 클릭 시 처리할 내용
                Intent intent = new Intent(OrderHistoryActivity.this, DonationMainActivity.class);
                startActivity(intent);
            }
        });

        navMypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 마이페이지 아이콘 클릭 시 처리할 내용
                Intent intent = new Intent(OrderHistoryActivity.this, MyPageActivity.class);
                startActivity(intent);
            }
        });


    }
}