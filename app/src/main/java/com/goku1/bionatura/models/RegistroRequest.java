package com.goku1.bionatura.models;

import com.google.gson.annotations.SerializedName;

public class RegistroRequest {
    @SerializedName("cantidad")
    private double cantidad;
    @SerializedName("nota")
    private String nota;
    @SerializedName("id_material")
    private int id_material;
    @SerializedName("id_user")
    private String id_user;
    @SerializedName("id_distrito")
    private int id_distrito;

    public RegistroRequest(double cantidad, String nota, int id_material, String id_user, int id_distrito) {
        this.cantidad = cantidad;
        this.nota = nota;
        this.id_material = id_material;
        this.id_user = id_user;
        this.id_distrito = id_distrito;
    }
}
