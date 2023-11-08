package com.example.greeningapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class mainslide01_Fg1 extends Fragment {

    private TextView slide01_main1;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.slide01_main1, container, false);

        slide01_main1 = rootView.findViewById(R.id.slide01_main1);

        slide01_main1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Firebase 데이터베이스에서 해당 상품을 검색
                databaseReference = FirebaseDatabase.getInstance().getReference("Product");
                Query productQuery = databaseReference.orderByChild("pid").equalTo(11004);

                productQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // 상품이 존재하면 첫 번째 항목만 사용
                            DataSnapshot firstSnapshot = dataSnapshot.getChildren().iterator().next();
                            Product product = firstSnapshot.getValue(Product.class);

                            // ProductDetailActivity로 이동
                            Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                            intent.putExtra("detail", product);
                            startActivity(intent);

                        } else {
                            // 해당 상품이 없을 때 처리
                            // 예를 들어, Toast 메시지를 표시하거나 다른 동작을 수행할 수 있습니다.
                            Log.d("mainslide01_Fg1", "이동 못함");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("mainslide01_Fg1", String.valueOf(databaseError.toException())); // 에러 처리
                    }
                });
            }
        });

        return rootView;

    }

}