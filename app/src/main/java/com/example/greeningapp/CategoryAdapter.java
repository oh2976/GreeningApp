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

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHorder> {

    private ArrayList<Product> arrayList;
    private Context context;

    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    public CategoryAdapter(ArrayList<Product> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryViewHorder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        CategoryViewHorder holder = new CategoryViewHorder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHorder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getPimg())
                .into(holder.pimg);
        holder.pname.setText(arrayList.get(position).getPname());
        holder.psay.setText(arrayList.get(position).getPsay());
        holder.pprice.setText(String.valueOf(decimalFormat.format(arrayList.get(position).getPprice())) + "원");

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
        return (arrayList !=null ? arrayList.size() : 0);
    }

    public class CategoryViewHorder extends RecyclerView.ViewHolder {
        ImageView pimg;
        TextView pname;
        TextView psay;
        TextView pprice;

        public CategoryViewHorder(@NonNull View itemView) {
            super(itemView);
            this.pimg= itemView.findViewById(R.id.searchpimg);
            this.pname= itemView.findViewById(R.id.searchpname);
            this.psay= itemView.findViewById(R.id.searchpsay);
            this.pprice= itemView.findViewById(R.id.searchpprice);
        }
    }
}