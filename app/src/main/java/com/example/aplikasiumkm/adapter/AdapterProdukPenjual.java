
package com.example.aplikasiumkm.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplikasiumkm.EditProdukActivity;
import com.example.aplikasiumkm.FilterProduk;
import com.example.aplikasiumkm.R;
import com.example.aplikasiumkm.models.ModelProduk;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;


public class AdapterProdukPenjual extends RecyclerView.Adapter<AdapterProdukPenjual.HolderProdukPenjual> implements Filterable {

    private final Context context;
    private FilterProduk filterProduk;
    public ArrayList<ModelProduk> produkList, filterList;

    public AdapterProdukPenjual(Context context, ArrayList<ModelProduk> produkList) {
        this.context = context;
        this.produkList = produkList;
        this.filterList = produkList;
    }

    @NonNull
    @Override
    public HolderProdukPenjual onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_list_produk_new_style, parent, false);
        return new HolderProdukPenjual(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProdukPenjual holder, int position) {
        ModelProduk model = produkList.get(position);

        holder.titleEt.setText(model.getProductTitle());
        holder.quantityEt.setText(model.getProdukQuantity());
        holder.diskonHargaNoteEt.setText("Rp. " + model.getDiskonPriceNote());
        holder.diskonHargaEt.setText("Diskon : " + model.getDiskonPrice());
        holder.priceEt.setText("Rp. " + model.getOriginalPrice());

        if ("true".equals(model.getDiskonAvailable())) {
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

        holder.itemView.setOnClickListener(view -> showDetailBottomSheet(model));
    }

    private void showDetailBottomSheet(ModelProduk model) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bs_detail_produk, null, false);
        if (view == null) return; // prevent crash if layout not found

        bottomSheetDialog.setContentView(view);

        ImageButton backBtn = view.findViewById(R.id.back_btn);
        ImageButton deleteBtn = view.findViewById(R.id.delete_btn);
        ImageButton editBtn = view.findViewById(R.id.edit_produk_btn);
        ImageView produkIconIv = view.findViewById(R.id.produkIconIv);
        TextView diskonHargaEt = view.findViewById(R.id.diskonHargaEt);
        TextView titleEt = view.findViewById(R.id.titleEt);
        TextView deskripsiEt = view.findViewById(R.id.deskripsiEt);
        TextView categoryTv = view.findViewById(R.id.categoryTv);
        TextView quantityEt = view.findViewById(R.id.quantityEt);
        TextView diskonHargaNoteEt = view.findViewById(R.id.diskonHargaNoteEt);
        TextView priceEt = view.findViewById(R.id.priceEt);

        // Set Data
        titleEt.setText(model.getProductTitle());
        deskripsiEt.setText(model.getDeskripsiProduk());
        categoryTv.setText(model.getCategoryProduk());
        quantityEt.setText(model.getProdukQuantity());
        diskonHargaNoteEt.setText("Rp. " + model.getDiskonPriceNote());
        diskonHargaEt.setText("Diskon : " + model.getDiskonPrice());
        priceEt.setText("Rp. " + model.getOriginalPrice());

        if ("true".equals(model.getDiskonAvailable())) {
            diskonHargaEt.setVisibility(View.VISIBLE);
            diskonHargaNoteEt.setVisibility(View.VISIBLE);
            priceEt.setPaintFlags(priceEt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            diskonHargaEt.setVisibility(View.GONE);
            diskonHargaNoteEt.setVisibility(View.GONE);
        }

        try {
            Picasso.get()
                    .load(model.getProdukIcon())
                    .placeholder(R.drawable.ic_baseline_add_shopping_cart_yellow)
                    .into(produkIconIv);
        } catch (Exception e) {
            produkIconIv.setImageResource(R.drawable.ic_baseline_add_shopping_cart_yellow);
        }

        editBtn.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(context, EditProdukActivity.class);
            intent.putExtra("produkId", model.getProductId());
            context.startActivity(intent);
        });

        deleteBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Hapus Produk")
                    .setMessage("Apakah Anda yakin ingin menghapus produk " + model.getProductTitle() + "?")
                    .setPositiveButton("Hapus", (dialog, i) -> deleteProduk(model.getProductId()))
                    .setNegativeButton("Batal", (dialog, i) -> dialog.dismiss())
                    .show();
        });

        backBtn.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    private void deleteProduk(String id) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = Objects.requireNonNull(firebaseAuth.getUid());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(uid)
                .child("Produk")
                .child(id)
                .removeValue()
                .addOnSuccessListener(unused ->
                        Toast.makeText(context, "Produk telah dihapus.", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    public int getItemCount() {
        return produkList.size();
    }

    @Override
    public Filter getFilter() {
        if (filterProduk == null) {
            filterProduk = new FilterProduk(this, filterList);
        }
        return filterProduk;
    }

    static class HolderProdukPenjual extends RecyclerView.ViewHolder {

        ImageView produkIconIv;
        TextView diskonHargaNoteEt, titleEt, quantityEt, diskonHargaEt, priceEt;

        public HolderProdukPenjual(@NonNull View itemView) {
            super(itemView);
            produkIconIv = itemView.findViewById(R.id.produkIconIv);
            diskonHargaNoteEt = itemView.findViewById(R.id.diskonHargaNoteEt);
            titleEt = itemView.findViewById(R.id.titleEt);
            quantityEt = itemView.findViewById(R.id.quantityEt);
            diskonHargaEt = itemView.findViewById(R.id.diskonHargaEt);
            priceEt = itemView.findViewById(R.id.priceEt);
        }
    }
}
