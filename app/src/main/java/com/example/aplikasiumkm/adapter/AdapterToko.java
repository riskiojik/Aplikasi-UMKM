package com.example.aplikasiumkm.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplikasiumkm.R;
import com.example.aplikasiumkm.TokoDetailActivity;
import com.example.aplikasiumkm.models.ModelToko;
import com.example.aplikasiumkm.penjual.MainPenjualActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterToko extends RecyclerView.Adapter<AdapterToko.HolderToko>{

    private Context context;
    public ArrayList<ModelToko> tokoList;


    public AdapterToko(Context context, ArrayList<ModelToko> tokoList) {
        this.context = context;
        this.tokoList = tokoList;
    }

    @NonNull
    @Override
    public HolderToko onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        inflate row_toko,xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_toko,parent,false);
        return new HolderToko(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderToko holder, int position) {
//        Getdata
        ModelToko modelToko = tokoList.get(position);
        String accountType = modelToko.getAccountType();
        String address = modelToko.getAddress();
        String city = modelToko.getCity();
        String country = modelToko.getCountry();
        String NamaToko = modelToko.getNamaToko();
        String JenisKelamin = modelToko.getJenisKelamin();
        String deliveryFee = modelToko.getDeliveryFee();
        String email = modelToko.getEmail();
        String latitude = modelToko.getLatitude();
        String longitude = modelToko.getLongitude();
        String online = modelToko.getOnline();
        String NamaLengkap = modelToko.getNamaLengkap();
        String phone = modelToko.getPhone();
        String uid = modelToko.getUid();
        String tokoBuka = modelToko.getTokoBuka();
        String state = modelToko.getState();
        String profileImage = modelToko.getProfileImage();

        loadReview(modelToko, holder);


        //SetData
        holder.namaTokoTv.setText(NamaToko);
        holder.phoneTv.setText(phone);
        holder.addressTv.setText(address);

        if (online.equals("true")){
            holder.tokoOnlineIv.setVisibility(View.VISIBLE);

        }else {
            holder.tokoOnlineIv.setVisibility(View.GONE);
        }

//        //cek toko buka
        if (tokoBuka.equals("true")){
            holder.tokoTutupTv.setVisibility(View.GONE);
        }else {
            holder.tokoTutupTv.setVisibility(View.VISIBLE);
        }

        try {
            Picasso.get().load(profileImage).placeholder(R.drawable.ic_baseline_storefront_grey).into(holder.TokoIv);

        }catch (Exception e){
            holder.TokoIv.setImageResource(R.drawable.ic_baseline_storefront_grey);

        }
        //detailToko
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TokoDetailActivity.class);
                intent.putExtra("tokoUid", uid);
                context.startActivity(intent);
            }
        });

    }
    private float ratingsum = 0;
    private void loadReview(ModelToko modelToko, HolderToko holder) {
            String tokoUid  =modelToko.getUid();

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

                    holder.RatingBr.setRating(avgRating);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }


    @Override
    public int getItemCount() {
        return tokoList.size();
    }

    class HolderToko extends RecyclerView.ViewHolder{

        private ImageView TokoIv, tokoOnlineIv;
        private TextView tokoTutupTv, namaTokoTv, phoneTv, addressTv;
        private RatingBar RatingBr;

        public HolderToko(@NonNull View itemView) {
            super(itemView);

            TokoIv = itemView.findViewById(R.id.TokoIv);
            tokoOnlineIv = itemView.findViewById(R.id.tokoOnlineIv);
            tokoTutupTv = itemView.findViewById(R.id.tokoTutupTv);
            namaTokoTv = itemView.findViewById(R.id.namaTokoTv);
            phoneTv = itemView.findViewById(R.id.phoneTv);
            addressTv = itemView.findViewById(R.id.addressTv);
            RatingBr = itemView.findViewById(R.id.RatingBr);


        }
    }
}
