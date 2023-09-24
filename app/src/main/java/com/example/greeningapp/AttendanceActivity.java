package com.example.greeningapp;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView attendanceCompletedTextView;
    private Button btn_attendcheck;
    private Button btn_home;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DatabaseReference userRef; // Firebase Realtime Database 참조
    DatabaseReference databaseReference;
    DatabaseReference databaseReference2;
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private String idToken; // 사용자 ID
    private int userSPoint;
    private String pointName = "출석체크 리워드";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        calendarView = findViewById(R.id.calendarView);
        attendanceCompletedTextView = findViewById(R.id.tv_attendance_completed);
        btn_attendcheck = findViewById(R.id.btn_attendcheck);
        btn_home = findViewById(R.id.btn_home);

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
                Log.e("AttendanceActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        // 홈으로 이동하기 버튼 클릭 시 호출되는 리스너 설정
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MainActivity로 이동하는 Intent 생성 (임시로 ProductListActivity로 이동하도록 설정함)
                Intent intent = new Intent(AttendanceActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // AttendanceActivity는 더 이상 필요하지 않으므로 종료함
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
                        attendanceCompletedTextView.setVisibility(View.VISIBLE);    // 출석체크 완료 텍스트뷰 보이게 설정
                        btn_attendcheck.setEnabled(false);    // 출석체크 버튼 비활성화
                        calendarView.setAlpha(0.3f);    // 캘린더뷰 반투명하게 설정
                    } else {
                        // 출석체크가 완료되지 않은 경우
                        btn_attendcheck.setEnabled(true);    // 출석체크 버튼 활성화
                        calendarView.setAlpha(1.0f);    // 캘린더뷰를 다시 불투명하게 설정
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // 데이터베이스 오류 처리
                }
            });
        } else {
            // 로그인 오류
            Toast.makeText(this, "로그인 정보를 찾을 수 없습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 캘린더뷰에서 날짜를 선택할 때마다 호출되는 리스너 설정
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // 출석체크 버튼을 보이게 설정
                btn_attendcheck.setEnabled(true);
                // 캘린더뷰 보이게, 출석체크 완료 텍스트뷰 안 보이게 설정
                calendarView.setVisibility(View.VISIBLE);
                attendanceCompletedTextView.setVisibility(View.INVISIBLE);

                // 선택된 날짜를 문자열로 변환하여 Firebase에서 해당 날짜의 출석체크 데이터 여부를 확인
                String selectedDate = formatDate(year, month, dayOfMonth);
                userRef.child("MyAttendance").child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Boolean attendanceCompleted = dataSnapshot.getValue(Boolean.class);
                        if (attendanceCompleted != null && attendanceCompleted) {
                            // 출석체크가 이미 완료된 경우
                            btn_attendcheck.setEnabled(false);    // 출석체크 버튼 비활성화
                            calendarView.setAlpha(0.3f);    // 캘린더뷰 반투명하게 설정
                            attendanceCompletedTextView.setVisibility(View.VISIBLE);    // 출석체크 완료 텍스트뷰 보이게 설정
                        } else {
                            // 출석체크가 완료되지 않은 경우
                            btn_attendcheck.setEnabled(true);    // 출석체크 버튼 활성화
                            calendarView.setAlpha(1.0f);    // 캘린더뷰를 다시 불투명하게 설정
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
                                // 출석체크가 이미 완료된 경우 메시지와 뷰를 변경
                                Toast.makeText(AttendanceActivity.this, "출석체크는 당일 날짜에만 가능합니다.", Toast.LENGTH_SHORT).show();    // 요상한 부분..
                            } else {
                                // 출석체크가 완료되지 않은 경우 (출석체크 완료 처리하고 Toast메시지 출력)
                                markAttendanceCompletedForDate(selectedDate);
                                Toast.makeText(AttendanceActivity.this, "출석체크가 완료되었습니다!", Toast.LENGTH_SHORT).show();
                                btn_attendcheck.setEnabled(false);    // 출석체크 버튼 비활성화
                                calendarView.setAlpha(0.3f);    // 캘린더뷰 반투명하게 설정
                                attendanceCompletedTextView.setVisibility(View.VISIBLE);    // 출석체크 완료 텍스트뷰 보이게 설정
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // 데이터베이스 오류 처리
                        }
                    });
                } else {
                    // 선택한 날짜가 현재 날짜와 일치하지 않을 때 메시지 표시
                    Toast.makeText(AttendanceActivity.this, "출석체크는 당일 날짜에만 가능합니다.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AttendanceActivity.this, "출석체크 포인트 지급 완료", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AttendanceActivity.this, "포인트 데이터 저장 완료", Toast.LENGTH_SHORT).show();
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
}