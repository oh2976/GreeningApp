package com.example.greeningapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{
    private ArrayList<Product> arrayList;
    private Context context;

    public ProductAdapter(ArrayList<Product> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 뷰에 레이아웃 연결
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        // 뷰 홀더 객체 생성 및 반환
        ProductViewHolder holder = new ProductViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getPimg())
                .into(holder.imageView);
        holder.textName.setText("상품명 :" + arrayList.get(position).getPname());
        holder.textPrice.setText("가격 : " + String.valueOf(arrayList.get(position).getPprice()));
        holder.textStock.setText("재고수량 : " + String.valueOf(arrayList.get(position).getStock()));
        holder.textPopulstock.setText("총 판매량 : " + String.valueOf(arrayList.get(position).getPopulstock()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ManageProductDetailActivity.class);
                intent.putExtra("ManageProductDetail", arrayList.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        // 목록이 비어 있지 않으면 목록의 크기 반환, 비어 있으면 0 반환
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textName;
        TextView textPrice;
        TextView textStock;
        ImageView detailImg;
        TextView textPopulstock;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textName = itemView.findViewById(R.id.textName);
            this.textPrice = itemView.findViewById(R.id.textPrice);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.textStock = itemView.findViewById(R.id.textStock);
            this.detailImg = imageView.findViewById(R.id.detail_longimg);
            this.textPopulstock = itemView.findViewById(R.id.textPopulstock);
        }
    }
}