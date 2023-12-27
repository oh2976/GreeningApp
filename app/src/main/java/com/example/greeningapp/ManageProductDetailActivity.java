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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ManageProductDetailActivity extends AppCompatActivity {
    private EditText ModifyPid, ModifyCategoryId, ModifyPimg, ModifyPDetailimg, ModifyPname, ModifyPPrice, ModifyPsay, ModifyStock, ModifyPPopulstock;
    private int strModifyPid, strModifyCategoryId, strModifyPPrice, strModifyStock, strModifyPPopulstock;
    private String strModifyPimg, strModifyPDetailimg, strModifyPname, strModifyPsay;
    private Button MGRemoveProduct, MGModifiyProduct;
    Dialog dialog, dialog2;
    private Product product = null;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_product_detail);

        // 툴바
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        // 레이아웃 요소 설정 초기화
        ModifyPid = (EditText) findViewById(R.id.ModifyPid);
        ModifyCategoryId = (EditText) findViewById(R.id.ModifyCategoryId);
        ModifyPimg = (EditText) findViewById(R.id.ModifyPimg);
        ModifyPDetailimg = (EditText) findViewById(R.id.ModifyPDetailimg);
        ModifyPname = (EditText) findViewById(R.id.ModifyPname);
        ModifyPPrice = (EditText) findViewById(R.id.ModifyPPrice);
        ModifyPsay = (EditText) findViewById(R.id.ModifyPsay);
        ModifyStock = (EditText) findViewById(R.id.ModifyStock);
        ModifyPPopulstock = (EditText) findViewById(R.id.ModifyPPopulstock);
        MGRemoveProduct = (Button) findViewById(R.id.MGRemoveProduct);
        MGModifiyProduct = (Button) findViewById(R.id.MGProductModify);

        // ProductAdapter에서 받은 정보 object 객체 안에 넣기
        final Object object = getIntent().getSerializableExtra("ManageProductDetail");

        // object 객체 Product 객체로 형변환
        if(object instanceof Product){
            product = (Product) object;
        }

        // 파이어베이스 경로 설정
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Product");

        // 다이얼로그 객체 생성
        dialog = new Dialog(ManageProductDetailActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm2);
        dialog2 = new Dialog(ManageProductDetailActivity.this);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.setContentView(R.layout.dialog_confirm2);

        // ProductAdapter에서 받은 데이터 레이아웃에 넣기
        ModifyPid.setText(String.valueOf(product.getPid()));
        ModifyCategoryId.setText(String.valueOf(product.getCategory()));
        ModifyPimg.setText(String.valueOf(product.getPimg()));
        ModifyPDetailimg.setText(String.valueOf(product.getPdetailimg()));
        ModifyPname.setText(String.valueOf(product.getPname()));
        ModifyPPrice.setText(String.valueOf(product.getPprice()));
        ModifyPsay.setText(String.valueOf(product.getPsay()));
        ModifyStock.setText(String.valueOf(product.getStock()));
        ModifyPPopulstock.setText(String.valueOf(product.getPopulstock()));

        // 상품 삭제 버튼 클릭 시
        MGRemoveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                // 상품 삭제에 관한 방어적 다이얼로그 띄우기
                TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
                confirmTextView.setText("상품을 삭제하시겠습니까?\n삭제 후에는 작업을 되돌릴 수 없습니다.");

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
                        // 해당 상품 테이블에서 삭제 처리
                        databaseReference.child(String.valueOf(product.getPid())).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog.dismiss();
                                Log.d("ManageProductDetail", "상품 삭제 완료");
                                Intent intent = new Intent(ManageProductDetailActivity.this, ShoppingMainActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        });
                    }
                });
            }
        });

        // 상품 정보 수정 버튼 클릭 시
        MGModifiyProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.show();

                // 상품 정보 수정에 관한 방어적 다이얼로그 띄우기
                TextView confirmTextView = dialog2.findViewById(R.id.confirmTextView);
                confirmTextView.setText("상품 정보를 수정하시겠습니까?\n수정 후에는 작업을 되돌릴 수 없습니다.");

                Button btnleft2 = dialog2.findViewById(R.id.btn_left);
                btnleft2.setText("취소");
                btnleft2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.dismiss();
                    }
                });

                Button btnright2 = dialog2.findViewById(R.id.btn_right);
                btnright2.setText("확인");
                btnright2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.dismiss();

                        // 관리자 수정한 내용, 레이아웃에 담긴 데이터 변수에 담기
                        strModifyPid = Integer.parseInt(ModifyPid.getText().toString().trim());
                        strModifyCategoryId = Integer.parseInt(ModifyCategoryId.getText().toString().trim());
                        strModifyPimg = ModifyPimg.getText().toString().trim();
                        strModifyPDetailimg = ModifyPDetailimg.getText().toString().trim();
                        strModifyPname = ModifyPname.getText().toString().trim();
                        strModifyPPrice = Integer.parseInt(ModifyPPrice.getText().toString().trim());
                        strModifyPsay = ModifyPsay.getText().toString().trim();
                        strModifyStock = Integer.parseInt(ModifyStock.getText().toString().trim());
                        strModifyPPopulstock = Integer.parseInt(ModifyPPopulstock.getText().toString().trim());

                        // 관리자가 수정한 내용, 레이아웃에 담긴 데이터로 데이터베이스 값 변경
                        databaseReference.child(String.valueOf(product.getPid())).child("pid").setValue(strModifyPid);
                        databaseReference.child(String.valueOf(product.getPid())).child("category").setValue(strModifyCategoryId);
                        databaseReference.child(String.valueOf(product.getPid())).child("pdetailimg").setValue(strModifyPDetailimg);
                        databaseReference.child(String.valueOf(product.getPid())).child("pimg").setValue(strModifyPimg);
                        databaseReference.child(String.valueOf(product.getPid())).child("pname").setValue(strModifyPname);
                        databaseReference.child(String.valueOf(product.getPid())).child("pprice").setValue(strModifyPPrice);
                        databaseReference.child(String.valueOf(product.getPid())).child("psay").setValue(strModifyPsay);
                        databaseReference.child(String.valueOf(product.getPid())).child("stock").setValue(strModifyStock);
                        databaseReference.child(String.valueOf(product.getPid())).child("populstock").setValue(strModifyPPopulstock);

                        Log.d("ManageProductDetail", "상품 수정 완료" + strModifyStock + strModifyStock);

                        // 데이터베이스 업데이트가 완료된 후 액티비티를 다시 시작(안 하면 수정 전 데이터를 띄우는 오류 발생)
                        databaseReference.child(String.valueOf(product.getPid())).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // 데이터를 가져와서 액티비티를 업데이트
                                Product updatedProduct = dataSnapshot.getValue(Product.class);

                                // 액티비티를 다시 시작
                                Intent intent = new Intent(ManageProductDetailActivity.this, ManageProductDetailActivity.class);
                                intent.putExtra("ManageProductDetail", updatedProduct);
                                startActivity(intent);
                                Toast.makeText(ManageProductDetailActivity.this, "상품 수정이 되었습니다.", Toast.LENGTH_SHORT).show();
                                finish(); // 현재 액티비티를 종료
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // 에러 처리
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
