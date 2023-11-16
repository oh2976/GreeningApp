package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<BarEntry> productEntries = new ArrayList<>();
                final ArrayList<String> productNames = new ArrayList<>();

                int index = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    int populstock = snapshot.child("populstock").getValue(Integer.class);
                    productEntries.add(new BarEntry(index, populstock));

                    String pname = snapshot.child("pname").getValue(String.class);
                    productNames.add(pname);

                    index++;
                }

                BarDataSet barDataSet = new BarDataSet(productEntries, "실시간 판매량(개)");
                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                barDataSet.setValueTextColor(getResources().getColor(R.color.black));
                barDataSet.setValueTextSize(14f);

                barDataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.valueOf((int) value) + "개";
                    }
                });

                BarData barData = new BarData(barDataSet);
                barData.setValueTextSize(14f);

                barChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.valueOf((int) value) + "개";
                    }
                });

                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(productNames));
                xAxis.setDrawGridLines(false);
                xAxis.setDrawAxisLine(false);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setTextSize(14f);
                xAxis.setGranularity(0.1f);
                xAxis.setLabelCount(productNames.size());

                barChart.setData(barData);
                barChart.setExtraTopOffset(15f);
                barChart.setExtraBottomOffset(10f);
                barChart.getBarData().setBarWidth(0.6f);
                barChart.getAxisLeft().setEnabled(false);
                barChart.getAxisRight().setEnabled(false);
                barChart.animateY(2000);
                barChart.getDescription().setText("그리닝 상품별 실시간 판매량 차트");
                barChart.getDescription().setTextSize(14f);
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