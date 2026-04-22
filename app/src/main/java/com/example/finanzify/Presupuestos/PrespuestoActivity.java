package com.example.finanzify.Presupuestos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finanzify.Ajustes.AjustesActivity;
import com.example.finanzify.Clases.Limite;
import com.example.finanzify.Clases.PresupuestoA;
import com.example.finanzify.Clases.Transaccion;
import com.example.finanzify.Historial.HistorialActivity;
import com.example.finanzify.Historial.HistorialAdapter;
import com.example.finanzify.PantallaInicioActivity;
import com.example.finanzify.R;
import com.example.finanzify.Transacciones.CategoriasCrearPagoActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PrespuestoActivity extends AppCompatActivity {
    private PresupuestoAdapter adapter;
    private RecyclerView recyclerView; // Declaración de recyclerView como variable de instancia
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prespuesto);

        // En onCreate()
        recyclerView = findViewById(R.id.recyclerViewHistorial);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<PresupuestoA> presupuestosList = new ArrayList<>(); // Aquí debes obtener la lista de presupuestos

        adapter = new PresupuestoAdapter(PrespuestoActivity.this,presupuestosList);
        recyclerView.setAdapter(adapter);
        recuperarPresupuestos();

        //boton volver
        ImageButton buttonVolver = findViewById(R.id.buttonVolver);
        buttonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PrespuestoActivity.this, PantallaInicioActivity.class);
                startActivity(intent);
            }
        });


        Button buttonLimite = findViewById(R.id.buttonlimite);
        buttonLimite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí defines la acción que deseas realizar al hacer clic en el botón
                // Por ejemplo, iniciar una nueva actividad
                Intent intent = new Intent(PrespuestoActivity.this, CrearPresupuestoActivity.class);
                startActivity(intent);
            }
        });


    }

    private void recuperarPresupuestos() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String currentUserUID = user.getUid();
            DatabaseReference presupuestosRef = FirebaseDatabase.getInstance().getReference()
                    .child("usuarios").child(currentUserUID).child("presupuestos");

            presupuestosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<PresupuestoA> presupuestosList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Limite limite = snapshot.getValue(Limite.class);
                        String presupuestoId = limite.getClave(); // Suponiendo que tienes un método getId() en la clase Limite que devuelve un identificador único
                        Log.d("Presupuesto", "clave presupuesto " + presupuestoId);
                        if (limite != null) {
                            // Validar si el presupuesto está desactualizado y no se repite
                            if (esPresupuestoDesactualizado(limite) && !limite.isSeRepite()) {
                                // Eliminar el presupuesto de la base de datos
                                snapshot.getRef().removeValue();
                            }
                            else{
                            calcularTotalGastosPorCategoria(limite.getCategoria().getNombre(), limite.getCategoria().getTipo() , limite.getFrecuenciaRepetición(), new OnTotalGastosObtainedListener() {
                                @Override
                                public void onTotalGastosObtained(double totalGastos) {
                                    // Calcula el porcentaje
                                    double porcentaje = (totalGastos / limite.getCantidadLimite()) * 100;
                                    DecimalFormat df = new DecimalFormat("#.###");
                                   // Log.d("presupuesto", "totalGastos: " + totalGastos);

                                    PresupuestoA presupuesto = new PresupuestoA(
                                            limite.getCategoria().getNombre(),
                                            limite.getFrecuenciaRepetición(), df.format(porcentaje),
                                            totalGastos, // Asigna el total de gastos
                                            limite.getCantidadLimite(),
                                            limite.getClave(),
                                            limite.getFechaInicio(),
                                            limite.isSeRepite()
                                    );
                                    presupuestosList.add(presupuesto);
                                    // Después de agregar todos los presupuestos a la lista, configura el adaptador
                                    adapter = new PresupuestoAdapter(PrespuestoActivity.this, presupuestosList);
                                    recyclerView.setAdapter(adapter);
                                    adapter.setOnItemClickListener(new PresupuestoAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(PresupuestoA presupuesto) {
                                            Dialog dialog = new Dialog(PrespuestoActivity.this);
                                            dialog.setContentView(R.layout.dialog_presupuesto);
                                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                                            dialog.setCancelable(false);

                                            // Obtener referencias a los TextView en el diseño del diálogo
                                            TextView nombreCategoriaTextView = dialog.findViewById(R.id.nombreCategoria);
                                            TextView frecuenciaTextView = dialog.findViewById(R.id.frecuencia);
                                            TextView porcentajeTextView = dialog.findViewById(R.id.porcentaje);
                                            TextView gastadoTextView = dialog.findViewById(R.id.gastado);
                                            TextView totalTextView = dialog.findViewById(R.id.total);
                                            TextView fechaInicioTextView = dialog.findViewById(R.id.fechaInicio);
                                            TextView seRepiteTextView = dialog.findViewById(R.id.seRepite);

                                            Button okButton = dialog.findViewById(R.id.buttonaOk);
                                            Button EliminarButton = dialog.findViewById(R.id.buttonaEliminar);
                                            // Capturar la referencia al presupuesto específico
                                            DatabaseReference presupuestoRef = dataSnapshot.getRef();
                                            // Establecer el texto de cada TextView con los valores del presupuesto
                                            nombreCategoriaTextView.setText(presupuesto.getNombreCategoria());
                                            frecuenciaTextView.setText(presupuesto.getFrecuencia());
                                            porcentajeTextView.setText(presupuesto.getPorcentaje() + "%");
                                            gastadoTextView.setText(String.valueOf(presupuesto.getGastado()));
                                            totalTextView.setText(String.valueOf(presupuesto.getTotal()));
                                            fechaInicioTextView.setText(presupuesto.getFecha());
                                            seRepiteTextView.setText((presupuesto.isSeRepite() ? "Sí" : "No"));

                                            // Agregar OnClickListener al botón OK para cerrar el diálogo
                                            okButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    dialog.dismiss(); // Cerrar el diálogo
                                                }
                                            });



                                            // Agregar OnClickListener al botón Eliminar para eliminar el presupuesto
                                            EliminarButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Log.d("Presupuesto", "clave presupuesto " + presupuesto.getClave());
                                                    // Obtener la referencia al presupuesto específico
                                                    DatabaseReference presupuestoRef = presupuestosRef.child(presupuesto.getClave());

                                                    // Eliminar el presupuesto de la base de datos
                                                    presupuestoRef.removeValue();

                                                    // Eliminar el presupuesto de la lista y notificar al adaptador
                                                    presupuestosList.remove(presupuesto);
                                                    adapter.notifyDataSetChanged();
                                                    dialog.dismiss(); // Cerrar el diálogo
                                                }
                                            });


                                            dialog.show();
                                            //String message = "Presupuesto: " + presupuesto.getNombreCategoria() + ", Gastado: " + presupuesto.getGastado() + ", Total: " + presupuesto.getTotal();
                                            //Toast.makeText(PrespuestoActivity.this, message, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            });
                        }
                        }
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Manejar errores si los hay
                }
            });
        }
    }


    public interface OnTotalGastosObtainedListener {
        void onTotalGastosObtained(double totalGastos);
    }

    private void calcularTotalGastosPorCategoria(String categoria,String tipo_categoria_limite,String recurrencia , OnTotalGastosObtainedListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            // Referencia a las transacciones del usuario en la base de datos
            DatabaseReference gastosRef = FirebaseDatabase.getInstance().getReference()
                    .child("usuarios").child(uid).child("transacciones");


            gastosRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    double totalGastos = 0.0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Transaccion gasto = snapshot.getValue(Transaccion.class);
                        if (gasto != null && "pago".equals(gasto.getTipo()) && (gasto.getCategoria().getNombre().equals(categoria)
                                ||"Global".equals(tipo_categoria_limite) ) && verificarRecurrencia(gasto, recurrencia)) {
                            totalGastos += gasto.getCantidad();
                        }


                    }
                    // Llama al método onTotalGastosObtained del listener y pasa el total de gastos
                    listener.onTotalGastosObtained(totalGastos);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Manejar errores si los hay
                }
            });
        }
    }

    // Método para verificar la recurrencia de un gasto
// Método para verificar la recurrencia de un gasto
    private boolean verificarRecurrencia(Transaccion gasto, String recurrencia) {
        // Obtener la fecha del gasto
        Date fechaActual = new Date();
       // Log.d("presupuesto", "categoria transaccion: " + gasto.getCategoria().getNombre());
        // Obtener la fecha de inicio del intervalo según la recurrencia
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaActual);
        // Ajustar horas, minutos y segundos a 0 para obtener las 0:00 horas del día actual
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date inicioIntervalo = null;
        Date finIntervalo = null;
        if (recurrencia.equals("Semanal")) {
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Establecer al primer día de la semana
            inicioIntervalo = cal.getTime();
            cal.add(Calendar.WEEK_OF_YEAR, 1); // Avanzar una semana
            finIntervalo = cal.getTime();
        } else if (recurrencia.equals("Mensual")) {
            cal.set(Calendar.DAY_OF_MONTH, 1); // Establecer al primer día del mes
            inicioIntervalo = cal.getTime();
            cal.add(Calendar.MONTH, 1); // Avanzar un mes
            finIntervalo = cal.getTime();
        } else if (recurrencia.equals("Trimestral")) {
            int mes = cal.get(Calendar.MONTH);
            Log.d("presupuesto", "mes " + mes);
            cal.set(Calendar.DAY_OF_MONTH, 1); // Establecer al primer día del mes
            if (mes >= Calendar.JANUARY && mes <= Calendar.MARCH) {
                cal.set(Calendar.MONTH, Calendar.JANUARY);
            } else if (mes >= Calendar.APRIL && mes <= Calendar.JUNE) {
                cal.set(Calendar.MONTH, Calendar.APRIL);
            } else if (mes >= Calendar.JULY && mes <= Calendar.SEPTEMBER) {
                cal.set(Calendar.MONTH, Calendar.JULY);
            } else {
                cal.add(Calendar.YEAR, 1); // Avanzar un año
                cal.set(Calendar.MONTH, Calendar.JANUARY);
            }
            inicioIntervalo = cal.getTime();
            cal.set(Calendar.DAY_OF_MONTH, 1); // Establecer al primer día del mes
            cal.add(Calendar.MONTH, 3); // Avanzar tres meses
            finIntervalo = cal.getTime();
        } else if (recurrencia.equals("anual")) {
            cal.set(Calendar.DAY_OF_YEAR, 1); // Establecer al primer día del año
            inicioIntervalo = cal.getTime();
            cal.add(Calendar.YEAR, 1); // Avanzar un año
            finIntervalo = cal.getTime();
        }
        Log.d("presupuesto", "recurrencia: " + recurrencia);
        Log.d("presupuesto", "fecha inicio : " + inicioIntervalo);
        Log.d("presupuesto", "fecha fin: " + finIntervalo);
        // Verificar si la fecha del gasto está dentro del intervalo
        Date fechaGasto = parsearFecha(gasto.getFecha());
        fechaGasto = agregarSegundo(fechaGasto);
        Log.d("presupuesto", "fecha del gasto: " + fechaGasto);
        Log.d("presupuesto", "esta en intervalo " + estaEnIntervalo(fechaGasto, inicioIntervalo, finIntervalo));
        return estaEnIntervalo(fechaGasto, inicioIntervalo, finIntervalo);
    }

    // como no guardamos la hora actual de cuando se realiza un pago, se realiza 0.0

    private Date agregarSegundo(Date fecha) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.add(Calendar.SECOND, 1);
        return cal.getTime();
    }

    private boolean esPresupuestoDesactualizado(Limite limite) {
        boolean desact = false;
        String fechaInicioStr = limite.getFechaInicio();
        boolean recurrente = limite.isSeRepite();
        Date fechaActual = parsearFecha(fechaInicioStr);
        fechaActual = agregarSegundo(fechaActual);
        if (!recurrente) {
            // Verificar según la frecuencia de repetición
            if (limite.getFrecuenciaRepetición().equals("Semanal")) {
                // Obtener la fecha de fin de la semana
                // Obtener la fecha de este lunes
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                Date fechaInicio = cal.getTime();
                // Obtener la fecha del próximo lunes
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                Date fechaFin = cal.getTime();
                desact = !estaEnIntervalo(fechaActual, fechaInicio, fechaFin);

            } else if (limite.getFrecuenciaRepetición().equals("Mensual")) {
                // Obtener la fecha de fin del mes
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, 1); // Establecer el día al primero del mes
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                Date fechaInicio = cal.getTime();
                // Obtener la fecha de fin de este mes
                cal.add(Calendar.MONTH, 1); // Avanzar al primer día del próximo mes
                Date fechaFin = cal.getTime();
                desact = !estaEnIntervalo(fechaActual, fechaInicio, fechaFin);
                Log.d("presupuesto desactualizado", "fecha inicio: " + fechaInicio);
                Log.d("presupuesto desactualizado", "fecha fin: " + fechaFin);

            } else if (limite.getFrecuenciaRepetición().equals("Anual")) {
                // Obtener la fecha de fin del año
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_YEAR, 1); // Establecer el día del año al primero
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                Date fechaInicio = cal.getTime();
                cal.add(Calendar.YEAR, 1); // Avanzar al primer día del próximo año
                Date fechaFin = cal.getTime();
                desact = !estaEnIntervalo(fechaActual, fechaInicio, fechaFin);

            } else if (limite.getFrecuenciaRepetición().equals("Trimestral")) {
                // Obtener la fecha de inicio del trimestre actual
                Calendar cal = Calendar.getInstance();
                cal.setTime(fechaActual);
                int mes = cal.get(Calendar.MONTH) ;
                Log.d("presupuesto desactualizado", "mes " + mes + Calendar.MAY);
                cal = Calendar.getInstance();
                if (mes >= Calendar.JANUARY && mes <= Calendar.MARCH) {
                    // Primer trimestre: del 1 de enero al 31 de marzo
                    cal.set(Calendar.MONTH, Calendar.JANUARY);;
                } else if (mes >= Calendar.APRIL && mes <= Calendar.JUNE) {
                    // Segundo trimestre: del 1 de abril al 30 de junio
                    cal.set(Calendar.MONTH, Calendar.APRIL);
                } else if (mes >= Calendar.JULY && mes <= Calendar.SEPTEMBER) {
                    // Tercer trimestre: del 1 de julio al 30 de septiembre
                    cal.set(Calendar.MONTH, Calendar.JULY);
                } else {
                    // Cuarto trimestre: del 1 de octubre al 31 de diciembre
                    cal.set(Calendar.MONTH, Calendar.OCTOBER);
                }
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
               Date fechaInicio = cal.getTime();
                // Obtener la fecha de fin del semestre actual
                cal.add(Calendar.MONTH, 3); // Avanzar tres meses para llegar al primer día del próximo semestre
                Date fechaFin = cal.getTime();
                    desact = !estaEnIntervalo(fechaActual, fechaInicio, fechaFin);
                }
            }

        Log.d("presupuesto desactualizado", "fecha act: " + fechaActual);
        return desact;
    }


    // Método para verificar si una fecha está dentro de un intervalo
    private boolean estaEnIntervalo(Date fecha, Date inicioIntervalo, Date finIntervalo) {
        if (fecha == null || inicioIntervalo == null || finIntervalo == null) {
            return false; // Manejar el caso donde alguno de los objetos de fecha es nulo
        }
        if(fecha.equals(inicioIntervalo) || fecha.equals(finIntervalo))
            return true;

        return !fecha.before(inicioIntervalo) && !fecha.after(finIntervalo) ;
    }


    // Método para convertir una cadena de fecha en un objeto Date
    private Date parsearFecha(String fechaStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            return dateFormat.parse(fechaStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


}