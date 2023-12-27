package com.example.greeningapp;

import android.annotation.SuppressLint;
import android.content.Intent;
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

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.myviewholder> {
    private List<Product> searchResults;
    DecimalFormat decimalFormat = new DecimalFormat("###,###");
    public SearchAdapter(List<Product> searchResults) {
        this.searchResults = searchResults;
    }

    // 뷰홀더 생성
    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        myviewholder holder = new myviewholder(view);
        return holder;
    }

    // 뷰홀더에 데이터 바인딩
    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, @SuppressLint("RecyclerView") int position) {
        // Glide 사용하여 이미지 로드
        // 상품명, 상품설명, 상품 가격 설정
        Glide.with(holder.itemView)
                .load(searchResults.get(position).getPimg())
                .into(holder.pimg);
        holder.pname.setText(searchResults.get(position).getPname());
        holder.psay.setText(searchResults.get(position).getPsay());
        holder.pprice.setText(String.valueOf(decimalFormat.format(searchResults.get(position).getPprice())) + "원");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            // 아이템을 클릭한 상품의 상세 화면으로 이동
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
                intent.putExtra("detail", searchResults.get(position));
                v.getContext().startActivity(intent);
//                ((Activity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    class myviewholder extends RecyclerView.ViewHolder {
        ImageView pimg;
        TextView pname, pprice, psay;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            // XML 레이아웃에서 각 위젯을 찾아서 변수에 할당
            pimg = itemView.findViewById(R.id.searchpimg);
            pname = itemView.findViewById(R.id.searchpname);
            psay = itemView.findViewById(R.id.searchpsay);
            pprice = itemView.findViewById(R.id.searchpprice);

        }
    }
}