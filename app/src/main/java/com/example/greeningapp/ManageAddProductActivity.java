package com.example.greeningapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.function.Consumer;

public class ManageAddProductActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final int GALLERY_REQUEST_1 = 1;
    private static final int GALLERY_REQUEST_2 = 2;

    private EditText AddPid, AddCategoryId,  AddPname, AddPPrice, AddPsay, AddPSearch, AddStock;


    private int strAddPid, strAddCategoryId, strAddPPrice, strAddStock;

    private String strAddPimg, strAddPDetailimg, strAddPname, strAddPsay, strAddPSearch, strAddService;

    private ImageButton AddPimg, AddPDetailimg;

    private Uri imageUri1, imageUri2;
    private Button btnMGProductAdd;


    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_add_product);

        // Firebase Realtime Database 및 Firebase Storage에 대한 레퍼런스 생성
        databaseReference = FirebaseDatabase.getInstance().getReference("Product");
        storageReference = FirebaseStorage.getInstance().getReference();

        // UI 요소 초기화
        AddPid = (EditText) findViewById(R.id.AddPid);
//        AddCategoryId = (EditText) findViewById(R.id.AddCategoryId);
        AddPimg = (ImageButton) findViewById(R.id.AddPImg);
        AddPDetailimg = (ImageButton) findViewById(R.id.AddPDetailimg);
        AddPname = (EditText) findViewById(R.id.AddPname);
        AddPPrice = (EditText) findViewById(R.id.AddPPrice);
        AddPsay = (EditText) findViewById(R.id.AddPsay);
        AddPSearch = (EditText) findViewById(R.id.AddPSearch);
        AddStock = (EditText) findViewById(R.id.AddStock);

        btnMGProductAdd = (Button) findViewById(R.id.btnMGProductAdd);

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

        Spinner spinner = findViewById(R.id.category_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,

                // R.array.gender_array는 2번에서 설정한 string-array 태그의 name 입니다.
                R.array.category_select_item,

                // android.R.layout.simple_spinner_dropdown_item은 android에서 기본 제공
                // 되는 layout 입니다. 이 부분은 "선택된 item" 부분의 layout을 결정합니다.
                android.R.layout.simple_spinner_dropdown_item
        );

        // android.R.layout.simple_spinner_dropdown_item도 android에서 기본 제공
        // 되는 layout 입니다. 이 부분은 "선택할 item 목록" 부분의 layout을 결정합니다.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);




        btnMGProductAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strAddPid = Integer.parseInt(AddPid.getText().toString());
//                strAddCategoryId = Integer.parseInt(AddCategoryId.getText().toString());
//                strAddPimg = AddPimg.getText().toString();
//                strAddPDetailimg = AddPDetailimg.getText().toString();
                strAddPname = AddPname.getText().toString();
                strAddPPrice = Integer.parseInt(AddPPrice.getText().toString());
                strAddPsay = AddPsay.getText().toString();
                strAddPSearch = AddPSearch.getText().toString();
                strAddStock = Integer.parseInt(AddStock.getText().toString());
//
//                final HashMap<String, Object> ProductAddMap = new HashMap<>();
//
//                ProductAddMap.put("pid", strAddPid);
//                ProductAddMap.put("category", strAddCategoryId);
//                ProductAddMap.put("pdetailimg", strAddPDetailimg);
//                ProductAddMap.put("pimg", strAddPimg);
//                ProductAddMap.put("pname", strAddPname);
//                ProductAddMap.put("pprice", strAddPPrice);
//                ProductAddMap.put("psay", strAddPsay);
//                ProductAddMap.put("psearch", strAddPSearch);
//                ProductAddMap.put("stock", strAddStock);

                String selectedCategory = spinner.getSelectedItem().toString();

                // 선택된 아이템에 따라 값을 설정

                if (selectedCategory.equals("101-욕실주방용품")) {
                    strAddCategoryId = 101;
                } else if (selectedCategory.equals("102-생활잡화")) {
                    strAddCategoryId = 102;
                } else if (selectedCategory.equals("103-취미")) {
                    strAddCategoryId = 103;
                } else {
                    // 기본값 설정 (선택 사항)
                    strAddCategoryId = 102;
                }
//
                dialog.show();

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

//                        databaseReference.child(String.valueOf(strAddPid)).setValue(ProductAddMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                Log.d("ManageAddProductActivity", strAddPid + strAddCategoryId + strAddPPrice + strAddStock + strAddPimg + strAddPDetailimg + strAddPname + strAddPsay + strAddPSearch);
//                            }
//                        });
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

    // 이미지 업로드 및 데이터베이스 저장 처리
    private void uploadImagesAndSaveData() {
        if (imageUri1 != null && imageUri2 != null) {
            StorageReference filePath1 = storageReference.child("images").child(imageUri1.getLastPathSegment());
            StorageReference filePath2 = storageReference.child("images").child(imageUri2.getLastPathSegment());

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
                                            // 두 번째 이미지의 다운로드 URL을 가져옵니다.
                                            filePath2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri2) {
                                                    String imageUrl2 = uri2.toString();

                                                    // 나머지 데이터를 Firebase Realtime Database에 저장
                                                    DatabaseReference productRef = databaseReference.child(String.valueOf(strAddPid));
                                                    productRef.child("pimg").setValue(imageUrl1);
                                                    productRef.child("pdetailimg").setValue(imageUrl2);
                                                    productRef.child("pid").setValue(strAddPid);
                                                    productRef.child("category").setValue(strAddCategoryId);
                                                    productRef.child("pname").setValue(strAddPname);
                                                    productRef.child("pprice").setValue(strAddPPrice);
                                                    productRef.child("psay").setValue(strAddPsay);
                                                    productRef.child("psearch").setValue(strAddPSearch);
                                                    productRef.child("stock").setValue(strAddStock);

                                                    Toast.makeText(ManageAddProductActivity.this, "상품이 추가되었습니다.", Toast.LENGTH_SHORT).show();
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
            // 이미지가 선택되지 않았을 경우에 대한 처리
            DatabaseReference productRef = databaseReference.child(String.valueOf(strAddPid));
            productRef.child("pimg").setValue("");
            productRef.child("pdetailimg").setValue("");
            productRef.child("pid").setValue(strAddPid);
            productRef.child("category").setValue(strAddCategoryId);
            productRef.child("pname").setValue(strAddPname);
            productRef.child("pprice").setValue(strAddPPrice);
            productRef.child("psay").setValue(strAddPsay);
            productRef.child("psearch").setValue(strAddPSearch);
            productRef.child("stock").setValue(strAddStock);

            Toast.makeText(ManageAddProductActivity.this, "상품이 추가되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }


}