package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ManageDataStatisticsActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_data_statistics);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BarChart barChart = findViewById(R.id.barChart);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Product");

        // firebase 데이터를 가져오느 이벤트 리스너 등록
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 상품 데이터를 저장할 리스트 초기화
                ArrayList<Pair<String, Integer>> productEntries = new ArrayList<>();

                // 데이터 스냅샷에서 상품 데이터 추출
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String pname = snapshot.child("pname").getValue(String.class);    // 상품명 데이터 추출
                    int populstock = snapshot.child("populstock").getValue(Integer.class);    // 판매량 데이터 추출

                    // 추출한 데이터를 상품 데이터 리스트에 추가
                    productEntries.add(new Pair<>(pname, populstock));
                }

                // 상품 데이터 리스트의 데이터들을 판매량(populstock) 기준으로 정렬
                Collections.sort(productEntries, new Comparator<Pair<String, Integer>>() {
                    @Override
                    public int compare(Pair<String, Integer> entry1, Pair<String, Integer> entry2) {
                        return Integer.compare(entry1.second, entry2.second);
                    }
                });

                // 바 차트에 표시할 데이터 설정
                BarDataSet barDataSet = new BarDataSet(createBarEntries(productEntries), "실시간 판매량(개)");
                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                barDataSet.setValueTextColor(getResources().getColor(R.color.black));
                barDataSet.setValueTextSize(14f);

                // 바 데이터 값 포맷 설정 ("X개")
                barDataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.valueOf((int) value) + "개";
                    }
                });

                BarData barData = new BarData(barDataSet);
                barData.setValueTextSize(14f);

                // X축 설정
                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(getXAxisLabels(productEntries)));
                xAxis.setDrawGridLines(false);
                xAxis.setDrawAxisLine(false);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setTextSize(14f);
                xAxis.setGranularity(1f);
                xAxis.setLabelCount(productEntries.size());

                // 바 차트 설정, 갱신
                barChart.setData(barData);
                barChart.setExtraTopOffset(15f);
                barChart.setExtraBottomOffset(10f);
                barChart.getBarData().setBarWidth(0.6f);
                barChart.getAxisLeft().setEnabled(false);
                barChart.getAxisRight().setEnabled(false);
                barChart.animateY(2000);
                barChart.getDescription().setEnabled(false);
                barChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ManageDataStatisticsActivity, 통계 데이터 로드 오류", String.valueOf(databaseError.toException()));
            }
        });
    }

    // 차트에 표시할 데이터 entry 생성 메서드
    private List<BarEntry> createBarEntries(List<Pair<String, Integer>> pairList) {
        List<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < pairList.size(); i++) {
            Pair<String, Integer> pair = pairList.get(i);
            barEntries.add(new BarEntry(i, pair.second));
        }
        return barEntries;
    }

    // x축 레이블을 가져오는 메서드
    private List<String> getXAxisLabels(List<Pair<String, Integer>> pairList) {
        List<String> xAxisLabels = new ArrayList<>();
        for (Pair<String, Integer> pair : pairList) {
            xAxisLabels.add(pair.first);    // x축 레이블을 상품명으로 설정
        }
        return xAxisLabels;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}