package com.example.greeningapp;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

    private Button qlist1;
    private Button qlist2;
    private Button qlist3;
    private Button qlist4;

    private FragmentSuccess fragmentSuccess;

    private FragmentQList fragmentQList;

    private FragmentFailure fragmentFailure;

    private FragmentHome fragmentHome;

    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceUser;
    private DatabaseReference databaseReferenceCurrentUser;
    private FirebaseAuth firebaseAuth;

    private int quizid;

    private int userpoint = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qlist , container,false);

        qlist1 = view.findViewById(R.id.qlist1);
        qlist2 = view.findViewById(R.id.qlist2);
        qlist3 = view.findViewById(R.id.qlist3);
        qlist4 = view.findViewById(R.id.qlist4);

        fragmentFailure = new FragmentFailure();
        fragmentSuccess = new FragmentSuccess();
        fragmentQList = new FragmentQList();
        fragmentHome = new FragmentHome();

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
                qlist1.setText(quiz.getQlist1());
                qlist2.setText(quiz.getQlist2());
                qlist3.setText(quiz.getQlist3());
                qlist4.setText(quiz.getQlist4());

                databaseReferenceUser.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        userpoint = user.getSpoint();
                        Log.d("FragmentQList", "적립 전 데이터베이스 spoint" + userpoint);
                        int resultSpoint = userpoint + 10;

                        qlist1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if(quiz.getQans().equals(qlist1.getText().toString())){
                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragmentFrame1, fragmentSuccess);
                                    transaction.commit();

                                    databaseReferenceUser.child(firebaseUser.getUid()).child("spoint").setValue(userpoint).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d("FragmentQList", "spoint 적립 완료" + userpoint);

                                            databaseReferenceUser.child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                                                    User user = datasnapshot.getValue(User.class);
                                                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                                    final HashMap<String, Object> pointMap = new HashMap<>();
                                                    pointMap.put("pointname", "씨드 적립 - 오늘의 퀴즈");
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

//                            ((QuizActivity) getActivity()).hideFragmentQList();

                                } else {
                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragmentFrame1, fragmentFailure);
                                    transaction.commit();
                                }
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragmentFrame2, fragmentHome);
                                transaction.commit();

                                databaseReferenceUser.child(firebaseUser.getUid()).child("doquiz").setValue("Yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d("FragmentQList", "doquiz 키값 변경 완료" + user.getDoquiz());
                                    }
                                });


                            }
                        });

                        qlist2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(quiz.getQans().equals(qlist2.getText().toString())){
                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragmentFrame1, fragmentSuccess);
                                    transaction.commit();

                                    databaseReferenceUser.child(firebaseUser.getUid()).child("spoint").setValue(userpoint).addOnCompleteListener(new OnCompleteListener<Void>() {
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

//                            ((QuizActivity) getActivity()).hideFragmentQList();

                                } else {
                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragmentFrame1, fragmentFailure);
                                    transaction.commit();
                                }

                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragmentFrame2, fragmentHome);
                                transaction.commit();

                                databaseReferenceUser.child(firebaseUser.getUid()).child("doquiz").setValue("Yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d("FragmentQList", "doquiz 키값 변경 완료" + user.getDoquiz());
                                    }
                                });
                            }
                        });

                        qlist3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(quiz.getQans().equals(qlist3.getText().toString())){
                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragmentFrame1, fragmentSuccess);
                                    transaction.commit();

                                    databaseReferenceUser.child(firebaseUser.getUid()).child("spoint").setValue(userpoint).addOnCompleteListener(new OnCompleteListener<Void>() {
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

//                            ((QuizActivity) getActivity()).hideFragmentQList();

                                } else {
                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragmentFrame1, fragmentFailure);
                                    transaction.commit();
                                }

                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragmentFrame2, fragmentHome);
                                transaction.commit();

                                databaseReferenceUser.child(firebaseUser.getUid()).child("doquiz").setValue("Yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d("FragmentQList", "doquiz 키값 변경 완료" + user.getDoquiz());
                                    }
                                });
                            }
                        });

                        qlist4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(quiz.getQans().equals(qlist4.getText().toString())){
                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragmentFrame1, fragmentSuccess);
                                    transaction.commit();

                                    databaseReferenceUser.child(firebaseUser.getUid()).child("spoint").setValue(resultSpoint).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d("FragmentQList", "spoint 적립 완료" + resultSpoint);

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

//                            ((QuizActivity) getActivity()).hideFragmentQList();

                                } else {
                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragmentFrame1, fragmentFailure);
                                    transaction.commit();
                                }

                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragmentFrame2, fragmentHome);
                                transaction.commit();

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
}