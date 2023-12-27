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

public class CertificateAdapter extends RecyclerView.Adapter<CertificateAdapter.CertificateViewHolder> {
    private Context context;
    private ArrayList<Donation> donationArrayList;

    public CertificateAdapter(ArrayList<Donation> donationArrayList, Context context){
        this.donationArrayList = donationArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CertificateAdapter.CertificateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 뷰에 레이아웃 연결
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.docertificate_item, parent, false);
        // 뷰 홀더 객체 생성 및 반환
        CertificateViewHolder holder = new CertificateViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CertificateAdapter.CertificateViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // 뷰에 데이터 바인딩
        Glide.with(holder.itemView)
                .load(donationArrayList.get(position).getDonationimg())
                .into(holder.doCertiImg);
        holder.doCertiName.setText(donationArrayList.get(position).getDonationname());
        holder.doCertiStart.setText(donationArrayList.get(position).getDonationstart());
        holder.doCertiEnd.setText(donationArrayList.get(position).getDonationend());

        // itemView 클릭 시 해당 데이터를 list 형식으로 처리 후 증명서 상세 내역 페이지로 이동
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DoCertificateDetailActivity.class);
                intent.putExtra("CertificateDetail", donationArrayList.get(position));
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        // 목록이 비어 있지 않으면 목록의 크기 반환, 비어 있으면 0 반환
        if (donationArrayList != null) {
            return donationArrayList.size();
        }
        return 0;
    }

    public class CertificateViewHolder extends RecyclerView.ViewHolder {
        TextView doCertiName, doCertiStart, doCertiEnd;
        ImageView doCertiImg;

        // 뷰에 대한 참조
        public CertificateViewHolder(@NonNull View itemView) {
            super(itemView);
            this.doCertiName = itemView.findViewById(R.id.docerti_name);
            this.doCertiImg = itemView.findViewById(R.id.docerti_img);
            this.doCertiStart = itemView.findViewById(R.id.docerti_start);
            this.doCertiEnd = itemView.findViewById(R.id.docerti_end);
        }
    }
}
