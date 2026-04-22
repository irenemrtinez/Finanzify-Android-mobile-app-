package com.example.finanzify.Moneda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.finanzify.Clases.Limite;
import com.example.finanzify.Clases.Transaccion;
import com.example.finanzify.Clases.TransaccionRecurrente;
import com.example.finanzify.Clases.Usuario;
import com.example.finanzify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CambioMonedaActivity extends AppCompatActivity {
    List<String> resultList;
    ListView resultListView;
    AdaptadorMoneda resultAdapter;
    double balanceTotalConvertido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambio_moneda);
        resultListView = findViewById(R.id.result_list_view);

        try {
            loadResults();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadResults() throws IOException {
        String url = "https://api.frankfurter.app/currencies";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
                //Toast.makeText(CambioMonedaActivity.this, mMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String mMessage = response.body().string();

                Log.d("API Response", mMessage);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject obj = new JSONObject(mMessage);
                            Iterator<String> keys = obj.keys();
                            resultList = new ArrayList<>();

                            while (keys.hasNext()) {
                                String key = keys.next();
                                String currencyName = obj.getString(key);
                                resultList.add(key + " (" + currencyName + ")");
                            }
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();

                                // Aquí realizas la consulta a la base de datos para obtener la moneda preferida
                                DatabaseReference usuarioRef = FirebaseDatabase.getInstance().getReference().child("usuarios").child(uid);
                                usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // Obtener el objeto Usuario
                                        Usuario usuario = dataSnapshot.getValue(Usuario.class);
                                        if (usuario != null) {
                                            // Aquí obtienes la moneda preferida del usuario
                                            String monedaPreferida = usuario.getMonedaPreferida();
                                            resultAdapter = new AdaptadorMoneda(CambioMonedaActivity.this, android.R.layout.simple_list_item_1, resultList, monedaPreferida);
                                            resultListView.setAdapter(resultAdapter);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Manejar el error en caso de que la consulta sea cancelada
                                        Toast.makeText(CambioMonedaActivity.this, "Error al obtener la moneda preferida del usuario: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }


                            // Listener para manejar la selección de resultados
                            resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String selectedResult = resultList.get(position);
                                    // Realizar las acciones apropiadas con el resultado seleccionado
                                    // Obtener el UID del usuario actual
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user != null) {
                                        String uid = user.getUid();

                                        // Aquí realizas la consulta a la base de datos para obtener la moneda preferida
                                        DatabaseReference usuarioRef = FirebaseDatabase.getInstance().getReference().child("usuarios").child(uid);
                                        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                // Obtener el objeto Usuario
                                                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                                                if (usuario != null) {
                                                    // Aquí obtienes la moneda preferida del usuario
                                                    String monedaPreferidaActual = usuario.getMonedaPreferida();
                                                    String monedaPreferidaNueva = selectedResult;

                                                    // Obtener el balance total en la moneda inicial
                                                    double balanceTotalInicial = usuario.getBalanceTotal();
                                                    // Realizar la conversión si la moneda preferida actual es diferente de la nueva
                                                    if (!monedaPreferidaActual.equals(monedaPreferidaNueva)) {
                                                        // Realizar la conversión del balance total a la nueva moneda
                                                        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                // Obtener el objeto Usuario
                                                                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                                                                if (usuario != null) {
                                                                    // Aquí obtienes la moneda preferida del usuario
                                                                    String monedaPreferidaActual = usuario.getMonedaPreferida();
                                                                    Log.w("cantidad", String.valueOf(balanceTotalInicial));
                                                                    Log.w("moneda", monedaPreferidaActual.substring(0, 3));
                                                                    Log.w("moneda", monedaPreferidaNueva.substring(0, 3));
                                                                    // Realizar la conversión del balance total a la nueva moneda
                                                                    convertirMoneda(balanceTotalInicial, monedaPreferidaActual.substring(0, 3), monedaPreferidaNueva.substring(0, 3), new ConversionCallback() {
                                                                        @Override
                                                                        public void onConversionComplete(double cantidadConvertida) {
                                                                            // Actualizar el balance total con el nuevo valor convertido
                                                                            balanceTotalConvertido = cantidadConvertida;

                                                                                // Actualizar la lista después de cambiar la moneda preferida
                                                                                if (resultAdapter != null) {
                                                                                    resultAdapter.notifyDataSetChanged();
                                                                                }

                                                                                // Mostrar el toast de cambio realizado
                                                                                Toast.makeText(CambioMonedaActivity.this, "Cambio realizado: " + selectedResult, Toast.LENGTH_SHORT).show();
                                                                            }

                                                                        @Override
                                                                        public void onConversionFailure(String errorMessage) {
                                                                            // Manejar el error de conversión aquí
                                                                        }
                                                                    });
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                // Manejar el error en caso de que la consulta sea cancelada
                                                                Toast.makeText(CambioMonedaActivity.this, "Error al obtener la moneda preferida del usuario: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                        // Actualizar la lista después de cambiar la moneda preferida
                                                        if (resultAdapter != null) {
                                                            resultAdapter.notifyDataSetChanged();
                                                        }

                                                        Toast.makeText(CambioMonedaActivity.this, "Cambio realizado: " + selectedResult, Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        // No es necesario realizar la conversión si la moneda preferida no ha cambiado
                                                        Toast.makeText(CambioMonedaActivity.this, "La moneda preferida no ha cambiado", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                // Manejar el error en caso de que la consulta sea cancelada
                                                Toast.makeText(CambioMonedaActivity.this, "Error al obtener la moneda preferida del usuario: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }

                                    Toast.makeText(CambioMonedaActivity.this, "Cambio realizado " + selectedResult, Toast.LENGTH_SHORT).show();
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public interface ConversionCallback {
        void onConversionComplete(double cantidadConvertida);

        void onConversionFailure(String errorMessage);
    }

    public void convertirMoneda(final double cantidad, final String monedaOrigen, final String monedaDestino, final ConversionCallback callback) {
        actualizarPresupuestos(monedaOrigen, monedaDestino);
        actualizarTransacciones(monedaOrigen, monedaDestino);
        actualizarTransaccionesRecurrentes(monedaOrigen, monedaDestino);

        String url = "https://api.frankfurter.app/latest?amount=" + cantidad + "&from=" + monedaOrigen.substring(0, 3) + "&to=" + monedaDestino.substring(0, 3);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                String errorMessage = e.getMessage().toString();
                Log.w("failure Response", errorMessage);
                callback.onConversionFailure(errorMessage);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String mMessage = response.body().string();
                CambioMonedaActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject obj = new JSONObject(mMessage);
                            double output = obj.getJSONObject("rates").getDouble(monedaDestino);
                            Log.w("cantidad", String.valueOf(output));

                            // Llamar al método onConversionComplete() del callback con el valor convertido
                            callback.onConversionComplete(output);

                            // Actualizar la base de datos y mostrar el toast solo cuando la conversión se haya completado
                            // Obtener el UID del usuario actual
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();

                                // Actualizar el balance total y la moneda preferida del usuario en la base de datos
                                DatabaseReference usuarioRef = FirebaseDatabase.getInstance().getReference().child("usuarios").child(uid);
                                usuarioRef.child("monedaPreferida").setValue(monedaDestino);
                                usuarioRef.child("balanceTotal").setValue(output);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        });
    }

    public void convertidorDeMoneda(final double cantidad, final String monedaOrigen, final String monedaDestino, final ConversionCallback callback) {
        String url = "https://api.frankfurter.app/latest?amount=" + cantidad + "&from=" + monedaOrigen.substring(0, 3) + "&to=" + monedaDestino.substring(0, 3);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                String errorMessage = e.getMessage().toString();
                Log.w("failure Response", errorMessage);
                callback.onConversionFailure(errorMessage);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String mMessage = response.body().string();
                CambioMonedaActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject obj = new JSONObject(mMessage);
                            double output = obj.getJSONObject("rates").getDouble(monedaDestino);
                            Log.w("cantidad", String.valueOf(output));

                            // Llamar al método onConversionComplete() del callback con el valor convertido
                            callback.onConversionComplete(output);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    // Método para actualizar los presupuestos con la nueva moneda preferida
    private void actualizarPresupuestos(String monedaPreferidaNueva, String monedaPreferidaActual) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference presupuestosRef = FirebaseDatabase.getInstance().getReference().child("usuarios").child(uid).child("presupuestos");
            presupuestosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Limite presupuesto = snapshot.getValue(Limite.class);
                        if (presupuesto != null) {
                            // Actualizar la cantidad del presupuesto con la nueva moneda
                            double cantidadActual = presupuesto.getCantidadLimite();
                            convertidorDeMoneda(cantidadActual, monedaPreferidaNueva.substring(0, 3), monedaPreferidaActual.substring(0, 3), new ConversionCallback() {
                                @Override
                                public void onConversionComplete(double cantidadConvertida) {
                                    // Actualizar la cantidad del presupuesto con el nuevo valor convertido
                                    presupuesto.setCantidadLimite(cantidadConvertida);
                                    // Guardar el presupuesto actualizado en la base de datos
                                    snapshot.getRef().setValue(presupuesto);
                                }

                                @Override
                                public void onConversionFailure(String errorMessage) {
                                    // Manejar el error de conversión
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar el error de base de datos
                }
            });
        }
    }

    private void actualizarTransacciones(String monedaPreferidaNueva, String monedaPreferidaActual) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference transaccionesRef = FirebaseDatabase.getInstance().getReference().child("usuarios").child(uid).child("transacciones");
            transaccionesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Transaccion transaccion = snapshot.getValue(Transaccion.class);
                        if (transaccion != null) {
                            // Actualizar la cantidad de la transacción con la nueva moneda
                            double cantidadActual = transaccion.getCantidad();
                            convertidorDeMoneda(cantidadActual, monedaPreferidaNueva.substring(0, 3), monedaPreferidaActual.substring(0, 3), new ConversionCallback() {
                                @Override
                                public void onConversionComplete(double cantidadConvertida) {
                                    // Actualizar la cantidad de la transacción con el nuevo valor convertido
                                    transaccion.setCantidad(cantidadConvertida);
                                    // Guardar la transacción actualizada en la base de datos
                                    snapshot.getRef().setValue(transaccion);
                                }

                                @Override
                                public void onConversionFailure(String errorMessage) {
                                    // Manejar el error de conversión
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar el error de base de datos
                }
            });
        }
    }

    private void actualizarTransaccionesRecurrentes(String monedaPreferidaNueva, String monedaPreferidaActual) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference transaccionesRef = FirebaseDatabase.getInstance().getReference().child("usuarios").child(uid).child("transaccionesRecurrentes");
            transaccionesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        TransaccionRecurrente transaccionRecurrente = snapshot.getValue(TransaccionRecurrente.class);
                        if (transaccionRecurrente != null) {
                            // Actualizar la cantidad de la transacción recurrente con la nueva moneda
                            double cantidadActual = transaccionRecurrente.getCantidad();
                            convertidorDeMoneda(cantidadActual, monedaPreferidaNueva.substring(0, 3), monedaPreferidaActual.substring(0, 3), new ConversionCallback() {
                                @Override
                                public void onConversionComplete(double cantidadConvertida) {
                                    // Actualizar la cantidad de la transacción recurrente con el nuevo valor convertido
                                    transaccionRecurrente.setCantidad(cantidadConvertida);
                                    // Guardar la transacción recurrente actualizada en la base de datos
                                    snapshot.getRef().setValue(transaccionRecurrente);
                                }

                                @Override
                                public void onConversionFailure(String errorMessage) {
                                    // Manejar el error de conversión
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar el error de base de datos
                }
            });
        }
    }


}
