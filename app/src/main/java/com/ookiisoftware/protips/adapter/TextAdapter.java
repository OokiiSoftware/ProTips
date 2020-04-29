package com.ookiisoftware.protips.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.modelo.Esporte;

import java.util.ArrayList;
import java.util.HashMap;


public class TextAdapter extends RecyclerView.Adapter<TextAdapter.Holder> implements View.OnClickListener, View.OnLongClickListener {

    private HashMap<String, String> mercados;
    private HashMap<String, Esporte> esportes;
    private Activity activity;

    public TextAdapter(Activity activity, HashMap<String, String> mercados, boolean b) {
        this.activity = activity;
        this.mercados = mercados;
    }
    protected TextAdapter(Activity activity, HashMap<String, Esporte> esportes) {
        this.activity = activity;
        this.esportes = esportes;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_text, parent, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        if (mercados != null) {
            String s = (new ArrayList<>(mercados.values())).get(position);
            holder.texto.setText(s);
        }
        if (esportes != null) {
            Esporte e = (new ArrayList<>(esportes.values())).get(position);
            holder.texto.setText(e.getNome());
        }

    }

    @Override
    public int getItemCount() {
        if (mercados != null)
            return mercados.size();
        if (esportes != null)
            return esportes.size();
        return 0;
    }

    @Override
    public void onClick(View v) {}

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    public Esporte getEsporte(int position) {
        if (esportes != null)
            return (new ArrayList<>(esportes.values())).get(position);
        return null;
    }

    public String getMercado(int position) {
        if (mercados != null)
            return (new ArrayList<>(mercados.values())).get(position);
        return "";
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView texto;

        Holder(@NonNull View itemView) {
            super(itemView);
            texto = itemView.findViewById(R.id.tv_texto);
        }
    }
}
