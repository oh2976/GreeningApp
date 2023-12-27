package com.example.greeningapp;

import android.annotation.SuppressLint;
import android.app.Activity;
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

public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.DonationViewHolder> {
    private Context context;
    private ArrayList<Donation> donationList;

    public DonationAdapter(ArrayList<Donation> donationList, Context context){
        this.donationList = donationList;
        this.context = context;
    }

    @NonNull
    @Override
    public DonationAdapter.DonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 뷰에 레이아웃 연결
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.donation_item, parent, false);
        // 뷰 홀더 객체 생성 및 반환
        DonationViewHolder holder = new DonationViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull DonationAdapter.DonationViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // 뷰에 데이터 바인딩
        Glide.with(holder.itemView)
                .load(donationList.get(position).getDonationimg())
                .into(holder.donationImg);
        holder.donationName.setText(donationList.get(position).getDonationname());
        holder.donationStart.setText(donationList.get(position).getDonationstart());
        holder.donationEnd.setText(donationList.get(position).getDonationend());

        // itemView 클릭 시 해당 데이터를 list 형식으로 처리 후 기부 상세 내역 페이지로 이동
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DonationDetailActivity.class);
                intent.putExtra("donationDetail", donationList.get(position));
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        // 목록이 비어 있지 않으면 목록의 크기 반환, 비어 있으면 0 반환
        if (donationList != null) {
            return donationList.size();
        }
        return 0;
    }

    public class DonationViewHolder extends RecyclerView.ViewHolder {
        TextView donationName, donationStart, donationEnd;
        ImageView donationImg;

        // 뷰에 대한 참조
        public DonationViewHolder(@NonNull View itemView) {
            super(itemView);

            this.donationName = itemView.findViewById(R.id.donation_name);
            this.donationImg = itemView.findViewById(R.id.donation_img);
            this.donationStart = itemView.findViewById(R.id.donation_start);
            this.donationEnd = itemView.findViewById(R.id.donation_end);
        }
    }

}

