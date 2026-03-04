package com.goku1.bionatura.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class UserDate implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("cantidad")
    private String cantidad;

    @SerializedName("primernombre")
    private String primernombre;

    @SerializedName("segundo_nombre")
    private String segundo_nombre;

    @SerializedName("material")
    private String material;

    @SerializedName("nota")
    private String nota;

    @SerializedName("distrito")
    private String distrito;

    @SerializedName("fecha_registro")
    private String fecha_registro;

    // Getters
    public int getId() { return id; }
    public String getCantidad() { return cantidad; }
    public String getPrimernombre() { return primernombre; }
    public String getSegundo_nombre() { return segundo_nombre; }
    public String getMaterial() { return material; }
    public String getNota() { return nota; }
    public String getDistrito() { return distrito; }
    public String getFecha_registro() { return fecha_registro; }
}