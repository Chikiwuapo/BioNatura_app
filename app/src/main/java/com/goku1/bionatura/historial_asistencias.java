package com.goku1.bionatura;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class historial_asistencias extends AppCompatActivity {

    private RecyclerView recyclerHistorial;
    private HistorialAdapter adapter;
    private List<HistorialAsistencia> listaHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_asistencias);

        // Inicializar RecyclerView
        recyclerHistorial = findViewById(R.id.recyclerHistorial);
        recyclerHistorial.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar lista y adapter
        listaHistorial = new ArrayList<>();
        adapter = new HistorialAdapter(listaHistorial);
        recyclerHistorial.setAdapter(adapter);

        // Ajuste de márgenes por Insets (barras de sistema)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Botón para volver
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        // 🔹 Obtener token desde SharedPreferences
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String token = "Bearer " + prefs.getString("token", "");

        // 🔹 Llamada a la API
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getHistorial(token).enqueue(new Callback<List<HistorialAsistencia>>() {
            @Override
            public void onResponse(Call<List<HistorialAsistencia>> call, Response<List<HistorialAsistencia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaHistorial.clear();
                    listaHistorial.addAll(response.body());

                    Log.d("API_RESPONSE", "Recibidos " + listaHistorial.size() + " registros");
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("API_RESPONSE", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<HistorialAsistencia>> call, Throwable t) {
                Log.e("API_RESPONSE", "Fallo en la llamada", t);
            }
        });
    }
}