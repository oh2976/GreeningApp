package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerTermsOfUseAdapter extends FragmentPagerAdapter {
    public ViewPagerTermsOfUseAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position==0){
            return new TermsOfUseOneFragment();
        } else {
            return new TermsOfUseTwoFragment();
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position==0) {
            return "서비스 이용약관";
        } else {
            return "개인정보 수집 및 이용약관";
        }
    }
}