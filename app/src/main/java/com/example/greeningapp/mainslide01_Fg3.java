package com.example.greeningapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class mainslide01_Fg3 extends Fragment {

    private TextView slide01_main3;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.slide01_main3, container, false);

        slide01_main3 = rootView.findViewById(R.id.slide01_main3); // 레이아웃 파일에서 TextView의 ID를 사용하여 뷰를 찾는다.

        slide01_main3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), CategoryActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }
}