package com.goku1.bionatura;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

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

        holder.txtFecha.setText("Fecha: " + item.getFecha());
        holder.txtEntrada.setText("Entrada: " + item.getHoraEntrada());
        holder.txtSalida.setText("Salida: " + item.getHoraSalida());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtFecha;
        TextView txtEntrada;
        TextView txtSalida;

        public ViewHolder(View itemView) {
            super(itemView);

            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtEntrada = itemView.findViewById(R.id.txtEntrada);
            txtSalida = itemView.findViewById(R.id.txtSalida);
        }
    }
}