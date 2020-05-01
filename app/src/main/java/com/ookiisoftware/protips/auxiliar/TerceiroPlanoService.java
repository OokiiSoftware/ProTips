package com.ookiisoftware.protips.auxiliar;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.activity.GerenciaActivity;
import com.ookiisoftware.protips.activity.MainActivity;
import com.ookiisoftware.protips.modelo.Post;
import com.ookiisoftware.protips.modelo.Punter;
import com.ookiisoftware.protips.modelo.Tipster;
import com.ookiisoftware.protips.modelo.Usuario;

import java.util.List;

public class TerceiroPlanoService extends Service {

    private static final String TAG = "TerceiroPlanoService";
    private Context getContext() {
        return this;
    }

    //region Overrides

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            CommandSolicitacoes();
        } catch (Exception e) {
            Import.Alert.erro(TAG, e);
        }

        // START_STICKY serve para executar seu serviço até que você pare ele, é reiniciado automaticamente sempre que termina
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //endregion

    //region Command

    // Quando o usuario solicita para ser um tipster
    private void CommandSolicitacoes() {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.SOLICITACAO_NOVO_TIPSTER)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        try {
                            String uid = dataSnapshot.getKey();
                            if (uid != null)
                                getTipster(uid);
//                            Tipster item = dataSnapshot.getValue(Tipster.class);
//                            if (item != null) {
//                                Tipster item_2 = Import.get.tipsters.findTipster(item.getDados().getId());
//                                if (item_2 == null) {
//                                    Import.get.tipsters.getAll().add(item);
//                                    Import.get.tipsters.getAllAux().add(item);
//                                } else {
//                                    Import.get.tipsters.getAll().set(Import.get.tipsters.getAll().indexOf(item_2), item);
//                                    Import.get.tipsters.getAllAux().set(Import.get.tipsters.getAllAux().indexOf(item_2), item);
//                                }
//                            }
                        } catch (Exception ex) {
                            Import.Alert.erro(TAG, ex);
                        }
                        try {
                            Import.activites.getGerenciaActivity().solicitacoesFragment.adapterUpdate();
                            Import.activites.getGerenciaActivity().solicitacoesFragment.refreshLayout.setRefreshing(false);
                        } catch (Exception ignored) {}
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getKey();
                        Import.get.tipsters.getAll().remove(Import.get.tipsters.findTipster(value));
                        Import.get.tipsters.getAllAux().remove(Import.get.tipsters.findTipster(value));
                        Import.Alert.msg(TAG, "onChildRemoved", value);
                        Import.activites.getGerenciaActivity().solicitacoesFragment.adapterUpdate();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    private void getTipster(String id) {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.TIPSTERS)
                .child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Tipster item = dataSnapshot.getValue(Tipster.class);
                        if (item != null) {
                            Tipster item_2 = Import.get.tipsters.findTipster(item.getDados().getId());
                            if (item_2 == null) {
                                Import.get.tipsters.getAll().add(item);
                                Import.get.tipsters.getAllAux().add(item);
                            } else {
                                Import.get.tipsters.getAll().set(Import.get.tipsters.getAll().indexOf(item_2), item);
                                Import.get.tipsters.getAllAux().set(Import.get.tipsters.getAllAux().indexOf(item_2), item);
                            }
                            Import.activites.getGerenciaActivity().solicitacoesFragment.adapterUpdate();
                            notificationSolicitacao(item.getDados());
                        }
                        Import.activites.getGerenciaActivity().solicitacoesFragment.refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    //endregion

    //region notifications

    private void notificationSolicitacao(@NonNull Usuario usuario) {
        String titulo = getResources().getString(R.string.app_name);
        String texto = getResources().getString(R.string.nova_solicitação_tipster) + "\n" + usuario.getNome();

        Intent intent = new Intent(getContext(), GerenciaActivity.class);
        intent.putExtra(Constantes.intent.PAGE_SELECT, 1);
        Import.notificacao(getContext(), intent, titulo, texto);
    }

    //endregion

}
