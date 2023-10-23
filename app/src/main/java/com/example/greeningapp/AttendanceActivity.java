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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference2 = FirebaseDatabase.getInstance().getReference("CurrentUser");
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userSPoint = user.getSpoint();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("AttendanceActivity, 회원 데이터 접속 오류", String.valueOf(databaseError.toException()));
            }
        });

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AttendanceActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            idToken = firebaseUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference().child("CurrentUser").child(idToken);

            Calendar currentDateCalendar = Calendar.getInstance();
            int currentYear = currentDateCalendar.get(Calendar.YEAR);
            int currentMonth = currentDateCalendar.get(Calendar.MONTH);
            int currentDayOfMonth = currentDateCalendar.get(Calendar.DAY_OF_MONTH);

            String currentDate = formatDate(currentYear, currentMonth, currentDayOfMonth);
            userRef.child("MyAttendance").child(currentDate).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Boolean attendanceCompleted = dataSnapshot.getValue(Boolean.class);
                    if (attendanceCompleted != null && attendanceCompleted) {
                        showDialog();
                    } else {
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("AttendanceActivity, 회원 출석체크 데이터 로드 오류", String.valueOf(databaseError.toException()));
                }
            });
        } else {
            finish();
        }

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                btn_attendcheck.setEnabled(true);

                String selectedDate = formatDate(year, month, dayOfMonth);
                userRef.child("MyAttendance").child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Boolean attendanceCompleted = dataSnapshot.getValue(Boolean.class);
                        if (attendanceCompleted != null && attendanceCompleted) {
                            if (isToday(selectedDate)) {
                                showDialog();    // "오늘은 이미 출석체크에 참여하셨습니다."
                            } else {
                                showDialog4();    // "해당 날짜에 출석체크를 참여하셨습니다 :)"
                            }
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("AttendanceActivity, 회원 데이터 로드 오류", String.valueOf(databaseError.toException()));
                    }
                });
            }
        });

        btn_attendcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentDateCalendar = Calendar.getInstance();
                int currentYear = currentDateCalendar.get(Calendar.YEAR);
                int currentMonth = currentDateCalendar.get(Calendar.MONTH);
                int currentDayOfMonth = currentDateCalendar.get(Calendar.DAY_OF_MONTH);

                long selectedDateInMillis = calendarView.getDate();
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.setTimeInMillis(selectedDateInMillis);
                int selectedYear = selectedCalendar.get(Calendar.YEAR);
                int selectedMonth = selectedCalendar.get(Calendar.MONTH);
                int selectedDayOfMonth = selectedCalendar.get(Calendar.DAY_OF_MONTH);

                if (currentYear == selectedYear && currentMonth == selectedMonth && currentDayOfMonth == selectedDayOfMonth) {
                    String selectedDate = formatDate(selectedYear, selectedMonth, selectedDayOfMonth);

                    userRef.child("MyAttendance").child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Boolean attendanceCompleted = dataSnapshot.getValue(Boolean.class);
                            if (attendanceCompleted != null && attendanceCompleted) {
                                showDialog();
                            } else {
                                markAttendanceCompletedForDate(selectedDate);
                                btn_attendcheck.setEnabled(false);
                                showDialog3();    // "출석체크가 완료되었습니다 :)"
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("AttendanceActivity, 출석체크 오류", String.valueOf(databaseError.toException()));
                        }
                    });
                } if (currentYear != selectedYear || currentMonth != selectedMonth || currentDayOfMonth != selectedDayOfMonth) {
                    showDialog2();
                }
            }
        });
    }

    private String formatDate(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    private void markAttendanceCompletedForDate(String date) {
        userRef.child("MyAttendance").child(date).setValue(true);

        int changePoint = userSPoint + 5;
        databaseReference.child(firebaseAuth.getUid()).child("spoint").setValue(changePoint).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });

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
                Log.e("AttendanceActivity, 출석체크 적립 오류", String.valueOf(error.toException()));
            }
        });
    }

    // 현재 날짜인지 확인하는 함수
    private boolean isToday(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        String today = dateFormat.format(currentDate);
        return today.equals(date);
    }

    private String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

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