//package com.example.greeningapp;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.RatingBar;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.util.ArrayList;
//import java.util.List;
//
////public class ReviewHistoryAdapter {
////    private ArrayList<ReviewData> reviewhistoryList;
////    private Context context;
////
////    FirebaseDatabase firebaseDatabase;
////    FirebaseAuth firebaseAuth;
////    DatabaseReference databaseReference;
////
////    public ReviewHistoryAdapter(ArrayList<ReviewData> reviewhistoryList, Context context) {
////        this.reviewhistoryList = reviewhistoryList;
////        this.context = context;
////        firebaseDatabase = FirebaseDatabase.getInstance();
////        firebaseAuth = FirebaseAuth.getInstance();
////    }
////
////
////    @NonNull
////    @Override
////    public ReviewHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
////        View view = LayoutInflater.from(context).inflate(R.layout.review_history_list, parent, false);
////        //ReviewAdapter.MyViewHolder holder = new ReviewAdapterReviewHistory.MyViewHolder(view);
////        ReviewHistoryViewHolder holder = new ReviewHistoryViewHolder(view);
////        return holder;
////    }
////
////    @Override
////    public void onBindViewHolder(@NonNull ReviewHistoryAdapter.ReviewHistoryViewHolder holder, int position) {
////        databaseReference = FirebaseDatabase.getInstance().getReference("Review");
////        Glide.with(holder.itemView).load(reviewhistoryList.get(position).getReview_image()).into(holder.recyclerImage);
////        holder.recyclerEt.setText(String.valueOf(reviewhistoryList.get(position).getWrite_review()));
////        holder.reviewdate.setText(String.valueOf(reviewhistoryList.get(position).getWrite_review()));
////        holder.recyclerRating.setRating(reviewhistoryList.get(position).getRating());
////    }
////
////    @Override
////    public int getItemCount() {
////        return reviewhistoryList.size();
////    }
////
////    public static class ReviewHistoryViewHolder extends RecyclerView.ViewHolder{
////        private ImageView recyclerImage;
////        private TextView recyclerEt;
////        private RatingBar recyclerRating;
////        private TextView reviewdate;
////
////
////        public ReviewHistoryViewHolder(@NonNull View itemView) {
////            super(itemView);
////
////            this.recyclerImage = itemView.findViewById(R.id.reviewhistoryPhoto);
////            this.recyclerEt = itemView.findViewById(R.id.reviewhistoryText);
////            this.recyclerRating = itemView.findViewById(R.id.reviewhistoryRate);
////            this.reviewdate = itemView.findViewById(R.id.reviewhistoryDate);
////        }
////    }
////}
//
//public class ReviewHistoryAdapter extends RecyclerView.Adapter<ReviewHistoryAdapter.ReviewHistoryViewHolder> {
//    private ArrayList<Review> reviewhistoryList;
//
//    List<MyOrder> MyOrderPList;
//    private Context context;
//
//    MyOrder product = null;
//
//    FirebaseDatabase database;
//    FirebaseAuth firebaseAuth;
//    DatabaseReference databaseReference;
//    DatabaseReference databaseReference2;
//
//    public ReviewHistoryAdapter(ArrayList<Review> reviewhistoryList, Context context) {
//        this.reviewhistoryList = reviewhistoryList;
//        //this.MyOrderPList = MyOrderPList;   //잠시 주석
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
//        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//        databaseReference = database.getReference("Review");
//        //databaseReference2 = FirebaseDatabase.getInstance().getReference("CurrentUser").child(firebaseUser.getUid()).child("MyOrder");   //잠시 주석
//
//        MyOrder myOrder = MyOrderPList.get(position);
//
//        Glide.with(holder.itemView).load(reviewhistoryList.get(position).getReview_image()).into(holder.recyclerImage);
//        holder.recyclerEt.setText(String.valueOf(reviewhistoryList.get(position).getWrite_review()));
//        holder.recyclerRating.setRating(reviewhistoryList.get(position).getRating());
//        holder.reviewdate.setText(String.valueOf(reviewhistoryList.get(position).getReview_date()));
////        holder.Pname.setText(reviewhistoryList.get(position).getProductName());
////        Glide.with(holder.itemView).load(reviewhistoryList.get(position).getOrderImg()).into(holder.Pimg);
//
//        //holder.Pname.setText(myOrder.getProductName()); // MyOrder에서 Pname 가져오기     //잠시 주석
//        //Glide.with(holder.itemView).load(myOrder.getOrderImg()).into(holder.Pimg); // MyOrder에서 Pimg 가져오기
//
////        // MyOrder 객체에서 주문 이미지와 제품 이름 가져오기
////        String Pimg = reviewhistoryList.get(position).getOrderImg();
////        String Pname = reviewhistoryList.get(position).getProductName();
////
////        // 뷰홀더에 주문 이미지 및 제품 이름 설정
////        Glide.with(holder.itemView).load(Pimg).into(holder.Pimg);
////        holder.Pname.setText(Pname);
//
//
//        Log.d("ReviewHistoryAdapter", "productImage: " + reviewhistoryList.get(position).getOrderImg());
//        Log.d("ReviewHistoryAdapter", "productname: " + reviewhistoryList.get(position).getProductName());
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
////        private TextView Pname;    //잠시 주석
////        private ImageView Pimg;
//
//        public ReviewHistoryViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            this.recyclerImage = itemView.findViewById(R.id.reviewhistoryPhoto);
//            this.recyclerEt = itemView.findViewById(R.id.reviewhistoryText);
//            this.recyclerRating = itemView.findViewById(R.id.reviewhistoryRate);
//            this.reviewdate = itemView.findViewById(R.id.reviewhistoryDate);
////            this.Pname = itemView.findViewById(R.id.reviewhistoryPn);   //잠시 주석
////            this.Pimg = itemView.findViewById(R.id.reviewhistoryPImg);
//        }
//    }
//}