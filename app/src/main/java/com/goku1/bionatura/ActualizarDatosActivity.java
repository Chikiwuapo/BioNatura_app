package com.goku1.bionatura;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.goku1.bionatura.models.UserProfile;
import com.goku1.bionatura.models.UserUpdateProfileRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActualizarDatosActivity extends AppCompatActivity {

    private ImageButton btnVolver;
    private Button btnCancelar, btnActualizar;
    private EditText email1, email2, telefono;
    private UserProfile user;
    private static final String SESSION_NAME = "session";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_datos);

        inicializarVistas();

        user = (UserProfile) getIntent().getSerializableExtra("usuario");

        if (user != null) {
            email1.setText(user.getCorreo());
            email2.setText(user.getCorreo());
            telefono.setText(user.getNro_telefono());
        }

        btnVolver.setOnClickListener(v -> finish());
        btnCancelar.setOnClickListener(v -> finish());
        btnActualizar.setOnClickListener(v -> validarYEnviar());
    }

    private void inicializarVistas() {
        btnVolver = findViewById(R.id.btnVolver);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnActualizar = findViewById(R.id.btnActualizar);
        email1 = findViewById(R.id.txtemail1);
        email2 = findViewById(R.id.txtemail2);
        telefono = findViewById(R.id.txtnumero);
    }

    private void validarYEnviar() {
        String mail = email1.getText().toString().trim();
        String confirmMail = email2.getText().toString().trim();
        String tel = telefono.getText().toString().trim();

        if (mail.isEmpty() || tel.isEmpty()) {
            Toast.makeText(this, "Los campos no pueden estar vacíos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mail.equals(confirmMail)) {
            email2.setError("Los correos no coinciden");
            return;
        }

        ejecutarActualizacion(mail, tel);
    }

    private void ejecutarActualizacion(String mail, String tel) {
        UserUpdateProfileRequest request = new UserUpdateProfileRequest();
        request.setCorreo(mail);
        request.setNro_telefono(tel);

        SharedPreferences prefs = getSharedPreferences(SESSION_NAME, MODE_PRIVATE);
        String token = prefs.getString("token", "");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.actualizarPerfil("Bearer " + token, request);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ActualizarDatosActivity.this, "Datos actualizados exitosamente", Toast.LENGTH_SHORT).show();
                    finish(); // Regresa al perfil, donde onResume refrescará todo automáticamente
                } else {
                    Toast.makeText(ActualizarDatosActivity.this, "Error al actualizar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ActualizarDatosActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}