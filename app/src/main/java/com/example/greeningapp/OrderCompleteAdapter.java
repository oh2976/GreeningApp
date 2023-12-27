package com.example.greeningapp;

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
import java.util.List;

public class OrderCompleteAdapter extends RecyclerView.Adapter<OrderCompleteAdapter.OrderCompleteViewHolder>{
    Context context;
    List<MyOrder> myOrderList;
    // 화폐 단위 형식 객체 생성
    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    public OrderCompleteAdapter(Context context, List<MyOrder> myOrderList){
        this.context = context;
        this.myOrderList = myOrderList;

    }

    @NonNull
    @Override
    public OrderCompleteAdapter.OrderCompleteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderCompleteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderCompleteAdapter.OrderCompleteViewHolder holder, int position) {
        // 뷰에 데이터 바인딩 + 화폐 단위 처리
        Glide.with(holder.itemView)
                .load(myOrderList.get(position).getOrderImg())
                .into(holder.pimg_orderitem);
        holder.pName_orderitem.setText(myOrderList.get(position).getProductName());
        holder.pPrice_orderitem.setText(String.valueOf(decimalFormat.format(myOrderList.get(position).getTotalPrice())) + "원");
        holder.pQauntity_orderitem.setText(decimalFormat.format(myOrderList.get(position).getTotalQuantity()) + "개");
    }

    @Override
    public int getItemCount() {
        // 목록이 비어 있지 않으면 목록의 크기 반환, 비어 있으면 0 반환
        if(myOrderList != null){
            return myOrderList.size();
        }
        return 0;
    }

    public class OrderCompleteViewHolder extends RecyclerView.ViewHolder {
        ImageView pimg_orderitem;
        TextView pName_orderitem, pPrice_orderitem, pQauntity_orderitem;

        // 뷰에 대한 참조
        public OrderCompleteViewHolder(@NonNull View itemView) {
            super(itemView);

            pimg_orderitem = itemView.findViewById(R.id.pimg_orderitem);
            pName_orderitem = itemView.findViewById(R.id.pName_orderitem);
            pPrice_orderitem = itemView.findViewById(R.id.pPrice_orderitem);
            pQauntity_orderitem = itemView.findViewById(R.id.pQauntity_orderitem);
        }
    }
}
