package com.example.finanzify.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finanzify.Ajustes.AjustesActivity;
import com.example.finanzify.PantallaInicioActivity;
import com.example.finanzify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMultiFactorException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText editmail, editpassword;
    TextView contraOlvidada;
    FirebaseAuth mAuth;
    ProgressBar barraL;

    @Override
    public void onStart() {
        super.onStart();

        // Comprobación si el usuario ya está autenticado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intentPantallaInicio = new Intent(getApplicationContext(), PantallaInicioActivity.class);
            startActivity(intentPantallaInicio);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicialización de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Vinculación de los campos de texto y barra de progreso con sus correspondientes vistas en el layout
        editmail = findViewById(R.id.textNombre);
        editpassword = findViewById(R.id.contra_login);
        barraL = findViewById(R.id.barraprogresoL);
        // Configuración del OnClickListener para el botón de inicio de sesión
        Button botonLog = findViewById(R.id.buttonLogin);
        botonLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtención de los valores de los campos de texto
                String email = String.valueOf(editmail.getText());
                String password = String.valueOf(editpassword.getText());
                barraL.setVisibility(View.VISIBLE);

                // Validación de campos vacíos
                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this,"Debe registrar un email",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this,"Debe registrar una contraseña",Toast.LENGTH_SHORT).show();
                    return;
                }

                // Inicio de sesión del usuario en Firebase Auth
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                barraL.setVisibility(View.GONE);


                                if (task.isSuccessful()) {
                                    // Obtén el ID único del usuario
                                    FirebaseUser usuario = mAuth.getCurrentUser();
                                    // Recuperar el valor autorizado2FA del usuario
                                    recuperarDatosUsuarioDeFirebase(usuario.getUid());

                                    // Inicio de sesión exitoso, redirigir al usuario a la pantalla de inicio
                                    Toast.makeText(getApplicationContext(), "Autenticación exitosa.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Exception exception = task.getException();
                                    if (exception instanceof FirebaseAuthMultiFactorException) {
                                        // Se requiere un segundo factor para completar la autenticación
                                        // Puedes manejar esto redirigiendo al usuario a la actividad de doble autenticación
                                        Intent intent = new Intent(LoginActivity.this, DobleAutenticacionActivity.class);
                                        startActivity(intent);
                                        finish(); // Opcional: finaliza esta actividad si ya no la necesitas en la pila de actividades
                                    } else {
                                        // Otros casos de error de inicio de sesión
                                        String errorMessage = "Error en el inicio de sesión: ";
                                        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                            errorMessage += "Credenciales inválidas.";
                                        } else if (exception instanceof FirebaseTooManyRequestsException) {
                                            errorMessage += "Demasiadas solicitudes. Intente nuevamente más tarde.";
                                        } else {
                                            errorMessage += exception.getMessage();
                                        }
                                        Toast.makeText(LoginActivity.this, errorMessage,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                        });
            }
        });

        // Configuración del OnClickListener para el botón de registro
        Button botonReg = findViewById(R.id.buttonRegistro);
        botonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_log= new Intent(LoginActivity.this, RegistrarActivity.class);
                startActivity(intent_log);
                finish();
            }
        });
        //ir a contraOlvidadaactivity por si ha olvidado la contraseña
        contraOlvidada= findViewById(R.id.textoOlvido);
        contraOlvidada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_log= new Intent(LoginActivity.this, ContraOlvidadaActivity.class);
                startActivity(intent_log);
                finish();
            }
        });
    }

    private void recuperarDatosUsuarioDeFirebase(String uid) {
        // Referencia a la base de datos
        DatabaseReference usuarioRef = FirebaseDatabase.getInstance().getReference().child("usuarios").child(uid);

        // Leer los datos del usuario de la base de datos
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Verificar si los datos existen en la base de datos
                if (dataSnapshot.exists()) {
                    // Obtener el valor de autorizado2FA
                    Boolean autorizado2FA = dataSnapshot.child("autorizado2FA").getValue(Boolean.class);

                    // Hacer lo que necesites con el valor recuperado
                    if (autorizado2FA != null && autorizado2FA) {
                        Intent intentAutenticacion2FA = new Intent(getApplicationContext(), DobleAutenticacionActivity.class);
                        startActivity(intentAutenticacion2FA);
                        finish(); // Finalizar la actividad actual para evitar que el usuario regrese a esta pantalla usando el botón Atrás
                    } else {
                        Intent intentPantallaInicio = new Intent(getApplicationContext(), PantallaInicioActivity.class);
                        startActivity(intentPantallaInicio);
                        finish();
                    }
                } else {
                    // error al recupear datos de usuario
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores si los hay
                Toast.makeText(LoginActivity.this, "Error al recuperar los datos del usuario de la base de datos: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}

