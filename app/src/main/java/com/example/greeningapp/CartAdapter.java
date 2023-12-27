package com.example.greeningapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    Context context;
    List<Cart> cartList;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    // 화폐 단위 형식 설정
    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    public CartAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 뷰 홀더 객체 생성 및 반환
        return new CartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, @SuppressLint("RecyclerView") int position) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("CurrentUser");

        // 뷰에 데이터 바인딩 + 화폐 단위 처리
        Glide.with(holder.itemView)
                .load(cartList.get(position).getProductImg())
                .into(holder.productImg);
        holder.name.setText(cartList.get(position).getProductName());
        holder.price.setText(decimalFormat.format(Integer.parseInt(cartList.get(position).getProductPrice())) + "원");
        holder.quantity.setText(decimalFormat.format(cartList.get(position).getSelectedQuantity()) +"개");
        holder.totalPrice.setText(String.valueOf(decimalFormat.format(cartList.get(position).getTotalPrice())) + "원");

        // 장바구니에 담겨 있는 상품 삭제 시
        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 해당 상품 장바구니 데이터베이스에서 삭제
                databaseReference.child(firebaseUser.getUid()).child("AddToCart").child(cartList.get(position).getDataId())
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // 삭제 후 액티비티 재실행
                                if (task.isSuccessful()) {
                                    cartList.remove(cartList.get(position));
                                    notifyDataSetChanged();
                                    Intent intent = ((CartActivity)context).getIntent();
                                    ((CartActivity)context).finish(); //현재 액티비티 종료 실시
                                    ((CartActivity)context).overridePendingTransition(0, 0); //효과 없애기
                                    ((CartActivity)context).startActivity(intent); //현재 액티비티 재실행 실시
                                    ((CartActivity)context).overridePendingTransition(0, 0); //효과 없애기
                                    Log.d("CartAdapter", "item Delete");
                                } else {
                                    Log.d("CartAdapter",  "Error" + task.getException().getMessage());
                                }
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        // 목록이 비어 있지 않으면 목록의 크기 반환, 비어 있으면 0 반환
        if (cartList != null) {
            return cartList.size();
        }
        return 0;
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, quantity, totalPrice;
        ImageView deleteItem, productImg;

        // 뷰에 대한 참조
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.product_price);
            quantity = itemView.findViewById(R.id.total_quantity);
            totalPrice = itemView.findViewById(R.id.total_price);
            deleteItem = itemView.findViewById(R.id.delete_item);
            productImg = itemView.findViewById(R.id.cart_pimg);
        }
    }
}