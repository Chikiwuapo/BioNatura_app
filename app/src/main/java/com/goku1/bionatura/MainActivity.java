package com.goku1.bionatura;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.goku1.bionatura.models.UserProfile;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String SESSION_NAME = "session";
    private static final String KEY_TOKEN = "token";

    private UserProfile user; // Guardará datos del usuario

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cargarPerfil();

        setupCards();
    }

    private void setupCards() {
        MaterialCardView cardPerfil = findViewById(R.id.card_perfil);
        MaterialCardView card_historial = findViewById(R.id.card_historial);
        MaterialCardView card_registro = findViewById(R.id.card_registro);
        MaterialCardView card_asistencia = findViewById(R.id.card_asistencia);

        cardPerfil.setOnClickListener(v -> {
            if (user != null) {
                Intent intent = new Intent(MainActivity.this, perfil.class);
                intent.putExtra("usuario", user);
                startActivity(intent);
            }
        });

        card_registro.setOnClickListener(view -> {
            if (user != null) {
                Intent intent = new Intent(MainActivity.this, material_register.class);
                intent.putExtra("usuario", user);
                startActivity(intent);
            }
        });

        card_historial.setOnClickListener(view -> {
            if (user != null) {
                Intent intent = new Intent(MainActivity.this, historial.class);
                intent.putExtra("usuario", user);
                startActivity(intent);
            }
        });
        card_asistencia.setOnClickListener(view -> {
            if (user != null) {
                Intent intent = new Intent(MainActivity.this, asistencia.class);
                intent.putExtra("usuario", user);
                startActivity(intent);
            }
        });

    }

    private void cargarPerfil() {
        SharedPreferences prefs = getSharedPreferences(SESSION_NAME, MODE_PRIVATE);
        String token = prefs.getString(KEY_TOKEN, null);

        if (token == null || token.isEmpty()) {
            redirectToLogin();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<UserProfile> call = apiService.getProfile("Bearer " + token);

        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user = response.body();
                    TextView tv_greeting = findViewById(R.id.tv_greeting);
                    tv_greeting.setText("Hola " + user.getPrimernombre());
                } else {
                    redirectToLogin();
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                t.printStackTrace();
                redirectToLogin();
            }
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }
}