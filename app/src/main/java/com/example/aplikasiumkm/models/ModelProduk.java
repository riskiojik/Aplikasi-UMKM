package com.example.aplikasiumkm.models;

public class ModelProduk {
    private String productId, productTitle, deskripsiProduk, categoryProduk, produkQuantity,
                    originalPrice, diskonPrice, diskonPriceNote, diskonAvailable, produkIcon, timestamp, uid;

    public ModelProduk() {

    }

    public ModelProduk(String productId, String productTitle, String deskripsiProduk, String categoryProduk, String produkQuantity, String originalPrice, String diskonPrice, String diskonPriceNote, String diskonAvailable,
                       String produkIcon, String timestamp, String uid) {

        this.productId = productId;
        this.productTitle = productTitle;
        this.deskripsiProduk = deskripsiProduk;
        this.categoryProduk = categoryProduk;
        this.produkQuantity = produkQuantity;
        this.originalPrice = originalPrice;
        this.diskonPrice = diskonPrice;
        this.diskonPriceNote = diskonPriceNote;
        this.diskonAvailable = diskonAvailable;
        this.produkIcon = produkIcon;
        this.timestamp = timestamp;
        this.uid = uid;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getDeskripsiProduk() {
        return deskripsiProduk;
    }

    public void setDeskripsiProduk(String deskripsiProduk) {
        this.deskripsiProduk = deskripsiProduk;
    }

    public String getCategoryProduk() {
        return categoryProduk;
    }

    public void setCategoryProduk(String categoryProduk) {
        this.categoryProduk = categoryProduk;
    }

    public String getProdukQuantity() {
        return produkQuantity;
    }

    public void setProdukQuantity(String produkQuantity) {
        this.produkQuantity = produkQuantity;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getDiskonPrice() {
        return diskonPrice;
    }

    public void setDiskonPrice(String diskonPrice) {
        this.diskonPrice = diskonPrice;
    }

    public String getDiskonPriceNote() {
        return diskonPriceNote;
    }

    public void setDiskonPriceNote(String diskonPriceNote) {
        this.diskonPriceNote = diskonPriceNote;
    }

    public String getDiskonAvailable() {
        return diskonAvailable;
    }

    public void setDiskonAvailable(String diskonAvailable) {
        this.diskonAvailable = diskonAvailable;
    }

    public String getProdukIcon() {
        return produkIcon;
    }

    public void setProdukIcon(String produkIcon) {
        this.produkIcon = produkIcon;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
