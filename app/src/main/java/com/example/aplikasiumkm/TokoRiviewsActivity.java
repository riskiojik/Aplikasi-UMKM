package com.example.aplikasiumkm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.aplikasiumkm.adapter.AdapterRiviewsToko;
import com.example.aplikasiumkm.models.ModelRiviewsToko;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TokoRiviewsActivity extends AppCompatActivity {

    //UI Views
    private ImageButton back_btn;
    private ImageView profileTokoIv;
    private TextView namaTokoTv, ratingTv;
    private RatingBar rtgBar;
    private RecyclerView reviewsRv;

    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelRiviewsToko> modelRiviewsTokoArrayList;
    private AdapterRiviewsToko adapterRiviewsToko;


    private String tokoUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toko_riviews);

        back_btn = findViewById(R.id.back_btn);
        profileTokoIv = findViewById(R.id.profileTokoIv);
        namaTokoTv = findViewById(R.id.namaTokoTv);
        ratingTv = findViewById(R.id.ratingTv);
        rtgBar = findViewById(R.id.rtgBar);
        reviewsRv = findViewById(R.id.reviewsRv);

        tokoUid = getIntent().getStringExtra("tokoUid");

        firebaseAuth = FirebaseAuth.getInstance();
        loadTokoDetail();
        loadReview();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }



    private float ratingsum = 0;
    private void loadReview() {
        //initList
        modelRiviewsTokoArrayList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(tokoUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelRiviewsTokoArrayList.clear();
                ratingsum = 0;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    float rating = Float.parseFloat(""+ dataSnapshot.child("ratings").getValue());
                    ratingsum = ratingsum +rating;

                    ModelRiviewsToko modelRiviewsToko = dataSnapshot.getValue(ModelRiviewsToko.class);
                    modelRiviewsTokoArrayList.add(modelRiviewsToko);

                }
                //setUpAdapter
                adapterRiviewsToko = new AdapterRiviewsToko(TokoRiviewsActivity.this, modelRiviewsTokoArrayList);
                //setUp RecyclerView
                reviewsRv.setAdapter(adapterRiviewsToko);

                long nomerReviews = snapshot.getChildrenCount();
                float avgRating = ratingsum/nomerReviews;

                ratingTv.setText(String.format("%.2f", avgRating)+"[" + nomerReviews+"]");
                rtgBar.setRating(avgRating);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void loadTokoDetail() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(tokoUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String NamaToko = ""+ snapshot.child("NamaToko").getValue();
                String profileImage = ""+ snapshot.child("profileImage").getValue();

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
}