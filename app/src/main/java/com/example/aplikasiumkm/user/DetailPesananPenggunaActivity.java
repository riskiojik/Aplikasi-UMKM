package com.example.aplikasiumkm.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.aplikasiumkm.MenulisReviewActivity;
import com.example.aplikasiumkm.R;
import com.example.aplikasiumkm.adapter.AdapterItemPesanan;
import com.example.aplikasiumkm.models.ModelItemPesanan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DetailPesananPenggunaActivity extends AppCompatActivity {

    private String pesananTo, pesananId;

    private ImageButton back_btn, writeReview_btn;
    private TextView IdPesananTv, DatePesananTv, statusPesananTv, namaTokoTv
            , JumlahPesananTv, TotalHargaTv, AddressTv;
    private RecyclerView ItemsRv;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelItemPesanan> modelItemPesananArrayList;
    private AdapterItemPesanan adapterItemPesanan;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pesanan_pengguna);

        //initViews
        back_btn = findViewById(R.id.back_btn);
        IdPesananTv = findViewById(R.id.IdPesananTv);
        DatePesananTv = findViewById(R.id.DatePesananTv);
        statusPesananTv = findViewById(R.id.statusPesananTv);
        namaTokoTv = findViewById(R.id.namaTokoTv);
        JumlahPesananTv = findViewById(R.id.JumlahPesananTv);
        TotalHargaTv = findViewById(R.id.TotalHargaTv);
        AddressTv = findViewById(R.id.AddressTv);
        ItemsRv = findViewById(R.id.ItemsRv);
        writeReview_btn = findViewById(R.id.writeReview_btn);

        final Intent intent = getIntent();
        pesananTo = intent.getStringExtra("pesananTo");
        pesananId = intent.getStringExtra("pesananId");

        firebaseAuth = FirebaseAuth.getInstance();
        loadTokoInfo();
        loadDetailPesanan();
        loadItemPesanan();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        writeReview_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent1 = new Intent(DetailPesananPenggunaActivity.this, MenulisReviewActivity.class);
                intent1.putExtra("tokoUid", pesananTo);
                startActivity(intent1);

            }
        });
    }


    private void loadItemPesanan() {
        //initList
        modelItemPesananArrayList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(pesananTo).child("Pesanan").child(pesananId).child("Items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelItemPesananArrayList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ModelItemPesanan modelItemPesanan = dataSnapshot.getValue(ModelItemPesanan.class);
                    //addToList
                    modelItemPesananArrayList.add(modelItemPesanan);
                }
                //setUpAdapter
                adapterItemPesanan = new AdapterItemPesanan(DetailPesananPenggunaActivity.this, modelItemPesananArrayList);
                //setAdapter
                ItemsRv.setAdapter(adapterItemPesanan);

                //setjumlahItem
                JumlahPesananTv.setText(""+snapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadDetailPesanan() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(pesananTo).child("Pesanan").child(pesananId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //GetData
                String pesananId = ""+ snapshot.child("pesananId").getValue();
                String pesanantotalHarga = ""+ snapshot.child("pesanantotalHarga").getValue();
                String pesananBy = ""+ snapshot.child("pesananBy").getValue();
                String pesananTo = ""+ snapshot.child("pesananTo").getValue();
                String pesananStatus = ""+ snapshot.child("pesananStatus").getValue();
                String pesananTime = ""+ snapshot.child("pesananTime").getValue();
                String DeliveryFee = ""+ snapshot.child("DeliveryFee").getValue(); //CekLagi
                String latitude = ""+ snapshot.child("latitude").getValue();
                String longitude = ""+ snapshot.child("longitude").getValue();

                //ConverDataPesanan
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(pesananTime));
                String dataPesanan = DateFormat.format("dd/MM/yyyy hh:mm a", calendar).toString();

                if (pesananStatus.equals("Sedang di proses..")){
                    statusPesananTv.setTextColor(getResources().getColor(R.color.bluedark));
                }else if (pesananStatus.equals("Dikemas")){
                    statusPesananTv.setTextColor(getResources().getColor(R.color.yellow));
                }else if (pesananStatus.equals("Dikirim")){
                    statusPesananTv.setTextColor(getResources().getColor(R.color.green));
                } else if (pesananStatus.equals("Berhasil")){
                    statusPesananTv.setTextColor(getResources().getColor(R.color.teal_200));
                }else if (pesananStatus.equals("Gagal")){
                    statusPesananTv.setTextColor(getResources().getColor(R.color.merah));
                }

                //setData
                IdPesananTv.setText(pesananId);
                statusPesananTv.setText(pesananStatus);
                TotalHargaTv.setText(pesanantotalHarga + " [ Include biaya pengiriman " + DeliveryFee + "]");
                DatePesananTv.setText(dataPesanan);

                lokasiPesanan( latitude, longitude);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void lokasiPesanan(String latitude, String longitude) {
        double lat = Double.parseDouble(latitude);
        double lon = Double.parseDouble(longitude);

        //temukanLokasi
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);

            String address = addresses.get(0).getAddressLine(0);
            AddressTv.setText(address);


        }catch (Exception e){

        }

    }

    private void loadTokoInfo() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(pesananTo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String NamaToko = ""+ snapshot.child("NamaToko").getValue();
                namaTokoTv.setText(NamaToko);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}