package com.example.finanzify.Historial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finanzify.Ajustes.AjustesActivity;
import com.example.finanzify.Ajustes.NotificacionesActivity;
import com.example.finanzify.Categorias.CategoriasAdapter;
import com.example.finanzify.Clases.Categoria;
import com.example.finanzify.Clases.Transaccion;
import com.example.finanzify.PantallaInicioActivity;
import com.example.finanzify.R;
import com.example.finanzify.Transacciones.CategoriasCrearIngresoActivity;
import com.example.finanzify.Transacciones.CrearIngresoActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class HistorialActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistorialAdapter historialAdapter;
    private List<Transaccion> transaccionesList;

    TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        // Inicializar RecyclerView y su adaptador
        recyclerView = findViewById(R.id.recyclerViewHistorial);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        transaccionesList = new ArrayList<>();

        historialAdapter = new HistorialAdapter(transaccionesList);
        recyclerView.setAdapter(historialAdapter);

        // Obtener las transacciones del usuario actual
        obtenerTransaccionesUsuario();

        //boton historial recurrente

        textview = findViewById(R.id.textrec);
        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistorialActivity.this, HistorialRecurrentesActivity.class);
                startActivity(intent);
            }
        });


        //boton volver
        ImageButton buttonVolver = findViewById(R.id.buttonVolver);
        buttonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistorialActivity.this, PantallaInicioActivity.class);
                startActivity(intent);
            }
        });


        historialAdapter.setOnItemClickListener(new HistorialAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Transaccion transaccion) {
                Dialog dialog = new Dialog(HistorialActivity.this);
                dialog.setContentView(R.layout.dialog_historial);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);

                // Obtener referencias a los TextView en el diseño del diálogo
                TextView tipoTransaccionTextView = dialog.findViewById(R.id.transaccion);
                TextView fechaTextView = dialog.findViewById(R.id.Fecha);
                TextView cantidadTextView = dialog.findViewById(R.id.Cantidad);
                TextView mensajeTextView = dialog.findViewById(R.id.Mensaje);
                TextView categoriaTextView = dialog.findViewById(R.id.Categoria);

                // Establecer el texto de cada TextView con los valores de la transacción
                tipoTransaccionTextView.setText(transaccion.getTipo());
                fechaTextView.setText(transaccion.getFecha());
                cantidadTextView.setText(String.valueOf(transaccion.getCantidad()));
                mensajeTextView.setText(transaccion.getMensaje());
                categoriaTextView.setText(transaccion.getCategoria().getNombre());
                Button okButton = dialog.findViewById(R.id.buttonaOk);
                // Agregar OnClickListener al botón OK para cerrar el diálogo
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss(); // Cerrar el diálogo
                    }
                });

                dialog.show();


            }

        });

    }




    private void obtenerTransaccionesUsuario() {
        // Obtener el ID del usuario actual
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            // Referencia a las transacciones del usuario en la base de datos
            DatabaseReference transaccionesRef = FirebaseDatabase.getInstance().getReference()
                    .child("usuarios").child(uid).child("transacciones");


            // Leer las transacciones del usuario
            transaccionesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Limpiar la lista actual de transacciones
                    transaccionesList.clear();

                    // Iterar a través de las transacciones y agregarlas a la lista
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Transaccion transaccion = snapshot.getValue(Transaccion.class);
                        if (transaccion != null && transaccion.isCobrado()) {
                            // Agregar la transacción a la lista
                            transaccionesList.add(transaccion);
                            // Imprimir el nombre de la categoría
                            System.out.println("Nombre de la categoría: " + transaccion.getCategoria());
                        }
                    }
                    // Ordenar la lista de transacciones por fecha
                    Collections.sort(transaccionesList, new Comparator<Transaccion>() {
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                        @Override
                        public int compare(Transaccion t2, Transaccion t1) {
                            try {
                                Date date1 = dateFormat.parse(t1.getFecha());
                                Date date2 = dateFormat.parse(t2.getFecha());
                                return date1.compareTo(date2);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            return 0;
                        }
                    });
                    // Notificar al adaptador que los datos han cambiado
                    historialAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar errores de lectura de la base de datos
                    Toast.makeText(HistorialActivity.this, "Error al obtener transacciones: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
