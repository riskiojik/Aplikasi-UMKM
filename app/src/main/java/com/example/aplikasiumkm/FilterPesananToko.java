package com.example.aplikasiumkm;

import android.widget.Filter;

import com.example.aplikasiumkm.adapter.AdapterPesananToko;
import com.example.aplikasiumkm.models.ModelPesananToko;

import java.util.ArrayList;

public class FilterPesananToko extends Filter {

    private final AdapterPesananToko adapter;
    private final ArrayList<ModelPesananToko> filterList;

    public FilterPesananToko(AdapterPesananToko adapter, ArrayList<ModelPesananToko> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();

        if (charSequence != null && charSequence.length() > 0) {
            String query = charSequence.toString().toLowerCase().trim();
            ArrayList<ModelPesananToko> filteredList = new ArrayList<>();

            for (ModelPesananToko item : filterList) {
                if (item.getPesananStatus().toLowerCase().contains(query)) {
                    filteredList.add(item);
                }
            }

            results.count = filteredList.size();
            results.values = filteredList;

        } else {
            results.count = filterList.size();
            results.values = filterList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        // Perhatikan: gunakan nama field yang benar sesuai deklarasi di AdapterPesananToko.java
        adapter.pesananTokoArrayList = (ArrayList<ModelPesananToko>) results.values;
        adapter.notifyDataSetChanged();
    }
}
