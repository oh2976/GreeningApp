package com.example.greeningapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FragmentQuestion extends Fragment {
    private TextView rtitle;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private String question;
    private int quizid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question , container,false);

        rtitle = view.findViewById(R.id.rtitle);

        firebaseAuth =  FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("Quiz");

        quizid = 610001;

        databaseReference.child(String.valueOf(quizid)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                Quiz quiz = dataSnapshot.getValue(Quiz.class); //  만들어 뒀던 Product 객체에 데이터를 담는다.
                question = quiz.getQqst();
                rtitle.setText(question);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;


    }
}
