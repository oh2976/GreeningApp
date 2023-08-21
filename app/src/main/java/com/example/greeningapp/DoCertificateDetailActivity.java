package com.example.greeningapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DoCertificateDetailActivity extends AppCompatActivity {

    private ImageView CERTdetailed_img, CERTdetail_longimg;
    private TextView CERTdetailed_name, CERTdetail_start, CERTdetail_end;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    Donation donation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_certificate_detail);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Donation");

        final Object object = getIntent().getSerializableExtra("CertificateDetail");
        if(object instanceof Donation){
            donation = (Donation) object;
        }

        CERTdetailed_img = (ImageView) findViewById(R.id.CERTdetailed_img);
        CERTdetail_longimg = (ImageView) findViewById(R.id.CERTdetail_longimg);
        CERTdetailed_name = (TextView) findViewById(R.id.CERTdetailed_name);
        CERTdetail_start = (TextView) findViewById(R.id.CERTdetail_start);
        CERTdetail_end = (TextView) findViewById(R.id.CERTdetail_end);

        if(donation != null) {
            Glide.with(getApplicationContext()).load(donation.getDonationimg()).into(CERTdetailed_img);
            Glide.with(getApplicationContext()).load(donation.getDonationdetailimg()).into(CERTdetail_longimg);
            CERTdetailed_name.setText(donation.getDonationname() + " 기부 결산 내역");
            CERTdetail_start.setText(donation.getDonationstart());
            CERTdetail_end.setText(donation.getDonationend());
        }
    }
}