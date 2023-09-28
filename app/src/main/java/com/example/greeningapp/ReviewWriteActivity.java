package com.example.greeningapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ReviewWriteActivity extends AppCompatActivity {

    private static final int Gallery_Code=1;

    FirebaseDatabase mDatabase;
    DatabaseReference dtf; //잠시추가 0924
    DatabaseReference mRef;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    FirebaseStorage mStorage;

    ImageView uploadImage;
    Button uploadBtn;
    RatingBar RatingBarEt;
    Uri imageUri=null;
    Button cancelBtn;
    EditText reviewEt;

    MyOrder product = null;

    TextView Pname;
    ImageView Pimg;

    private String orderId,myOrderId, eachOrderedId;

    TextView mDate;  //날짜

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("CurrentUser").child(firebaseUser.getUid()).child("MyOrder");


        uploadBtn = findViewById(R.id.writeUploadBtn);
        uploadImage = findViewById(R.id.writeUploadImage);
        reviewEt = findViewById(R.id.writeReviewEt);
        RatingBarEt = findViewById(R.id.writeRatingBar);

        mDatabase=FirebaseDatabase.getInstance();
        mRef=mDatabase.getReference().child("Review");
        mStorage=FirebaseStorage.getInstance();
        //날짜 표시
        mDate = findViewById(R.id.reviewDate);

        String dateTimeFormat = "yyyy-MM-dd";
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateTimeFormat);
        String formattedDate = simpleDateFormat.format(date);
        mDate.setText(formattedDate);

        Pname = findViewById(R.id.writePname);
        Pimg = (ImageView) findViewById(R.id.writePImg);

        final Object object = getIntent().getSerializableExtra("product");

        if(object instanceof MyOrder){
            product = (MyOrder) object;
            Log.d("Review_write", product+"");
        }

        if (product != null) {
            Pname.setText(product.getProductName());
            Glide.with(getApplicationContext()).load(product.getOrderImg()).into(Pimg);

            orderId = product.getOrderId();
            eachOrderedId = product.getEachOrderedId();
//            Log.d("Review", "orderId = " + orderId);
            Log.d("Review",product.getProductName());
        }


        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/");
                startActivityForResult(intent,Gallery_Code);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==Gallery_Code && resultCode == RESULT_OK)
        {
            imageUri =data.getData();
            uploadImage.setImageURI(imageUri);
        }

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fn = reviewEt.getText().toString().trim();
                String reviewImage = imageUri.toString();

                if (!(fn.isEmpty() && imageUri != null))   //이미지와 후기작성이 비어있지않으면 업로드 진행
                {
                    float rating = RatingBarEt.getRating();
                    String reviewDate = mDate.getText().toString();

                    // Create a HashMap to store the review data
                    HashMap<String, Object> reviewwriteMap = new HashMap<>();
                    reviewwriteMap.put("pid", product.getProductId());
                    reviewwriteMap.put("pname", product.getProductName());
                    reviewwriteMap.put("pimg", product.getOrderImg());
                    reviewwriteMap.put("username", product.getUserName());

                    reviewwriteMap.put("rimage", reviewImage);
                    reviewwriteMap.put("rcontent", fn);
                    reviewwriteMap.put("rscore", rating);
                    reviewwriteMap.put("rdatetime", reviewDate);


                    int pidInt = product.getProductId(); // 정수 값을 가져온후
                    String pid = String.valueOf(pidInt);  //문자열로 변환하여 저장

                    mRef.push().setValue(reviewwriteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                databaseReference.child(product.getOrderId()).child(product.getEachOrderedId()).child("doReview").setValue("Yes").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Myreview", "myOrderId: " + myOrderId);
                                        Log.d("eachorderid", "eachOrderedId: " + eachOrderedId);
                                    }
                                });

                            } else {
                                // Firebase 데이터 쓰기가 실패한 경우
                                Log.e("Firebase", "Data write failed: " + task.getException().getMessage());
                            }
                        }
                    });


                    AlertDialog.Builder builder = new AlertDialog.Builder(ReviewWriteActivity.this);

                    builder.setTitle("작성 완료").setMessage("감사합니다!");


                    builder.setPositiveButton("홈 이동", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(ReviewWriteActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });

                    builder.setNegativeButton("리뷰 확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
//                            Intent intent = new Intent(Review_write.this, ReviewHistoryActivity.class);
//                            startActivity(intent);
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }else {
                }
            }

        });
    }
}