package com.example.finanzify.Estadísticas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.example.finanzify.Ajustes.AjustesActivity;
import com.example.finanzify.PantallaInicioActivity;
import com.google.firebase.database.Query;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.example.finanzify.Clases.CategoriaColor;
import com.example.finanzify.Clases.Transaccion;
import com.example.finanzify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.property.TextAlignment;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Cell;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import android.view.View;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;


import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class EstadisticasActivity extends AppCompatActivity {

    // Create the object of TextView and PieChart class
    PieChart pieChart;
    RecyclerView recyclerView, recyclerViewEst;
    PieChartAdapter adapter;
    // Variable para almacenar la fecha de inicio seleccionada
    private String fechaInicioSeleccionada = "";
    // Variable para almacenar la fecha de fin seleccionada
    private String fechaFinSeleccionada = "";
    EstadisticasAdapter adaptador;
    TextView textView, textviewT;
    double mesactual =0;
    double mesanterior =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        // Obtener la referencia del RecyclerView en tu actividad
        recyclerView = findViewById(R.id.recyclerViewCategorias);
        recyclerViewEst = findViewById(R.id.recyclerViewEst);
        textView = findViewById(R.id.textView);
        textviewT = findViewById(R.id.textViewCat);
        pieChart = findViewById(R.id.piechart);
        actualizarTextView();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEst.setLayoutManager(new LinearLayoutManager(this));

        Spinner spinnerLapsos = findViewById(R.id.spinnerLapsos);
        ArrayAdapter <String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.lapsos_tiempo_estadisticas));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLapsos.setAdapter(spinnerAdapter);

        spinnerLapsos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String lapsedTiempo = parentView.getItemAtPosition(position).toString();

                if (lapsedTiempo.equals("Todos los tiempos")) {
                    mostrarEstadisticasTodosLosTiempos();

                } else if (lapsedTiempo.equals("Semanal")) {
                    mostrarEstadisticasSemanal();

                } else if (lapsedTiempo.equals("Mensual")) {
                    mostrarEstadisticasMensual();
                } else if (lapsedTiempo.equals("Trimestral")) {
                    mostrarEstadisticasTrimestral();

                } else if (lapsedTiempo.equals("Personalizado")) {
                    // Si se selecciona "Personalizada", mostrar el diálogo para seleccionar las fechas
                    showStartDatePicker();
                } else {
                    mostrarEstadisticasAnual();
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
                Intent intent = new Intent(EstadisticasActivity.this, PantallaInicioActivity.class);
                startActivity(intent);
            }
        });
}


    // Método para mostrar el diálogo de selección de fecha para la fecha de inicio
    private void showStartDatePicker() {
        // Obtener la fecha actual
        final Calendar calendario = Calendar.getInstance();
        int año = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        // Crear un diálogo de selección de fecha para la fecha de inicio
        DatePickerDialog datePickerDialogInicio = new DatePickerDialog(EstadisticasActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int año, int mes, int dia) {
                        // Acciones cuando se selecciona la fecha de inicio
                        // Construir la fecha de inicio seleccionada en formato de cadena
                        fechaInicioSeleccionada = String.format("%02d/%02d/%d", dia, mes + 1, año);

                        // Mostrar la fecha de inicio seleccionada en un Toast
                        Toast.makeText(getApplicationContext(), "Fecha de inicio seleccionada: " + fechaInicioSeleccionada, Toast.LENGTH_SHORT).show();

                        // Mostrar un segundo diálogo para seleccionar la fecha de fin
                        showEndDatePicker();
                    }
                }, año, mes, dia);
        // Mostrar el diálogo
        datePickerDialogInicio.show();
    }

    // Método para mostrar el diálogo de selección de fecha para la fecha de fin
    private void showEndDatePicker() {
        // Obtener la fecha actual
        final Calendar calendario = Calendar.getInstance();
        int año = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        // Crear un diálogo de selección de fecha para la fecha de fin
        DatePickerDialog datePickerDialogFin = new DatePickerDialog(EstadisticasActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int año, int mes, int dia) {
                        // Acciones cuando se selecciona la fecha de fin
                        // Construir la fecha de fin seleccionada en formato de cadena
                        fechaFinSeleccionada = String.format("%02d/%02d/%d", dia, mes + 1, año);

                        // Mostrar la fecha de fin seleccionada en un Toast
                        Toast.makeText(getApplicationContext(), "Fecha de fin seleccionada: " + fechaFinSeleccionada, Toast.LENGTH_SHORT).show();

                        // Llamar a la función mostrarEstadisticasPersonalizado con las fechas seleccionadas
                        mostrarEstadisticasPersonalizado(fechaInicioSeleccionada, fechaFinSeleccionada);
                    }
                }, año, mes, dia);
        // Mostrar el diálogo
        datePickerDialogFin.show();
    }

    // Métodos para mostrar estadísticas según el lapso de tiempo seleccionado
    private void mostrarEstadisticasTodosLosTiempos() {
        // Obtener y mostrar las estadísticas de todos los tiempos
        GetGastos(new GastosCallback() {
            @Override
            public void onGastosObtenidos(List<CategoriaColor> listaCategoriasColores) {
                setData(listaCategoriasColores);
                adapter = new PieChartAdapter(EstadisticasActivity.this, listaCategoriasColores);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                adaptador = new EstadisticasAdapter(EstadisticasActivity.this, listaCategoriasColores);
                recyclerViewEst.setAdapter(adaptador);
                adaptador.notifyDataSetChanged();
            }
        });
    }

    private void mostrarEstadisticasSemanal() {
        // Obtener la fecha actual
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Establecer al primer día de la semana
        Date fechaInicioSemana = calendar.getTime();
        calendar.add(Calendar.WEEK_OF_YEAR, 1); // Avanzar una semana
        Date fechaFinSemana = calendar.getTime();


        // Convertir las fechas a formato de cadena
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String strFechaInicio = sdf.format(fechaInicioSemana);
        String strFechaFin = sdf.format(fechaFinSemana);


        // Log para imprimir la fecha de inicio de la semana
        Log.d("FechaInicioSemana", "Fecha de inicio de la semana: " + strFechaInicio);

        // Log para imprimir la fecha de fin de la semana
        Log.d("FechaFinSemana", "Fecha de fin de la semana: " + strFechaFin);

        // Llamar a la función GetGastosEnIntervalo con las fechas de esta semana
        GetGastosEnIntervalo(strFechaInicio, strFechaFin, new GastosCallback() {
            @Override
            public void onGastosObtenidos(List<CategoriaColor> listaCategoriasColores) {
                setData(listaCategoriasColores);
                adapter = new PieChartAdapter(EstadisticasActivity.this, listaCategoriasColores);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                adaptador = new EstadisticasAdapter(EstadisticasActivity.this, listaCategoriasColores);
                recyclerViewEst.setAdapter(adaptador);
                adaptador.notifyDataSetChanged();
            }
        });
    }


    private void mostrarEstadisticasMensual() {
        // Obtener la fecha actual
        Calendar calendar = Calendar.getInstance();
        // Obtener el primer día del mes actual
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date fechaInicioMes = calendar.getTime();

        // Obtener el último día del mes actual
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date fechaFinMes = calendar.getTime();

        // Convertir las fechas a formato de cadena
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String strFechaInicio = sdf.format(fechaInicioMes);
        String strFechaFin = sdf.format(fechaFinMes);

        // Log para imprimir la fecha de inicio del mes
        Log.d("FechaInicioMes", "Fecha de inicio del mes: " + strFechaInicio);

        // Log para imprimir la fecha de fin del mes
        Log.d("FechaFinMes", "Fecha de fin del mes: " + strFechaFin);

        // Llamar a la función GetGastosEnIntervalo con las fechas de este mes
        GetGastosEnIntervalo(strFechaInicio, strFechaFin, new GastosCallback() {
            @Override
            public void onGastosObtenidos(List<CategoriaColor> listaCategoriasColores) {
                setData(listaCategoriasColores);
                adapter = new PieChartAdapter(EstadisticasActivity.this, listaCategoriasColores);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                adaptador = new EstadisticasAdapter(EstadisticasActivity.this, listaCategoriasColores);
                recyclerViewEst.setAdapter(adaptador);
                adaptador.notifyDataSetChanged();
            }
        });
    }

    private void mostrarEstadisticasTrimestral() {
        // Obtener la fecha actual
        Calendar calendar = Calendar.getInstance();
        int mes = calendar.get(Calendar.MONTH);

        // Definir los límites de los trimestres
        int trimestreInicio1 = Calendar.JANUARY;
        int trimestreFin1 = Calendar.MARCH;
        int trimestreInicio2 = Calendar.APRIL;
        int trimestreFin2 = Calendar.JUNE;
        int trimestreInicio3 = Calendar.JULY;
        int trimestreFin3 = Calendar.SEPTEMBER;
        int trimestreInicio4 = Calendar.OCTOBER;
        int trimestreFin4 = Calendar.DECEMBER;

        // Definir las fechas de inicio y fin del trimestre actual
        Date fechaInicioTrimestre;
        Date fechaFinTrimestre;

        if (mes >= trimestreInicio1 && mes <= trimestreFin1) {
            calendar.set(Calendar.MONTH, trimestreInicio1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            fechaInicioTrimestre = calendar.getTime();
            calendar.set(Calendar.MONTH, trimestreFin1);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            fechaFinTrimestre = calendar.getTime();
        } else if (mes >= trimestreInicio2 && mes <= trimestreFin2) {
            calendar.set(Calendar.MONTH, trimestreInicio2);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            fechaInicioTrimestre = calendar.getTime();
            calendar.set(Calendar.MONTH, trimestreFin2);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            fechaFinTrimestre = calendar.getTime();
        } else if (mes >= trimestreInicio3 && mes <= trimestreFin3) {
            calendar.set(Calendar.MONTH, trimestreInicio3);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            fechaInicioTrimestre = calendar.getTime();
            calendar.set(Calendar.MONTH, trimestreFin3);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            fechaFinTrimestre = calendar.getTime();
        } else {
            calendar.set(Calendar.MONTH, trimestreInicio4);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            fechaInicioTrimestre = calendar.getTime();
            calendar.set(Calendar.MONTH, trimestreFin4);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            fechaFinTrimestre = calendar.getTime();
        }

        // Convertir las fechas a formato de cadena
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String strFechaInicio = sdf.format(fechaInicioTrimestre);
        String strFechaFin = sdf.format(fechaFinTrimestre);

        // Log para imprimir la fecha de inicio del trimestre
        Log.d("FechaInicioTrimestre", "Fecha de inicio del trimestre: " + strFechaInicio);

        // Log para imprimir la fecha de fin del trimestre
        Log.d("FechaFinTrimestre", "Fecha de fin del trimestre: " + strFechaFin);

        // Llamar a la función GetGastosEnIntervalo con las fechas del trimestre actual
        GetGastosEnIntervalo(strFechaInicio, strFechaFin, new GastosCallback() {
            @Override
            public void onGastosObtenidos(List<CategoriaColor> listaCategoriasColores) {
                setData(listaCategoriasColores);
                adapter = new PieChartAdapter(EstadisticasActivity.this, listaCategoriasColores);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                adaptador = new EstadisticasAdapter(EstadisticasActivity.this, listaCategoriasColores);
                recyclerViewEst.setAdapter(adaptador);
                adaptador.notifyDataSetChanged();
            }
        });
    }


    private void mostrarEstadisticasAnual() {
        // Obtener la fecha actual
        Calendar calendar = Calendar.getInstance();
        // Obtener el primer día del año actual
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        Date fechaInicioAnio = calendar.getTime();

        // Obtener el último día del año actual
        calendar.add(Calendar.YEAR, 1);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date fechaFinAnio = calendar.getTime();

        // Convertir las fechas a formato de cadena
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String strFechaInicio = sdf.format(fechaInicioAnio);
        String strFechaFin = sdf.format(fechaFinAnio);

        // Log para imprimir la fecha de inicio del año
        Log.d("FechaInicioAnio", "Fecha de inicio del año: " + strFechaInicio);

        // Log para imprimir la fecha de fin del año
        Log.d("FechaFinAnio", "Fecha de fin del año: " + strFechaFin);

        // Llamar a la función GetGastosEnIntervalo con las fechas del año actual
        GetGastosEnIntervalo(strFechaInicio, strFechaFin, new GastosCallback() {
            @Override
            public void onGastosObtenidos(List<CategoriaColor> listaCategoriasColores) {
                setData(listaCategoriasColores);
                adapter = new PieChartAdapter(EstadisticasActivity.this, listaCategoriasColores);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                adaptador = new EstadisticasAdapter(EstadisticasActivity.this, listaCategoriasColores);
                recyclerViewEst.setAdapter(adaptador);
                adaptador.notifyDataSetChanged();
            }
        });
    }

    private void mostrarEstadisticasPersonalizado(String fecha1, String fecha2) {

        GetGastosEnIntervalo(fecha1, fecha2, new GastosCallback() {
            @Override
            public void onGastosObtenidos(List<CategoriaColor> listaCategoriasColores) {
                setData(listaCategoriasColores);
                adapter = new PieChartAdapter(EstadisticasActivity.this, listaCategoriasColores);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                adaptador = new EstadisticasAdapter(EstadisticasActivity.this, listaCategoriasColores);
                recyclerViewEst.setAdapter(adaptador);
                adaptador.notifyDataSetChanged();
            }
        });
    }


    private void setData(List<CategoriaColor> listaCategoriasColores) {
        // Obtener las cantidades totales de todas las categorías
        double totalCantidades = 0.0;
        for (CategoriaColor categoriaColor : listaCategoriasColores) {
            totalCantidades += categoriaColor.getCantidad();
        }

        // Limpiar el gráfico antes de agregar nuevos datos
        pieChart.clearChart();
        // Iterar a través de la lista de categorías y colores
        for (CategoriaColor categoriaColor : listaCategoriasColores) {
            String categoria = categoriaColor.getCategoria();
            double cantidad = categoriaColor.getCantidad();
            // Calcular el porcentaje de la cantidad
            double porcentaje = (cantidad / totalCantidades) * 100;
            // Añadir la porción al gráfico
            pieChart.addPieSlice(
                    new PieModel(
                            categoria,
                            (float) porcentaje,
                            categoriaColor.getColor()));
        }

        // Iniciar la animación del gráfico
        pieChart.startAnimation();
    }


    // Método para obtener un color aleatorio asegurandonos de que no se repite 
    private int getRandomColor(List<CategoriaColor> listaCategorias) {
        Random rnd = new Random();
        int color;
        boolean colorRepetido;
        do {
            colorRepetido = false;
            color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            // Verificar si el color generado es igual al de algún elemento existente
            for (CategoriaColor cat : listaCategorias) {
                if (cat.getColor() == color) {
                    colorRepetido = true;
                    break;
                }
            }
        } while (colorRepetido);
        return color;
    }

    private void GetGastosEnIntervalo(String fechaInicio, String fechaFin, final GastosCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            // Referencia a las transacciones del usuario en la base de datos
            DatabaseReference transaccionesRef = FirebaseDatabase.getInstance().getReference()
                    .child("usuarios").child(uid).child("transacciones");

            // Crear una lista para almacenar las categorías y cantidades
            List<CategoriaColor> listaCategorias = new ArrayList<>();

            // Leer las transacciones del usuario
            transaccionesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Iterar a través de las transacciones
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Transaccion transaccion = snapshot.getValue(Transaccion.class);
                        if (transaccion != null && transaccion.getTipo().equals("pago") && transaccion.isCobrado()) {
                            String fechaTransaccion = transaccion.getFecha(); // Obtener la fecha de la transacción

                            // Verificar si la fecha de la transacción está dentro del intervalo
                            if (enIntervalo(fechaTransaccion, fechaInicio, fechaFin)) {
                                // La transacción está dentro del intervalo, agregar categoría y cantidad
                                String categoria = transaccion.getCategoria().getNombre();
                                double cantidad = transaccion.getCantidad();
                                boolean categoriaExistente = false;
                                for (CategoriaColor cat : listaCategorias) {
                                    if (cat.getCategoria().equalsIgnoreCase(categoria)) {
                                        // La categoría ya existe, actualizar la cantidad
                                        cat.setCantidad(cat.getCantidad() + cantidad);
                                        categoriaExistente = true;
                                    }
                                }
                                if (!categoriaExistente) {
                                    // Obtener un color aleatorio
                                    int color = getRandomColor(listaCategorias);
                                    // Agregar la categoría y el color a la lista
                                    listaCategorias.add(new CategoriaColor(categoria, color, cantidad));
                                }
                            }
                        }
                    }
                    // Ordenar la lista de categorías por cantidad de mayor a menor
                    Collections.sort(listaCategorias, new Comparator<CategoriaColor>() {
                        @Override
                        public int compare(CategoriaColor o1, CategoriaColor o2) {
                            return Double.compare(o2.getCantidad(), o1.getCantidad());
                        }
                    });

                    // Llamar al callback con la lista de categorías y cantidades
                    callback.onGastosObtenidos(listaCategorias);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar errores de lectura de la base de datos
                    Toast.makeText(EstadisticasActivity.this, "Error al obtener transacciones: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void GetGastos(final GastosCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            // Referencia a las transacciones del usuario en la base de datos
            DatabaseReference transaccionesRef = FirebaseDatabase.getInstance().getReference()
                    .child("usuarios").child(uid).child("transacciones");

            // Crear una lista para almacenar las categorías y colores
            List<CategoriaColor> listaCategorias = new ArrayList<>();

            // Leer las transacciones del usuario
            transaccionesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Iterar a través de las transacciones
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Transaccion transaccion = snapshot.getValue(Transaccion.class);
                        if (transaccion != null && transaccion.getTipo().equals("pago") && transaccion.isCobrado()) {
                            // Obtener la categoría y la cantidad de la transacción
                            String categoria = transaccion.getCategoria().getNombre();
                            double cantidad = transaccion.getCantidad();
                            boolean categoriaExistente = false;
                            for (CategoriaColor cat : listaCategorias) {
                                if (cat.getCategoria().equalsIgnoreCase(categoria)) {
                                    // La categoría ya existe, actualizar la cantidad
                                    cat.setCantidad(cat.getCantidad() + cantidad);
                                    categoriaExistente = true;
                                }
                            } if (!categoriaExistente) {
                                // Obtener un color aleatorio
                                int color = getRandomColor(listaCategorias);
                                // Agregar la categoría y el color a la lista
                                listaCategorias.add(new CategoriaColor(categoria, color, cantidad));
                            }
                        }
                    }
                    // Ordenar la lista de categorías por cantidad de mayor a menor
                    Collections.sort(listaCategorias, new Comparator<CategoriaColor>() {
                        @Override
                        public int compare(CategoriaColor o1, CategoriaColor o2) {
                            return Double.compare(o2.getCantidad(), o1.getCantidad());
                        }
                    });

                    // Llamar al callback con la lista de categorías y colores
                    callback.onGastosObtenidos(listaCategorias);
                    // Notificar al adaptador que los datos han cambiado
                    // Configurar el adaptador una vez se obtengan los datos
                    /*adapter = new PieChartAdapter(EstadisticasActivity.this, listaCategorias);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();*/
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar errores de lectura de la base de datos
                    Toast.makeText(EstadisticasActivity.this, "Error al obtener transacciones: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Define una interfaz para el callback
    interface GastosCallback {
        void onGastosObtenidos(List<CategoriaColor> listaCategoriasColores);
    }

    public static boolean enIntervalo(String fechaA, String fechaInicio, String fechaFin) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date dateA = formato.parse(fechaA);
            Date dateInicio = formato.parse(fechaInicio);
            Date dateFin = formato.parse(fechaFin);

            return dateInicio.compareTo(dateA) <= 0 && dateA.compareTo(dateFin) <= 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // En caso de error, se retorna false
        }
    }

    private void actualizarTextView() {
        // Obtener el calendario y establecerlo en el mes actual
        // Obtener la fecha actual
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // El mes se cuenta desde 0, por lo que necesitas agregar 1

        // Obtener la fecha del primer día del mes actual
        Calendar firstDayOfThisMonthCalendar = Calendar.getInstance();
        firstDayOfThisMonthCalendar.set(Calendar.YEAR, year);
        firstDayOfThisMonthCalendar.set(Calendar.MONTH, month - 1); // Restar 1 para obtener el mes anterior
        firstDayOfThisMonthCalendar.set(Calendar.DAY_OF_MONTH, 1);

        // Obtener la fecha del primer día del mes siguiente
        Calendar firstDayOfNextMonthCalendar = Calendar.getInstance();
        firstDayOfNextMonthCalendar.set(Calendar.YEAR, year);
        firstDayOfNextMonthCalendar.set(Calendar.MONTH, month); // No resta 1 para obtener el mes siguiente
        firstDayOfNextMonthCalendar.set(Calendar.DAY_OF_MONTH, 1);

        Date firstDayOfThisMonth = firstDayOfThisMonthCalendar.getTime();
        Date firstDayOfNextMonth = firstDayOfNextMonthCalendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String primerDiaMesActualS = dateFormat.format(firstDayOfThisMonth);
        String ultimoDiaMesActualS = dateFormat.format(firstDayOfNextMonth);

        obtenerTransaccionesUsuarioPago(primerDiaMesActualS, ultimoDiaMesActualS, new EstadisticasActivity.TotalCantidadesPagoCallback() {
            @Override
            public void onTotalCantidadesObtenido(double totalCantidades) {
                mesactual =  totalCantidades;
                Log.d("MesActual", "Cantidad del mes actual: " + mesactual);
            }
        });

        // Llamar a la función para obtener la transacción con el gasto más grande del mes actual
        obtenerTransaccionMasGrande(primerDiaMesActualS, ultimoDiaMesActualS, new TransaccionMasGrandeCallback() {
            @Override
            public void onTransaccionMasGrandeObtenida(Transaccion transaccionMasGrande) {
                if (transaccionMasGrande != null) {
                    // Aquí puedes hacer lo que necesites con la transacción más grande del mes actual
                    // Por ejemplo, imprimir el nombre de la categoría, el día y la cantidad
                    Log.d("TransaccionMasGrande", "Transacción más grande del mes actual:");
                    Log.d("TransaccionMasGrande", "Categoría: " + transaccionMasGrande.getCategoria().getNombre());
                    Log.d("TransaccionMasGrande", "Día: " + transaccionMasGrande.getFecha());
                    Log.d("TransaccionMasGrande", "Cantidad: " + transaccionMasGrande.getCantidad());

                    String fecha = "<b>" + transaccionMasGrande.getFecha() + "</b>";
                    String categoria = "<b>" + transaccionMasGrande.getCategoria().getNombre() + "</b>";
                    String cantidad = "<b>" + String.format("%.2f", transaccionMasGrande.getCantidad()) + "</b>";

                    String mensaje = "El gasto más grande realizada este mes fue el " + fecha + " de la categoría " +
                            categoria + " con la cantidad " + cantidad + ".";
                    textviewT.setText(Html.fromHtml(mensaje));

                } else {
                    // Manejar el caso en que no se encuentre ninguna transacción en el mes actual
                    Log.d("TransaccionMasGrande", "No se encontró ninguna transacción en el mes actual.");
                }
            }
        });

        // Obtener el calendario y establecerlo en el mes anterior
       calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1); // Retroceder un mes para obtener el mes anterior
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Establecer en el primer día del mes anterior
        Date primerDiaMesAnterior = calendar.getTime();

        calendar.add(Calendar.MONTH, 1); // Avanzar un mes para obtener el mes actual
        calendar.add(Calendar.DAY_OF_MONTH, -1); // Retroceder un día para obtener el último día del mes anterior
        Date ultimoDiaMesAnterior = calendar.getTime();

        // Formatear las fechas como cadenas
        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String primerDiaMesAnteriorS = dateFormat.format(primerDiaMesAnterior);
        String ultimoDiaMesAnteriorS = dateFormat.format(ultimoDiaMesAnterior);


        obtenerTransaccionesUsuarioPago(primerDiaMesAnteriorS, ultimoDiaMesAnteriorS, new EstadisticasActivity.TotalCantidadesPagoCallback() {
            @Override
            public void onTotalCantidadesObtenido(double totalCantidades) {
                mesanterior =  totalCantidades;
                String mensaje = "";
                if (mesanterior > mesactual) {
                    mensaje = "Este mes has gastado <b>" + String.format("%.2f", mesactual) + "</b>, mientras que el mes pasado gastaste <b>" + String.format("%.2f", mesanterior) + "</b> por lo que has gastado " +
                            "<font color='#6B8E23'>" + String.format("%.2f", ((mesanterior - mesactual) / mesanterior) * 100) + "% menos</font> que el mes pasado.";
                } else {
                    mensaje = "Este mes has gastado <b>" + String.format("%.2f", mesactual) + "</b>, mientras que el mes pasado gastaste <b>" + String.format("%.2f", mesanterior) + "</b> por lo que has gastado " +
                            "<font color='#FF0000'>" + String.format("%.2f", ((mesactual - mesanterior) / mesanterior) * 100) + "% más</font> que el mes pasado.";
                }
                textView.setText(Html.fromHtml(mensaje));

                Log.d("MesAnterior", "Cantidad del mes anterior: " + mesanterior);
            }
        });
    }

    // Interfaz de callback para devolver la transacción con el gasto más grande
    interface TransaccionMasGrandeCallback {
        void onTransaccionMasGrandeObtenida(Transaccion transaccionMasGrande);
    }

    private void obtenerTransaccionMasGrande(String fechaI, String fechaF, TransaccionMasGrandeCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            DatabaseReference transaccionesRef = FirebaseDatabase.getInstance().getReference()
                    .child("usuarios").child(uid).child("transacciones");

            Query query = transaccionesRef.orderByChild("cantidad");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Transaccion transaccionMasGrande = null;
                    double cantidadMasGrande = Double.MIN_VALUE;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Transaccion transaccion = snapshot.getValue(Transaccion.class);
                        if (transaccion != null && transaccion.getTipo().equals("pago") && enIntervalo(transaccion.getFecha(), fechaI, fechaF) && transaccion.isCobrado()) {
                            if (transaccion.getCantidad() > cantidadMasGrande) {
                                cantidadMasGrande = transaccion.getCantidad();
                                transaccionMasGrande = transaccion;
                            }
                        }
                    }

                    callback.onTransaccionMasGrandeObtenida(transaccionMasGrande);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar errores de base de datos
                }
            });
        }
    }



    //funciones
    interface TotalCantidadesPagoCallback {
        void onTotalCantidadesObtenido(double totalCantidades);
    }


    private void obtenerTransaccionesUsuarioPago(String fechaI, String fechaF,EstadisticasActivity.TotalCantidadesPagoCallback callback) {
        // Obtener el ID del usuario actual
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            // Referencia a las transacciones del usuario en la base de datos
            DatabaseReference transaccionesRef = FirebaseDatabase.getInstance().getReference()
                    .child("usuarios").child(uid).child("transacciones");

            Query query = transaccionesRef.orderByChild("fecha");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Inicializar la suma de cantidades
                    double totalCantidades = 0.0;

                    // Iterar a través de las transacciones y sumar las cantidades
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Transaccion transaccion = snapshot.getValue(Transaccion.class);
                        if (transaccion != null && transaccion.getTipo().equals("pago") && transaccion.isCobrado() && enIntervalo(transaccion.getFecha(),
                                fechaI, fechaF)) {
                            // Sumar la cantidad de la transacción al total
                            Log.d("TAG", "Fecha de inicio: " + fechaI);
                            Log.d("TAG", "Fecha de fin: " + fechaF);
                            Log.d("TAG", "categoria " + transaccion.getCategoria().getNombre());
                            totalCantidades += transaccion.getCantidad();
                            Log.d("TAG", "cantidad " + totalCantidades);
                        }
                    }

                    // Llamar al callback con el total de cantidades
                    callback.onTotalCantidadesObtenido(totalCantidades);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar errores de base de datos
                }
            });
        }
    }



}


