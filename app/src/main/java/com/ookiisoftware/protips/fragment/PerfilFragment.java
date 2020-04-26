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

public class PerfilFragment extends Fragment {

    //region Variáveis
//    private static final String TAG = "PerfilFragment";

    private Activity activity;
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

        ImageView newPost = view.findViewById(R.id.iv_new_post);
        ImageView newPunter = view.findViewById(R.id.iv_new_punter);
        //endregion

        boolean isTipster = Import.getFirebase.isTipster();
        FirebaseUser user = Import.getFirebase.getAuth().getCurrentUser();

        //region setValues
        String uri = "";
        if (user != null) {
            txt_nome.setText(user.getDisplayName());
            txt_email.setText(user.getEmail());
            if (user.getPhotoUrl() != null) {
                uri = user.getPhotoUrl().toString();
            }
        }
        if (isTipster) {
            txt_tipname.setText(Import.getFirebase.getTipster().getDados().getTipname());
        } else {
            txt_tipname.setText(Import.getFirebase.getPunter().getDados().getTipname());
        }
        newPost.setEnabled(isTipster);

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
            newPunter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Import.activites.getMainActivity().viewPager.setCurrentItem(3);
            }
        });

        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, PostActivity.class);
                startActivity(intent);
            }
        });
        //endregion
    }

    //endregion
}