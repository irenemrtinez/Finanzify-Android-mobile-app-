package com.example.finanzify.TransaccionesRecurrentes;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.finanzify.Clases.Categoria;
import com.example.finanzify.Clases.Transaccion;
import com.example.finanzify.Clases.TransaccionRecurrente;
import com.example.finanzify.Transacciones.CrearPagoActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class TransaccionRecurrenteWorker extends Worker {
    Context context = getApplicationContext();
    public TransaccionRecurrenteWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Obtener los datos necesarios para realizar el pago
        String fechaInicio = getInputData().getString("fechaInicio");
        double cantidad = getInputData().getDouble("cantidad", 0.0);
        String recurrencia = getInputData().getString("recurrencia");
        String clave = getInputData().getString("clave");
        // Aquí colocarías la lógica para efectuar el pago recurrente
        // Por ejemplo, podrías guardar un registro de la transacción en la base de datos
        // o interactuar con un servicio de pago externo

        // Por ahora, simplemente imprimiremos los datos obtenidos
        Log.d("TransaccionRecurrenteWorker", "Fecha de inicio: " + fechaInicio);
        Log.d("TransaccionRecurrenteWorker", "Cantidad: " + cantidad);
        Log.d("TransaccionRecurrenteWorker", "Recurrencia: " + recurrencia);
        ObtenerTR(clave, fechaInicio);

        // Indica que la tarea se ha completado exitosamente
        return Result.success();
    }

    private void ObtenerTR(String clavePagoRecurrente,String fechaInicio) {
        Context context = getApplicationContext();
        // Obtener una referencia al pago recurrente utilizando la clave
        DatabaseReference pagoRecurrenteRef = FirebaseDatabase.getInstance().getReference()
                .child("usuarios")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("transaccionesRecurrentes")
                .child(clavePagoRecurrente);

        // Escuchar una vez para obtener los detalles del pago recurrente
        pagoRecurrenteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Verificar si existe el pago recurrente con la clave proporcionada
                if (dataSnapshot.exists()) {
                    // Obtener los detalles del pago recurrente
                    //String fechaInicio = dataSnapshot.child("fechaInicio").getValue(String.class);
                    String fechaFin = dataSnapshot.child("fechaFin").getValue(String.class);
                    double cantidad = dataSnapshot.child("cantidad").getValue(Double.class);
                    String mensaje = dataSnapshot.child("mensaje").getValue(String.class);
                    String numCat = dataSnapshot.child("numCat").getValue(String.class);
                    String recurrencia = dataSnapshot.child("recurrencia").getValue(String.class);
                    String tipo = dataSnapshot.child("tipo").getValue(String.class);
                    Categoria categoria = dataSnapshot.child("categoria").getValue(Categoria.class);
                    agregarNuevaTransaccion(fechaInicio,cantidad,categoria,
                            mensaje + " Transaccion creada a partir de una transacción recurrente",tipo);
                    // Actualizar o crear la lista fechasPagosRealizados
                    TransaccionRecurrente transaccionRecurrente = dataSnapshot.getValue(TransaccionRecurrente.class);
                    if (transaccionRecurrente != null && transaccionRecurrente.getFechasPagosRealizados() != null) {
                        transaccionRecurrente.getFechasPagosRealizados().add(obtenerFechaActual());
                    } else {
                        transaccionRecurrente = new TransaccionRecurrente();
                        transaccionRecurrente.setFechasPagosRealizados(new ArrayList<>(Collections.singletonList(obtenerFechaActual())));
                    }

                    // Actualizar la lista fechasPagosRealizados en la base de datos
                    pagoRecurrenteRef.child("fechasPagosRealizados").setValue(transaccionRecurrente.getFechasPagosRealizados());


                } else {
                    // No se encontró ningún pago recurrente con la clave proporcionada
                    Toast.makeText(context, "No se encontró ningún pago recurrente con la clave proporcionada", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores si los hay
                Toast.makeText(context, "Error al obtener el pago recurrente: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para obtener la fecha actual en el formato deseado
    private String obtenerFechaActual() {
        // Obtener la fecha actual
        Date fechaActual = new Date();
        // Formatear la fecha en el formato deseado
        SimpleDateFormat formatoFecha = new SimpleDateFormat("d/M/yyyy");
        String fechaFormateada = formatoFecha.format(fechaActual);

        return fechaFormateada;
    }

    private void agregarNuevaTransaccion(String fecha, double cantidad, Categoria categoria, String mensaje, String tipo) {
        // Obtener el UID del usuario actual
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserUID = mAuth.getCurrentUser().getUid();

        // Obtenemos la referencia a la base de datos
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Obtener una referencia al nodo de transacciones del usuario actual
        DatabaseReference transaccionesRef = databaseRef.child("usuarios").child(currentUserUID).child("transacciones");

        // Escuchar una vez para obtener el número total de transacciones
        transaccionesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Obtener el número de transacciones actuales
                long numTransacciones = dataSnapshot.getChildrenCount();
                // Creamos un nuevo nodo para la transacción bajo el UID del usuario
                String nuevaClave = String.valueOf(numTransacciones + 1); // Siguiente número de transacción
                DatabaseReference transaccionRef = transaccionesRef.child(nuevaClave);

                // Creamos un objeto Transaccion con los datos de la nueva transacción
                Transaccion transaccion = new Transaccion(fecha, cantidad, categoria, mensaje,tipo,true);
                // Guardamos los datos de la nueva transacción en la base de datos
                transaccionRef.setValue(transaccion);
                actualizarBalance(cantidad,tipo);
                // Mostramos un mensaje de éxito
                Toast.makeText(context, "Transacción creada exitosamente", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores si los hay
                Toast.makeText(context, "Error al obtener el número de transacciones: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarBalance(double cantidad, String tipo) {
        // Obtener el UID del usuario actual
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserUID = mAuth.getCurrentUser().getUid();

        // Obtenemos la referencia a la base de datos
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Obtener una referencia al nodo de balance del usuario actual
        DatabaseReference balanceRef = databaseRef.child("usuarios").child(currentUserUID).child("balanceTotal");

        // Escuchar una vez para obtener el balance actual
        balanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double balanceActual = snapshot.getValue(Double.class);

                double nuevoBalance;

                // Si el balanceActual es null, establecerlo en 0.0
                if (balanceActual == null) {
                    balanceActual = 0.0;
                }

                // Calcular el nuevo balance sumando o restando la cantidad de la transacción
                if (tipo.equals("pago"))
                nuevoBalance = balanceActual - cantidad; // Cambiado a suma según el enunciado
                else
                    nuevoBalance = balanceActual + cantidad; // Cambiado a suma según el enunciado
                // Actualizar el balance en la base de datos
                Log.d("TransaccionRecurrenteWorker", "Balance " + nuevoBalance);
                balanceRef.setValue(nuevoBalance);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores si los hay
                Toast.makeText(context, "Error al obtener el balance: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
