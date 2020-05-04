package com.ookiisoftware.protips.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ookiisoftware.protips.modelo.Esporte;

import java.util.List;

public class EsporteSpinnerAdapter extends ArrayAdapter<Esporte> {

    private List<Esporte> objects;

    public EsporteSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<Esporte> objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(objects.get(position).getNome());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
//        return super.getView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(objects.get(position).getNome());

        return label;
//        return super.getDropDownView(position, convertView, parent);
    }

    @Nullable
    @Override
    public Esporte getItem(int position) {
        return objects.get(position);
    }

    @Override
    public int getCount() {
        return objects.size();
    }
}
