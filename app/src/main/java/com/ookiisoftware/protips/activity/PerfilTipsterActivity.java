package com.ookiisoftware.protips.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.adapter.TextAdapter;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Esporte;
import com.ookiisoftware.protips.modelo.Punter;
import com.ookiisoftware.protips.modelo.Tipster;
import com.ookiisoftware.protips.modelo.Usuario;

import java.util.HashMap;

public class PerfilTipsterActivity extends AppCompatActivity {

    //region Variáveis
    private static final String TAG = "PerfilTipsterActivity";

    //    private TextAdapter mercadosAdapter;
    private TextAdapter esportesAdapter;

    private HashMap<String, String> mercados;
    private HashMap<String, Esporte> esportes;

    private Activity activity;
    private Tipster tipster;
    private Usuario usuario;
//    private Tipster.Acao acao;
    private String meuId;
    private int acao1;
    private boolean isGerencia;
    //endregion

    //region Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_tipster);
        activity = this;
        init();
    }

    //endregion

    //region Métodos

    private void init() {
        //region findViewById
        final RecyclerView esportesRecycler = findViewById(R.id.rv_esportes);
        final ImageView foto = findViewById(R.id.iv_foto);
        final EditText nome = findViewById(R.id.et_nome);
        final EditText email = findViewById(R.id.et_email);
        final EditText tipName = findViewById(R.id.et_tipname);
        final EditText telefone = findViewById(R.id.et_telefone);
        final EditText info = findViewById(R.id.et_info);
        final RecyclerView mercadosRecycler = findViewById(R.id.rv_mercados);
        final LinearLayout email_container = findViewById(R.id.ll_email_container);
        final LinearLayout telefone_container = findViewById(R.id.ll_telefone_container);
        final Button btn_voltar = findViewById(R.id.cancelar);
        final Button btn_seguir = findViewById(R.id.salvar);
        final Button btn_recusar = findViewById(R.id.btn_recusar);
        //endregion

        meuId = Import.getFirebase.getId();
        isGerencia = false;

        //region Bundle
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String idUser = bundle.getString(Constantes.intent.USER_ID, null);
            isGerencia = bundle.getBoolean(Constantes.intent.IS_GERENCIA);
            if (idUser == null) {
                Import.Alert.toast(activity, getResources().getString(R.string.erro_generico));
                Import.Alert.erro(TAG, "Init", "idUser == null");
                onBackPressed();
                return;
            }

            tipster = Import.get.tipsters.findTipster(idUser);

            if (tipster != null)
                usuario = tipster.getDados();
        }

        if (usuario == null) {
            Import.Alert.toast(activity, getResources().getString(R.string.erro_generico));
            Import.Alert.erro(TAG, "Init", "usuario == null");
            onBackPressed();
            return;
        }
        //endregion

        //region setValues

        mercados = new HashMap<>();
        esportes = new HashMap<>();
        info.setEnabled(false);
        nome.setEnabled(false);
        email.setEnabled(false);
        tipName.setEnabled(false);
        telefone.setEnabled(false);

        nome.setText(usuario.getNome());
        email.setText(usuario.getEmail());
        tipName.setText(usuario.getTipname());
        telefone.setText(usuario.getTelefone());
        Glide.with(activity).load(usuario.getFoto()).into(foto);

        if (usuario.getInfo() == null || usuario.getInfo().isEmpty())
            info.setVisibility(View.GONE);
        else
            info.setText(usuario.getInfo());

        esportes.putAll(tipster.getEsportes());
        final TextAdapter mercadosAdapter = new TextAdapter(activity, mercados, false);
        mercadosRecycler.setAdapter(mercadosAdapter);
        esportesAdapter = new TextAdapter(activity, esportes) {
            @Override
            public void onClick(View v) {
                int itemPosition = esportesRecycler.getChildAdapterPosition(v);
                Esporte currentEsporte = esportesAdapter.getEsporte(itemPosition);
                mercados.clear();
                mercados.putAll(currentEsporte.getMercados());
                mercadosAdapter.notifyDataSetChanged();
            }
        };
        esportesRecycler.setAdapter(esportesAdapter);

        if (isGerencia) {
            btn_seguir.setVisibility(View.VISIBLE);
            if (usuario.isBloqueado()) {
                btn_seguir.setText(getResources().getString(R.string.aceitar));
                btn_recusar.setVisibility(View.VISIBLE);
//                acao = Tipster.Acao.Aceitar;
                acao1 = R.string.aceitar;
            } else {
                btn_seguir.setText(getResources().getString(R.string.remover));
//                acao = Tipster.Acao.Remover;
                acao1 = R.string.remover;
            }
        } else {
            if (Import.getFirebase.isTipster())
                btn_seguir.setVisibility(View.GONE);
            else {
                btn_seguir.setVisibility(View.VISIBLE);

                if (tipster.getPuntersPendentes().containsValue(meuId)) {
                    btn_seguir.setText(getResources().getString(R.string.pendente));
//                    acao = Tipster.Acao.Cancelar;
                    acao1 = R.string.pendente;
                } else if (tipster.getPunters().containsKey(meuId)) {
                    btn_seguir.setText(getResources().getString(R.string.remover));
//                    acao = Tipster.Acao.Desseguir;
                    acao1 = R.string.remover;
                } else {
                    btn_seguir.setText(getResources().getString(R.string.seguir));
//                    acao = Tipster.Acao.Seguir;
                    acao1 = R.string.seguir;
                }
            }

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
        }

        //endregion

        //region setListener

        btn_recusar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGerencia) {
                    tipster.solicitarSerTipsterCancelar();
                    tipster.getDados().toPunter().desbloquear();
                }
                btn_seguir.setVisibility(View.GONE);
                btn_recusar.setVisibility(View.GONE);
            }
        });
        btn_seguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (isGerencia) {
                        switch (acao1) {
                            case R.string.remover: {
                                tipster.bloquear();
                                tipster.getDados().toPunter().desbloquear();
                                btn_seguir.setVisibility(View.GONE);
                                break;
                            }
                            case R.string.aceitar: {
//                                acao = Tipster.Acao.Cancelar;

                                tipster.desbloquear();
                                tipster.removerSolicitarSerTipster();

                                btn_seguir.setText(getResources().getString(R.string.remover));
                                btn_recusar.setVisibility(View.GONE);
                                break;
                            }
                        }
                    } else {
                        switch (acao1) {
                            case R.string.seguir: {
                                btn_seguir.setText(getResources().getString(R.string.pendente));
                                tipster.addSolicitacao(meuId);
                                acao1 = R.string.pendente;
                                break;
                            }
                            case R.string.remover: {
                                Punter eu = Import.getFirebase.getPunter();
                                if (eu != null)
                                    tipster.removerPunter(eu);
                                acao1 = R.string.seguir;
                                btn_seguir.setText(getResources().getString(R.string.seguir));
                            }
                            case R.string.pendente: {
                                tipster.removerSolicitacao(meuId);
                                btn_seguir.setText(getResources().getString(R.string.seguir));
                                acao1 = R.string.seguir;
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    Import.Alert.erro(TAG, "btn_seguir.setOnClickListener", e);
                }
            }
        });
        btn_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //endregion

    }

    //endregion

}
