package com.goku1.bionatura;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    List<HistorialItem> lista;

    public HistorialAdapter(List<HistorialItem> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historial, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        HistorialItem item = lista.get(position);

        holder.txtFecha.setText(item.fecha);
        holder.txtEntrada.setText("Entrada: " + item.entrada);
        holder.txtSalida.setText("Salida: " + item.salida);
        holder.txtLugar.setText("Lugar asignado: " + item.lugar);
        holder.txtHoras.setText("Horas trabajadas: " + item.horas);
        holder.txtEstado.setText(item.estado);

        // COLOR SEGÚN ESTADO
        switch (item.estado) {

            case "PUNTUAL":
                holder.txtEstado.setBackgroundColor(Color.parseColor("#1DB954"));
                holder.txtEstado.setTextColor(Color.WHITE);
                break;

            case "TARDANZA":
                holder.txtEstado.setBackgroundColor(Color.parseColor("#D4FF00"));
                holder.txtEstado.setTextColor(Color.BLACK);
                break;

            case "FALTA":
                holder.txtEstado.setBackgroundColor(Color.parseColor("#FF6B6B"));
                holder.txtEstado.setTextColor(Color.WHITE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtFecha, txtEntrada, txtSalida, txtLugar, txtHoras, txtEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtEntrada = itemView.findViewById(R.id.txtEntrada);
            txtSalida = itemView.findViewById(R.id.txtSalida);
            txtLugar = itemView.findViewById(R.id.txtLugar);
            txtHoras = itemView.findViewById(R.id.txtHoras);
            txtEstado = itemView.findViewById(R.id.txtEstado);
        }
    }
}