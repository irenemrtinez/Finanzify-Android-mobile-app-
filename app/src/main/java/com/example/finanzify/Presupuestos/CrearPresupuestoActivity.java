package com.example.finanzify.Presupuestos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finanzify.Clases.Categoria;
import com.example.finanzify.Clases.Limite;
import com.example.finanzify.Historial.HistorialRecurrentesActivity;
import com.example.finanzify.PantallaInicioActivity;
import com.example.finanzify.R;
import com.example.finanzify.Transacciones.CategoriasCrearPagoActivity;
import com.example.finanzify.Transacciones.CrearPagoActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CrearPresupuestoActivity extends AppCompatActivity {
    private TextInputEditText cantidadEditText;
    String recurrencia = null;
    private TextView categoria;
    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_presupuesto);
        // Referencia al TextInputLayout y TextInputEditText de la fecha
        categoria= findViewById(R.id.buttoncategoria);
        cantidadEditText = findViewById(R.id.cantidadid);
        CheckBox checkBoxRenovacion = findViewById(R.id.checkBoxRenovacion);
        boolean renovarLimite = checkBoxRenovacion.isChecked();
        // Verificar si la actividad fue iniciada desde CategoriasCrearPagoActivity

            if (getIntent().hasExtra("categoriaSeleccionada")) {
                String Categoria = getIntent().getStringExtra("categoriaSeleccionada");
                String numCat = getIntent().getStringExtra("numeroCategoria");
                String cantidad = getIntent().getStringExtra("cantidad");
                categoria.setText(Categoria);
            }



            Spinner spinnerLapsos = findViewById(R.id.spinnerLapsos);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.lapsos_tiempo_presu));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLapsos.setAdapter(spinnerAdapter);

        spinnerLapsos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String lapsedTiempo = parentView.getItemAtPosition(position).toString();

                if (lapsedTiempo.equals("Todos los tiempos")) {
                } else if (lapsedTiempo.equals("Semanal")) {
                    recurrencia = "Semanal";
                } else if (lapsedTiempo.equals("Mensual")) {
                    recurrencia = "Mensual";
                } else if (lapsedTiempo.equals("Trimestral")) {
                    recurrencia = "Trimestral";
                } else if (lapsedTiempo.equals("Personalizado")) {
                } else {
                    recurrencia = "anual";
                }
                // Aquí puedes hacer algo con el lapso de tiempo seleccionado
                Toast.makeText(getApplicationContext(), "Lapso de tiempo seleccionado: " + lapsedTiempo, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Aquí puedes manejar la situación en la que no se ha seleccionado nada
            }
        });

        //boton volver
        ImageButton buttonVolver = findViewById(R.id.buttonVolver);
        buttonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CrearPresupuestoActivity.this, PrespuestoActivity.class);
                startActivity(intent);
            }
        });


        categoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los datos de fecha, cantidad y mensaje
                String cantidad = cantidadEditText.getText().toString();
                String categoria = null;
                        categoria = getIntent().getStringExtra("categoriaSeleccionada");
                String numCat = getIntent().getStringExtra("numeroCategoria");

                // Configurar el Intent para iniciar la actividad de selección de categoría
                Intent intent = new Intent(CrearPresupuestoActivity.this, CategoriasCrearPresupuestoActivity.class);
                intent.putExtra("cantidad", cantidad);
                intent.putExtra("categoriaSeleccionada", categoria);
                //intent.putExtra("renovarLimite", renovarLimite);
                intent.putExtra("numeroCategoria", numCat); // Aquí obtenemos la clave de la categoría
                setResult(RESULT_OK, intent);
                startActivity(intent);
                finish();
            }
        });




        Button buttonanadir = findViewById(R.id.buttonanadir);

// Configurar el OnClickListener para el botón "añadir"
        buttonanadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el estado actualizado del CheckBox
                CheckBox checkBoxRenovacion = findViewById(R.id.checkBoxRenovacion);
                boolean renovarLimite = checkBoxRenovacion.isChecked();
                Log.d("presupuesto", "limite: " + renovarLimite);

                // Obtener los datos de fecha, cantidad y mensaje del Intent
                double cantidad = Double.parseDouble(cantidadEditText.getText().toString());
                String numCat = getIntent().getStringExtra("numeroCategoria");
                // PROBLEMA AQUI PORQUE NO HAY NUM CATEGORIA
                Log.d("PieChartAdapter", "Número de elementos en la lista: " + numCat);

                // Obtener la categoría seleccionada usando el número de categoría
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String uid = user.getUid();
                    DatabaseReference categoriasRef = FirebaseDatabase.getInstance().
                            getReference().child("usuarios").child(uid).child("categorias").child(numCat);
                    categoriasRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Obtener la categoría seleccionada
                            Categoria categoria = null;
                            categoria = dataSnapshot.getValue(Categoria.class);
                            // Verificar si la categoría se recuperó correctamente
                            if (categoria != null) {
                                // Llamar a la función para agregar la nueva transacción
                                if (recurrencia!=null)
                                    agregarNuevoPresupuesto(cantidad, categoria,renovarLimite,recurrencia);
                                else
                                    Toast.makeText(CrearPresupuestoActivity.this, "Escoja un lapso de tiempo", Toast.LENGTH_SHORT).show();
                            } else {
                                agregarNuevoPresupuesto(cantidad, null,renovarLimite,recurrencia);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(CrearPresupuestoActivity.this, "Error al obtener la categoría: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });




    }

    private void agregarNuevoPresupuesto(double cantidad, Categoria categoria, boolean renovarLimite, String recurrencia) {
        Log.d("Presupuesto", "cat presupuesto " + categoria.getNombre());
        // Obtener el UID del usuario actual
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserUID = mAuth.getCurrentUser().getUid();

        // Obtener la fecha actual
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaInicio = sdf.format(new Date());

        // Obtenemos la referencia a la base de datos
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Obtener una referencia al nodo de presupuestos del usuario actual
        DatabaseReference presupuestosRef = databaseRef.child("usuarios").child(currentUserUID).child("presupuestos");

        // Escuchar una vez para obtener el número total de presupuestos
        presupuestosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nuevaClave = generarClaveAleatoria(10);
                DatabaseReference presupuestoRef = presupuestosRef.child(nuevaClave);



                // Creamos un objeto Presupuesto con los datos del nuevo presupuesto
                Limite presupuesto;
                if (categoria != null) {
                    // Si hay una categoría seleccionada
                    presupuesto = new Limite(categoria, cantidad, fechaInicio, renovarLimite, recurrencia, nuevaClave);
                    Log.d("Presupuesto", "cat presupuesto " + presupuesto.getCategoria());
                } else {
                    // Si no hay categoría seleccionada
                    Categoria cat = new Categoria("general", "","sin categoria");
                    presupuesto = new Limite(cat, cantidad, fechaInicio, renovarLimite, recurrencia,nuevaClave);
                    Log.d("Presupuesto", "cat presupuesto " + presupuesto.getCategoria());

                }

                // Guardamos los datos del nuevo presupuesto en la base de datos
                presupuestoRef.setValue(presupuesto);

                // Mostramos un mensaje de éxito
                Toast.makeText(CrearPresupuestoActivity.this, "Presupuesto creado exitosamente", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores si los hay
                Toast.makeText(CrearPresupuestoActivity.this, "Error al obtener el número de presupuestos: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

    public static String generarClaveUnica(DatabaseReference presupuestosRef, int longitud) {
        final boolean[] claveExistente = {false}; // Declarar como un array de un solo elemento

        String nuevaClave;
        do {
            nuevaClave = generarClaveAleatoria(longitud);

            // Verificar si la clave ya existe en la base de datos
            presupuestosRef.child(nuevaClave).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Si la clave existe, establecer el primer elemento del array como verdadero
                    claveExistente[0] = dataSnapshot.exists();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar errores de la base de datos, si los hay
                }
            });

        } while (claveExistente[0]);

        return nuevaClave;
    }




}