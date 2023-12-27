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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QuizActivity extends AppCompatActivity {
    DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FragmentStart fragmentStart;
    private FragmentQuestion fragmentQuestion;
    private FragmentQList fragmentQList;
    public Button btnDoQuiz;
    private String quizResult;
    ImageView alreayDoImage;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // 파이어베이스 경로 설정
        firebaseAuth =  FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("User");

        // 툴바
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        // 회원 데이터 가져오기
        databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 파이어베이스 데이터베이스의 데이터를 받아오는 곳
                User user = dataSnapshot.getValue(User.class); //  만들어 뒀던 User 객체에 데이터를 담는다.
                // 회원 테이블에 있는 금일 퀴즈 참여 유무 데이터를 담고 있는 doquiz에 대한 값 담기
                quizResult = user.getDoquiz();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // 프레그먼트 객체 생성
        fragmentStart = new FragmentStart();
        fragmentQuestion = new FragmentQuestion();
        fragmentQList = new FragmentQList();

        // 다이얼로그 객체 생성
        dialog = new Dialog(QuizActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm3);

        alreayDoImage = (ImageView) dialog.findViewById(R.id.image);

        // 퀴즈 액티비티 시작 시 FragmentStart 데이터 가져오기(퀴즈 풀기 공지 화면 띄우기)
        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentFrame2, fragmentStart);
        fragmentTransaction.commit();

        // 퀴즈 풀기 버튼 레이아웃 초기화
        btnDoQuiz = (Button) findViewById(R.id.btnDoQuiz);

        // 퀴즈 풀기 버튼 클릭시
        btnDoQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 금일 퀴즈를 풀지 않았으면
                if("No".equals(quizResult)){
                    // 퀴즈 질문 가져오기
                    FragmentTransaction ft2 = fragmentManager.beginTransaction();
                    ft2.replace(R.id.fragmentFrame1, fragmentQuestion);
                    ft2.commit();

                    // 퀴즈 사지선다 요소 가져오기
                    FragmentTransaction ft4 = fragmentManager.beginTransaction();
                    ft4.replace(R.id.fragmentFrame2, fragmentQList);
                    ft4.commit();
                } else if("Yes".equals(quizResult)){
                    // 금일 퀴즈에 이미 참여했다면 다이얼로그 띄우기
                    showDialog();
                }

            }
        });

    }

    // 금일 퀴즈에 이미 참여했다는 다이얼로그 생성
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
