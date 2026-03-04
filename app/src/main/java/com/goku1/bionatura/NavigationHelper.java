package com.goku1.bionatura;

import android.app.Activity;
import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationHelper {

    public static void setupBottomNavigation(Activity activity, int currentItemId) {
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottom_navigation);
        
        if (bottomNavigationView == null) return;

        // Establecer el elemento seleccionado actual
        bottomNavigationView.setSelectedItemId(currentItemId);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            
            if (id == currentItemId) return true;

            Intent intent = null;
            if (id == R.id.nav_inicio) {
                intent = new Intent(activity, MainActivity.class);
            } else if (id == R.id.nav_historial) {
                intent = new Intent(activity, historial_asistencias.class);
            } else if (id == R.id.nav_registrar) {
                intent = new Intent(activity, asistencia.class);
            } else if (id == R.id.nav_perfil) {
                intent = new Intent(activity, perfil.class);
            }

            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                activity.startActivity(intent);
                // Opcional: animaciones de transición
                activity.overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}
