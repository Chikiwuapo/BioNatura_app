package com.goku1.bionatura.models;

public class SalidaRequest {
    private String id_user;

    public SalidaRequest(String id_user) {
        this.id_user = id_user;
    }

    public String getId_user() { return id_user; }
    public void setId_user(String id_user) { this.id_user = id_user; }
}
