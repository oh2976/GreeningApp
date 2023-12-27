package com.example.greeningapp;

import androidx.annotation.NonNull;
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
    Dialog dialog2;
    private int pid;
    ImageView detailedImg;
    ImageView detailedLongImg;
    TextView price, stock, name;
    Button addToCart, buyNow;
    ImageView addItem, removeItem;
    Product product = null;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button moreReviewsButton;
    private ReviewAdapter adapter;
    private ArrayList<Review> arrayList;
    private BottomNavigationView bottomNavigationView;
    private int getstock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar_product_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 다이얼로그 설정
        dialog = new Dialog(ProductDetailActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm2);

        dialog2 = new Dialog(ProductDetailActivity.this);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.setContentView(R.layout.dialog_confirm);

        // 파이어베이스 설정
        database = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("CurrentUser");
        String cartID = databaseReference.push().getKey();
        auth = FirebaseAuth.getInstance();

        //상품 카테고리 페이지에서 상품 상세 페이지로 데이터 가져오기
        final Object object = getIntent().getSerializableExtra("detail");
        if(object instanceof Product){
            product = (Product) object;
        }

        // 레이아웃 요소
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
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_shopping) {
                    // Category 액티비티로 이동
                    startActivity(new Intent(ProductDetailActivity.this, CategoryActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_donation) {
                    // Donation 액티비티로 이동
                    startActivity(new Intent(ProductDetailActivity.this, DonationMainActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_mypage) {
                    // My Page 액티비티로 이동
                    startActivity(new Intent(ProductDetailActivity.this, MyPageActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });

        // product 데이터가 비어 있지 않을 경우 데이터 가져오기
        if (product != null) {
            Glide.with(getApplicationContext()).load(product.getPimg()).into(detailedImg);
            price.setText(String.valueOf(decimalFormat.format(product.getPprice())) + "원");
            stock.setText("( 재고: " + String.valueOf(product.getStock()) + " )");
            name.setText(product.getPname());
            Glide.with(getApplicationContext()).load(product.getPdetailimg()).into(detailedLongImg);
            totalPrice= product.getPprice() * totalQuantity;
            decimalFormat.format(totalPrice);
            pid = product.getPid();
            getstock = product.getStock();
        }

        // 상품후기 전체보기 버튼 및 후기 리사이클러뷰 초기화
        moreReviewsButton = findViewById(R.id.moreReviewsButton);
        recyclerView = findViewById(R.id.recyclerView);  // 아이디 연결
        recyclerView.setHasFixedSize(true);  // 리사이클러뷰 기존 성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        arrayList = new ArrayList<>();

        // 상품 후기 데이터 최대 3개 가져오기
        Query reviewQuery = database.getReference("Review").orderByChild("pid").equalTo(pid);
        reviewQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                int count = 0;  // 데이터 개수를 세는 변수
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (count >= 3)  // 3개의 후기 데이터만 가져오기
                        break;

                    Review review = snapshot.getValue(Review.class);
                    arrayList.add(review);
                    count++;  // 데이터 개수 증가
                }
                adapter.notifyDataSetChanged();  // 어댑터에 데이터 변경 알림
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 상품 후기 데이터 로드 오류 시 에러 메세지 출력
                Log.e("ProductDetailActivity, 상품 후기 데이터 로드 오류", String.valueOf(databaseError.toException()));  // 에러문 출력
            }
        });

        adapter = new ReviewAdapter(arrayList, FirebaseAuth.getInstance(), FirebaseDatabase.getInstance().getReference("User"));
        recyclerView.setAdapter(adapter);

        // 상품후기 전체보기 버튼 클릭 이벤트 설정
        moreReviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this, ReviewActivity.class);
                intent.putExtra("pid", pid);
                startActivity(intent);
            }
        });

        // 장바구니 담기 버튼 클릭 이벤트 설정
        addToCart = findViewById(R.id.add_to_cart);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 재고가 0 이상일 경우
                if (getstock > 0) {
                    // 선택된 수량이 재고보다 작거나 같은 경우
                    if (totalQuantity <= getstock) {
                        // cartMap에 상품 정보 추가
                        final HashMap<String, Object> cartMap = new HashMap<>();
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        cartMap.put("productName", product.getPname());
                        cartMap.put("productPrice", String.valueOf(product.getPprice()));
                        cartMap.put("selectedQuantity", totalQuantity);
                        cartMap.put("totalPrice", totalPrice * totalQuantity);
                        cartMap.put("pId", product.getPid());
                        cartMap.put("productImg", product.getPimg());
                        cartMap.put("productStock", product.getStock());
                        Log.d("DetailActivity", product.getPid() + "");

                        // 파이어베이스에 cartMap에 추가한 상품 정보를 CurrentUser 테이블에 사용자 ID 하위 AddToCart 자식에 고유한 cartID로 저장
                        databaseReference.child(firebaseUser.getUid()).child("AddToCart").child(cartID).setValue(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                showDialog();  // 장바구니 담기 성공 시 다이얼로그 생성
                            }
                        });
                    } else {  // 선택된 수량이 재고보다 클 경우
                        showImpossibleDialog();  // 다이얼로그 생성
                    }
                } else {  // 재고가 없을 경우
                    showStockDialog();  // 다이얼로그 생성
                }
            }
        });

        // 바로구매 버튼 클릭 이벤트 설정
        buyNow = (Button) findViewById(R.id.buyNow);
        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 재고가 0 이상일 경우
                if(getstock > 0) {
                    // 선택된 수량이 재고보다 작거나 같은 경우
                    if (totalQuantity <= getstock) {
                        // 주문서 작성 액티비티로 이동
                        Intent intent = new Intent(ProductDetailActivity.this, BuyNowActivity.class);

                        // 인텐트에 전달할 데이터를 Bundle에 추가
                        Bundle bundle = new Bundle();
                        bundle.putString("productName", product.getPname());
                        bundle.putString("productPrice", String.valueOf(product.getPprice()));
                        bundle.putInt("selectedQuantity", totalQuantity);
                        bundle.putInt("totalPrice", totalPrice * totalQuantity);
                        bundle.putInt("pId", product.getPid());
                        bundle.putString("productImg", product.getPimg());
                        bundle.putInt("productStock", product.getStock());

                        //인텐트에 Bundle 추가
                        intent.putExtras(bundle);

                        startActivity(intent);
                    } else {  // 선택된 수량이 재고보다 클 경우
                        showImpossibleDialog();  // 다이얼로그 생성
                    }
                } else {  // 재고가 없을 경우
                    showStockDialog();  // 다이얼로그 생성
                }
            }
        });

        // 수량 + 버튼 클릭 이벤트 설정
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 수량이 10까지만 증가되도록 설정
                if(totalQuantity < 10){
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));
                }
            }
        });

        // 수량 - 버튼 클릭 이벤트 설정
        removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 수량이 1까지만 감소되도록 설정
                if (totalQuantity > 1) {
                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));
                } else {
                }
            }
        });
    }

    // 뒤로가기 처리
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void showDialog() {
        dialog.show();

        TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
        confirmTextView.setText("상품을 장바구니에 담았습니다.\n장바구니로 이동하시겠습니까?");

        Button btnleft = dialog.findViewById(R.id.btn_left);
        btnleft.setText("쇼핑 계속하기");
        // 쇼핑 계속하기 버튼 클릭 이벤트 처리
        btnleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btnright = dialog.findViewById(R.id.btn_right);
        btnright.setText("장바구니 이동");
        // 장바구니 이동 버튼 클릭 이벤트 처리
        btnright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showStockDialog() {
        dialog2.show();

        TextView confirmTextView = dialog2.findViewById(R.id.confirmTextView);
        confirmTextView.setText("해당 상품은 현재 일시 품절입니다.");

        Button btnOk = dialog2.findViewById(R.id.btn_ok);
        btnOk.setText("확인");

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });
    }

    public void showImpossibleDialog() {
        dialog2.show();

        TextView confirmTextView = dialog2.findViewById(R.id.confirmTextView);
        confirmTextView.setText("현재 구매 가능 개수는\n" + getstock + "개입니다.");

        Button btnOk = dialog2.findViewById(R.id.btn_ok);
        btnOk.setText("확인");

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });
    }
}