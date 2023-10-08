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
        // 새로운 뷰 홀더 객체 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        ReviewViewHolder holder = new ReviewViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, @SuppressLint("reviewRecyclerView") int position) {
        // 뷰 홀더에 데이터 바인딩
        // 해당 포지션의 아이템 데이터를 가져와서 뷰에 표시함
//        Review review = reviewList.get(position);

        // 리뷰 제목과 내용을 뷰에 설정
//        holder.titleTextView.setText("리뷰 제목 : " + review.getRtitle());
        holder.contentTextView.setText(reviewList.get(position).getRcontent());
    }

    @Override
    public int getItemCount() {
        // 데이터 개수 반환
        if (reviewList != null) {
            return reviewList.size();
        }
        return 0;
    }

    // itemView를 저장하는 뷰 홀더 클래스
    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView contentTextView;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            // itemView의 각 요소를 뷰 홀더에 연결
//            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
        }

        public void bind(Review review) {
            // 리뷰 제목과 내용을 뷰에 설정
//            titleTextView.setText(review.getRtitle());
            contentTextView.setText(review.getRcontent());
        }
    }
}