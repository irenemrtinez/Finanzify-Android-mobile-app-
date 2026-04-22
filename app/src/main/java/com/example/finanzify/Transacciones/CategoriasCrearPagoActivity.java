package com.example.finanzify.Transacciones;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finanzify.Ajustes.AjustesActivity;
import com.example.finanzify.Ajustes.EditPerfilActivity;
import com.example.finanzify.Categorias.CategoriasAdapter;
import com.example.finanzify.Categorias.CategoriasIngresosActivity;
import com.example.finanzify.Clases.Categoria;
import com.example.finanzify.R;
import com.example.finanzify.TransaccionesRecurrentes.PagoRecurrenteActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class CategoriasCrearPagoActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CategoriasAdapter categoriasAdapter;
    private List<Categoria> categoriasList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias_crear_pago);

        recyclerView = findViewById(R.id.recyclerViewCategorias);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar la lista de categorías
        categoriasList = new ArrayList<>();

        // Crear un adaptador y configurarlo en el RecyclerView
        categoriasAdapter = new CategoriasAdapter(categoriasList);
        recyclerView.setAdapter(categoriasAdapter);

        // Obtener las categorías del usuario actual
        obtenerCategoriasUsuario();

        categoriasAdapter.setOnItemClickListener(new CategoriasAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Obtener la categoría seleccionada
                Categoria categoriaSeleccionada = categoriasList.get(position);

                // Obtener los datos de fecha, cantidad y mensaje del Intent
                String cantidad = getIntent().getStringExtra("cantidad");
                String mensaje = getIntent().getStringExtra("mensaje");


                // Obtener la clave de la categoría seleccionada desde DataSnapshot
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String uid = user.getUid();
                    DatabaseReference categoriasRef = FirebaseDatabase.getInstance().getReference().child("usuarios").child(uid).child("categorias");
                    categoriasRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Categoria categoria = snapshot.getValue(Categoria.class);
                                if (categoria != null && categoria.getNombre().equals(categoriaSeleccionada.getNombre())) {
                                    // Configurar el Intent para pasar los datos de vuelta a la actividad de creación de pago
                                    if (getIntent().getStringExtra("fechaSeleccionada") != null)
                                    {
                                        String fechaSeleccionada = getIntent().getStringExtra("fechaSeleccionada");
                                        Intent intent = new Intent(CategoriasCrearPagoActivity.this, CrearPagoActivity.class);
                                        intent.putExtra("categoriaSeleccionada", categoria.getNombre());
                                        intent.putExtra("numeroCategoria", snapshot.getKey()); // Aquí obtenemos la clave de la categoría
                                        intent.putExtra("fechaSeleccionada", fechaSeleccionada);
                                        intent.putExtra("cantidad", cantidad);
                                        intent.putExtra("mensaje", mensaje);
                                        setResult(RESULT_OK, intent);
                                        // Finalizar esta actividad y volver a la actividad de creación de pago
                                        startActivity(intent);
                                        finish();
                                        return;
                                    } else {
                                        String fechaSeleccionadai = getIntent().getStringExtra("fechaSeleccionadaInicio");
                                        String fechaSeleccionadaf = getIntent().getStringExtra("fechaSeleccionadaFin");
                                        Intent intent = new Intent(CategoriasCrearPagoActivity.this, PagoRecurrenteActivity.class);
                                        intent.putExtra("categoriaSeleccionada", categoria.getNombre());
                                        intent.putExtra("numeroCategoria", snapshot.getKey()); // Aquí obtenemos la clave de la categoría
                                        intent.putExtra("fechaSeleccionadaInicio", fechaSeleccionadai);
                                        intent.putExtra("fechaSeleccionadaFin", fechaSeleccionadaf);
                                        intent.putExtra("cantidad", cantidad);
                                        intent.putExtra("mensaje", mensaje);
                                        setResult(RESULT_OK, intent);
                                        // Finalizar esta actividad y volver a la actividad de creación de pago
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(CategoriasCrearPagoActivity.this, "Error al obtener categorías: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        // boton ingresos
        TextView textViewIngresos = findViewById(R.id.textViewIngresos);
        textViewIngresos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para abrir la nueva actividad
                Intent intent = new Intent(CategoriasCrearPagoActivity.this, CategoriasIngresosActivity.class);
                // Iniciar la nueva actividad
                startActivity(intent);
            }
        });
        // fin boton ingresos

        //boton volver
        ImageButton buttonVolver = findViewById(R.id.buttonVolver);
        buttonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriasCrearPagoActivity.this, AjustesActivity.class);
                startActivity(intent);
            }
        });
        // fin boton volver



    }

    private void obtenerCategoriasUsuario() {
        // Obtener el ID del usuario actual
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            // Referencia a la base de datos
            DatabaseReference categoriasRef = FirebaseDatabase.getInstance().getReference().child("usuarios").child(uid).child("categorias");

            // Escuchar los cambios en los datos de las categorías del usuario
            categoriasRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Limpiar la lista actual de categorías
                    categoriasList.clear();

                    // Iterar a través de todas las categorías del usuario
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Obtener la categoría actual
                        Categoria categoria = snapshot.getValue(Categoria.class);
                        // Verificar si la categoría es de tipo "ingresos"
                        if (categoria != null && "pago".equals(categoria.getTipo())) {
                            // Agregar la categoría a la lista solo si es de tipo "pago"
                            categoriasList.add(categoria);
                            // Imprimir el nombre de la categoría
                            System.out.println("Nombre de la categoría: " + categoria.getNombre());
                        }
                    }

                    // Notificar al adaptador que los datos han cambiado
                    categoriasAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(CategoriasCrearPagoActivity.this, "Error al obtener categorías: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



}
