package com.example.greeningapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class FragmentQList extends Fragment {

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private FragmentQList fragmentQList;


    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceUser;
    private DatabaseReference databaseReferenceCurrentUser;
    private FirebaseAuth firebaseAuth;

    private int quizid;

    private int userpoint = 0;

    Dialog dialog;

    ImageView successImage, failureImage;

    RadioGroup radioGroup;

    RadioButton qlist1RadioButton, qlist2RadioButton, qlist3RadioButton, qlist4RadioButton;

    String resultUser;

    private Button btnDoQuiz;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qlist , container,false);


        fragmentQList = new FragmentQList();
        QuizActivity quizActivity = (QuizActivity) getActivity();

        if (quizActivity != null && quizActivity.btnDoQuiz != null) {
            btnDoQuiz = quizActivity.btnDoQuiz;

        }


        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm3);

        successImage = dialog.findViewById(R.id.image);

        failureImage = dialog.findViewById(R.id.image);

        radioGroup = (RadioGroup) view.findViewById(R.id.qlistRadioGroup);
        qlist1RadioButton = view.findViewById(R.id.qlist1);
        qlist2RadioButton = view.findViewById(R.id.qlist2);
        qlist3RadioButton = view.findViewById(R.id.qlist3);
        qlist4RadioButton = view.findViewById(R.id.qlist4);


        // 초기 상태에서 버튼 비활성화
        btnDoQuiz.setEnabled(false);
        btnDoQuiz.setBackgroundColor(getResources().getColor(R.color.textColorGray));



        databaseReference = FirebaseDatabase.getInstance().getReference("Quiz");
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference("User");
        databaseReferenceCurrentUser = FirebaseDatabase.getInstance().getReference("CurrentUser");

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        quizid = 610001;
        databaseReference.child(String.valueOf(quizid)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Quiz quiz = snapshot.getValue(Quiz.class);
                qlist1RadioButton.setText(quiz.getQlist1());
                qlist2RadioButton.setText(quiz.getQlist2());
                qlist3RadioButton.setText(quiz.getQlist3());
                qlist4RadioButton.setText(quiz.getQlist4());

                databaseReferenceUser.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        userpoint = user.getSpoint();
                        Log.d("FragmentQList", "적립 전 데이터베이스 spoint" + userpoint);
                        int resultSpoint = userpoint + 10;

                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                if(checkedId == R.id.qlist1){
                                    resultUser = String.valueOf(qlist1RadioButton.getText());
                                    btnDoQuiz.setEnabled(true);
                                    btnDoQuiz.setBackgroundColor(getResources().getColor(R.color.mainColor));
                                } else if(checkedId == R.id.qlist2){
                                    resultUser = String.valueOf(qlist2RadioButton.getText());
                                    btnDoQuiz.setEnabled(true);
                                    btnDoQuiz.setBackgroundColor(getResources().getColor(R.color.mainColor));
                                } else if(checkedId == R.id.qlist3){
                                    resultUser = String.valueOf(qlist3RadioButton.getText());
                                    btnDoQuiz.setEnabled(true);
                                    btnDoQuiz.setBackgroundColor(getResources().getColor(R.color.mainColor));
                                } else if(checkedId == R.id.qlist4){
                                    resultUser = String.valueOf(qlist4RadioButton.getText());
                                    btnDoQuiz.setEnabled(true);
                                    btnDoQuiz.setBackgroundColor(getResources().getColor(R.color.mainColor));
                                }

                            }
                        });

                        btnDoQuiz.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(quiz.getQans().equals(resultUser)){

                                    showSuccessDialog();

                                    databaseReferenceUser.child(firebaseUser.getUid()).child("spoint").setValue(resultSpoint).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d("FragmentQList", "spoint 적립 완료" + userpoint);

                                            databaseReferenceUser.child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                                                    User user = datasnapshot.getValue(User.class);
                                                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                                    final HashMap<String, Object> pointMap = new HashMap<>();
                                                    pointMap.put("pointName", "씨드 적립 - 오늘의 퀴즈");
                                                    pointMap.put("pointDate", getTime());
                                                    pointMap.put("type", "savepoint");
                                                    pointMap.put("point", 10);
                                                    pointMap.put("userName", user.getUsername());

                                                    String pointID = databaseReferenceCurrentUser.child(firebaseUser.getUid()).child("MyPoint").push().getKey();
                                                    databaseReferenceCurrentUser.child(firebaseUser.getUid()).child("MyPoint").child(pointID).setValue(pointMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    });

                                } else {
                                    showFailureDialog();
                                }

                                // 테스트를 위해서 잠시 주석 처리 (퀴즈 완료 처리하는 코드)
                                databaseReferenceUser.child(firebaseUser.getUid()).child("doquiz").setValue("Yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d("FragmentQList", "doquiz 키값 변경 완료" + user.getDoquiz());
                                    }
                                });

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    public void showSuccessDialog() {
        dialog.show();

        TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
        confirmTextView.setText("정답입니다! \n 10씨드가 적립되었습니다.");
        successImage.setImageResource(R.drawable.quiz_success_size);

        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnOk.setText("홈으로 돌아가기");


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    public void showFailureDialog() {
        dialog.show();

        TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
        confirmTextView.setText("오답입니다! \n 내일 또 도전해주세요.");
        failureImage.setImageResource(R.drawable.quiz_failure_size);

        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnOk.setText("홈으로 돌아가기");


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }


}