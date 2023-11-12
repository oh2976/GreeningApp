package com.example.greeningapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class ManagerMainActivity extends AppCompatActivity {

    Dialog dialog;
    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증 처리


//    Button ProductManage, AddProduct, UserManage, UserOrderManage, UserReviewManage, Logout, AppMain, DataStatistics;

    TextView LogoutTxt;
    Button Btn_ManageProduct, Btn_AddProduct, Btn_ManageUser, Btn_ManageOrder, Btn_ManageReview, Btn_GoToShoppingMall, Btn_DataStatistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_main);

        mFirebaseAuth = FirebaseAuth.getInstance();

        dialog = new Dialog(ManagerMainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.customdialog);

        Btn_ManageProduct = (Button) findViewById(R.id.Btn_ManageProduct);
        Btn_ManageProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerMainActivity.this, ShoppingMainActivity.class);
                startActivity(intent);
            }
        });


        Btn_AddProduct = (Button) findViewById(R.id.Btn_AddProduct);
        Btn_AddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerMainActivity.this, ManageAddProductActivity.class);
                startActivity(intent);

            }
        });

        Btn_ManageUser = (Button) findViewById(R.id.Btn_ManageUser);
        Btn_ManageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerMainActivity.this, ManageUserActivity.class);
                startActivity(intent);

            }
        });

        Btn_ManageOrder = (Button) findViewById(R.id.Btn_ManageOrder);
        Btn_ManageOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ManagerMainActivity.this, ManageUserOrderActivity.class);
                startActivity(intent);

            }
        });

        Btn_ManageReview = (Button) findViewById(R.id.Btn_ManageReview);
        Btn_ManageReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerMainActivity.this, ManageUserReviewActivity.class);
                startActivity(intent);
            }
        });

        Btn_GoToShoppingMall = (Button) findViewById(R.id.Btn_GoToShoppingMall);
        Btn_GoToShoppingMall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerMainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        LogoutTxt = (TextView) findViewById(R.id.LogoutTxt);

        String mystring = LogoutTxt.getText().toString();
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        LogoutTxt.setText(content);

        LogoutTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog();
            }
        });

        Btn_DataStatistics = (Button) findViewById(R.id.Btn_DataStatistics);
        Btn_DataStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerMainActivity.this, ManageDataStatisticsActivity.class);
                startActivity(intent);
            }
        });
    }

    //로그아웃 확인
    public void showLogoutConfirmationDialog() {

        dialog.show();

        TextView confirmTextView = dialog.findViewById(R.id.say);
        confirmTextView.setText("로그아웃하시겠습니까?");

        Button btnno = dialog.findViewById(R.id.btnNo);
        Button btnok = dialog.findViewById(R.id.btnOk);
        btnno.setText("아니요");
        btnok.setText("예");

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자 계정 삭제
                logout();
                dialog.dismiss();
            }
        });

        btnno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void logout() {
        mFirebaseAuth.signOut();
        Intent intent = new Intent(ManagerMainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}