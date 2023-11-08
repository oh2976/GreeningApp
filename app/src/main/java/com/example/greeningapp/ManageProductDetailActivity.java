package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.auth.FirebaseAuth;
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

    Dialog dialog;

    Dialog dialog2;

    private Product product = null;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private Uri imageUri; // 이미지의 URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_product_detail);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김


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

        final Object object = getIntent().getSerializableExtra("ManageProductDetail");

        if(object instanceof Product){
            product = (Product) object;
        }

        database = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Product");

        firebaseAuth = FirebaseAuth.getInstance();

        dialog = new Dialog(ManageProductDetailActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm2);

        dialog2 = new Dialog(ManageProductDetailActivity.this);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.setContentView(R.layout.dialog_confirm2);

        ModifyPid.setText(String.valueOf(product.getPid()));
        ModifyCategoryId.setText(String.valueOf(product.getCategory()));
        ModifyPimg.setText(String.valueOf(product.getPimg()));
        ModifyPDetailimg.setText(String.valueOf(product.getPdetailimg()));
        ModifyPname.setText(String.valueOf(product.getPname()));
        ModifyPPrice.setText(String.valueOf(product.getPprice()));
        ModifyPsay.setText(String.valueOf(product.getPsay()));
        ModifyStock.setText(String.valueOf(product.getStock()));
        ModifyPPopulstock.setText(String.valueOf(product.getPopulstock()));






        MGRemoveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();

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

        MGModifiyProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.show();

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

                        strModifyPid = Integer.parseInt(ModifyPid.getText().toString().trim());
                        strModifyCategoryId = Integer.parseInt(ModifyCategoryId.getText().toString().trim());
                        strModifyPimg = ModifyPimg.getText().toString().trim();
                        strModifyPDetailimg = ModifyPDetailimg.getText().toString().trim();
                        strModifyPname = ModifyPname.getText().toString().trim();
                        strModifyPPrice = Integer.parseInt(ModifyPPrice.getText().toString().trim());
                        strModifyPsay = ModifyPsay.getText().toString().trim();
                        strModifyStock = Integer.parseInt(ModifyStock.getText().toString().trim());
                        strModifyPPopulstock = Integer.parseInt(ModifyPPopulstock.getText().toString().trim());


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


//                        recreate();

                        // 데이터베이스 업데이트가 완료된 후, 현재 액티비티를 종료
//                        finish();
//
//                        // 수정된 데이터를 다시 불러오기 위해 현재 액티비티를 다시 시작
//                        Intent intent = new Intent(ManageProductDetailActivity.this, ManageProductDetailActivity.class);
//                        intent.putExtra("ManageProductDetail", product); // product 변수는 수정 전 데이터
//                        startActivity(intent);


                        // 데이터베이스 업데이트가 완료된 후 액티비티를 다시 시작
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
