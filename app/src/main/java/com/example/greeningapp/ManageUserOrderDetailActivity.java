package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.w3c.dom.Text;

public class ManageUserOrderDetailActivity extends AppCompatActivity {
    TextView MGOrderName_detail, MGUserIDToken_detail, MGOrderID_detail, MGEachOrderID_detail;
    TextView MGOrderDate_detail, MGOrderProductID_detail, MGOrderProductName_detail, MGProductPrice_detail;
    TextView MGOrderStock_detail, MGOrderTotalPrice_detail, MGOverTotalPrice_detail, MGOrderPhone_detail;
    TextView MGOrderPostcode_detail, MGOrderAddress_detail, MGOrderState_detail, MGOrderDoReview_detail;
    private MyOrder myOrder = null;
    Button MGRemoveOrder, MGOrderStateModify;
    Dialog dialog;
    Dialog dialog2;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceAdmin;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user_order_detail);

        // 툴바
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        // 레이아웃 요소 설정 초기화
        MGOrderName_detail = (TextView) findViewById(R.id.MGOrderName_detail);
        MGUserIDToken_detail = (TextView) findViewById(R.id.MGUserIDToken_detail);
        MGOrderID_detail = (TextView) findViewById(R.id.MGOrderID_detail);
        MGEachOrderID_detail = (TextView) findViewById(R.id.MGEachOrderID_detail);
        MGOrderDate_detail = (TextView) findViewById(R.id.MGOrderDate_detail);
        MGOrderProductID_detail = (TextView) findViewById(R.id.MGOrderProductID_detail);
        MGOrderProductName_detail = (TextView) findViewById(R.id.MGOrderProductName_detail);
        MGProductPrice_detail = (TextView) findViewById(R.id.MGProductPrice_detail);
        MGOrderStock_detail = (TextView) findViewById(R.id.MGOrderStock_detail);
        MGOrderTotalPrice_detail = (TextView) findViewById(R.id.MGOrderTotalPrice_detail);
        MGOverTotalPrice_detail = (TextView) findViewById(R.id.MGOrderTotalPrice_detail);
        MGOrderPhone_detail = (TextView) findViewById(R.id.MGOrderPhone_detail);
        MGOrderPostcode_detail = (TextView) findViewById(R.id.MGOrderPostcode_detail);
        MGOrderAddress_detail = (TextView) findViewById(R.id.MGOrderAddress_detail);
        MGOrderState_detail = (TextView) findViewById(R.id.MGOrderState_detail);
        MGOrderDoReview_detail = (TextView) findViewById(R.id.MGOrderDoReview_detail);
        MGRemoveOrder = (Button) findViewById(R.id.MGRemoveOrder);
        MGOrderStateModify = (Button) findViewById(R.id.MGOrderStateModify);

        // 파이어베이스 경로 설정
        database = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("CurrentUser");
        databaseReferenceAdmin = FirebaseDatabase.getInstance().getReference("Admin");
        firebaseAuth = FirebaseAuth.getInstance();

        // 다이얼로그 객체 생성
        dialog = new Dialog(ManageUserOrderDetailActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm2);

        dialog2 = new Dialog(ManageUserOrderDetailActivity.this);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.setContentView(R.layout.dialog_confirm2);

        // Object 객체에 ManageUserOrderAdapter.java 에서 받은 회원 정보 담기
        final Object object = getIntent().getSerializableExtra("ManageUserOrderDetail");
        // Object 객체 MyOrder 객체로 형변환
        if(object instanceof MyOrder){
            myOrder = (MyOrder) object;
        }

        // ManageUserOrderAdapter.java 에서 받은 회원 데이터 레이아웃에 담기
        MGOrderName_detail.setText(myOrder.getUserName());
        MGUserIDToken_detail.setText(myOrder.getUseridtoken());
        MGOrderID_detail.setText(myOrder.getOrderId());
        MGEachOrderID_detail.setText(myOrder.getEachOrderedId());
        MGOrderDate_detail.setText(myOrder.getOrderDate());
        MGOrderProductID_detail.setText(String.valueOf(myOrder.getProductId()));
        MGOrderProductName_detail.setText(myOrder.getProductName());
        MGProductPrice_detail.setText(String.valueOf(myOrder.getProductPrice()));
        MGOrderStock_detail.setText(String.valueOf(myOrder.getTotalQuantity()));
        MGOrderTotalPrice_detail.setText(String.valueOf(myOrder.getTotalPrice()));
        MGOverTotalPrice_detail.setText(String.valueOf(myOrder.getTotalPrice()));
        MGOrderPhone_detail.setText(myOrder.getPhone());
        MGOrderPostcode_detail.setText(myOrder.getPostcode());
        MGOrderAddress_detail.setText(myOrder.getAddress());
        MGOrderState_detail.setText(myOrder.getOrderstate());
        MGOrderDoReview_detail.setText(myOrder.getDoReview());

        // 주문 내역 삭제 버튼 클릭 시
        MGRemoveOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();

                // 방어적인 다이얼로그 띄우기
                TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
                confirmTextView.setText("주문을 삭제하시겠습니까?\n삭제 후에는 작업을 되돌릴 수 없습니다.");

                Button btnleft1 = dialog.findViewById(R.id.btn_left);
                btnleft1.setText("취소");
                btnleft1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                Button btnright1 = dialog.findViewById(R.id.btn_right);
                btnright1.setText("확인");
                btnright1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 관리자 계정에서 받은 회원 주문 데이터베이스 경로를 받아서 회원의 주문 원본 데이터베이스를 삭제 처리
                        databaseReference.child(String.valueOf(myOrder.getUseridtoken())).child("MyOrder").child(myOrder.getOrderId()).child(myOrder.getEachOrderedId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog.dismiss();
                                Log.d("ManageUserOrderDetailActivity", "주문 삭제 완료");
                                Intent intent = new Intent(ManageUserOrderDetailActivity.this, ManageUserOrderActivity.class);
                                startActivity(intent);

                            }
                        });

                        // 관리자 계정에 있는 회원 주문 정보 또한 삭제
                        databaseReferenceAdmin.child("UserOrder").child(myOrder.getEachOrderedId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog.dismiss();
                                Log.d("ManageUserOrderDetailActivity", "주문 삭제 완료");
                                Intent intent = new Intent(ManageUserOrderDetailActivity.this, ManageUserOrderActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                });

            }
        });

        // 주문 상태를 배송 완료 처리하는 버튼 클릭 시
        MGOrderStateModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog2.show();

                // 방어적인 다이얼로그 띄우기
                TextView confirmTextView = dialog2.findViewById(R.id.confirmTextView);
                confirmTextView.setText("배송 완료로 처리하시겠습니까?\n처리 후에는 작업을 되돌릴 수 없습니다.");

                Button btnleft1 = dialog2.findViewById(R.id.btn_left);
                btnleft1.setText("취소");
                btnleft1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.dismiss();
                    }
                });

                Button btnright1 = dialog2.findViewById(R.id.btn_right);
                btnright1.setText("확인");
                btnright1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 관리자 계정에서 받은 회원 주문 데이터베이스 경로를 받아서 회원의 주문 원본 데이터베이스의 배송 상태 변경
                        databaseReference.child(String.valueOf(myOrder.getUseridtoken())).child("MyOrder").child(myOrder.getOrderId()).child(myOrder.getEachOrderedId()).child("orderstate").setValue("shipped").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog2.dismiss();
                                Log.d("ManageUserOrderDetailActivity", "배송 완료 처리");
                                finish();

                            }
                        });

                        // 관리자 계정에 있는 회원 주문 상태 또한 변경
                        databaseReferenceAdmin.child("UserOrder").child(myOrder.getEachOrderedId()).child("orderstate").setValue("shipped").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog2.dismiss();
                                Log.d("ManageUserOrderDetailActivity", "배송 완료 처리");
                                finish();

                            }
                        });
                    }
                });
            }
        });
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