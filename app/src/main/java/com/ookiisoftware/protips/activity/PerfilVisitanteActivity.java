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

import java.util.ArrayList;

public class PerfilVisitanteActivity extends AppCompatActivity {

    //region Variáveis
    private static final String TAG = "PerfilVisitanteActivity";

    private RecyclerView esportesRecycler;
    private TextAdapter mercadosAdapter;

    private ArrayList<String> mercados;
    private ArrayList<Esporte> esportes;

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
        esportesRecycler = findViewById(R.id.esportes);
        ImageView foto = findViewById(R.id.iv_foto);
        EditText nome = findViewById(R.id.nome);
        EditText email = findViewById(R.id.et_email);
        EditText tipName = findViewById(R.id.tipname);
        RecyclerView mercadosRecycler = findViewById(R.id.mercados);
        LinearLayout email_container = findViewById(R.id.email_container);
        LinearLayout tipster_container = findViewById(R.id.tipster_container);
        LinearLayout telefone_container = findViewById(R.id.telefone_container);
        Button btn_voltar = findViewById(R.id.cancelar);
        final Button btn_2 = findViewById(R.id.btn_2);
        final Button btn_seguir = findViewById(R.id.salvar);
        final EditText telefone = findViewById(R.id.telefone);
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

        mercados = new ArrayList<>();
        esportes = new ArrayList<>();
        nome.setEnabled(false);
        email.setEnabled(false);
        tipName.setEnabled(false);
        telefone.setEnabled(false);

        if (userIsTipster) {
            esportes.addAll(tipster.getEsportes());
            mercadosAdapter = new TextAdapter(activity, mercados, false);
            mercadosRecycler.setAdapter(mercadosAdapter);
            TextAdapter esportesAdapter = new TextAdapter(activity, esportes) {
                @Override
                public void onClick(View v) {
                    int itemPosition = esportesRecycler.getChildAdapterPosition(v);
                    Esporte currentEsporte = esportes.get(itemPosition);
                    mercados.clear();
                    mercados.addAll(currentEsporte.getMercados());
                    mercadosAdapter.notifyDataSetChanged();
                }
            };
            esportesRecycler.setAdapter(esportesAdapter);
            tipster_container.setVisibility(View.VISIBLE);
            if (Import.getFirebase.isTipster())
                btn_seguir.setVisibility(View.GONE);
            else
                btn_seguir.setVisibility(View.VISIBLE);
            if (tipster.getPuntersPendentes().contains(Import.getFirebase.getId())) {
                btn_seguir.setText(getResources().getString(R.string.pendente));
                acao = Tipster.Acao.Remover_Pendente;
            } else if (tipster.getPunters().contains(Import.getFirebase.getId())) {
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
                if (eu.getPuntersPendentes().contains(usuario.getId())) {
                    btn_seguir.setVisibility(View.VISIBLE);
                    btn_2.setVisibility(View.VISIBLE);
                    btn_seguir.setText(getResources().getString(R.string.aceitar));
                    btn_2.setText(getResources().getString(R.string.recusar));
                    acao = Tipster.Acao.Aceitar;
                } else if (eu.getPunters().contains(usuario.getId())) {
                    btn_seguir.setVisibility(View.VISIBLE);
                    btn_seguir.setText(getResources().getString(R.string.remover));
                    acao = Tipster.Acao.Desseguir;
                } else {
                    btn_seguir.setVisibility(View.GONE);
                }
            }
        }

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
            telefone_container.setVisibility(View.VISIBLE);
        }

        //endregion

        //region setListener

        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Import.getFirebase.getTipster().solicitar(punter, Tipster.Acao.Remover_Pendente);
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
                        tipster.solicitar(Import.getFirebase.getPunter(), acao);
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
                        Import.getFirebase.getTipster().solicitar(punter, acao);
                        switch (acao) {
                            case Aceitar:
                                btn_seguir.setText(getResources().getString(R.string.remover));
                                acao = Tipster.Acao.Desseguir;
                                btn_2.setVisibility(View.GONE);
                                break;
                            case Desseguir:
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
