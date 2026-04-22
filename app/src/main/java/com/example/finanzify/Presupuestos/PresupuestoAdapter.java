package com.example.finanzify.Presupuestos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finanzify.Clases.Limite;
import com.example.finanzify.Clases.PresupuestoA;
import com.example.finanzify.Clases.Transaccion;
import com.example.finanzify.Estadísticas.EstadisticasAdapter;
import com.example.finanzify.Historial.HistorialAdapter;
import com.example.finanzify.R;

import java.text.DecimalFormat;
import java.util.List;

public class PresupuestoAdapter extends RecyclerView.Adapter<PresupuestoAdapter.MyViewHolder> {

    private Context mContext;
    private List<PresupuestoA> mItemList;
    private PresupuestoAdapter.OnItemClickListener listener;

    public PresupuestoAdapter(Context context, List<PresupuestoA> limites) {
        mContext = context;
        mItemList = limites;
    }

    // Método para establecer el listener desde fuera del adaptador
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Interfaz para manejar los eventos de clic
    public interface OnItemClickListener {
        void onItemClick(PresupuestoA presupuesto);
    }

    @NonNull
    @Override
    public PresupuestoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.presupuestos_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PresupuestoAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PresupuestoA limite = mItemList.get(position);
        // Aquí puedes establecer los valores de tus elementos de diseño según los datos de tu objeto Limite
        // Calcula el porcentaje de gastos gastado
        double porcentaje = (limite.getGastado() / limite.getTotal()) * 100;
        // Formatea el porcentaje con dos decimales
        DecimalFormat df = new DecimalFormat("#.##");
        String porcentajeFormateado = df.format(porcentaje);
        // Configura el progreso de la ProgressBar
        holder.progressBar.setProgress((int) porcentaje);

        // Configura el color del progreso de la ProgressBar según el porcentaje
        if (porcentaje <= 35) {
            holder.progressBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(0,128,0) ));
        } else if (porcentaje <= 65) {
            holder.progressBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(248,243,43)));
        } else {
            holder.progressBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(192,0,0)));
        }
        holder.textCategory.setText(limite.getNombreCategoria()); // Suponiendo que tienes un método para obtener el nombre de la categoría
        holder.textFrequency.setText(limite.getFrecuencia());

        holder.text_spent_percentage.setText(porcentajeFormateado + "%");
        // Formatea el gasto y el total con dos decimales
        String gastadoFormateado = df.format(limite.getGastado());
        String totalFormateado = df.format(limite.getTotal());
        holder.gastado.setText("Gastado: " + gastadoFormateado);
        holder.total.setText("Total: " + totalFormateado);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(mItemList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // Aquí declaras tus vistas según el layout
        TextView textCategory, textFrequency, text_spent_percentage, gastado, total;
        ProgressBar progressBar; // Declaración de la ProgressBar
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Aquí asignas las vistas
            textCategory = itemView.findViewById(R.id.text_category);
            textFrequency = itemView.findViewById(R.id.text_frequency);
            text_spent_percentage = itemView.findViewById(R.id.text_spent_percentage);
            gastado = itemView.findViewById(R.id.gastado);
            total = itemView.findViewById(R.id.total);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}