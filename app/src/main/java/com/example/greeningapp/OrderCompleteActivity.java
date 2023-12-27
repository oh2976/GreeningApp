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
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.DecimalFormat;
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
    private BottomNavigationView bottomNavigationView;
    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_complete);

        // 툴바
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        // 데이터베이스 연동 변수 처리
        databaseReference = FirebaseDatabase.getInstance().getReference("CurrentUser");
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        // orderActivity에서 보낸 결제 데이터베이스 id 가져오기
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        myOrderId = intent.getStringExtra("myOrderId");
        Log.d("OrderCompleteActivity", orderId);

        // 리사이클러뷰 레이아웃 설정 초기화
        recyclerView = findViewById(R.id.recyclerView_orderComplete);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // 레이아웃 설정 초기화
        cmpOrderId = findViewById(R.id.cmpOrderId);
        cmpOrderDate = findViewById(R.id.cmpOrderDate);
        cmp_totalPrice = findViewById(R.id.cmp_totalPrice);
        cmp_name = findViewById(R.id.cmp_name);
        cmp_phone = findViewById(R.id.cmp_phone);
        cmp_address = findViewById(R.id.cmp_address);
        cmp_postcode = (TextView) findViewById(R.id.cmp_postcode);

        myOrderList = new ArrayList<>(); // MyOrder 객체를 담을 리스트 초기화

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
                        cmp_totalPrice.setText(String.valueOf(decimalFormat.format(myOrder.getOverTotalPrice())) + "원");
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
                finish();
            }
        });

        // 하단바 구현
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation_ordercp);

        // 초기 선택 항목 설정
        bottomNavigationView.setSelectedItemId(R.id.tab_shopping);

        // BottomNavigationView의 아이템 클릭 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.tab_home) {
                    // Home 액티비티로 이동
                    startActivity(new Intent(OrderCompleteActivity.this, MainActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_shopping) {
                    // Category 액티비티로 이동
                    startActivity(new Intent(OrderCompleteActivity.this, CategoryActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_donation) {
                    // Donation 액티비티로 이동
                    startActivity(new Intent(OrderCompleteActivity.this, DonationMainActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_mypage) {
                    // My Page 액티비티로 이동
                    startActivity(new Intent(OrderCompleteActivity.this, MyPageActivity.class));
                    finish();
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