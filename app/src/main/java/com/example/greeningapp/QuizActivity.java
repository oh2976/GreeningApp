package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class QuizActivity extends AppCompatActivity {

    private long timeUntilMidnight; // 자정까지 남은 시간을 저장할 변수

    DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private FragmentStart fragmentStart;
    private FragmentQuestion fragmentQuestion;

    private FragmentQList fragmentQList;

    private FragmentEnd fragmentEnd;
    private Button btnDoQuiz;

    private String quizResult;

    private long quizTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        firebaseAuth =  FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("User");

        databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                User user = dataSnapshot.getValue(User.class); //  만들어 뒀던 Product 객체에 데이터를 담는다.
                quizResult = user.getDoquiz();
//                quizTimestamp = userAccount.getQuiztimestamp();
//                checkQuizStatus();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fragmentStart = new FragmentStart();
        fragmentQuestion = new FragmentQuestion();
        fragmentEnd = new FragmentEnd();
        fragmentQList = new FragmentQList();

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentFrame1, fragmentStart);
        fragmentTransaction.commit();

        btnDoQuiz = (Button) findViewById(R.id.btnDoQuiz);

        btnDoQuiz.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Toast.makeText(QuizActivity.this, "버튼을 눌렀습니다." + quizResult, Toast.LENGTH_SHORT).show();

                if("No".equals(quizResult)){
                    FragmentManager fm2 = getSupportFragmentManager();
                    FragmentTransaction ft2 = fragmentManager.beginTransaction();
                    ft2.replace(R.id.fragmentFrame1, fragmentQuestion);
                    ft2.commit();

                    FragmentManager fm4 = getSupportFragmentManager();
                    FragmentTransaction ft4 = fragmentManager.beginTransaction();
                    ft4.replace(R.id.fragmentFrame2, fragmentQList);
                    ft4.commit();

                    btnDoQuiz.setVisibility(View.INVISIBLE);

                } else if("Yes".equals(quizResult)){
                    FragmentManager fm3 = getSupportFragmentManager();
                    FragmentTransaction ft3 = fragmentManager.beginTransaction();
                    ft3.replace(R.id.fragmentFrame1, fragmentEnd);
                    ft3.commit();
                }

            }
        });

    }

    // FragmentQList를 숨기는 메서드
    public void hideFragmentQList() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(fragmentQList);
        ft.commit();
    }


}
