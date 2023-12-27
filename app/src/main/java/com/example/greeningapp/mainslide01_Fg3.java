package com.example.greeningapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class mainslide01_Fg3 extends Fragment {
    //큰 광고 세번째 페이지
    private TextView slide01_main3;

    //사용할 레이아웃 설정
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.slide01_main3, container, false);

        slide01_main3 = rootView.findViewById(R.id.slide01_main3);

        return rootView;
    }
}