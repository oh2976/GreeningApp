package com.example.greeningapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.CustomViewHolder>{
    private ArrayList<Review> dataList;
    private Context context;

    public ReviewAdapter(ArrayList<Review> dataList,  Context context ) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(dataList.get(position).getRimage())
                .into(holder.inputimg);
        holder.reviewdes.setText(String.valueOf(dataList.get(position).getRcontent()));
        holder.userrating.setRating(dataList.get(position).getRscore());
        holder.reviewdate.setText(dataList.get(position).getRdatetime());
        holder.username.setText(dataList.get(position).getUsername());

    }

    @Override
    public int getItemCount() {
        //삼합연산자
        return (dataList !=null ? dataList.size() :0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView inputimg;
        RatingBar userrating;
        TextView username;
        TextView reviewdes;
        TextView reviewdate;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.inputimg = itemView.findViewById(R.id.inputimg);
            this.username = itemView.findViewById(R.id.username);
            this.reviewdes = itemView.findViewById(R.id.reviewdes);
            this.userrating = itemView.findViewById(R.id.userrating);
            this.reviewdate = itemView.findViewById(R.id.reviewdate);

        }
    }


}