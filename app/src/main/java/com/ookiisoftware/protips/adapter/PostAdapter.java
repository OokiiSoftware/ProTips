package com.ookiisoftware.protips.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.activity.PerfilActivity;
import com.ookiisoftware.protips.activity.PerfilTipsterActivity;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Post;
import com.ookiisoftware.protips.modelo.User;

import java.util.ArrayList;
import java.util.Objects;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Holder> implements View.OnClickListener, View.OnTouchListener {

    //region Variáveis
//    private final static String TAG = "PostAdapter";
    private Activity activity;
    private ArrayList<Post> data;
    //endregion

    protected PostAdapter(Activity activity, ArrayList<Post> data) {
        this.activity = activity;
        this.data = data;
    }

    //region Overrides

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
        final Post item = data.get(position);
        final String meuId = Import.getFirebase.getId();
        final String link = item.getLink();
        final MenuItem menuItemExcluir = holder.toolbar.getMenu().findItem(R.id.menu_excluir);
        final MenuItem menuItemLink = holder.toolbar.getMenu().findItem(R.id.menu_link);
        String _data = Import.reorder(item.getData());
        holder.data.setText(_data);
        holder.texto.setText(item.getTexto());

        holder.esporte.setText(item.getEsporte());
        holder.mercado.setText(item.getMercado());
        holder.titulo.setText(item.getTitulo());
        holder.odd_min.setText(item.getOdd_minima());
        holder.odd_max.setText(item.getOdd_maxima());
        holder.horario_min.setText(item.getHorario_minimo());
        holder.horario_max.setText(item.getHorario_maximo());

        //region path foto
        String userId = item.getId_tipster();
        Uri path = null;
        String foto_post = item.getFoto();
        if (Objects.equals(userId, meuId)) {
            path = Import.getFirebase.getFoto();
            holder.userName.setText(Import.getFirebase.getUser().getDisplayName());
        } else {
            User tipster = Import.get.tipsters.get(userId);
            if (tipster != null) {
                path = Uri.parse(tipster.getDados().getFoto());
                holder.userName.setText(tipster.getDados().getNome());
            }
        }

        if (path != null)
            Glide.with(activity).load(path).into(holder.foto_user);
        if (foto_post != null)
            Glide.with(activity).load(foto_post).into(holder.foto_post);

        //endregion

        if (Objects.equals(meuId, userId)) {
            holder.green_red.setVisibility(View.VISIBLE);
            menuItemExcluir.setVisible(true);

            if (item.getBom().containsValue(meuId))
                holder.bom.setTextColor(activity.getResources().getColor(R.color.amarelo));
            else
                holder.bom.setTextColor(activity.getResources().getColor(R.color.text_light));
            if (item.getRuim().containsValue(meuId))
                holder.ruim.setTextColor(activity.getResources().getColor(R.color.amarelo));
            else
                holder.ruim.setTextColor(activity.getResources().getColor(R.color.text_light));
        } else {
            menuItemExcluir.setVisible(false);
            holder.green_red.setVisibility(View.GONE);
        }

        menuItemLink.setVisible(link != null && !link.isEmpty());

        //region setListener
        holder.bom.setOnClickListener(v -> {
            if (item.getBom().containsValue(meuId)) {
                item.removeBom(meuId);
                Import.Alert.snakeBar(activity, activity.getResources().getString(R.string.tip_voto_removido));
            } else {
                item.addBom(meuId);
                Import.Alert.snakeBar(activity, activity.getResources().getString(R.string.tip_voto_bom));
            }
            notifyItemChanged(position);
        });
        holder.ruim.setOnClickListener(v -> {
            if (item.getRuim().containsValue(meuId)) {
                item.removeRuim(meuId);
                Import.Alert.snakeBar(activity, activity.getResources().getString(R.string.tip_voto_removido));
            } else {
                item.addRuim(meuId);
                Import.Alert.snakeBar(activity, activity.getResources().getString(R.string.tip_voto_ruim));
            }
            notifyItemChanged(position);
        });

        holder.toolbar.setOnClickListener(v -> {
            Intent intent;
            if (Objects.equals(item.getId_tipster(), meuId)) {
                intent = new Intent(activity, PerfilActivity.class);
            } else {
                intent = new Intent(activity, PerfilTipsterActivity.class);
                intent.putExtra(Constantes.intent.USER_ID, item.getId_tipster());
            }
            activity.startActivity(intent);
        });
        holder.toolbar.setOnMenuItemClickListener(_item -> {
            switch (_item.getItemId()) {
                case R.id.menu_excluir: {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                    dialog.setTitle(activity.getResources().getString(R.string.excluir));
                    dialog.setMessage(activity.getResources().getString(R.string.excluir_post));
                    dialog.setPositiveButton(activity.getResources().getString(R.string.ok), (dialog1, which) -> {
                        item.excluir(PostAdapter.this);
                        holder.userName.setText(activity.getResources().getString(R.string.excluindo));
                    });
                    dialog.setNegativeButton(activity.getResources().getString(R.string.cancelar), null);
                    dialog.show();
                    break;
                }
                case R.id.menu_link: {
                    Import.abrirLink(activity, link);
                    break;
                }
            }
            return false;
        });

        holder.foto_post.setOnTouchListener(this);
        //endregion

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onClick(View v) {}

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    //endregion

    static class Holder extends RecyclerView.ViewHolder {
        ImageView foto_user;
        ImageViewTouch foto_post;
        LinearLayout green_red;
        Toolbar toolbar;
        TextView titulo;
        TextView esporte;
        TextView mercado;
        TextView odd_min;
        TextView odd_max;
        TextView horario_min;
        TextView horario_max;
        TextView texto, data, userName, bom, ruim;

        Holder(@NonNull View itemView) {
            super(itemView);
            toolbar = itemView.findViewById(R.id.toolbar);
            titulo = itemView.findViewById(R.id.tv_titulo);
            esporte = itemView.findViewById(R.id.tv_esporte);
            mercado = itemView.findViewById(R.id.tv_mercado);
            odd_min = itemView.findViewById(R.id.tv_odd_min);
            odd_max = itemView.findViewById(R.id.tv_odd_max);
            horario_max = itemView.findViewById(R.id.tv_horario_max);
            horario_min = itemView.findViewById(R.id.tv_horario_min);

            foto_user = itemView.findViewById(R.id.iv_foto);
            foto_post = itemView.findViewById(R.id.ivt_foto_post);
            green_red = itemView.findViewById(R.id.tipster_container);
            texto = itemView.findViewById(R.id.tv_texto);
            data = itemView.findViewById(R.id.tv_data);
            userName = itemView.findViewById(R.id.tv_tipster);
            bom = itemView.findViewById(R.id.tv_bom);
            ruim = itemView.findViewById(R.id.tv_ruim);
        }
    }
}
