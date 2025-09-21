package com.example.aplikasiumkm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.aplikasiumkm.adapter.AdapterItemPesanan;
import com.example.aplikasiumkm.models.ModelItemPesanan;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DetailPesananPenjualActivity extends AppCompatActivity {


    private ImageButton back_btn, mapBtn, writeReview_btn;
    private TextView IdPesananTv, DataPesananTv, statusPesananTv, NamaPemesanTv, EmailPesananTv
            , NomerHpPesananTv, JumlahProdukPesananTv, TotalHargaPesananTv, AlamatPesananTv;
    private RecyclerView ItemsRv;
    private ImageView profilePmsn;

    String pesananId, pesananBy;

    String cariLatitude, cariLongitude , destinasiLatitude, destinasiLongitude;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelItemPesanan> ItemPesananArrayList;
    private AdapterItemPesanan adapterItemPesanan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pesanan_penjual);
        back_btn = findViewById(R.id.back_btn);
        mapBtn = findViewById(R.id.mapBtn);
        writeReview_btn = findViewById(R.id.writeReview_btn);
        IdPesananTv = findViewById(R.id.IdPesananTv);
        DataPesananTv = findViewById(R.id.PesananTv);
        statusPesananTv = findViewById(R.id.statusPesananTv);
        NamaPemesanTv = findViewById(R.id.NamaPemesanTv);
        EmailPesananTv = findViewById(R.id.EmailPesananTv);
        NomerHpPesananTv = findViewById(R.id.NomerHpPesananTv);
        JumlahProdukPesananTv = findViewById(R.id.JumlahProdukPesananTv);
        TotalHargaPesananTv = findViewById(R.id.TotalHargaPesananTv);
        AlamatPesananTv = findViewById(R.id.AlamatPesananTv);
        ItemsRv = findViewById(R.id.ItemsRv);
        profilePmsn = findViewById(R.id.profilePmsn);

//        final Intent intent = getIntent();
        pesananId =getIntent().getStringExtra("pesananId");
        pesananBy =getIntent().getStringExtra("pesananBy");

        firebaseAuth = FirebaseAuth.getInstance();

        loadMyInfo();
        loadInfoPembeli();
        loadDetailPesanan();
        loadItemPesanan();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String address = "https://maps.google.com/maps?saddr=" + cariLatitude +","+cariLongitude + "&daddr=" + destinasiLatitude+","+ destinasiLongitude;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
                startActivity(intent );
            }
        });
        writeReview_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusPesanan();

            }
        });

    }

    private void statusPesanan() {
        String[] options = {"Sedang di proses..","Dikemas", "Dikirim","Berhasil", "Gagal" };
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Status Pesanan").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String pilihan = options[i];
                editStatusPesanan(pilihan);

            }
        }).show();
    }

    private void editStatusPesanan(final String pilihan) {
        HashMap <String, Object> hashMap = new HashMap<>();
        hashMap.put("pesananStatus", ""+ pilihan);

        DatabaseReference databaseReference =FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Pesanan").child(pesananId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                String message = "Pesanan Sekarang" + pilihan;
                Toast.makeText(DetailPesananPenjualActivity.this, message, Toast.LENGTH_SHORT).show();

                NotificationMessage(pesananId, message);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailPesananPenjualActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void loadItemPesanan() {
        ItemPesananArrayList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Pesanan").child(pesananId).child("Items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ItemPesananArrayList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ModelItemPesanan modelItemPesanan =  dataSnapshot.getValue(ModelItemPesanan.class);

                    ItemPesananArrayList.add(modelItemPesanan);
                }

                adapterItemPesanan = new AdapterItemPesanan(DetailPesananPenjualActivity.this, ItemPesananArrayList);
                ItemsRv.setAdapter(adapterItemPesanan);

                JumlahProdukPesananTv.setText(""+snapshot.getChildrenCount());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadDetailPesanan() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Pesanan").child(pesananId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String pesananId = ""+ snapshot.child("pesananId").getValue();
                String pesanantotalHarga = ""+ snapshot.child("pesanantotalHarga").getValue();
                String pesananBy = ""+ snapshot.child("pesananBy").getValue();
                String pesananTo = ""+ snapshot.child("pesananTo").getValue();
                String pesananStatus = ""+ snapshot.child("pesananStatus").getValue();
                String pesananTime = ""+ snapshot.child("pesananTime").getValue();
                String DeliveryFee = ""+ snapshot.child("DeliveryFee").getValue(); //CekLagi
                String latitude = ""+ snapshot.child("latitude").getValue();
                String longitude = ""+ snapshot.child("longitude").getValue();

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(pesananTime));
                String formatWaktu = DateFormat.format("dd/MM/yyyy", calendar).toString();


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
                DataPesananTv.setText(formatWaktu);
                TotalHargaPesananTv.setText(pesanantotalHarga + " [Include biaya pengiriman " + DeliveryFee + "]");

                cariAlamat(latitude, longitude);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void cariAlamat(String latitude, String longitude) {

        double lat = Double.parseDouble(latitude);
        double lon = Double.parseDouble(longitude);

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);

            String address = addresses.get(0).getAddressLine(0);
            AlamatPesananTv.setText(address);

        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

        }


    }

    private void loadInfoPembeli() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(pesananBy).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                destinasiLatitude = ""+snapshot.child("latitude").getValue();
                destinasiLongitude = ""+snapshot.child("longitude").getValue();
                String email = ""+snapshot.child("email").getValue();
                String NamaLengkap = ""+snapshot.child("NamaLengkap").getValue();
                String phone = ""+snapshot.child("phone").getValue();
                String profileImage = ""+ snapshot.child("profileImage").getValue();
//
                try {
                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_baseline_person_black).into(profilePmsn);
                }
                catch (Exception e){
                   profilePmsn.setImageResource(R.drawable.ic_baseline_person_black);
                }

                EmailPesananTv.setText(email);
                NomerHpPesananTv.setText(phone);
                NamaPemesanTv.setText(NamaLengkap);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadMyInfo() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                cariLatitude = ""+snapshot.child("latitude").getValue();
                cariLongitude = ""+snapshot.child("longitude").getValue();
//                String profileImage = ""+ snapshot.child("profileImage").getValue();
//
//                try {
//                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_baseline_person_black).into(profilePmsn);
//                }
//                catch (Exception e){
//                   profilePmsn.setImageResource(R.drawable.ic_baseline_person_black);
//                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void NotificationMessage(String pesananId, String message){

        String NOTIFICATION_TOPIC = "/topics" +Constants.FCM_TOPIC;
        String NOTIFICATION_TITLE = "Pesanan Baru " + pesananId;
        String NOTIFICATION_MESSAGE = "" + message;
        String NOTIFICATION_TYPE = "StatusPesanan";

        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();

        try {
            notificationBodyJo.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJo.put("pembeliUid", pesananBy);
            notificationBodyJo.put("penjualUid", firebaseAuth.getUid());
            notificationBodyJo.put("pesananId", pesananId);
            notificationBodyJo.put("notificationTitle", NOTIFICATION_TITLE); //CekLagi
//            notificationBodyJo.put("notificationPemesan", NOTIFICATION_TITLE_1);
            notificationBodyJo.put("notificationMessage", NOTIFICATION_MESSAGE);

            notificationJo.put("to", NOTIFICATION_TOPIC);
            notificationJo.put("data", notificationBodyJo);

        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        mengirimNotification(notificationJo);
    }

    private void mengirimNotification(JSONObject notificationJo) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {



            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String > headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key" + Constants.FCM_KEY);

                return headers;
            }
        };

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

}