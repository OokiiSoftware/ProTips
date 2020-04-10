package com.ookiisoftware.protips.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.activity.UserEditActivity;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;

import java.util.Objects;

public class PerfilFragment extends Fragment {

//    private static final String TAG = "PerfilFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        Init(view);
        return view;
    }

    private void Init(View view) {
        // Elementos do layout
        ImageView img_foto_usuario = view.findViewById(R.id.perfil_usuario_img_foto);
        TextView txt_tipname = view.findViewById(R.id.perfil_usuario_txt_tipname);
        TextView txt_nome = view.findViewById(R.id.perfil_usuario_txt_nome);
        TextView txt_email = view.findViewById(R.id.perfil_usuario_txt_email);
        LinearLayout btm_edit = view.findViewById(R.id.perfil_btn_edit);

        FirebaseUser user = Import.getFirebase.getAuth().getCurrentUser();

        // Exibir os dados na tela
        String uri = "";
        if (user != null) {
            txt_nome.setText(user.getDisplayName());
            txt_email.setText(user.getEmail());
            if (user.getPhotoUrl() != null) {
                uri = user.getPhotoUrl().toString();
            }
        }
        txt_tipname.setText(Import.getFirebase.getId());

        Glide.with(Objects.requireNonNull(getActivity())).load(uri).into(img_foto_usuario);

        // Ação de click dos botões
        btm_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UserEditActivity.class);
                intent.putExtra(Constantes.FRAGMENT, Constantes.FRAGMENT_EDIT);
                startActivity(intent);
            }
        });
    }
}