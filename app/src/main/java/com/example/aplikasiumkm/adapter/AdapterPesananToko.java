package com.example.aplikasiumkm.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplikasiumkm.DetailPesananPenjualActivity;
import com.example.aplikasiumkm.FilterPesananToko;
import com.example.aplikasiumkm.R;
import com.example.aplikasiumkm.models.ModelPesananToko;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterPesananToko extends RecyclerView.Adapter<AdapterPesananToko.HolderPesananToko> implements Filterable {

    private final Context context;
    public ArrayList<ModelPesananToko> pesananTokoArrayList, filterList;
    private FilterPesananToko filter;

    public AdapterPesananToko(Context context, ArrayList<ModelPesananToko> pesananTokoArrayList) {
        this.context = context;
        this.pesananTokoArrayList = pesananTokoArrayList;
        this.filterList = pesananTokoArrayList;
    }

    @NonNull
    @Override
    public HolderPesananToko onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_pesanan_penjual, parent, false);
        return new HolderPesananToko(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPesananToko holder, int position) {
        ModelPesananToko model = pesananTokoArrayList.get(position);

        // Ambil data
        String pesananId = model.getPesananId();
        String pesananTime = model.getPesananTime();
        String pesananStatus = model.getPesananStatus();
        String totalHarga = model.getPesanantotalHarga();
        String pesananBy = model.getPesananBy();

        // Tampilkan informasi pengguna
        loadInfoPengguna(model, holder);

        // Set informasi pesanan
        holder.pesananIdTv.setText("IdPesanan: " + pesananId);
        holder.jumlahTv.setText("Jumlah: " + totalHarga);
        holder.statusTv.setText(pesananStatus);

        // Format tanggal
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(pesananTime));
        String formattedDate = DateFormat.format("dd/MM/yyyy", calendar).toString();
        holder.dateIdTv.setText(formattedDate);

        // Warna status
        switch (pesananStatus) {
            case "Sedang di proses..":
                holder.statusTv.setTextColor(ContextCompat.getColor(context, R.color.bluedark));
                break;
            case "Dikemas":
                holder.statusTv.setTextColor(ContextCompat.getColor(context, R.color.yellow));
                break;
            case "Dikirim":
                holder.statusTv.setTextColor(ContextCompat.getColor(context, R.color.green));
                break;
            case "Berhasil":
                holder.statusTv.setTextColor(ContextCompat.getColor(context, R.color.teal_200));
                break;
            case "Gagal":
                holder.statusTv.setTextColor(ContextCompat.getColor(context, R.color.design_default_color_error));
                break;
            default:
                holder.statusTv.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }

        // Klik item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailPesananPenjualActivity.class);
            intent.putExtra("pesananId", pesananId);
            intent.putExtra("pesananBy", pesananBy);
            context.startActivity(intent);
        });
    }

    private void loadInfoPengguna(ModelPesananToko model, HolderPesananToko holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(model.getPesananBy()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = "" + snapshot.child("email").getValue();
                String namaLengkap = "" + snapshot.child("NamaLengkap").getValue();
                String profileImage = "" + snapshot.child("profileImage").getValue();

                holder.emailTv.setText(email);
                holder.namaTv.setText(namaLengkap);
                try {
                    Picasso.get()
                            .load(profileImage)
                            .placeholder(R.drawable.ic_baseline_person_black)
                            .into(holder.profilePmsn);
                } catch (Exception e) {
                    holder.profilePmsn.setImageResource(R.drawable.ic_baseline_person_black);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // bisa ditambahkan log error jika diperlukan
            }
        });
    }

    @Override
    public int getItemCount() {
        return pesananTokoArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterPesananToko(this, filterList);
        }
        return filter;
    }

    // ViewHolder
    static class HolderPesananToko extends RecyclerView.ViewHolder {
        TextView pesananIdTv, emailTv, jumlahTv, dateIdTv, statusTv, namaTv;
        ImageButton nextIv;
        ImageView profilePmsn;

        public HolderPesananToko(@NonNull View itemView) {
            super(itemView);
            pesananIdTv = itemView.findViewById(R.id.pesananIdTv);
            emailTv = itemView.findViewById(R.id.emailTv);
            jumlahTv = itemView.findViewById(R.id.jumlahTv);
            dateIdTv = itemView.findViewById(R.id.dateIdTv);
            statusTv = itemView.findViewById(R.id.statusTv);
            namaTv = itemView.findViewById(R.id.namaTv);
            profilePmsn = itemView.findViewById(R.id.profilePmsn);
            nextIv = itemView.findViewById(R.id.nextIv);
        }
    }
}
