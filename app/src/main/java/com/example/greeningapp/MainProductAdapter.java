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
import com.example.greeningapp.Product;
import com.example.greeningapp.ProductDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainProductAdapter  extends RecyclerView.Adapter<MainProductAdapter.CustomViewHolder> {
    private ArrayList<Product> arrayList;
    private Context context;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private DatabaseReference databaseReferenceReview;

    private int QuantityReview;


    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    public MainProductAdapter(ArrayList<Product> arrayList, Context context) {
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
    public void onBindViewHolder(@NonNull MainProductAdapter.CustomViewHolder holder, @SuppressLint("RecyclerView") int position) {
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReferenceReview = FirebaseDatabase.getInstance().getReference("Review");
        Query reviewQuery = databaseReferenceReview.orderByChild("pid").equalTo(arrayList.get(position).getPid()); // populstock 내림차순 최대 10개상품가져옴
        reviewQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                QuantityReview = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Review review = snapshot.getValue(Review.class);
                    Log.d("pid",review.getRcontent() +"가져왔음");
                    QuantityReview += 1;     //총점개수
                }
                holder.tv_reviewQ.setText("("+QuantityReview+")");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ReviewActivity", String.valueOf(databaseError.toException())); //에러문출력
            }
        });

        reviewQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float totalRating = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    float rating = snapshot.child("rscore").getValue(Float.class);
                    totalRating += rating;             //총점
                }
                float averageRating = 0;
                if (QuantityReview != 0) {
                    averageRating = totalRating / QuantityReview;
                }
                float TotalReview = Math.round(averageRating * 5 / 5.0f);    //총점 반올림 o
//                float TotalReview = averageRating * 5 / 5.0f;           //반올림 x

                holder.reviewRating01.setRating(TotalReview);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 오류 처리 코드를 추가하세요.
            }
        });

        Glide.with(holder.itemView)
                .load(arrayList.get(position).getPimg())
                .into(holder.iv_pimg);
        holder.pname.setText(arrayList.get(position).getPname());
        holder.tv_pprice.setText(String.valueOf(decimalFormat.format(arrayList.get(position).getPprice())) + "원");


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
        return Math.min(arrayList.size(), 6); //가로 3 총 6개
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_pimg;
        TextView pname;
        TextView tv_pprice, tv_reviewQ;

        RatingBar reviewRating01; //총점

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_pimg = itemView.findViewById(R.id.iv_pimg);
            this.pname = itemView.findViewById(R.id.pname);
            this.tv_pprice = itemView.findViewById(R.id.tv_pprice);
            this.tv_reviewQ = itemView.findViewById(R.id.tv_reviewQ);
            this.reviewRating01 = itemView.findViewById(R.id.reviewRating01);
        }
    }
}