package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class BuyNowActivity extends AppCompatActivity {



    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;
    DatabaseReference databaseReferenceProduct;

    private TextView overTotalAmount;
    private TextView buynow_pname, buynow_pprice, buynow_totalprice, buynow_totalquantity;
    private ImageView buynow_pimg;

    private TextView orderName;
    private TextView orderPhone;
    private TextView orderAddress;
    private  TextView orderPostcode;

    private String strOrderName;
    private String strOrderPhone;
    private String strOrderAddress;
    private String strOrderPostcode;
    private int userSPoint;

    private String productName, productPrice, totalQuantity, productImg;
    private int totalPrice, pId, productStock;

    private ImageButton navMain, navCategory, navDonation, navMypage;




    int total = 0;

    Button btnPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_now);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

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

        databaseReference = FirebaseDatabase.getInstance().getReference("CurrentUser");
        databaseReferenceProduct = FirebaseDatabase.getInstance().getReference("Product");

        String myOrderId = databaseReference.child("MyOrder").push().getKey();
        String pointID = databaseReference.child("MyPoint").push().getKey();



        databaseReference2.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
//                arrayList.clear(); //기존 배열 리스트가 존재하지 않게 남아 있는 데이터 초기화
                // 반복문으로 데이터 List를 추출해냄

                User user = dataSnapshot.getValue(User.class); //  만들어 뒀던 Product 객체에 데이터를 담는다.
                orderName.setText(user.getUsername());
                orderPhone.setText(user.getPhone());
                orderAddress.setText(user.getAddress());
                strOrderName = user.getUsername();
                strOrderPhone = user.getPhone();
                strOrderAddress = user.getAddress();
                strOrderPostcode = user.getPostcode();

                userSPoint = user.getSpoint();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 디비를 가져오던 중 에러 발생 시
                Log.e("OrderActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            productName = bundle.getString("productName");
            productPrice = bundle.getString("productPrice");
            totalQuantity = bundle.getString("totalQuantity");
            productImg = bundle.getString("productImg");
            totalPrice = bundle.getInt("totalPrice");
            pId = bundle.getInt("pId");
            productStock = bundle.getInt("productStock");

            Log.d("BuyNow", productName + productPrice + totalQuantity + productImg + totalPrice + pId + productStock);
        }

        Glide.with(getApplicationContext()).load(productImg).into(buynow_pimg);

        buynow_pname.setText(productName);
        buynow_pprice.setText(productPrice);
        buynow_totalprice.setText(String.valueOf(totalPrice));
        buynow_totalquantity.setText(String.valueOf(totalQuantity));

        overTotalAmount.setText(String.valueOf(totalPrice));



        final String orderId = databaseReference.push().getKey();

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김




        btnPayment = findViewById(R.id.buynow_btnPayment);

        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final HashMap<String, Object> cartMap = new HashMap<>();

                cartMap.put("productName", productName);
                cartMap.put("productPrice", productPrice);
                cartMap.put("totalQuantity", totalQuantity);
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
                Log.d("OrderActivity1", total+"");

                int totalStock = productStock - Integer.valueOf(totalQuantity);
                double changePoint = userSPoint + totalPrice * 0.01;


                databaseReference.child(firebaseUser.getUid()).child("MyOrder").child(myOrderId).child(orderId).setValue(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(BuyNowActivity.this, "주문완료", Toast.LENGTH_SHORT).show();

                        databaseReferenceProduct.child(String.valueOf(pId)).child("stock").setValue(totalStock).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
//                                            Toast.makeText(OrderActivity.this, "재고 변동 완료", Toast.LENGTH_SHORT).show();
                                final HashMap<String, Object> pointMap = new HashMap<>();
                                pointMap.put("pointname", "구매 확정 적립");
                                pointMap.put("pointdate", getTime());
                                pointMap.put("type", "savepoint");
                                pointMap.put("point", total * 0.01);
                                pointMap.put("username", strOrderName);

                                databaseReference.child(firebaseUser.getUid()).child("MyPoint").child(pointID).setValue(pointMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(BuyNowActivity.this, "바로 구매 포인트 내역 저장" , Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });



                        // 구매한 후 씨드 변경
                        databaseReference2.child(firebaseUser.getUid()).child("spoint").setValue(changePoint).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(BuyNowActivity.this, changePoint + "쇼핑 포인트 지급 완료", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });


                Intent intent = new Intent(BuyNowActivity.this, OrderCompleteActivity.class);
                intent.putExtra("orderId", orderId);
                intent.putExtra("myOrderId", myOrderId);

                startActivity(intent);
            }


        });

        navMain = findViewById(R.id.navMain_buyNow);
        navCategory = findViewById(R.id.navCategory_buyNow);
        navDonation = findViewById(R.id.navDonation_buyNow);
        navMypage = findViewById(R.id.navMypage_buyNow);

        // 각 아이콘 클릭 이벤트 처리
        navMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 홈 아이콘 클릭 시 처리할 내용
                Intent intent = new Intent(BuyNowActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        navCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 카테고리 아이콘 클릭 시 처리할 내용
                Intent intent = new Intent(BuyNowActivity.this, CategoryActivity.class);
                startActivity(intent);
            }
        });

        navDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 기부 아이콘 클릭 시 처리할 내용
                Intent intent = new Intent(BuyNowActivity.this, DonationMainActivity.class);
                startActivity(intent);
            }
        });

        navMypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 마이페이지 아이콘 클릭 시 처리할 내용
                Intent intent = new Intent(BuyNowActivity.this, MyPageActivity.class);
                startActivity(intent);
            }
        });
    }

    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }
}