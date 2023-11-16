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

import java.text.DecimalFormat;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.myviewholder> {
    private List<Product> searchResults;
    Context context;
    DecimalFormat decimalFormat = new DecimalFormat("###,###");
    public SearchAdapter(List<Product> searchResults) {
        this.searchResults = searchResults;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        myviewholder holder = new myviewholder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(holder.itemView)
                .load(searchResults.get(position).getPimg())
                .into(holder.pimg);
        holder.pname.setText(searchResults.get(position).getPname());
        holder.psay.setText(searchResults.get(position).getPsay());
        holder.pprice.setText(String.valueOf(decimalFormat.format(searchResults.get(position).getPprice())) + "원");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
                intent.putExtra("detail", searchResults.get(position));
                v.getContext().startActivity(intent);
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
            pimg = itemView.findViewById(R.id.searchpimg);
            pname = itemView.findViewById(R.id.searchpname);
            psay = itemView.findViewById(R.id.searchpsay);
            pprice = itemView.findViewById(R.id.searchpprice);

        }
    }
}