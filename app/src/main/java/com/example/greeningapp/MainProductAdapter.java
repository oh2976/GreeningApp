package com.example.greeningapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MainProductAdapter extends RecyclerView.Adapter<MainProductAdapter.CustomViewHolder> {
    private ArrayList<Product> arrayList;
    private Context context;

    public MainProductAdapter(ArrayList<Product> arrayList, MainActivity mainActivity) {
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
    public void onBindViewHolder(@NonNull MainProductAdapter.CustomViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getPimg())
                .into(holder.iv_pimg);
        holder.pname.setText(arrayList.get(position).getPname());
        holder.tv_pprice.setText(String.valueOf(arrayList.get(position).getPprice()));
    }

    @Override
    public int getItemCount() {
        //삼합연산자
        return (arrayList !=null ? arrayList.size() :0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_pimg;
        TextView pname;
        TextView tv_pprice;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_pimg = itemView.findViewById(R.id.iv_pimg);
            this.pname = itemView.findViewById(R.id.pname);
            this.tv_pprice = itemView.findViewById(R.id.tv_pprice);
        }
    }
}
