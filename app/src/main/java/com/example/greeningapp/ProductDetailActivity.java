package com.example.greeningapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ProductDetailActivity extends AppCompatActivity {

    TextView quantity;
    int totalQuantity = 1;
    int totalPrice = 0;

    Dialog dialog;

    private int pid;

    ImageView detailedImg;
    ImageView detailedLongImg;
    TextView price, description, stock, name;
    Button addToCart, buyNow;
    ImageView addItem, removeItem;

    Product product = null;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    // 리뷰
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button moreReviewsButton;
    private ReviewAdapter adapter;
    private ArrayList<Review> arrayList;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Toolbar toolbar = findViewById(R.id.toolbar_product_detail);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);//기본 제목 삭제.
        actionBar.setDisplayHomeAsUpEnabled(true);

        dialog = new Dialog(ProductDetailActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm2);

        database = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("CurrentUser");
        String cartID = databaseReference.push().getKey();

        auth = FirebaseAuth.getInstance();
        //상품 리스트에서 상품 상세 페이지로 데이터 가져오기
        final Object object = getIntent().getSerializableExtra("detail");
        if(object instanceof Product){
            product = (Product) object;
        }

        quantity = findViewById(R.id.quantity);


        detailedImg = findViewById(R.id.detailed_img);
        addItem = findViewById(R.id.add_item);
        removeItem = findViewById(R.id.remove_item);
        detailedLongImg = findViewById(R.id.detail_longimg);

        price = findViewById(R.id.detail_price);
        stock = findViewById(R.id.detail_stock);

        name = findViewById(R.id.detailed_name);

        // 숫자에 콤마 표시
        DecimalFormat decimalFormat = new DecimalFormat("###,###");

        // 하단바 구현
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation_productDetail);

        // 초기 선택 항목 설정
        bottomNavigationView.setSelectedItemId(R.id.tab_shopping);

        // BottomNavigationView의 아이템 클릭 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.tab_home) {
                    // Home 액티비티로 이동
                    startActivity(new Intent(ProductDetailActivity.this, MainActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.tab_shopping) {
                    // Category 액티비티로 이동
                    startActivity(new Intent(ProductDetailActivity.this, CategoryActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.tab_donation) {
                    // Donation 액티비티로 이동
                    startActivity(new Intent(ProductDetailActivity.this, DonationMainActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.tab_mypage) {
                    // My Page 액티비티로 이동
                    startActivity(new Intent(ProductDetailActivity.this, MyPageActivity.class));
                    return true;
                }
                return false;
            }
        });

//        totalQuantity = Integer.parseInt(quantity.getText().toString());

        if (product != null) {
            Glide.with(getApplicationContext()).load(product.getPimg()).into(detailedImg);
//            description.setText(product.getDescription());
            price.setText(String.valueOf(decimalFormat.format(product.getPprice())) + "원");
            stock.setText("( 재고: " + String.valueOf(product.getStock()) + " )");
            name.setText(product.getPname());
            Glide.with(getApplicationContext()).load(product.getPdetailimg()).into(detailedLongImg);

            totalPrice= product.getPprice() * totalQuantity;
            decimalFormat.format(totalPrice);

            pid = product.getPid();
        }

        // 더 많은 리뷰 보기 버튼 및 리사이클러뷰 초기화
        moreReviewsButton = findViewById(R.id.moreReviewsButton);
        recyclerView = findViewById(R.id.recyclerView);    // 아이디 연결
        recyclerView.setHasFixedSize(true);    // 리사이클러뷰 기존 성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        arrayList = new ArrayList<>();

        Query reviewQuery = database.getReference("Review").orderByChild("pid").equalTo(pid);
        reviewQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                int count = 0;    // 데이터 개수를 세는 변수
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (count >= 3)    // 3개의 데이터만 가져오기
                        break;

                    Review review = snapshot.getValue(Review.class);
                    arrayList.add(review);
                    count++;    // 데이터 개수 증가
                }
                adapter.notifyDataSetChanged();    // 어댑터에 데이터 변경 알림
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {
                // DB를 가져오던 중 에러 발생 시
                Log.e("ProductDetailActivity", String.valueOf(databaseError.toException()));    // 에러문 출력
            }
        });



        adapter = new ReviewAdapter(arrayList, this);
        recyclerView.setAdapter(adapter);

        // 더 많은 리뷰 보기 버튼 클릭 시
        moreReviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 리뷰 목록 액티비티로 전환
                Intent intent = new Intent(ProductDetailActivity.this, ReviewActivity.class);
                intent.putExtra("pid", pid);
                startActivity(intent);
            }
        });

        addToCart = findViewById(R.id.add_to_cart);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                addedToCart();
                final HashMap<String, Object> cartMap = new HashMap<>();
                FirebaseUser firebaseUser = auth.getCurrentUser();
                cartMap.put("productName", product.getPname());
                cartMap.put("productPrice", String.valueOf(product.getPprice()));
                cartMap.put("selectedQuantity", totalQuantity);
                cartMap.put("totalPrice", totalPrice * totalQuantity);
                cartMap.put("pId", product.getPid());
                cartMap.put("productImg", product.getPimg());
                cartMap.put("productStock", product.getStock());
                Log.d("DetailActivity", product.getPid()+"");

                databaseReference.child(firebaseUser.getUid()).child("AddToCart").child(cartID).setValue(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        showDialog();
                    }
                });

            }
        });

        buyNow = (Button) findViewById(R.id.buyNow);
        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this, BuyNowActivity.class);



                Bundle bundle = new Bundle();
                bundle.putString("productName", product.getPname());
                bundle.putString("productPrice", String.valueOf(product.getPprice()));
                bundle.putInt("selectedQuantity", totalQuantity);
                bundle.putInt("totalPrice", totalPrice * totalQuantity);
                bundle.putInt("pId", product.getPid());
                bundle.putString("productImg", product.getPimg());
                bundle.putInt("productStock", product.getStock());

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(totalQuantity < 10){
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));
                }
            }
        });
        removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(totalQuantity > 0){
                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));
                }
            }
        });
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId ()) {
            case android.R.id.home:
                Intent intent = new Intent(ProductDetailActivity.this, CategoryActivity.class);
                startActivity(intent);
                finish ();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showDialog() {
        dialog.show();

        TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
        confirmTextView.setText("상품을 장바구니에 담았습니다.\n장바구니로 이동하시겠습니까?");

        Button btnleft = dialog.findViewById(R.id.btn_left);
        btnleft.setText("쇼핑 계속하기");
        btnleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btnright = dialog.findViewById(R.id.btn_right);
        btnright.setText("장바구니 이동");
        btnright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}