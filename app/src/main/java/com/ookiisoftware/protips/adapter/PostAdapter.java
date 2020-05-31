package com.ookiisoftware.protips.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
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
    private final static String TAG = "PostAdapter";
    private ConstraintSet set = new ConstraintSet();
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
        holder.foto_post.setId(position);

        holder.data.setText(_data);
        holder.mercado.setText(item.getLinha());
        holder.titulo.setText(item.getTitulo());
        holder.esporte.setText(item.getEsporte());
        holder.horario_min.setText(item.getHorario_minimo());
        holder.horario_max.setText(item.getHorario_maximo());

        //region ifs
        if ((item.getOdd_minima() == null || item.getOdd_minima().isEmpty()) &&
                (item.getOdd_maxima() == null || item.getOdd_maxima().isEmpty())) {
            holder.container_odd.setVisibility(View.GONE);
        } else {
            holder.odd_min.setText(item.getOdd_minima());
            holder.odd_max.setText(item.getOdd_maxima());
        }
        if ((item.getOdd_atual() == null || item.getOdd_atual().isEmpty()) &&
                (item.getUnidade() == null || item.getUnidade().isEmpty())) {
            holder.container_odd_atual.setVisibility(View.GONE);
        } else {
            holder.odd_atual.setText(item.getOdd_atual());
            holder.unidade.setText(item.getUnidade());
        }
        if (item.getCampeonato() == null || item.getCampeonato().isEmpty()) {
            holder.campeonato.setVisibility(View.GONE);
        } else {
            holder.campeonato.setText(item.getCampeonato());
        }
        if (item.getDescricao() == null || item.getDescricao().isEmpty()) {
            holder.descricao.setVisibility(View.GONE);
        } else {
            holder.descricao.setText(item.getDescricao());
        }
        //endregion

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
            Glide.with(activity).asBitmap().load(foto_post).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    String ratio = String.format("%s:%s", resource.getWidth(), resource.getHeight());
                    set.clone(holder.constraintLayout);
                    set.setDimensionRatio(holder.foto_post.getId(), ratio);
                    set.applyTo(holder.constraintLayout);

                    holder.foto_post.setImageBitmap(resource);
                }
            });

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
                    dialog.setTitle(activity.getResources().getString(R.string.post_excluir));
                    dialog.setMessage(activity.getResources().getString(R.string.deseja_prosseguir));
                    dialog.setPositiveButton(activity.getResources().getString(R.string.sim), (dialog1, which) -> {
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
        ConstraintLayout constraintLayout;
        TableRow container_odd;
        TableRow container_horario;
        TableRow container_odd_atual;
        ImageView foto_user;
        ImageViewTouch foto_post;
        LinearLayout green_red;
        Toolbar toolbar;
        TextView titulo;
        TextView esporte;
        TextView mercado;
        TextView odd_min;
        TextView odd_max;
        TextView odd_atual;
        TextView unidade;
        TextView campeonato;
        TextView horario_min;
        TextView horario_max;
        TextView descricao, data, userName, bom, ruim;

        Holder(@NonNull View itemView) {
            super(itemView);
            container_odd = itemView.findViewById(R.id.container_odd);
            container_horario = itemView.findViewById(R.id.container_horario);
            container_odd_atual = itemView.findViewById(R.id.container_odd_atual);
            constraintLayout = itemView.findViewById(R.id.constraint);

            toolbar = itemView.findViewById(R.id.toolbar);
            titulo = itemView.findViewById(R.id.tv_titulo);
            esporte = itemView.findViewById(R.id.tv_esporte);
            mercado = itemView.findViewById(R.id.tv_mercado);
            odd_min = itemView.findViewById(R.id.tv_odd_min);
            odd_max = itemView.findViewById(R.id.tv_odd_max);
            unidade = itemView.findViewById(R.id.tv_unidade);
            campeonato = itemView.findViewById(R.id.tv_campeonato);
            odd_atual = itemView.findViewById(R.id.tv_odd_atual);
            horario_max = itemView.findViewById(R.id.tv_horario_max);
            horario_min = itemView.findViewById(R.id.tv_horario_min);

            foto_user = itemView.findViewById(R.id.iv_foto);
            foto_post = itemView.findViewById(R.id.iv_foto_post);
            green_red = itemView.findViewById(R.id.tipster_container);
            descricao = itemView.findViewById(R.id.tv_texto);
            data = itemView.findViewById(R.id.tv_data);
            userName = itemView.findViewById(R.id.tv_tipster);
            bom = itemView.findViewById(R.id.tv_bom);
            ruim = itemView.findViewById(R.id.tv_ruim);
        }
    }
}
