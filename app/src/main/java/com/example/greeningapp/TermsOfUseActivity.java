package com.example.greeningapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.google.android.material.tabs.TabLayout;

public class TermsOfUseActivity extends AppCompatActivity {
    TabLayout tab_termsofuse;
    ViewPager viewPager_termsofuse;
    Button closeTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_use);

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar_termsofuse);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 탭 레이아웃, 뷰 페이저 초기화
        tab_termsofuse = (TabLayout) findViewById(R.id.tab_termsofuse);
        viewPager_termsofuse = (ViewPager) findViewById(R.id.viewPager_termsofuse);

        // 뷰 페이저 어댑터 설정
        ViewPagerTermsOfUseAdapter viewPagerTermsOfUseAdapter = new ViewPagerTermsOfUseAdapter(getSupportFragmentManager());
        viewPager_termsofuse.setAdapter(viewPagerTermsOfUseAdapter);

        tab_termsofuse.setupWithViewPager(viewPager_termsofuse);

        closeTab = findViewById(R.id.btn_closeTab);

        // 닫기 버튼 클릭 이벤트 처리
        closeTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 전달받은 Intent에서 selectedTab 값 확인하여 선택된 탭 설정
        Intent intent = getIntent();
        String selectedTab = intent.getStringExtra("selectedTab");

        if ("left".equals(selectedTab)) {
            TabLayout tabLayout = findViewById(R.id.tab_termsofuse);
            tabLayout.getTabAt(0).select();
        } else if ("right".equals(selectedTab)) {
            TabLayout tabLayout = findViewById(R.id.tab_termsofuse);
            tabLayout.getTabAt(1).select();
        }
    }

    // 뒤로가기
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } if (itemId == R.id.btn_closeTab) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}