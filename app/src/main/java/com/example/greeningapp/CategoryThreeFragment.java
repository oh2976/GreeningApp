package com.example.greeningapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class CategoryThreeFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Product> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_three, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        //리사이클러뷰의 크기를 고정하여 성능 향상
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        //파이어베이스 데이터베이스 연동
        database = FirebaseDatabase.getInstance();

        // Product 테이블에 대한 레퍼런스 생성
        databaseReference = database.getReference("Product");
        // category 필드가 103인 데이터를 조회하는 쿼리 생성
        Query query = databaseReference.orderByChild("category").equalTo(103);//db 데이블 연결

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터베이스의 데이터를 받아오는 곳
                arrayList.clear(); // 이전 데이터 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Product Product = snapshot.getValue(com.example.greeningapp.Product.class);
                    arrayList.add(Product); //담은 데이터들을 배열리스트에 넣고 리사이클로뷰로 보낼 준비
                }
                // 리스트 저장 및 새로고침
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // 어댑터 초기화 및 리사이클러뷰에 어댑터 연결
        adapter = new CategoryAdapter(arrayList, getActivity());
        recyclerView.setAdapter(adapter);

        return view;
    }
}
