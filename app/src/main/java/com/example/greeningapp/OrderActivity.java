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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;
    DatabaseReference databaseReferenceProduct;
    DatabaseReference databaseReferenceAdmin;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Cart> arrayList;

    Product product = null;

    private TextView overTotalAmount;

    private TextView orderName;
    private TextView orderPhone;
    private TextView orderAddress;

    private TextView orderPostcode;

    private String strOrderName;
    private String strOrderPhone;
    private String strOrderAddress;

    private String strOrderPostcode;
    private int userSPoint;

    int total = 0;

    Button btnPayment;

    private ImageButton navMain, navCategory, navDonation, navMypage;

    private BottomNavigationView bottomNavigationView;

    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // 툴바 생성
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        // AddToCart에 있는 데이터베이스를 넣을 상품 리사이클러뷰
        recyclerView = findViewById(R.id.recyclerView_order); //아디 연결
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존 성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Product 객체를 담을 어레이리스트(어댑터 쪽으로 날릴 거임)
        arrayList = new ArrayList<>();

        // 파이어베이스 연동을 위한 변수 만들어주기
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        overTotalAmount = (TextView)findViewById(R.id.order_totalPrice);

        orderName = (TextView)findViewById(R.id.order_name);
        orderPhone = (TextView)findViewById(R.id.order_phone);
        orderAddress = (TextView)findViewById(R.id.order_address);
        orderPostcode = (TextView) findViewById(R.id.order_postcode);

        databaseReference2 = FirebaseDatabase.getInstance().getReference("User");

        databaseReference = FirebaseDatabase.getInstance().getReference("CurrentUser");
        databaseReferenceProduct = FirebaseDatabase.getInstance().getReference("Product");
        databaseReferenceAdmin = FirebaseDatabase.getInstance().getReference("Admin");

        String myOrderId = databaseReference.child("MyOrder").push().getKey();

        // 회원 정보 가져오기
        databaseReference2.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳

                User user = dataSnapshot.getValue(User.class); //  만들어 뒀던 Product 객체에 데이터를 담는다.
                orderName.setText(user.getUsername());
                orderPhone.setText(user.getPhone());
                orderAddress.setText(user.getAddress());
                orderPostcode.setText(user.getPostcode());

                // MyOrder 데이터베이스에 회원 정보 저장을 위해서 변수에 따로 저장
                strOrderName = user.getUsername();
                strOrderPhone = user.getPhone();
                strOrderAddress = user.getAddress();
                strOrderPostcode = user.getPostcode();

                // 결제 시 회원 테이블에 있는 sPoint 변경을 위해서 기존 sPoint를 변수에 저장
                userSPoint = user.getSpoint();
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
                    overTotalAmount.setText(String.valueOf(decimalFormat.format(total)) + "원");
                }
                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 디비를 가져오던 중 에러 발생 시
                Log.e("OrderActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        Log.e("OrderActivity", String.valueOf(total)); // 에러문 출력

        adapter = new OrderAdapter(this, arrayList);
        recyclerView.setAdapter(adapter); //리사이클러뷰에 어댑터 연결

        String orderId = databaseReference.push().getKey();



        List<Cart> list = (ArrayList<Cart>) getIntent().getSerializableExtra("itemList");
        btnPayment = (Button) findViewById(R.id.btnPayment);

        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference2.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        final HashMap<String, Object> pointMap = new HashMap<>();
                        pointMap.put("pointName", "씨드 적립 - 상품 구매");
                        pointMap.put("pointDate", getTime());
                        pointMap.put("type", "savepoint");
                        pointMap.put("point", total * 0.01);
                        pointMap.put("userName", user.getUsername());

                        String pointID = databaseReference.child(firebaseUser.getUid()).child("MyPoint").push().getKey();

                        databaseReference.child(firebaseUser.getUid()).child("MyPoint").child(pointID).setValue(pointMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
//                                Toast.makeText(OrderActivity.this, "상품 구매 포인트 내역 저장" , Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if (list != null && list.size() > 0) {
                    for (Cart model : list) {

                        String eachOrderedId = model.getDataId();

                        final HashMap<String, Object> cartMap = new HashMap<>();

                        cartMap.put("productName", model.getProductName());
                        cartMap.put("productPrice", model.getProductPrice());
                        cartMap.put("totalQuantity", model.getSelectedQuantity());
                        cartMap.put("totalPrice", model.getTotalPrice());
                        cartMap.put("productId", model.getpId());
                        cartMap.put("overTotalPrice", total);
                        cartMap.put("userName", strOrderName);
                        cartMap.put("phone", strOrderPhone);
                        cartMap.put("address", strOrderAddress);
                        cartMap.put("postcode", strOrderPostcode);
                        cartMap.put("orderId", myOrderId);
                        cartMap.put("orderDate", getTime());
                        cartMap.put("doReview", "No");
                        cartMap.put("orderImg", model.getProductImg());
                        cartMap.put("orderstate", "paid");
                        cartMap.put("eachOrderedId", eachOrderedId);
                        cartMap.put("useridtoken", firebaseUser.getUid());

                        // 결제 된 재고만큼 기존 재고에서 변경한 값을 변수에 저장
                        int totalStock = model.getProductStock() - Integer.valueOf(model.getSelectedQuantity());
                        // 기존 회원 sPoint에 있는 값에 결제 후 추가될 씨드 더하여 변수에 저장
                        double changePoint = userSPoint + total * 0.01;

                        databaseReferenceAdmin.child("UserOrder").child(eachOrderedId).setValue(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("OrderActivity", "Admin 계정에 추가 완료" + eachOrderedId);
                            }
                        });


                        // 결제 버튼을 누르면 데이터베이스에 MyOrder 테이블 생성 코드
                        // 데이터베이스 경로 변경됨.
                        databaseReference.child(firebaseUser.getUid()).child("MyOrder").child(myOrderId).child(eachOrderedId).setValue(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
//                                Toast.makeText(OrderActivity.this, "주문완료", Toast.LENGTH_SHORT).show();
                                int b = list.size();
                                while (b > 0){
                                    int pId = model.getpId();

                                    // 상품 테이블에 있는 재고 변동 코드
                                    databaseReferenceProduct.child(String.valueOf(pId)).child("stock").setValue(totalStock).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
//                                            Toast.makeText(OrderActivity.this, "재고 변동 완료", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    // 주문 완료, 재고 변동 후 AddToCart에 있는 상품 삭제
                                    databaseReference.child(firebaseUser.getUid()).child("AddToCart").child(list.get(b-1).getDataId())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    Toast.makeText(OrderActivity.this, "해당 장바구니 데이터 삭제, " + totalStock , Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    b--;

                                }

                                // 결제 후 결제 금액의 1% 만큼의 씨드 sPoint 추가하는 코드
                                // ExampleApp이랑 데이터베이스 경로가 다름
                                databaseReference2.child(firebaseUser.getUid()).child("spoint").setValue(changePoint).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
//                                        Toast.makeText(OrderActivity.this, "쇼핑 포인트 지급 완료", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        // 주문 완료 페이지에서 현재 주문에 대한 데이터베이스를 가져오기 위해 id를 OrderCompleteActivity에 넘겨줌
                        Intent intent = new Intent(OrderActivity.this, OrderCompleteActivity.class);
                        intent.putExtra("orderId", orderId);
                        intent.putExtra("myOrderId", myOrderId);

                        startActivity(intent);
                        finish();

                    }


                }

            }
        });


        // 하단바 구현
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation_order);

        // 초기 선택 항목 설정
        bottomNavigationView.setSelectedItemId(R.id.tab_shopping);

        // BottomNavigationView의 아이템 클릭 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.tab_home) {
                    // Home 액티비티로 이동
                    startActivity(new Intent(OrderActivity.this, MainActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_shopping) {
                    // Category 액티비티로 이동
                    startActivity(new Intent(OrderActivity.this, CategoryActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_donation) {
                    // Donation 액티비티로 이동
                    startActivity(new Intent(OrderActivity.this, DonationMainActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_mypage) {
                    // My Page 액티비티로 이동
                    startActivity(new Intent(OrderActivity.this, MyPageActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });


    }

    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
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