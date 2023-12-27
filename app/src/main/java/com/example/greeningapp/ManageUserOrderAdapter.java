package com.example.greeningapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class ManageUserOrderAdapter extends RecyclerView.Adapter<ManageUserOrderAdapter.ManageUserOrderViewHolder> {
    private ArrayList<MyOrder> arrayList;
    private Context context;

    public ManageUserOrderAdapter(ArrayList<MyOrder> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ManageUserOrderAdapter.ManageUserOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 뷰에 레이아웃 연결
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_order_item, parent, false);
        // 뷰 홀더 객체 생성 및 반환
        ManageUserOrderAdapter.ManageUserOrderViewHolder holder = new ManageUserOrderAdapter.ManageUserOrderViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ManageUserOrderAdapter.ManageUserOrderViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // 뷰에 데이터 바인딩
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getOrderImg())
                .into(holder.MGProductImg_order);

        holder.MGOrderID_order.setText(arrayList.get(position).getOrderId());
        holder.MGOrderDate_order.setText(arrayList.get(position).getOrderDate());
        holder.MGProductName_order.setText(arrayList.get(position).getProductName());
        holder.MGOrderPrice_order.setText(arrayList.get(position).getTotalPrice() + "원");
        holder.MGOrderStock_order.setText(arrayList.get(position).getTotalQuantity() + "개");
        holder.MGOrderState_order.setText(arrayList.get(position).getOrderstate());
        holder.MGEachOrderId_order.setText(arrayList.get(position).getEachOrderedId());

        // itemView 클릭 시 해당 데이터를 list 형식으로 처리 후 회원 주문 내역 페이지로 이동
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ManageUserOrderDetailActivity.class);
                intent.putExtra("ManageUserOrderDetail", arrayList.get(position));
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        // 목록이 비어 있지 않으면 목록의 크기 반환, 비어 있으면 0 반환
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    public class ManageUserOrderViewHolder extends RecyclerView.ViewHolder {
        ImageView MGProductImg_order;
        TextView MGOrderID_order, MGOrderDate_order, MGProductName_order, MGOrderPrice_order, MGOrderStock_order, MGOrderState_order, MGEachOrderId_order;

        // 뷰에 대한 참조
        public ManageUserOrderViewHolder(@NonNull View itemView) {
            super(itemView);

            this.MGProductImg_order = itemView.findViewById(R.id.MGProductImg_order);
            this.MGOrderID_order = itemView.findViewById(R.id.MGOrderID_order);
            this.MGOrderDate_order = itemView.findViewById(R.id.MGOrderDate_order);
            this.MGProductName_order = itemView.findViewById(R.id.MGProductName_order);
            this.MGOrderPrice_order = itemView.findViewById(R.id.MGOrderPrice_order);
            this.MGOrderStock_order = itemView.findViewById(R.id.MGOrderStock_order);
            this.MGOrderState_order = itemView.findViewById(R.id.MGOrderState_order);
            this.MGEachOrderId_order = itemView.findViewById(R.id.MGEachOrderId_order);
;        }
    }
}
