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

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{

    private List<Review> reviewList;
    private Context context;

    public ReviewAdapter(List<Review> reviewList, Context context){
        this.reviewList = reviewList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        ReviewViewHolder holder = new ReviewViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ReviewViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(reviewList.get(position).getRid())
                .into(holder.inputimg);
        holder.reviewdes.setText(String.valueOf(reviewList.get(position).getRcontent()));
        holder.userrating.setRating(reviewList.get(position).getRscore());
        holder.reviewdate.setText(reviewList.get(position).getRdatetime());

    }

    @Override
    public int getItemCount() {
        if (reviewList != null) {
            return reviewList.size();
        }
        return 0;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView inputimg;
        RatingBar userrating;
        TextView reviewdes;
        TextView reviewdate;
        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            this.inputimg = itemView.findViewById(R.id.inputimg);
            this.reviewdes = itemView.findViewById(R.id.reviewdes);
            this.userrating = itemView.findViewById(R.id.userrating);
            this.reviewdate = itemView.findViewById(R.id.reviewdate);
        }
    }
}