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
        // 포지션에 따라 다른 프캐그먼트 반환
        if (position==0){
            return new TermsOfUseOneFragment();
        } else {
            return new TermsOfUseTwoFragment();
        }
    }

    @Override
    public int getCount() {
        // 전체 페이지 수 반환
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // 각 페이지 타이틀 반환
        if (position==0) {
            return "서비스 이용약관";
        } else {
            return "개인정보 수집 및 이용약관";
        }
    }
}