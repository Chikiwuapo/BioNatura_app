package com.goku1.bionatura;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;

import com.goku1.bionatura.models.UserDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class historial extends AppCompatActivity {

    private static final String SESSION_NAME = "session";
    private static final String KEY_TOKEN = "token";

    private List<UserDate> registros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historial);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cargarRegistros();
        //findViewById(R.id.btnRegresar).setOnClickListener(v -> finish());
    }

    private void cargarRegistros() {
        SharedPreferences prefs = getSharedPreferences(SESSION_NAME, MODE_PRIVATE);
        String token = prefs.getString(KEY_TOKEN, null);

        if (token == null || token.isEmpty()) {
            redirectToLogin();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<UserDate>> call = apiService.getRegistros("Bearer " + token);

        call.enqueue(new Callback<List<UserDate>>() {
            @Override
            public void onResponse(Call<List<UserDate>> call, Response<List<UserDate>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    registros = response.body();
                    mostrarRegistros();
                } else {
                    redirectToLogin();
                }
            }

            @Override
            public void onFailure(Call<List<UserDate>> call, Throwable t) {
                t.printStackTrace();
                redirectToLogin();
            }
        });
    }

    private void mostrarRegistros() {
        LinearLayout container = findViewById(R.id.container);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (UserDate registro : registros) {
            View componente = inflater.inflate(R.layout.item_historial, container, false);

            TextView text_title = componente.findViewById(R.id.text_title);
            TextView text_subtitle = componente.findViewById(R.id.text_subtitle);
            TextView txtnameuser = componente.findViewById(R.id.txtnameuser);
            TextView txtnota = componente.findViewById(R.id.txtnota);
            TextView txtdate = componente.findViewById(R.id.txtdate);
            TextView text_value = componente.findViewById(R.id.text_value);

            text_title.setText("Residuo " + registro.getMaterial());
            text_subtitle.setText(registro.getDistrito());
            txtnameuser.setText(registro.getPrimernombre() + " " + registro.getSegundo_nombre());
            txtnota.setText("Obs: " + registro.getNota());
            txtdate.setText("Fecha: " + formatearFecha(registro.getFecha_registro()));
            text_value.setText(registro.getCantidad() + " kg");

            CardView card = componente.findViewById(R.id.card_root);
            LinearLayout extraInfo = componente.findViewById(R.id.extra_info);

            card.setOnClickListener(v -> {
                AutoTransition transition = new AutoTransition();
                transition.setDuration(200);
                TransitionManager.beginDelayedTransition((ViewGroup) componente, transition);

                if (extraInfo.getVisibility() == View.GONE) {
                    extraInfo.setVisibility(View.VISIBLE);
                } else {
                    extraInfo.setVisibility(View.GONE);
                }
            });
            container.addView(componente);
        }
    }
    private void redirectToLogin() {
        Intent intent = new Intent(historial.this, Login.class);
        startActivity(intent);
        finish();
    }
    private String formatearFecha(String fechaISO) {
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date date = isoFormat.parse(fechaISO);

            // Cambiamos el formato a solo fecha
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return fechaISO; // en caso de error, devolvemos la original
        }
    }
}