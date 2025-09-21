package com.example.aplikasiumkm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class LupaPasswordActivity extends AppCompatActivity {

    //UI Views
    ImageButton back_btn;
    EditText EmailEt;
    Button send_btn;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupa_password);

        back_btn = findViewById(R.id.back_btn);
        EmailEt = findViewById(R.id.EmailEt);
        send_btn = findViewById(R.id.send_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetUlangPassword();

            }
        });
    }

    private String email;
    private void SetUlangPassword() {

        email = EmailEt.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Email tidak sesuai...", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Kirim untuk mendapatkan password yang baru..");
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //sukses kirim password ulang
                progressDialog.dismiss();
                Toast.makeText(LupaPasswordActivity.this, "Periksa email anda...", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LupaPasswordActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}