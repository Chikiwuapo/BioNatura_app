package com.goku1.bionatura.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class UserProfile implements Serializable {

    @SerializedName("id_usuario")
    private String id_usuario;

    @SerializedName("dni")
    private String dni;

    @SerializedName("primernombre")
    private String primernombre;

    @SerializedName("segundo_nombre")
    private String segundo_nombre;

    @SerializedName("primer_apellido")
    private String primer_apellido;

    @SerializedName("segundo_apellido")
    private String segundo_apellido;

    @SerializedName("correo")
    private String correo;

    @SerializedName("nro_telefono")
    private String nro_telefono;

    @SerializedName("distrito")
    private String distrito;

    @SerializedName("cargo")
    private String cargo;

    @SerializedName("area")
    private String area;

    @SerializedName("fecha_registro")
    private String fecha_registro;

    // Getters
    public String getId_usuario() { return id_usuario; }
    public String getDni() { return dni; }
    public String getPrimernombre() { return primernombre; }
    public String getSegundo_nombre() { return segundo_nombre; }
    public String getPrimer_apellido() { return primer_apellido; }
    public String getSegundo_apellido() { return segundo_apellido; }
    public String getCorreo() { return correo; }
    public String getNro_telefono() { return nro_telefono; }
    public String getDistrito() { return distrito; }
    public String getCargo() { return cargo; }
    public String getArea() { return area; }
    public String getFecha_registro() { return fecha_registro; }
}