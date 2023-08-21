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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.docertificate_item, parent, false);
        CertificateViewHolder holder = new CertificateViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CertificateAdapter.CertificateViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(holder.itemView)
                .load(donationArrayList.get(position).getDonationimg())
                .into(holder.doCertiImg);
        holder.doCertiName.setText(donationArrayList.get(position).getDonationname());
        holder.doCertiStart.setText(donationArrayList.get(position).getDonationstart());
        holder.doCertiEnd.setText(donationArrayList.get(position).getDonationend());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DoCertificateDetailActivity.class);
                intent.putExtra("CertificateDetail", donationArrayList.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (donationArrayList != null) {
            return donationArrayList.size();
        }
        return 0;
    }

    public class CertificateViewHolder extends RecyclerView.ViewHolder {
        TextView doCertiName, doCertiStart, doCertiEnd;
        ImageView doCertiImg;
        public CertificateViewHolder(@NonNull View itemView) {
            super(itemView);

            this.doCertiName = itemView.findViewById(R.id.docerti_name);
            this.doCertiImg = itemView.findViewById(R.id.docerti_img);
            this.doCertiStart = itemView.findViewById(R.id.docerti_start);
            this.doCertiEnd = itemView.findViewById(R.id.docerti_end);
        }
    }
}
