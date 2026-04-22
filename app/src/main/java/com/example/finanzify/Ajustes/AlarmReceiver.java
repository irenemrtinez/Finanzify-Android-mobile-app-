package com.example.finanzify.Ajustes;


import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.finanzify.PantallaInicioActivity;
import com.example.finanzify.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AlarmReceiver extends BroadcastReceiver {
    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        // Obtener el usuario actualmente autenticado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String tituloNotificacion;
        tituloNotificacion = "Recordatorio";
        if (user != null) {
            // Obtener el nombre del usuario desde Firebase Authentication
            String nombreUsuario = user.getDisplayName();
            if (nombreUsuario != null && !nombreUsuario.isEmpty())
                // Si el nombre de usuario está disponible, usarlo como título de la notificación
                tituloNotificacion = "¡Hola " + nombreUsuario +"!";
            }

        // Construir la notificación con el título dinámico
        Intent nextActivity = new Intent(context, PantallaInicioActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, nextActivity, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "androidknowledge")
                .setSmallIcon(R.drawable.icono_blanco)
                .setContentTitle(tituloNotificacion)
                .setContentText("Es hora de actualizar tus finanzas en la app :)")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123, builder.build());
    }
}
