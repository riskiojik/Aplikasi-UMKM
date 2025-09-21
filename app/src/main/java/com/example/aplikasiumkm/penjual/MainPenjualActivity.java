package com.example.aplikasiumkm.penjual;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aplikasiumkm.ChatActivity;
import com.example.aplikasiumkm.Constants;
import com.example.aplikasiumkm.DirectMessageWApenjual;
import com.example.aplikasiumkm.LoginActivity;
import com.example.aplikasiumkm.PenjualChatActivity;
import com.example.aplikasiumkm.R;
import com.example.aplikasiumkm.SettingActivity;
import com.example.aplikasiumkm.TambahProdukActivity;
import com.example.aplikasiumkm.TokoRiviewsActivity;
import com.example.aplikasiumkm.adapter.AdapterPesananToko;
import com.example.aplikasiumkm.adapter.AdapterProdukPenjual;
import com.example.aplikasiumkm.models.ModelPesananToko;
import com.example.aplikasiumkm.models.ModelProduk;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MainPenjualActivity extends AppCompatActivity {

    private TextView nameTv, statusTv, namaTokoTv, emailTv, tabProdukTv, tabPesananTv, filterProdukTv, filterPesananTv;
    private EditText serachProdukEt;
    private ImageButton btn_logout, btn_editProfile, addProdukBtn, categoryIbtn, categoryPsnBtn, reviewIbtn, btn_settting, pesanIb;
    private ImageView profileTv;
    private RelativeLayout ProdukRl, PesananRl;
    private RecyclerView showProdukRv, pesananRv;

    private BottomNavigationView bottom_navigation;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelProduk> produkList;
    private AdapterProdukPenjual adapterProdukPenjual;

    private ArrayList<ModelPesananToko> PesananTokoArrayList;
    private AdapterPesananToko adapterPesananToko;

    private String tokoUid;
    private String timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_penjual);

        nameTv = findViewById(R.id.nameTv);
        statusTv = findViewById(R.id.statusTv);
        namaTokoTv = findViewById(R.id.namaTokoTv);
        emailTv = findViewById(R.id.emailTv);
        tabProdukTv = findViewById(R.id.tabProdukTv);
        tabPesananTv = findViewById(R.id.tabPesananTv);
        profileTv = findViewById(R.id.profileTv);
        btn_logout = findViewById(R.id.btn_logout);
//        addProdukBtn = findViewById(R.id.addProdukBtn);
//        btn_editProfile = findViewById(R.id.btn_editProfile);
        ProdukRl = findViewById(R.id.ProdukRl);
        PesananRl = findViewById(R.id.PesananRl);
        serachProdukEt = findViewById(R.id.serachProdukEt);
        categoryIbtn = findViewById(R.id.categoryIbtn);
        filterProdukTv = findViewById(R.id.filterProdukTv);
        showProdukRv = findViewById(R.id.showProdukRv);
        filterPesananTv = findViewById(R.id.filterPesananTv);
        categoryPsnBtn = findViewById(R.id.categoryPsnBtn);
        pesananRv = findViewById(R.id.pesananRv);
//        reviewIbtn = findViewById(R.id.reviewIbtn);
        btn_settting = findViewById(R.id.btn_settting);
//        pesanIb = findViewById(R.id.pesanIb);
        bottom_navigation = findViewById(R.id.bottom_navigation);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        tokoUid = getIntent().getStringExtra("uid");
        timestamp = ""+System.currentTimeMillis();

        firebaseAuth = FirebaseAuth.getInstance();
        cekUser();
        loadProduk();
        loadPesanan();

        showProduk();

        bottom_navigation.setSelectedItemId(R.id.dahsboard);

        bottom_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.dahsboard:
                        return true;

                    case R.id.pesanIb:
                        Intent intent1 = new Intent(getApplicationContext(), DirectMessageWApenjual.class);
//                        intent1.putExtra("uid", tokoUid);
                        startActivity(intent1);
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.addProdukBtn:
                        startActivity(new Intent(getApplicationContext(), TambahProdukActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.btn_editProfile:
                        startActivity(new Intent(getApplicationContext(),EditProfilePenjualActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.reviewIbtn:
                        Intent intent = new Intent(getApplicationContext(),TokoRiviewsActivity.class);
                        intent.putExtra("tokoUid", ""+firebaseAuth.getUid());
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                membuatOffline();
            }
        });

        btn_settting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainPenjualActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

//        btn_editProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //EditProfile
//                startActivity(new Intent(MainPenjualActivity.this, EditProfilePenjualActivity.class));
//
//            }
//        });

//        addProdukBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Tambah Prooduk
//                startActivity(new Intent(MainPenjualActivity.this, TambahProdukActivity.class));
//            }
//        });
        tabProdukTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //MemanggilProduk
                showProduk();
            }
        });
        tabPesananTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //MemanggilPesanan
                showPesanan();

            }
        });
        categoryIbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainPenjualActivity.this);
                builder.setTitle("Pilih Kategori:").setItems(Constants.pilihan1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String memilih = Constants.pilihan1[i];
                        filterProdukTv.setText(memilih);
                        if (memilih.equals("Semua")){
                            loadProduk();
                        }else {
                            loadfilterProduk(memilih);
                        }

                    }
                }).show();
            }
        });

        categoryPsnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String [] option = {"Semua", "Sedang di proses..","Dikemas","Dikirim","Berhasil", "Gagal" };

                AlertDialog.Builder builder = new AlertDialog.Builder(MainPenjualActivity.this);
                builder.setTitle("Status Pesanan").setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0){
                            filterPesananTv.setText("Menampilkan Semua Pesanan");
                            adapterPesananToko.getFilter().filter("");
                        }else {
                            String optionKlik = option[i];
                            filterPesananTv.setText("Menampilkan "+ optionKlik+" Pesanan");
                            adapterPesananToko.getFilter().filter(optionKlik);

                        }
                    }
                }).show();

            }
        });

        //Search
        serachProdukEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try {
                    adapterProdukPenjual.getFilter().filter(charSequence);

                }catch (Exception e){
                    e.printStackTrace();

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//        reviewIbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainPenjualActivity.this, TokoRiviewsActivity.class);
//                intent.putExtra("tokoUid", ""+firebaseAuth.getUid());
//                startActivity(intent);
//
//            }
//        });
    }

    private void loadPesanan() {
        PesananTokoArrayList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Pesanan").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PesananTokoArrayList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ModelPesananToko modelPesananToko = dataSnapshot.getValue(ModelPesananToko.class);

                    PesananTokoArrayList.add(modelPesananToko);
                }
                //setAdapter
                adapterPesananToko = new AdapterPesananToko(MainPenjualActivity.this, PesananTokoArrayList);
                //setAdapterToRecyclerview
                pesananRv.setAdapter(adapterPesananToko);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadfilterProduk(String memilih) {
        produkList = new ArrayList<>();
        //getData
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Produk")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        produkList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()){
                            String categoryProduk = ""+ds.child("categoryProduk").getValue();
                            if (memilih.equals(categoryProduk)){
                                ModelProduk modelProduk = ds.getValue(ModelProduk.class);
                                produkList.add(modelProduk);
                            }
                        }
                        //setUPAdapter
                        adapterProdukPenjual = new AdapterProdukPenjual(MainPenjualActivity.this, produkList);
                        showProdukRv.setAdapter(adapterProdukPenjual);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }

    private void loadProduk() {
        produkList = new ArrayList<>();
        //getData
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Produk")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produkList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){

                    ModelProduk modelProduk = ds.getValue(ModelProduk.class);
                    produkList.add(modelProduk);
                }
                //setUPAdapter
                adapterProdukPenjual = new AdapterProdukPenjual(MainPenjualActivity.this, produkList);
                showProdukRv.setAdapter(adapterProdukPenjual);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showProduk() {
        ProdukRl.setVisibility(View.VISIBLE);
        PesananRl.setVisibility(View.GONE);

        tabProdukTv.setTextColor(getResources().getColor(R.color.black));
        tabProdukTv.setBackgroundResource(R.drawable.shape_tab1);

        tabPesananTv.setTextColor(getResources().getColor(R.color.white));
        tabPesananTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }

    private void showPesanan() {
        PesananRl.setVisibility(View.VISIBLE);
        ProdukRl.setVisibility(View.GONE);

        tabProdukTv.setTextColor(getResources().getColor(R.color.white));
        tabProdukTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabPesananTv.setTextColor(getResources().getColor(R.color.black));
        tabPesananTv.setBackgroundResource(R.drawable.shape_tab1);

    }

    private void membuatOffline() {
        //setalhdaftarkemudianOnline
        progressDialog.setMessage("Keluar...");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "false");

        //Update to database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseAuth.signOut();
                cekUser();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Gagal
                progressDialog.dismiss();
                Toast.makeText(MainPenjualActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void cekUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null){
            startActivity(new Intent(MainPenjualActivity.this, LoginActivity.class));
            finish();

        }else {

            cekInfoUser();
        }
    }

    private void cekInfoUser() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("uid").equalTo(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String NamaLengkap = "" + dataSnapshot.child("NamaLengkap").getValue();
                    String accountType = "" + dataSnapshot.child("accountType").getValue();
                    String email = "" + dataSnapshot.child("email").getValue();
                    String NamaToko = "" + dataSnapshot.child("NamaToko").getValue();
                    String profileImage = "" + dataSnapshot.child("profileImage").getValue();

                    nameTv.setText(NamaLengkap);
                    statusTv.setText(accountType);
                    emailTv.setText(email);
                    namaTokoTv.setText(NamaToko);



                    try {
                        Picasso.get().load(profileImage).placeholder(R.drawable.ic_baseline_storefront_grey).into(profileTv);
                    }
                    catch (Exception e){
                        profileTv.setImageResource(R.drawable.ic_baseline_storefront_grey);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}