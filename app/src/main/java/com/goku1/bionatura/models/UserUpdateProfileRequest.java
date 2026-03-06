package com.goku1.bionatura.models;

public class UserUpdateProfileRequest {
    private String correo, nro_telefono;

    public UserUpdateProfileRequest() {}
    public void setCorreo(String correo) { this.correo = correo; }
    public void setNro_telefono(String nro_telefono) { this.nro_telefono = nro_telefono; }
}
