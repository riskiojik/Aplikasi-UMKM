package com.example.aplikasiumkm;

import android.widget.Filter;

import com.example.aplikasiumkm.adapter.AdapterProduk;
import com.example.aplikasiumkm.models.ModelProduk;

import java.util.ArrayList;

public class FilterSemuaProduk extends Filter {

    private AdapterProduk adapterProduk;
    private ArrayList<ModelProduk> filterlist;

    public FilterSemuaProduk(AdapterProduk adapter, ArrayList<ModelProduk> list) {
        this.adapterProduk = adapter;
        this.filterlist = list;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if (charSequence !=null && charSequence.length() > 0 ){

            charSequence = charSequence.toString().toUpperCase();

            ArrayList<ModelProduk> filtermodel = new ArrayList<>();
            for (int i = 0; i<filterlist.size(); i++){
                //mengecek
                if (filterlist.get(i).getProductTitle().toUpperCase().contains(charSequence) ||
                        filterlist.get(i).getCategoryProduk().toUpperCase().contains(charSequence)){

                    filtermodel.add(filterlist.get(i));
                }
            }
            results.count = filtermodel.size();
            results.values = filtermodel;
        }else {
            results.count = filterlist.size();
            results.values = filterlist;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapterProduk.produkList = (ArrayList<ModelProduk>) filterResults.values;

        adapterProduk.notifyDataSetChanged();

    }
}
