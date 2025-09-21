package com.example.aplikasiumkm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.aplikasiumkm.adapter.AdapterCartItem;
import com.example.aplikasiumkm.adapter.AdapterProdukPengguna;
import com.example.aplikasiumkm.adapter.AdapterRiviewsToko;
import com.example.aplikasiumkm.models.ModelCartItems;
import com.example.aplikasiumkm.models.ModelProduk;
import com.example.aplikasiumkm.models.ModelRiviewsToko;
import com.example.aplikasiumkm.user.DetailPesananPenggunaActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class TokoDetailActivity extends AppCompatActivity {

    private ImageView TokoIv;
    private TextView namaTokoTv, phoneTv, emailTv, tokoTutupTv, DelieveryEt
            , addressTv, filterProdukTv, jumlahCartTv;
    private ImageButton TeleponIb, mapIb , pesanIb, back_btn, btn_cart, categoryIbtn, btn_Reviews;
    private EditText serachProdukEt;
    private RecyclerView showProdukRv;
    public String DeliveryFee;
    private RatingBar ratingBar;


    private String tokoUid;
    private String tokoDetailUid;
    private String myLatitude, myLongitude, myphone;
    private String NamaToko, email, phone, address, tokoLatitude, tokoLongitude;
    private FirebaseAuth firebaseAuth;


    private ArrayList<ModelProduk> produkList;
    private AdapterProdukPengguna adapterProdukPengguna;

    private ArrayList<ModelCartItems> cartItems;
    private AdapterCartItem adapterCartItem;

    private EasyDB easyDB;

    //progressDialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toko_detail);

        TokoIv = findViewById(R.id.TokoIv);
        namaTokoTv = findViewById(R.id.namaTokoTv);
        phoneTv = findViewById(R.id.phoneTv);
        emailTv = findViewById(R.id.emailTv);
        tokoTutupTv = findViewById(R.id.tokoTutupTv);
        DelieveryEt = findViewById(R.id.DelieveryEt);
        addressTv = findViewById(R.id.addressTv);
        filterProdukTv = findViewById(R.id.filterProdukTv);
        TeleponIb = findViewById(R.id.TeleponIb);
        mapIb = findViewById(R.id.mapIb);
        pesanIb = findViewById(R.id.pesanIb);
        back_btn = findViewById(R.id.back_btn);
        btn_cart = findViewById(R.id.btn_cart);
        btn_Reviews = findViewById(R.id.btn_Reviews);
        categoryIbtn = findViewById(R.id.categoryIbtn);
        serachProdukEt = findViewById(R.id.serachProdukEt);
        showProdukRv = findViewById(R.id.showProdukRv);
        jumlahCartTv = findViewById(R.id.jumlahCartTv);
        ratingBar = findViewById(R.id.ratingBar);
        tokoUid = getIntent().getStringExtra("produkId");
//        tokoDetailUid = getIntent().getStringExtra("tokoUid");
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Please wait...");
                progressDialog.setCanceledOnTouchOutside(false);

        //getUiddariInten
        cekInfoUser();
//        tokoDetail();
        cekDetailToko();

        menampilkanToko();
        loadReview();

        easyDB =EasyDB.init(this, "ITEMS_DB").setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Harga", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_TotalHarga", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .doneTableColumn();

        MenghapusDataCart();
        jumlahCart();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MenampilkanPesanan();

            }
        });

        btn_Reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TokoDetailActivity.this, TokoRiviewsActivity.class);
                intent.putExtra("tokoUid", tokoUid);
                startActivity(intent);

            }
        });

        TeleponIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aksesTelpon();

            }
        });
        mapIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMap();

            }
        });
        pesanIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent intent = new Intent(TokoDetailActivity.this, ChatActivity.class);
//                intent.putExtra("tokoUid", tokoUid);
//                startActivity(intent);
                Intent intent = new Intent(TokoDetailActivity.this, DirectWhatsApp.class);
                startActivity(intent);
            }
        });

        serachProdukEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try {
                    adapterProdukPengguna.getFilter().filter(charSequence);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(TokoDetailActivity.this);
                builder.setTitle("Pilih Kategori:").setItems(Constants.pilihan1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String memilih = Constants.pilihan1[i];
                        filterProdukTv.setText(memilih);
                        if (memilih.equals("Semua")){
                            menampilkanToko(); //Ceklagi
                        }else {
                            adapterProdukPengguna.getFilter().filter(memilih);
                        }

                    }
                }).show();
            }
        });


    }

    private void tokoDetail() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(tokoDetailUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String NamaLengkap = ""+snapshot.child("NamaLengkap").getValue();
                NamaToko = ""+snapshot.child("NamaToko").getValue();
                email = ""+snapshot.child("email").getValue();
                phone = ""+snapshot.child("phone").getValue();
                tokoLatitude = ""+snapshot.child("latitude").getValue();
                address = ""+snapshot.child("address").getValue();
                tokoLongitude = ""+snapshot.child("longitude").getValue();
                DeliveryFee = ""+snapshot.child("DeliveryFee").getValue(); //Cek Lagi
                String profileImage = ""+snapshot.child("profileImage").getValue();
                String tokoBuka = ""+snapshot.child("tokoBuka").getValue();

                //setUpData
                namaTokoTv.setText(NamaToko);
                emailTv.setText(email);
                phoneTv.setText(phone);
                DelieveryEt.setText("Pengiriman "+DeliveryFee);
                addressTv.setText(address);

                if (tokoBuka.equals("true")){
                    tokoTutupTv.setText("Buka");
                }else {
                    tokoTutupTv.setText("Tutup");
                }

                try {
                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_baseline_storefront).into(TokoIv);


                }catch (Exception e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private float ratingsum = 0;
    private void loadReview() {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(tokoUid).child("Ratings").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ratingsum = 0;
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        float rating = Float.parseFloat(""+ dataSnapshot.child("ratings").getValue());
                        ratingsum = ratingsum +rating;
                    }

                    long nomerReviews = snapshot.getChildrenCount();
                    float avgRating = ratingsum/nomerReviews;

                    ratingBar.setRating(avgRating);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


    }

    private void MenghapusDataCart() {
        easyDB.deleteAllDataFromTable();
    }
    public void jumlahCart(){
        int jumlah = easyDB.getAllData().getCount();
        if (jumlah<=0){
            jumlahCartTv.setVisibility(View.GONE);
        }else {
            jumlahCartTv.setVisibility(View.VISIBLE);
            jumlahCartTv.setText("" + jumlah);
        }

    }


    public int TotalHarga = 0;
    public TextView sTotalTv, sDeliveryTv, TotalHargaTv;
//    private static final int LOCATION_REQUEST_CODE = 100;
//    private LocationManager locationManager;
//    private String[] locationPermissions;
//
//    private double mylatitude = 0.0;
//    private double mylongitude = 0.0;
    private void MenampilkanPesanan() {

//        locationPermissions =  new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        cartItems = new ArrayList<>();
    //inflateLayout
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart_order, null);


        TextView namaTokoTv =view.findViewById(R.id.namaTokoTv);
//        TextView UpdatLokasi =view.findViewById(R.id.UpdatLokasi);
        sTotalTv =view.findViewById(R.id.sTotalTv);
        TotalHargaTv = view.findViewById(R.id.TotalHargaTv);
        sDeliveryTv = view.findViewById(R.id.sDeliveryTv);
        RecyclerView cartItemRv = view.findViewById(R.id.cartItemRv);
        Button KonfirmasiPesanan = view.findViewById(R.id.KonfirmasiPesanan);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        namaTokoTv.setText(NamaToko);

        EasyDB easyDB =EasyDB.init(this, "ITEMS_DB").setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Harga", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_TotalHarga", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .doneTableColumn();

        Cursor cursor = easyDB.getAllData();
        while (cursor.moveToNext()){
//            String ID = cursor.getString(0);
            String id = cursor.getString(1);
            String pId = cursor.getString(2);
            String produkTitle = cursor.getString(3);
            String harga = cursor.getString(4);
            String totalHarga = cursor.getString(5);
            String quantity = cursor.getString(6);

            ModelCartItems modelCartItems = new ModelCartItems(
                      "" + id
                    , "" + pId
                    , "" + produkTitle
                    , "" + harga
                    , "" + totalHarga
                    , "" + quantity);

            TotalHarga = TotalHarga + Integer.parseInt((totalHarga));

            cartItems.add(modelCartItems);
        }
        //setAdapater
        adapterCartItem = new AdapterCartItem(this, cartItems);
        //settoRV
        cartItemRv.setAdapter(adapterCartItem);
        sDeliveryTv.setText("" +DeliveryFee);
        sTotalTv.setText("Rp. " + TotalHarga);
        TotalHargaTv.setText("Rp. " +(TotalHarga + Integer.parseInt(DeliveryFee.replace("Rp.", "").trim())));

            //showDialog
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    TotalHarga = 0;
                }
            });

//        UpdatLokasi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!checkLocationPermision()){
//                    detectLocation();
//
//                }else {
//                    requestLocationPermission();
//                }
//            }
//
//        });
//




        KonfirmasiPesanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Validasi Address
//                if (mylongitude == 0.0 || mylatitude == 0.0  || mylongitude == 0.0  || myongitude == 0.0 ){
//                    Toast.makeText(TokoDetailActivity.this, "Lengkapi alamat anda di profil terlebih dahulu..", Toast.LENGTH_SHORT).show();
//                    return;

                if (myLatitude.equals("") || myLatitude.equals("null") || myLongitude.equals("") || myLongitude.equals("null")){
                        Toast.makeText(TokoDetailActivity.this, "Lengkapi alamat anda di profil terlebih dahulu..", Toast.LENGTH_SHORT).show();
                        return;

                }
                if (myphone.equals("") || myphone.equals("null")){
                    Toast.makeText(TokoDetailActivity.this, "Lengkapi Nomer Hp anda di profil terlebih dahulu..", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cartItems.size() == 0){
                    Toast.makeText(TokoDetailActivity.this, "Tidak ada pesanan", Toast.LENGTH_SHORT).show();
                    return;
                }
                submitOrder();

            }
        });
    }

//    private void requestLocationPermission() {
//        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_REQUEST_CODE);
//
//    }
//
//    private void detectLocation() {
//        Toast.makeText(this, "Sedang mencari lokasi...", Toast.LENGTH_LONG).show();
//
//        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, (LocationListener) this);
//
//    }
//
//    private boolean checkLocationPermision() {
//        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == (PackageManager.PERMISSION_GRANTED);
//        return result;
//
//    }

    private void submitOrder() {
        progressDialog.setMessage("Placing pesanan");
        progressDialog.show();

        final String timestamp = ""+ System.currentTimeMillis();

        String totalHarga = TotalHargaTv.getText().toString().trim().replace("Rp. ", ""); //Ceklagi

        //SetUp Order Data
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("pesananId", ""+ timestamp);
        hashMap.put("pesananTime", ""+ timestamp);
        hashMap.put("pesananStatus", "Sedang di proses..");// Sedang di proses/ Berhasil/ Gagal
        hashMap.put("pesanantotalHarga", ""+ totalHarga);
        hashMap.put("pesananBy", ""+ firebaseAuth.getUid());
        hashMap.put("pesananTo", "" +tokoUid);
        hashMap.put("DeliveryFee", "" +DeliveryFee);
        hashMap.put("latitude", "" +myLatitude);
        hashMap.put("longitude", "" +myLongitude);

        //Addto database
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(tokoUid).child("Pesanan");
        databaseReference.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        for (int i = 0; i<cartItems.size(); i++){
                            String pId = cartItems.get(i).getpId();
                            String id = cartItems.get(i).getId();
                            String produkTitle = cartItems.get(i).getNama();
                            String harga = cartItems.get(i).getPrice();
                            String totalHarga = cartItems.get(i).getCost();
                            String quantity = cartItems.get(i).getQuantity();

                            HashMap<String, String> hashMap1 =  new HashMap<>(); //setUpData ModelItemPesanan
                            hashMap1.put("pId", pId);
                            hashMap1.put("produkTitle",produkTitle );
                            hashMap1.put("harga", harga);
                            hashMap1.put("totalHarga", totalHarga);
                            hashMap1.put("quantity", quantity);


                            databaseReference.child(timestamp).child("Items").child(pId).setValue(hashMap1);
                        }
                        progressDialog.dismiss();
                        Toast.makeText(TokoDetailActivity.this, "Pesanan berhasil", Toast.LENGTH_SHORT).show();

                        NotificationMessage(timestamp);//CekLagi

                        Intent intent = new Intent(TokoDetailActivity.this, DetailPesananPenggunaActivity.class);
                        intent.putExtra("pesananTo", tokoUid);
                        intent.putExtra("pesananId", timestamp);

                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(TokoDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();


            }
        });

    }


    private void openMap() {
        String address = "https://maps.google.com/maps?saddr=" + myLatitude +","+myLongitude + "&daddr=" + tokoLatitude+","+ tokoLongitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        startActivity(intent );

    }

    private void aksesTelpon() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(phone))));
        Toast.makeText(this, ""+phone, Toast.LENGTH_SHORT).show();



    }


    private void cekInfoUser() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("uid").equalTo(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String NamaLengkap = "" + dataSnapshot.child("NamaLengkap").getValue();
                    String accountType = "" + dataSnapshot.child("accountType").getValue();
                    myphone = "" + dataSnapshot.child("phone").getValue();
                    String address = "" + dataSnapshot.child("address").getValue();
                    String email = "" + dataSnapshot.child("email").getValue();
                    String city = "" + dataSnapshot.child("city").getValue();
                    String profileImage = ""+dataSnapshot.child("profileImage").getValue();
                    myLatitude = ""+dataSnapshot.child("latitude").getValue();
                    myLongitude = ""+dataSnapshot.child("longitude").getValue();


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void cekDetailToko() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(tokoUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String NamaLengkap = ""+snapshot.child("NamaLengkap").getValue();
                NamaToko = ""+snapshot.child("NamaToko").getValue();
                email = ""+snapshot.child("email").getValue();
                phone = ""+snapshot.child("phone").getValue();
                tokoLatitude = ""+snapshot.child("latitude").getValue();
                address = ""+snapshot.child("address").getValue();
                tokoLongitude = ""+snapshot.child("longitude").getValue();
                DeliveryFee = ""+snapshot.child("DeliveryFee").getValue(); //Cek Lagi
                String profileImage = ""+snapshot.child("profileImage").getValue();
                String tokoBuka = ""+snapshot.child("tokoBuka").getValue();

                //setUpData
                namaTokoTv.setText(NamaToko);
                emailTv.setText(email);
                phoneTv.setText(phone);
                DelieveryEt.setText("Pengiriman "+DeliveryFee);
                addressTv.setText(address);

                if (tokoBuka.equals("true")){
                    tokoTutupTv.setText("Buka");
                }else {
                    tokoTutupTv.setText("Tutup");
                }

                try {
                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_baseline_storefront).into(TokoIv);


                }catch (Exception e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void menampilkanToko() {
        produkList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(tokoUid).child("Produk").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produkList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ModelProduk modelProduk= dataSnapshot.getValue(ModelProduk.class);
                    produkList.add(modelProduk);
                }

                adapterProdukPengguna = new AdapterProdukPengguna(TokoDetailActivity.this, produkList);
                showProdukRv.setAdapter(adapterProdukPengguna);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void NotificationMessage(String pesananId){
        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;
        String NOTIFICATION_TITLE = "Pesanan Masuk " + pesananId;
        String NOTIFICATION_MESSAGE = "Selemat..! Pesanan Masuk";
        String NOTIFICATION_TYPE = "PesananMasuk"; //Cek lagi

        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();

        try {
            notificationBodyJo.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJo.put("pembeliUid", firebaseAuth.getUid());
            notificationBodyJo.put("penjualUid", tokoUid);
            notificationBodyJo.put("pesananId", pesananId);
            notificationBodyJo.put("notificationTitle", NOTIFICATION_TITLE); //CekLagi
            notificationBodyJo.put("notificationMessage", NOTIFICATION_MESSAGE);

            notificationJo.put("to", NOTIFICATION_TOPIC);
            notificationJo.put("data", notificationBodyJo);

        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        mengirimNotification(notificationJo, pesananId);
    }

    private void mengirimNotification(JSONObject notificationJo, final String pesananId) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Setelah placing order open details page
                Intent intent = new Intent(TokoDetailActivity.this, DetailPesananPenggunaActivity.class);
                intent.putExtra("pesananTo", tokoUid);
                intent.putExtra("pesananId", pesananId);
                startActivity(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Intent intent = new Intent(TokoDetailActivity.this, DetailPesananPenggunaActivity.class);
                intent.putExtra("pesananTo", tokoUid);
                intent.putExtra("pesananId", pesananId);
                startActivity(intent);


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String > headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key=" + Constants.FCM_KEY);

                return headers;
            }
        };

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

}