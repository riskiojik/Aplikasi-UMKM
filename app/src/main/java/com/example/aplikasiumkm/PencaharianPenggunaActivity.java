package com.example.aplikasiumkm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.aplikasiumkm.adapter.AdapterProduk;
import com.example.aplikasiumkm.adapter.AdapterToko;
import com.example.aplikasiumkm.models.ModelProduk;
import com.example.aplikasiumkm.models.ModelToko;
import com.example.aplikasiumkm.user.MainPenggunaActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PencaharianPenggunaActivity extends AppCompatActivity {

    private ArrayList<ModelProduk> produkArrayListList;
    private AdapterProduk adapterProduk;

    private FirebaseAuth firebaseAuth;
    private RecyclerView produkRv;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pencaharian_pengguna);
        produkRv = findViewById(R.id.produkRv);

        firebaseAuth =FirebaseAuth.getInstance();


        loadProduk();


    }

    private void loadProduk() {
        produkArrayListList = new ArrayList<>();
        //getData
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Produk")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        produkArrayListList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()){

                            ModelProduk modelProduk = ds.getValue(ModelProduk.class);
                            produkArrayListList.add(modelProduk);
                        }
                        //setUPAdapter
                        adapterProduk = new AdapterProduk(PencaharianPenggunaActivity.this, produkArrayListList);
                        produkRv.setAdapter(adapterProduk);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}