package com.ookiisoftware.protips.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.activity.PostActivity;
import com.ookiisoftware.protips.activity.PerfilActivity;
import com.ookiisoftware.protips.activity.SeguidoresActivity;
import com.ookiisoftware.protips.activity.SeguindoActivity;
import com.ookiisoftware.protips.adapter.PostPerfilAdapter;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Criptografia;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.auxiliar.OnSwipeListener;
import com.ookiisoftware.protips.modelo.PostPerfil;
import com.ookiisoftware.protips.modelo.Usuario;

import java.util.ArrayList;
import java.util.Collections;

import static android.app.Activity.RESULT_OK;

public class PerfilFragment extends Fragment {

    //region Variáveis
//    private static final String TAG = "PerfilFragment";

    private Activity activity;
    private OnSwipeListener onSwipeListener = new OnSwipeListener() {
        @Override
        public void onTouchUp() {
            if (dialog != null)
                if (dialog.isShowing())
                    dialog.dismiss();
        }
    };

    private String foto_path;

    private PostPerfilAdapter perfilAdapter;
    private ArrayList<PostPerfil> postPerfils;
    private boolean isTipster;

    private Dialog dialog;
    private ImageView btn_notificacao;
    private TextView notification_quant;
    private RecyclerView recyclerView;

    //endregion

    public PerfilFragment(){}
    public PerfilFragment(Activity activity) {
        this.activity = activity;
    }

    //region Overrides

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        Init(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNotificacao();
        adapterUpdate();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constantes.REQUEST_PERMISSION_STORANGE && resultCode == RESULT_OK) {
            if (dialog != null) {
                if (data == null || data.getData() == null) {
                    dialog.dismiss();
                } else {
                    Uri uri = data.getData();
                    foto_path = uri.toString();
                    ImageView foto = dialog.findViewById(R.id.iv_foto);
                    Glide.with(activity).load(uri).into(foto);
                }
            }
        } else {
            if (dialog != null)
                dialog.dismiss();
        }
    }

    //endregion

    //region Métodos

    @SuppressLint("ClickableViewAccessibility")
    private void Init(View view) {
        //region findViewById
        ImageView foto_user = view.findViewById(R.id.iv_foto);
        TextView txt_tipname = view.findViewById(R.id.tv_tipname);
        TextView txt_nome = view.findViewById(R.id.tv_nome);
        TextView txt_email = view.findViewById(R.id.tv_email);
        TextView posts_no_perfil = view.findViewById(R.id.tv_posts_no_perfil);
        TextView btn_seguidores = view.findViewById(R.id.tv_seguidores);

        ImageView btn_newPost = view.findViewById(R.id.iv_new_post);
        ImageView btn_newPostPerfil = view.findViewById(R.id.iv_new_post_perfil);
        ImageView btn_planilha = view.findViewById(R.id.iv_planilha);
        ImageView btn_3_perfil_fragment = view.findViewById(R.id.btn_3_perfil_fragment);

        recyclerView = view.findViewById(R.id.recycler);
        btn_notificacao = view.findViewById(R.id.iv_new_punter);
        notification_quant = view.findViewById(R.id.tv_notification_quant);
        //endregion

        //region setValues

        isTipster = Import.getFirebase.isTipster();
        Usuario user = Import.getFirebase.getTipster().getDados();

        if (user == null)
            return;

        txt_nome.setText(user.getNome());
        txt_email.setText(user.getEmail());
        txt_tipname.setText(user.getTipname());
        Glide.with(activity).load(user.getFoto()).into(foto_user);

        if (isTipster) {
            postPerfils = new ArrayList<>(Import.getFirebase.getTipster().getPost_perfil().values());
            Collections.sort(postPerfils, new PostPerfil.orderByDate());
            perfilAdapter = new PostPerfilAdapter(activity, postPerfils, true, onSwipeListener) {
                @Override
                public boolean onLongClick(View v) {
                    int position = recyclerView.getChildAdapterPosition(v);
                    PostPerfil item = postPerfils.get(position);
                    popupPhoto(item.getFoto());
                    recyclerView.suppressLayout(true);
                    Import.activites.getMainActivity().setPagingEnabled(false);
                    return super.onLongClick(v);
                }
            };
            recyclerView.setAdapter(perfilAdapter);
            recyclerView.setOnTouchListener(onSwipeListener);

            btn_newPostPerfil.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            btn_seguidores.setText(activity.getResources().getString(R.string.titulo_meus_punters));
            posts_no_perfil.setVisibility(View.VISIBLE);
        } else {
            posts_no_perfil.setVisibility(View.GONE);
            btn_seguidores.setText(activity.getResources().getString(R.string.titulo_meus_tipsters));
            btn_newPost.setVisibility(View.GONE);
            btn_planilha.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            btn_newPostPerfil.setVisibility(View.GONE);
        }

        //endregion

        //region setListener
        foto_user.setOnClickListener(view1 -> {
            Intent intent = new Intent(activity, PerfilActivity.class);
            startActivity(intent);
        });

        if (isTipster) {
            btn_newPost.setOnClickListener(v -> {
                Intent intent = new Intent(activity, PostActivity.class);
                startActivity(intent);
            });

            btn_newPostPerfil.setOnClickListener(v -> postPerfil());
        }
        btn_notificacao.setOnClickListener(v -> {
            removeNotification();
            Import.activites.getMainActivity().setPagePosition(Constantes.classes.fragments.pagerPosition.NOTIFICATIONS);
        });
        btn_3_perfil_fragment.setOnClickListener(v -> {

        });
        btn_seguidores.setOnClickListener(v -> {
            Intent intent;
            if (isTipster)
                intent = new Intent(activity, SeguidoresActivity.class);
            else
                intent = new Intent(activity, SeguindoActivity.class);
            activity.startActivity(intent);
        });

        //endregion
    }

    public void updateNotificacao() {
        int quant = Import.get.solicitacao.getAll().size();
        if (quant > 0) {
            btn_notificacao.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_sms_2));
            String quantS = "" + quant;
            notification_quant.setText(quantS);
            notification_quant.setVisibility(View.VISIBLE);
        } else {
            notification_quant.setVisibility(View.GONE);
            btn_notificacao.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_sms));
        }
    }

    public void adapterUpdate() {
        if (perfilAdapter != null)
            perfilAdapter.notifyDataSetChanged();
    }

    private void removeNotification() {
        if (isTipster)
            Import.notificacaoCancel(activity, Constantes.notification.id.NOVO_PUNTER_PENDENTE);
        else
            Import.notificacaoCancel(activity, Constantes.notification.id.NOVO_PUNTER_ACEITO);
    }

    private void postPerfil() {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_post_perfil);
        dialog.setCancelable(false);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final EditText titulo = dialog.findViewById(R.id.et_titulo);
        final EditText texto = dialog.findViewById(R.id.et_texto);
        final ImageView foto = dialog.findViewById(R.id.iv_foto);
        final RelativeLayout rl = dialog.findViewById(R.id.rl_1);
        final Button postar = dialog.findViewById(R.id.ok_button);
        final Button cancel = dialog.findViewById(R.id.cancel_button);
        pegarFotoDaGaleria();
        rl.setVisibility(View.VISIBLE);

        foto.setScaleType(ImageView.ScaleType.FIT_CENTER);
        foto.setOnClickListener(v -> pegarFotoDaGaleria());
        cancel.setOnClickListener(v -> dialog.dismiss());
        postar.setOnClickListener(v -> {
            PostPerfil post = new PostPerfil();
            post.setTitulo(titulo.getText().toString());
            post.setTexto(texto.getText().toString());
            post.setData(Import.get.Data());
            post.setId(Criptografia.criptografar(post.getData()));
            post.setId_tipster(Import.getFirebase.getId());
            post.setFoto(foto_path);

            if (post.getFoto() == null || post.getFoto().isEmpty()) {
                return;
            }
            post.salvar(activity, null, true);
            postPerfils.add(post);
            Collections.sort(postPerfils, new PostPerfil.orderByDate());
            adapterUpdate();
            dialog.dismiss();
        });
        dialog.setOnDismissListener(dialog -> foto_path = null);

        dialog.show();
    }

    private void pegarFotoDaGaleria() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, Constantes.REQUEST_PERMISSION_STORANGE);
    }

    private void popupPhoto(String uri) {
        try {
            if (uri == null || uri.isEmpty())
                return;
            dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_foto);
            dialog.setOnDismissListener(dialog -> {
                recyclerView.suppressLayout(false);
                Import.activites.getMainActivity().setPagingEnabled(true);
            });
            if (dialog.getWindow() != null)
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            ImageView foto = dialog.findViewById(R.id.iv_foto);
            foto.setVisibility(View.VISIBLE);
            Glide.with(activity).load(uri).into(foto);
            foto.requestLayout();
        } catch (Exception ignored) {}
    }

    //endregion
}