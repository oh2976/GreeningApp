package com.example.greeningapp;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AttendanceActivity extends AppCompatActivity {
    private CalendarView calendarView;
    Dialog dialog;
    private Button btn_attendcheck;
    private Button btn_home;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference userRef;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private String idToken;
    private int userSPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        calendarView = findViewById(R.id.calendarView);
        btn_attendcheck = findViewById(R.id.btn_attendcheck);
        btn_home = findViewById(R.id.btn_home);

        dialog = new Dialog(AttendanceActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);

        Toolbar toolbar = findViewById(R.id.toolbar_attendance);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference2 = FirebaseDatabase.getInstance().getReference("CurrentUser");
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 결제 시 회원 테이블에 있는 sPoint 변경을 위해서 기존 sPoint를 변수에 저장
                User user = dataSnapshot.getValue(User.class); //  만들어 뒀던 Product 객체에 데이터를 담는다.
                userSPoint = user.getSpoint();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 디비를 가져오던 중 에러 발생 시
                Log.e("AttendanceActivity", String.valueOf(databaseError.toException()));
            }
        });

        // 홈으로 이동하기 버튼 클릭 시 호출되는 리스너 설정
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendanceActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 파이어베이스에서 현재 로그인된 사용자의 데이터 참조
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            idToken = firebaseUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference().child("CurrentUser").child(idToken);

            // 현재 날짜 가져오기
            Calendar currentDateCalendar = Calendar.getInstance();
            int currentYear = currentDateCalendar.get(Calendar.YEAR);
            int currentMonth = currentDateCalendar.get(Calendar.MONTH);
            int currentDayOfMonth = currentDateCalendar.get(Calendar.DAY_OF_MONTH);

            // 현재 날짜를 문자열로 변환하여 Firebase에서 해당 날짜의 출석체크 데이터 여부를 확인
            String currentDate = formatDate(currentYear, currentMonth, currentDayOfMonth);
            userRef.child("MyAttendance").child(currentDate).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Boolean attendanceCompleted = dataSnapshot.getValue(Boolean.class);
                    if (attendanceCompleted != null && attendanceCompleted) {
                        // 출석체크가 이미 완료된 경우
                        showDialog();
                    } else {
                        // 출석체크가 완료되지 않은 경우
                        btn_attendcheck.setEnabled(true);    // 출석체크 버튼 활성화
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // 데이터베이스 오류 처리
                }
            });
        } else {
            finish();
        }

        // 캘린더뷰에서 날짜를 선택할 때마다 호출되는 리스너 설정
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // 출석체크 버튼을 보이게 설정
                btn_attendcheck.setEnabled(true);

                // 선택된 날짜를 문자열로 변환하여 Firebase에서 해당 날짜의 출석체크 데이터 여부를 확인
                String selectedDate = formatDate(year, month, dayOfMonth);
                userRef.child("MyAttendance").child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Boolean attendanceCompleted = dataSnapshot.getValue(Boolean.class);
                        if (attendanceCompleted != null && attendanceCompleted) {
                            // 출석체크가 이미 완료된 경우
                            showDialog4();
                        } else {
                            // 출석체크가 완료되지 않은 경우
                            btn_attendcheck.setEnabled(true);    // 출석체크 버튼 활성화
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // 데이터베이스 오류 처리
                    }
                });
            }
        });

        // 출석체크 버튼 클릭 시 호출되는 리스너 설정
        btn_attendcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 날짜 가져오기
                Calendar currentDateCalendar = Calendar.getInstance();
                int currentYear = currentDateCalendar.get(Calendar.YEAR);
                int currentMonth = currentDateCalendar.get(Calendar.MONTH);
                int currentDayOfMonth = currentDateCalendar.get(Calendar.DAY_OF_MONTH);

                // 캘린더뷰에서 선택한 날짜 가져오기
                long selectedDateInMillis = calendarView.getDate();
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.setTimeInMillis(selectedDateInMillis);
                int selectedYear = selectedCalendar.get(Calendar.YEAR);
                int selectedMonth = selectedCalendar.get(Calendar.MONTH);
                int selectedDayOfMonth = selectedCalendar.get(Calendar.DAY_OF_MONTH);

                if (currentYear == selectedYear && currentMonth == selectedMonth && currentDayOfMonth == selectedDayOfMonth) {
                    // 선택한 날짜가 현재 날짜와 일치하면 출석체크 가능
                    String selectedDate = formatDate(selectedYear, selectedMonth, selectedDayOfMonth);

                    userRef.child("MyAttendance").child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Boolean attendanceCompleted = dataSnapshot.getValue(Boolean.class);
                            if (attendanceCompleted != null && attendanceCompleted) {
                                showDialog2();
                            } else {
                                // 출석체크가 완료되지 않은 경우
                                markAttendanceCompletedForDate(selectedDate);
                                btn_attendcheck.setEnabled(false);
                                showDialog3();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // 데이터베이스 오류 처리
                        }
                    });
                } else {
                    showDialog2();
                }
            }
        });
    }

    // 날짜를 "yyyy-MM-dd" 형식의 문자열로 변환하는 메서드
    private String formatDate(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    // 선택된 날짜의 출석체크 완료를 Firebase에 저장하는 메서드
    private void markAttendanceCompletedForDate(String date) {
        userRef.child("MyAttendance").child(date).setValue(true);

        int changePoint = userSPoint + 5;
        databaseReference.child(firebaseAuth.getUid()).child("spoint").setValue(changePoint).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });

        // 포인트 지급 내역 저장
        databaseReference.child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                User user = datasnapshot.getValue(User.class);
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                final HashMap<String, Object> pointMap = new HashMap<>();
                pointMap.put("pointName", "씨드 적립 - 출석체크");
                pointMap.put("pointDate", getTime());
                pointMap.put("type", "savepoint");
                pointMap.put("point", 5);
                pointMap.put("userName", user.getUsername());

                String pointID = databaseReference2.child(firebaseUser.getUid()).child("MyPoint").push().getKey();
                databaseReference2.child(firebaseUser.getUid()).child("MyPoint").child(pointID).setValue(pointMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId ()) {
            case android.R.id.home:
                Intent intent = new Intent(AttendanceActivity.this, MyPageActivity.class);
                startActivity(intent);
                finish ();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showDialog() {
        dialog.show();

        TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
        confirmTextView.setText("오늘은 이미 출석체크에 참여하셨습니다 :)");

        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnOk.setText("확인");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void showDialog2() {
        dialog.show();

        TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
        confirmTextView.setText("출석체크는 당일에만 참여 가능합니다.");

        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnOk.setText("확인");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void showDialog3() {
        dialog.show();

        TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
        confirmTextView.setText("출석체크가 완료되었습니다 :)");

        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnOk.setText("확인");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void showDialog4() {
        dialog.show();

        TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
        confirmTextView.setText("해당 날짜에 출석체크를 참여하셨습니다 :)");

        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnOk.setText("확인");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}