package com.example.greeningapp;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.annotation.NonNull;

public class MainAdapter extends FragmentStateAdapter{

    //광고페이지 개수 설정
    public int mCount;
    public MainAdapter(FragmentActivity fa, int count) {
        super(fa);
        mCount = count;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        //실제 표시할 페이지 인덱스 설정
        int index = getRealPosition(position);

        //큰광고 3개
        if(index==0) return new mainslide01_Fg1();
        else if(index==1) return new mainslide01_Fg2();
        else if(index==2) return new mainslide01_Fg3();
        else return null;

    }

    //전체 광고페이지 2000개로 설정
    @Override
    public int getItemCount() {
        return 2000;
    }

    //전체페이지를 나눈 값이 실제 페이지
    public int getRealPosition(int position) { return position % mCount; }
}