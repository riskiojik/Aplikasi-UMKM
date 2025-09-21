package com.example.aplikasiumkm.models;

public class ModelItemPesanan {
    private String pId, produkTitle, harga, totalHarga, quantity;

    public ModelItemPesanan() {
    }

    public ModelItemPesanan(String pId, String produkTitle, String harga, String totalHarga, String quantity) {
        this.pId = pId;
        this.produkTitle = produkTitle;
        this.harga = harga;
        this.totalHarga = totalHarga;
        this.quantity = quantity;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getProdukTitle() {
        return produkTitle;
    }

    public void setProdukTitle(String produkTitle) {
        this.produkTitle = produkTitle;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(String totalHarga) {
        this.totalHarga = totalHarga;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
