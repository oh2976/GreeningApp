//package com.example.greeningapp;
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ReviewHistoryActivity extends AppCompatActivity {
//
//    FirebaseDatabase firebaseDatabase;
//    DatabaseReference databaseReference2;
//    DatabaseReference databaseReferenceProduct;
//
//    private FirebaseAuth firebaseAuth;
//    private DatabaseReference databaseReference;
//
//    FirebaseDatabase database;
//
//    Review product = null;
//    TextView Pname;
//    ImageView Pimg;
//
//    //private RecyclerView parentRecyclerView;
//
//    //private ArrayList<MyOrder> parentModelArrayList;
//    //private RecyclerView.Adapter ParentAdapter;
//
//    //private RecyclerView.LayoutManager parentLayoutManager;
//
//    private RecyclerView recyclerView;
//    private ArrayList<Review> dataList;
//    private List<MyOrder> MyOrderPList = new ArrayList<>();   //잠시 추가
//
//    private ReviewHistoryAdapter adapter;
//    private RecyclerView.LayoutManager layoutManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_review_history);
//
//        recyclerView = findViewById(R.id.reviewHisyoryRecycler);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        layoutManager = new LinearLayoutManager(this);
//        dataList = new ArrayList<>();
//
//        database = FirebaseDatabase.getInstance(); //파이어베이스 연동
//        databaseReference = database.getReference("Review");//db데이터연결
//
//        // Firebase Realtime Database 참조를 가져옵니다.
//        // 잠시 주석 DatabaseReference myOrderReference = FirebaseDatabase.getInstance().getReference("MyOrder");
//
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myOrderReference = database.getReference("MyOrder");
//
//        myOrderReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // 데이터를 가져오고 MyOrder 객체로 변환한 후 MyOrderPList에 추가합니다.
//                MyOrderPList.clear(); // 기존 데이터를 모두 제거하고 새로운 데이터로 대체합니다.
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    MyOrder myOrder = snapshot.getValue(MyOrder.class);
//                    MyOrderPList.add(myOrder);
//                }
//
//                // 데이터가 변경될 때마다 RecyclerView를 업데이트합니다.
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // 오류 처리를 수행합니다.
//                Log.e("ReviewHistoryActivity", "Firebase 데이터 가져오기 실패: " + databaseError.getMessage());
//            }
//        });
//
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            //@SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                dataList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Review review = snapshot.getValue(Review.class);
//                    dataList.add(review);
//
//                }
//                adapter.notifyDataSetChanged();
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e("ReviewHistoryActivity", String.valueOf(databaseError.toException()));
//            }
//        });
//        adapter = new ReviewHistoryAdapter(dataList, this); //MyOrderPList 잠시 추가
//        recyclerView.setAdapter(adapter);
//
//    }
//}
//
//
//
////잠시
////        database = FirebaseDatabase.getInstance();
////        databaseReference = FirebaseDatabase.getInstance().getReference("CurrentUser");
////
////        firebaseAuth = FirebaseAuth.getInstance();
////        //상품 리스트에서 상품 상세 페이지로 데이터 가져오기
////        final Object object = getIntent().getSerializableExtra("product");
////        if(object instanceof ReviewData){
////            product = (ReviewData) object;
////        }
////
////        Pimg = findViewById(R.id.reviewhistoryPImg);
////        Pname = findViewById(R.id.reviewhistoryPn);
////
////        if (product != null) {
////            Glide.with(getApplicationContext()).load(product.getOrderImg()).into(Pimg);
////            Pname.setText(product.getProductName());
////
////        }