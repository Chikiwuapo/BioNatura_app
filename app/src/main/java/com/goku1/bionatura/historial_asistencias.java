package com.goku1.bionatura;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class historial_asistencias extends AppCompatActivity {

    private RecyclerView recyclerHistorial;
    private HistorialAdapter adapter;
    private List<HistorialAsistencia> listaHistorial;
    private List<HistorialAsistencia> listaOriginal;

    // TextViews del resumen
    private TextView txtTotalAsistencias, txtTotalTardanzas, txtTotalFaltas, txtPorcentajePuntualidad;

    // Botones de filtro
    private MaterialButton btnFiltrarAsistencias, btnFiltrarTardanzas, btnFiltrarTodos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_asistencias);

        recyclerHistorial = findViewById(R.id.recyclerHistorial);
        recyclerHistorial.setLayoutManager(new LinearLayoutManager(this));

        listaHistorial = new ArrayList<>();
        listaOriginal = new ArrayList<>();
        adapter = new HistorialAdapter(listaHistorial);
        recyclerHistorial.setAdapter(adapter);

        txtTotalAsistencias = findViewById(R.id.txtnroasistencias);
        txtTotalTardanzas = findViewById(R.id.txtnrotardanzas);
        txtTotalFaltas = findViewById(R.id.txtnrofaltas);
        txtPorcentajePuntualidad = findViewById(R.id.txtporcentajepuntualidad);

        // Botones de filtro
        btnFiltrarAsistencias = findViewById(R.id.btnFiltrarAsistencias);
        btnFiltrarTardanzas = findViewById(R.id.btnFiltrarTardanzas);
        btnFiltrarTodos = findViewById(R.id.btnFiltrarTodos);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("token", "");

        // Cargar datos desde API
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getHistorial(token).enqueue(new Callback<List<HistorialAsistencia>>() {
            @Override
            public void onResponse(Call<List<HistorialAsistencia>> call, Response<List<HistorialAsistencia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaOriginal.clear();
                    listaOriginal.addAll(response.body());

                    actualizarLista(listaOriginal); // Mostrar todos al inicio
                } else {
                    Log.e("API_RESPONSE", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<HistorialAsistencia>> call, Throwable t) {
                Log.e("API_RESPONSE", "Fallo en la llamada", t);
            }
        });

        // Filtros
        btnFiltrarAsistencias.setOnClickListener(v -> {
            List<HistorialAsistencia> filtrados = new ArrayList<>();
            for (HistorialAsistencia item : listaOriginal) {
                String estado = item.getEstado() != null ? item.getEstado().toUpperCase() : "";
                if (estado.equals("ASISTIÓ")) filtrados.add(item);
            }
            actualizarLista(filtrados);
        });

        btnFiltrarTardanzas.setOnClickListener(v -> {
            List<HistorialAsistencia> filtrados = new ArrayList<>();
            for (HistorialAsistencia item : listaOriginal) {
                String estado = item.getEstado() != null ? item.getEstado().toUpperCase() : "";
                if (estado.equals("TARDANZA")) filtrados.add(item);
            }
            actualizarLista(filtrados);
        });

        btnFiltrarTodos.setOnClickListener(v -> actualizarLista(listaOriginal));
    }

    private void actualizarLista(List<HistorialAsistencia> nuevaLista) {
        listaHistorial.clear();
        listaHistorial.addAll(nuevaLista);
        adapter.notifyDataSetChanged();

        // Calcular totales
        int totalAsistencias = 0;
        int totalTardanzas = 0;
        int totalFaltas = 0;

        for (HistorialAsistencia item : listaHistorial) {
            String estado = item.getEstado() != null ? item.getEstado().toUpperCase() : "";

            if (estado.equals("TARDANZA")) totalTardanzas++;
            else if (estado.equals("FALTA") || item.getHoraEntrada() == null || item.getHoraEntrada().isEmpty()) totalFaltas++;
            else totalAsistencias++;
        }

        txtTotalAsistencias.setText(totalAsistencias + " asistencias");
        txtTotalTardanzas.setText(totalTardanzas + " tardanzas");
        txtTotalFaltas.setText(totalFaltas + " faltas");

        int totalParaPuntualidad = totalAsistencias + totalTardanzas;
        int porcentajePuntualidad = totalParaPuntualidad > 0
                ? (int) ((totalAsistencias * 100.0) / totalParaPuntualidad)
                : 0;

        txtPorcentajePuntualidad.setText(porcentajePuntualidad + "% de puntualidad");
    }
}