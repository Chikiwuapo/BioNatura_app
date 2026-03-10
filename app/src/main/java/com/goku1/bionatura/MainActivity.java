package com.goku1.bionatura;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;
import com.goku1.bionatura.models.TopRegistro;
import com.goku1.bionatura.models.UserProfile;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String SESSION_NAME = "session";
    private static final String KEY_TOKEN = "token";

    private UserProfile user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cargarPerfil();
        setupCards();

        Button btnEnviarCorreo = findViewById(R.id.btnEnviarCorreo);
        btnEnviarCorreo.setOnClickListener(v -> enviarCorreo());
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarTopRegistro();
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

                    TextView txtdistrito = findViewById(R.id.txtdistrito);
                    txtdistrito.setText("Distrito: " + user.getDistrito());

                    TextView tv_greeting = findViewById(R.id.tv_greeting);
                    tv_greeting.setText("Hola, " + user.getPrimernombre());

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

    private void cargarTopRegistro() {

        SharedPreferences prefs = getSharedPreferences(SESSION_NAME, MODE_PRIVATE);
        String token = prefs.getString(KEY_TOKEN, null);

        if (token == null || token.isEmpty()) return;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<TopRegistro> call = apiService.getTopRegistro("Bearer " + token);

        call.enqueue(new Callback<TopRegistro>() {

            @Override
            public void onResponse(Call<TopRegistro> call, Response<TopRegistro> response) {

                if (response.isSuccessful() && response.body() != null) {

                    TopRegistro top = response.body();

                    TextView txtRecolectado = findViewById(R.id.txtrecolectado);
                    TextView txtRegistros = findViewById(R.id.txtregistros);
                    TextView txtFrecuente = findViewById(R.id.txtfrecuente);

                    txtRecolectado.setText(top.getTotalCantidad() + " KG");
                    txtRegistros.setText(String.valueOf(top.getTotalRegistros()));
                    txtFrecuente.setText(top.getMaterial());

                } else {
                    mostrarToast("No se pudo obtener el top registro");
                }
            }

            @Override
            public void onFailure(Call<TopRegistro> call, Throwable t) {
                t.printStackTrace();
                mostrarToast("Error al conectar con el servidor");
            }
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }

    private void enviarCorreo() {

        if (user == null || user.getCorreo() == null || user.getCorreo().isEmpty()) {
            mostrarToast("No se pudo obtener el correo del usuario");
            return;
        }

        Intent correo = new Intent(Intent.ACTION_SENDTO);
        correo.setData(Uri.parse("mailto:"));
        correo.putExtra(Intent.EXTRA_EMAIL, new String[]{user.getCorreo()});
        correo.putExtra(Intent.EXTRA_SUBJECT, "Asunto del correo");
        correo.putExtra(Intent.EXTRA_TEXT, "Hola " + user.getPrimernombre() + ", este es un mensaje desde la app.");

        Intent chooser = Intent.createChooser(correo, "Enviar correo usando");

        if (correo.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        } else {
            mostrarToast("No hay aplicación de correo instalada");
        }
    }

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