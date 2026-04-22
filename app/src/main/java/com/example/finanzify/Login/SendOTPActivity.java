package com.example.finanzify.Login;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.finanzify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;

public class SendOTPActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 1;
    private BroadcastReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otpactivity);

        EditText telefono = findViewById(R.id.telefono);
        Button enviar = findViewById(R.id.buttonEnviar);

        // Verificar si tenemos permiso RECEIVE_SMS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permiso si no lo tenemos
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS},
                    MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
        } else {
            // Ya tenemos permiso, registrar el receptor de emisión
            registerSmsReceiver();
        }

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (telefono.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SendOTPActivity.this, "Por favor, añade un teléfono", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Lógica para enviar el mensaje SMS
                sendSMSMessage();
            }
        });
    }

    private void sendSMSMessage() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        EditText telefono = findViewById(R.id.telefono);
        String codigo = generarCodigo();
        if (user != null) {
            String phoneNumber = "+34" + telefono.getText().toString().trim(); // Obtener el número de teléfono del EditText
            // Envío de SMS
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, codigo, null, null);
                Toast.makeText(SendOTPActivity.this, "Mensaje enviado correctamente", Toast.LENGTH_SHORT).show();
                // Abrir la nueva actividad y pasar el código generado como un extra en el intent
                Intent intent = new Intent(SendOTPActivity.this, VerifyOTPActivity.class);
                intent.putExtra("telefono",phoneNumber);
                intent.putExtra("codigo", codigo); // Pasar el código como un extra
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(SendOTPActivity.this, "Fallo al enviar mensaje: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_RECEIVE_SMS) {
            // Si el permiso es concedido, registrar el receptor de emisión
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                registerSmsReceiver();
            } else {
                // Si el permiso es denegado, mostrar un mensaje al usuario
                Toast.makeText(this, "Permiso RECEIVE_SMS denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registerSmsReceiver() {
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    if (pdus != null) {
                        for (Object pdu : pdus) {
                            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                            String messageBody = smsMessage.getMessageBody();
                            // Aquí puedes procesar el mensaje SMS recibido
                            Toast.makeText(context, "El código ha sido mandado correctamente", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        };

        // Registrar el receptor de emisión para recibir mensajes SMS
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, intentFilter);
    }

    // Función para generar un código aleatorio de 6 dígitos
    private String generarCodigo() {
        // Crear una instancia de la clase Random
        Random random = new Random();
        // Generar un número aleatorio de 6 cifras
        int codigo = random.nextInt(900000) + 100000;
        return String.valueOf(codigo);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desregistrar el receptor de emisión cuando la actividad se destruye
        if (smsReceiver != null) {
            unregisterReceiver(smsReceiver);
        }
    }
}
