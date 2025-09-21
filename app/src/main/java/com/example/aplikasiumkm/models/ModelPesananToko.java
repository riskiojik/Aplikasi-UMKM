package com.example.aplikasiumkm.models;

public class ModelPesananToko {
    String pesananId, pesananTime, pesananStatus, pesanantotalHarga, pesananBy, pesananTo, latitude, longitude, DeliveryFeel;

    public ModelPesananToko() {
    }

    public ModelPesananToko(String pesananId, String pesananTime, String pesananStatus, String pesanantotalHarga, String pesananBy, String pesananTo
            , String latitude, String longitude, String deliveryFeel) {
        this.pesananId = pesananId;
        this.pesananTime = pesananTime;
        this.pesananStatus = pesananStatus;
        this.pesanantotalHarga = pesanantotalHarga;
        this.pesananBy = pesananBy;
        this.pesananTo = pesananTo;
        this.latitude = latitude;
        this.longitude = longitude;
        DeliveryFeel = deliveryFeel;
    }

    public String getPesananId() {
        return pesananId;
    }

    public void setPesananId(String pesananId) {
        this.pesananId = pesananId;
    }

    public String getPesananTime() {
        return pesananTime;
    }

    public void setPesananTime(String pesananTime) {
        this.pesananTime = pesananTime;
    }

    public String getPesananStatus() {
        return pesananStatus;
    }

    public void setPesananStatus(String pesananStatus) {
        this.pesananStatus = pesananStatus;
    }

    public String getPesanantotalHarga() {
        return pesanantotalHarga;
    }

    public void setPesanantotalHarga(String pesanantotalHarga) {
        this.pesanantotalHarga = pesanantotalHarga;
    }

    public String getPesananBy() {
        return pesananBy;
    }

    public void setPesananBy(String pesananBy) {
        this.pesananBy = pesananBy;
    }

    public String getPesananTo() {
        return pesananTo;
    }

    public void setPesananTo(String pesananTo) {
        this.pesananTo = pesananTo;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDeliveryFeel() {
        return DeliveryFeel;
    }

    public void setDeliveryFeel(String deliveryFeel) {
        DeliveryFeel = deliveryFeel;
    }
}
