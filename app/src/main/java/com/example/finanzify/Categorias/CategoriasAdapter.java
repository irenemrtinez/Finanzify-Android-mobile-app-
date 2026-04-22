package com.example.finanzify.Categorias;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.finanzify.Clases.Categoria;
import com.example.finanzify.R;

import java.util.List;

public class CategoriasAdapter extends RecyclerView.Adapter<CategoriasAdapter.ViewHolder> {
    private List<Categoria> categorias;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public CategoriasAdapter(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categoria_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Categoria categoria = categorias.get(position);
        holder.textViewNombreCategoria.setText(categoria.getNombre());
        Glide.with(holder.itemView.getContext()).load(categoria.getUrlImagen()).into(holder.imageViewCategoria);
        if ("Global".equals(categoria.getTipo())) {
            holder.imageViewCategoria.setBackgroundResource(R.drawable.rounded_red_background);
        } else {
            // Establecer el drawable de fondo en lugar del color
            holder.imageViewCategoria.setBackgroundResource(R.drawable.rounded_green_background);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCategoria;
        TextView textViewNombreCategoria;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCategoria = itemView.findViewById(R.id.imageViewCategoria);
            textViewNombreCategoria = itemView.findViewById(R.id.textViewNombreCategoria);
        }
    }
}