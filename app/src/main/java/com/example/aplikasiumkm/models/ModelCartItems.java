package com.example.aplikasiumkm.models;

public class ModelCartItems {
    String id, pId, nama, price, cost, quantity;

    public ModelCartItems() {

    }

    public ModelCartItems(String id, String pId, String nama, String price, String cost, String quantity) {
        this.id = id;
        this.pId = pId;
        this.nama = nama;
        this.price = price;
        this.cost = cost;
        this.quantity = quantity;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
