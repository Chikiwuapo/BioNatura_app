package com.goku1.bionatura;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
    private boolean esEntrada;

    private static final String PREFS_ASISTENCIA = "asistencia_session";
    private static final String KEY_PENDIENTE_SALIDA = "pendienteSalida";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnMarcarAsistencia = findViewById(R.id.btnMarcarAsistencia);
        btnHistorialAsis = findViewById(R.id.btnHistorialAsis);

        btnHistorialAsis.setOnClickListener(view -> changeToHistorialAsistencia());

        findViewById(R.id.btnBackDashboard).setOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences(PREFS_ASISTENCIA, MODE_PRIVATE);
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
                        esEntrada = !pendiente;

                        btnMarcarAsistencia.setText(esEntrada ? "Marcar Entrada" : "Marcar Salida");

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

        btnMarcarAsistencia.setOnClickListener(view -> {

            if (esEntrada) {
                registrarEntrada();
            } else {
                registrarSalida();
            }

        });
    }

    // REGISTRAR ENTRADA
    private void registrarEntrada() {

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if (token == null || token.isEmpty()) return;

        Date ahora = new Date();

        String horaActualStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(ahora);

        int estadoLocal = 1;

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

            Date horaLimite = sdf.parse("08:01");
            Date horaAhora = sdf.parse(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(ahora));

            if (horaAhora.after(horaLimite)) {
                estadoLocal = 2;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        final int estado = estadoLocal;

        String idUser = getIdUserFromToken(token);

        if (idUser == null) {
            mostrarToast("No se pudo obtener el ID del usuario");
            return;
        }

        UserAsistencia asistencia = new UserAsistencia(idUser, estado, horaActualStr);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.registrarAsistencia(asistencia);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {

                    String mensaje = (estado == 1)
                            ? "Entrada registrada"
                            : "Entrada registrada como TARDANZA";

                    mostrarToast(mensaje);

                    esEntrada = false;
                    btnMarcarAsistencia.setText("Marcar Salida");

                    getSharedPreferences(PREFS_ASISTENCIA, MODE_PRIVATE)
                            .edit()
                            .putBoolean(KEY_PENDIENTE_SALIDA, true)
                            .apply();

                } else {

                    mostrarToast("Error al registrar entrada");

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                mostrarToast("Error de red: " + t.getMessage());

            }
        });
    }
    // REGISTRAR SALIDA
    private void registrarSalida() {

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if (token == null || token.isEmpty()) return;

        String idUser = getIdUserFromToken(token);

        if (idUser == null) {
            mostrarToast("No se pudo obtener el ID del usuario");
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("id_user", idUser);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<ResponseBody> call = apiService.registrarSalida("Bearer " + token, body);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {

                    mostrarToast("Salida registrada");

                    esEntrada = true;
                    btnMarcarAsistencia.setText("Marcar Entrada");

                    getSharedPreferences(PREFS_ASISTENCIA, MODE_PRIVATE)
                            .edit()
                            .putBoolean(KEY_PENDIENTE_SALIDA, false)
                            .apply();

                } else {

                    mostrarToast("Error al registrar salida");

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                mostrarToast("Error de red: " + t.getMessage());

            }
        });
    }
    // OBTENER ID DEL TOKEN
    private String getIdUserFromToken(String jwtToken) {

        try {

            String[] parts = jwtToken.split("\\.");
            String payload = new String(Base64.decode(parts[1], Base64.URL_SAFE), "UTF-8");
            JSONObject jsonObject = new JSONObject(payload);
            return jsonObject.getString("id");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void changeToHistorialAsistencia() {

        Intent intent = new Intent(asistencia.this, historial_asistencias.class);
        startActivity(intent);
    }

    // TOAST PERSONALIZADO
    public void mostrarToast(String mensaje){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, null);

        TextView txt = layout.findViewById(R.id.txtMensaje);
        txt.setText(mensaje);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);

        toast.setGravity(android.view.Gravity.TOP | android.view.Gravity.END, 30, 120);

        toast.show();
    }
}