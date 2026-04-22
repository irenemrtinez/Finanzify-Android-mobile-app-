package com.example.finanzify.Historial;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finanzify.Ajustes.AjustesActivity;
import com.example.finanzify.Categorias.CategoriasAdapter;
import com.example.finanzify.Categorias.CategoriasPagosActivity;
import com.example.finanzify.Clases.Transaccion;
import com.example.finanzify.R;

import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {
    private List<Transaccion> transacciones;
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public HistorialAdapter(List<Transaccion> transacciones) {

        this.transacciones = transacciones;
    }

    public interface OnItemClickListener {
        void onItemClick(Transaccion transaccion);
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.historial_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Transaccion transaccion = transacciones.get(position);
        holder.textViewNombreCategoria.setText(transaccion.getCategoria().getNombre());
        holder.textViewFecha.setText(transaccion.getFecha());

        // Formatear la cantidad para mostrar solo dos dígitos después del punto
        String cantidadFormateada = String.format("%.2f", transaccion.getCantidad());

        // Cambiar el color del texto dependiendo del tipo de transacción
        if (transaccion.getTipo().equals("ingresos")) {
            holder.textViewCantidad.setText("+ " + cantidadFormateada);
            holder.textViewCantidad.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.verde_medio));
        } else if (transaccion.getTipo().equals("pago")) {
            holder.textViewCantidad.setText("- " + cantidadFormateada);
            holder.textViewCantidad.setTextColor(Color.RED);
        }

        // Cargar la imagen utilizando Glide
        Glide.with(holder.itemView.getContext())
                .load(transaccion.getCategoria().getUrlImagen())
                .into(holder.imageViewCategoria);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(transacciones.get(position));
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return transacciones.size();
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
}
