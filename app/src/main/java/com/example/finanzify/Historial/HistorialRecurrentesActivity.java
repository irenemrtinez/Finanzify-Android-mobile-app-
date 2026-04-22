package com.example.finanzify.Historial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finanzify.Clases.Transaccion;
import com.example.finanzify.Clases.TransaccionRecurrente;
import com.example.finanzify.PantallaInicioActivity;
import com.example.finanzify.R;
import com.example.finanzify.TransaccionesRecurrentes.PagoRecurrenteActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistorialRecurrentesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistorialRecurrenteAdapter historialAdapter;
    private List<TransaccionRecurrente> transaccionesRecurrentesList;

    TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_recurrentes);
        // Inicializar RecyclerView y su adaptador
        recyclerView = findViewById(R.id.recyclerViewHistorial);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        transaccionesRecurrentesList = new ArrayList<>();

        historialAdapter = new HistorialRecurrenteAdapter(transaccionesRecurrentesList);
        recyclerView.setAdapter(historialAdapter);

        // Obtener las transacciones recurrentes del usuario actual
        obtenerTransaccionesRecurrentesUsuario();

        //boton historial recurrente
        textview = findViewById(R.id.textNorec);
        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistorialRecurrentesActivity.this, HistorialActivity.class);
                startActivity(intent);
            }
        });

        //boton volver
        ImageButton buttonVolver = findViewById(R.id.buttonVolver);
        buttonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistorialRecurrentesActivity.this, PantallaInicioActivity.class);
                startActivity(intent);
            }
        });


        historialAdapter.setOnItemClickListener(new HistorialRecurrenteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TransaccionRecurrente transaccionRecurrente) {
                Dialog dialog = new Dialog(HistorialRecurrentesActivity.this);
                dialog.setContentView(R.layout.dialog_historial_recurrente);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);

                // Obtener referencias a los TextView en el diseño del diálogo
                TextView tipoTransaccionTextView = dialog.findViewById(R.id.transaccion);
                TextView fechaTextViewI = dialog.findViewById(R.id.FechaI);
                TextView fechaTextViewF = dialog.findViewById(R.id.FechaF);
                TextView recurrenciaTextView = dialog.findViewById(R.id.recurrencia);
                TextView cantidadTextView = dialog.findViewById(R.id.Cantidad);
                TextView mensajeTextView = dialog.findViewById(R.id.Mensaje);
                TextView categoriaTextView = dialog.findViewById(R.id.Categoria);

                // Establecer el texto de cada TextView con los valores de la transacción
                tipoTransaccionTextView.setText(transaccionRecurrente.getTipo());
                fechaTextViewI.setText(transaccionRecurrente.getFechaInicio());
                fechaTextViewF.setText(transaccionRecurrente.getFechaFin());
                recurrenciaTextView.setText(transaccionRecurrente.getRecurrencia());
                cantidadTextView.setText(String.valueOf(transaccionRecurrente.getCantidad()));
                mensajeTextView.setText(transaccionRecurrente.getMensaje());
                categoriaTextView.setText(transaccionRecurrente.getCategoria().getNombre());

                Button okButton = dialog.findViewById(R.id.buttonaOk);
                // Agregar OnClickListener al botón OK para cerrar el diálogo
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss(); // Cerrar el diálogo
                    }
                });
                Button ElimButton = dialog.findViewById(R.id.buttonaEliminar);
                // Agregar OnClickListener al botón OK para cerrar el diálogo
                ElimButton.setOnClickListener(new View.OnClickListener() {
                    //aqui eliminar el presupuesto recurrente
                    @Override
                    public void onClick(View view) {

                        // Recuperar la clave única de la transacción recurrente
                        String claveTransaccion = transaccionRecurrente.getClave();
                        Calendar calendar = Calendar.getInstance();
                        java.util.Date currentDate = calendar.getTime();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                       // String formattedDate = "25/05/2024";
                        String formattedDate = dateFormat.format(currentDate);
                        // Actualizar la fecha fin de la transacción a la fecha actual
                        transaccionRecurrente.setFechaFin(formattedDate);

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUserUID = user.getUid();
                        DatabaseReference presupuestosRef = FirebaseDatabase.getInstance().getReference()
                                .child("usuarios").child(currentUserUID).child("transaccionesRecurrentes");
                        DatabaseReference transRef = presupuestosRef.child(transaccionRecurrente.getClave());
                        // Eliminar el presupuesto de la base de datos
                        transRef.removeValue();
                        // Actualizar la transacción en la base de datos
                        dialog.dismiss(); // Cerrar el diálogo
                    }
                });

                dialog.show();

            }

        });

    }


    private void obtenerTransaccionesRecurrentesUsuario() {
        // Obtener el ID del usuario actual
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            // Referencia a las transacciones recurrentes del usuario en la base de datos
            DatabaseReference transaccionesRef = FirebaseDatabase.getInstance().getReference()
                    .child("usuarios").child(uid).child("transaccionesRecurrentes");

            // Leer las transacciones recurrentes del usuario
            transaccionesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Limpiar la lista actual de transacciones recurrentes
                    transaccionesRecurrentesList.clear();

                    // Iterar a través de las transacciones recurrentes y agregarlas a la lista
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        TransaccionRecurrente transaccionRecurrente = snapshot.getValue(TransaccionRecurrente.class);
                        if (transaccionRecurrente != null) {
                            // Agregar la transacción recurrente a la lista
                            Calendar calendar = Calendar.getInstance();
                            java.util.Date currentDate = calendar.getTime();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            //String formattedDate = "25/05/2024";
                            String formattedDate = dateFormat.format(currentDate);
                            //if(!esFechaAnteriorOIgual(transaccionRecurrente.getFechaFin(),formattedDate) || dateFormat.equals(transaccionRecurrente.getFechaFin())) {
                                transaccionesRecurrentesList.add(transaccionRecurrente);
                            //}
                        }
                    }
                    // Ordenar la lista de transacciones recurrentes por fecha de inicio
                    Collections.sort(transaccionesRecurrentesList, new Comparator<TransaccionRecurrente>() {
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                        @Override
                        public int compare(TransaccionRecurrente t2, TransaccionRecurrente t1) {
                            try {
                                Date date1 = dateFormat.parse(t1.getFechaInicio());
                                Date date2 = dateFormat.parse(t2.getFechaInicio());
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
                    Toast.makeText(HistorialRecurrentesActivity.this, "Error al obtener transacciones recurrentes: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean esFechaAnteriorOIgual(String fechaSeleccionada, String fechaActual) {
        // Parsear las fechas a objetos Date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date dateSeleccionada = sdf.parse(fechaSeleccionada);
            Date dateActual = sdf.parse(fechaActual);
            // Comparar las fechas
            return !dateSeleccionada.after(dateActual);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}