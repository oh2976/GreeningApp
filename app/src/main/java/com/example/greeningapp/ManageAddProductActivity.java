package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ManageAddProductActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final int GALLERY_REQUEST_1 = 1;
    private static final int GALLERY_REQUEST_2 = 2;
    private EditText AddPid,  AddPname, AddPPrice, AddPsay, AddStock, AddPopulstock;
    private int strAddPid, strAddCategoryId, strAddPPrice, strAddStock, strAddPopulstock;
    private String strAddPname, strAddPsay;
    private ImageButton AddPimg, AddPDetailimg;
    private Uri imageUri1, imageUri2;
    private Button btnMGProductAdd;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_add_product);

        // 툴바
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        // 파이어베이스 경로 설정
        databaseReference = FirebaseDatabase.getInstance().getReference("Product");
        storageReference = FirebaseStorage.getInstance().getReference();

        // 레이아웃 요소 초기화
        AddPid = (EditText) findViewById(R.id.AddPid);
        AddPimg = (ImageButton) findViewById(R.id.AddPImg);
        AddPDetailimg = (ImageButton) findViewById(R.id.AddPDetailimg);
        AddPname = (EditText) findViewById(R.id.AddPname);
        AddPPrice = (EditText) findViewById(R.id.AddPPrice);
        AddPsay = (EditText) findViewById(R.id.AddPsay);
        AddStock = (EditText) findViewById(R.id.AddStock);
        AddPopulstock = (EditText) findViewById(R.id.AddPPopulstock);
        btnMGProductAdd = (Button) findViewById(R.id.btnMGProductAdd);

        // 다이얼로그 객체 생성
        dialog = new Dialog(ManageAddProductActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm2);

        AddPimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미지 선택 인텐트를 생성
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_1);
            }
        });

        AddPDetailimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미지 선택 인텐트를 생성
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_2);
            }
        });

        // 카테고리 드롭다운 메뉴 생성
        Spinner spinner = findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.category_select_item,
                android.R.layout.simple_spinner_dropdown_item
        );

        // 안드로이드에서 기본으로 제공하는 adapter 생성 후 spinner에 적용
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // 상품 추가 버튼 클릭 시
        btnMGProductAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 변수에 각각의 레이아웃에서 데이터 가져와서 담기
                strAddPid = Integer.parseInt(AddPid.getText().toString());
                strAddPname = AddPname.getText().toString();
                strAddPPrice = Integer.parseInt(AddPPrice.getText().toString());
                strAddPsay = AddPsay.getText().toString();
                strAddStock = Integer.parseInt(AddStock.getText().toString());
                strAddPopulstock = Integer.parseInt(AddPopulstock.getText().toString());

                String selectedCategory = spinner.getSelectedItem().toString();

                // 선택된 아이템에 따라 값이 다르게 설정
                if (selectedCategory.equals("101-욕실주방용품")) {
                    strAddCategoryId = 101;
                } else if (selectedCategory.equals("102-생활잡화")) {
                    strAddCategoryId = 102;
                } else if (selectedCategory.equals("103-취미")) {
                    strAddCategoryId = 103;
                } else {
                    strAddCategoryId = 102;
                }

                dialog.show();

                // 상품 추가 시 방어적으로 다시 한 번 물어보는 다이얼로그 생성
                TextView confirmTextView = dialog.findViewById(R.id.confirmTextView);
                confirmTextView.setText("상품을 추가하시겠습니까?\n삭제 후에는 작업을 되돌릴 수 없습니다.");

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
                        // 이미지 업로드 후 데이터베이스에 저장
                        dialog.dismiss();
                        uploadImagesAndSaveData();
                    }
                });
            }
        });
    }

    // 이미지 선택 다이얼로그에서 이미지를 선택한 후 호출되는 메서드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_1 && resultCode == RESULT_OK) {
            imageUri1 = data.getData();
            AddPimg.setImageURI(imageUri1);
        } else if (requestCode == GALLERY_REQUEST_2 && resultCode == RESULT_OK) {
            imageUri2 = data.getData();
            AddPDetailimg.setImageURI(imageUri2);
        }
    }

    // 이미지 데이터 저장 및 데이터베이스 저장
    private void uploadImagesAndSaveData() {
        if (imageUri1 != null && imageUri2 != null) {
            StorageReference filePath1 = storageReference.child("ProductImages").child(imageUri1.getLastPathSegment());
            StorageReference filePath2 = storageReference.child("ProductImages").child(imageUri2.getLastPathSegment());

            // 첫 번째 이미지를 Firebase Storage에 업로드
            filePath1.putFile(imageUri1).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task1) {
                    if (task1.isSuccessful()) {
                        // 첫 번째 이미지의 다운로드 URL을 가져옵니다.
                        filePath1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri1) {
                                String imageUrl1 = uri1.toString();

                                // 두 번째 이미지를 Firebase Storage에 업로드
                                filePath2.putFile(imageUri2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task2) {
                                        if (task2.isSuccessful()) {
                                            // 두 번째 이미지의 다운로드 URL을 가져오기
                                            filePath2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri2) {
                                                    String imageUrl2 = uri2.toString();

                                                    // 나머지 데이터를 데이터베이스에 저장
                                                    DatabaseReference productRef = databaseReference.child(String.valueOf(strAddPid));
                                                    productRef.child("pimg").setValue(imageUrl1);
                                                    productRef.child("pdetailimg").setValue(imageUrl2);
                                                    productRef.child("pid").setValue(strAddPid);
                                                    productRef.child("category").setValue(strAddCategoryId);
                                                    productRef.child("pname").setValue(strAddPname);
                                                    productRef.child("pprice").setValue(strAddPPrice);
                                                    productRef.child("psay").setValue(strAddPsay);
                                                    productRef.child("stock").setValue(strAddStock);
                                                    productRef.child("populstock").setValue(strAddPopulstock);

                                                    Toast.makeText(ManageAddProductActivity.this, "상품이 추가되었습니다.", Toast.LENGTH_SHORT).show();

                                                    Intent intent = new Intent(ManageAddProductActivity.this, ManagerMainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                        } else {
                                            // 두 번째 이미지 업로드 실패 처리
                                            Toast.makeText(ManageAddProductActivity.this, "두 번째 이미지 업로드 실패", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    } else {
                        // 첫 번째 이미지 업로드 실패 처리
                        Toast.makeText(ManageAddProductActivity.this, "첫 번째 이미지 업로드 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            // 이미지가 선택되지 않았을 경우 이미지 url은 빈 문자열로 처리해 데이터 저장
            DatabaseReference productRef = databaseReference.child(String.valueOf(strAddPid));
            productRef.child("pimg").setValue("");
            productRef.child("pdetailimg").setValue("");
            productRef.child("pid").setValue(strAddPid);
            productRef.child("category").setValue(strAddCategoryId);
            productRef.child("pname").setValue(strAddPname);
            productRef.child("pprice").setValue(strAddPPrice);
            productRef.child("psay").setValue(strAddPsay);
            productRef.child("stock").setValue(strAddStock);
            productRef.child("populstock").setValue(strAddPopulstock);

            Toast.makeText(ManageAddProductActivity.this, "상품이 추가되었습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ManageAddProductActivity.this, ManagerMainActivity.class);
            startActivity(intent);
            finish();
        }
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