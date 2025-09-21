package com.example.aplikasiumkm.models;

public class ModelPesananPengguna {

    String pesananId, pesananTime, pesananStatus, pesanantotalHarga, pesananBy, pesananTo;

    public ModelPesananPengguna() {

    }

    public ModelPesananPengguna(String pesananId, String pesananTime, String pesananStatus
            , String pesanantotalHarga, String pesananBy, String pesananTo) {
        this.pesananId = pesananId;
        this.pesananTime = pesananTime;
        this.pesananStatus = pesananStatus;
        this.pesanantotalHarga = pesanantotalHarga;
        this.pesananBy = pesananBy;
        this.pesananTo = pesananTo;
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
}
