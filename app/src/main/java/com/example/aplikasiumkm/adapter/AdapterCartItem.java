package com.example.aplikasiumkm.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplikasiumkm.R;
import com.example.aplikasiumkm.TokoDetailActivity;
import com.example.aplikasiumkm.models.ModelCartItems;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.HolderCartItem> {

    private Context context;
    private ArrayList<ModelCartItems> cartItems;

    public AdapterCartItem(Context context, ArrayList<ModelCartItems> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_cart_item, parent, false);
        return new HolderCartItem(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HolderCartItem holder, @SuppressLint("RecyclerView") int position) {
        ModelCartItems item = cartItems.get(position);

        String id = item.getId();
        String productTitle = item.getNama();
        String harga = item.getPrice();
        String totalHarga = item.getCost();
        String quantity = item.getQuantity();

        holder.namaProdukTv.setText(productTitle);
        holder.hargaItemTv.setText("Rp." + harga);
        holder.hargaPerItem.setText("Rp." + totalHarga);
        holder.produkquantityTv.setText("[" + quantity + "]");

        holder.hapusProdukTv.setOnClickListener(view -> {
            EasyDB easyDB = EasyDB.init(context, "ITEMS_DB")
                    .setTableName("ITEMS_TABLE")
                    .addColumn(new Column("Item_Id", new String[]{"text", "unique"}))
                    .addColumn(new Column("Item_PID", new String[]{"text", "not null"}))
                    .addColumn(new Column("Item_Name", new String[]{"text", "not null"}))
                    .addColumn(new Column("Item_Harga", new String[]{"text", "not null"}))
                    .addColumn(new Column("Item_TotalHarga", new String[]{"text", "not null"}))
                    .addColumn(new Column("Item_Quantity", new String[]{"text", "not null"}))
                    .doneTableColumn();

            // Hapus dari DB
            easyDB.deleteRow(1, id);
            Toast.makeText(context, "Produk pesanan dihapus", Toast.LENGTH_SHORT).show();

            // Hapus dari list dan refresh
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());

            try {
                TokoDetailActivity activity = (TokoDetailActivity) context;
                int currentTotal = Integer.parseInt(activity.TotalHargaTv.getText().toString().replace("Rp.", "").trim());
                int totalHargaa = currentTotal - Integer.parseInt(totalHarga.trim());

                activity.TotalHarga = totalHargaa;
                activity.TotalHargaTv.setText("Rp." + totalHargaa);
                activity.jumlahCart();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class HolderCartItem extends RecyclerView.ViewHolder {
        TextView namaProdukTv, hargaItemTv, hargaPerItem, produkquantityTv, hapusProdukTv;

        public HolderCartItem(@NonNull View itemView) {
            super(itemView);
            namaProdukTv = itemView.findViewById(R.id.namaProdukTv);
            hargaItemTv = itemView.findViewById(R.id.hargaItemTv);
            hargaPerItem = itemView.findViewById(R.id.hargaPerItem);
            produkquantityTv = itemView.findViewById(R.id.produkquantityTv);
            hapusProdukTv = itemView.findViewById(R.id.hapusProdukTv);
        }
    }
}
