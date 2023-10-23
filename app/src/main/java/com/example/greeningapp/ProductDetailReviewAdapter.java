package com.example.greeningapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductDetailReviewAdapter extends RecyclerView.Adapter<ProductDetailReviewAdapter.ReviewViewHolder> {
    private List<Review> reviewList;
    private Context context;

    public ProductDetailReviewAdapter(List<Review> reviewList, Context context) {
        this.reviewList = reviewList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        ReviewViewHolder holder = new ReviewViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, @SuppressLint("reviewRecyclerView") int position) {
        holder.contentTextView.setText(reviewList.get(position).getRcontent());
    }

    @Override
    public int getItemCount() {
        if (reviewList != null) {
            return reviewList.size();
        }
        return 0;
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView contentTextView;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
        }

        public void bind(Review review) {
            contentTextView.setText(review.getRcontent());
        }
    }
}