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
import com.ookiisoftware.protips.adapter.PostPerfilAdapter;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Criptografia;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.auxiliar.OnSwipeListener;
import com.ookiisoftware.protips.modelo.PostPerfil;
import com.ookiisoftware.protips.modelo.Usuario;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class PerfilFragment extends Fragment {

    //region Variáveis
//    private static final String TAG = "PerfilFragment";

    private Activity activity;
    private ImageView btn_notificacao;
    private TextView notification_quant;
    private RecyclerView recyclerView;

    private Dialog dialog;
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
        if(requestCode == Constantes.REQUEST_PERMISSION_STORANGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            if (dialog != null) {
                foto_path = uri.toString();
                ImageView foto = dialog.findViewById(R.id.iv_foto);
                Glide.with(activity).load(uri).into(foto);
            }
        }
    }

    //endregion

    //region Métodos

    @SuppressLint("ClickableViewAccessibility")
    private void Init(View view) {
        //region findViewById
        ImageView img_foto_usuario = view.findViewById(R.id.iv_foto);
        TextView txt_tipname = view.findViewById(R.id.tv_tipname);
        TextView txt_nome = view.findViewById(R.id.tv_nome);
        TextView txt_email = view.findViewById(R.id.tv_email);
//        TextView btm_edit = view.findViewById(R.id.tv_edit);
        TextView text = view.findViewById(R.id.text);
        TextView btn_seguidores = view.findViewById(R.id.tv_seguidores);
        notification_quant = view.findViewById(R.id.tv_notification_quant);

        ImageView btn_newPost = view.findViewById(R.id.iv_new_post);
        ImageView btn_newPostPerfil = view.findViewById(R.id.iv_new_post_perfil);
        ImageView btn_planilha = view.findViewById(R.id.iv_planilha);
        ImageView btn_3_perfil_fragment = view.findViewById(R.id.btn_3_perfil_fragment);
        recyclerView = view.findViewById(R.id.recycler);
        btn_notificacao = view.findViewById(R.id.iv_new_punter);
        //endregion

        //region setValues

        isTipster = Import.getFirebase.isTipster();

        Usuario user;
        if (isTipster) {
            postPerfils = new ArrayList<>(Import.getFirebase.getTipster().getPost_perfil().values());
            ArrayList<PostPerfil> data = postPerfils;
            perfilAdapter = new PostPerfilAdapter(activity, data, true, onSwipeListener) {
                @Override
                public void onClick(View v) {

                }

                @Override
                public boolean onLongClick(View v) {
                    int position = recyclerView.getChildAdapterPosition(v);
                    PostPerfil item = postPerfils.get(position);
                    popupPhoto(item.getFoto());
                    return super.onLongClick(v);
                }
            };
            recyclerView.setAdapter(perfilAdapter);
            recyclerView.setOnTouchListener(onSwipeListener);

            user = Import.getFirebase.getTipster().getDados();
            btn_newPostPerfil.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            btn_seguidores.setText(activity.getResources().getString(R.string.punters));
            text.setVisibility(View.VISIBLE);
        } else {
            text.setVisibility(View.GONE);
            btn_seguidores.setText(activity.getResources().getString(R.string.tipster));
            btn_newPost.setVisibility(View.GONE);
            btn_planilha.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            btn_newPostPerfil.setVisibility(View.GONE);
            user = Import.getFirebase.getPunter().getDados();
        }

        if (user == null)
            return;

        txt_nome.setText(user.getNome());
        txt_email.setText(user.getEmail());
        txt_tipname.setText(user.getTipname());
        String uri = user.getFoto();

        boolean b = isTipster && !user.isBloqueado();
        btn_newPost.setEnabled(b);
        btn_planilha.setEnabled(b);

        Glide.with(activity).load(uri).into(img_foto_usuario);

        //endregion

        //region setListener
        img_foto_usuario.setOnClickListener(view1 -> {
            Intent intent = new Intent(activity, PerfilActivity.class);
            intent.putExtra(Constantes.FRAGMENT, Constantes.FRAGMENT_EDIT);
            startActivity(intent);
        });

        if (isTipster) {
            btn_notificacao.setOnClickListener(v -> {
                removeNotification();
                Import.activites.getMainActivity().viewPager.setCurrentItem(Constantes.classes.fragments.pagerPosition.NOTIFICATIONS);
            });

            btn_newPost.setOnClickListener(v -> {
                Intent intent = new Intent(activity, PostActivity.class);
                startActivity(intent);
            });

            btn_newPostPerfil.setOnClickListener(v -> postPerfil());
        }
        btn_3_perfil_fragment.setOnClickListener(v -> {

        });
        btn_seguidores.setOnClickListener(v -> {
            Intent intent = new Intent(activity, SeguidoresActivity.class);
            activity.startActivity(intent);
        });

        //endregion
    }

    public void updateNotificacao() {
        int quant = Import.get.tipsters.getPuntersPendentes().size();
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
        dialog.setContentView(R.layout.item_post_perfil);
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

        foto.setOnClickListener(v -> pegarFotoDaGaleria());
        cancel.setOnClickListener(v -> dialog.dismiss());
        postar.setOnClickListener(v -> {
            {
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
                adapterUpdate();
                dialog.dismiss();
            }
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
        if (uri == null || uri.isEmpty())
            return;
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_foto);
        dialog.setOnDismissListener(dialog -> {
            recyclerView.suppressLayout(false);
            Import.activites.getMainActivity().viewPager.setPagingEnabled(true);
        });
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        recyclerView.suppressLayout(true);
        Import.activites.getMainActivity().viewPager.setPagingEnabled(false);

        ImageView foto = dialog.findViewById(R.id.iv_foto);
        foto.setVisibility(View.VISIBLE);
        Glide.with(activity).load(uri).into(foto);
        foto.requestLayout();
    }

    //endregion
}