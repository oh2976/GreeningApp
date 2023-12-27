package com.example.greeningapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TermsOfUseTwoFragment extends Fragment {
    public TermsOfUseTwoFragment() {

    }

    public static TermsOfUseTwoFragment newInstance(String param1, String param2) {
        TermsOfUseTwoFragment fragment = new TermsOfUseTwoFragment();
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
        return inflater.inflate(R.layout.fragment_terms_of_use_two, container, false);
    }
}