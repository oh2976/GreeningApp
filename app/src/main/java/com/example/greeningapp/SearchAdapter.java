package com.example.greeningapp;
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

public class SearchAdapter extends FirebaseRecyclerAdapter<Product,SearchAdapter.myviewholder> {

    public SearchAdapter(@NonNull FirebaseRecyclerOptions<Product> options) {

        super(options);
    }

    //    @Override
//    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull Product product) {
//        holder.pname.setText(product.getPname());
//        holder.psay.setText(product.getPsay());
//        holder.pprice.setText(String.valueOf(product.getPprice())); // int 값을 String으로 변환하여 setText
//        Glide.with(holder.pimg.getContext()).load(product.getPimg()).into(holder.pimg);
//    }
    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull Product product) {
        holder.pname.setText(getItem(position).getPname());
        holder.psay.setText(getItem(position).getPsay());
        holder.psay.setText(product.getPsay());
        holder.pprice.setText(String.valueOf(getItem(position).getPprice()));
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
            pimg = (ImageView) itemView.findViewById(R.id.searchpimg);
            pname = (TextView) itemView.findViewById(R.id.searchpname);
            psay = (TextView) itemView.findViewById(R.id.searchpsay);
            pprice = (TextView) itemView.findViewById(R.id.searchpprice);

        }

    }
}