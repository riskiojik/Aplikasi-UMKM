package com.example.aplikasiumkm.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplikasiumkm.R;
import com.example.aplikasiumkm.models.ModelPesananPengguna;
import com.example.aplikasiumkm.user.DetailPesananPenggunaActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterPesananPengguna extends RecyclerView.Adapter<AdapterPesananPengguna.HolderPesananPengguna> {

    private final Context context;
    private final ArrayList<ModelPesananPengguna> pesananPenggunaList;

    public AdapterPesananPengguna(Context context, ArrayList<ModelPesananPengguna> pesananPenggunaList) {
        this.context = context;
        this.pesananPenggunaList = pesananPenggunaList;
    }

    @NonNull
    @Override
    public HolderPesananPengguna onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_pesanan_pengguna, parent, false);
        return new HolderPesananPengguna(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPesananPengguna holder, int position) {
        ModelPesananPengguna model = pesananPenggunaList.get(position);

        String pesananId = model.getPesananId();
        String pesananTime = model.getPesananTime();
        String pesananStatus = model.getPesananStatus();
        String totalHarga = model.getPesanantotalHarga();
        String pesananTo = model.getPesananTo();

        // Ambil info toko dari Firebase
        loadTokoInfo(model, holder);

        // Set data ke tampilan
        holder.jumlahTv.setText("Jumlah : " + totalHarga);
        holder.statusTv.setText(pesananStatus);
        holder.pesananTv.setText("IdPesanan : " + pesananId);

        // Warna status berdasarkan nilai
        switch (pesananStatus) {
            case "Sedang di proses..":
                holder.statusTv.setTextColor(context.getResources().getColor(R.color.bluedark));
                break;
            case "Dikemas":
                holder.statusTv.setTextColor(context.getResources().getColor(R.color.yellow));
                break;
            case "Dikirim":
                holder.statusTv.setTextColor(context.getResources().getColor(R.color.green));
                break;
            case "Berhasil":
                holder.statusTv.setTextColor(context.getResources().getColor(R.color.teal_200));
                break;
            case "Gagal":
                holder.statusTv.setTextColor(context.getResources().getColor(R.color.merah));
                break;
        }

        // Format waktu
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(pesananTime));
        String formatWaktu = DateFormat.format("dd/MM/yyyy", calendar).toString();
        holder.waktuTv.setText(formatWaktu);

        // Klik item untuk buka detail
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, DetailPesananPenggunaActivity.class);
            intent.putExtra("pesananTo", pesananTo);
            intent.putExtra("pesananId", pesananId);
            context.startActivity(intent);
        });
    }

    private void loadTokoInfo(ModelPesananPengguna model, HolderPesananPengguna holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(model.getPesananTo()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String namaToko = "" + snapshot.child("NamaToko").getValue();
                holder.namaTokoTv.setText("Nama Toko: " + namaToko);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error jika perlu
            }
        });
    }

    @Override
    public int getItemCount() {
        return pesananPenggunaList.size();
    }

    // ViewHolder
    static class HolderPesananPengguna extends RecyclerView.ViewHolder {

        TextView pesananTv, waktuTv, namaTokoTv, jumlahTv, statusTv;
        ImageButton nextIv;

        public HolderPesananPengguna(@NonNull View itemView) {
            super(itemView);
            pesananTv = itemView.findViewById(R.id.pesananTv);
            waktuTv = itemView.findViewById(R.id.waktuTv);
            namaTokoTv = itemView.findViewById(R.id.namaTokoTv);
            jumlahTv = itemView.findViewById(R.id.jumlahTv);
            statusTv = itemView.findViewById(R.id.statusTv);
            nextIv = itemView.findViewById(R.id.nextIv);
        }
    }
}
