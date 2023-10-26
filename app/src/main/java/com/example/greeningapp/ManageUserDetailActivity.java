package com.example.greeningapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ManageUserDetailActivity extends AppCompatActivity {

    TextView UserEmail, UserName, Password, UserPhone, UserToken, UserPostcode, UserAdderess, UserDoquiz, UserSPoint, UserUPoint, UserRegdate;

    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user_detail);

        UserEmail = (TextView) findViewById(R.id.UserEmail);
        UserName = (TextView) findViewById(R.id.UserName);
        Password = (TextView) findViewById(R.id.UserPassword);
        UserPhone = (TextView) findViewById(R.id.UserPhone);
        UserToken = (TextView) findViewById(R.id.UserToken);
        UserPostcode = (TextView) findViewById(R.id.UserPostcode);
        UserAdderess = (TextView) findViewById(R.id.UserAderess);
        UserDoquiz = (TextView) findViewById(R.id.UserDoquiz);
        UserSPoint = (TextView) findViewById(R.id.UserSPoint);
        UserUPoint = (TextView) findViewById(R.id.UserUPoint);
        UserRegdate = (TextView) findViewById(R.id.UserRegdate);

        final Object object = getIntent().getSerializableExtra("ManageUserDetail");
        if(object instanceof User){
            user = (User) object;
        }

        UserEmail.setText(user.getEmailId());
        UserName.setText(user.getUsername());
        Password.setText(user.getPassword());
        UserPhone.setText(user.getPhone());
        UserToken.setText(user.getIdToken());
        UserPostcode.setText(user.getPostcode());
        UserAdderess.setText(user.getAddress());
        UserDoquiz.setText(user.getDoquiz());
        UserSPoint.setText(String.valueOf(user.getSpoint()));
        UserUPoint.setText(String.valueOf(user.getUpoint()));
        UserRegdate.setText(user.getRegdate());


    }
}