package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class DonationDetailActivity extends AppCompatActivity {
    long mNow;
    Date mDate;
    Dialog dialog2;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    ImageView dodetailImg, dodetailLongImg;
    TextView dodetailName, dodetailStart, dodetailEnd, dodetailuPoint, dodetatilallPoint;
    Button doDonate;
    BottomSheetDialog dialog;
    Donation donation = null;
    private DatabaseReference databaseReference, databaseReference2, databaseReference3, databaseReference4;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private int spoint = 0;
    private int upoint = 0;
    private int allDoPoint = 0;
    private int donationId = 0;
    private String donationName = "";
    private String userAccountName = "";
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_detail);

        // 툴바
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_donation);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        // 화폐 단위 형식 객체 생성
        DecimalFormat decimalFormat = new DecimalFormat("###,###");

        // 파이어베이스 경로 지정
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Donation");
        databaseReference = FirebaseDatabase.getInstance().getReference("Point");
        databaseReference2 = FirebaseDatabase.getInstance().getReference("User");
        databaseReference3 = FirebaseDatabase.getInstance().getReference("Donation");
        databaseReference4 = FirebaseDatabase.getInstance().getReference("CurrentUser");


        // DonationAdapter.java 에서 보낸 데이터 받기
        final Object object = getIntent().getSerializableExtra("donationDetail");
        // 받은 데이터를 Donation 객체로 형변환
        if(object instanceof Donation){
            donation = (Donation) object;
        }

        // 레이아웃 요소 설정
        dialog2 = new Dialog(DonationDetailActivity.this);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.setContentView(R.layout.dialog_confirm);

        dodetailImg = findViewById(R.id.dodetailed_img);
        dodetailName = findViewById(R.id.dodetailed_name);
        dodetailLongImg = findViewById(R.id.dodetail_longimg);
        dodetailStart = findViewById(R.id.dodetail_start);
        dodetailEnd = findViewById(R.id.dodetail_end);
        dodetatilallPoint = findViewById(R.id.dodetail_point);
        dodetailuPoint = findViewById(R.id.detail_point);
        doDonate = (Button) findViewById(R.id.doDonate);

        // 객체가 null이 아니라면 레이아웃에 데이터 넣기
        if(donation != null){
            Glide.with(getApplicationContext()).load(donation.getDonationimg()).into(dodetailImg);
            dodetailName.setText(donation.getDonationname());
            dodetatilallPoint.setText(String.valueOf(decimalFormat.format(donation.getPoint())) + " 씨드");
            dodetailStart.setText(donation.getDonationstart());
            dodetailEnd.setText(donation.getDonationend());
            Glide.with(getApplicationContext()).load(donation.getDonationdetailimg()).into(dodetailLongImg);
            donationId = donation.getDonationid();
            allDoPoint = donation.getPoint();
            donationName = donation.getDonationname();
        }

        // 회원 테이블 데이터 불러오기
        databaseReference2.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // User 객체에 데이터 담기, 포인트 데이터 넣기
                User user = dataSnapshot.getValue(User.class);
                dodetailuPoint.setText(decimalFormat.format(user.getSpoint()) + " 씨드");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // 다이얼로그 객체 생성
        dialog = new BottomSheetDialog(this);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View view = getLayoutInflater().inflate(R.layout.bottom_dialog, null, false);

        Button wannaDonate = view.findViewById(R.id.wannaDonate);
        EditText wannaDonatepoint = view.findViewById(R.id.wannaDonatepoint);

        // 포인트 idtoken 설정
        String pointID = databaseReference4.push().getKey();

        // EditText에 기부하고 싶은 씨드를 작성하고 기부 버튼을 누를 때 씨드에 관한 모든 데이터베이스 변경
        wannaDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    // EditText에 적은 값이 200보다 크고 10의 배수여야 한다.
                    if( Integer.parseInt(wannaDonatepoint.getText().toString()) >= 200 && Integer.parseInt(wannaDonatepoint.getText().toString()) % 10 == 0 ){

                        //회원 정보에 있는 sPoint와 uPoint 변경을 위해 데이터베이스에서 가져와 변수 처리
                        databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Point point = dataSnapshot.getValue(Point.class); //  만들어 뒀던 Point 객체에 데이터를 담는다.
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                spoint = point.getSpoint();
                                spoint = spoint - Integer.parseInt(wannaDonatepoint.getText().toString());
                                upoint = point.getUpoint();
                                upoint = upoint + Integer.parseInt(wannaDonatepoint.getText().toString());

                                // 프로젝트에 기부된 전체 포인트를 담는 변수
                                allDoPoint = allDoPoint + Integer.parseInt(wannaDonatepoint.getText().toString());

                                // Donation 테이블에 있는 point 값 변경
                                databaseReference3.child(String.valueOf(donationId)).child("point").setValue(allDoPoint).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
//                                    Toast.makeText(DonationDetailActivity.this, "allDoPoint 변동 완료", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                // 회원 정보에 있는 sPoint 변경
                                databaseReference2.child(firebaseUser.getUid()).child("spoint").setValue(spoint).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d("DonationDetailActivity", "spoint 변동 완료" + spoint);
                                    }
                                });

                                // 회원 정보에 있는 uPoint 변경
                                databaseReference2.child(firebaseUser.getUid()).child("upoint").setValue(upoint).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d("DonationDetailActivity", "upoint 변동 완료" + upoint);
                                    }
                                });

                                // 회원 정보에 있는 sPoint, uPoint 정보를 가져오기 위해 만들었던 임시 테이블 삭제
                                databaseReference.child(firebaseUser.getUid())
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d("DonationDetailActivity", "임시 Point 테이블 삭제 완료");
                                            }
                                        });

                                databaseReference2.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // 기부한 정보를 저장하기 위해서 데이터베이스 값 가져오기
                                        User user = dataSnapshot.getValue(User.class); //  만들어 뒀던 user 객체에 데이터를 담는다.
                                        final HashMap<String, Object> pointMap = new HashMap<>();
                                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                        pointMap.put("userName", user.getUsername());
                                        pointMap.put("pointName", "씨드 기부 - " + donationName);
                                        pointMap.put("point", Integer.parseInt(wannaDonatepoint.getText().toString()));
                                        pointMap.put("pointDate", getTime());
                                        pointMap.put("type", "usepoint");
                                        userAccountName = user.getUsername();

                                        // 기부한 정보 저장
                                        databaseReference4.child(firebaseUser.getUid()).child("MyPoint").child(pointID).setValue(pointMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d("DonationDetailActivity",  "point table create");
                                                dialog.show();
                                            }
                                        });

                                        // 기부 완료 페이지를 위해 현재 유저, 프로젝트에 관한 내용을 묶어서 DonationCompleteActivity로 보내기
                                        Intent intent = new Intent(DonationDetailActivity.this, DonationCompleteActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("userName", userAccountName);
                                        bundle.putString("donationName", donationName);
                                        bundle.putInt("donationPoint", Integer.parseInt(wannaDonatepoint.getText().toString()));
                                        bundle.putString("donationDate", getTime());
                                        bundle.putString("donationImg", donation.getDonationimg());

                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        finish();

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

                        // 완료 되면 다이어로그 화면 없애기
                        dialog.dismiss();

                        Log.d("DonationDetailActivity",databaseReference2.child(firebaseUser.getUid()).child("spoint").toString());
                    } else{
                        showCheckPoint();
                    }
                }catch (NumberFormatException exception){
                    showCheckPoint();
                } catch (Exception e){
                    showCheckPoint();
                }



            }
        });

        // 혹시나 기부하지 않고 그냥 다이어로그 화면을 cancel 했을 때 임시로 작성했던 Point 테이블 삭제
        dialog.setContentView(view);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                databaseReference.child(firebaseUser.getUid())
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("DonationDetailActivity", "임시 Point 테이블 삭제 완료");
                            }
                        });
            }
        });

        // 바텀 다이얼로그에 있는 기부하기 버튼 클릭 시
        doDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference2.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // 기부하기 버튼을 누르면 유저 테이블에서 유저 정보를 가져와 테이블에 저장
                        User user = dataSnapshot.getValue(User.class); //  만들어 뒀던 User 객체에 데이터를 담는다.
                        final HashMap<String, Object> donateMap = new HashMap<>();
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        donateMap.put("spoint", user.getSpoint());
                        donateMap.put("upoint", user.getUpoint());
                        donateMap.put("username", user.getUsername());

                        databaseReference.child(firebaseUser.getUid()).setValue(donateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("DonationDetailActivity",  "point table create");
                                dialog.show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        // 하단바 구현
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigation_dodetail);

        // 초기 선택 항목 설정
        bottomNavigationView.setSelectedItemId(R.id.tab_donation);

        // BottomNavigationView의 아이템 클릭 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.tab_home) {
                    // Home 액티비티로 이동
                    startActivity(new Intent(DonationDetailActivity.this, MainActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_shopping) {
                    // Category 액티비티로 이동
                    startActivity(new Intent(DonationDetailActivity.this, CategoryActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_donation) {
                    // Donation 액티비티로 이동
                    startActivity(new Intent(DonationDetailActivity.this, DonationMainActivity.class));
                    finish();
                    return true;
                } else if (item.getItemId() == R.id.tab_mypage) {
                    // My Page 액티비티로 이동
                    startActivity(new Intent(DonationDetailActivity.this, MyPageActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });

    }

    // 현재 시간 가져오기
    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
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

    // 기부 씨드 조건에 만족하지 않으면 뜨는 다이얼로그 생성
    public void showCheckPoint() {
        dialog2.show();

        TextView confirmTextView = dialog2.findViewById(R.id.confirmTextView);
        confirmTextView.setText("작성된 씨드를 다시 한 번 확인해주세요.");

        Button btnOk = dialog2.findViewById(R.id.btn_ok);
        btnOk.setText("확인");

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });
    }


}