package com.example.finanzify.Estadísticas;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finanzify.Clases.CategoriaColor;
import com.example.finanzify.R;

import java.util.List;

public class EstadisticasAdapter extends RecyclerView.Adapter {
    private List<CategoriaColor> categorias;
    private Context context;

    public EstadisticasAdapter(Context context, List<CategoriaColor> categorias) {
        this.context = context;
        this.categorias = categorias;
        Log.d("PieChartAdapter", "Número de elementos en la lista: " + categorias.size());
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.estadisticas_adapter, parent, false);
        return new EstadisticasAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        CategoriaColor categoriaColor = categorias.get(position);
        viewHolder.Categoria.setText(categoriaColor.getCategoria());
        viewHolder.cantidad.setText(String.valueOf(categoriaColor.getCantidad()));

    }

    @Override
    public int getItemCount(){
        return categorias.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Categoria, cantidad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Categoria = itemView.findViewById(R.id.categoria);
            cantidad= itemView.findViewById(R.id.cantidad);
        }

    }
}
