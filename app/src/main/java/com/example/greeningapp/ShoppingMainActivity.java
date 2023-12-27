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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ShoppingMainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Product> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Button btnManageMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_main);

        // 툴바
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김


        recyclerView = findViewById(R.id.recyclerView); // 리사이클러 레이아웃 연결
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존 성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        arrayList = new ArrayList<>(); // Product 객체를 담을 어레이리스트(어댑터 쪽으로 날릴 거임)

        // 파이어베이스 경로 설정
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Product");

        // 상품 데이터 중 populstock을 기준으로 데이터를 가져오는 쿼리
        Query query = databaseReference.orderByChild("populstock");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터베이스의 데이터를 받아오는곳
                arrayList.clear(); //기준 배열리스트가 존재하지않게 초기화

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    arrayList.add(product);

                    //arrayList를 populstock 내림차순 정렬, 값 없는건 pid순 정렬
                    Collections.sort(arrayList, new Comparator<Product>() {
                        @Override
                        public int compare(Product product1, Product product2) {
                            return Integer.compare(product2.getPopulstock(), product1.getPopulstock());
                        }
                    });

                }

                adapter.notifyDataSetChanged(); //리스트저장 및 새로고침

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ShoppingMainActivity", String.valueOf(databaseError.toException()));
            }
        });

        // 상품 어댑터 생성 후 리사이클러뷰에 어댑터 연결
        adapter = new ProductAdapter(arrayList, this);
        recyclerView.setAdapter(adapter); //리사이클러뷰에 어댑터 연결

        // 관리자 메인 페이지로 이동
        btnManageMain = (Button) findViewById(R.id.btnManageMain_shop);
        btnManageMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShoppingMainActivity.this, ManagerMainActivity.class);
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