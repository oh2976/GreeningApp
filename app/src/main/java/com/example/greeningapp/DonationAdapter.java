package com.example.greeningapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.donation_item, parent, false);
        DonationViewHolder holder = new DonationViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull DonationAdapter.DonationViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(holder.itemView)
                .load(donationList.get(position).getDonationimg())
                .into(holder.donationImg);
        holder.donationName.setText(donationList.get(position).getDonationname());
        holder.donationStart.setText(donationList.get(position).getDonationstart());
        holder.donationEnd.setText(donationList.get(position).getDonationend());

        String startDateString = donationList.get(position).getDonationstart();
        String endDateString = donationList.get(position).getDonationend();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DonationDetailActivity.class);
                intent.putExtra("donationDetail", donationList.get(position));
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });

//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//
////        try {
////            Date startDate = dateFormat.parse(startDateString);
////            Date endDate = dateFormat.parse(endDateString);
////
////            Date currentDate = new Date();
////
////            if (!currentDate.before(startDate) && !currentDate.after(endDate)) {
////                holder.itemView.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        Intent intent = new Intent(context, DonationDetailActivity.class);
////                        intent.putExtra("donationDetail", donationList.get(position));
////                        context.startActivity(intent);
////                    }
////                });
////            } else {
////                // 기부 가능 기간이 아닌 경우 처리 (예: Toast 메시지 출력)
////                Log.d("DonationAdapter", "기부 가능 기간이 아닙니다.");
////            }
////        } catch (ParseException e) {
////            e.printStackTrace();
////        }
//
//        try {
//            Date startDate = dateFormat.parse(startDateString);
//            Date endDate = dateFormat.parse(endDateString);
//
//            Date currentDate = new Date();
//
//            if (!currentDate.before(startDate) && !currentDate.after(endDate)) {
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(context, DonationDetailActivity.class);
//                        intent.putExtra("donationDetail", donationList.get(position));
//                        context.startActivity(intent);
//                    }
//                });
//            } else {
//                // 기부 가능 기간이 아닌 경우 처리 (예: Toast 메시지 출력)
//                Log.d("DonationAdapter", "기부 가능 기간이 아닙니다.");
//                holder.itemView.setAlpha(0.5f);
//                holder.itemView.setClickable(false);
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }






//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(context, DonationDetailActivity.class);
//                intent.putExtra("donationDetail", donationList.get(position));
//                context.startActivity(intent);
//
//
//
//
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        if (donationList != null) {
            return donationList.size();
        }
        return 0;
    }

    public class DonationViewHolder extends RecyclerView.ViewHolder {

        TextView donationName, donationStart, donationEnd;
        ImageView donationImg;
        public DonationViewHolder(@NonNull View itemView) {
            super(itemView);

            this.donationName = itemView.findViewById(R.id.donation_name);
            this.donationImg = itemView.findViewById(R.id.donation_img);
            this.donationStart = itemView.findViewById(R.id.donation_start);
            this.donationEnd = itemView.findViewById(R.id.donation_end);
        }
    }

}

