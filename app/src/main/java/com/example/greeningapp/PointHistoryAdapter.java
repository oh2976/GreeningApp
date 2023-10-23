package com.example.greeningapp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.List;

public class PointHistoryAdapter extends RecyclerView.Adapter<PointHistoryAdapter.PointHistoryViewHolder> {
    Context context;
    List<MyPoint> pointHistoryList;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;

    DecimalFormat decimalFormat = new DecimalFormat("###,###");

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
            holder.pointTextView.setText(String.valueOf(decimalFormat.format(myPoint.getPoint())) + "씨드가 적립되었습니다.");
        } else if (myPoint.getType().equals("usepoint")) {
            holder.pointTextView.setText(String.valueOf(decimalFormat.format(myPoint.getPoint())) + "씨드가 기부 완료되었습니다.");
        }

        String pointNameTextView = myPoint.getPointName();
        if (pointNameTextView.length() > 21) {
            pointNameTextView = pointNameTextView.substring(0, 27) + "…";
        }

        holder.pointNameTextView.setText(pointNameTextView);
        holder.pointDateTextView.setText(myPoint.getPointDate());
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
        private ImageView pointListFigure;

        public PointHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            pointNameTextView = itemView.findViewById(R.id.pointNameTextView);
            pointDateTextView = itemView.findViewById(R.id.pointDateTextView);
            pointTextView = itemView.findViewById(R.id.pointTextView);
            pointListFigure = itemView.findViewById(R.id.pointlistfigure);
        }

        public void bind(MyPoint myPoint) {
            pointNameTextView.setText(myPoint.getPointName());
            pointDateTextView.setText(myPoint.getPointDate());
            pointTextView.setText(String.valueOf(myPoint.getPoint()));
        }
    }
}