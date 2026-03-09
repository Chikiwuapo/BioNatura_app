package com.goku1.bionatura;

public class HistorialAsistencia {

    private String fecha;
    private String horaEntrada;
    private String horaSalida;

    public HistorialAsistencia(String fecha, String horaEntrada, String horaSalida) {
        this.fecha = fecha;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHoraEntrada() {
        return horaEntrada;
    }

    public String getHoraSalida() {
        return horaSalida;
    }
}