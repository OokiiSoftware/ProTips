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
import com.ookiisoftware.protips.activity.MainActivity;
import com.ookiisoftware.protips.modelo.Post;
import com.ookiisoftware.protips.modelo.Punter;
import com.ookiisoftware.protips.modelo.Tipster;
import com.ookiisoftware.protips.modelo.Usuario;

import java.util.List;

public class SegundoPlanoService extends Service {

    private static final String TAG = "SegundoPlanoService";
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
            if (Import.getFirebase.isTipster()) {
                CommandTipsterPunter();
                CommandTipsterPunterPendente();
            } else {
                CommandPostes();
                CommandPunterPendenteAceito();
            }
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

    // Aguarda novos Postes
    private void CommandPostes() {
        for (String id : Import.getFirebase.getPunter().getTipsters().values()) {
            postAddChildEventList(id);
        }
    }

    // Quando o punter solicita para ser punter de um tipster
    private void CommandTipsterPunterPendente() {
        final String meu_id = Import.getFirebase.getId();
        final DatabaseReference ref = Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.TIPSTERS)
                .child(meu_id)
                .child(Constantes.firebase.child.PUNTERS_PENDENTES);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getValue(String.class);
                if (key == null)
                    return;

                Import.Alert.msg(TAG, "CommandTipsterPunterPendente", "onChildAdded", key);
                Import.getFirebase.getTipster().getPuntersPendentes().put(key, key);

                getPunter(key);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getValue(String.class);
                if (key == null)
                    return;
                Import.Alert.msg(TAG, "CommandTipsterPunterPendente", "onChildRemoved", key);
                Import.getFirebase.getTipster().getPuntersPendentes().remove(key);

                Punter item = Import.get.tipsters.findPuntersPendentes(key);
                if (item != null) {
                    Import.get.tipsters.getPuntersPendentes().remove(item);
                    try {
                        Import.activites.getMainActivity().notificationsFragment.adapterUpdate();
                        Import.activites.getMainActivity().perfilFragment.updateNotificacao();
                    } catch (Exception ignored) {}
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void getPunter(String key) {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.PUNTERS)
                .child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Punter item = dataSnapshot.getValue(Punter.class);
                        if (item != null) {
                            Punter item2 = Import.get.tipsters.findPuntersPendentes(item.getDados().getId());
                            if (item2 == null) {
                                Import.get.tipsters.getPuntersPendentes().add(item);
                            } else {
                                Import.get.tipsters.getPuntersPendentes().set(Import.get.tipsters.getPuntersPendentes().indexOf(item2), item);
                            }
                            try {
                                notificationPunterPendente(item.getDados());
                                Import.activites.getMainActivity().notificationsFragment.adapterUpdate();
                                Import.activites.getMainActivity().perfilFragment.updateNotificacao();
                            } catch (Exception ignored) {}
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    // Quando um Punter é adicionado na lista de 'punters' do Tipster
    private void CommandTipsterPunter() {
        final String meu_id = Import.getFirebase.getId();
        final DatabaseReference ref = Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.TIPSTERS)
                .child(meu_id)
                .child(Constantes.firebase.child.PUNTERS);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String key = dataSnapshot.getValue(String.class);
                if (key == null)
                    return;

                DatabaseReference ref =  Import.getFirebase.getReference()
                        .child(Constantes.firebase.child.USUARIO)
                        .child(Constantes.firebase.child.PUNTERS)
                        .child(key);

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Punter item = dataSnapshot.getValue(Punter.class);
                        if (item != null) {
                            if (item.getDados().isBloqueado())
                                return;

                            Import.getFirebase.getTipster().getPuntersPendentes().remove(key);
                            Punter item2 = Import.get.tipsters.findPuntersPendentes(item.getDados().getId());
                            if (item2 != null) {
                                Import.get.tipsters.getPuntersPendentes().remove(item2);
                            }

                            item.addTipster(meu_id);
                            item2 = Import.get.punter.find(item.getDados().getId());
                            if (item2 == null) {
                                Import.get.punter.getAll().add(item);
                                Import.get.punter.getAllAux().add(item);
                            } else {
                                Import.get.punter.getAll().set(Import.get.punter.getAll().indexOf(item2), item);
                                Import.get.punter.getAllAux().set(Import.get.punter.getAllAux().indexOf(item2), item);
                            }

                            try {
                                Import.activites.getMainActivity().notificationsFragment.adapterUpdate();
                                Import.activites.getMainActivity().tipstersFragment.adapterUpdate();
                                Import.activites.getMainActivity().perfilFragment.updateNotificacao();
                            } catch (Exception ignored) {}
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getValue(String.class);
                if (key == null)
                    return;

                Punter item = Import.get.punter.find(key);
                if (item != null) {
                    Import.get.punter.getAll().remove(item);
                    Import.get.punter.getAllAux().remove(item);

                    item.removerTipster(meu_id);
                }

                try {
                    Import.activites.getMainActivity().tipstersFragment.adapterUpdate();
                } catch (Exception ignored) {}
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    // Quando o tipster aceita a solicitação do punter
    private void CommandPunterPendenteAceito() {
        final String meu_id = Import.getFirebase.getId();
        final DatabaseReference ref = Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.PUNTERS)
                .child(meu_id)
                .child(Constantes.firebase.child.TIPSTERS);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String key = dataSnapshot.getValue(String.class);
                if (key == null)
                    return;

                DatabaseReference ref =  Import.getFirebase.getReference()
                        .child(Constantes.firebase.child.USUARIO)
                        .child(Constantes.firebase.child.TIPSTERS)
                        .child(key);

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Tipster item = dataSnapshot.getValue(Tipster.class);
                        if (item != null) {
                            Tipster item2 = Import.get.tipsters.findTipster(item.getDados().getId());
                            if (item2 == null) {
                                Import.get.tipsters.getAll().add(item);
                                Import.get.tipsters.getAllAux().add(item);
                                Import.get.tipsters.postes().addAll(item.getPostes().values());
                                postAddChildEventList(item.getDados().getId());
                                notificationPunterAceito(item.getDados());
                            } else {
                                Import.get.tipsters.getAll().set(Import.get.tipsters.getAll().indexOf(item2), item);
                                Import.get.tipsters.getAllAux().set(Import.get.tipsters.getAllAux().indexOf(item2), item);
                                for (Post p : item.getPostes().values())
                                    if (Import.get.tipsters.findPost(p.getId()) == null)
                                        Import.get.tipsters.postes().add(p);
                            }
                            try {
                                Import.activites.getMainActivity().feedFragment.adapterUpdate();
                            } catch (Exception ignored) {}
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getValue(String.class);
                if (key == null)
                    return;

                Punter item = Import.get.punter.find(key);
                if (item != null) {
                    Import.get.punter.getAll().remove(item);
                    Import.get.punter.getAllAux().remove(item);

                    item.removerTipster(meu_id);
                }

                try {
                    Import.activites.getMainActivity().tipstersFragment.adapterUpdate();
                } catch (Exception ignored) {}
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    /*private void CommandConversas(){
        final String usuarioLogadoId = Import.getFirebase.getId();
        final DatabaseReference fbRefConversas = Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(usuarioLogadoId)
                .child(Constantes.firebase.child.CONVERSAS);

        ValueEventListener valueEventListenerConversas = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final StringBuilder textoNotificacao = new StringBuilder();
                final StringBuilder tituloNotificacao = new StringBuilder();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (data.getKey() != null){
                        //Na hierarquia = root > usuarios > id_usuario_logado > conversas > id_conversa
                        final DatabaseReference refTemp = fbRefConversas.child(data.getKey());

                        refTemp.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot data2 : dataSnapshot.getChildren()) {
                                    Mensagem mensagem = data2.getValue(Mensagem.class);

                                    if (mensagem != null) {
                                        String nome;
                                        {
                                            String id_conversa = Criptografia.descriptografar(mensagem.getId_conversa());
                                            // Procurar o nome do remetente nos contatos, se não tiver coloco o TipName
//                                            SQLiteContato dbContato = new SQLiteContato(getApplicationContext());
                                            Contato contato = dbContato.get(id_conversa);
                                            if (contato == null)
                                                nome = id_conversa;
                                            else
                                                nome = contato.getNome();
                                        }

                                        if (Import.SalvarMensagemNoDispositivo(getApplicationContext(), mensagem)) {
                                            Conversa conversa = new Conversa();
                                            conversa.setId(mensagem.getId_conversa());
                                            conversa.setNome_contato(nome);
                                            conversa.setUltima_msg(mensagem.getMensagem());
                                            conversa.setData(mensagem.getData_de_envio());

                                            {
                                                boolean mostrarNotificacao = false;
                                                boolean marcarMsgComoLido = false;

                                                if (mensagem.getId_remetente().equals(usuarioLogadoId)) {
                                                    marcarMsgComoLido = true;// se eu que enviei então marca como lida no meu dispositivo
                                                } else if (Import.getUsuario.getUsuarioConversa() == null) {
                                                    mostrarNotificacao = true;
                                                } else if (Import.getUsuario.getUsuarioConversa().getId().equals(conversa.getId()))
                                                    marcarMsgComoLido = true;// se eu estou conversando com a pessoa então marca como lida no meu dispositivo
//
                                                if (marcarMsgComoLido)
                                                    conversa.setLido(Constantes.CONVERSA_MENSAGEM_LIDA);
                                                else
                                                    conversa.setLido(Constantes.CONVERSA_MENSAGEM_ENVIADA);

                                                if (mostrarNotificacao) {

                                                    String msg = Criptografia.descriptografar(mensagem.getMensagem());
                                                    tituloNotificacao.append(nome).append(",");
                                                    textoNotificacao.append(nome).append(": ").append(msg).append("\n");
                                                    CriarNotificacaoDaMensagem(mensagem.getId_conversa(), tituloNotificacao.toString(), textoNotificacao.toString());
                                                }

                                            }// Verificar quem mandou a mensagem e se a conversa está aberta com o remetente

                                            Import.SalvarConversaNoDispositivo(getApplicationContext(), conversa);
                                            Import.RemoverMensagemDoFirebase(refTemp, mensagem);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
                        refTemp.removeEventListener(this);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        fbRefConversas.addValueEventListener(valueEventListenerConversas);
    }*/

    //endregion

    //region notifications

    private void notificationPunterPendente(@NonNull Usuario usuario) {
        String titulo = getResources().getString(R.string.app_name);
        String texto = getResources().getString(R.string.nova_solicitação_punter) + "\n" + usuario.getNome();

        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra(Constantes.intent.PAGE_SELECT, 3);
        Import.notificacao(getContext(), intent, titulo, texto);
    }

    private void notificationPunterAceito(@NonNull Usuario usuario) {
        String titulo = getResources().getString(R.string.app_name);
        String texto = getResources().getString(R.string.punter_aceito) + "\n" + getResources().getString(R.string.tipster) + ": "
                + usuario.getNome();

        Intent intent = new Intent(getContext(), MainActivity.class);
        Import.notificacao(getContext(), intent, titulo, texto);
    }

    private void notificationNewPost(@NonNull Tipster usuario, @NonNull Post post) {
        String titulo = getResources().getString(R.string.app_name);
        String texto = getResources().getString(R.string.novo_poste) + ": " + usuario.getDados().getNome();
        texto += "\n" + getResources().getString(R.string.esporte) + ": " + post.getEsporte();
        texto += "\n" + getResources().getString(R.string.mercado) + ": " + post.getMercado();
        texto += "\n" + getResources().getString(R.string.odd) + ": " + post.getOdd_minima() + " - " + post.getOdd_maxima();
        texto += "\n" + getResources().getString(R.string.horario) + ": " + post.getHorario_minimo() + " - " + post.getHorario_maximo();

        Intent intent = new Intent(getContext(), MainActivity.class);
        Import.notificacao(getContext(), intent, titulo, texto);
    }

    /*private void CriarNotificacaoDaMensagem(String id_conversa, String titulo, String texto) {
        id_conversa = Criptografia.descriptografar(id_conversa);

        Intent intent;
        String[] tituloAux = titulo.split(",");
        String tituloReal;

        if(tituloAux.length == 1) {
            tituloReal = tituloAux[0];
            intent = new Intent(getApplicationContext(), ConversaActivity.class);
            intent.putExtra(Constantes.CONVERSA_CONTATO_ID, id_conversa);
            intent.putExtra(Constantes.CONVERSA_CONTATO_NOME, tituloReal);
        } else {
            tituloReal = "ProTips";
            intent = new Intent(getApplicationContext(), BatepapoActivity.class);
        }

        Import.Notificacao(getContext(), intent, tituloReal, texto);
    }*/

    //endregion

    private void postAddChildEventList(String id_tipster) {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.TIPSTERS)
                .child(id_tipster)
                .child(Constantes.firebase.child.POSTES)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Post item = dataSnapshot.getValue(Post.class);
                        if (item == null)
                            return;

                        Post item2 = Import.get.tipsters.findPost(item.getId());
                        if (item2 == null) {
                            Import.get.tipsters.postes().add(item);
                            Tipster t = Import.get.tipsters.findTipster(item.getId_tipster());
                            if (t != null) {
                                boolean mostrarNotification = true;
                                try {
                                    if (Import.activites.getMainActivity().viewPager.getCurrentItem() == 0
                                        && verifyApplicationRunning(getContext())
                                    && Import.activites.getMainActivity().isInPrimeiroPlano()) {
                                        mostrarNotification = false;
                                        Import.activites.getMainActivity().feedFragment.haveNewPostes(true);
                                    }
                                }catch (Exception e) {
                                    Import.Alert.erro(TAG, e);
                                }
                                if (mostrarNotification)
                                    notificationNewPost(t, item);
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        Post item = dataSnapshot.getValue(Post.class);
                        if (item == null)
                            return;
                        Post item2 = Import.get.tipsters.findPost(item.getId());
                        if (item2 != null) {
                            Import.get.tipsters.postes().remove(item2);
                            try{
                                Import.activites.getMainActivity().feedFragment.adapterUpdate();
                            } catch (Exception ignored) {}
                        }
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    private boolean verifyApplicationRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos;
        if (activityManager != null) {
            procInfos = activityManager.getRunningAppProcesses();
            for (int i = 0; i < procInfos.size(); i++) {
                if (procInfos.get(i).processName.equals("com.ookiisoftware.protips")) {
                    onDestroy();
                    return true;
                }
            }
        }
        return false;
    }

}
