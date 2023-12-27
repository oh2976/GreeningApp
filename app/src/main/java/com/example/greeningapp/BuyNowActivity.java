package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class BuyNowActivity extends AppCompatActivity {
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference, databaseReference2, databaseReferenceProduct, databaseReferenceAdmin;
    private TextView buynow_pname, buynow_pprice, buynow_totalprice, buynow_totalquantity, overTotalAmount;
    private ImageView buynow_pimg;
    private TextView orderName, orderPhone, orderAddress, orderPostcode;
    private String strOrderName, strOrderPhone, strOrderAddress, strOrderPostcode;
    private int userSPoint;
    private String productName, productPrice, productImg;
    private int totalPrice, pId, productStock, selectedQuantity;
    int total = 0;
    Button btnPayment;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_now);

        // 파이어베이스
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        // 툴바
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        // 레이아웃 요소 불러오기
        buynow_pimg = (ImageView) findViewById(R.id.buynow_pimg);
        buynow_pname = (TextView) findViewById(R.id.buynow_pname);
        buynow_pprice = (TextView) findViewById(R.id.buynow_pprice);
        buynow_totalprice = (TextView) findViewById(R.id.buynow_totalprice);
        buynow_totalquantity = (TextView) findViewById(R.id.buynow_totalquantity);
        overTotalAmount = findViewById(R.id.buynow_overtotalPrice);
        orderName = findViewById(R.id.buynow_name);
        orderPhone = findViewById(R.id.buynow_phone);
        orderAddress = findViewById(R.id.buynow_address);
        orderPostcode = (TextView) findViewById(R.id.buynow_postcode);
        databaseReference2 = FirebaseDatabase.getInstance().getReference("User");

        // 파이어베이스 경로 설정
        databaseReference = FirebaseDatabase.getInstance().getReference("CurrentUser");
        databaseReferenceProduct = FirebaseDatabase.getInstance().getReference("Product");
        databaseReferenceAdmin = FirebaseDatabase.getInstance().getReference("Admin");

        // 주문 내역 idtoken 설정
        String myOrderId = databaseReference.child("MyOrder").push().getKey();

        databaseReference2.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 파이어베이스 회원 데이터 가져와서 레이아웃 요소에 뿌리기
                User user = dataSnapshot.getValue(User.class); //  만들어 뒀던 Product 객체에 데이터를 담는다.
                orderName.setText(user.getUsername());
                orderPhone.setText(user.getPhone());
                orderAddress.setText(user.getAddress());
                orderPostcode.setText(user.getPostcode());
                strOrderName = user.getUsername();
                strOrderPhone = user.getPhone();
                strOrderAddress = user.getAddress();
                strOrderPostcode = user.getPostcode();
                userSPoint = user.getSpoint();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 디비를 가져오던 중 에러 발생 시
                Log.e("BuyNowActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        // ProductDetailActivity.java 파일에서 보낸 데이터 변수에 담기
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            productName = bundle.getString("productName");
            productPrice = bundle.getString("productPrice");
            selectedQuantity = bundle.getInt("selectedQuantity");
            productImg = bundle.getString("productImg");
            totalPrice = bundle.getInt("totalPrice");
            pId = bundle.getInt("pId");
            productStock = bundle.getInt("productStock");

            Log.d("BuyNow", productName + productPrice + selectedQuantity + productImg + totalPrice + pId + productStock);
        }

        Glide.with(getApplicationContext()).load(productImg).into(buynow_pimg);

        // 화폐 단위 처리
        DecimalFormat decimalFormat = new DecimalFormat("###,###");

        buynow_pname.setText(productName);
        buynow_pprice.setText(String.valueOf(decimalFormat.format(Integer.parseInt(productPrice)))+ "원");
        buynow_totalprice.setText(String.valueOf(decimalFormat.format(totalPrice)) + "원");
        buynow_totalquantity.setText(String.valueOf(decimalFormat.format( selectedQuantity)) + "개");
        overTotalAmount.setText(String.valueOf(decimalFormat.format(totalPrice)) + "원");

        // orderId idtoken 생성
        final String orderId = databaseReference.push().getKey();

        btnPayment = findViewById(R.id.buynow_btnPayment);


        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 파이어베이스에 넣을 값 한 번에 HashMap 처리
                final HashMap<String, Object> cartMap = new HashMap<>();

                cartMap.put("productName", productName);
                cartMap.put("productPrice", productPrice);
                cartMap.put("totalQuantity", selectedQuantity);
                cartMap.put("totalPrice", totalPrice);
                cartMap.put("productId", pId);
                cartMap.put("overTotalPrice", totalPrice);
                cartMap.put("userName", strOrderName);
                cartMap.put("phone", strOrderPhone);
                cartMap.put("address", strOrderAddress);
                cartMap.put("postcode", strOrderPostcode);
                cartMap.put("orderId", myOrderId);
                cartMap.put("orderDate", getTime());
                cartMap.put("orderImg", productImg);
                cartMap.put("eachOrderedId", orderId);
                cartMap.put("doReview", "No");
                cartMap.put("orderstate", "paid");
                cartMap.put("useridtoken", firebaseUser.getUid());
                Log.d("OrderActivity1", total+"");

                int totalStock = productStock - Integer.valueOf(selectedQuantity);
                double changePoint = userSPoint + totalPrice * 0.01;

                // 주문 내역 데이터 저장(관리자 페이지에서 사용됨)
                databaseReferenceAdmin.child("UserOrder").child(orderId).setValue(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("OrderActivity", "Admin 계정에 추가 완료" + orderId);
                    }
                });

                // 주문 내역 데이터 저장(사용자 계정에서 사용됨)
                databaseReference.child(firebaseUser.getUid()).child("MyOrder").child(myOrderId).child(orderId).setValue(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // 총 판매량 데이터 누적 후 변경
                        databaseReference.child(firebaseUser.getUid()).child("MyOrder").child(myOrderId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int totalSelQuantity = 0;

                                // "totalQuantity" 값 누적
                                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                                    int orderQuantity = orderSnapshot.child("totalQuantity").getValue(Integer.class);
                                    totalSelQuantity += orderQuantity;
                                }
                                databaseReferenceProduct.child(String.valueOf(pId)).child("populstock").runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        Integer previousSelStock = mutableData.getValue(Integer.class);
                                        if (previousSelStock == null) {
                                            previousSelStock = 0;
                                        }

                                        int updatedSelStock = previousSelStock + Integer.valueOf(selectedQuantity);
                                        mutableData.setValue(updatedSelStock);
                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                                        if (databaseError != null) {
                                        } else if (committed) {
                                        } else {
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // 오류 처리 코드
                            }
                        });

                        // 판매량 만큼 줄어든 재고 변경
                        databaseReferenceProduct.child(String.valueOf(pId)).child("stock").setValue(totalStock).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("OrderActivity", "재고 변동 완료" + totalStock);
                            }
                        });

                        // 구매한 후 씨드 변경
                        databaseReference2.child(firebaseUser.getUid()).child("spoint").setValue(changePoint).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("OrderActivity", changePoint + "쇼핑 포인트 지급 완료");

                                databaseReference2.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        User user = snapshot.getValue(User.class);
                                        final HashMap<String, Object> pointMap = new HashMap<>();
                                        pointMap.put("pointName", "씨드 적립 - 상품 구매");
                                        pointMap.put("pointDate", getTime());
                                        pointMap.put("type", "savepoint");
                                        pointMap.put("point", totalPrice * 0.01);
                                        pointMap.put("userName", user.getUsername());

                                        String pointID = databaseReference.child(firebaseUser.getUid()).child("MyPoint").push().getKey();

                                        // 포인트 적립 내역 데이터 저장
                                        databaseReference.child(firebaseUser.getUid()).child("MyPoint").child(pointID).setValue(pointMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d("OrderActivity", "상품 구매 포인트 내역 저장" + pointID);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                    }
                });

                // 주문이 끝난 후 방금 저장한 파이어베이스 데이터 경로 키값을 가지고 주문 완료 페이지로 이동
                Intent intent = new Intent(BuyNowActivity.this, OrderCompleteActivity.class);
                intent.putExtra("orderId", orderId);
                intent.putExtra("myOrderId", myOrderId);
                startActivity(intent);
                finish();
            }


        });

        // 하단바 구현
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation_buynow);
        // 초기 선택 항목 설정
        bottomNavigationView.setSelectedItemId(R.id.tab_shopping);

        // BottomNavigationView의 아이템 클릭 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.tab_home) {
                    // Home 액티비티로 이동
                    startActivity(new Intent(BuyNowActivity.this, MainActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_shopping) {
                    // Category 액티비티로 이동
                    startActivity(new Intent(BuyNowActivity.this, CategoryActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_donation) {
                    // Donation 액티비티로 이동
                    startActivity(new Intent(BuyNowActivity.this, DonationMainActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_mypage) {
                    // My Page 액티비티로 이동
                    startActivity(new Intent(BuyNowActivity.this, MyPageActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    // 현재 시간 가져오는 메소드
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