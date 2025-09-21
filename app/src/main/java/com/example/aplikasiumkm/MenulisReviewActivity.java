package com.example.aplikasiumkm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

public class MenulisReviewActivity extends AppCompatActivity {

    private  String tokoUid;

    private ImageButton back_btn;
    private ImageView profileTokoIv;
    private TextView namaTokoTv;
    private RatingBar ratingTokoRb;
    private EditText reviewEt;
    private FloatingActionButton done_Btn;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menulis_review);

        //init Views
        back_btn = findViewById(R.id.back_btn);
        profileTokoIv = findViewById(R.id.profileTokoIv);
        namaTokoTv = findViewById(R.id.namaTokoTv);
        ratingTokoRb = findViewById(R.id.ratingTokoRb);
        reviewEt = findViewById(R.id.reviewEt);
        done_Btn = findViewById(R.id.done_Btn);

        tokoUid = getIntent().getStringExtra("tokoUid");
        firebaseAuth = FirebaseAuth.getInstance();
        loadReview();
        loadInfoToko();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        done_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                inputData();
            }
        });
    }

    private void loadInfoToko() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(tokoUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String NamaToko =  ""+ snapshot.child("NamaToko").getValue();
                String profileImage =  ""+ snapshot.child("profileImage").getValue();

                //setUpData
                namaTokoTv.setText(NamaToko);

                try {
                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_baseline_storefront_grey).into(profileTokoIv);

                }catch (Exception e){
                    profileTokoIv.setImageResource(R.drawable.ic_baseline_storefront_grey);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void loadReview() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(tokoUid).child("Ratings").child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    //MEndapatkan detail reviews
                    String uid = ""+ snapshot.child("uid").getValue();
                    String ratings = ""+ snapshot.child("ratings").getValue();
                    String review = ""+ snapshot.child("review").getValue();
                    String timestamp = ""+ snapshot.child("timestamp").getValue();


                    float myRating = Float.parseFloat(ratings);
                    ratingTokoRb.setRating(myRating);
                    reviewEt.setText(review);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void inputData() {
        String ratings = ""+ ratingTokoRb.getRating();
        String review = reviewEt.getText().toString().trim();

        //Time
        String timestamp = ""+ System.currentTimeMillis();
        //SetUpData
        HashMap <String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", "" +firebaseAuth.getUid());
        hashMap.put("ratings", ""+ ratings);
        hashMap.put("review", ""+ review);
        hashMap.put("timestamp", ""+ timestamp);

        //SaveDatabase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(tokoUid).child("Ratings").child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MenulisReviewActivity.this, "Komentar anda berhasil disimpan..", Toast.LENGTH_SHORT).show();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MenulisReviewActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
}