package com.example.greeningapp;
import android.annotation.SuppressLint;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

//public class ReviewHistoryAdapter {
//    private ArrayList<ReviewData> reviewhistoryList;
//    private Context context;
//
//    FirebaseDatabase firebaseDatabase;
//    FirebaseAuth firebaseAuth;
//    DatabaseReference databaseReference;
//
//    public ReviewHistoryAdapter(ArrayList<ReviewData> reviewhistoryList, Context context) {
//        this.reviewhistoryList = reviewhistoryList;
//        this.context = context;
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        firebaseAuth = FirebaseAuth.getInstance();
//    }
//
//
//    @NonNull
//    @Override
//    public ReviewHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.review_history_list, parent, false);
//        //ReviewAdapter.MyViewHolder holder = new ReviewAdapterReviewHistory.MyViewHolder(view);
//        ReviewHistoryViewHolder holder = new ReviewHistoryViewHolder(view);
//        return holder;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ReviewHistoryAdapter.ReviewHistoryViewHolder holder, int position) {
//        databaseReference = FirebaseDatabase.getInstance().getReference("Review");
//        Glide.with(holder.itemView).load(reviewhistoryList.get(position).getReview_image()).into(holder.recyclerImage);
//        holder.recyclerEt.setText(String.valueOf(reviewhistoryList.get(position).getWrite_review()));
//        holder.reviewdate.setText(String.valueOf(reviewhistoryList.get(position).getWrite_review()));
//        holder.recyclerRating.setRating(reviewhistoryList.get(position).getRating());
//    }
//
//    @Override
//    public int getItemCount() {
//        return reviewhistoryList.size();
//    }
//
//    public static class ReviewHistoryViewHolder extends RecyclerView.ViewHolder{
//        private ImageView recyclerImage;
//        private TextView recyclerEt;
//        private RatingBar recyclerRating;
//        private TextView reviewdate;
//
//
//        public ReviewHistoryViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            this.recyclerImage = itemView.findViewById(R.id.reviewhistoryPhoto);
//            this.recyclerEt = itemView.findViewById(R.id.reviewhistoryText);
//            this.recyclerRating = itemView.findViewById(R.id.reviewhistoryRate);
//            this.reviewdate = itemView.findViewById(R.id.reviewhistoryDate);
//        }
//    }
//}


///////////////
//public class ReviewHistoryAdapter extends RecyclerView.Adapter<ReviewHistoryAdapter.ReviewHistoryViewHolder> {
//    private ArrayList<ReviewData> reviewhistoryList;
//    private Context context;
//
//    FirebaseDatabase database;
//    FirebaseAuth firebaseAuth;
//    DatabaseReference databaseReference;
//
//
//    public ReviewHistoryAdapter(ArrayList<ReviewData> reviewhistoryList, Context context) {
//        this.reviewhistoryList = reviewhistoryList;
//        this.context = context;
//        database = FirebaseDatabase.getInstance();
//        firebaseAuth = FirebaseAuth.getInstance();
//    }
//
//    @NonNull
//    @Override
//    public ReviewHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.review_history_list, parent, false);
//        ReviewHistoryViewHolder holder = new ReviewHistoryViewHolder(view);
//        return holder;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ReviewHistoryViewHolder holder, @SuppressLint("RecyclerView") int position) {
//        databaseReference = database.getReference("Review");
//        Glide.with(holder.itemView).load(reviewhistoryList.get(position).getReview_image()).into(holder.recyclerImage);
//        holder.recyclerEt.setText(String.valueOf(reviewhistoryList.get(position).getWrite_review()));
//        holder.recyclerRating.setRating(reviewhistoryList.get(position).getRating());
//        holder.reviewdate.setText(String.valueOf(reviewhistoryList.get(position).getReview_date()));
//
//    }
//
//    @Override
//    public int getItemCount() {
//        if (reviewhistoryList != null) {
//            return reviewhistoryList.size();
//        }
//        //return reviewhistoryList.size();
//        return 0;
//    }
//
//    public class ReviewHistoryViewHolder extends RecyclerView.ViewHolder {
//        private ImageView recyclerImage;
//        private TextView recyclerEt;
//        private RatingBar recyclerRating;
//        private TextView reviewdate;
//
//
//        public ReviewHistoryViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            this.recyclerImage = itemView.findViewById(R.id.reviewhistoryPhoto);
//            this.recyclerEt = itemView.findViewById(R.id.reviewhistoryText);
//            this.recyclerRating = itemView.findViewById(R.id.reviewhistoryRate);
//            this.reviewdate = itemView.findViewById(R.id.reviewhistoryDate);
//
//        }
//    }
//}
//////////////////

public class ReviewHistoryAdapter extends RecyclerView.Adapter<ReviewHistoryAdapter.ReviewHistoryViewHolder> {
    private ArrayList<Review> reviewhistoryList;
    private Context context;

//    FirebaseDatabase database;
//    FirebaseAuth firebaseAuth;
//    DatabaseReference databaseReference;


    public ReviewHistoryAdapter(ArrayList<Review> reviewhistoryList, Context context) {
        this.reviewhistoryList = reviewhistoryList;
        this.context = context;
//        database = FirebaseDatabase.getInstance();
//        firebaseAuth = FirebaseAuth.getInstance();
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
        //databaseReference = database.getReference("Review");
        Glide.with(holder.itemView).load(reviewhistoryList.get(position).getRimage()).into(holder.recyclerImage);
//        String reviewImage = reviewhistoryList.get(position).getRimage();
//        if (reviewImage != null && !reviewImage.isEmpty()) {
//            Glide.with(context).load(reviewImage).into(holder.recyclerImage);
//        } else {
//            // 이미지가 없을 때 이미지 없음 아이콘 표시
//            //holder.recyclerImage.setImageResource(R.drawable.no_image); // 이미지 없음 아이콘 등록
//        }

        holder.recyclerEt.setText(String.valueOf(reviewhistoryList.get(position).getRcontent()));
        //holder.recyclerRating.setRating(reviewhistoryList.get(position).getRscore());
        holder.reviewdate.setText(String.valueOf(reviewhistoryList.get(position).getRdatetime()));
        holder.ProductPrice.setText(String.valueOf(reviewhistoryList.get(position).getPprice()) + "원");
        holder.ProductName.setText(String.valueOf(reviewhistoryList.get(position).getPname()));
        holder.TotalQ.setText(String.valueOf(reviewhistoryList.get(position).getTotalquantity())+ "개");
        Glide.with(holder.itemView).load(reviewhistoryList.get(position).getPimg()).into(holder.ProductImg);

    }

    @Override
    public int getItemCount() {
        if (reviewhistoryList != null) {
            return reviewhistoryList.size();
        }
        //return (dataList!=null ? reviewhistoryList.size() :0);
        return 0;
    }

    public class ReviewHistoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView recyclerImage;
        private TextView recyclerEt;
        //private RatingBar recyclerRating;
        private TextView reviewdate;

        private TextView ProductName;
        private ImageView ProductImg;

        private TextView ProductPrice; //추가
        private TextView TotalQ; //추가

        //private String username;


        public ReviewHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            this.recyclerImage = itemView.findViewById(R.id.reviewhistoryPhoto);
            this.recyclerEt = itemView.findViewById(R.id.reviewhistoryText);
            //this.recyclerRating = itemView.findViewById(R.id.reviewhistoryRate);
            this.reviewdate = itemView.findViewById(R.id.reviewhistoryDate);
            this.ProductPrice = itemView.findViewById(R.id.reviewhistoryprice);
            this.ProductName = itemView.findViewById(R.id.reviewhistoryPn);
            this.ProductImg = itemView.findViewById(R.id.reviewhistoryPImg);
            this.TotalQ = itemView.findViewById(R.id.reviewhistoryquantity);

        }
    }
}