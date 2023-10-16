package com.example.greeningapp;

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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SearchAdapter extends FirebaseRecyclerAdapter<Product, SearchAdapter.myviewholder> {
    private ArrayList<Product> arrayList;
    private Context context;

    DecimalFormat decimalFormat = new DecimalFormat("###,###");
    public SearchAdapter(@NonNull FirebaseRecyclerOptions<Product> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull Product product) {
        holder.pname.setText(getItem(position).getPname());
        holder.psay.setText(getItem(position).getPsay());
        holder.pprice.setText(String.valueOf(decimalFormat.format(getItem(position).getPprice())) + "원");
        Glide.with(holder.pimg.getContext()).load(getItem(position).getPimg()).into(holder.pimg);
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        return new myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder {
        ImageView pimg;
        TextView pname, psay, pprice;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            pimg = itemView.findViewById(R.id.searchpimg);
            pname = itemView.findViewById(R.id.searchpname);
            psay = itemView.findViewById(R.id.searchpsay);
            pprice = itemView.findViewById(R.id.searchpprice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Product product = getItem(position);
                        if (product != null) {
                            Intent intent = new Intent(itemView.getContext(), ProductDetailActivity.class);
                            intent.putExtra("detail", product);
                            itemView.getContext().startActivity(intent);
                        }
                    }
                }
            });
        }
    }
}