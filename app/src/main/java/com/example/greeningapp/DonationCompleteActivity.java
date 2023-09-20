package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DonationCompleteActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private TextView completeuPoint, completement, completeDoName, completeDoDate, completeDoPoint;

    private String donationName;
    private String donationDate;
    private String userName;
    private int donationPoint;
    private Button goToMain;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_complete);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("User");

        completeuPoint = findViewById(R.id.completeU_point);
        completeDoName = findViewById(R.id.completeDoName);
        completeDoDate = findViewById(R.id.completeDoDate);
        completeDoPoint = findViewById(R.id.completeDoPoint);
        completement = (TextView) findViewById(R.id.completement);

        databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class); //  만들어 뒀던 Product 객체에 데이터를 담는다.
                completeuPoint.setText(user.getSpoint() + " 씨드");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // DonationDetailActivity에서 보낸 회원, 프로젝트 정보 받
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            userName = bundle.getString("userName");
            donationName = bundle.getString("donationName");
            donationPoint = bundle.getInt("donationPoint");
            donationDate = bundle.getString("donationDate");

            Log.d("DonationCompleteActivity", userName + donationName + donationPoint + donationDate);


        }

        completement.setText("총 " + String.valueOf(donationPoint) + " 씨드로");

        completeDoName.setText(donationName);
        completeDoDate.setText(donationDate);
        completeDoPoint.setText(String.valueOf(donationPoint));

        goToMain = (Button) findViewById(R.id.goToMain);
        goToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DonationCompleteActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }
}