package com.goku1.bionatura;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.goku1.bionatura.models.UserProfile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.TextView;

public class perfil extends AppCompatActivity {

    Button btnCerrarSesion;
    TextView txtNombre,Cargo,txtprofileemail,txtphonenumber,txtarea,txtfechaingreso;
    private static final String SESSION_NAME = "session";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_perfil);
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
                    startActivity(new Intent(this, asistencia.class));
                    finish();
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_perfil) {
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
        //txtprofileemail,txtphonenumber,txtarea,txtfechaingreso
        txtNombre = findViewById(R.id.txtNombre);
        Cargo = findViewById(R.id.Cargo);
        txtprofileemail = findViewById(R.id.txtprofileemail);
        txtphonenumber = findViewById(R.id.txtphonenumber);
        txtarea = findViewById(R.id.txtarea);
        txtfechaingreso = findViewById(R.id.txtfechaingreso);

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
        btnCerrarSesion.setOnClickListener(view -> cerrarSesion());

        UserProfile user = (UserProfile) getIntent().getSerializableExtra("usuario");

        if (user != null) {
            // 3. Asignar datos a los TextViews
            txtNombre.setText(user.getPrimernombre() + " " + user.getSegundo_nombre());
            Cargo.setText(user.getCargo());
            txtprofileemail.setText(user.getCorreo());
            txtphonenumber.setText(user.getNro_telefono());
            txtarea.setText(user.getArea());
            txtfechaingreso.setText(user.getFecha_registro());
        }
    }
    private void cerrarSesion() {

        // 1. Obtener SharedPreferences
        SharedPreferences prefs = getSharedPreferences(SESSION_NAME, MODE_PRIVATE);

        // 2. Eliminar datos de sesión (token, user_id, etc.)
        prefs.edit().clear().apply();

        // 3. Ir al Login
        Intent intent = new Intent(perfil.this, Login.class);

        // 4. Limpiar el historial de Activities (importante)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
}
