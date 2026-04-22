package com.example.finanzify.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finanzify.PantallaInicioActivity;
import com.example.finanzify.R;

public class VerifyOTPActivity extends AppCompatActivity {
    private EditText codigoInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otpactivity);

        TextView numero = findViewById(R.id.textViewDesc);
        numero.setText(String.format(
                "Se ha enviado el código de verificación al número +34 %s",getIntent().getStringExtra("telefono")));
        String codigoEnviado = getIntent().getStringExtra("codigo");
        codigoInput = findViewById(R.id.codigo);
        Button confirmar = findViewById(R.id.buttonConfirmar);
        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codigoRecibido = codigoInput.getText().toString().trim();
                verificarCodigo(codigoEnviado,codigoRecibido);
            }
        });
    }
    private void verificarCodigo(String codigoEnviado, String CodigoRecibido) {
        // Obtener el código ingresado por el usuario

        // Verificar si el código ingresado coincide con el código enviado por correo
        if (codigoEnviado.equals(CodigoRecibido)) {
            // Si coinciden, mostrar un Toast de autenticación correcta y pasar a otra actividad
            Toast.makeText(this, "Autenticación correcta", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(VerifyOTPActivity.this, PantallaInicioActivity.class);
            startActivity(intent);
        } else {
            // Si no coinciden, mostrar un Toast de código incorrecto
            Toast.makeText(this, "Código incorrecto", Toast.LENGTH_SHORT).show();
        }
    }
}