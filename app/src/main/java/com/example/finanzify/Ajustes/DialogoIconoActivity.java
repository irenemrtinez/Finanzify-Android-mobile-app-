package com.example.finanzify.Ajustes;

import static android.app.Activity.RESULT_OK;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.finanzify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DialogoIconoActivity extends DialogFragment {
StorageReference storageReference;
LinearProgressIndicator progress;
Uri image;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_dialogo_icono, null);
        builder.setView(dialogView);

        //boton cerrar
        ImageButton buttonCerrar = dialogView.findViewById(R.id.buttonCerrar);
        buttonCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss(); // Cierra el diálogo
            }
        });


        // fin
        ImageView icono1 = dialogView.findViewById(R.id.icono1);
        ImageView icono2 = dialogView.findViewById(R.id.icono2);
        ImageView icono3 = dialogView.findViewById(R.id.icono3);
        ImageView icono4 = dialogView.findViewById(R.id.icono4);
        ImageView icono5 = dialogView.findViewById(R.id.icono5);
        ImageView icono6 = dialogView.findViewById(R.id.icono6);

        icono1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarFotoEnFirebase(1);
                }
            });

        icono2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarFotoEnFirebase(2);
            }
        });
        icono3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarFotoEnFirebase(3);
            }
        });
        icono4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarFotoEnFirebase(4);
            }
        });
        icono5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarFotoEnFirebase(5);
            }
        });
        icono6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarFotoEnFirebase(6);
            }
        });

        return builder.create();
    }
    private void actualizarFotoEnFirebase(int resourceID) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String URL = obtenerUrlImagen(resourceID);
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(URL)) // Aquí estableces la URL de la foto del usuario
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Foto de perfil actualizada correctamente.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Error al actualizar la foto de perfil", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Si no hay usuario autenticado, muestra un mensaje de error
            Toast.makeText(getContext(), "No hay usuario autenticado", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("SuspiciousIndentation")
    private String obtenerUrlImagen(int resourceID) {
        String imageURL = null;
        if(resourceID == 1)
            imageURL = "https://www.pokencyclopedia.info/sprites/spin-off/ico_shuffle/ico_shuffle_002.png";
        if(resourceID == 2)
            imageURL = "https://www.pokencyclopedia.info/sprites/spin-off/ico_shuffle/ico_shuffle_007.png";
        if(resourceID == 3)
            imageURL = "https://www.pokencyclopedia.info/sprites/spin-off/ico_shuffle/ico_shuffle_066.png";
        if(resourceID == 4)
            imageURL = "https://www.pkparaiso.com/shuffle/sprites/004.png";
        if(resourceID == 5)
            imageURL = "https://www.pkparaiso.com/shuffle/sprites/025.png";
        if(resourceID == 6)
        imageURL = "https://tiermaker.com/images/template_images/2022/670969/pokemon-shuffle-grass-types-670969/157png.png";
        return imageURL;
        }



}