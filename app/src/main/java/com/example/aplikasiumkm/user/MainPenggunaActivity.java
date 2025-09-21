package com.example.aplikasiumkm.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.aplikasiumkm.Constants;
import com.example.aplikasiumkm.LoginActivity;
import com.example.aplikasiumkm.PencaharianPenggunaActivity;
import com.example.aplikasiumkm.R;
import com.example.aplikasiumkm.SettingActivity;
import com.example.aplikasiumkm.TokoDetailActivity;
import com.example.aplikasiumkm.adapter.AdapterPesananPengguna;
import com.example.aplikasiumkm.adapter.AdapterProduk;
import com.example.aplikasiumkm.adapter.AdapterProdukPengguna;
import com.example.aplikasiumkm.adapter.AdapterProdukPenjual;
import com.example.aplikasiumkm.adapter.AdapterToko;
import com.example.aplikasiumkm.models.ModelPesananPengguna;
import com.example.aplikasiumkm.models.ModelProduk;
import com.example.aplikasiumkm.models.ModelToko;
import com.example.aplikasiumkm.penjual.MainPenjualActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class MainPenggunaActivity extends AppCompatActivity {

    private TextView nameTv, statusTv, emailTv, phoneTv, tabTokoTv, tabPesananTv, tabProdukTv, filterProdukTv;
    private ImageButton btn_logout, btn_editProfile, btn_settting, categoryIbtn;
    private ImageView profileTv;
    private RelativeLayout TokoRl, PesananRl, ProdukRl;
    private RecyclerView tokoRv, PesananRview, produkRv;
    private String tokoUid;
//    private ViewFlipper viewFliper;
    private EditText serachProdukEt;
//    private String produkId;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelToko> tokolist;
    private AdapterToko adapterToko;

    private ArrayList<ModelProduk> produkArrayListList;
    private AdapterProduk adapterProduk;

    private ArrayList<ModelPesananPengguna> PesananPenggunaList;
    private AdapterPesananPengguna adapterPesananPengguna;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_pengguna);

        nameTv = findViewById(R.id.nameTv);
        statusTv = findViewById(R.id.statusTv);
        btn_logout = findViewById(R.id.btn_logout);
        btn_editProfile = findViewById(R.id.btn_editProfile);
        emailTv = findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);
//        tabTokoTv = findViewById(R.id.tabTokoTv);
        tabPesananTv = findViewById(R.id.tabPesananTv);
        tabProdukTv = findViewById(R.id.tabProdukTv);
        profileTv = findViewById(R.id.profileTv);
//        TokoRl = findViewById(R.id.TokoRl);
        PesananRl = findViewById(R.id.PesananRl);
        tokoRv = findViewById(R.id.tokoRv);
        PesananRview = findViewById(R.id.PesananRview);
        btn_settting = findViewById(R.id.btn_settting);
        ProdukRl = findViewById(R.id.ProdukRl);
        produkRv = findViewById(R.id.produkRv);
        serachProdukEt = findViewById(R.id.serachProdukEt);
        categoryIbtn = findViewById(R.id.categoryIbtn);
        filterProdukTv = findViewById(R.id.filterProdukTv);
//        viewFliper = findViewById(R.id.viewFliper);
        tokoUid = getIntent().getStringExtra("tokoUid");
//        produkId = getIntent().getStringExtra("produkId");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        cekUser();

//        int images[] = {R.drawable.strategipromosi, R.drawable.cara, R.drawable.pomosipenjualan};
//
//        for (int image : images){
//            fliperImage(image);
//        }
//        menampilkanToko();







        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                membuatOffline();
            }
        });

        btn_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //EditProfile
                startActivity(new Intent(MainPenggunaActivity.this, EditProfilePenggunaActivity.class));
                //finish();

            }
        });
        btn_settting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainPenggunaActivity.this, SettingActivity.class);
                startActivity(intent);

            }
        });
        tabPesananTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPesanan();
            }
        });
//        tabTokoTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                menampilkanToko();
//
//
//            }
//        });
        tabProdukTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MainPenggunaActivity.this, PencaharianPenggunaActivity.class);
//                startActivity(intent);
                menampilkanProduk();
            }
        });

        serachProdukEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try {
                    adapterProduk.getFilter().filter(charSequence);

                }catch (Exception e){
                    e.printStackTrace();

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        categoryIbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainPenggunaActivity.this);
                builder.setTitle("Pilih Kategori:").setItems(Constants.pilihan1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String memilih = Constants.pilihan1[i];
                        filterProdukTv.setText(memilih);
                        if (memilih.equals("Semua")){
                            menampilkanproduk(); //Ceklagi
                        }else {
                            adapterProduk.getFilter().filter(memilih);
                        }

                    }
                }).show();
            }

        });
    }

//    private void fliperImage(int image) {
//        ImageView imageView = new ImageView(this);
//        imageView.setBackgroundResource(image);
//
//        viewFliper.addView(imageView);
//        viewFliper.setFlipInterval(4000);
//        viewFliper.setAutoStart(true);
//        viewFliper.setInAnimation(this, android.R.anim.slide_in_left);
//        viewFliper.setOutAnimation(this, android.R.anim.slide_out_right);
//    }

    private void menampilkanproduk() {
        produkArrayListList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produkArrayListList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String uid = "" + dataSnapshot.getRef().getKey();

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Produk");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (dataSnapshot.exists()){
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    ModelProduk modelProduk = dataSnapshot.getValue(ModelProduk.class);

                                    produkArrayListList.add(modelProduk);
                                }

                                adapterProduk = new AdapterProduk(MainPenggunaActivity.this, produkArrayListList);

                                produkRv.setAdapter(adapterProduk);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void menampilkanToko() {

    }
    private void menampilkanProduk() {
//        TokoRl.setVisibility(View.GONE);
        ProdukRl.setVisibility(View.VISIBLE);
        PesananRl.setVisibility(View.GONE);

//        tabTokoTv.setTextColor(getResources().getColor(R.color.white));
//        tabTokoTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabPesananTv.setTextColor(getResources().getColor(R.color.white));
        tabPesananTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabProdukTv.setTextColor(getResources().getColor(R.color.black));
        tabProdukTv.setBackgroundResource(R.drawable.shape_tab1);

    }


//    private void menampilkanToko() {
//        TokoRl.setVisibility(View.VISIBLE);
//        PesananRl.setVisibility(View.GONE);
//        ProdukRl.setVisibility(View.GONE);
//
//        tabTokoTv.setTextColor(getResources().getColor(R.color.black));
//        tabTokoTv.setBackgroundResource(R.drawable.shape_tab1);
//
//        tabPesananTv.setTextColor(getResources().getColor(R.color.white));
//        tabPesananTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
//
//        tabProdukTv.setTextColor(getResources().getColor(R.color.white));
//        tabProdukTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
//
//    }
    private void showPesanan() {
//        TokoRl.setVisibility(View.GONE);
        ProdukRl.setVisibility(View.GONE);
        PesananRl.setVisibility(View.VISIBLE);

//        tabTokoTv.setTextColor(getResources().getColor(R.color.white));
//        tabTokoTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

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
                Toast.makeText(MainPenggunaActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void cekUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null){
            startActivity(new Intent(MainPenggunaActivity.this, LoginActivity.class));
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
                    String phone = "" + dataSnapshot.child("phone").getValue();
                    String email = "" + dataSnapshot.child("email").getValue();
                    String city = "" + dataSnapshot.child("city").getValue();
                    String profileImage = ""+dataSnapshot.child("profileImage").getValue();

                    nameTv.setText(NamaLengkap);
                    statusTv.setText(accountType);
                    phoneTv.setText(phone);
                    emailTv.setText(email);

                    try {
                        Picasso.get().load(profileImage).placeholder(R.drawable.ic_baseline_person).into(profileTv);

                    }catch (Exception e){

                    }

                    loadToko(city);
                    loadProduk();
                    loadPesanan();
//                    loadDataPenjual();





                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void loadPesanan() {
        PesananPenggunaList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PesananPenggunaList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String uid = "" + dataSnapshot.getRef().getKey();

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Pesanan");
                    databaseReference.orderByChild("pesananBy").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (dataSnapshot.exists()){
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                     ModelPesananPengguna modelPesananPengguna = dataSnapshot.getValue(ModelPesananPengguna.class);

                                     PesananPenggunaList.add(modelPesananPengguna);
                                }

                                adapterPesananPengguna = new AdapterPesananPengguna(MainPenggunaActivity.this, PesananPenggunaList);

                                PesananRview.setAdapter(adapterPesananPengguna);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
//    private void loadDataPenjual() {
//        produkArrayListList = new ArrayList<>();
//        //getData
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot ds:snapshot.getChildren()){
//                    ModelToko modto=ds.getValue(ModelToko.class);
//                    assert modto != null;
//                    if (modto.getAccountType().equals("Penjual")){
//                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users")
//                                .child(modto.getUid())
//                                .child("Produk");
//                        ref.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                produkArrayListList.clear();
//                                for (DataSnapshot ds:snapshot.getChildren()){
//                                    ModelProduk mpro=ds.getValue(ModelProduk.class);
//                                    produkArrayListList.add(mpro);
//                                }
//                                //setUPAdapter
//                                adapterProduk = new AdapterProduk(MainPenggunaActivity.this, produkArrayListList);
//                                produkRv.setAdapter(adapterProduk);
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }

//    private void loadProduk(String city) {
//        produkArrayListList = new ArrayList<>();
//        //getData
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//        reference.orderByChild("accountType").equalTo("Penjual")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        produkArrayListList.clear();
//                        for (DataSnapshot ds : snapshot.getChildren()){
//                            ModelProduk modelProduk = ds.getValue(ModelProduk.class);
//                            String produk = "" + ds.child("Produk").getValue();
//
//                            if (produk.equals(city)){
//                                produkArrayListList.add(modelProduk);
//                            }
//                        }
//                        //setUPAdapter
//                        adapterProduk = new AdapterProduk(MainPenggunaActivity.this, produkArrayListList);
//                        produkRv.setAdapter(adapterProduk);
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }

    private void loadToko(String city) {
        tokolist = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("accountType").equalTo("Penjual").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                tokolist.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ModelToko modelToko = dataSnapshot.getValue(ModelToko.class);

                    String kotaToko = ""+dataSnapshot.child("city").getValue();

                    if (kotaToko.equals(city)){
                        tokolist.add(modelToko);
                    }
                    //SetUpAdapater
                    adapterToko = new AdapterToko(MainPenggunaActivity.this, tokolist);
                    tokoRv.setAdapter(adapterToko);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadProduk() {
        produkArrayListList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produkArrayListList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String uid = "" + dataSnapshot.getRef().getKey();

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Produk");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (dataSnapshot.exists()){
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    ModelProduk modelProduk = dataSnapshot.getValue(ModelProduk.class);

                                    produkArrayListList.add(modelProduk);
                                }

                                adapterProduk = new AdapterProduk(MainPenggunaActivity.this, produkArrayListList);

                                produkRv.setAdapter(adapterProduk);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

//    private void loadToko(String city) {
//        produkArrayListList = new ArrayList<>();
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//        reference.orderByChild("accountType").equalTo("Penjual").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                produkArrayListList.clear();
//                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
//                    ModelProduk modelProduk = dataSnapshot.getValue(ModelProduk.class);
//
//                    String kotaToko = ""+dataSnapshot.child("city").getValue();
//
//                    if (kotaToko.equals(city)){
//                        produkArrayListList.add(modelProduk);
//                    }
//                    //SetUpAdapater
//                    adapterProduk = new AdapterProduk(MainPenggunaActivity.this, produkArrayListList);
//                    tokoRv.setAdapter(adapterProduk);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
}