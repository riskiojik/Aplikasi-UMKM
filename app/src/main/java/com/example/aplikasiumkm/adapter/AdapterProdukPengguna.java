package com.example.aplikasiumkm.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplikasiumkm.FilterProdukPengguna;
import com.example.aplikasiumkm.R;
import com.example.aplikasiumkm.TokoDetailActivity;
import com.example.aplikasiumkm.models.ModelProduk;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterProdukPengguna extends RecyclerView.Adapter<AdapterProdukPengguna.HolderProdukPengguna> implements Filterable {

    private final Context context;
    public ArrayList<ModelProduk> produkList, filterList;
    private FilterProdukPengguna filterProdukPengguna;

    public AdapterProdukPengguna(Context context, ArrayList<ModelProduk> produkList) {
        this.context = context;
        this.produkList = produkList;
        this.filterList = produkList;
    }

    @NonNull
    @Override
    public HolderProdukPengguna onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_list_produk_pengguna_new_style, parent, false);
        return new HolderProdukPengguna(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProdukPengguna holder, int position) {
        ModelProduk model = produkList.get(position);

        String hargaAwal = model.getOriginalPrice();
        String hargaDiskon = model.getDiskonPriceNote();
        String diskon = model.getDiskonPrice();
        String diskonAvailable = model.getDiskonAvailable();

        holder.titleEt.setText(model.getProductTitle());
        holder.deskripsiEt.setText(model.getDeskripsiProduk());
        holder.priceEt.setText("Rp. " + hargaAwal);
        holder.diskonHargaNoteEt.setText("Rp. " + hargaDiskon);
        holder.diskonHargaEt.setText("Diskon: " + diskon);
        holder.stokTv.setText("Stok: " + model.getProdukQuantity());

        if ("true".equals(diskonAvailable)) {
            holder.diskonHargaEt.setVisibility(View.VISIBLE);
            holder.diskonHargaNoteEt.setVisibility(View.VISIBLE);
            holder.priceEt.setPaintFlags(holder.priceEt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.diskonHargaEt.setVisibility(View.GONE);
            holder.diskonHargaNoteEt.setVisibility(View.GONE);
            holder.priceEt.setPaintFlags(0);
        }

        try {
            Picasso.get()
                    .load(model.getProdukIcon())
                    .placeholder(R.drawable.ic_baseline_add_shopping_cart_yellow)
                    .into(holder.produkIconIv);
        } catch (Exception e) {
            holder.produkIconIv.setImageResource(R.drawable.ic_baseline_add_shopping_cart_yellow);
        }

        holder.tmbhKeranjangTv.setOnClickListener(v -> tampilkanDialogJumlah(model));
    }

    private int cost = 0, totalCost = 0, quantity = 0;

    private void tampilkanDialogJumlah(ModelProduk model) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_cart, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        ImageView produkIconIv = view.findViewById(R.id.produkIconIv);
        TextView titleTv = view.findViewById(R.id.titleTv);
        TextView produkQtyTv = view.findViewById(R.id.produkquantityTv);
        TextView deskripsiTv = view.findViewById(R.id.deskripsiEt);
        TextView diskonTv = view.findViewById(R.id.diskonHargaEt);
        TextView diskonNoteTv = view.findViewById(R.id.diskonHargaNoteEt);
        TextView priceTv = view.findViewById(R.id.priceEt);
        TextView totalTv = view.findViewById(R.id.totalTv);
        TextView quantityTv = view.findViewById(R.id.quantityTv);
        ImageButton tambahBtn = view.findViewById(R.id.TambahIb);
        ImageButton kurangBtn = view.findViewById(R.id.KurangIb);
        Button tambahKeranjangBtn = view.findViewById(R.id.TambahKeranjangBtn);

        String hargaFix = model.getDiskonAvailable().equals("true") ?
                model.getDiskonPriceNote() : model.getOriginalPrice();

        hargaFix = hargaFix.replaceAll("[^0-9]", "");
        cost = Integer.parseInt(hargaFix);
        totalCost = cost;
        quantity = 1;

        titleTv.setText(model.getProductTitle());
        produkQtyTv.setText(model.getProdukQuantity());
        deskripsiTv.setText(model.getDeskripsiProduk());
        priceTv.setText("Rp. " + model.getOriginalPrice());
        diskonNoteTv.setText("Rp. " + model.getDiskonPriceNote());
        diskonTv.setText("Diskon: " + model.getDiskonPrice());
        quantityTv.setText(String.valueOf(quantity));
        totalTv.setText("Rp. " + totalCost);

        if ("true".equals(model.getDiskonAvailable())) {
            diskonTv.setVisibility(View.VISIBLE);
            diskonNoteTv.setVisibility(View.VISIBLE);
            priceTv.setPaintFlags(priceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            diskonTv.setVisibility(View.GONE);
            diskonNoteTv.setVisibility(View.GONE);
            priceTv.setPaintFlags(0);
        }

        try {
            Picasso.get()
                    .load(model.getProdukIcon())
                    .placeholder(R.drawable.ic_baseline_add_shopping_cart_yellow)
                    .into(produkIconIv);
        } catch (Exception e) {
            produkIconIv.setImageResource(R.drawable.ic_baseline_add_shopping_cart_yellow);
        }

        tambahBtn.setOnClickListener(v -> {
            quantity++;
            totalCost = cost * quantity;
            quantityTv.setText(String.valueOf(quantity));
            totalTv.setText("Rp. " + totalCost);
        });

        kurangBtn.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                totalCost = cost * quantity;
                quantityTv.setText(String.valueOf(quantity));
                totalTv.setText("Rp. " + totalCost);
            }
        });

        tambahKeranjangBtn.setOnClickListener(v -> {
            tambahKeKeranjang(
                    model.getProductId(),
                    model.getProductTitle(),
                    String.valueOf(cost),
                    String.valueOf(totalCost),
                    String.valueOf(quantity)
            );
            dialog.dismiss();
        });

        dialog.show();
    }

    private int itemId = 1;

    private void tambahKeKeranjang(String productId, String title, String harga, String total, String qty) {
        itemId++;
        EasyDB easyDB = EasyDB.init(context, "ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Harga", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_TotalHarga", new String[]{"text", "not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                .doneTableColumn();

        easyDB.addData("Item_Id", itemId)
                .addData("Item_PID", productId)
                .addData("Item_Name", title)
                .addData("Item_Harga", harga)
                .addData("Item_TotalHarga", total)
                .addData("Item_Quantity", qty)
                .doneDataAdding();

        Toast.makeText(context, "Menambahkan produk ke keranjang", Toast.LENGTH_SHORT).show();
        ((TokoDetailActivity) context).jumlahCart();
    }

    @Override
    public int getItemCount() {
        return produkList.size();
    }

    @Override
    public Filter getFilter() {
        if (filterProdukPengguna == null) {
            filterProdukPengguna = new FilterProdukPengguna(this, filterList);
        }
        return filterProdukPengguna;
    }

    static class HolderProdukPengguna extends RecyclerView.ViewHolder {
        ImageView produkIconIv, nextIv;
        TextView diskonHargaEt, titleEt, deskripsiEt, tmbhKeranjangTv, diskonHargaNoteEt, priceEt, stokTv;

        public HolderProdukPengguna(@NonNull View itemView) {
            super(itemView);
            produkIconIv = itemView.findViewById(R.id.produkIconIv);
            diskonHargaEt = itemView.findViewById(R.id.diskonHargaEt);
            titleEt = itemView.findViewById(R.id.titleEt);
            deskripsiEt = itemView.findViewById(R.id.deskripsiEt);
            tmbhKeranjangTv = itemView.findViewById(R.id.tmbhKeranjangTv);
            diskonHargaNoteEt = itemView.findViewById(R.id.diskonHargaNoteEt);
            priceEt = itemView.findViewById(R.id.priceEt);
            stokTv = itemView.findViewById(R.id.stokTv);
            nextIv = itemView.findViewById(R.id.nextIv);
        }
    }
}
