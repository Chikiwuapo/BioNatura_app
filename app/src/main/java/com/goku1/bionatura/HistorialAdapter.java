package com.goku1.bionatura;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {
    private List<HistorialAsistencia> lista;

    public HistorialAdapter(List<HistorialAsistencia> lista) {
        this.lista = lista;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_historial_lista,
                parent,
                false
        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HistorialAsistencia item = lista.get(position);

        // Fecha
        holder.txtFecha.setText("Fecha: " + item.getFecha());

        // Entrada
        if (item.getHoraEntrada() != null && !item.getHoraEntrada().isEmpty()) {
            holder.txtEntrada.setText("Entrada: " + item.getHoraEntrada().split("T")[1].substring(0, 8));
        } else {
            holder.txtEntrada.setText("Entrada: No marcado");
        }

        // Salida
        if (item.getHoraSalida() != null && !item.getHoraSalida().isEmpty()) {
            holder.txtSalida.setText("Salida: " + item.getHoraSalida().split("T")[1].substring(0, 8));
        } else {
            holder.txtSalida.setText("Salida: No marcado");
        }

        // Calcular horas trabajadas solo si hay horaSalida
        if (item.getHoraEntrada() != null && !item.getHoraEntrada().isEmpty() &&
                item.getHoraSalida() != null && !item.getHoraSalida().isEmpty()) {
            try {
                String horaEntradaStr = item.getHoraEntrada().split("T")[1].substring(0, 8);
                String horaSalidaStr = item.getHoraSalida().split("T")[1].substring(0, 8);

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                Date entrada = sdf.parse(horaEntradaStr);
                Date salida = sdf.parse(horaSalidaStr);

                long diffMillis = salida.getTime() - entrada.getTime();
                long horas = diffMillis / (1000 * 60 * 60);
                long minutos = (diffMillis / (1000 * 60)) % 60;

                holder.txtHoras.setText("Horas trabajadas: " + horas + "h " + minutos + "m");
            } catch (Exception e) {
                holder.txtHoras.setText("");
            }
        } else {
            holder.txtHoras.setText(""); // No mostrar si falta salida
        }

        // Distrito
        holder.txtDistrito.setText("Lugar asignado: " + (item.getDistrito() != null ? item.getDistrito() : ""));

        // Estado
        String estado = item.getEstado() != null ? item.getEstado().toUpperCase() : "";
        holder.txtEstado.setText(estado);

        // Cambiar color según estado
        switch (estado) {
            case "TARDANZA":
                holder.txtEstado.setBackgroundColor(Color.parseColor("#FFA500")); // Naranja
                holder.txtEstado.setTextColor(Color.BLACK);
                break;
            case "FALTA":
                holder.txtEstado.setBackgroundColor(Color.parseColor("#FF0000")); // Rojo
                holder.txtEstado.setTextColor(Color.WHITE);
                break;
            default:
                holder.txtEstado.setBackgroundColor(Color.parseColor("#2E7D32")); // Verde
                holder.txtEstado.setTextColor(Color.WHITE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtFecha, txtEstado, txtDistrito, txtHoras;
        TextView txtEntrada, txtSalida;

        public ViewHolder(View itemView) {
            super(itemView);

            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtEntrada = itemView.findViewById(R.id.txtEntrada);
            txtSalida = itemView.findViewById(R.id.txtSalida);
            txtDistrito = itemView.findViewById(R.id.txtDistrito);
            txtHoras = itemView.findViewById(R.id.txtHoras);
            txtEstado = itemView.findViewById(R.id.txtEstado);
        }
    }
}