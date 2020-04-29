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

public class PerfilVisitanteActivity extends AppCompatActivity {

    //region Variáveis
    private static final String TAG = "PerfilVisitanteActivity";

    private RecyclerView esportesRecycler;
    private TextAdapter mercadosAdapter;
    private TextAdapter esportesAdapter;

    private HashMap<String, String> mercados;
    private HashMap<String, Esporte> esportes;

    private Activity activity;
    private Tipster tipster;
    private Punter punter;
    private Usuario usuario;
    private Tipster.Acao acao;
    private boolean userIsTipster;
    //endregion

    //region Overrides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_visitante);
        activity = this;
        Init();
    }

    //endregion

    //region Métodos

    private void Init() {
        //region findViewById
        esportesRecycler = findViewById(R.id.rv_esportes);
        final ImageView foto = findViewById(R.id.iv_foto);
        final EditText nome = findViewById(R.id.et_nome);
        final EditText email = findViewById(R.id.et_email);
        final EditText tipName = findViewById(R.id.et_tipname);
        final EditText telefone = findViewById(R.id.et_telefone);
        final EditText info = findViewById(R.id.et_info);
        final RecyclerView mercadosRecycler = findViewById(R.id.rv_mercados);
        final LinearLayout email_container = findViewById(R.id.ll_email_container);
        final LinearLayout tipster_container = findViewById(R.id.ll_tipster_container);
        final LinearLayout telefone_container = findViewById(R.id.ll_telefone_container);
        final Button btn_voltar = findViewById(R.id.cancelar);
        final Button btn_seguir = findViewById(R.id.salvar);
        final Button btn_2 = findViewById(R.id.btn_2);
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
            if (punter == null)
                tipster = Import.get.tipsters.findTipster(idUser);
            else
                usuario = punter.getDados();

            if (tipster != null)
                usuario = tipster.getDados();

            userIsTipster = tipster != null;
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

        if (userIsTipster) {
            esportes.putAll(tipster.getEsportes());
            mercadosAdapter = new TextAdapter(activity, mercados, false);
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
            tipster_container.setVisibility(View.VISIBLE);
            if (Import.getFirebase.isTipster())
                btn_seguir.setVisibility(View.GONE);
            else
                btn_seguir.setVisibility(View.VISIBLE);
            if (tipster.getPuntersPendentes().containsValue(Import.getFirebase.getId())) {
                btn_seguir.setText(getResources().getString(R.string.pendente));
                acao = Tipster.Acao.Remover_Pendente;
            } else if (tipster.getPunters().containsKey(Import.getFirebase.getId())) {
                btn_seguir.setText(getResources().getString(R.string.remover));
                acao = Tipster.Acao.Desseguir;
            } else {
                btn_seguir.setText(getResources().getString(R.string.seguir));
                acao = Tipster.Acao.Seguir;
            }
        } else {
            tipster_container.setVisibility(View.GONE);
            if (Import.getFirebase.isTipster() && punter != null) {
                Tipster eu = Import.getFirebase.getTipster();
                if (eu.getPuntersPendentes().containsValue(usuario.getId())) {
                    btn_seguir.setVisibility(View.VISIBLE);
                    btn_2.setVisibility(View.VISIBLE);
                    btn_seguir.setText(getResources().getString(R.string.aceitar));
                    btn_2.setText(getResources().getString(R.string.recusar));
                    acao = Tipster.Acao.Aceitar;
                } else if (eu.getPunters().containsKey(usuario.getId())) {
                    btn_seguir.setVisibility(View.VISIBLE);
                    btn_seguir.setText(getResources().getString(R.string.remover));
                    acao = Tipster.Acao.Desseguir;
                } else {
                    btn_seguir.setVisibility(View.GONE);
                }
            }
        }
        if (usuario.getInfo() == null || usuario.getInfo().isEmpty())
            info.setVisibility(View.GONE);
        else
            info.setText(usuario.getInfo());

        tipName.setText(usuario.getTipname());
        nome.setText(usuario.getNome());
        email.setText(usuario.getEmail());
        telefone.setText(usuario.getTelefone());

        Glide.with(activity).load(usuario.getFoto()).into(foto);
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

        //endregion

        //region setListener

        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Import.getFirebase.getTipster().removerSolicitacao(punter.getDados().getId());
                Import.get.tipsters.getPuntersPendentes().remove(punter);
                btn_seguir.setVisibility(View.GONE);
                btn_2.setVisibility(View.GONE);
            }
        });
        btn_seguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    if (userIsTipster) {
                        tipster.addSolicitacao(Import.getFirebase.getId());
                        switch (acao) {
                            case Seguir:
                                acao = Tipster.Acao.Remover_Pendente;
                                btn_seguir.setText(getResources().getString(R.string.pendente));
                                break;
                            case Desseguir:
                            case Remover_Pendente:
                                acao = Tipster.Acao.Seguir;
                                btn_seguir.setText(getResources().getString(R.string.seguir));
                                break;
                        }
                    } else {
                        Tipster t = Import.getFirebase.getTipster();//.solicitar(punter, acao);
                        switch (acao) {
                            case Aceitar:
                                t.addPunter(punter);
                                btn_seguir.setText(getResources().getString(R.string.remover));
                                acao = Tipster.Acao.Desseguir;
                                btn_2.setVisibility(View.GONE);
                                break;
                            case Desseguir:
                                t.removerPunter(punter);
                                btn_seguir.setVisibility(View.GONE);
                                btn_2.setVisibility(View.GONE);
                                break;
                        }
                    }
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
