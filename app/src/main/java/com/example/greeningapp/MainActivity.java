package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import me.relex.circleindicator.CircleIndicator3;

//상품진열
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    private ViewPager2 mPager;
    private ViewPager2 mPager01;   //01붙은거는 슬라이드2변수

    private FragmentStateAdapter pagerAdapter;
    private FragmentStateAdapter pagerAdapter01;
    private final int num_page = 4;    //viewpager2에 2개의 페이지가 표시됨.
    private final int num_page01 = 4;
    private CircleIndicator3 mIndicator;
    private CircleIndicator3 mIndicator01;
    //상품목록
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Product> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private Button goToShoppingMain;

    // 임시 하단바 버튼
    private Button mainbtnCart, mainbtnDonation, mainbtnMyPage;

    //슬라이드1 화면
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ViewPager2
        mPager = findViewById(R.id.viewpager);
        mPager01 = findViewById(R.id.viewpager01);
        // Adapter
        FragmentStateAdapter pagerAdapter = new MainAdapter(this, num_page);
        mPager.setAdapter(pagerAdapter);

        FragmentStateAdapter pagerAdapter01 = new MainAdapter01(this, num_page01);
        mPager01.setAdapter(pagerAdapter01);

        // Indicator 초기화 및 설정
        mIndicator = findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);     //indicator와 ViewPager2이 연동됨
        mIndicator.createIndicators(num_page, 0);

        mIndicator01 = findViewById(R.id.indicator01);
        mIndicator01.setViewPager(mPager01);     //indicator와 ViewPager2이 연동됨
        mIndicator01.createIndicators(num_page01, 0);
        // ViewPager2 설정
        mPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);  //슬라이드방향(수평)
        mPager.setCurrentItem(1000);           // 시작 지점
        mPager.setOffscreenPageLimit(2);       // 최대 이미지 수

        mPager01.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);  //슬라이드방향(수평)
        mPager01.setCurrentItem(1000);           // 시작 지점
        mPager01.setOffscreenPageLimit(2);

        //상품목록
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_main); //아디연결
        recyclerView.setHasFixedSize(true); //리사이클뷰 성능강화
        layoutManager = new LinearLayoutManager(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);   //가로2개(추가)
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); //Product객체를 담을 ArrayList(어댑터쪽으로)

        database = FirebaseDatabase.getInstance(); //파이어베이스 연동

        databaseReference = database.getReference("Product");//db데이터연결
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터베이스의 데이터를 받아오는곳
                arrayList.clear(); //기준 배열리스트가 존재하지않게 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { //반복문으로 데이터리스트 추출
                    Product product = snapshot.getValue(Product.class);  //만들어뒀던 user객체에 데이터를 담는다
                    arrayList.add(product); //담은 데이터들을 배열리스트에 넣고 리사이클뷰로 보낼준비
                }
                adapter.notifyDataSetChanged(); //리스트저장 및 새로고침
                //db가져오던중 에러발생시
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", String.valueOf(databaseError.toException())); //에러문출력
            }
        });
        adapter = new MainProductAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);  //리사이클뷰에 어댑터연결


        //페이지 변경 이벤트 리스너 등록
        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    mPager.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mIndicator.animatePageSelected(position % num_page);
            }
        });
        //슬라이드2
//        mPager01.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//                if (positionOffsetPixels == 0) {
//                    mPager01.setCurrentItem(position);
//                }
//            }
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                mIndicator01.animatePageSelected(position % num_page01);
//            }
//        });

        goToShoppingMain = (Button) findViewById(R.id.goToShoppingMain);
        goToShoppingMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShoppingMainActivity.class);
                startActivity(intent);
            }
        });

        mainbtnCart = (Button) findViewById(R.id.mainbtnGoToCart);
        mainbtnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        mainbtnDonation = (Button) findViewById(R.id.mainbtnDonation);
        mainbtnDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DonationMainActivity.class);
                startActivity(intent);
            }
        });

        mainbtnMyPage = (Button) findViewById(R.id.mainbtnMyPage);
        mainbtnMyPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyPageActivity.class);
                startActivity(intent);
            }
        });

    }
}