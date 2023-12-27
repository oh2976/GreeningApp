package com.example.greeningapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TermsOfUseOneFragment extends Fragment {
    public TermsOfUseOneFragment() {

    }

    public static TermsOfUseOneFragment newInstance(String param1, String param2) {
        TermsOfUseOneFragment fragment = new TermsOfUseOneFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_terms_of_use_one, container, false);
    }
}