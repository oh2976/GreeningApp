package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderActivity extends AppCompatActivity {


    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;
    DatabaseReference databaseReferenceProduct;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Cart> arrayList;

    Product product = null;

    private TextView overTotalAmount;

    private TextView orderName;
    private TextView orderPhone;
    private TextView orderAddress;



    int total = 0;

    Button btnPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        recyclerView = findViewById(R.id.recyclerView_order); //아디 연결
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존 성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        arrayList = new ArrayList<>(); // Product 객체를 담을 어레이리스트(어댑터 쪽으로 날릴 거임)

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser()
                ;
        overTotalAmount = (TextView)findViewById(R.id.order_totalPrice);

        orderName = (TextView)findViewById(R.id.order_name);
        orderPhone = (TextView)findViewById(R.id.order_phone);
        orderAddress = (TextView)findViewById(R.id.order_address);

        databaseReference2 = FirebaseDatabase.getInstance().getReference("UserAccount");

        databaseReference = FirebaseDatabase.getInstance().getReference("CurrentUser");
        databaseReferenceProduct = FirebaseDatabase.getInstance().getReference("Product");


        // 회원 정보 가져오기
        databaseReference2.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳

                UserAccount userAccount = dataSnapshot.getValue(UserAccount.class); //  만들어 뒀던 Product 객체에 데이터를 담는다.
                orderName.setText(userAccount.getUsername());
                orderPhone.setText(userAccount.getPhone());
                orderAddress.setText(userAccount.getAddress());
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 디비를 가져오던 중 에러 발생 시
                Log.e("OrderActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        databaseReference.child(firebaseUser.getUid()).child("AddToCart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                arrayList.clear(); //기존 배열 리스트가 존재하지 않게 남아 있는 데이터 초기화
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    // 반복문으로 데이터 List를 추출해냄
                    String dataId = dataSnapshot.getKey();

                    Cart cart = snapshot.getValue(Cart.class); //  만들어 뒀던 Product 객체에 데이터를 담는다.
                    arrayList.add(cart); // 담은 데이터들을 배열 리스트에 넣고 리사이클러뷰로 보낼 준비

                    cart.setDataId(dataId);
                    total += cart.getTotalPrice();
                    Log.d("OrderActivity", total+"");
                    overTotalAmount.setText(String.valueOf(total));

                }
                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 디비를 가져오던 중 에러 발생 시
                Log.e("OrderActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        adapter = new CartAdapter(this, arrayList);
        recyclerView.setAdapter(adapter); //리사이클러뷰에 어댑터 연결

        String orderId = databaseReference.push().getKey();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김



        List<Cart> list = (ArrayList<Cart>) getIntent().getSerializableExtra("itemList");



        btnPayment = (Button) findViewById(R.id.btnPayment);


        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (list != null && list.size() > 0) {
                    for (Cart model : list) {

                        final HashMap<String, Object> cartMap = new HashMap<>();

                        cartMap.put("productName", model.getProductName());
                        cartMap.put("productPrice", model.getProductPrice());
                        cartMap.put("totalQuantity", model.getTotalQuantity());
                        cartMap.put("totalPrice", model.getTotalPrice());
                        cartMap.put("productId", model.getpId());
                        cartMap.put("overTotalPrice", total);
                        Log.d("OrderActivity1", total+"");

                        int totalStock = model.getProductStock() - Integer.valueOf(model.getTotalQuantity());

                        databaseReference.child(firebaseUser.getUid()).child("MyOrder").child(model.getDataId()).setValue(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(OrderActivity.this, "주문완료", Toast.LENGTH_SHORT).show();
                                int b = list.size();
                                while (b > 0){
//                                   Log.d("OrderActivity2", databaseReference.child(firebaseUser.getUid()).child("AddToCart").child(list.get(b-1).getDataId())+"");

                                    int pId = model.getpId();

//                                    int totalStock = 0;
//                                    databaseReferenceProduct.child(model.getDataId()).child("stock");

                                    databaseReferenceProduct.child(String.valueOf(pId)).child("stock").setValue(totalStock).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(OrderActivity.this, "재고 변동 완료", Toast.LENGTH_SHORT).show();
                                        }
                                    });


//                                    Log.d("OrderActivity2", databaseReference.child(firebaseUser.getUid()).child("MyOrder").child(model.getDataId()).child(productId)+"");

                                    databaseReference.child(firebaseUser.getUid()).child("AddToCart").child(list.get(b-1).getDataId())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(OrderActivity.this, "해당 장바구니 데이터 삭제, " + totalStock , Toast.LENGTH_SHORT).show();


                                                }
                                            });
                                    b--;

                                }


                            }
                        });






                    }


                }

            }
        });

    }
}