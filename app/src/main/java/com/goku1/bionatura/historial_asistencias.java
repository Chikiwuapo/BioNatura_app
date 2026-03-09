package com.goku1.bionatura;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class historial_asistencias extends AppCompatActivity {

    private RecyclerView recyclerHistorial;
    private HistorialAdapter adapter;
    private List<HistorialAsistencia> listaHistorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_asistencias);

        recyclerHistorial = findViewById(R.id.recyclerHistorial);

        recyclerHistorial.setLayoutManager(new LinearLayoutManager(this));

        listaHistorial = new ArrayList<>();


        listaHistorial.add(new HistorialAsistencia("2026-03-08","08:00","17:00"));
        listaHistorial.add(new HistorialAsistencia("2026-03-07","08:10","17:05"));
        listaHistorial.add(new HistorialAsistencia("2026-03-06","08:05","17:01"));
        listaHistorial.add(new HistorialAsistencia("2026-03-06","08:05","17:01"));
        adapter = new HistorialAdapter(listaHistorial);

        recyclerHistorial.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
    }
}