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
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class perfil extends AppCompatActivity {

    Button btnCerrarSesion, btnEditar, btnCambiar;
    TextView txtNombre,Cargo,txtprofileemail,txtphonenumber,txtarea,txtfechaingreso;
    private UserProfile user;
    private static final String SESSION_NAME = "session";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        btnEditar = findViewById(R.id.btnEditar);
        btnCambiar = findViewById(R.id.btnCambiar);
        txtNombre = findViewById(R.id.txtNombre);
        Cargo = findViewById(R.id.Cargo);
        txtprofileemail = findViewById(R.id.txtprofileemail);
        txtphonenumber = findViewById(R.id.txtphonenumber);
        txtarea = findViewById(R.id.txtarea);
        txtfechaingreso = findViewById(R.id.txtfechaingreso);

        user = (UserProfile) getIntent().getSerializableExtra("usuario");

        // Botón para Editar Perfil
        btnEditar.setOnClickListener(v -> {
            if (user != null) {
                Intent intent = new Intent(perfil.this, ActualizarDatosActivity.class);
                intent.putExtra("usuario", user);
                startActivity(intent);
            }
        });

        // Botón para Cambiar la Contraseña
        btnCambiar.setOnClickListener(v -> {
            if (user != null) {
                Intent intent = new Intent(perfil.this, ActualizarContrasena.class);
                intent.putExtra("usuario", user);
                startActivity(intent);
            }
        });

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
        btnCerrarSesion.setOnClickListener(view -> cerrarSesion());


        configurarInsets();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Carga los datos frescos cada vez que el usuario vuelve a esta pantalla
        refrescarDatosPerfil();
    }

    private void refrescarDatosPerfil() {
        SharedPreferences prefs = getSharedPreferences(SESSION_NAME, MODE_PRIVATE);
        String token = prefs.getString("token", "");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<UserProfile> call = apiService.getProfile("Bearer " + token);

        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user = response.body(); // Actualizamos nuestra variable global
                    actualizarUI(user);     // Refrescamos la vista
                }
            }
            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) { }
        });
    }

    private void actualizarUI(UserProfile user) {
        txtNombre.setText(user.getPrimernombre() + " " + user.getSegundo_nombre());
        Cargo.setText(user.getCargo());
        txtprofileemail.setText(user.getCorreo());
        txtphonenumber.setText(user.getNro_telefono());
        txtarea.setText(user.getArea());
        txtfechaingreso.setText(user.getFecha_registro());
    }


    private void configurarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
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
