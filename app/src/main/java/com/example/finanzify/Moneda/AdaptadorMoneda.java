package com.example.finanzify.Moneda;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

    public class AdaptadorMoneda extends ArrayAdapter<String> {
        private Context mContext;
        private List<String> mValues;
        private String monedaPreferida; // Variable para almacenar la moneda preferida del usuario

        public AdaptadorMoneda(Context context, int resource, List<String> values, String monedaPreferida) {
            super(context, resource, values);
            mContext = context;
            mValues = values;
            this.monedaPreferida = monedaPreferida;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(android.R.layout.simple_list_item_1, null);
            }

            String item = mValues.get(position);
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(item);

            // Siempre establece el color del texto en rojo
            // Si el item es igual a la moneda preferida, establece el color del texto en rojo; de lo contrario, en negro
            if (item.substring(0, 3).equals(monedaPreferida.substring(0, 3))) {
                textView.setTextColor(Color.RED);
            } else {
                textView.setTextColor(Color.BLACK);
            }

            return view;
        }
    }
