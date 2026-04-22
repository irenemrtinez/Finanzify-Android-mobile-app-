package com.example.finanzify;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.WorkManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finanzify.Ajustes.AjustesActivity;
import com.example.finanzify.Ajustes.CambiarContraActivity;
import com.example.finanzify.Clases.Categoria;
import com.example.finanzify.Clases.Transaccion;
import com.example.finanzify.Clases.TransaccionRecurrente;
import com.example.finanzify.Estadísticas.EstadisticasActivity;
import com.example.finanzify.Historial.HistorialActivity;
import com.example.finanzify.Historial.HistorialRecurrentesActivity;
import com.example.finanzify.Login.LoginActivity;
import com.example.finanzify.Login.RegistrarActivity;
import com.example.finanzify.Presupuestos.PrespuestoActivity;
import com.example.finanzify.R;
import com.example.finanzify.Transacciones.CrearIngresoActivity;
import com.example.finanzify.Transacciones.CrearPagoActivity;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

public class PantallaInicioActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    DatabaseReference databaseRef;
    FirebaseUser currentUser;
    private List<Transaccion> transaccionesList;

    // Formatear la fecha en el formato deseado (por ejemplo, "dd/MM/yyyy")
    Calendar calendar = Calendar.getInstance();
    java.util.Date currentDate = calendar.getTime();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    //String formattedDate = "15/08/2024";
    String formattedDate = dateFormat.format(currentDate);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_inicio);

        // actualizar los pagos
        cobrarTransaccionesRecurrentes();
        cobrarTransaccionesPendientes();
        WorkManager.getInstance(PantallaInicioActivity.this).cancelAllWork();
        transaccionesList = new ArrayList<>();
        // Obtener la fecha actual
        Calendar calendar = Calendar.getInstance();
        java.util.Date currentDate = calendar.getTime();


        // Encontrar el TextView en tu layout y establecer la fecha formateada
        TextView textViewDate = findViewById(R.id.textViewDate);
        textViewDate.setText(formattedDate);

        TextView textViewBalance = findViewById(R.id.textViewBalance);
        // AQUI X DEBE SER EL BALANCE TOTAL QUE SACAREMOS DE UNA LISTA O LA BASE DE DATOS

        // Inicializa Firebase Auth y obtén el usuario actual
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Obtén una referencia a la base de datos de Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // Verifica si el usuario está autenticado
        if (currentUser != null) {
            // Obtiene el ID único del usuario actual
            String uid = currentUser.getUid();
            // Obtiene una referencia al nodo del usuario en la base de datos
            DatabaseReference usuarioRef = databaseRef.child("usuarios").child(uid);
            // Agrega un listener para obtener los datos del usuario
            usuarioRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Verifica si los datos existen
                    if (dataSnapshot.exists()) {
                        // Recupera los datos del usuario
                        Double balanceTotal = dataSnapshot.child("balanceTotal").getValue(Double.class);
                        String formattedBalance = String.format("%.2f", balanceTotal);
                        String monedaPreferida = dataSnapshot.child("monedaPreferida").getValue(String.class);
                        // Aquí puedes seguir recuperando otros datos del usuario, como el balance total, etc.
                        String bm = formattedBalance  + " " + monedaPreferida;
                        textViewBalance.setText(bm); // SACAR SIMBOLO DE LA MONEDA ELEGIDA EN AJUSTES

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Maneja el error si la lectura de datos se cancela
                }
            });
        } else {
            String bm = "0.0" + " " + "EU";
            textViewBalance.setText(bm); // SACAR SIMBOLO DE LA MONEDA ELEGIDA EN AJUSTES
        }


        //TextView textViewGasTotal = findViewById(R.id.textViewGasTotal);
        DecimalFormat df = new DecimalFormat("#.##");

        // Llamar a obtenerTransaccionesUsuario() y manejar el valor devuelto en el callback
        obtenerTransaccionesUsuarioPago(new TotalCantidadesPagoCallback () {
            @Override
            public void onTotalCantidadesObtenido(double totalCantidades) {
                // Actualizar el TextView con el valor de totalCantidades
                TextView textViewGasTotal = findViewById(R.id.textViewGasTotal);
                // Convertir el valor double a int (asumiendo que el totalCantidades es un valor en euros)
                double x =  totalCantidades;
                String Formateado = df.format(x);
                textViewGasTotal.setText(Formateado);
            }
        });


        //TextView textViewGasTotal = findViewById(R.id.textViewGasTotal);
        // Llamar a obtenerTransaccionesUsuario() y manejar el valor devuelto en el callback
        obtenerTransaccionesUsuarioIngresos(new TotalCantidadesIngresosCallback () {
            @Override
            public void onTotalCantidadesObtenido(double totalCantidades) {
                // Actualizar el TextView con el valor de totalCantidades
                TextView textViewIngTotal = findViewById(R.id.textViewIngTotal);
                // Convertir el valor double a int (asumiendo que el totalCantidades es un valor en euros)
                double x =  totalCantidades;
                String Formateado = df.format(x);
                textViewIngTotal.setText(Formateado);
            }
        });

        //botton de ajustes
        ImageButton buttonAjustes = findViewById(R.id.buttonAjustes);

        // Agregar un OnClickListener al ImageButton
        buttonAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para iniciar la nueva actividad (AjustesActivity)
                Intent intent = new Intent(PantallaInicioActivity.this, AjustesActivity.class);
                startActivity(intent);
            }
        });

        // Configuración del OnClickListener para el botón de inicio de sesión
        Button botonPago = findViewById(R.id.buttonpagoIng);
        botonPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_reg = new Intent(PantallaInicioActivity.this, CrearPagoActivity.class);
                startActivity(intent_reg);
            }
        });



        // Configuración del OnClickListener para el botón de inicio de sesión
        Button botonHist = findViewById(R.id.buttonHist);
        botonHist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_reg = new Intent(PantallaInicioActivity.this, HistorialActivity.class);
                startActivity(intent_reg);
            }
        });


        // boton estadisticas
        Button buttonEst = findViewById(R.id.buttonEst);
        buttonEst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PantallaInicioActivity.this, EstadisticasActivity.class);
                startActivity(intent);
            }
        });

    // boton estadisticas
    Button buttonPre= findViewById(R.id.buttonPresu);
        buttonPre.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(PantallaInicioActivity.this, PrespuestoActivity.class);
            startActivity(intent);
        }
    });
}
    private void cobrarTransaccionesRecurrentes() {
        Log.d("TransaccionRecurrenteWorker", "entrando en transacciones recurrentes");

        // Obtener la fecha actual
        Calendar calendar = Calendar.getInstance();
        java.util.Date currentDate = calendar.getTime();

        // Formatear la fecha en el formato deseado (por ejemplo, "dd/MM/yyyy")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        //String formattedDate = dateFormat.format(currentDate);

        // Obtener el UID del usuario actual
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserUID = mAuth.getCurrentUser().getUid();

        // Obtener una referencia a la base de datos
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Obtener una referencia a las transacciones del usuario actual
        DatabaseReference transaccionesRef = databaseRef.child("usuarios").child(currentUserUID).child("transaccionesRecurrentes");

        // recoger las transacciones recurrentes y crear las transacciones normales con la fecha que toca

        // Leer las transacciones recurrentes del usuario
        transaccionesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Iterar a través de las transacciones recurrentes y agregarlas a la lista
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TransaccionRecurrente transaccionRecurrente = snapshot.getValue(TransaccionRecurrente.class);
                    Log.d("TransaccionRecurrente UltimaFecha",  transaccionRecurrente.getCategoria().getNombre());

                    // Primero verifica si fechasPagosRealizados es nulo o no
                    if (transaccionRecurrente != null && (transaccionRecurrente.getFechasPagosRealizados() != null) && !transaccionRecurrente.isPrimeravez()){
                    if(!transaccionRecurrente.getFechasPagosRealizados().isEmpty()){
                            Log.d("TransaccionRecurrente UltimaFecha", "entra en el if en el que la lista no puede ser null " +transaccionRecurrente.getCategoria().getNombre());
                            List<String> fechasPagosRealizados = transaccionRecurrente.getFechasPagosRealizados();
                            // Verifica si la lista de fechas pagadas no está vacía
                            if (!fechasPagosRealizados.isEmpty()) {
                                // Obtener la última fecha de la lista
                                String ultimaFecha = fechasPagosRealizados.get(fechasPagosRealizados.size() - 1);
                                // Ahora puedes usar la variable "ultimaFecha" como necesites
                                Log.d("TransaccionRecurrente UltimaFecha", "La última fecha de pago realizado: " + ultimaFecha);

                                // Convertir la última fecha a un objeto Calendar
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                Calendar calendarUltimaFecha = Calendar.getInstance();
                                try {
                                    calendarUltimaFecha.setTime(dateFormat.parse(ultimaFecha));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    return; // Salir del método si hay un error al parsear la fecha
                                }
                                // Calcular la siguiente fecha según la recurrencia
                                switch (transaccionRecurrente.getRecurrencia()) {
                                    case "Diario":
                                        calendarUltimaFecha.add(Calendar.DAY_OF_YEAR, 1);
                                        break;
                                    case "Semanal":
                                        calendarUltimaFecha.add(Calendar.DAY_OF_YEAR, 7);
                                        break;
                                    case "Mensual":
                                        calendarUltimaFecha.add(Calendar.MONTH, 1);
                                        break;
                                    case "Trimestral":
                                        calendarUltimaFecha.add(Calendar.MONTH, 3);
                                        break;
                                    case "Anual":
                                        calendarUltimaFecha.add(Calendar.YEAR, 1);
                                        break;
                                    default:
                                        Toast.makeText(PantallaInicioActivity.this, "Error al obtener la recurrencia ", Toast.LENGTH_SHORT).show();
                                        return; // Salir del método si la recurrencia no es válida
                                }

                                // Obtener la siguiente fecha en formato de texto
                                Date siguienteFecha = calendarUltimaFecha.getTime();
                                String siguienteFechaString = dateFormat.format(siguienteFecha);
                                Log.d("TransaccionRecurrente UltimaFecha", "La última fecha de pago realizada es: " + siguienteFechaString);

                                //PRUEBAS

                                // si la siguiente fecha del pago es anterior o igual a la fecha actual, se cobra el pago Y es anterior o igual a la fecha
                                if ((esFechaAnteriorOIgual(siguienteFechaString, formattedDate) || siguienteFechaString.equals(formattedDate))
                                        && !formattedDate.equals(transaccionRecurrente.getFechasPagosRealizados().get(transaccionRecurrente.getFechasPagosRealizados().size()-1))) {
                                    if (transaccionRecurrente.getFechaFin().equals("") || transaccionRecurrente.getFechaFin() == null || esFechaAnteriorOIgual(siguienteFechaString, transaccionRecurrente.getFechaFin())) {
                                        Log.d("TransaccionRecurrente creartransaccion", "entra a esfechaanterior o igual ");
                                        String mensaje = transaccionRecurrente.getMensaje() + " Transaccion creada a partir de una transacción recurrente el día " + siguienteFechaString;

                                        // Agregar la nueva fecha a la lista de fechas de pagos realizados
                                        transaccionRecurrente.getFechasPagosRealizados().add(siguienteFechaString);

                                        // Actualizar la lista fechasPagosRealizados en la base de datos
                                        // aqui entra
                                        agregarNuevaTransaccion(siguienteFechaString, transaccionRecurrente.getCantidad(), transaccionRecurrente.getCategoria(),
                                                mensaje, transaccionRecurrente.getTipo());
                                        // aqui entra bien porque la lista aparece con todas las fechas que debe
                                        snapshot.getRef().child("fechasPagosRealizados").setValue(transaccionRecurrente.getFechasPagosRealizados());
                                        break;
                                    }
                                }

                            }
                        }
                    } else {
                            // Después, verifica si la fecha de inicio es anterior o igual a la fecha actual
                             Log.d("TransaccionRecurrente fecha", "transaccionRecurrente no es null: " + (transaccionRecurrente != null));
                        Log.d("TransaccionRecurrente fecha", "transaccionRecurrente.isPrimeravez(): " + transaccionRecurrente.isPrimeravez());
                        Log.d("TransaccionRecurrente fecha", "esFechaAnteriorOIgual(transaccionRecurrente.getFechaInicio(), formattedDate): " + esFechaAnteriorOIgual(transaccionRecurrente.getFechaInicio(), formattedDate));
                        Log.d("TransaccionRecurrente fecha", "transaccionRecurrente.getFechasPagosRealizados() == null: " + (transaccionRecurrente.getFechasPagosRealizados() == null));

                        if (transaccionRecurrente != null && transaccionRecurrente.isPrimeravez() && esFechaAnteriorOIgual(transaccionRecurrente.getFechaInicio(), formattedDate)
                                && transaccionRecurrente.getFechasPagosRealizados() == null && (esFechaAnteriorOIgual(transaccionRecurrente.getFechaInicio(), transaccionRecurrente.getFechaFin())) ||
                        transaccionRecurrente.getFechaFin().equals("") || transaccionRecurrente.getFechaFin()==null) {
                                Log.d("TransaccionRecurrente fecha", "entra en el primer if donde la lista es null");
                                String mensaje = transaccionRecurrente.getMensaje() + " Transaccion creada a partir de una transacción recurrente el día " + transaccionRecurrente.getFechaInicio();
                                agregarNuevaTransaccion(transaccionRecurrente.getFechaInicio(), transaccionRecurrente.getCantidad(), transaccionRecurrente.getCategoria(),
                                        mensaje, transaccionRecurrente.getTipo());
                                TransaccionRecurrente transaccionRec = new TransaccionRecurrente();
                                transaccionRec.setFechasPagosRealizados(new ArrayList<>(Collections.singletonList(transaccionRecurrente.getFechaInicio())));
                                // Actualizar la lista fechasPagosRealizados en la base de datos
                                snapshot.getRef().child("fechasPagosRealizados").setValue(transaccionRec.getFechasPagosRealizados());
                                snapshot.getRef().child("primeravez").setValue(false);
                            }
                        }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PantallaInicioActivity.this, "Error al obtener transacciones recurrentes: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void agregarNuevaTransaccion(String fecha, double cantidad, Categoria categoria, String mensaje, String tipo) {
        // Obtener el UID del usuario actual
        Log.d("TransaccionR agrega fecha", "entrado a crear transaccion" +fecha);
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
                Transaccion transaccion = new Transaccion(fecha, cantidad, categoria, mensaje, tipo, false);
                transaccionRef.setValue(transaccion); // Guardamos los datos de la nueva transacción en la base de datos
                Log.d("transaccion", "transaccion creada " + categoria.getNombre() + fecha);

                // Mostramos un mensaje de éxito
                //Toast.makeText(PantallaInicioActivity.this, "Transacción creada exitosamente", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores si los hay
                Toast.makeText(PantallaInicioActivity.this, "Error al obtener el número de transacciones: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void cobrarTransaccionesPendientes() {
        Log.d("TransaccionRecurrenteWorker", "entrando en transacciones pendienes");
        // Obtener el UID del usuario actual
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserUID = mAuth.getCurrentUser().getUid();

        // Obtener una referencia a la base de datos
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Obtener una referencia a las transacciones del usuario actual
        DatabaseReference transaccionesRef = databaseRef.child("usuarios").child(currentUserUID).child("transacciones");

        // Obtener la fecha actual
        Calendar calendar = Calendar.getInstance();
        java.util.Date currentDate = calendar.getTime();

        // Formatear la fecha en el formato deseado (por ejemplo, "dd/MM/yyyy")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        //String formattedDate = dateFormat.format(currentDate);

        // Escuchar los cambios en las transacciones
        transaccionesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Iterar sobre todas las transacciones
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Obtener la transacción
                    Transaccion transaccion = snapshot.getValue(Transaccion.class);
                    if (transaccion != null && !transaccion.isCobrado() && esFechaAnteriorOIgual(transaccion.getFecha(), formattedDate)) {
                        if(transaccion.getTipo().equals("pago"))
                        actualizarBalance(transaccion.getCantidad(), "pago");
                        else
                            actualizarBalance(transaccion.getCantidad(), "ingresos");
                        // Marcar la transacción como cobrada
                        transaccion.setCobrado(true);
                        // Actualizar la transacción en la base de datos
                        snapshot.getRef().setValue(transaccion);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de base de datos
            }
        });
    }

    //funciones
    interface TotalCantidadesPagoCallback {
        void onTotalCantidadesObtenido(double totalCantidades);
    }

    private void obtenerTransaccionesUsuarioPago(TotalCantidadesPagoCallback callback) {
        // Obtener el ID del usuario actual
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            // Referencia a las transacciones del usuario en la base de datos
            DatabaseReference transaccionesRef = FirebaseDatabase.getInstance().getReference()
                    .child("usuarios").child(uid).child("transacciones");

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
            String formattedFirstDayOfThisMonth = dateFormat.format(firstDayOfThisMonth);
            String formattedFirstDayOfNextMonth = dateFormat.format(firstDayOfNextMonth);
            // Imprimir las fechas

            Query query = transaccionesRef.orderByChild("fecha");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Inicializar la suma de cantidades
                    double totalCantidades = 0.0;

                    // Iterar a través de las transacciones y sumar las cantidades
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Transaccion transaccion = snapshot.getValue(Transaccion.class);
                        // Logs para las fechas de inicio y fin
                        Log.d("TAG", "Fecha de inicio: " + formattedFirstDayOfThisMonth);
                        Log.d("TAG", "Fecha de fin: " + formattedFirstDayOfNextMonth);
                        Log.d("TAG", "categoria " + transaccion.getCategoria().getNombre());
                        Log.d("TAG", "intervalo " + enIntervalo(transaccion.getFecha(),
                                formattedFirstDayOfThisMonth, formattedFirstDayOfNextMonth));
                        if (transaccion != null && transaccion.getTipo().equals("pago") && transaccion.isCobrado() && enIntervalo(transaccion.getFecha(),
                                formattedFirstDayOfThisMonth, formattedFirstDayOfNextMonth) && !transaccion.getFecha().equals(formattedFirstDayOfNextMonth)) {
                            // Sumar la cantidad de la transacción al total
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

    //funciones
    interface TotalCantidadesIngresosCallback {
        void onTotalCantidadesObtenido(double totalCantidades);
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

    private void obtenerTransaccionesUsuarioIngresos(TotalCantidadesIngresosCallback callback) {
        // Obtener el ID del usuario actual
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            // Referencia a las transacciones del usuario en la base de datos
            DatabaseReference transaccionesRef = FirebaseDatabase.getInstance().getReference()
                    .child("usuarios").child(uid).child("transacciones");

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
            String formattedFirstDayOfThisMonth = dateFormat.format(firstDayOfThisMonth);
            String formattedFirstDayOfNextMonth = dateFormat.format(firstDayOfNextMonth);
            // Imprimir las fechas
            //Toast.makeText(PantallaInicioActivity.this, String.valueOf(firstDayOfThisMonth), Toast.LENGTH_SHORT).show();

            // Filtrar las transacciones que ocurrieron dentro del mes actual y cuyo tipo sea "ingresos"
            Query query = transaccionesRef.orderByChild("fecha");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Inicializar la suma de cantidades
                    double totalCantidades = 0.0;

                    // Iterar a través de las transacciones y sumar las cantidades
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Transaccion transaccion = snapshot.getValue(Transaccion.class);
                        if (transaccion != null && transaccion.getTipo().equals("ingresos") && enIntervalo(transaccion.getFecha(),
                                formattedFirstDayOfThisMonth, formattedFirstDayOfNextMonth) && !transaccion.getFecha().equals(formattedFirstDayOfNextMonth)) {
                            // Sumar la cantidad de la transacción al total
                            totalCantidades += transaccion.getCantidad();
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
                if(tipo.equals("pago"))
                nuevoBalance = balanceActual - cantidad; // Cambiado a suma según el enunciado
                else
                    nuevoBalance = balanceActual + cantidad; // Cambiado a suma según el enunciado
                // Actualizar el balance en la base de datos
                balanceRef.setValue(nuevoBalance);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores si los hay
                Toast.makeText(PantallaInicioActivity.this, "Error al obtener el balance: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}


