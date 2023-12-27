package com.example.greeningapp;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class ReviewHistoryAdapter extends RecyclerView.Adapter<ReviewHistoryAdapter.ReviewHistoryViewHolder> {
    private ArrayList<Review> reviewhistoryList;
    private Context context;

    DecimalFormat decimalFormat = new DecimalFormat("###,###"); //가격에 "," 처리

    int ProductPrice = 0;


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
        Glide.with(holder.itemView).load(reviewhistoryList.get(position).getRimage()).into(holder.recyclerImage); // 후기 사진
        holder.recyclerEt.setText(String.valueOf(reviewhistoryList.get(position).getRcontent()));
        holder.reviewdate.setText(String.valueOf(reviewhistoryList.get(position).getRdatetime()));
        holder.ProductPrice.setText(String.valueOf(decimalFormat.format(reviewhistoryList.get(position).getPprice())) + "원"); // 가격, " - 원"으로 표시되도록 설정
        holder.ProductName.setText(String.valueOf(reviewhistoryList.get(position).getPname()));
        holder.TotalQ.setText(String.valueOf(decimalFormat.format(reviewhistoryList.get(position).getTotalquantity())) + "개"); // 구매 수량, " - 개"로 표시되도록 설정
        Glide.with(holder.itemView).load(reviewhistoryList.get(position).getPimg()).into(holder.ProductImg); // 상품 이미지
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
        private TextView reviewdate;
        private TextView ProductName;
        private ImageView ProductImg;
        private TextView ProductPrice;
        private TextView TotalQ;

        public ReviewHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            this.recyclerImage = itemView.findViewById(R.id.reviewhistoryPhoto);
            this.recyclerEt = itemView.findViewById(R.id.reviewhistoryText);
            this.reviewdate = itemView.findViewById(R.id.reviewhistoryDate);
            this.ProductPrice = itemView.findViewById(R.id.reviewhistoryprice);
            this.ProductName = itemView.findViewById(R.id.reviewhistoryPn);
            this.ProductImg = itemView.findViewById(R.id.reviewhistoryPImg);
            this.TotalQ = itemView.findViewById(R.id.reviewhistoryquantity);
        }
    }
}