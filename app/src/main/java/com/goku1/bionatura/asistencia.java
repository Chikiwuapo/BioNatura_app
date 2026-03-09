package com.goku1.bionatura;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.goku1.bionatura.models.UserAsistencia;
import com.goku1.bionatura.models.AsistenciaPendiente;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class asistencia extends AppCompatActivity {

    private Button btnMarcarAsistencia;
    Button btnHistorialAsis;
    private boolean esEntrada; // Estado del botón
    private static final String PREFS_ASISTENCIA = "asistencia_session";
    private static final String KEY_PENDIENTE_SALIDA = "pendienteSalida";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia);

        btnMarcarAsistencia = findViewById(R.id.btnMarcarAsistencia);
        btnHistorialAsis = findViewById(R.id.btnHistorialAsis);
        btnHistorialAsis.setOnClickListener(view -> changeToHistorialAsistencia());

        // Recuperar estado guardado local
        SharedPreferences prefs = getSharedPreferences(PREFS_ASISTENCIA, MODE_PRIVATE);

        // Primero verificamos en la API si hay pendiente
        SharedPreferences sessionPrefs = getSharedPreferences("session", MODE_PRIVATE);
        String token = sessionPrefs.getString("token", null);

        if (token != null && !token.isEmpty()) {
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<AsistenciaPendiente> call = apiService.verificarPendiente("Bearer " + token);
            call.enqueue(new Callback<AsistenciaPendiente>() {
                @Override
                public void onResponse(Call<AsistenciaPendiente> call, Response<AsistenciaPendiente> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        boolean pendiente = response.body().isPendienteSalida();
                        esEntrada = !pendiente; // Si hay pendiente, esEntrada = false
                        btnMarcarAsistencia.setText(esEntrada ? "Marcar Entrada" : "Marcar Salida");

                        // Guardar en SharedPreferences local
                        prefs.edit().putBoolean(KEY_PENDIENTE_SALIDA, pendiente).apply();
                    } else {
                        esEntrada = true;
                        btnMarcarAsistencia.setText("Marcar Entrada");
                    }
                }

                @Override
                public void onFailure(Call<AsistenciaPendiente> call, Throwable t) {
                    esEntrada = true;
                    btnMarcarAsistencia.setText("Marcar Entrada");
                }
            });
        } else {
            esEntrada = true;
            btnMarcarAsistencia.setText("Marcar Entrada");
        }

        // Click del botón
        btnMarcarAsistencia.setOnClickListener(view -> {
            if (esEntrada) {
                registrarEntrada();
            } else {
                registrarSalida();
            }
        });
    }

    // ---------------------
    // Registrar Entrada
    private void registrarEntrada() {
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String token = prefs.getString("token", null);
        if (token == null || token.isEmpty()) return;

        String horaActual = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());

        String idUser = getIdUserFromToken(token);
        if (idUser == null) {
            Toast.makeText(this, "No se pudo obtener el ID del usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        UserAsistencia asistencia = new UserAsistencia(idUser, 1, horaActual);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.registrarAsistencia(asistencia);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Entrada registrada", Toast.LENGTH_SHORT).show();
                    esEntrada = false;
                    btnMarcarAsistencia.setText("Marcar Salida");

                    // Guardar estado pendiente de salida
                    getSharedPreferences(PREFS_ASISTENCIA, MODE_PRIVATE)
                            .edit()
                            .putBoolean(KEY_PENDIENTE_SALIDA, true)
                            .apply();
                } else {
                    Toast.makeText(getApplicationContext(), "Error al registrar entrada", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ---------------------
    // Registrar Salida
    private void registrarSalida() {
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String token = prefs.getString("token", null);
        if (token == null || token.isEmpty()) return;

        String idUser = getIdUserFromToken(token);
        if (idUser == null) {
            Toast.makeText(this, "No se pudo obtener el ID del usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        // Enviar como JSON { "id_user": "..." }
        Map<String, String> body = new HashMap<>();
        body.put("id_user", idUser);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.registrarSalida("Bearer " + token, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Salida registrada", Toast.LENGTH_SHORT).show();
                    esEntrada = true;
                    btnMarcarAsistencia.setText("Marcar Entrada");

                    // Limpiar estado pendiente de salida
                    getSharedPreferences(PREFS_ASISTENCIA, MODE_PRIVATE)
                            .edit()
                            .putBoolean(KEY_PENDIENTE_SALIDA, false)
                            .apply();
                } else {
                    Toast.makeText(getApplicationContext(), "Error al registrar salida", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ---------------------
    // Obtener id del JWT
    private String getIdUserFromToken(String jwtToken) {
        try {
            String[] parts = jwtToken.split("\\.");
            String payload = new String(Base64.decode(parts[1], Base64.URL_SAFE), "UTF-8");
            JSONObject jsonObject = new JSONObject(payload);
            return jsonObject.getString("id"); // 🔑 coincide con tu middleware de Node.js
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void changeToHistorialAsistencia(){
        Intent intent = new Intent(asistencia.this, historial_asistencias.class);
        startActivity(intent);
    }
}