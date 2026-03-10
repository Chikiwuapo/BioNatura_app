package com.goku1.bionatura;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.goku1.bionatura.models.PasswordUpdateRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActualizarContrasena extends AppCompatActivity {

    ImageButton volver, ver1, ver2, ver3;
    Button cancelar, btnActualizar;
    EditText campo1, campo2, campo3;
    boolean isVisible1 = false, isVisible2 = false, isVisible3 = false;
    private static final String SESSION_NAME = "session";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_contrasena);

        volver = findViewById(R.id.btnVolver);
        cancelar = findViewById(R.id.btnCancelarPass);
        btnActualizar = findViewById(R.id.btnActualizarPass);

        campo1 = findViewById(R.id.txtContrasena1);
        campo2 = findViewById(R.id.txtContrasena2);
        campo3 = findViewById(R.id.txtContrasena3);

        ver1 = findViewById(R.id.ojoCerrado1);
        ver2 = findViewById(R.id.ojoCerrado2);
        ver3 = findViewById(R.id.ojoCerrado3);

        volver.setOnClickListener(v -> mostrarAdvertenciaSalida());
        cancelar.setOnClickListener(v -> mostrarAdvertenciaSalida());

        btnActualizar.setOnClickListener(v -> enviarPassword());

        ver1.setOnClickListener(v -> isVisible1 = alternarVisibilidad(campo1, ver1, isVisible1));
        ver2.setOnClickListener(v -> isVisible2 = alternarVisibilidad(campo2, ver2, isVisible2));
        ver3.setOnClickListener(v -> isVisible3 = alternarVisibilidad(campo3, ver3, isVisible3));
    }

    private void enviarPassword() {

        String actual = campo1.getText().toString().trim();
        String nueva = campo2.getText().toString().trim();
        String confirma = campo3.getText().toString().trim();

        if (actual.isEmpty() || nueva.isEmpty() || confirma.isEmpty()) {
            mostrarToast("Completa todos los campos");
            return;
        }

        if (!nueva.equals(confirma)) {
            campo3.setError("Las contraseñas no coinciden");
            return;
        }

        SharedPreferences prefs = getSharedPreferences(SESSION_NAME, MODE_PRIVATE);
        String token = prefs.getString("token", "");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.actualizarPassword("Bearer " + token,
                new PasswordUpdateRequest(actual, nueva));

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    mostrarToast("Contraseña actualizada correctamente");
                    finish();
                } else {
                    mostrarToast("Error al actualizar (verifica tu contraseña actual)");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mostrarToast("Error de red: " + t.getMessage());
            }
        });
    }

    private boolean alternarVisibilidad(EditText campo, ImageButton boton, boolean estaVisible) {

        if (estaVisible) {
            campo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            boton.setImageResource(R.drawable.ic_ojo);
        } else {
            campo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            boton.setImageResource(R.drawable.ic_ojo_abierto);
        }

        campo.setTypeface(Typeface.DEFAULT);
        campo.setSelection(campo.getText().length());

        return !estaVisible;
    }

    private void mostrarAdvertenciaSalida() {

        boolean hayCambios =
                !campo1.getText().toString().isEmpty() ||
                        !campo2.getText().toString().isEmpty() ||
                        !campo3.getText().toString().isEmpty();

        if (!hayCambios) {
            finish();
            return;
        }

        android.app.AlertDialog.Builder builder =
                new android.app.AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.layout_dialog_advertencia, null);
        builder.setView(view);

        final android.app.AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        view.findViewById(R.id.btnAceptarDialog).setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        view.findViewById(R.id.btnCancelarDialog).setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    public void mostrarToast(String mensaje){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, null);

        TextView txt = layout.findViewById(R.id.txtMensaje);
        txt.setText(mensaje);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);

        // posición arriba derecha
        toast.setGravity(android.view.Gravity.TOP | android.view.Gravity.END, 30, 120);

        toast.show();
    }
}