package com.goku1.bionatura;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.goku1.bionatura.models.UserProfile;
import com.google.android.material.card.MaterialCardView;

import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import com.goku1.bionatura.models.RegistroRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class material_register extends AppCompatActivity {

    private MaterialCardView cardOrganico, cardReciclable, cardPeligroso, cardIndustrial;
    private MaterialCardView[] listaCards;

    private final String[] distritos = {"Seleccione el lugar", "La Molina", "La Victoria", "Chorrillos", "Comas", "Ventanilla"};

    private final int COLOR_VERDE = Color.parseColor("#1B5E20");
    private final int COLOR_GRIS_TEXTO = Color.parseColor("#546E7A");
    private final int COLOR_BORDE_DESACTIVADO = Color.parseColor("#ECEFF1");
    private int materialSeleccionadoId = -1;
    private UserProfile userProfile;
    private EditText etCantidad, etObservaciones;
    private Spinner spinnerDistritos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Recuperamos el usuario enviado desde MainActivity
        userProfile = (UserProfile) getIntent().getSerializableExtra("usuario") ;

        // Mapeamos las variables de entrada
        etCantidad = findViewById(R.id.etCantidad);
        etObservaciones = findViewById(R.id.etObservaciones);
        spinnerDistritos = findViewById(R.id.spinnerDistritos);

        // Clic para el botón de guardar
        findViewById(R.id.btnGuardar).setOnClickListener(v -> validarYEnviar());

        // 1. Mapeamos las tarjetas
        cardOrganico = findViewById(R.id.cardOrganico);
        cardReciclable = findViewById(R.id.cardReciclable);
        cardPeligroso = findViewById(R.id.cardPeligroso);
        cardIndustrial = findViewById(R.id.cardIndustrial);

        listaCards = new MaterialCardView[]{cardOrganico, cardReciclable, cardPeligroso, cardIndustrial};

        for (MaterialCardView card : listaCards) {
            card.setOnClickListener(v -> gestionarSeleccion((MaterialCardView) v));
        }

        setupSpinnerConIcono();

        ImageView ivBack = findViewById(R.id.ivBack);
        if (ivBack != null) ivBack.setOnClickListener(v -> finish());

    }

    private void setupSpinnerConIcono() {
        Spinner spinner = findViewById(R.id.spinnerDistritos);

        // Adaptador personalizado para inflar el layout con el icono lucide_map_pin
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_spinner_ubicacion, R.id.tvDistrito, distritos) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return obtenerVistaPersonalizada(position, convertView, parent);
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                return obtenerVistaPersonalizada(position, convertView, parent);
            }

            private View obtenerVistaPersonalizada(int position, View convertView, ViewGroup parent) {
                View view = getLayoutInflater().inflate(R.layout.item_spinner_ubicacion, parent, false);
                TextView tv = view.findViewById(R.id.tvDistrito);
                ImageView icono = view.findViewById(R.id.imgIconoUbicacion);

                tv.setText(distritos[position]);
                // Aseguramos que el icono sea lucide_map_pin
                icono.setImageResource(R.drawable.lucide_map_pin);
                return view;
            }
        };

        spinner.setAdapter(adapter);
    }

    private void gestionarSeleccion(MaterialCardView cardSeleccionada) {
        for (int i = 0; i < listaCards.length; i++) {
            MaterialCardView card = listaCards[i];
            LinearLayout layout = (LinearLayout) card.getChildAt(0);
            ImageView icono = (ImageView) layout.getChildAt(0);
            TextView texto = (TextView) layout.getChildAt(1);

            if (card == cardSeleccionada) {
                materialSeleccionadoId = i + 1;

                card.setStrokeColor(COLOR_VERDE);
                card.setStrokeWidth(convertDpToPx(2));
                icono.setColorFilter(COLOR_VERDE);
                texto.setTextColor(COLOR_VERDE);
                texto.setTypeface(null, Typeface.BOLD);
            } else {
                // --- ESTADO DESACTIVADO ---
                card.setStrokeColor(COLOR_BORDE_DESACTIVADO);
                card.setStrokeWidth(convertDpToPx(1));
                icono.setColorFilter(COLOR_GRIS_TEXTO);
                texto.setTextColor(COLOR_GRIS_TEXTO);
                texto.setTypeface(null, Typeface.NORMAL);
            }
        }
    }

    private void validarYEnviar() {
        String cantStr = etCantidad.getText().toString().trim();
        int distritoPos = spinnerDistritos.getSelectedItemPosition();

        if (userProfile == null) {
            Toast.makeText(this, "Sesión no válida", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cantStr.isEmpty() || materialSeleccionadoId == -1 || distritoPos == 0) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double cantidad = Double.parseDouble(cantStr);
        String nota = etObservaciones.getText().toString().trim();
        String userId = userProfile.getId_usuario(); // Usamos el ID del usuario logueado

        enviarRegistro(new RegistroRequest(cantidad, nota, materialSeleccionadoId, userId, distritoPos));
    }

    private void enviarRegistro(RegistroRequest request) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.registrarMaterial(request);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    mostrarToast("Registro guardado correctamente");
                    finish();
                } else {
                    mostrarToast("Error al guardar");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mostrarToast("Error de red: " + t.getMessage());
            }
        });
    }

    private int convertDpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
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