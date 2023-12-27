package com.example.greeningapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class OrderHistoryParentRcyAdapter extends RecyclerView.Adapter<OrderHistoryParentRcyAdapter.MyViewHolder> {
    private ArrayList<MyOrder> parentModelArrayList;
    public Context cxt;

    public OrderHistoryParentRcyAdapter(ArrayList<MyOrder> parentModelArrayList ,Context context) {
        this.parentModelArrayList = parentModelArrayList;
        this.cxt = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_orderhistory_parent, parent, false);
        return new MyViewHolder(view);
    }

    //리사이클러뷰 아이템 개수 반환 설정
    @Override
    public int getItemCount() {
        if(parentModelArrayList != null){
            return parentModelArrayList.size();
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // 부모 데이터에서 날짜와 주문 ID를 가져옴
        holder.orderDate.setText(parentModelArrayList.get(position).getOrderDate());
        holder.ordh_OrderId.setText(parentModelArrayList.get(position).getOrderId());

        // 부모 데이터의 자식 데이터 리스트를 가져옴
        ArrayList<MyOrder> childModelArrayList = parentModelArrayList.get(position).getChildModelArrayList();

        //자식데이터 표시하기 위한 어댑터 설정
        OrderHistoryChildRcyAdapter childRecyclerViewAdapter = new OrderHistoryChildRcyAdapter(childModelArrayList, holder.childRecyclerView.getContext());
        holder.childRecyclerView.setAdapter(childRecyclerViewAdapter);

        // 자식 데이터를 표시할 레이아웃 매니저 설정
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(cxt, LinearLayoutManager.VERTICAL, false);
        holder.childRecyclerView.setLayoutManager(layoutManager);
        // RecyclerView 크기 고정
        holder.childRecyclerView.setHasFixedSize(true);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView orderDate;
        public RecyclerView childRecyclerView;
        public TextView ordh_OrderId;

        public MyViewHolder(View itemView) {
            super(itemView);

            ordh_OrderId = itemView.findViewById(R.id.ordh_OrderId);
            orderDate = itemView.findViewById(R.id.orderDate);
            childRecyclerView = itemView.findViewById(R.id.Child_RV);

        }
    }

}