package com.example.finanzify.TransaccionesRecurrentes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finanzify.Clases.Categoria;
import com.example.finanzify.Clases.TransaccionRecurrente;
import com.example.finanzify.R;
import com.example.finanzify.Transacciones.CategoriasCrearIngresoActivity;
import com.example.finanzify.Transacciones.CategoriasCrearPagoActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.SecureRandom;
import java.util.Calendar;

public class IngresoRecurrenteActivity extends AppCompatActivity {
    private Button buttonanadir;

    private TextView fechaI,fechaF, categoria;
    private TextInputEditText cantidadEditText, mensajeEditText;
    private String lapsedTiempo = null;
    private DatePickerDialog.OnDateSetListener dateSetListenerInicio, dateSetListenerFin;
    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso_recurrente);

        // Referencia al TextInputLayout y TextInputEditText de la fecha
        categoria = findViewById(R.id.buttoncategoria);
        fechaI = findViewById(R.id.buttonfechaI);
        fechaF = findViewById(R.id.buttonfechaF);
        cantidadEditText = findViewById(R.id.cantidadid);
        mensajeEditText = findViewById(R.id.Mensajeid);
        buttonanadir = findViewById(R.id.buttonanadir);

        // Obtener datos enviados desde CategoriasCrearPagoActivity
        String categoriaSeleccionada = getIntent().getStringExtra("categoriaSeleccionada");
        String numeroCategoria = getIntent().getStringExtra("numeroCategoria");
        String fechaSeleccionadaInicio = getIntent().getStringExtra("fechaSeleccionadaInicio");
        String fechaSeleccionadaFin = getIntent().getStringExtra("fechaSeleccionadaFin");
        String cantidad = getIntent().getStringExtra("cantidad");
        String mensaje = getIntent().getStringExtra("mensaje");

        // Establecer los valores recibidos en los campos correspondientes
        categoria.setText(categoriaSeleccionada);
        fechaI.setText(fechaSeleccionadaInicio);
        fechaF.setText(fechaSeleccionadaFin);
        cantidadEditText.setText(cantidad);
        mensajeEditText.setText(mensaje);

        Spinner spinnerLapsos = findViewById(R.id.spinnerLapsos);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lapsos_tiempo, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLapsos.setAdapter(adapter);
        spinnerLapsos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                lapsedTiempo = parentView.getItemAtPosition(position).toString();
                // Aquí puedes hacer algo con el lapso de tiempo seleccionado
                Toast.makeText(getApplicationContext(), "Lapso de tiempo seleccionado: " + lapsedTiempo, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Aquí puedes manejar la situación en la que no se ha seleccionado nada
            }
        });
// Configuración del OnClickListener para el TextInputLayout de la fecha inicio
        fechaI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener la fecha actual
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // Crear un DatePickerDialog y mostrarlo
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        IngresoRecurrenteActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListenerInicio, // Usar el dateSetListenerInicio
                        year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        // Configuración del dateSetListenerInicio para actualizar el TextInputEditText de la fecha inicio cuando se elija una fecha en el DatePickerDialog
        dateSetListenerInicio = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Incrementar el mes en 1 porque en el DatePickerDialog los meses comienzan en 0
                month = month + 1;
                // Formatear la fecha seleccionada
                String date = dayOfMonth + "/" + month + "/" + year;
                // Establecer la fecha seleccionada en el TextInputEditText de la fecha inicio
                fechaI.setText(date);
            }
        };

        // Configuración del OnClickListener para el TextInputLayout de la fecha fin
        fechaF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener la fecha actual
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // Crear un DatePickerDialog y mostrarlo
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        IngresoRecurrenteActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListenerFin, // Usar el dateSetListenerFin
                        year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        // Configuración del dateSetListenerFin para actualizar el TextInputEditText de la fecha fin cuando se elija una fecha en el DatePickerDialog
        dateSetListenerFin = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Incrementar el mes en 1 porque en el DatePickerDialog los meses comienzan en 0
                month = month + 1;
                // Formatear la fecha seleccionada
                String date = dayOfMonth + "/" + month + "/" + year;
                // Establecer la fecha seleccionada en el TextInputEditText de la fecha fin
                fechaF.setText(date);
            }
        };
        categoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los datos de fecha, cantidad y mensaje
                String fechaSeleccionadai = fechaI.getText().toString();
                String fechaSeleccionadaf = fechaI.getText().toString();
                String cantidad = cantidadEditText.getText().toString();
                String mensaje = mensajeEditText.getText().toString();
                String categoria = getIntent().getStringExtra("categoriaSeleccionada");
                String numCat = getIntent().getStringExtra("numeroCategoria");

                // Configurar el Intent para iniciar la actividad de selección de categoría
                Intent intent = new Intent(IngresoRecurrenteActivity.this, CategoriasCrearIngresoActivity.class);
                intent.putExtra("fechaSeleccionadaInicio", fechaSeleccionadai);
                intent.putExtra("fechaSeleccionadaFin", fechaSeleccionadaf);
                intent.putExtra("cantidad", cantidad);
                intent.putExtra("mensaje", mensaje);
                intent.putExtra("categoriaSeleccionada", categoria);
                intent.putExtra("numeroCategoria", numCat); // Aquí obtenemos la clave de la categoría
                setResult(RESULT_OK, intent);
                startActivity(intent);
                finish();
            }
        });

        // Configuración del OnClickListener para el botón "añadir"
        buttonanadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los datos de fecha, cantidad y mensaje
                String fechaSeleccionadai = fechaI.getText().toString();
                String fechaSeleccionadaf = fechaF.getText().toString();
                double cantidad = Double.parseDouble(cantidadEditText.getText().toString());
                String mensaje = mensajeEditText.getText().toString();
                String numCat = getIntent().getStringExtra("numeroCategoria");

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
                                guardarPagoRecurrente(fechaSeleccionadai, fechaSeleccionadaf, cantidad, mensaje, numCat, lapsedTiempo, categoria);
                            } else {
                                Toast.makeText(IngresoRecurrenteActivity.this, "Error: No se pudo recuperar la categoría seleccionada", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(IngresoRecurrenteActivity.this, "Error al obtener la categoría: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                finish();
            }
        });

    }

    public static String generarClaveAleatoria(int longitud) {
        StringBuilder clave = new StringBuilder();
        for (int i = 0; i < longitud; i++) {
            int indice = RANDOM.nextInt(CARACTERES.length());
            clave.append(CARACTERES.charAt(indice));
        }
        return clave.toString();
    }

    private void guardarPagoRecurrente(String fechaInicio, String fechaFin, double cantidad, String mensaje, String numCat, String recurrencia, Categoria categoria) {
        // Obtener el UID del usuario actual
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserUID = mAuth.getCurrentUser().getUid();
        // Obtenemos la referencia a la base de datos
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        // Obtener una referencia al nodo de transacciones del usuario actual
        DatabaseReference transaccionesRef = databaseRef.child("usuarios").child(currentUserUID).child("transaccionesRecurrentes");

        // Escuchar una vez para obtener el número total de transacciones
        transaccionesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Obtener el número de transacciones actuales
                long numTransacciones = dataSnapshot.getChildrenCount();

                // Creamos un nuevo nodo para la transacción bajo el UID del usuario
                String nuevaClave = generarClaveAleatoria(10);

                DatabaseReference transaccionRef = transaccionesRef.child(nuevaClave);

                // Creamos un objeto Transaccion con los datos de la nueva transacción
                //Transaccion transaccion = new Transaccion(fecha, cantidad, categoria, mensaje,"pago");
                TransaccionRecurrente transaccion = new TransaccionRecurrente(fechaInicio,fechaFin,cantidad,mensaje,categoria, recurrencia, "ingresos",nuevaClave,true);
                // Guardamos los datos de la nueva transacción en la base de datos
                transaccionRef.setValue(transaccion);
                //programarPagoRecurrente(fechaInicio,fechaFin,cantidad, recurrencia,nuevaClave);
                // Mostramos un mensaje de éxito
                Toast.makeText(IngresoRecurrenteActivity.this, "Transacción recurrentecreada exitosamente", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores si los hay
                Toast.makeText(IngresoRecurrenteActivity.this, "Error al obtener el número de transacciones: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}