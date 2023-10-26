package com.example.greeningapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class ManagerMainActivity extends AppCompatActivity {

    Dialog dialog;
    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증 처리


    Button ProductManage, AddProduct, UserManage, UserOrderManage, UserReviewManage, Logout, AppMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_main);

        mFirebaseAuth = FirebaseAuth.getInstance();

        dialog = new Dialog(ManagerMainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.customdialog);

        ProductManage = (Button) findViewById(R.id.ProductManage);
        ProductManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerMainActivity.this, ShoppingMainActivity.class);
                startActivity(intent);
            }
        });


        AddProduct = (Button) findViewById(R.id.AddProduct);
        AddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerMainActivity.this, ManageAddProductActivity.class);
                startActivity(intent);

            }
        });

        UserManage = (Button) findViewById(R.id.UserManage);
        UserManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerMainActivity.this, ManageUserActivity.class);
                startActivity(intent);

            }
        });

        UserOrderManage = (Button) findViewById(R.id.UserOrderManage);
        UserOrderManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ManagerMainActivity.this, ManageUserOrderActivity.class);
                startActivity(intent);

            }
        });

        UserReviewManage = (Button) findViewById(R.id.UserReviewManage);
        UserReviewManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerMainActivity.this, ManageUserReviewActivity.class);
                startActivity(intent);
            }
        });

        AppMain = (Button) findViewById(R.id.AppMain);
        AppMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerMainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Logout = (Button) findViewById(R.id.Logout);
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmationDialog();
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