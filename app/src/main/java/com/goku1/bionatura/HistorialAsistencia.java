package com.goku1.bionatura;

public class HistorialAsistencia {

    private String hora_entrada;
    private String hora_salida;
    private String estado;
    private String distrito;

    // Constructor
    public HistorialAsistencia(String hora_entrada, String hora_salida, String estado, String distrito) {
        this.hora_entrada = hora_entrada;
        this.hora_salida = hora_salida;
        this.estado = estado;
        this.distrito = distrito;
    }

    // Getters
    public String getHoraEntrada() { return hora_entrada; }
    public String getHoraSalida() { return hora_salida; }
    public String getEstado() { return estado; }
    public String getDistrito() { return distrito; }

    // Para mostrar fecha separada si quieres
    public String getFecha() {
        // Extrae la parte de la fecha de "hora_entrada"
        if (hora_entrada != null && hora_entrada.contains("T")) {
            return hora_entrada.split("T")[0];
        } else {
            return "";
        }
    }
}