package com.example.aplikasiumkm;

import android.widget.Filter;

import com.example.aplikasiumkm.adapter.AdapterProdukPengguna;
import com.example.aplikasiumkm.adapter.AdapterProdukPenjual;
import com.example.aplikasiumkm.models.ModelProduk;

import java.util.ArrayList;

public class FilterProdukPengguna extends Filter {

    private AdapterProdukPengguna adapter;
    private ArrayList<ModelProduk>filterList;

    public FilterProdukPengguna(AdapterProdukPengguna adapter, ArrayList<ModelProduk> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {

        FilterResults results = new FilterResults();
        if (charSequence !=null && charSequence.length() > 0 ){

            charSequence = charSequence.toString().toUpperCase();

            ArrayList<ModelProduk> filtermodel = new ArrayList<>();
            for (int i = 0; i<filterList.size(); i++){
                //mengecek
                if (filterList.get(i).getProductTitle().toUpperCase().contains(charSequence) ||
                    filterList.get(i).getCategoryProduk().toUpperCase().contains(charSequence)){

                    filtermodel.add(filterList.get(i));
                }
            }
            results.count = filtermodel.size();
            results.values = filtermodel;
        }else {
            results.count = filterList.size();
            results.values = filterList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

        adapter.produkList = (ArrayList<ModelProduk>) filterResults.values;

        adapter.notifyDataSetChanged();
    }
}

