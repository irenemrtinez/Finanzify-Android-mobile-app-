package com.example.finanzify.Presupuestos;

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
import com.example.finanzify.Categorias.CategoriasAdapter;
import com.example.finanzify.Categorias.CategoriasIngresosActivity;
import com.example.finanzify.Clases.Categoria;
import com.example.finanzify.R;
import com.example.finanzify.Transacciones.CategoriasCrearPagoActivity;
import com.example.finanzify.Transacciones.CrearPagoActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoriasCrearPresupuestoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoriasAdapter categoriasAdapter;
    private List<Categoria> categoriasList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias_crear_presupuesto);

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
                String fechaSeleccionada = getIntent().getStringExtra("fechaSeleccionada");
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
                                    Intent intent = new Intent(CategoriasCrearPresupuestoActivity.this, CrearPresupuestoActivity.class);
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
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(CategoriasCrearPresupuestoActivity.this, "Error al obtener categorías: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        //boton volver
        ImageButton buttonVolver = findViewById(R.id.buttonVolver);
        buttonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriasCrearPresupuestoActivity.this, AjustesActivity.class);
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
                // Listas separadas para categorías globales y de pago
                    List<Categoria> categoriasGlobales = new ArrayList<>();
                    List<Categoria> categoriasPago = new ArrayList<>();
                    // Iterar a través de todas las categorías del usuario
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Obtener la categoría actual
                        Categoria categoria = snapshot.getValue(Categoria.class);
                        // Verificar si la categoría es de tipo "global" o "pago"
                        if (categoria != null) {
                            if ("Global".equals(categoria.getTipo())) {
                                categoriasGlobales.add(categoria);
                            } else if ("pago".equals(categoria.getTipo())) {
                                categoriasPago.add(categoria);
                            }
                        }
                    }

                    // Agregar las categorías globales primero
                    categoriasList.addAll(categoriasGlobales);
                    // Luego, agregar las categorías de pago
                    categoriasList.addAll(categoriasPago);

                    // Notificar al adaptador que los datos han cambiado
                    categoriasAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(CategoriasCrearPresupuestoActivity.this, "Error al obtener categorías: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



}
