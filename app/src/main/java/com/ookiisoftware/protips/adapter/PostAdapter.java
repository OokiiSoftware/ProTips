package com.ookiisoftware.protips.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Post;
import com.ookiisoftware.protips.modelo.Tipster;
import com.ookiisoftware.protips.modelo.Usuario;

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
        View view = LayoutInflater.from(activity).inflate(R.layout.item_feed, parent, false);
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
            Tipster tipster = Import.get.tipsters.FindTipster(id_Tipster);
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
            Glide.with(activity).load(path).into(holder.foto);
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

    public Post getItem(int position) {
        return list.get(position);
    }

    static class Holder extends RecyclerView.ViewHolder {
        ConstraintLayout constraintLayout;
        ImageView foto;
        ImageViewTouch foto_post;
        LinearLayout green_red, bom, ruim;
        TextView texto, data, tipster;

        Holder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.img_constraint);
            foto = itemView.findViewById(R.id.foto);
            foto_post = itemView.findViewById(R.id.foto_post);
            green_red = itemView.findViewById(R.id.green_red);
            texto = itemView.findViewById(R.id.texto);
            data = itemView.findViewById(R.id.data);
            tipster = itemView.findViewById(R.id.tipster);
            bom = itemView.findViewById(R.id.bom);
            ruim = itemView.findViewById(R.id.ruim);
        }
    }
}
