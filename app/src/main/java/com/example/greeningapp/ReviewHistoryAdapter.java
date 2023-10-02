package com.example.greeningapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
public class ReviewHistoryAdapter extends RecyclerView.Adapter<ReviewHistoryAdapter.ReviewHistoryViewHolder> {
    private ArrayList<Review> reviewhistoryList;
    private Context context;


    public ReviewHistoryAdapter(ArrayList<Review> reviewhistoryList, Context context) {
        this.reviewhistoryList = reviewhistoryList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_history_list, parent, false);
        ReviewHistoryViewHolder holder = new ReviewHistoryViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHistoryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(holder.itemView).load(reviewhistoryList.get(position).getRimage()).into(holder.recyclerImage);
        holder.recyclerEt.setText(String.valueOf(reviewhistoryList.get(position).getRcontent()));
        holder.recyclerRating.setRating(reviewhistoryList.get(position).getRscore());
        holder.reviewdate.setText(String.valueOf(reviewhistoryList.get(position).getRdatetime()));

        holder.ProductName.setText(String.valueOf(reviewhistoryList.get(position).getPname()));
        Glide.with(holder.itemView).load(reviewhistoryList.get(position).getPimg()).into(holder.ProductImg);

    }

    @Override
    public int getItemCount() {
        if (reviewhistoryList != null) {
            return reviewhistoryList.size();
        }
        return 0;
    }

    public class ReviewHistoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView recyclerImage;
        private TextView recyclerEt;
        private RatingBar recyclerRating;
        private TextView reviewdate;

        private TextView ProductName;
        private ImageView ProductImg;

        //private String username;


        public ReviewHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            this.recyclerImage = itemView.findViewById(R.id.reviewhistoryPhoto);
            this.recyclerEt = itemView.findViewById(R.id.reviewhistoryText);
            this.recyclerRating = itemView.findViewById(R.id.reviewhistoryRate);
            this.reviewdate = itemView.findViewById(R.id.reviewhistoryDate);

            this.ProductName = itemView.findViewById(R.id.reviewhistoryPn);
            this.ProductImg = itemView.findViewById(R.id.reviewhistoryPImg);

        }
    }
}