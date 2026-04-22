package com.example.finanzify.Categorias;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finanzify.Clases.Categoria;
import com.example.finanzify.Login.RegistrarActivity;
import com.example.finanzify.PantallaInicioActivity;
import com.example.finanzify.Presupuestos.PrespuestoActivity;
import com.example.finanzify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CrearCategoriasActivity extends AppCompatActivity {
    private String nombreCategoria;
    private boolean esGasto = true; // Valor por defecto
    private int idIconoSeleccionado = -1; // Valor por defecto



    String[] urls = {
            "https://cdn.iconscout.com/icon/free/png-256/free-email-2029111-1713291.png?f=webp",
            "https://www.iconpacks.net/icons/2/free-mail-and-phone-icon-2562-thumb.png",
            "https://static-00.iconduck.com/assets.00/euro-symbol-icon-512x512-y0j8x7ac.png",
            "https://cdn-icons-png.flaticon.com/512/468/468182.png",
            "https://cdn-icons-png.flaticon.com/512/535/535239.png",
            "https://www.iconpacks.net/icons/2/free-folder-icon-1449-thumb.png",
            "https://icon-library.com/images/what-is-nfc-icon/what-is-nfc-icon-25.jpg",
            "https://i.pinimg.com/originals/b3/f1/da/b3f1da70927cc796e51f5a9066dde860.png",
            "https://static.thenounproject.com/png/1842086-200.png",
            "https://icons.iconarchive.com/icons/iconsmind/outline/512/T-Shirt-icon.png",
            "https://cdn-icons-png.flaticon.com/512/141/141584.png",
            "https://png.pngtree.com/png-clipart/20230317/original/pngtree-dress-vector-icon-design-illustration-png-image_8991554.png",
            "https://cdn-icons-png.flaticon.com/512/808/808726.png",
            "https://icon-library.com/images/playing-card-icon/playing-card-icon-12.jpg",
            "https://cdn-icons-png.freepik.com/512/1179/1179241.png",
            "https://cdn-icons-png.flaticon.com/512/5019/5019874.png",
            "https://pixsector.com/cache/8955ccde/avea0c6d1234636825bd6.png",
            "https://cdn-icons-png.flaticon.com/512/282/282151.png",
            "https://cdn1.iconfinder.com/data/icons/fitness-icon-collection/100/headphones-512.png",
            "https://cdn-icons-png.flaticon.com/512/2158/2158445.png",
            "https://saludactivaxela.com/wp-content/uploads/2017/10/sports-swimming-icon-png-1.png",
            "https://cdn-icons-png.flaticon.com/512/3104/3104509.png",
            //"https://www.freeiconspng.com/thumbs/sports-icon-png/sport-activities-football-icon-6.png",
            "https://cdn-icons-png.flaticon.com/512/184/184940.png",
            "https://cdn-icons-png.flaticon.com/512/1024/1024529.png",
            "https://cdn-icons-png.flaticon.com/512/130/130298.png",
            "https://cdn-icons-png.flaticon.com/512/775/775358.png",
            "https://cdn-icons-png.flaticon.com/512/2237/2237677.png",
            "https://cdn-icons-png.flaticon.com/512/1235/1235387.png",
            "https://static.thenounproject.com/png/3053080-200.png",
            "https://icons.veryicon.com/png/o/object/test-7/hobbies.png",
            "https://www.iconpacks.net/icons/2/free-opened-book-icon-3163-thumb.png",
            //"https://www.freeiconspng.com/thumbs/book-icon/book-stack-icon--icon-search-engine-16.png",
            "https://static.thenounproject.com/png/4276187-200.png",
            "https://cdn-icons-png.flaticon.com/512/735/735643.png",
            "https://i.pinimg.com/originals/5e/fa/77/5efa77186bd7ca39e06aae2bad562351.png",
            "https://icon-library.com/images/food-icon-png/food-icon-png-1.jpg",
            "https://icons.veryicon.com/png/o/miscellaneous/simple-linetype-icon/food-17.png",
            "https://cdn-icons-png.flaticon.com/512/4827/4827378.png",
            "https://cdn-icons-png.flaticon.com/512/675/675636.png",
            "https://iconape.com/wp-content/png_logo_vector/money-39.png",
            "https://static.thenounproject.com/png/1260093-200.png",
            "https://static.thenounproject.com/png/108601-200.png",
            "https://cdn-icons-png.freepik.com/512/7510/7510522.png",
            "https://cdn-icons-png.flaticon.com/512/3209/3209277.png"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_categorias);
        ponerIconos();

        // Obtener referencias a las vistas
        final RadioButton radioButtonGastos = findViewById(R.id.radioButtonGastos);
        final RadioButton radioButtonIngresos = findViewById(R.id.radioButtonIngresos);
        final RadioGroup radioGroupTipo = findViewById(R.id.radioGroupTipo);
        final ImageButton[] buttons = {
                findViewById(R.id.buttonCategoria1),
                findViewById(R.id.buttonCategoria2),
                findViewById(R.id.buttonCategoria3),
                findViewById(R.id.buttonCategoria4),
                findViewById(R.id.buttonCategoria5),
                findViewById(R.id.buttonCategoria6),
                findViewById(R.id.buttonCategoria7),
                findViewById(R.id.buttonCategoria8),
                findViewById(R.id.buttonCategoria9),
                findViewById(R.id.buttonCategoria10),
                findViewById(R.id.buttonCategoria11),
                findViewById(R.id.buttonCategoria12),
                findViewById(R.id.buttonCategoria13),
                findViewById(R.id.buttonCategoria14),
                findViewById(R.id.buttonCategoria15),
                findViewById(R.id.buttonCategoria16),
                findViewById(R.id.buttonCategoria17),
                findViewById(R.id.buttonCategoria18),
                findViewById(R.id.buttonCategoria19),
                findViewById(R.id.buttonCategoria20),
                findViewById(R.id.buttonCategoria21),
                findViewById(R.id.buttonCategoria22),
                findViewById(R.id.buttonCategoria23),
                findViewById(R.id.buttonCategoria24),
                findViewById(R.id.buttonCategoria25),
                findViewById(R.id.buttonCategoria26),
                findViewById(R.id.buttonCategoria27),
                findViewById(R.id.buttonCategoria28),
                findViewById(R.id.buttonCategoria29),
                findViewById(R.id.buttonCategoria30),
                findViewById(R.id.buttonCategoria31),
                findViewById(R.id.buttonCategoria32),
                findViewById(R.id.buttonCategoria33),
                findViewById(R.id.buttonCategoria34),
                findViewById(R.id.buttonCategoria35),
                findViewById(R.id.buttonCategoria36),
                findViewById(R.id.buttonCategoria37),
                findViewById(R.id.buttonCategoria38),
                findViewById(R.id.buttonCategoria39),
                findViewById(R.id.buttonCategoria40),
                findViewById(R.id.buttonCategoria41),
                findViewById(R.id.buttonCategoria42),
        };
        Button buttonCrear = findViewById(R.id.buttonCrear);
        // Configurar listener para los botones de categoría
        for (int i = 0; i < buttons.length; i++) {
            final int finalI = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    idIconoSeleccionado = finalI;
                    Toast.makeText(CrearCategoriasActivity.this, "Icono seleccionado", Toast.LENGTH_SHORT).show();
                }
            });
        }
        radioGroupTipo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == radioButtonGastos.getId()) {
                    esGasto = true;
                } else if (checkedId == radioButtonIngresos.getId()) {
                    esGasto = false;
                }
            }
        });
        //boton volver
        ImageButton buttonVolver = findViewById(R.id.buttonVolver);
        buttonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CrearCategoriasActivity.this, CategoriasPagosActivity.class);
                startActivity(intent);
            }
        });


        // boton crear
        buttonCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el nombre de la categoría ingresado por el usuario
                nombreCategoria = ((EditText) findViewById(R.id.Nombre)).getText().toString();

                // Verificar que se haya seleccionado un ícono
                if (idIconoSeleccionado == -1) {
                    Toast.makeText(CrearCategoriasActivity.this, "Debes seleccionar un ícono", Toast.LENGTH_SHORT).show();
                    return;
                }

                // guardar categoria en base de datos
                agregarNuevaCategoria(nombreCategoria, idIconoSeleccionado, esGasto);
            }
        });

    }

    // Método para agregar una nueva categoría a la base de datos
    private void agregarNuevaCategoria(String nombre, int idIcono, boolean esGasto) {
        // Obtener el UID del usuario actual
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserUID = mAuth.getCurrentUser().getUid();

        // Obtenemos la referencia a la base de datos
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Obtener una referencia al nodo de categorías del usuario actual
        DatabaseReference categoriasRef = databaseRef.child("usuarios").child(currentUserUID).child("categorias");

        // Escuchar una vez para obtener el número total de categorías
        categoriasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Obtener el número de categorías actuales
                long numCategorias = dataSnapshot.getChildrenCount();

                // Creamos un nuevo nodo para la categoría bajo el UID del usuario
                String nuevaClave = String.valueOf(numCategorias + 1); // Siguiente número de categoría
                DatabaseReference categoriaRef = categoriasRef.child(nuevaClave);

                // Creamos un objeto Categoria con los datos de la nueva categoría
                Categoria categoria = new Categoria(nombre, obtenerUrlIcono(idIcono), esGasto ? "pago" : "ingresos");

                // Guardamos los datos de la nueva categoría en la base de datos
                categoriaRef.setValue(categoria);

                // Mostramos un mensaje de éxito
                Toast.makeText(CrearCategoriasActivity.this, "Categoría creada exitosamente", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores si los hay
                Toast.makeText(CrearCategoriasActivity.this, "Error al obtener el número de categorías: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Método para obtener la URL del ícono según su ID
    private String obtenerUrlIcono(int idIcono) {
        return urls[idIcono]; // Retorna la URL del ícono
    }

    private void ponerIconos() {

        // Asignar cada URL a su respectivo botón
        ImageButton[] buttons = {
                findViewById(R.id.buttonCategoria1),
                findViewById(R.id.buttonCategoria2),
                findViewById(R.id.buttonCategoria3),
                findViewById(R.id.buttonCategoria4),
                findViewById(R.id.buttonCategoria5),
                findViewById(R.id.buttonCategoria6),
                findViewById(R.id.buttonCategoria7),
                findViewById(R.id.buttonCategoria8),
                findViewById(R.id.buttonCategoria9),
                findViewById(R.id.buttonCategoria10),
                findViewById(R.id.buttonCategoria11),
                findViewById(R.id.buttonCategoria12),
                findViewById(R.id.buttonCategoria13),
                findViewById(R.id.buttonCategoria14),
                findViewById(R.id.buttonCategoria15),
                findViewById(R.id.buttonCategoria16),
                findViewById(R.id.buttonCategoria17),
                findViewById(R.id.buttonCategoria18),
                findViewById(R.id.buttonCategoria19),
                findViewById(R.id.buttonCategoria20),
                findViewById(R.id.buttonCategoria21),
                findViewById(R.id.buttonCategoria22),
                findViewById(R.id.buttonCategoria23),
                findViewById(R.id.buttonCategoria24),
                findViewById(R.id.buttonCategoria25),
                findViewById(R.id.buttonCategoria26),
                findViewById(R.id.buttonCategoria27),
                findViewById(R.id.buttonCategoria28),
                findViewById(R.id.buttonCategoria29),
                findViewById(R.id.buttonCategoria30),
                findViewById(R.id.buttonCategoria31),
                findViewById(R.id.buttonCategoria32),
                findViewById(R.id.buttonCategoria33),
                findViewById(R.id.buttonCategoria34),
                findViewById(R.id.buttonCategoria35),
                findViewById(R.id.buttonCategoria36),
                findViewById(R.id.buttonCategoria37),
                findViewById(R.id.buttonCategoria38),
                findViewById(R.id.buttonCategoria39),
                findViewById(R.id.buttonCategoria40),
                findViewById(R.id.buttonCategoria41),
                findViewById(R.id.buttonCategoria42),
        };

        for (int i = 0; i < urls.length && i < buttons.length; i++) {
            Glide.with(this).load(urls[i]).into(buttons[i]);
        }
    }
}

