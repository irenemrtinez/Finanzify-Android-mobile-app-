package com.example.finanzify.Ajustes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.finanzify.Login.ContraOlvidadaActivity;
import com.example.finanzify.PantallaInicioActivity;
import com.example.finanzify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CambiarContraActivity extends AppCompatActivity {
    private TextInputEditText etContraVieja, etContraNueva;
    private Button btnCambiar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_contra);

        // Botón volver
        ImageButton buttonVolver = findViewById(R.id.buttonVolver);
        buttonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CambiarContraActivity.this, AjustesActivity.class);
                startActivity(intent);
            }
        });
        // Fin botón volver

        mAuth = FirebaseAuth.getInstance();
        etContraVieja = findViewById(R.id.contra_vieja);
        etContraNueva = findViewById(R.id.contra_nueva);
        btnCambiar = findViewById(R.id.buttonCC);

        btnCambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contraVieja = etContraVieja.getText().toString();
                final String contraNueva = etContraNueva.getText().toString();

                if (TextUtils.isEmpty(contraVieja)) {
                    etContraVieja.setError("Ingrese la contraseña antigua");
                    return;
                }

                if (TextUtils.isEmpty(contraNueva)) {
                    etContraNueva.setError("Ingrese la nueva contraseña");
                    return;
                }

                FirebaseUser user = mAuth.getCurrentUser();

                if (user != null) {
                    // Obtener credenciales de usuario actual
                    String email = user.getEmail();
                    AuthCredential credential = EmailAuthProvider.getCredential(email, contraVieja);

                    // Reautenticar al usuario
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // La contraseña antigua es correcta, cambiar la contraseña
                                        user.updatePassword(contraNueva)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(CambiarContraActivity.this, "Contraseña actualizada exitosamente", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        } else {
                                                            Toast.makeText(CambiarContraActivity.this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        // La contraseña antigua es incorrecta
                                        Toast.makeText(CambiarContraActivity.this, "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(CambiarContraActivity.this,"No se pudo obtener el usuario actual",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
