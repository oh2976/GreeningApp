package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
    public Button btnDoQuiz;

    private String quizResult;

    private long quizTimestamp;

    ImageView alreayDoImage;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        firebaseAuth =  FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

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

        dialog = new Dialog(QuizActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm3);

        alreayDoImage = (ImageView) dialog.findViewById(R.id.image);



        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentFrame2, fragmentStart);
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

//                    btnDoQuiz.setVisibility(View.INVISIBLE);

                } else if("Yes".equals(quizResult)){
//                    FragmentManager fm3 = getSupportFragmentManager();
//                    FragmentTransaction ft3 = fragmentManager.beginTransaction();
//                    ft3.replace(R.id.fragmentFrame1, fragmentEnd);
//                    ft3.commit();
                    showDialog();
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

    public void showDialog() {
        dialog.show();

        TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
        confirmTextView.setText("오늘은 이미 퀴즈에 참여하였습니다. \n 내일 또 도전해주세요.");
        alreayDoImage.setImageResource(R.drawable.quiz_alreay_do_size);

        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnOk.setText("홈으로 돌아가기");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(QuizActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) { //뒤로가기
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


}
