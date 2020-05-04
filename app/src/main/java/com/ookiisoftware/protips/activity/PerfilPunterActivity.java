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
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Punter;
import com.ookiisoftware.protips.modelo.Tipster;
import com.ookiisoftware.protips.modelo.Usuario;

public class PerfilPunterActivity extends AppCompatActivity {

    //region Variáveis
    private static final String TAG = "PerfilPunterActivity";

//    private TextAdapter mercadosAdapter;
//    private TextAdapter esportesAdapter;

//    private HashMap<String, String> mercados;
//    private HashMap<String, Esporte> esportes;

    private Activity activity;
//    private Tipster tipster;
    private Punter punter;
    private Usuario usuario;
    private int acao;
//    private Tipster.Acao acao;
//    private boolean userIsTipster;
//    private boolean isGerencia;
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
        final Button btn_aceitar = findViewById(R.id.salvar);
        final Button btn_recusar = findViewById(R.id.btn_2);
        //endregion

        //region Bundle
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String idUser = bundle.getString(Constantes.intent.USER_ID, null);
            if (idUser == null) {
                Import.Alert.toast(activity, getResources().getString(R.string.erro_generico));
                Import.Alert.erro(TAG, "Init", "idUser == null");
                onBackPressed();
                return;
            }
            punter = Import.get.punter.find(idUser);
            if (punter == null)
                punter = Import.get.tipsters.findPuntersPendentes(idUser);
            if (punter != null)
                usuario = punter.getDados();
        }

        if (usuario == null) {
            Import.Alert.toast(activity, getResources().getString(R.string.erro_generico));
            Import.Alert.erro(TAG, "Init", "usuario == null");
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

        tipName.setText(usuario.getTipname());
        nome.setText(usuario.getNome());
        email.setText(usuario.getEmail());
        telefone.setText(usuario.getTelefone());
        Glide.with(activity).load(usuario.getFoto()).into(foto);

        if (usuario.getInfo() == null || usuario.getInfo().isEmpty())
            info.setVisibility(View.GONE);
        else
            info.setText(usuario.getInfo());

        if (usuario.isPrivado()) {
            email_container.setVisibility(View.GONE);
            telefone_container.setVisibility(View.GONE);
        } else {
            email_container.setVisibility(View.VISIBLE);
            if (usuario.getTelefone() == null || usuario.getTelefone().isEmpty())
                telefone_container.setVisibility(View.GONE);
            else
                telefone_container.setVisibility(View.VISIBLE);
        }

        final Tipster eu = Import.getFirebase.getTipster();
        if (Import.getFirebase.isTipster()) {
            if (eu.getPuntersPendentes().containsValue(usuario.getId())) {
                btn_aceitar.setVisibility(View.VISIBLE);
                btn_recusar.setVisibility(View.VISIBLE);
                btn_aceitar.setText(getResources().getString(R.string.aceitar));
                btn_recusar.setText(getResources().getString(R.string.recusar));
                acao = R.string.aceitar;
            } else if (eu.getPunters().containsKey(usuario.getId())) {
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
            Import.getFirebase.getTipster().removerSolicitacao(punter.getDados().getId());
            Import.get.tipsters.getPuntersPendentes().remove(punter);
            btn_aceitar.setVisibility(View.GONE);
            btn_recusar.setVisibility(View.GONE);
        });
        btn_aceitar.setOnClickListener(view -> {
            try {
                switch (acao) {
                    case R.string.aceitar: {
                        btn_aceitar.setText(getResources().getString(R.string.remover));
                        eu.addPunter(punter);
                        acao = R.string.remover;
                        break;
                    }
                    case R.string.remover: {
                        btn_aceitar.setVisibility(View.GONE);
                        btn_recusar.setVisibility(View.GONE);
                        eu.removerPunter(punter);
                    }
                }
            } catch (Exception e) {
                Import.Alert.erro(TAG, "btn_aceitar.setOnClickListener", e);
            }
        });
        btn_voltar.setOnClickListener(view -> onBackPressed());

        //endregion

    }

    //endregion

}
