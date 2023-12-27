package com.example.greeningapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ManageUserAdapter extends RecyclerView.Adapter<ManageUserAdapter.ManageUserViewHolder> {
    private ArrayList<User> arrayList;
    private Context context;

    public ManageUserAdapter(ArrayList<User> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ManageUserAdapter.ManageUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 뷰에 레이아웃 연결
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manage_user_item, parent, false);
        // 뷰 홀더 객체 생성 및 반환
        ManageUserAdapter.ManageUserViewHolder holder = new ManageUserAdapter.ManageUserViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ManageUserAdapter.ManageUserViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // 뷰에 데이터 바인딩
        holder.MGuserName_user.setText("이름 : " + arrayList.get(position).getUsername());
        holder.MGUserID_user.setText("이메일 : " + arrayList.get(position).getEmailId());
        holder.MGUserDate_user.setText("가입 날짜 : " + arrayList.get(position).getRegdate());

        // itemView 클릭 시 해당 데이터를 list 형식으로 처리 후 회원 상세 내역 페이지로 이동
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ManageUserDetailActivity.class);
                intent.putExtra("ManageUserDetail", arrayList.get(position));
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

    public class ManageUserViewHolder extends RecyclerView.ViewHolder {
        TextView MGuserName_user, MGUserID_user, MGUserDate_user;

        // 뷰에 대한 참조
        public ManageUserViewHolder(@NonNull View itemView) {
            super(itemView);
            this.MGuserName_user = itemView.findViewById(R.id.MGUserName_user);
            this.MGUserID_user = itemView.findViewById(R.id.MGUserID_user);
            this.MGUserDate_user = itemView.findViewById(R.id.MGUserDate_user);
        }
    }
}
