package com.example.greeningapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.greeningapp.Product;
import com.example.greeningapp.ProductDetailActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainProductAdapter  extends RecyclerView.Adapter<MainProductAdapter.CustomViewHolder> {
    private ArrayList<Product> arrayList;
    private Context context;

    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    public MainProductAdapter(ArrayList<Product> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MainProductAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mainlist_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainProductAdapter.CustomViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getPimg())
                .into(holder.iv_pimg);
        holder.pname.setText(arrayList.get(position).getPname());
//        holder.tv_pprice.setText(String.valueOf(arrayList.get(position).getPprice()));
        holder.tv_pprice.setText(String.valueOf(decimalFormat.format(arrayList.get(position).getPprice())) + "원");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("detail", arrayList.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        //삼합연산자
//        return (arrayList !=null ? arrayList.size() :0);
        return Math.min(arrayList.size(), 6); //가로 3개 총 6개
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_pimg;
        TextView pname;
        TextView tv_pprice;

//        RatingBar reviewRating01;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_pimg = itemView.findViewById(R.id.iv_pimg);
            this.pname = itemView.findViewById(R.id.pname);
            this.tv_pprice = itemView.findViewById(R.id.tv_pprice);
//            this.reviewRating01 = itemView.findViewById(R.id.reviewRating01);
        }
    }
}