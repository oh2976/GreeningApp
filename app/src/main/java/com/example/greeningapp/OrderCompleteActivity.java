package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class OrderCompleteActivity extends AppCompatActivity {

    private String orderId, myOrderId;

    private TextView cmpOrderId, cmpOrderDate, cmp_totalPrice, cmp_name, cmp_phone, cmp_address, cmp_postcode;

    private Button btnGoMain;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;

    private OrderCompleteAdapter orderCompleteAdapter;
    private List<MyOrder> myOrderList;

    private ImageButton navMain, navCategory, navDonation, navMypage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_complete);

        // 데이터베이스 연동 변수 처리
        databaseReference = FirebaseDatabase.getInstance().getReference("CurrentUser");
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        // orderActivity에서 보낸 결제 데이터베이스 id 가져오기
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        myOrderId = intent.getStringExtra("myOrderId");
        Log.d("OrderCompleteActivity", orderId);

        recyclerView = findViewById(R.id.recyclerView_orderComplete);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        cmpOrderId = findViewById(R.id.cmpOrderId);
        cmpOrderDate = findViewById(R.id.cmpOrderDate);
        cmp_totalPrice = findViewById(R.id.cmp_totalPrice);
        cmp_name = findViewById(R.id.cmp_name);
        cmp_phone = findViewById(R.id.cmp_phone);
        cmp_address = findViewById(R.id.cmp_address);
        cmp_postcode = (TextView) findViewById(R.id.cmp_postcode);

        myOrderList = new ArrayList<>();

        // MyOrder에 있는 결제 정보 데이터베이스 가져와서 화면에 뿌리기
        databaseReference.child(firebaseUser.getUid()).child("MyOrder").child(myOrderId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        MyOrder myOrder = dataSnapshot.getValue(MyOrder.class);

                        myOrderList.add(myOrder);
                        orderCompleteAdapter.notifyDataSetChanged();

                        cmpOrderId.setText(myOrder.getOrderId()+"");
                        cmpOrderDate.setText(myOrder.getOrderDate());
                        cmp_totalPrice.setText(String.valueOf(myOrder.getOverTotalPrice()));
                        cmp_name.setText(myOrder.getUserName());
                        cmp_phone.setText(myOrder.getPhone());
                        cmp_address.setText(myOrder.getAddress());
                        cmp_postcode.setText(myOrder.getPostcode());
                    }

                }
            }
        });

        // 리사이클러 뷰와 연결
        orderCompleteAdapter = new OrderCompleteAdapter(this, myOrderList);
        recyclerView.setAdapter(orderCompleteAdapter);

        btnGoMain = (Button) findViewById(R.id.btnGoMain);

        // 메인으로 가기 누르면 ShoppingMainActivity로 이동
        btnGoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderCompleteActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        navMain = findViewById(R.id.navMain_orderComple);
        navCategory = findViewById(R.id.navCategory_orderComple);
        navDonation = findViewById(R.id.navDonation_orderComple);
        navMypage = findViewById(R.id.navMypage_orderComple);

        // 각 아이콘 클릭 이벤트 처리
        navMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 홈 아이콘 클릭 시 처리할 내용
                Intent intent = new Intent(OrderCompleteActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        navCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 카테고리 아이콘 클릭 시 처리할 내용
                Intent intent = new Intent(OrderCompleteActivity.this, CategoryActivity.class);
                startActivity(intent);
            }
        });

        navDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 기부 아이콘 클릭 시 처리할 내용
                Intent intent = new Intent(OrderCompleteActivity.this, DonationMainActivity.class);
                startActivity(intent);
            }
        });

        navMypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 마이페이지 아이콘 클릭 시 처리할 내용
                Intent intent = new Intent(OrderCompleteActivity.this, MyPageActivity.class);
                startActivity(intent);
            }
        });



    }
}