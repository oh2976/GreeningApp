package com.example.greeningapp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.content.Context;

import java.util.List;

public class PointHistoryAdapter extends RecyclerView.Adapter<PointHistoryAdapter.PointHistoryViewHolder> {

    Context context;
    List<MyPoint> pointHistoryList;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    public PointHistoryAdapter(Context context, List<MyPoint> pointHistoryList) {
        this.context = context;
        this.pointHistoryList = pointHistoryList;
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public PointHistoryAdapter.PointHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_point_list, parent, false);
        PointHistoryViewHolder holder = new PointHistoryViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PointHistoryAdapter.PointHistoryViewHolder holder, @SuppressLint("recyclerview_pointHistory") int position) {
        MyPoint myPoint = pointHistoryList.get(position);

        if (myPoint.getType().equals("savepoint")) {
            holder.pointIcon.setImageResource(R.drawable.savep); // "savepoint"에 해당하는 이미지
        } else if (myPoint.getType().equals("usepoint")) {
            holder.pointIcon.setImageResource(R.drawable.usep); // "usepoint"에 해당하는 이미지
        }

        holder.pointNameTextView.setText(myPoint.getPointName());
        holder.pointDateTextView.setText(myPoint.getPointDate());
        holder.pointTextView.setText(String.valueOf(myPoint.getPoint()) + "씨드");
    }

    @Override
    public int getItemCount() {
        if (pointHistoryList != null) {
            return pointHistoryList.size();
        }
        return 0;
    }

    public class PointHistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView pointNameTextView;
        private TextView pointDateTextView;
        private TextView pointTextView;
        private ImageView pointIcon;

        public PointHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            pointNameTextView = itemView.findViewById(R.id.pointNameTextView);
            pointDateTextView = itemView.findViewById(R.id.pointDateTextView);
            pointTextView = itemView.findViewById(R.id.pointTextView);
            pointIcon = itemView.findViewById(R.id.pointIcon);
        }

        public void bind(MyPoint myPoint) {
            pointNameTextView.setText(myPoint.getPointName());
            pointDateTextView.setText(myPoint.getPointDate());
            pointTextView.setText(String.valueOf(myPoint.getPoint()));
        }
    }
}