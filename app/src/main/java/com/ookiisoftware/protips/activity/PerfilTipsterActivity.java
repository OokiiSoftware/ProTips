package com.ookiisoftware.protips.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.adapter.PostPerfilAdapter;
import com.ookiisoftware.protips.adapter.TextAdapter;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.auxiliar.OnSwipeListener;
import com.ookiisoftware.protips.modelo.Esporte;
import com.ookiisoftware.protips.modelo.PostPerfil;
import com.ookiisoftware.protips.modelo.Punter;
import com.ookiisoftware.protips.modelo.Tipster;
import com.ookiisoftware.protips.modelo.Usuario;

import java.util.ArrayList;
import java.util.HashMap;

public class PerfilTipsterActivity extends AppCompatActivity {

    //region Variáveis
    private static final String TAG = "PerfilTipsterActivity";

    //    private TextAdapter mercadosAdapter;
    private TextAdapter esportesAdapter;

    private HashMap<String, String> mercados;
    private HashMap<String, Esporte> esportes;

    private RecyclerView recyclerPosts;

    private ArrayList<PostPerfil> postPerfils;
    private Dialog dialog;
    private OnSwipeListener onSwipeListener = new OnSwipeListener() {
        @Override
        public void onTouchUp() {
            if (dialog != null)
                if (dialog.isShowing())
                    dialog.dismiss();
        }
    };

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

    @SuppressLint("ClickableViewAccessibility")
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
        recyclerPosts = findViewById(R.id.recyclerPosts);
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

        postPerfils = new ArrayList<>(tipster.getPost_perfil().values());
        ArrayList<PostPerfil> data = postPerfils;
        PostPerfilAdapter perfilAdapter = new PostPerfilAdapter(activity, data, true, onSwipeListener) {
            @Override
            public void onClick(View v) {

            }

            @Override
            public boolean onLongClick(View v) {
                int position = recyclerPosts.getChildAdapterPosition(v);
                PostPerfil item = postPerfils.get(position);
                popupPhoto(item.getFoto());
                return super.onLongClick(v);
            }
        };
        recyclerPosts.setAdapter(perfilAdapter);
        recyclerPosts.setOnTouchListener(onSwipeListener);

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

        btn_recusar.setOnClickListener(v -> {
            if (isGerencia) {
                tipster.solicitarSerTipsterCancelar();
                tipster.getDados().toPunter().desbloquear();
            }
            btn_seguir.setVisibility(View.GONE);
            btn_recusar.setVisibility(View.GONE);
        });
        btn_seguir.setOnClickListener(view -> {
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
        });
        btn_voltar.setOnClickListener(view -> onBackPressed());

        //endregion

    }

    private void popupPhoto(String uri) {
        if (uri == null || uri.isEmpty())
            return;
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_foto);
        dialog.setOnDismissListener(dialog -> {
            recyclerPosts.suppressLayout(false);
            Import.activites.getMainActivity().viewPager.setPagingEnabled(true);
        });
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        recyclerPosts.suppressLayout(true);
        Import.activites.getMainActivity().viewPager.setPagingEnabled(false);

        ImageView foto = dialog.findViewById(R.id.iv_foto);
        foto.setVisibility(View.VISIBLE);
        Glide.with(activity).load(uri).into(foto);
        foto.requestLayout();
    }

    //endregion

}
