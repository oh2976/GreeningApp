package com.example.greeningapp;

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

        // 레이아웃 요소
        calendarView = findViewById(R.id.calendarView);
        btn_attendcheck = findViewById(R.id.btn_attendcheck);
        btn_home = findViewById(R.id.btn_home);

        // 다이얼로그 설정
        dialog = new Dialog(AttendanceActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);

        // 툴바 설정
        Toolbar toolbar = findViewById(R.id.toolbar_attendance);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 파이어베이스 설정
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference2 = FirebaseDatabase.getInstance().getReference("CurrentUser");
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        // Firebase 데이터베이스에서 사용자 정보를 읽어오는 비동기 이벤트 리스너 등록
        databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userSPoint = user.getSpoint();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 회원 데이터 로드 오류 시 에러 메세지 출력
                Log.e("AttendanceActivity, 회원 데이터 로드 오류", String.valueOf(databaseError.toException()));
            }
        });

        // 홈으로 이동하기 버튼 클릭 이벤트 처리
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendanceActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();  // 현재 로그인한 사용자 정보를 다시 가져옴
        if (firebaseUser != null) {
            idToken = firebaseUser.getUid();  // 사용자 토큰 초기화
            userRef = FirebaseDatabase.getInstance().getReference().child("CurrentUser").child(idToken);  // Firebase 데이터베이스에서 현재 사용자 정보에 대한 참조 객체를 초기화

            // 현재 날짜 정보 가져오기
            Calendar currentDateCalendar = Calendar.getInstance();
            int currentYear = currentDateCalendar.get(Calendar.YEAR);
            int currentMonth = currentDateCalendar.get(Calendar.MONTH);
            int currentDayOfMonth = currentDateCalendar.get(Calendar.DAY_OF_MONTH);

            String currentDate = formatDate(currentYear, currentMonth, currentDayOfMonth);

            // Firebase 데이터베이스에서 현재 사용자의 출석체크 정보를 읽어오는 비동기 이벤트 리스너 등록
            userRef.child("MyAttendance").child(currentDate).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Boolean attendanceCompleted = dataSnapshot.getValue(Boolean.class);  // 데이터 스냅샷에서 출석체크 완료 여부를 가져옴
                    if (attendanceCompleted != null && attendanceCompleted) {
                        showDialog();  // 다이얼로그를 표시
                        btn_attendcheck.setEnabled(false);  // 출석체크 버튼 비활성화
                        btn_attendcheck.setText("출석체크 완료");  // 버튼 텍스트 변경
                        btn_attendcheck.setBackgroundResource(R.drawable.custom_btn_attendance);  // 버튼 배경 변경
                    } else {
                        btn_attendcheck.setEnabled(true);  // 출석체크 버튼 활성화
                        btn_attendcheck.setText("출석체크 하기");  // 버튼 텍스트 변경
                        btn_attendcheck.setBackgroundResource(R.drawable.custom_btn);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // 회원 출석체크 데이터 로드 오류 시 에러 메세지 출력
                    Log.e("AttendanceActivity, 회원의 출석체크 데이터 로드 오류", String.valueOf(databaseError.toException()));
                }
            });
        } else {
            finish();  // 로그아웃되었을 경우 현재 액티비티를 종료
        }

        // 달력 뷰의 날짜 선택 이벤트 처리
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String selectedDate = formatDate(year, month, dayOfMonth);
                String today = formatDate(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

                if (selectedDate.equals(today)) {  // 선택된 날짜가 오늘일 경우
                    // Firebase 데이터베이스에서 선택된 날짜의 출석체크 정보를 읽어오는 비동기 이벤트 리스너 등록
                    userRef.child("MyAttendance").child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Boolean attendanceCompleted = dataSnapshot.getValue(Boolean.class);  // 데이터 스냅샷에서 출석체크 완료 여부를 가져옴
                            if (attendanceCompleted != null && attendanceCompleted) {
                                showDialog();
                                btn_attendcheck.setEnabled(false);
                                btn_attendcheck.setText("출석체크 완료");
                                btn_attendcheck.setBackgroundResource(R.drawable.custom_btn_attendance);
                            } else {
                                // 출석체크 데이터가 없으면 버튼 초기 상태로 초기화
                                btn_attendcheck.setEnabled(true);
                                btn_attendcheck.setText("출석체크 하기");
                                btn_attendcheck.setBackgroundResource(R.drawable.custom_btn);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // 회원 출석체크 데이터 로드 오류 시 에러 메세지 출력
                            Log.e("AttendanceActivity, 회원의 출석체크 데이터 로드 오류", String.valueOf(databaseError.toException()));
                        }
                    });
                } else {
                    // 선택된 날짜가 오늘이 아닐 경우
                    userRef.child("MyAttendance").child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Boolean attendanceCompleted = dataSnapshot.getValue(Boolean.class);
                            if (attendanceCompleted != null && attendanceCompleted) {
                                btn_attendcheck.setText("출석체크 완료");
                                showDialog3();
                            } else {
                                btn_attendcheck.setText("출석체크 미완료");
                            }
                            btn_attendcheck.setEnabled(false);
                            btn_attendcheck.setBackgroundResource(R.drawable.custom_btn_attendance);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // 회원 출석체크 데이터 로드 오류 시 에러 메세지 출력
                            Log.e("AttendanceActivity, 회원의 출석체크 데이터 로드 오류", String.valueOf(databaseError.toException()));
                        }
                    });
                }
            }
        });

        // 출석체크 버튼 클릭 이벤트 처리
        btn_attendcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼 텍스트 변경
                btn_attendcheck.setText("출석체크 완료");

                // 현재 날짜 정보 가져오기
                Calendar currentDateCalendar = Calendar.getInstance();
                int currentYear = currentDateCalendar.get(Calendar.YEAR);
                int currentMonth = currentDateCalendar.get(Calendar.MONTH);
                int currentDayOfMonth = currentDateCalendar.get(Calendar.DAY_OF_MONTH);

                // 선택된 날짜 정보 가져오기
                long selectedDateInMillis = calendarView.getDate();
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.setTimeInMillis(selectedDateInMillis);
                int selectedYear = selectedCalendar.get(Calendar.YEAR);
                int selectedMonth = selectedCalendar.get(Calendar.MONTH);
                int selectedDayOfMonth = selectedCalendar.get(Calendar.DAY_OF_MONTH);

                // 현재 날짜와 선택한 날짜가 같은 경우
                if (currentYear == selectedYear && currentMonth == selectedMonth && currentDayOfMonth == selectedDayOfMonth) {
                    String selectedDate = formatDate(selectedYear, selectedMonth, selectedDayOfMonth);

                    // Firebase 데이터베이스에서 선택한 날짜의 출석체크 정보를 읽어오는 비동기 이벤트 리스너 등록
                    userRef.child("MyAttendance").child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Boolean attendanceCompleted = dataSnapshot.getValue(Boolean.class);
                            // 현재 날짜에 이미 출석체크를 참여하였을 경우
                            if (attendanceCompleted != null && attendanceCompleted) {
                                showDialog();  // 출석체크 이미 완료 다이얼로그 표시
                            } else {// 현재 날짜에 출석체크를 참여하지 않았을 경우 출석체크 처리
                                markAttendanceCompletedForDate(selectedDate);  // 출석체크 완료 처리 메소드 호출
                                btn_attendcheck.setBackgroundResource(R.drawable.custom_btn_attendance);  // 버튼 배경 변경
                                btn_attendcheck.setText("출석체크 완료");  // 버튼 텍스트 변경
                                btn_attendcheck.setEnabled(false);  // 출석체크 버튼 비활성화
                                showDialog2();  // 출석체크 완료 다이얼로그 표시
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // 회원 출석체크 데이터 로드 오류 시 에러 메세지 출력
                            Log.e("AttendanceActivity, 회원의 출석체크 데이터 로드 오류", String.valueOf(databaseError.toException()));
                        }
                    });
                } else {
                }
            }
        });
    }

    // 날짜 포맷 변환 메서드
    private String formatDate(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    // 출석체크 완료 처리 함수
    private void markAttendanceCompletedForDate(String date) {
        // 출석체크 데이터 업데이트
        userRef.child("MyAttendance").child(date).setValue(true);
        showDialog2();

        // 출석체크 씨드 적립 처리
        int changePoint = userSPoint + 5;
        databaseReference.child(firebaseAuth.getUid()).child("spoint").setValue(changePoint).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });

        // 출석체크 씨드 적립 내역 저장
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
                // 씨드 적립 오류 시 에러 메세지 출력
                Log.e("AttendanceActivity, 출석체크 씨드 적립 오류", String.valueOf(error.toException()));
            }
        });
    }

    // 현재 시간 문자열로 반환
    private String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    // 뒤로가기 처리
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
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

    public void showDialog3() {
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