package com.example.finanzify.Transacciones;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finanzify.Clases.Categoria;
import com.example.finanzify.Clases.Transaccion;
import com.example.finanzify.R;
import com.example.finanzify.TransaccionesRecurrentes.IngresoRecurrenteActivity;
import com.example.finanzify.TransaccionesRecurrentes.PagoRecurrenteActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CrearIngresoActivity extends AppCompatActivity {

    TextView fecha, categoria;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TextInputEditText  cantidadEditText, mensajeEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_ingreso);

        // Referencia al TextInputLayout y TextInputEditText de la fecha
        categoria= findViewById(R.id.buttoncategoria);
        fecha = findViewById(R.id.buttonfecha);
        cantidadEditText = findViewById(R.id.cantidadid);
        mensajeEditText = findViewById(R.id.Mensajeid);

        // Verificar si la actividad fue iniciada desde CategoriasCrearIngresoActivity
        if (getIntent().hasExtra("fechaSeleccionada")) {
            // La actividad fue iniciada desde CategoriasCrearIngresoActivity
            String fechaSeleccionada = getIntent().getStringExtra("fechaSeleccionada");
            String cantidad = getIntent().getStringExtra("cantidad");
            String mensaje = getIntent().getStringExtra("mensaje");

            if (getIntent().hasExtra("categoriaSeleccionada")) {
                String Categoria = getIntent().getStringExtra("categoriaSeleccionada");
                String numCat = getIntent().getStringExtra("numeroCategoria");
                categoria.setText(Categoria);
            }

            // Establecer los valores recuperados en los campos correspondientes
            fecha.setText(fechaSeleccionada);
            cantidadEditText.setText(cantidad);
            mensajeEditText.setText(mensaje);
        }

        // Configuración del OnClickListener para el TextInputLayout de la fecha
        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener la fecha actual
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // Crear un DatePickerDialog y mostrarlo
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        CrearIngresoActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener,
                        year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        // Configuración del dateSetListener para actualizar el TextInputEditText de la fecha cuando se elija una fecha en el DatePickerDialog
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Incrementar el mes en 1 porque en el DatePickerDialog los meses comienzan en 0
                month = month + 1;
                // Formatear la fecha seleccionada
                String date = dayOfMonth + "/" + month + "/" + year;
                // Establecer la fecha seleccionada en el TextInputEditText de la fecha
                fecha.setText(date);
            }
        };

        // Obtener las referencias de los botones
        Button Recurrente= findViewById(R.id.buttonRec);
        Recurrente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fechaSeleccionada = fecha.getText().toString();
                String cantidad = cantidadEditText.getText().toString();
                String mensaje = mensajeEditText.getText().toString();
                // Crear un Intent para abrir la nueva actividad
                Intent intent = new Intent(CrearIngresoActivity.this, IngresoRecurrenteActivity.class);
                // Iniciar la nueva actividad

                intent.putExtra("fechaSeleccionada", fechaSeleccionada);
                intent.putExtra("cantidad", cantidad);
                intent.putExtra("mensaje", mensaje);
                setResult(RESULT_OK, intent);
                startActivity(intent);
                finish();
            }
        });

         categoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los datos de fecha, cantidad y mensaje
                String fechaSeleccionada = fecha.getText().toString();
                String cantidad = cantidadEditText.getText().toString();
                String mensaje = mensajeEditText.getText().toString();

                // Configurar el Intent para iniciar la actividad de selección de categoría
                Intent intent = new Intent(CrearIngresoActivity.this,CategoriasCrearIngresoActivity.class);
                intent.putExtra("fechaSeleccionada", fechaSeleccionada);
                intent.putExtra("cantidad", cantidad);
                intent.putExtra("mensaje", mensaje);
                setResult(RESULT_OK, intent);
                startActivity(intent);
                finish();
            }
        });

        //boton para crear pago ingresos
        TextView textViewIngresos = findViewById(R.id.textViewIngresos);
        textViewIngresos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fechaSeleccionada = fecha.getText().toString();
                String cantidad = cantidadEditText.getText().toString();
                String mensaje = mensajeEditText.getText().toString();
                // Crear un Intent para abrir la nueva actividad
                Intent intent = new Intent(CrearIngresoActivity.this, CrearIngresoActivity.class);
                // Iniciar la nueva actividad
                intent.putExtra("fechaSeleccionada", fechaSeleccionada);
                intent.putExtra("cantidad", cantidad);
                intent.putExtra("mensaje", mensaje);
                setResult(RESULT_OK, intent);
                startActivity(intent);
                finish();
            }
        });


        // añadir pago
        // Obtener las referencias de los botones
        Button buttonanadir = findViewById(R.id.buttonanadir);

        // Configurar el OnClickListener para el botón "añadir"
        buttonanadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los datos de fecha, cantidad y mensaje del Intent
                String fechaSeleccionada = fecha.getText().toString();
                double cantidad = Double.parseDouble(cantidadEditText.getText().toString());
                String mensaje = mensajeEditText.getText().toString();
                String numCat = getIntent().getStringExtra("numeroCategoria");

                // Obtener la categoría seleccionada usando el número de categoría
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String uid = user.getUid();
                    DatabaseReference categoriasRef = FirebaseDatabase.getInstance().getReference().child("usuarios").child(uid).child("categorias").child(numCat);
                    categoriasRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Obtener la categoría seleccionada
                            Categoria categoria = dataSnapshot.getValue(Categoria.class);
                            // Verificar si la categoría se recuperó correctamente
                            if (categoria != null) {
                                // Llamar a la función para agregar la nueva transacción
                                agregarNuevaTransaccion(fechaSeleccionada, cantidad, categoria, mensaje);
                                actualizarBalance(cantidad);
                            } else {
                                Toast.makeText(CrearIngresoActivity.this, "Error: No se pudo recuperar la categoría seleccionada", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(CrearIngresoActivity.this, "Error al obtener la categoría: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });



    }

    // codigo para crear la transaccion
    private void agregarNuevaTransaccion(String fecha, double cantidad, Categoria categoria, String mensaje) {
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

                Calendar calendar = Calendar.getInstance();
                String fechaActual = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
                // Log de la fecha actual y la fecha seleccionada
                Log.d("Fecha", "Fecha actual: " + fechaActual);
                Log.d("Fecha", "Fecha seleccionada: " + fecha);
                if (esFechaAnteriorOIgual(fecha, fechaActual)) {
                    actualizarBalance(cantidad);
                    Transaccion transaccion = new Transaccion(fecha, cantidad, categoria, mensaje, "ingresos", true);
                    transaccionRef.setValue(transaccion); // Guardamos los datos de la nueva transacción en la base de datos
                } else {
                    // Creamos un objeto Transaccion con los datos de la nueva transacción
                    Transaccion transaccion = new Transaccion(fecha, cantidad, categoria, mensaje, "ingresos", false);
                    transaccionRef.setValue(transaccion); // Guardamos los datos de la nueva transacción en la base de datos
                }


                // Mostramos un mensaje de éxito
                Toast.makeText(CrearIngresoActivity.this, "Transacción creada exitosamente", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores si los hay
                Toast.makeText(CrearIngresoActivity.this, "Error al obtener el número de transacciones: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para verificar si la fecha seleccionada es anterior o igual a la fecha actual
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

    private void actualizarBalance(double cantidad) {
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
                nuevoBalance = balanceActual + cantidad; // Cambiado a suma según el enunciado

                // Actualizar el balance en la base de datos
                balanceRef.setValue(nuevoBalance);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores si los hay
                Toast.makeText(CrearIngresoActivity.this, "Error al obtener el balance: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




}