package com.example.greeningapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class ManageUserDetailActivity extends AppCompatActivity {

    TextView UserEmail, UserName, Password, UserPhone, UserToken, UserPostcode, UserAdderess, UserDoquiz, UserSPoint, UserUPoint, UserRegdate;
    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user_detail);

        // 툴바
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        // 레이아웃 요소 설정 초기화
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

        // object 객체에 ManageUserAdapter에서 받은 정보 담기
        final Object object = getIntent().getSerializableExtra("ManageUserDetail");
        // object 객체 User 객체로 형변환
        if(object instanceof User){
            user = (User) object;
        }

        //
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

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) { //뒤로가기
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}