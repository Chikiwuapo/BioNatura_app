package com.goku1.bionatura.models;

import com.google.gson.annotations.SerializedName;

public class TopRegistro {

    @SerializedName("total_cantidad")
    private double totalCantidad;

    @SerializedName("material")
    private String material;

    @SerializedName("total_registros")
    private int totalRegistros;

    public double getTotalCantidad() {
        return totalCantidad;
    }

    public String getMaterial() {
        return material;
    }

    public int getTotalRegistros() {
        return totalRegistros;
    }
}