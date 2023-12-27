package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainAdapter01 extends FragmentStateAdapter{

    //광고페이지 개수 설정
    public int mCount;
    public MainAdapter01(FragmentActivity fa, int count) {
        super(fa);
        mCount = count;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        //실제 표시할 페이지 인덱스 설정
        int index = getRealPosition(position);

        //작은 광고 3개
        if(index==0) return new mainslide02_Fg1();
        else if(index==1) return new mainslide02_Fg2();
        else if(index==2) return new mainslide02_Fg3();
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