package com.example.aplikasiumkm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditProdukActivity extends AppCompatActivity {

    private ImageButton back_btn;
    private ImageView produkIconIv;
    private EditText titleEt, quantityEt, priceEt, diskonHargaEt, deskripsiEt, diskonHargaNoteEt;
    private TextView categoryTv;
    private SwitchCompat diskonswitch;
    private Button updateProdukBtn;

    //Get Permission
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    //Get Image Permission
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    //Get Permission Array
    private String[] cameraPermission;
    private String[] storagePermission;
    //Take Gambar
    private Uri image_uri;
    //firebase
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    private String produkId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_produk);

        back_btn = findViewById(R.id.back_btn);
        produkIconIv = findViewById(R.id.produkIconIv);
        titleEt = findViewById(R.id.titleEt);
        deskripsiEt = findViewById(R.id.deskripsiEt);
        categoryTv = findViewById(R.id.categoryTv);
        quantityEt = findViewById(R.id.quantityEt);
        priceEt = findViewById(R.id.priceEt);
        diskonHargaEt = findViewById(R.id.diskonHargaEt);
        diskonHargaNoteEt = findViewById(R.id.diskonHargaNoteEt);
        diskonswitch = findViewById(R.id.diskonswitch);
        updateProdukBtn = findViewById(R.id.updateProdukBtn);


        produkId = getIntent().getStringExtra("produkId");

        diskonHargaEt.setVisibility(View.GONE);
        diskonHargaNoteEt.setVisibility(View.GONE);


        firebaseAuth = FirebaseAuth.getInstance();
        loadDetailProduk();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);

        //CameraPermission
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        diskonswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    diskonHargaEt.setVisibility(View.VISIBLE);
                    diskonHargaNoteEt.setVisibility(View.VISIBLE);
                }else {
                    diskonHargaEt.setVisibility(View.GONE);
                    diskonHargaNoteEt.setVisibility(View.GONE);
                }
            }
        });

        produkIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showImagePickDialog();
            }
        });
        categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pickCategory
                categoryDialog();
            }
        });
        updateProdukBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                //
                //
                inputData();
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadDetailProduk() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Produk").child(produkId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //getData
                String productId = ""+snapshot.child("productId").getValue();
                String productTitle = ""+snapshot.child("productTitle").getValue();
                String deskripsiProduk = ""+snapshot.child("deskripsiProduk").getValue();
                String categoryProduk = ""+snapshot.child("categoryProduk").getValue();
                String produkQuantity = ""+snapshot.child("produkQuantity").getValue();
                String originalPrice = ""+snapshot.child("originalPrice").getValue();
                String diskonPrice = ""+snapshot.child("diskonPrice").getValue();
                String diskonPriceNote = ""+snapshot.child("diskonPriceNote").getValue();
                String diskonAvailable = ""+snapshot.child("diskonAvailable").getValue();
                String produkIcon = ""+snapshot.child("produkIcon").getValue();
                String timestamp = ""+snapshot.child("timestamp").getValue();
                String uid = ""+snapshot.child("uid").getValue();


                //setDiskonOpened
                if (diskonAvailable.equals("true")){
                    diskonswitch.setChecked(true);

                    diskonHargaEt.setVisibility(View.VISIBLE);
                    diskonHargaNoteEt.setVisibility(View.VISIBLE);
                }else {
                    diskonswitch.setChecked(false);

                    diskonHargaEt.setVisibility(View.GONE);
                    diskonHargaNoteEt.setVisibility(View.GONE);

                }

                titleEt.setText(productTitle);
                deskripsiEt.setText(deskripsiProduk);
                categoryTv.setText(categoryProduk);
                diskonHargaNoteEt.setText(diskonPriceNote);
                diskonHargaEt.setText(diskonPrice);
                quantityEt.setText(produkQuantity);
                priceEt.setText(originalPrice);


                try {
                    Picasso.get().load(produkIcon).placeholder(R.drawable.ic_baseline_add_shopping_cart_yellow).into(produkIconIv);

                }catch (Exception e){
                    produkIconIv.setImageResource(R.drawable.ic_baseline_add_shopping_cart_yellow);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private String productTitle, deskripsiProduk, categoryProduk, produkQuantity, originalPrice, diskonPrice, diskonPriceNote;
    private boolean diskonAvailable = false;

    private void inputData() {
        //inputData
        productTitle = titleEt.getText().toString().trim();
        deskripsiProduk = deskripsiEt.getText().toString().trim();
        categoryProduk = categoryTv.getText().toString().trim();
        produkQuantity = quantityEt.getText().toString().trim();
        originalPrice = priceEt.getText().toString().trim();
        diskonPrice = diskonHargaEt.getText().toString().trim();
        diskonPriceNote = diskonHargaNoteEt.getText().toString().trim();
        diskonAvailable = diskonswitch.isChecked();

        //ValidasiData
        if (TextUtils.isEmpty(productTitle)){
            Toast.makeText(this, "Isi nama produk", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(categoryProduk)){
            Toast.makeText(this, "Isi category produk", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(originalPrice)){
            Toast.makeText(this, "Isi harga produk anda", Toast.LENGTH_SHORT).show();
            return;
        }
        if (diskonAvailable){
            diskonPrice = diskonHargaEt.getText().toString().trim();
            diskonPriceNote = diskonHargaNoteEt.getText().toString().trim();
            if (TextUtils.isEmpty(diskonPrice)){
                Toast.makeText(this,"Isi jumlah diskon harga produk", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else {
            diskonPrice = "0";
            diskonPriceNote = "";
        }

        updateProduk();
    }

    private void updateProduk() {
        progressDialog.setMessage("Update produk..");
        progressDialog.show();


        if (image_uri == null){

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("produkTitle", "" + productTitle);
            hashMap.put("deskripsiProduk", "" + deskripsiProduk);
            hashMap.put("categoryProduk", "" + categoryProduk);
            hashMap.put("produkQuantity", "" + produkQuantity);
            hashMap.put("diskonPrice", "" + diskonPrice);
            hashMap.put("originalPrice", "" + originalPrice);
            hashMap.put("diskonPriceNote", "" + diskonPriceNote);
            hashMap.put("diskonAvailable", "" + diskonAvailable);

            //SavetoDatabase

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Produk").child(produkId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    progressDialog.dismiss();
                    Toast.makeText(EditProdukActivity.this, "Update...", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressDialog.dismiss();
                    Toast.makeText(EditProdukActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }else {
            String filePathAndName = "produk_image/" +"" + produkId;
            //Upload Imgae
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    Uri downloadImageUri = uriTask.getResult();

                    if (uriTask.isSuccessful()){

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("produkTitle", "" + productTitle);
                        hashMap.put("deskripsiProduk", "" + deskripsiProduk);
                        hashMap.put("categoryProduk", "" + categoryProduk);
                        hashMap.put("produkQuantity", "" + produkQuantity);
                        hashMap.put("produkIcon", "" + downloadImageUri);
                        hashMap.put("diskonPrice", "" + diskonPrice);
                        hashMap.put("originalPrice", "" + originalPrice);
                        hashMap.put("diskonPriceNote", "" + diskonPriceNote);
                        hashMap.put("diskonAvailable", "" + diskonAvailable);


                        //SavetoDatabase

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                        reference.child(firebaseAuth.getUid()).child("Produk").child(produkId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                progressDialog.dismiss();
                                Toast.makeText(EditProdukActivity.this, "Update...", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                progressDialog.dismiss();
                                Toast.makeText(EditProdukActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(EditProdukActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }


    }

    private void categoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kategori Produk").setItems(Constants.pilihan, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String category = Constants.pilihan[i];

                categoryTv.setText(category);

            }
        }).show();
    }
    private void showImagePickDialog() {
        //Option
        String[] option = {"Camera","Gallery"};
        //Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image").setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Item click
                if (i == 0){
                    //Camera
                    if (checkCameraPermission()){
                        //takePermission
                        pickFromCamera();
                    }
                    else {
                        //Failed
                        requestCameraPermission();
                    }

                }
                else {
                    //Gallery
                    if (checkStoragePermission()){
                        pickFromGallery();
                    }
                    else {
                        //failed
                        checkStoragePermission();

                    }
                }
            }
        }).show();

    }

    private void pickFromGallery(){
        //
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera(){
        //
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);

    }

    private boolean checkStoragePermission(){

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);

    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    //Permission Result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){

                        pickFromCamera();
                    }
                    else {
                        //MakeInformation
                        Toast.makeText(this, "Camera & storage permissions are required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        pickFromGallery();
                    }
                    else {
                        //MakeInformation
                        Toast.makeText(this, "Storage is required", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //ImagePickResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                //fromGaleeery

                //saveImage
                image_uri = data.getData();

                //setImage
                produkIconIv.setImageURI(image_uri);
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}