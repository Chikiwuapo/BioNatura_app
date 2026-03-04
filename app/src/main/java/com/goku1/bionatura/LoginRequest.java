package com.goku1.bionatura;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("correo")
    private String correo;

    @SerializedName("contraseña")
    private String contrasena;

    public LoginRequest(String correo, String contrasena) {
        this.correo = correo;
        this.contrasena = contrasena;
    }
}