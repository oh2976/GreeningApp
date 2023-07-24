package com.example.greeningapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
public class ReviewWriteActivity extends AppCompatActivity {

    FirebaseDatabase mDatabase;
    DatabaseReference mRef;
    FirebaseStorage mStorage;

    ImageButton selectedimgBtn;
    EditText reviewet;
    Button submitbtn;
    RatingBar EtRatingBar;

    private static final int Gallery_Code=1;

    Uri imageUrl=null;
    ProgressDialog progressDialog;

    TextView mDate;  //날짜

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        selectedimgBtn = findViewById(R.id.selectedimgBtn);
        reviewet = findViewById(R.id.reviewEt);
        submitbtn = findViewById(R.id.submitBtn);
        EtRatingBar= findViewById(R.id.EtRatingBar);


        mDatabase=FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference().child("Review");
        mStorage=FirebaseStorage.getInstance();
        progressDialog=new ProgressDialog(this);

        //날짜 표시
        mDate = findViewById(R.id.reviewDate);

        String dateTimeFormat = "yyyy.MM.dd";
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateTimeFormat);
        String formattedDate = simpleDateFormat.format(date);
        mDate.setText(formattedDate);




        selectedimgBtn.setOnClickListener(new View.OnClickListener() {
            @Override    // 기기에 설치된 이미지 갤러리 앱 중 하나를 통해 이미지가 선택되는데 보통 구글포토를 사용하나봄ㄷ
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/");
                startActivityForResult(intent,Gallery_Code);
            }
        });


        //취소버튼 클릭 //일단은 리뷰전체보이는 창으로 넘어가게함
        Button button = findViewById(R.id.cancelBtn);    //터치x

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewWriteActivity.this, ReviewActivity.class);         //터치 x
                startActivity(intent);
            }
        });



    } //끝


    //갤러리 사진
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==Gallery_Code && resultCode == RESULT_OK)
        {
            imageUrl =data.getData();
            selectedimgBtn.setImageURI(imageUrl);
        }

//확인버튼 누를시 업로딩이 뜨면서 파이어베이스에 저장됨 //리뷰 작성부분
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fn = reviewet.getText().toString().trim();

                if (!(fn.isEmpty()  && imageUrl != null) )   //이미지와 후기작성이 비어있지않으면 업로드 진행
                {
                    progressDialog.setTitle("Uploading...");   //업로드진행상황을 사용자에게 보여주는데 안써도 상관없음
                    progressDialog.show();
                    StorageReference filepath=mStorage.getReference().child("imagePost").child(imageUrl.getLastPathSegment());
                    filepath.putFile(imageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> downloadUrl=taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String t=task.getResult().toString();

                                    DatabaseReference newPost=mRef.push();

                                    newPost.child("rcontent").setValue(fn);
                                    newPost.child("Review_image").setValue(task.getResult().toString());
                                    // 추가: 레이팅바 평점 저장
                                    float rating = EtRatingBar.getRating();
                                    newPost.child("rimg").setValue(rating);

                                    //날짜 저장
                                    String date = mDate.getText().toString();
                                    newPost.child("rdatetime").setValue(date);





                                    progressDialog.dismiss();
                                }
                            });
                        }
                    });
                }
            }
        });
    }
}