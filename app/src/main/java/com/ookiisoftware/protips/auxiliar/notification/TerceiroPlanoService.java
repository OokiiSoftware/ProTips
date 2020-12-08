package com.ookiisoftware.protips.auxiliar.notification;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.activity.GerenciaActivity;
import com.ookiisoftware.protips.auxiliar.Const;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.User;
import com.ookiisoftware.protips.modelo.UserDados;

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
            Import.Alert.e(TAG, "onStartCommand", e);
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
                .child(Const.firebase.child.SOLICITACAO_NOVO_TIPSTER)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        try {
                            String key = dataSnapshot.getKey();
                            getTipster(key, true);
                        } catch (Exception ex) {
                            Import.Alert.e(TAG, "CommandSolicitacoes: onChildAdded", ex);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            String key = dataSnapshot.getKey();
                            Import.get.solicitacao.remove(key);
                            Import.activites.getGerenciaActivity().notificationsFragment.adapterUpdate();
                        } catch (Exception ex) {
                            Import.Alert.e(TAG, "CommandSolicitacoes: onChildRemoved", ex);
                        }
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    private void getTipster(String id, boolean seguidorPendente) {
        Import.getFirebase.getReference()
                .child(Const.firebase.child.USUARIO)
                .child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User item = dataSnapshot.getValue(User.class);
                        if (item == null)
                            return;

                        if (item.getDados().isTipster()) {
                            Import.get.tipsters.add(item);
                        }

                        if (seguidorPendente) {
                            Import.get.solicitacao.add(item);
                            notificationSolicitacao(item.getDados());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    //endregion

    //region notifications

    private void notificationSolicitacao(@NonNull UserDados userDados) {
        try {
            boolean inPrimeiroPlano = Import.activites.getMainActivity().isInPrimeiroPlano();
            if (Import.activites.getMainActivity().getPagePosition() != Const.classes.fragments.pagerPosition.TIPSTER_SOLICITACAO) {
                String titulo = getResources().getString(R.string.app_name);
                String texto = getResources().getString(R.string.nova_solicitação_tipster) + "\n" + userDados.getNome();

                Intent intent = null;
                if (!inPrimeiroPlano) {
                    intent = new Intent(getContext(), GerenciaActivity.class);
                    intent.putExtra(Const.intent.PAGE_SELECT, Const.classes.fragments.pagerPosition.TIPSTER_SOLICITACAO);
                }
//                int channelId = Constantes.notification.id.TIPSTER_SOLICITACAO;
//                MyNotificationManager.getInstance(getContext()).create(titulo, texto, intent);
            } else {
                Import.activites.getGerenciaActivity().notificationsFragment.adapterUpdate();
                Import.activites.getGerenciaActivity().notificationsFragment.refreshLayout.setRefreshing(false);
            }
        } catch (Exception ignored) {}
    }

    //endregion

}
