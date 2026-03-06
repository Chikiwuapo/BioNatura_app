package com.goku1.bionatura;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class asistencia extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_registrar);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_inicio) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_historial) {
                    startActivity(new Intent(this, historial_asistencias.class));
                    finish();
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_registrar) {
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

            android.app.Dialog dialog = new android.app.Dialog(asistencia.this);
            dialog.setContentView(R.layout.dialog_calendario);

            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                );
            }

            dialog.show();
        });
        findViewById(R.id.btnHistorial).setOnClickListener(v -> {
            startActivity(new Intent(this, historial_asistencias.class));
        });
    }
}
