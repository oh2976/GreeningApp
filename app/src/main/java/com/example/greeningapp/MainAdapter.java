package com.example.greeningapp;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.annotation.NonNull;

public class MainAdapter extends FragmentStateAdapter{
    public int mCount;
    public MainAdapter(FragmentActivity fa, int count) {
        super(fa);
        mCount = count;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        int index = getRealPosition(position);

        if(index==0) return new mainslide01_Fg1();
        else if(index==1) return new mainslide01_Fg2();
        else if(index==2) return new mainslide01_Fg3();
        else return null;
//        else return new mainslide01_Fg4();
    }

    @Override
    public int getItemCount() {
        return 2000;
    }

    public int getRealPosition(int position) { return position % mCount; }
}