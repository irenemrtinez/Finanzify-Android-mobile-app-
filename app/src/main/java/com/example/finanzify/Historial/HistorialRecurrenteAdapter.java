package com.example.finanzify.Historial;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finanzify.Clases.TransaccionRecurrente;
import com.example.finanzify.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistorialRecurrenteAdapter extends RecyclerView.Adapter<HistorialRecurrenteAdapter.ViewHolder> {
    private List<TransaccionRecurrente> transaccionesRecurrentes;
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public HistorialRecurrenteAdapter(List<TransaccionRecurrente> transaccionesRecurrentes) {
        this.transaccionesRecurrentes = transaccionesRecurrentes;
    }

    public interface OnItemClickListener {
        void onItemClick(TransaccionRecurrente transaccionRecurrente);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.historial_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TransaccionRecurrente transaccionRecurrente = transaccionesRecurrentes.get(position);
        holder.textViewNombreCategoria.setText(transaccionRecurrente.getCategoria().getNombre());
        Calendar calendar = Calendar.getInstance();
        java.util.Date currentDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);
        //formattedDate ="20/05/2024";
        if(!esFechaAnteriorOIgual(transaccionRecurrente.getFechaFin(),formattedDate))
        holder.textViewFecha.setText(transaccionRecurrente.getFechaInicio());
        else
            holder.textViewFecha.setText(transaccionRecurrente.getFechaInicio() + " - " +transaccionRecurrente.getFechaFin());
        // Formatear la cantidad para mostrar solo dos dígitos después del punto
        String cantidadFormateada = String.format("%.2f", transaccionRecurrente.getCantidad());

        // Cambiar el color del texto dependiendo del tipo de transacción
        if (transaccionRecurrente.getTipo().equals("ingresos")) {
            holder.textViewCantidad.setText("+ " + cantidadFormateada);
            holder.textViewCantidad.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.verde_medio));
        } else if (transaccionRecurrente.getTipo().equals("pago")) {
            holder.textViewCantidad.setText("- " + cantidadFormateada);
            holder.textViewCantidad.setTextColor(Color.RED);
        }

        // Cargar la imagen utilizando Glide
        Glide.with(holder.itemView.getContext())
                .load(transaccionRecurrente.getCategoria().getUrlImagen())
                .into(holder.imageViewCategoria);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(transaccionesRecurrentes.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return transaccionesRecurrentes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombreCategoria;
        TextView textViewFecha;
        TextView textViewCantidad;
        ImageView imageViewCategoria;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCategoria = itemView.findViewById(R.id.imageViewCategoria);
            textViewNombreCategoria = itemView.findViewById(R.id.textViewNombreCategoria);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            textViewCantidad = itemView.findViewById(R.id.textViewCantidad);
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

}
