package com.example.finanzify.Categorias;

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
import com.example.finanzify.Clases.Categoria;
import com.example.finanzify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoriasIngresosActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CategoriasAdapter categoriasAdapter;
    private List<Categoria> categoriasList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias_ingresos);

        recyclerView = findViewById(R.id.recyclerViewCategorias);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar la lista de categorías
        categoriasList = new ArrayList<>();

        // Crear un adaptador y configurarlo en el RecyclerView
        categoriasAdapter = new CategoriasAdapter(categoriasList);
        recyclerView.setAdapter(categoriasAdapter);

        // Obtener las categorías del usuario actual
        obtenerCategoriasUsuario();

        // boton ingresos
        TextView textViewPagos = findViewById(R.id.textViewPagos);
        textViewPagos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para abrir la nueva actividad
                Intent intent = new Intent(CategoriasIngresosActivity.this, CategoriasPagosActivity.class);
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
                Intent intent = new Intent(CategoriasIngresosActivity.this, AjustesActivity.class);
                startActivity(intent);
            }
        });
        // fin

        // boton añadir
        ImageButton buttonanadir = findViewById(R.id.buttonAnadir);
        buttonanadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriasIngresosActivity.this, CrearCategoriasActivity.class);
                startActivity(intent);
            }
        });
        // fin boton añadir
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
                        if (categoria != null && "ingresos".equals(categoria.getTipo())) {
                            // Agregar la categoría a la lista solo si es de tipo "ingresos"
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
                    Toast.makeText(CategoriasIngresosActivity.this, "Error al obtener categorías: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}