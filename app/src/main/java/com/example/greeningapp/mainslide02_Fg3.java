package com.example.greeningapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.greeningapp.DonationMainActivity;
import com.example.greeningapp.ShoppingMainActivity;

public class mainslide02_Fg3 extends Fragment{

    private TextView slide02_main3;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.slide02_main3, container, false);

        slide02_main3 = rootView.findViewById(R.id.slide02_main3); // 기부 광고

        slide02_main3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), DonationMainActivity.class);
                startActivity(intent);
            }
        });


        return rootView;
    }
}