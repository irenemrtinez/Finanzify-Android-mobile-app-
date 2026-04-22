package com.example.finanzify.Estadísticas;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finanzify.Clases.CategoriaColor;
import com.example.finanzify.R;

import java.util.List;
import java.util.Random;

public class PieChartAdapter extends RecyclerView.Adapter<PieChartAdapter.ViewHolder> {

    private List<CategoriaColor> categorias;
    private Context context;

    public PieChartAdapter(Context context, List<CategoriaColor> categorias) {
        this.context = context;
        this.categorias = categorias;
        Log.d("PieChartAdapter", "Número de elementos en la lista: " + categorias.size());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pie_chart_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoriaColor categoriaColor= categorias.get(position);
        holder.txtCategoria.setText(categoriaColor.getCategoria());
        // Establecer el color del cuadrado
        holder.cuadradito.setBackgroundColor(categoriaColor.getColor());
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public List<CategoriaColor> getData() {
        return categorias;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCategoria;
        View cuadradito;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCategoria = itemView.findViewById(R.id.categoria);
            cuadradito = itemView.findViewById(R.id.cuadrado);
        }

    }
}
