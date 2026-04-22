package com.example.finanzify.Ajustes;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finanzify.Login.LoginActivity;
import com.example.finanzify.MainActivity;
import com.example.finanzify.Moneda.CambioMonedaActivity;
import com.example.finanzify.PantallaInicioActivity;
import com.example.finanzify.Presupuestos.PrespuestoActivity;
import com.example.finanzify.R;
import com.example.finanzify.Categorias.CategoriasPagosActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class AjustesActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button boton_logout;
    FirebaseUser usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        TextView textViewNombre = findViewById(R.id.textViewNombre);

        // Obtener el usuario actualmente autenticado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ImageView pfp = findViewById(R.id.pfp);
        if (user != null) {
            // Obtener el nombre del usuario desde Firebase Authentication
            String nombreUsuario = user.getDisplayName();
            if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
                textViewNombre.setText(nombreUsuario);
                if (user.getPhotoUrl() != null) {
                    String photoUrl = user.getPhotoUrl().toString();
                    // Cargar la imagen utilizando Glide
                    Glide.with(this).load(photoUrl).into(pfp);
                }
            } else {
                textViewNombre.setText("Nombre no encontrado");
            }
        } else {
            textViewNombre.setText("Usuario no autenticado");
        }

        loadProfilePhoto();
        //boton volver
        ImageButton buttonVolver = findViewById(R.id.buttonVolver);
        buttonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AjustesActivity.this, PantallaInicioActivity.class);
                startActivity(intent);
            }
        });

        //boton editar el perfil
        TextView textViewEditEP = findViewById(R.id.textViewEditEP);
        ImageButton buttonEP = findViewById(R.id.buttonEP);
        textViewEditEP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirEditPerfilActivity();
            }
        });

        buttonEP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirEditPerfilActivity();
            }

        });


        // boton cambiar contraseña
        TextView textViewEditCC = findViewById(R.id.textViewEditCC);
        ImageButton buttonCC = findViewById(R.id.buttonCC);
        textViewEditCC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCambiarContrasenaActivity();
            }
        });

        buttonCC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCambiarContrasenaActivity();
            }
        });


        //boton configuracion de notificaciones
        TextView textViewEditCN = findViewById(R.id.textViewEditCN);
        ImageButton buttonCN = findViewById(R.id.buttonCN);
        textViewEditCN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirNotificacionesActivity();
            }
        });

        buttonCN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirNotificacionesActivity();
            }
        });

        // boton categorias
        //boton configuracion de notificaciones
        TextView textViewCategorias = findViewById(R.id.textViewCategorias);
        ImageButton buttonCategorias = findViewById(R.id.buttonCategorias);
        textViewCategorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCategoriasActivity();
            }
        });

        buttonCategorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCategoriasActivity();
            }
        });

        //boton configuracion de moneda
        TextView textViewMoneda = findViewById(R.id.textViewMoneda);
        ImageButton buttonmoneda = findViewById(R.id.buttonmoneda);
        textViewMoneda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirMonedaActivity();
            }
        });

        buttonmoneda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirMonedaActivity();;
            }
        });

        Button buttonexp = findViewById(R.id.buttonexp);
        buttonexp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportarDatos();
                //Toast.makeText(AjustesActivity.this, "Datos exportados", Toast.LENGTH_SHORT).show();

            }
        });

        //boton configuracion de notificaciones
        TextView textViewLim = findViewById(R.id.textViewLim);
        ImageButton buttonLim = findViewById(R.id.buttonLim);
        textViewLim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirPresupuestoActivity();
            }
        });

        buttonLim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirPresupuestoActivity();
            }
        });



        // 2FA
        SwitchCompat switchCompat = findViewById(R.id.switch2FA);
        SharedPreferences sharedPreferences = getSharedPreferences("misPreferencias", MODE_PRIVATE);
        // Recuperar el estado almacenado del interruptor desde SharedPreferences
        boolean switchState = sharedPreferences.getBoolean("switch2FAState", false);
        switchCompat.setChecked(switchState);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Guardar el estado actual del interruptor en SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("switch2FAState", isChecked);
                editor.apply();

                if (user != null) {
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference usuarioRef = databaseRef.child("usuarios").child(user.getUid());
                    usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (isChecked) {
                                // El interruptor está activado (ON)
                                if (dataSnapshot.exists()) {
                                    String phoneNumber = dataSnapshot.child("telefono").getValue(String.class);
                                    if (phoneNumber == null || phoneNumber.isEmpty()) {
                                        // Si el número de teléfono no está definido, pedir al usuario que lo ingrese
                                        requestPhoneNumber(usuarioRef);
                                    } else {
                                        // Si el número de teléfono ya está definido, no es necesario pedirlo nuevamente
                                        enableMultiFactorAuth(usuarioRef);
                                    }
                                } else {
                                    System.out.println("El nodo del usuario no existe en la base de datos.");
                                }
                            } else {
                                // El interruptor está desactivado (OFF)
                                usuarioRef.child("autorizado2FA").setValue(false);
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(user.getDisplayName()) // Mantén el nombre de visualización actual
                                        //.updatePhoneNumber("+127 127 127 127")
                                        .build();
                                user.updateProfile(profileUpdates);
                                Toast.makeText(AjustesActivity.this, "Autentificación usando doble factor desactivada", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(AjustesActivity.this, "Error al leer el número de teléfono del usuario: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(AjustesActivity.this, "Usuario no encontrado, inicie sesión para activar la doble autentificación", Toast.LENGTH_SHORT).show();
                }
            }

            private void requestPhoneNumber(DatabaseReference usuarioRef) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AjustesActivity.this);
                builder.setTitle("Número de teléfono");
                builder.setMessage("Por favor, introduzca su número de teléfono para enviarle un código cuando se inicie sesión:");

                final EditText input = new EditText(AjustesActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                builder.setView(input);

                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String phoneNumber = "+34" + input.getText().toString();
                        // Aquí puedes hacer lo que necesites con el número de teléfono
                        // Por ejemplo, guardarlo en Firebase o en SharedPreferences
                        // y luego actualizar el perfil del usuario con ese número de teléfono
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(user.getDisplayName()) // Mantén el nombre de visualización actual
                                //.setDisplayName(phoneNumber)
                                .build();
                        user.updateProfile(profileUpdates);

                        // Actualizar el valor de autorizado2FA a true
                        usuarioRef.child("autorizado2FA").setValue(true);
                        // Guardar el número de teléfono del usuario
                        usuarioRef.child("telefono").setValue(phoneNumber);
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }

            private void enableMultiFactorAuth(DatabaseReference usuarioRef) {
                // Actualizar el valor de autorizado2FA a true
                usuarioRef.child("autorizado2FA").setValue(true);
            }
        });





        // fin 2FA
        // botton logout
        auth = FirebaseAuth.getInstance();
        boton_logout= findViewById(R.id.buttonlogout);
        usuario = auth.getCurrentUser();
        if (usuario==null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            boton_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent_reg= new Intent(AjustesActivity.this, MainActivity.class);
                    startActivity(intent_reg);
                }
            });
        }
        // fin boton logout

    }
    private void abrirPresupuestoActivity() {
        Intent intent = new Intent(AjustesActivity.this, PrespuestoActivity.class);
        startActivity(intent);
    }
    private void abrirCategoriasActivity() {
        Intent intent = new Intent(AjustesActivity.this, CategoriasPagosActivity.class);
        startActivity(intent);
    }

    private void abrirCambiarContrasenaActivity() {
        Intent intent = new Intent(AjustesActivity.this, CambiarContraActivity.class);
        startActivity(intent);
    }
    private void abrirEditPerfilActivity() {
        Intent intent = new Intent(AjustesActivity.this, EditPerfilActivity.class);
        startActivity(intent);
    }
    private void abrirNotificacionesActivity() {
        Intent intent = new Intent(AjustesActivity.this, NotificacionesActivity.class);
        startActivity(intent);
    }
    private void abrirMonedaActivity(){
        Intent intent = new Intent(AjustesActivity.this, CambioMonedaActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfilePhoto(); // Llamar al método para cargar la foto de perfil
    }
    private void loadProfilePhoto() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ImageView imageView = findViewById(R.id.pfp);

        if (user != null && user.getPhotoUrl() != null) {
            String photoUrl = user.getPhotoUrl().toString();

            // Cargar la imagen utilizando Glide
            Glide.with(this).load(photoUrl).into(imageView);
        } else {
            // Si no hay foto de perfil, puedes establecer una imagen de perfil predeterminada
            imageView.setImageResource(R.drawable.pfp);
        }
    }

    private void exportarDatos() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference usuarioRef = databaseRef.child("usuarios").child(user.getUid());

            usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        JSONObject jsonObject = new JSONObject();

                        try {
                            // Recorrer todos los datos del usuario y agregarlos al JSON
                            JSONObject categoriasObject = new JSONObject();
                            JSONObject transaccionesObject = new JSONObject();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String key = snapshot.getKey();
                                Object value = snapshot.getValue();

                                // Tratar categorías como objetos JSON
                                if ("categorias".equals(key) && value instanceof String) {
                                    JSONArray categoriasArray = new JSONArray((String) value);
                                    for (int i = 0; i < categoriasArray.length(); i++) {
                                        JSONObject categoriaObject = categoriasArray.getJSONObject(i);
                                        String categoriaKey = String.valueOf(i); // Usar índice como clave para cada categoría
                                        categoriasObject.put(categoriaKey, categoriaObject);
                                    }
                                    jsonObject.put(key, categoriasObject);
                                }
                                // Tratar transacciones como objetos JSON
                                else if ("transacciones".equals(key) && value instanceof String) {
                                    JSONArray transaccionesArray = new JSONArray((String) value);
                                    for (int i = 0; i < transaccionesArray.length(); i++) {
                                        JSONObject transaccionObject = transaccionesArray.getJSONObject(i);
                                        String transaccionKey = String.valueOf(i); // Usar índice como clave para cada transacción
                                        transaccionesObject.put(transaccionKey, transaccionObject);
                                    }
                                    jsonObject.put(key, transaccionesObject);
                                }
                                // Convertir los demás datos a JSON y agregarlos al objeto JSON principal
                                else {
                                    if (value instanceof Map) {
                                        JSONObject innerObject = new JSONObject((Map<?, ?>) value);
                                        jsonObject.put(key, innerObject);
                                    } else {
                                        jsonObject.put(key, value);
                                    }
                                }
                            }

                            // Obtener la fecha actual en el formato deseado
                            SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
                            String fecha = dateFormat.format(new Date());

                            // Guardar el JSON en un archivo
                            exportarJSON(jsonObject, fecha);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AjustesActivity.this, "Error al exportar los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar cualquier error
                }
            });
        }
    }


    // Método para exportar el JSON a un archivo
    private void exportarJSON(JSONObject jsonObject, String fecha) {
        try {
            String fileName = "datos_usuario_" + fecha + ".json";
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);

            // Formatear el JSON para que esté correctamente indentado
            String jsonString = jsonObject.toString(4); // El número 4 indica la cantidad de espacios de indentación
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonString);
            fileWriter.flush();
            fileWriter.close();

            Log.d(TAG, "Ruta absoluta del archivo: " + file.getAbsolutePath());

            // Envía el archivo por correo electrónico
            Uri fileUri = FileProvider.getUriForFile(this, "com.example.finanzify.fileprovider", file);
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Datos de usuario");
            emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            startActivity(Intent.createChooser(emailIntent, "Enviar archivo"));

            Toast.makeText(AjustesActivity.this, "Datos exportados a " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(AjustesActivity.this, "Error al exportar los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}