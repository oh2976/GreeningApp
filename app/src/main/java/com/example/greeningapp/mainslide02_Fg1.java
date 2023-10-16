package com.example.greeningapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.greeningapp.AttendanceActivity;
import com.example.greeningapp.ShoppingMainActivity;

public class mainslide02_Fg1 extends Fragment{

    private TextView slide02_main1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.slide02_main1, container, false);

        slide02_main1 = rootView.findViewById(R.id.slide02_main1); //출석체크 광고

        slide02_main1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), AttendanceActivity.class);
                startActivity(intent);
            }
        });


        return rootView;
    }
}