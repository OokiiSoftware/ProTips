package com.ookiisoftware.protips.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.auxiliar.OnSwipeListener;
import com.ookiisoftware.protips.modelo.PostPerfil;

import java.util.ArrayList;
import java.util.Objects;

public class PostPerfilAdapter extends RecyclerView.Adapter<PostPerfilAdapter.Holder> implements View.OnClickListener, View.OnLongClickListener {

    //region Variáveis
    private final static String TAG = "PostPerfilAdapter";
    private Activity activity;
    private ArrayList<PostPerfil> data;
    private OnSwipeListener swipeListener;
    private boolean resume;

    //endregion

    protected PostPerfilAdapter(Activity activity, ArrayList<PostPerfil> data, boolean resume, OnSwipeListener swipeListener) {
        this.activity = activity;
        this.data = data;
        this.swipeListener = swipeListener;
        this.resume = resume;
    }

    //region Overrides

    @NonNull
    @Override
    public PostPerfilAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_post_perfil, parent, false);
        view.setOnClickListener(this);
        view.setOnTouchListener(swipeListener);
        view.setOnLongClickListener(this);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostPerfilAdapter.Holder holder, int position) {
        PostPerfil item = data.get(position);

        if (resume) {
            holder.titulo.setVisibility(View.GONE);
            holder.texto.setVisibility(View.GONE);
        } else {
            holder.texto.setVisibility(View.VISIBLE);
            holder.titulo.setVisibility(View.VISIBLE);

            if (item.getTitulo() == null || item.getTitulo().isEmpty())
                holder.titulo.setVisibility(View.GONE);
            else
                holder.titulo.setText(item.getTitulo());
            if (item.getTexto() == null || item.getTexto().isEmpty())
                holder.texto.setVisibility(View.GONE);
            else
                holder.texto.setText(item.getTexto());
        }

        holder.toolbar.setVisibility(View.GONE);
        holder.toolbar.setOnMenuItemClickListener(_item -> {
            switch (_item.getItemId()) {
                case R.id.menu_excluir: {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                    dialog.setTitle(activity.getResources().getString(R.string.post_excluir));
                    dialog.setMessage(activity.getResources().getString(R.string.deseja_prosseguir));

                    dialog.setPositiveButton(activity.getResources().getString(R.string.sim), (dialog1, which) -> item.excluir(activity));
                    dialog.setNeutralButton(activity.getResources().getString(R.string.cancelar), null);
                    dialog.show();
                    break;
                }
                case R.id.menu_ajuda: {
                    break;
                }
            }
            return false;
        });

        try {
            Glide.with(activity).load(item.getFoto()).into(holder.foto);
        } catch (Exception e) {
            Import.Alert.e(TAG, "onBindViewHolder", e);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onClick(View v) {}

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    //endregion

    public boolean isMyPost(int position) {
        PostPerfil item = data.get(position);
        return Objects.equals(item.getId_tipster(), Import.getFirebase.getId());
    }

    static class Holder extends RecyclerView.ViewHolder {
        Toolbar toolbar;
        ImageView foto;
        TextView titulo;
        TextView texto;

        Holder(@NonNull View itemView) {
            super(itemView);
            toolbar = itemView.findViewById(R.id.toolbar);
            foto = itemView.findViewById(R.id.iv_foto);
            texto = itemView.findViewById(R.id.et_texto);
            titulo = itemView.findViewById(R.id.et_titulo);

            texto.setFocusable(false);
            titulo.setFocusable(false);
            texto.setFocusableInTouchMode(false);
            titulo.setFocusableInTouchMode(false);
        }
    }
}
