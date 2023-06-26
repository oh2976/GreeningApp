package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ProductDetailActivity extends AppCompatActivity {

    TextView quantity;
    int totalQuantity = 1;
    int totalPrice = 0;

    ImageView detailedImg;
    ImageView detailedLongImg;
    TextView price, description, stock, name;
    Button addToCart;
    ImageView addItem, removeItem;

    Product product = null;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

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
        description = findViewById(R.id.detailed_disc);
        stock = findViewById(R.id.detail_stock);

        name = findViewById(R.id.detailed_name);

        if (product != null) {
            Glide.with(getApplicationContext()).load(product.getPimg()).into(detailedImg);
//            description.setText(product.getDescription());
            price.setText(String.valueOf(product.getPprice()));
            stock.setText("재고: " + String.valueOf(product.getStock()));
            name.setText(product.getPname());
            Glide.with(getApplicationContext()).load(product.getPdetailimg()).into(detailedLongImg);

            totalPrice= product.getPprice() * totalQuantity;

        }


        addToCart = findViewById(R.id.add_to_cart);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                addedToCart();
                final HashMap<String, Object> cartMap = new HashMap<>();
                FirebaseUser firebaseUser = auth.getCurrentUser();
                cartMap.put("productName", product.getPname());
                cartMap.put("productPrice", price.getText().toString());
                cartMap.put("totalQuantity", quantity.getText().toString());
                cartMap.put("totalPrice", totalPrice * totalQuantity);
                cartMap.put("pId", product.getPid());
                cartMap.put("productImg", product.getPimg());
                cartMap.put("productStock", product.getStock());
                Log.d("DetailActivity", product.getPid()+"");

                databaseReference.child(firebaseUser.getUid()).child("AddToCart").child(cartID).setValue(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ProductDetailActivity.this, "Add To A Cart", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });




//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Toast.makeText(DetailActivity.this, "Add To A Cart", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(DetailActivity.this, CartActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                });

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
}