package com.example.finanzify.Ajustes;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.finanzify.Login.LoginActivity;
import com.example.finanzify.MainActivity;
import com.example.finanzify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EditPerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perfil);

        // Obtener el usuario
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            ImageView pfp = findViewById(R.id.pfp);
            loadProfilePhoto(); // Llamar al método para cargar la foto de perfil

            pfp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogoIconoActivity dialogoIconoActivity = new DialogoIconoActivity();
                    dialogoIconoActivity.show(getSupportFragmentManager(), "DialogoIconoActivity");
                }
            });

            // Obtener el nombre del usuario desde Firebase Authentication
            String nombreUsuario = user.getDisplayName();
            if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
                // Asignar el nombre del usuario al campo de texto correspondiente
                TextInputEditText editTextNombre = findViewById(R.id.Nombre);
                editTextNombre.setText(nombreUsuario);
                //Toast.makeText(EditPerfilActivity.this, "Nombre añadido", Toast.LENGTH_SHORT).show();
            } else {
                // Si el nombre no está disponible, mostrar un mensaje de error
                TextInputEditText editTextNombre = findViewById(R.id.Nombre);
                editTextNombre.setText("Nombre no encontrado");
                Toast.makeText(EditPerfilActivity.this, "Nombre no encontrado", Toast.LENGTH_SHORT).show();
            }

            // Obtener el correo electrónico del usuario desde Firebase Authentication
            String correoUsuario = user.getEmail();
            if (correoUsuario != null && !correoUsuario.isEmpty()) {
                // Asignar el correo electrónico del usuario al campo de texto correspondiente
                TextInputEditText editTextCorreo = findViewById(R.id.correo);
                editTextCorreo.setText(correoUsuario);
                //Toast.makeText(EditPerfilActivity.this, "Correo añadido", Toast.LENGTH_SHORT).show();
            } else {
                // Si el correo electrónico no está disponible, mostrar un mensaje de error
                TextInputEditText editTextCorreo = findViewById(R.id.correo);
                editTextCorreo.setText("Correo no encontrado");
                Toast.makeText(EditPerfilActivity.this, "Correo no encontrado", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Si el usuario no está autenticado, mostrar un mensaje de error
            TextInputEditText editTextNombre = findViewById(R.id.Nombre);
            editTextNombre.setText("Usuario no autenticado");
            TextInputEditText editTextCorreo = findViewById(R.id.correo);
            editTextCorreo.setText("Usuario no autenticado");
            Toast.makeText(EditPerfilActivity.this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }


        // BOTON PARA EDITAR PERFIL
        Button buttonEdit = findViewById(R.id.buttonEdit);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el texto actualmente ingresado en los campos de texto
                TextInputEditText editTextNombre = findViewById(R.id.Nombre);
                String nuevoNombre = editTextNombre.getText().toString();

                // Obtener el usuario actualmente autenticado
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // Obtener el nombre actualmente almacenado en Firebase
                    String nombreActual = user.getDisplayName();

                    // Verificar si el nombre ha cambiado
                    if (!TextUtils.equals(nuevoNombre, nombreActual)) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(nuevoNombre)
                                .build();

                        // Actualizar el nombre en Firebase
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(EditPerfilActivity.this, "Nombre actualizado correctamente", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(EditPerfilActivity.this, "Error al actualizar el nombre", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        // Si el nombre no ha cambiado, mostrar un mensaje
                        Toast.makeText(EditPerfilActivity.this, "El nombre no ha cambiado", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //boton volver
        ImageButton buttonVolver = findViewById(R.id.buttonVolver);
        buttonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditPerfilActivity.this, AjustesActivity.class);
                startActivity(intent);
            }
        });
        // fin

        // boton logout
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Button boton_logout = findViewById(R.id.buttonLogout);
        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            boton_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent_reg = new Intent(EditPerfilActivity.this, MainActivity.class);
                    startActivity(intent_reg);
                }
            });
        }
        // fin boton logout

        //boton eliminar
        // Inicializa el botón de eliminar cuenta
        Button botonEliminarCuenta = findViewById(R.id.buttonElim);
        botonEliminarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para eliminar la cuenta del usuario
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // La cuenta se eliminó correctamente
                                        Toast.makeText(EditPerfilActivity.this, "Cuenta eliminada exitosamente", Toast.LENGTH_SHORT).show();
                                        // Aquí puedes redirigir al usuario a la pantalla de inicio de sesión o a donde desees

                                    } else {
                                        // Ocurrió un error al eliminar la cuenta
                                        Toast.makeText(EditPerfilActivity.this, "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });


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
}

