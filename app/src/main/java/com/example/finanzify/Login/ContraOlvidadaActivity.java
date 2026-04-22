package com.example.finanzify.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finanzify.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ContraOlvidadaActivity extends AppCompatActivity {
    Button btnenviar;
    TextInputEditText editmail;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contra_olvidada);

        btnenviar= findViewById(R.id.buttonEnviar);
        editmail = findViewById(R.id.fieldemail);
        // Inicialización de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        btnenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(editmail.getText());
                if (!TextUtils.isEmpty(email)){
                    mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(ContraOlvidadaActivity.this,"Se ha mandado un correo con la contraseña",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ContraOlvidadaActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("ResetPassword", "Error al enviar correo: " + e.getMessage());
                                    Toast.makeText(ContraOlvidadaActivity.this,"Error",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else{
                    Toast.makeText(ContraOlvidadaActivity.this,"Debe poner un email",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }
}