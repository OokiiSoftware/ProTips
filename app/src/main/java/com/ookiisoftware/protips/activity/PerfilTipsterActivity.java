package com.ookiisoftware.protips.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.adapter.CustomViewPager;
import com.ookiisoftware.protips.adapter.SectionsPagerAdapter;
import com.ookiisoftware.protips.auxiliar.Const;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.fragment.PostPerfilFragment;
import com.ookiisoftware.protips.modelo.PostPerfil;
import com.ookiisoftware.protips.modelo.User;
import com.ookiisoftware.protips.modelo.UserDados;

import java.util.ArrayList;
import java.util.Collections;

public class PerfilTipsterActivity extends AppCompatActivity {

    //region Variáveis
    private static final String TAG = "PerfilTipsterActivity";

    public SwipeRefreshLayout refreshLayout;
    public CustomViewPager viewPager;
    private PostPerfilFragment postPerfilFragment1;
    private PostPerfilFragment postPerfilFragment2;

    private SectionsPagerAdapter pagerAdapter;
    private PerfilTipsterActivity activity;
    private ArrayList<PostPerfil> data;
    private User user;
    private UserDados userDados;
    private String meuId;
    private int acao;
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
        final ImageView foto = findViewById(R.id.iv_foto);
        final TextView nome = findViewById(R.id.tv_nome);
        final TextView email = findViewById(R.id.tv_email);
        final TextView tipName = findViewById(R.id.tv_tipname);
        final TextView telefone = findViewById(R.id.tv_telefone);
        final TextView seguidores = findViewById(R.id.tv_seguidores);
        final TextView info = findViewById(R.id.tv_info);
        final TextView btn_seguir = findViewById(R.id.tv_seguir);
        final TextView btn_recusar = findViewById(R.id.tv_recusar);
        final TextView btn_remover = findViewById(R.id.tv_remover);
        final TextView btn_aceitar = findViewById(R.id.tv_aceitar);
        TabLayout tabs = findViewById(R.id.tabs);

        refreshLayout = findViewById(R.id.swipeRefresh);
        viewPager = findViewById(R.id.viewPager);
        //endregion

        meuId = Import.getFirebase.getId();

        if (user == null) {
            //region Bundle
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                String userId = bundle.getString(Const.intent.USER_ID, null);
                isGerencia = bundle.getBoolean(Const.intent.IS_GERENCIA);
                if (userId == null) {
                    Import.Alert.toast(activity, getResources().getString(R.string.erro_generico));
                    Import.Alert.e(TAG, "Init", "idUser == null");
                    onBackPressed();
                    return;
                }

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
        }

        final User eu = Import.getFirebase.getTipster();

        //region setValues

        nome.setText(userDados.getNome());
        email.setText(userDados.getEmail());
        tipName.setText(userDados.getTipname());
        telefone.setText(userDados.getTelefone());
        String seguidoresS = getResources().getString(R.string.filiados) + ": " + user.getSeguidores().size();
        seguidores.setText(seguidoresS);
        Glide.with(activity).load(userDados.getFoto()).into(foto);

        if (userDados.getInfo() == null || userDados.getInfo().isEmpty())
            info.setVisibility(View.GONE);
        else
            info.setText(userDados.getInfo());

        btn_remover.setVisibility(View.GONE);
        btn_aceitar.setVisibility(View.GONE);
        btn_recusar.setVisibility(View.GONE);
        btn_seguir.setVisibility(View.GONE);

        if (isGerencia) {
            if (userDados.isBloqueado()) {
                btn_aceitar.setVisibility(View.VISIBLE);
                btn_recusar.setVisibility(View.VISIBLE);
            } else {
                btn_remover.setVisibility(View.VISIBLE);
            }
        } else {
            if (user.getSeguidoresPendentes().containsValue(meuId)) {
                btn_seguir.setVisibility(View.VISIBLE);
                btn_seguir.setText(getResources().getString(R.string.pendente));
                acao = R.string.pendente;
            } else if (eu.getSeguindo().containsKey(user.getDados().getId())) {
                btn_remover.setVisibility(View.VISIBLE);
                btn_seguir.setVisibility(View.GONE);
            } else {
                btn_seguir.setVisibility(View.VISIBLE);
                btn_seguir.setText(getResources().getString(R.string.seguir));
                acao = R.string.seguir;
            }

            if (eu.getSeguidoresPendentes().containsValue(user.getDados().getId())) {
                btn_aceitar.setVisibility(View.VISIBLE);
                btn_recusar.setVisibility(View.VISIBLE);
            }

            if (userDados.isPrivado()) {
                email.setVisibility(View.GONE);
                telefone.setVisibility(View.GONE);
            } else {
                email.setVisibility(View.VISIBLE);
                if (userDados.getTelefone() == null || userDados.getTelefone().isEmpty())
                    telefone.setVisibility(View.GONE);
                else
                    telefone.setVisibility(View.VISIBLE);
            }
        }

        if (data == null) {
            data = new ArrayList<>(user.getPost_perfil().values());
            Collections.sort(data, new PostPerfil.orderByDate());
            if (postPerfilFragment1 == null)
                postPerfilFragment1 = new PostPerfilFragment(activity, data, false);
            if (postPerfilFragment2 == null)
                postPerfilFragment2 = new PostPerfilFragment(activity, data, true);
        } else {
            data.clear();
            data.addAll(user.getPost_perfil().values());
            Collections.sort(data, new PostPerfil.orderByDate());
            postPerfilFragment1.adapterUpdate();
            postPerfilFragment2.adapterUpdate();
        }

        //region viewPager
        if (pagerAdapter == null) {
            pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), activity, postPerfilFragment1, postPerfilFragment2);
            viewPager.setAdapter(pagerAdapter);
        }
        //endregion

        //region Tabs
        tabs.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorAccent));
        tabs.setupWithViewPager(viewPager);

        TabLayout.Tab tab1 = tabs.getTabAt(0);
        TabLayout.Tab tab2 = tabs.getTabAt(1);
        if (tab1 != null)
            tab1.setIcon(getResources().getDrawable(R.drawable.ic_view_module));
        if (tab2 != null)
            tab2.setIcon(getResources().getDrawable(R.drawable.ic_list));
        //endregion

        //endregion

        //region setListener

        btn_recusar.setOnClickListener(v -> {
            if (isGerencia) {
                user.solicitarSerTipsterCancelar();
            } else {
                eu.removerSolicitacao(user.getDados().getId());
                Import.get.solicitacao.remove(user.getDados().getId());

                btn_recusar.setVisibility(View.GONE);
            }
            btn_recusar.setVisibility(View.GONE);
            btn_aceitar.setVisibility(View.GONE);
        });
        btn_aceitar.setOnClickListener(view -> {
            try {
                if (isGerencia) {
                    user.solicitarSerTipsterCancelar();
                    user.habilitarTipster(true);
                } else {
                    eu.aceitarSeguidor(activity, user);
                }
                btn_recusar.setVisibility(View.GONE);
                btn_aceitar.setVisibility(View.GONE);
            } catch (Exception e) {
                Import.Alert.e(TAG, "btn_aceitar.setOnClickListener", e);
            }
        });

        btn_remover.setOnClickListener(view -> {
            try {
                if (isGerencia) {
                    user.bloquear();
                } else {
                    user.removerSeguidor(eu);
                    btn_seguir.setVisibility(View.VISIBLE);
                    acao = R.string.seguir;
                }
                btn_remover.setVisibility(View.GONE);
            } catch (Exception e) {
                Import.Alert.e(TAG, "btn_seguir.setOnClickListener", e);
            }
        });
        btn_seguir.setOnClickListener(view -> {
            try {
                switch (acao) {
                    case R.string.seguir: {
                        btn_seguir.setText(getResources().getString(R.string.pendente));
                        user.addSolicitacao(activity, eu);
                        acao = R.string.pendente;
                        break;
                    }
                    case R.string.pendente: {
                        user.removerSolicitacao(meuId);
                        btn_seguir.setText(getResources().getString(R.string.seguir));
                        acao = R.string.seguir;
                        break;
                    }
                }
            } catch (Exception e) {
                Import.Alert.e(TAG, "btn_seguir.setOnClickListener", e);
            }
        });

        refreshLayout.setOnRefreshListener(this::updateDados);

        //endregion
    }

    private void updateDados() {
        Import.getFirebase.getReference()
                .child(Const.firebase.child.USUARIO)
                .child(user.getDados().getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User item = dataSnapshot.getValue(User.class);
                        if (item == null)
                            return;

                        String id = item.getDados().getId();
                        Import.get.tipsters.add(item);

                        if (!isGerencia) {
                            if (Import.getFirebase.getTipster().getSeguidores().containsValue(id)) {
                                Import.get.seguidores.add(item);
                            }

                            if (Import.getFirebase.getTipster().getSeguindo().containsValue(id)) {
                                Import.get.seguindo.add(item);
                            }
                        }

                        user = item;
                        refreshLayout.setRefreshing(false);
                        init();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    //endregion

}
