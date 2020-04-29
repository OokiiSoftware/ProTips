package com.ookiisoftware.protips.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.activity.PostActivity;
import com.ookiisoftware.protips.activity.PerfilActivity;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Usuario;

public class PerfilFragment extends Fragment {

    //region Variáveis
//    private static final String TAG = "PerfilFragment";

    private Activity activity;
    private ImageView btn_notificacao;

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

    //endregion

    //region Métodos

    private void Init(View view) {
        //region findViewById
        ImageView img_foto_usuario = view.findViewById(R.id.iv_foto);
        TextView txt_tipname = view.findViewById(R.id.tv_tipname);
        TextView txt_nome = view.findViewById(R.id.tv_nome);
        TextView txt_email = view.findViewById(R.id.tv_email);
        TextView btm_edit = view.findViewById(R.id.tv_edit);

        ImageView btn_newPost = view.findViewById(R.id.iv_new_post);
        ImageView btn_planilha = view.findViewById(R.id.iv_planilha);
        ImageView btn_3_perfil_fragment = view.findViewById(R.id.btn_3_perfil_fragment);
        btn_notificacao = view.findViewById(R.id.iv_new_punter);
        //endregion

        boolean isTipster = Import.getFirebase.isTipster();

        Usuario user;
        if (isTipster)
            user = Import.getFirebase.getTipster().getDados();
        else
            user = Import.getFirebase.getPunter().getDados();

        if (user == null)
            return;

        //region setValues

        txt_nome.setText(user.getNome());
        txt_email.setText(user.getEmail());
        txt_tipname.setText(user.getTipname());
        String uri = user.getFoto();

        boolean b = isTipster && !user.isBloqueado();
        btn_newPost.setEnabled(b);
        btn_planilha.setEnabled(b);
        if (!isTipster) {
            btn_newPost.setVisibility(View.GONE);
            btn_planilha.setVisibility(View.GONE);
            btn_notificacao.setVisibility(View.GONE);
            btn_3_perfil_fragment.setVisibility(View.GONE);
        }

        Glide.with(activity).load(uri).into(img_foto_usuario);

        //endregion

        //region setListener
        btm_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, PerfilActivity.class);
                intent.putExtra(Constantes.FRAGMENT, Constantes.FRAGMENT_EDIT);
                startActivity(intent);
            }
        });

        if (isTipster)
            btn_notificacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Import.activites.getMainActivity().viewPager.setCurrentItem(3);
            }
        });

        btn_newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, PostActivity.class);
                startActivity(intent);
            }
        });
        //endregion
    }

    public void updateNotificacao() {
        if (Import.get.tipsters.getPuntersPendentes().size() > 0)
            btn_notificacao.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_sms_2));
        else
            btn_notificacao.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_sms));
    }

    //endregion
}