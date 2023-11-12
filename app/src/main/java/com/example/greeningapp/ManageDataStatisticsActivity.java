package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;

import java.util.ArrayList;

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

        // 파이어베이스에서 데이터 가져오기
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Product");

        databaseReference.limitToFirst(20).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<BarEntry> productEntries = new ArrayList<>();
                final ArrayList<String> productNames = new ArrayList<>();

                int index = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Product 테이블의 populstock를 BarEntry에 추가
                    int populstock = snapshot.child("populstock").getValue(Integer.class);
                    productEntries.add(new BarEntry(index, populstock));

                    // Product 테이블의 pname을 리스트에 추가
                    String pname = snapshot.child("pname").getValue(String.class);
                    productNames.add(pname);

                    index++;
                }

                BarDataSet barDataSet = new BarDataSet(productEntries, "Product Popularity");
                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                barDataSet.setValueTextColor(getResources().getColor(R.color.black));
                barDataSet.setValueTextSize(16f);

                BarData barData = new BarData(barDataSet);

                BarChart barChart = findViewById(R.id.barChart);

                // X축 설정
                barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(productNames));
                barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                barChart.getXAxis().setGranularity(1f);

                barChart.setFitBars(true);
                barChart.setData(barData);
                barChart.getDescription().setText("Product Popularity Chart");
                barChart.animateY(2000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ManageDataStatisticsActivity", String.valueOf(databaseError.toException()));
            }
        });
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