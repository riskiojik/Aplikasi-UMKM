package com.example.aplikasiumkm.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplikasiumkm.R;
import com.example.aplikasiumkm.models.ModelRiviewsToko;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterRiviewsToko extends RecyclerView.Adapter<AdapterRiviewsToko.HolderRiview> {

    private Context context;
    private ArrayList<ModelRiviewsToko> RiviewsTokoArrayList;

    public AdapterRiviewsToko(Context context, ArrayList<ModelRiviewsToko> RiviewsTokoArrayList) {
        this.context = context;
        this.RiviewsTokoArrayList = RiviewsTokoArrayList;
    }

    @NonNull
    @Override
    public HolderRiview onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_riviews, parent, false);
        return new HolderRiview(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRiview holder, int position) {
        //getData
        ModelRiviewsToko modelRiviewsToko = RiviewsTokoArrayList.get(position);
            String uid = modelRiviewsToko.getUid();
            String ratings = modelRiviewsToko.getRatings();
            String review = modelRiviewsToko.getReview();
            String timestamp = modelRiviewsToko.getTimestamp();

            loadDetailUser(modelRiviewsToko, holder);

            //SetDataTime
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(timestamp));
            String dateFormat = DateFormat.format("dd/MM/yyyy", calendar).toString();

            //getData
            holder.ratingBar.setRating(Float.parseFloat(ratings));
            holder.riviewsTv.setText(review);
            holder.dateTv.setText(dateFormat);


    }

    private void loadDetailUser(ModelRiviewsToko modelRiviewsToko, HolderRiview holder) {
        String uid = modelRiviewsToko.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String NamaLengkap = ""+ snapshot.child("NamaLengkap").getValue();
                String profileImage = ""+ snapshot.child("profileImage").getValue();

                holder.namaTv.setText(NamaLengkap);

                try {
                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_baseline_person).into(holder.profileTv);
                }catch (Exception e){
                    holder.profileTv.setImageResource(R.drawable.ic_baseline_person);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return RiviewsTokoArrayList.size();
    }

    class HolderRiview extends RecyclerView.ViewHolder{

        private ImageView profileTv;
        private TextView namaTv, dateTv, riviewsTv;
        private RatingBar ratingBar;

        public HolderRiview(@NonNull View itemView) {
            super(itemView);

            profileTv = itemView.findViewById(R.id.profileTv);
            namaTv = itemView.findViewById(R.id.namaTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            riviewsTv = itemView.findViewById(R.id.riviewsTv);
            ratingBar = itemView.findViewById(R.id.ratingBar);


        }
    }
}
