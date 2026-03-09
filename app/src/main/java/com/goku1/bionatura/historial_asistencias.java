package com.goku1.bionatura;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class historial_asistencias extends AppCompatActivity {

    RecyclerView recyclerView;
    HistorialAdapter adapter;
    List<HistorialItem> lista;

    ImageButton btnLista, btnGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_asistencias);

        recyclerView = findViewById(R.id.recyclerHistorial);
        btnLista = findViewById(R.id.btnLista);
        btnGrid = findViewById(R.id.btnGrid);

        lista = new ArrayList<>();

        // DATOS SIMULADOS
        lista.add(new HistorialItem("04 Mayo 2026","9:16 AM","4:40 PM","San Juan de Lurigancho","8H","TARDANZA"));
        lista.add(new HistorialItem("15 Mayo 2026","9:40 AM","4:40 PM","La Molina","8H","TARDANZA"));
        lista.add(new HistorialItem("15 Febrero 2026","8:40 AM","4:40 PM","Comas","8H","PUNTUAL"));
        lista.add(new HistorialItem("15 Abril 2026","8:40 AM","4:40 PM","Los Olivos","8H","PUNTUAL"));

        adapter = new HistorialAdapter(lista);

        // VISTA POR DEFECTO LISTA
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // BOTON LISTA
        btnLista.setOnClickListener(v -> {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        });

        // BOTON GRID
        btnGrid.setOnClickListener(v -> {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_historial);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_inicio) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                    overridePendingTransition(0, 0);
                    return true;

                } else if (id == R.id.nav_historial) {
                    return true;

                } else if (id == R.id.nav_registrar) {
                    startActivity(new Intent(this, asistencia.class));
                    finish();
                    overridePendingTransition(0, 0);
                    return true;

                } else if (id == R.id.nav_perfil) {
                    startActivity(new Intent(this, perfil.class));
                    finish();
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            });
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        findViewById(R.id.btnCalendario).setOnClickListener(v -> {

            android.app.Dialog dialog = new android.app.Dialog(historial_asistencias.this);
            dialog.setContentView(R.layout.dialog_calendario);

            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                );
            }

            CalendarView calendarView = dialog.findViewById(R.id.calendarView);
            calendarView.setDate(System.currentTimeMillis(), true, true);

            ImageView btnCerrar = dialog.findViewById(R.id.btnCerrar);
            btnCerrar.setOnClickListener(v2 -> dialog.dismiss());

            dialog.show();
        });
    }
}