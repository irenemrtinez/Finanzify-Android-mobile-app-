package com.example.finanzify.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.finanzify.Clases.Categoria;
import com.example.finanzify.Clases.Usuario;
import com.example.finanzify.PantallaInicioActivity;
import com.example.finanzify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrarActivity extends AppCompatActivity {
    FirebaseAuth mAuth;

    // Declaración de los campos de texto
    TextInputEditText editmail, editnombre, editpassword;
    Button botonReg;

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
        setContentView(R.layout.activity_registrar);

        // Inicialización de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Vinculación de los campos de texto y botones con sus correspondientes vistas en el layout
        editmail = findViewById(R.id.textmail);
        editnombre = findViewById(R.id.nombre_reg);
        editpassword = findViewById(R.id.contra_login);
        botonReg = findViewById(R.id.buttonReg);

// Configuración del OnClickListener para el botón de registro
        botonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtención de los valores de los campos de texto
                String email = String.valueOf(editmail.getText());
                String password = String.valueOf(editpassword.getText());
                String nombre = String.valueOf(editnombre.getText());
                ProgressBar barraR = findViewById(R.id.barraprogresoR);
                barraR.setVisibility(View.VISIBLE);

                // Comprobación de campos vacíos
                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(RegistrarActivity.this,"Debe registrar un email",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)) {
                    Toast.makeText(RegistrarActivity.this,"Debe registrar una contraseña",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(nombre)) {
                    Toast.makeText(RegistrarActivity.this,"Debe registrar un nombre",Toast.LENGTH_SHORT).show();
                    return;
                }

                // Registro del usuario en Firebase Auth
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                barraR.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Registro exitoso, redirigir al usuario a la pantalla de inicio
                                    Toast.makeText(RegistrarActivity.this,"Se ha registrado con éxito",Toast.LENGTH_SHORT).show();

                                    // Obtén el usuario actual
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            // Inicio de sesión exitoso, redirigir al usuario a la pantalla de inicio
                                            Toast.makeText(getApplicationContext(), "Se ha mandado el email de verificacion.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Error al mandar el email de verificacion",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    // Crea un UserProfileChangeRequest y establece el nombre del usuario
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(nombre) // Aquí se establece el nombre del usuario
                                            .build();

                                    // Actualiza el perfil del usuario con el nombre
                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Redirige al usuario a la pantalla de inicio
                                                        guardarDatosUsuarioEnFirebase(user.getUid(), user.getEmail(), password, user.getDisplayName());
                                                        Intent intentPantallaInicio = new Intent(getApplicationContext(), PantallaInicioActivity.class);
                                                        startActivity(intentPantallaInicio);
                                                        finish();
                                                    } else {
                                                        // Maneja el error si la actualización del perfil falla
                                                        Toast.makeText(RegistrarActivity.this, "Error al actualizar el nombre", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    // Si ocurre un error durante el registro
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        // El correo electrónico ya se ha utilizado para otra cuenta
                                        Toast.makeText(RegistrarActivity.this, "Este correo electrónico ya está registrado", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Otro error en el registro
                                        Toast.makeText(RegistrarActivity.this, "Error en el registro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                // guardar usuari en base de datos de firebase

            }
        });


        // Configuración del OnClickListener para el botón de inicio de sesión
        Button botonLog = findViewById(R.id.buttonLog);
        botonLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_reg= new Intent(RegistrarActivity.this, LoginActivity.class);
                startActivity(intent_reg);
            }
        });

        // guardar usuari en base de datos de firebase
    }

    private void guardarDatosUsuarioEnFirebase(String uid, String email, String contraseña, String nombre) {
        // Referencia a la base de datos
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Creamos un nuevo nodo para el usuario con su ID único
        DatabaseReference usuarioRef = databaseRef.child("usuarios").child(uid);

        // Creamos un objeto Usuario con los datos del usuario
        Usuario usuario = new Usuario(email, uid, false, "EUR", 0.0,null);

        // Guardamos los datos del usuario en la base de datos
        usuarioRef.setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // La información del usuario se ha guardado exitosamente en la base de datos
                    Toast.makeText(RegistrarActivity.this, "Datos de usuario guardados en la base de datos", Toast.LENGTH_SHORT).show();

                    // Agregamos las tablas adicionales
                    agregarTablasAdicionales(uid);
                } else {
                    // Si ocurre un error al guardar los datos del usuario
                    Toast.makeText(RegistrarActivity.this, "Error al guardar los datos del usuario en la base de datos: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void agregarTablasAdicionales(String uid) {
        // Referencia a la base de datos
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Creamos nodos vacíos para las tablas adicionales asociadas con el usuario
        DatabaseReference transaccionesRef = databaseRef.child("usuarios").child(uid).child("transacciones");
        DatabaseReference transaccionesRecurrentesRef = databaseRef.child("usuarios").child(uid).child("transacciones_recurrentes");
        DatabaseReference limitesRef = databaseRef.child("usuarios").child(uid).child("limites");

        // Creamos la tabla de categorías con algunas categorías predeterminadas asociadas con el usuario
        DatabaseReference categoriasRef = databaseRef.child("usuarios").child(uid).child("categorias");
        Map<String, Categoria> categorias = new HashMap<>();
        categorias.put("1", new Categoria("Alquiler", "https://images.vexels.com/media/users/3/155844/isolated/preview/26ed4bf470ad0a2fa80c1b6258e9f5a1-icono-de-llave-basica.png", "pago"));
        categorias.put("2", new Categoria("Becas", "https://static.vecteezy.com/system/resources/previews/017/785/212/non_2x/money-icon-on-transparent-background-free-png.png", "ingresos"));
        categorias.put("3", new Categoria("Casa", "https://cdn-icons-png.freepik.com/256/846/846449.png?semt=ais_hybrid", "pago"));
        categorias.put("4", new Categoria("Coche", "https://www.iconpacks.net/icons/2/free-car-icon-2897-thumb.png", "pago"));
        categorias.put("5", new Categoria("Comida", "https://www.drawhipo.com/wp-content/uploads/2022/07/Fast-Food-Mono-3-Pizza-Curved.png", "pago"));
        categorias.put("6", new Categoria("Compras", "https://cdn-icons-png.flaticon.com/512/126/126510.png", "pago"));
        categorias.put("7", new Categoria("Dinero de bolsillo", "https://cdn.icon-icons.com/icons2/788/PNG/512/wallet_icon-icons.com_65116.png", "ingresos"));
        categorias.put("8", new Categoria("Educación", "https://static.vecteezy.com/system/resources/previews/014/179/595/non_2x/graduation-thin-line-icon-education-icon-set-png.png", "pago"));
        categorias.put("9", new Categoria("Electronica", "https://www.iconpacks.net/icons/1/free-phone-icon-1-thumb.png", "pago"));
        categorias.put("10", new Categoria("Familia", "https://cdn-icons-png.freepik.com/256/1416/1416832.png?semt=ais_hybrid", "pago"));
        categorias.put("11", new Categoria("Gastos médicos", "https://static.vecteezy.com/system/resources/thumbnails/024/818/382/small/medical-diagnostic-line-icon-png.png", "pago"));
        categorias.put("12", new Categoria("Inversiones", "https://cdn-icons-png.flaticon.com/512/218/218409.png", "ingresos"));
        categorias.put("13", new Categoria("Juegos", "https://icons.veryicon.com/png/o/business/hotel-facilities/games-room.png", "pago"));
        categorias.put("14", new Categoria("Mascota", "https://www.veryicon.com/download/png/animal/pet-icon/dog-24?s=256", "pago"));
        categorias.put("15", new Categoria("Nomina", "https://cdn-icons-png.flaticon.com/512/5348/5348095.png", "ingresos"));
        categorias.put("16", new Categoria("Ocio", "https://static-00.iconduck.com/assets.00/vacation-icon-512x430-ukc5nyq3.png", "pago"));
        categorias.put("17", new Categoria("Otros", "https://static.thenounproject.com/png/2821166-200.png", "pago"));
        categorias.put("18", new Categoria("Premios", "https://cdn.icon-icons.com/icons2/563/PNG/512/award-with-a-star_icon-icons.com_54115.png", "ingresos"));
        categorias.put("19", new Categoria("Reembolsos", "https://cdn-icons-png.flaticon.com/512/118/118111.png", "ingresos"));
        categorias.put("20", new Categoria("Ropa", "https://static.vecteezy.com/system/resources/previews/010/145/448/non_2x/clothes-hanger-icon-sign-symbol-design-free-png.png", "pago"));
        categorias.put("21", new Categoria("Salud", "https://cdn-icons-png.flaticon.com/512/103/103387.png", "pago"));
        categorias.put("22", new Categoria("Ventas", "https://cdn-icons-png.flaticon.com/512/103/103831.png", "ingresos"));
        categorias.put("23", new Categoria("Sin categoria", "https://endlessicons.com/wp-content/uploads/2014/05/global-icon-614x460.png", "Global"));
        // Guardamos las tablas adicionales asociadas con el usuario en la base de datos
        transaccionesRef.setValue(null);
        transaccionesRecurrentesRef.setValue(null);
        limitesRef.setValue(null);
        categoriasRef.setValue(categorias).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegistrarActivity.this, "Tablas adicionales creadas con éxito", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegistrarActivity.this, "Error al crear las tablas adicionales: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



}
