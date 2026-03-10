package com.goku1.bionatura;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    EditText inputemail, inputpassword;
    Button btnlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputemail = findViewById(R.id.inputemail);
        inputpassword = findViewById(R.id.inputpassword);
        btnlogin = findViewById(R.id.btnlogin);

        btnlogin.setOnClickListener(v -> {

            String correo = inputemail.getText().toString().trim();
            String contrasena = inputpassword.getText().toString().trim();

            if (correo.isEmpty() || contrasena.isEmpty()) {
                mostrarToast("Completa todos los campos");
                return;
            }

            login(correo, contrasena);
        });
    }

    private void login(String correo, String contrasena) {

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<LoginResponse> call = apiService.login(new LoginRequest(correo, contrasena));

        call.enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    String token = response.body().getToken();

                    getSharedPreferences("session", MODE_PRIVATE)
                            .edit()
                            .putString("token", token)
                            .apply();

                    mostrarToast("Login exitoso");

                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    mostrarToast("Correo o contraseña incorrectos");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                mostrarToast("Error de conexión: " + t.getMessage());
            }
        });
    }

    public void mostrarToast(String mensaje){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, null);

        TextView txt = layout.findViewById(R.id.txtMensaje);
        txt.setText(mensaje);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);

        // posision toast (arriba derecha)
        toast.setGravity(android.view.Gravity.TOP | android.view.Gravity.END, 30, 120);

        toast.show();
    }
}