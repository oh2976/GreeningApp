package com.example.greeningapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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

    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    int totalPrice = 0;


    public CartAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, @SuppressLint("RecyclerView") int position) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("CurrentUser");
        String cartID = databaseReference.push().getKey();

        Glide.with(holder.itemView)
                .load(cartList.get(position).getProductImg())
                .into(holder.productImg);
        holder.name.setText(cartList.get(position).getProductName());
        holder.price.setText(decimalFormat.format(Integer.parseInt(cartList.get(position).getProductPrice())) + "원");
        holder.quantity.setText(decimalFormat.format(cartList.get(position).getSelectedQuantity()) +"개");
        holder.totalPrice.setText(String.valueOf(decimalFormat.format(cartList.get(position).getTotalPrice())) + "원");

        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference.child(firebaseUser.getUid()).child("AddToCart").child(cartList.get(position).getDataId())
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    cartList.remove(cartList.get(position));
                                    notifyDataSetChanged();
                                    Intent intent = ((CartActivity)context).getIntent();
                                    ((CartActivity)context).finish(); //현재 액티비티 종료 실시
                                    ((CartActivity)context).overridePendingTransition(0, 0); //효과 없애기
                                    ((CartActivity)context).startActivity(intent); //현재 액티비티 재실행 실시
                                    ((CartActivity)context).overridePendingTransition(0, 0); //효과 없애기
//                                    ((CartActivity) context).recreate();
//                                    Toast.makeText(context, "item Delete", Toast.LENGTH_SHORT).show();
                                } else {
//                                    Toast.makeText(context, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        if (cartList != null) {
            return cartList.size();
        }
        return 0;
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {

        TextView name, price, quantity, totalPrice, cartTotalPrice;

        ImageView deleteItem, productImg;

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