package com.example.greeningapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class OrderHistoryChildRcyAdapter extends RecyclerView.Adapter<OrderHistoryChildRcyAdapter.ChildViewHolder> {

    public ArrayList<MyOrder> childModelArrayList;
    Context cxt;

    public OrderHistoryChildRcyAdapter(ArrayList<MyOrder> childModelArrayList, Context mContext) {
        this.cxt = mContext;
        this.childModelArrayList = childModelArrayList;
    }

    @Override
    public ChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_orderhistory_child, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder,  @SuppressLint("RecyclerView") int position) {
        Glide.with(holder.itemView)
                .load(childModelArrayList.get(position).getOrderImg())
                .into(holder.orderhistory_img);
        holder.pro_name.setText(childModelArrayList.get(position).getProductName());
        holder.pro_price.setText(childModelArrayList.get(position).getProductPrice() + "원");
        holder.ordervalue.setText(childModelArrayList.get(position).getTotalQuantity() + "개");

        String state = childModelArrayList.get(position).getOrderstate();

        //주문 상태에 따라 상태 처리 표시설정
        if("paid".equals(state)){
            holder.OrderState_orderhistory.setText("결제 완료");
            holder.ordhreviewBtn.setVisibility(View.INVISIBLE);
        } else if("shipped".equals(state)){
            holder.OrderState_orderhistory.setText("배송 완료");
            holder.ordhreviewBtn.setVisibility(View.VISIBLE);
        }

        String isReviewCompleted = childModelArrayList.get(position).getDoReview();

        // 후기 작성 여부에 따라 버튼 설정
        if ("No".equals(isReviewCompleted)) {

        } else if ("Yes".equals(isReviewCompleted)) {
            //후기 작성 완료시 처리
            holder.ordhreviewBtn.setText("후기 작성완료");
            holder.ordhreviewBtn.setBackgroundTintList(ColorStateList.valueOf(cxt.getResources().getColor(R.color.ordh_btn_click))); //버튼색변경
            holder.ordhreviewBtn.setTextColor(cxt.getResources().getColor(R.color.white)); // 글자색 변경
            holder.ordhreviewBtn.setEnabled(false);
        }

        holder.ordhreviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("No".equals(isReviewCompleted)) {
                    // 후기 작성이 안된 경우 이동할 엑티비티 설정
                    Intent intent = new Intent(cxt, ReviewWriteActivity.class);
                    intent.putExtra("product", childModelArrayList.get(position));
                    cxt.startActivity(intent);
                    ((Activity)cxt).finish();

                } else if ("Yes".equals(isReviewCompleted)) {

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (childModelArrayList != null) {
            return childModelArrayList.size();
        }
        return 0;
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {
        public ImageView orderhistory_img;
        public TextView pro_name, pro_price, ordervalue;
        TextView OrderState_orderhistory;
        AppCompatButton ordhreviewBtn;

        public ChildViewHolder(View itemView) {
            super(itemView);
            orderhistory_img = itemView.findViewById(R.id.orderhistory_img);
            pro_name = itemView.findViewById(R.id.pro_name);
            pro_price = itemView.findViewById(R.id.pro_price);
            ordervalue = itemView.findViewById(R.id.ordervalue);
            ordhreviewBtn = itemView.findViewById(R.id.ordhreviewBtn);
            this.OrderState_orderhistory = itemView.findViewById(R.id.OrderState_orderhistory);
        }
    }
}