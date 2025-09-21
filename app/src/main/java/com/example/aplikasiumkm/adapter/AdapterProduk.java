package com.example.aplikasiumkm.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplikasiumkm.FilterSemuaProduk;
import com.example.aplikasiumkm.R;
import com.example.aplikasiumkm.TokoDetailActivity;
import com.example.aplikasiumkm.models.ModelProduk;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterProduk extends RecyclerView.Adapter<AdapterProduk.HolderProduk> implements Filterable {

    private final Context context;
    private FilterSemuaProduk filterSemuaProduk;
    public ArrayList<ModelProduk> produkList, filterList;

    public AdapterProduk(Context context, ArrayList<ModelProduk> produkList) {
        this.context = context;
        this.produkList = produkList;
        this.filterList = produkList;
    }

    @NonNull
    @Override
    public HolderProduk onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_produk, parent, false);
        return new HolderProduk(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProduk holder, int position) {
        final ModelProduk model = produkList.get(position);

        String diskonAvailable = model.getDiskonAvailable();
        String diskonPriceNote = model.getDiskonPriceNote();
        String diskonPrice = model.getDiskonPrice();
        String originalPrice = model.getOriginalPrice();
        String deskripsiProduk = model.getDeskripsiProduk();
        String productTitle = model.getProductTitle();
        String produkIcon = model.getProdukIcon();
        String quantity = model.getProdukQuantity();
        String uid = model.getUid();

        // Set data
        holder.titleEt.setText(productTitle);
        holder.deskripsiEt.setText(deskripsiProduk);
        holder.priceEt.setText("Rp. " + originalPrice);
        holder.diskonHargaNoteEt.setText("Rp. " + diskonPriceNote);
        holder.diskonHargaEt.setText("Diskon: " + diskonPrice);
        holder.stokTv.setText("Stok: " + quantity);

        if ("true".equals(diskonAvailable)) {
            holder.diskonHargaEt.setVisibility(View.VISIBLE);
            holder.diskonHargaNoteEt.setVisibility(View.VISIBLE);
            holder.priceEt.setPaintFlags(holder.priceEt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.diskonHargaEt.setVisibility(View.GONE);
            holder.diskonHargaNoteEt.setVisibility(View.GONE);
            holder.priceEt.setPaintFlags(0);
        }

        // Load gambar produk
        try {
            Picasso.get()
                    .load(produkIcon)
                    .placeholder(R.drawable.ic_baseline_add_shopping_cart_yellow)
                    .into(holder.produkIconIv);
        } catch (Exception e) {
            holder.produkIconIv.setImageResource(R.drawable.ic_baseline_add_shopping_cart_yellow);
        }

        // Klik item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TokoDetailActivity.class);
            intent.putExtra("produkId", uid);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return produkList.size();
    }

    @Override
    public Filter getFilter() {
        if (filterSemuaProduk == null) {
            filterSemuaProduk = new FilterSemuaProduk(this, filterList);
        }
        return filterSemuaProduk;
    }

    class HolderProduk extends RecyclerView.ViewHolder {

        ImageView produkIconIv, nextIv;
        TextView diskonHargaEt, titleEt, deskripsiEt, diskonHargaNoteEt, priceEt, stokTv;

        public HolderProduk(@NonNull View itemView) {
            super(itemView);
            produkIconIv = itemView.findViewById(R.id.produkIconIv);
            diskonHargaEt = itemView.findViewById(R.id.diskonHargaEt);
            titleEt = itemView.findViewById(R.id.titleEt);
            deskripsiEt = itemView.findViewById(R.id.deskripsiEt);
            diskonHargaNoteEt = itemView.findViewById(R.id.diskonHargaNoteEt);
            priceEt = itemView.findViewById(R.id.priceEt);
            stokTv = itemView.findViewById(R.id.stokTv);
            nextIv = itemView.findViewById(R.id.nextIv);
        }
    }
}
