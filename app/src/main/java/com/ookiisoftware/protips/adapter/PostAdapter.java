package com.ookiisoftware.protips.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Post;
import com.ookiisoftware.protips.modelo.Tipster;

import java.util.ArrayList;
import java.util.Objects;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Holder> implements View.OnClickListener, View.OnTouchListener {

//    private final static String TAG = "PostAdapter";
    private Activity activity;
    private ArrayList<Post> list;

    protected PostAdapter(Activity activity, ArrayList<Post> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_post, parent, false);
        view.setOnClickListener(this);
        return new Holder(view);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        final Post item = list.get(position);
        final String myId = Import.getFirebase.getId();
        String data = Import.reorder(item.getData());
        holder.data.setText(data);
        holder.texto.setText(item.getTexto());

        ArrayAdapter mercadoAdapter = ArrayAdapter.createFromResource(activity, R.array.mercado, R.layout.item_text);
        ArrayAdapter esporteAdapter = ArrayAdapter.createFromResource(activity, R.array.esporte, R.layout.item_text);

        Object esp = esporteAdapter.getItem(item.getEsporte()+1);
        Object mer = mercadoAdapter.getItem(item.getMercado()+1);

        if (esp != null)
            holder.esporte.setText(esp.toString());
        if (mer != null)
            holder.mercado.setText(mer.toString());
        holder.titulo.setText(item.getTitulo());
        holder.odd_min.setText(item.getOdd_minima());
        holder.odd_max.setText(item.getOdd_maxima());
        holder.horario_min.setText(item.getHorario_minimo());
        holder.horario_max.setText(item.getHorario_maximo());

        holder.bom.setEnabled(!item.getBom().contains(myId));
        holder.ruim.setEnabled(!item.getRuim().contains(myId));

        //region setListener
        holder.bom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!item.getBom().contains(myId)) {
                    item.getBom().add(myId);
                    item.getRuim().remove(myId);
                    item.atualizar();
                    notifyItemChanged(position);
                    Import.Alert.snakeBar(activity.getCurrentFocus(), activity.getResources().getString(R.string.tip_voto_bom));
                }
            }
        });
        holder.ruim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!item.getRuim().contains(myId)) {
                    item.getRuim().add(myId);
                    item.getBom().remove(myId);
                    item.atualizar();
                    notifyItemChanged(position);
                    Import.Alert.snakeBar(activity.getCurrentFocus(), activity.getResources().getString(R.string.tip_voto_ruim));
                }
            }
        });
        holder.foto_post.setOnTouchListener(this);
        //endregion

        //region path foto
        String id_Tipster = item.getId_tipster();
        Uri path = null;
        String foto_post = item.getFoto();
        if (Objects.equals(id_Tipster, myId)) {
            path = Import.getFirebase.getFoto();
            holder.tipster.setText(Import.getFirebase.getUser().getDisplayName());
        } else {
            Tipster tipster = Import.get.tipsters.findTipster(id_Tipster);
            if (tipster != null) {
                path = Uri.parse(tipster.getDados().getFoto());
                holder.tipster.setText(tipster.getDados().getNome());
            }
        }
        //endregion

        if (Import.getFirebase.isTipster())
            holder.green_red.setVisibility(View.VISIBLE);
        else
            holder.green_red.setVisibility(View.GONE);

        if (path != null)
            Glide.with(activity).load(path).into(holder.foto_user);
        if (foto_post != null)
            Glide.with(activity).load(foto_post).into(holder.foto_post);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View v) {}

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    static class Holder extends RecyclerView.ViewHolder {
        ImageView foto_user;
        ImageViewTouch foto_post;
        LinearLayout green_red;
        TextView titulo;
        TextView esporte;
        TextView mercado;
        TextView odd_min;
        TextView odd_max;
        TextView horario_min;
        TextView horario_max;
        TextView texto, data, tipster, bom, ruim;

        Holder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tv_titulo);
            esporte = itemView.findViewById(R.id.tv_esporte);
            mercado = itemView.findViewById(R.id.tv_mercado);
            odd_min = itemView.findViewById(R.id.tv_odd_min);
            odd_max = itemView.findViewById(R.id.tv_odd_max);
            horario_max = itemView.findViewById(R.id.tv_horario_max);
            horario_min = itemView.findViewById(R.id.tv_horario_min);

            foto_user = itemView.findViewById(R.id.iv_foto);
            foto_post = itemView.findViewById(R.id.ivt_foto_post);
            green_red = itemView.findViewById(R.id.ll_green_red);
            texto = itemView.findViewById(R.id.tv_texto);
            data = itemView.findViewById(R.id.tv_data);
            tipster = itemView.findViewById(R.id.tv_tipster);
            bom = itemView.findViewById(R.id.tv_bom);
            ruim = itemView.findViewById(R.id.tv_ruim);
        }
    }
}
