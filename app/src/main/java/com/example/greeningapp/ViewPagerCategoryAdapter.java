package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerCategoryAdapter extends FragmentPagerAdapter {

    public ViewPagerCategoryAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position==0){
            return new CategoryZeroFragment();
        } else if (position==1){
            return new CategoryOneFragment();
        } else if (position==2){
            return new CategoryTwoFragment();
        } else { //3
            return new CategoryThreeFragment();
        }
    }

    @Override
    public int getCount() {
        return 4; //no. of tabs
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position==0) {
            return "전체";
        } else if (position==1) {
            return "욕실/주방";
        } else if (position==2) {
            return "취미";
        } else { //3
            return "생활/잡화";
        }
    }
}
