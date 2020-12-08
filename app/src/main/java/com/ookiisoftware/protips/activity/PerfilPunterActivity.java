package com.ookiisoftware.protips.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.Const;
import com.ookiisoftware.protips.auxiliar.Import;
//import com.ookiisoftware.protips.modelo.Punter;
import com.ookiisoftware.protips.modelo.User;
import com.ookiisoftware.protips.modelo.UserDados;

public class PerfilPunterActivity extends AppCompatActivity {

    //region Variáveis
    private static final String TAG = "PerfilPunterActivity";

    private Activity activity;
    private User user;
    private UserDados userDados;
    private int acao;
    //endregion

    //region Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_punter);
        activity = this;
        init();
    }

    //endregion

    //region Métodos

    private void init() {
        //region findViewById
        final ImageView foto = findViewById(R.id.iv_foto);
        final EditText nome = findViewById(R.id.et_nome);
        final EditText email = findViewById(R.id.et_email);
        final EditText tipName = findViewById(R.id.et_tipname);
        final EditText telefone = findViewById(R.id.et_telefone);
        final EditText info = findViewById(R.id.et_info);
        final LinearLayout email_container = findViewById(R.id.ll_email_container);
        final LinearLayout telefone_container = findViewById(R.id.ll_telefone_container);
        final Button btn_voltar = findViewById(R.id.cancelar);
        final Button btn_aceitar = findViewById(R.id.tv_salvar);
        final Button btn_recusar = findViewById(R.id.btn_2);
        //endregion

        //region Bundle
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String userId = bundle.getString(Const.intent.USER_ID, null);
            if (userId == null) {
                Import.Alert.toast(activity, getResources().getString(R.string.erro_generico));
                Import.Alert.e(TAG, "Init", "idUser == null");
                onBackPressed();
                return;
            }

            user = Import.get.seguidores.get(userId);
            if (user == null)
                user = Import.get.seguindo.get(userId);
            if (user == null)
                user = Import.get.solicitacao.get(userId);
            if (user == null)
                user = Import.get.tipsters.get(userId);
            if (user != null)
                userDados = user.getDados();
        }

        if (userDados == null) {
            Import.Alert.toast(activity, getResources().getString(R.string.erro_generico));
            Import.Alert.e(TAG, "Init", "usuario == null");
            onBackPressed();
            return;
        }
        //endregion

        //region setValues

        info.setEnabled(false);
        nome.setEnabled(false);
        email.setEnabled(false);
        tipName.setEnabled(false);
        telefone.setEnabled(false);

        tipName.setText(userDados.getTipname());
        nome.setText(userDados.getNome());
        email.setText(userDados.getEmail());
        telefone.setText(userDados.getTelefone());
        Glide.with(activity).load(userDados.getFoto()).into(foto);

        if (userDados.getInfo() == null || userDados.getInfo().isEmpty())
            info.setVisibility(View.GONE);
        else
            info.setText(userDados.getInfo());

        if (userDados.isPrivado()) {
            email_container.setVisibility(View.GONE);
            telefone_container.setVisibility(View.GONE);
        } else {
            email_container.setVisibility(View.VISIBLE);
            if (userDados.getTelefone() == null || userDados.getTelefone().isEmpty())
                telefone_container.setVisibility(View.GONE);
            else
                telefone_container.setVisibility(View.VISIBLE);
        }

        final User eu = Import.getFirebase.getTipster();
        if (Import.getFirebase.isTipster()) {
            if (eu.getSeguidoresPendentes().containsValue(userDados.getId())) {
                btn_aceitar.setVisibility(View.VISIBLE);
                btn_recusar.setVisibility(View.VISIBLE);
                btn_aceitar.setText(getResources().getString(R.string.aceitar));
                btn_recusar.setText(getResources().getString(R.string.recusar));
                acao = R.string.aceitar;
            } else if (eu.getSeguidores().containsKey(userDados.getId())) {
                btn_aceitar.setVisibility(View.VISIBLE);
                btn_aceitar.setText(getResources().getString(R.string.remover));
                acao = R.string.remover;
            } else {
                btn_aceitar.setVisibility(View.GONE);
                btn_recusar.setVisibility(View.GONE);
            }
        }

        //endregion

        //region setListener

        btn_recusar.setOnClickListener(v -> {
            Import.getFirebase.getTipster().removerSolicitacao(user.getDados().getId());
            Import.get.solicitacao.remove(user.getDados().getId());
            btn_aceitar.setVisibility(View.GONE);
            btn_recusar.setVisibility(View.GONE);
        });
        btn_aceitar.setOnClickListener(view -> {
            try {
                switch (acao) {
                    case R.string.aceitar: {
                        btn_aceitar.setText(getResources().getString(R.string.remover));
                        eu.aceitarSeguidor(activity, user);
                        btn_recusar.setVisibility(View.GONE);
                        acao = R.string.remover;
                        break;
                    }
                    case R.string.remover: {
                        btn_aceitar.setVisibility(View.GONE);
                        btn_recusar.setVisibility(View.GONE);
                        eu.removerSeguidor(user);
                    }
                }
            } catch (Exception e) {
                Import.Alert.e(TAG, "btn_aceitar.setOnClickListener", e);
            }
        });
        btn_voltar.setOnClickListener(view -> onBackPressed());

        //endregion

    }

    //endregion

}
