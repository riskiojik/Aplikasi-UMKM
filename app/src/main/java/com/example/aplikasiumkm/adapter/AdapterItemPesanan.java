package com.example.aplikasiumkm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplikasiumkm.R;
import com.example.aplikasiumkm.models.ModelItemPesanan;

import java.util.ArrayList;

public class AdapterItemPesanan extends RecyclerView.Adapter<AdapterItemPesanan.HolderItemPesanan> {

    private final Context context;
    private final ArrayList<ModelItemPesanan> modelItemPesananArrayList;

    public AdapterItemPesanan(Context context, ArrayList<ModelItemPesanan> modelItemPesananArrayList) {
        this.context = context;
        this.modelItemPesananArrayList = modelItemPesananArrayList;
    }

    @NonNull
    @Override
    public HolderItemPesanan onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_item_pesanan, parent, false);
        return new HolderItemPesanan(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderItemPesanan holder, int position) {
        // Ambil data dari list
        ModelItemPesanan item = modelItemPesananArrayList.get(position);

        // Set data ke komponen UI
        holder.namaProdukTv.setText(item.getProdukTitle());
        holder.hargaItemTv.setText("Rp. " + item.getTotalHarga());
        holder.hargaPerItem.setText("Rp. " + item.getHarga());
        holder.produkquantityTv.setText("[" + item.getQuantity() + "]");
    }

    @Override
    public int getItemCount() {
        return modelItemPesananArrayList.size();
    }

    // ViewHolder
    static class HolderItemPesanan extends RecyclerView.ViewHolder {

        TextView namaProdukTv, hargaItemTv, hargaPerItem, produkquantityTv;

        public HolderItemPesanan(@NonNull View itemView) {
            super(itemView);

            namaProdukTv = itemView.findViewById(R.id.namaProdukTv);
            hargaItemTv = itemView.findViewById(R.id.hargaItemTv);
            hargaPerItem = itemView.findViewById(R.id.hargaPerItem);
            produkquantityTv = itemView.findViewById(R.id.produkquantityTv);
        }
    }
}
