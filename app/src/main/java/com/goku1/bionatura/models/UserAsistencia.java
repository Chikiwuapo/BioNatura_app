package com.goku1.bionatura.models;

import com.google.gson.annotations.SerializedName;

public class UserAsistencia {
    @SerializedName("hora_entrada")
    private String hora_entrada; // Formato "YYYY-MM-DD HH:MM:SS"

    @SerializedName("id_user")
    private String id_user;

    @SerializedName("id_estado_asistencia")
    private int id_estado_asistencia;

    public UserAsistencia(String id_user, int id_estado_asistencia, String hora_entrada) {
        this.id_user = id_user;
        this.id_estado_asistencia = id_estado_asistencia;
        this.hora_entrada = hora_entrada;
    }

    // Getters
    public String getHora_entrada() { return hora_entrada; }
    public String getId_user() { return id_user; }
    public int getId_estado_asistencia() { return id_estado_asistencia; }
}
