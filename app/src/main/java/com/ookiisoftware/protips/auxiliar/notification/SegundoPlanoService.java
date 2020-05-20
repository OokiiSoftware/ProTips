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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.activity.MainActivity;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Post;
import com.ookiisoftware.protips.modelo.User;
import com.ookiisoftware.protips.modelo.Usuario;

public class SegundoPlanoService extends Service {

    private static final String TAG = "SegundoPlanoService";
    private static final int PENDENTE = 345;
    private static final int SEGUINDO = 656;
    private static final int SEGUIDORES = 234;

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
            CommandTipsterPunter();
            CommandTipsterPunterPendente();
            CommandPunterPendenteAceito();
            CommandPostes();
        } catch (Exception e) {
            Import.Alert.erro(TAG, e);
        }

        // START_STICKY serve para executar seu serviço até que você pare ele, é reiniciado automaticamente sempre que termina
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Import.Alert.msg(TAG, "onDestroy", "----");
    }

    //endregion

    //region Commands

    // Aguarda novos Postes
    private void CommandPostes() {
        for (String id : Import.getFirebase.getTipster().getSeguindo().values()) {
            postAddChildEventList(id);
        }
    }

    // Quando o punter solicita para ser punter de um tipster
    private void CommandTipsterPunterPendente() {
        final String meu_id = Import.getFirebase.getId();
        final DatabaseReference ref = Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(meu_id)
                .child(Constantes.firebase.child.SEGUIDORES_PENDENTES);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getValue(String.class);
                if (key == null)
                    return;

                boolean b = !Import.getFirebase.getTipster().getSeguidoresPendentes().containsKey(key);
                getSeguidorPendente(key, b);
                Import.getFirebase.getTipster().getSeguidoresPendentes().put(key, key);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getValue(String.class);
                if (key == null)
                    return;

                Import.getFirebase.getTipster().getSeguidoresPendentes().remove(key);

                Import.get.solicitacao.remove(key);
                try {
                    Import.activites.getMainActivity().notificationsFragment.adapterUpdate();
                    Import.activites.getMainActivity().perfilFragment.updateNotificacao();
                } catch (Exception ignored) {}
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    // Quando um Punter é adicionado na lista de 'punters' do Tipster
    private void CommandTipsterPunter() {
        final String meu_id = Import.getFirebase.getId();
        final DatabaseReference ref = Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(meu_id)
                .child(Constantes.firebase.child.SEGUIDORES);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String key = dataSnapshot.getValue(String.class);
                if (key == null)
                    return;

                boolean b = !Import.getFirebase.getTipster().getSeguidores().containsKey(key);
                getSeguidor(key, b);
                Import.getFirebase.getTipster().getSeguidores().put(key, key);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getValue(String.class);
                Import.getFirebase.getTipster().getSeguidores().remove(key);
                Import.get.seguidores.remove(key);
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
                .child(meu_id)
                .child(Constantes.firebase.child.SEGUINDO);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String key = dataSnapshot.getValue(String.class);
                if (key == null)
                    return;

                boolean b = !Import.getFirebase.getTipster().getSeguindo().containsKey(key);
                getSeguindo(key, b);
                Import.getFirebase.getTipster().getSeguindo().put(key, key);
                postAddChildEventList(key);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getValue(String.class);
                if (key == null)
                    return;

                Import.get.seguindo.remove(key);
                Import.getFirebase.getTipster().getSeguindo().remove(key);
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
        try {
            boolean inPrimeiroPlano = Import.activites.getMainActivity().isInPrimeiroPlano();
            switch (Import.activites.getMainActivity().getPagePosition()) {
                case Constantes.classes.fragments.pagerPosition.NOTIFICATIONS:
                    Import.activites.getMainActivity().notificationsFragment.adapterUpdate();
                    break;
                case Constantes.classes.fragments.pagerPosition.PERFIL:
                    Import.activites.getMainActivity().perfilFragment.updateNotificacao();
                    break;
                default: {
                    String titulo = getResources().getString(R.string.app_name);
                    String texto = getResources().getString(R.string.nova_solicitação_filiado) + "\n" + usuario.getNome();

                    Intent intent = null;
                    if (!inPrimeiroPlano) {
                        intent = new Intent(getContext(), MainActivity.class);
                        intent.putExtra(Constantes.intent.PAGE_SELECT, Constantes.classes.fragments.pagerPosition.NOTIFICATIONS);
                    }
//                    int channelId = Constantes.notification.id.NOVO_PUNTER_PENDENTE;
                    MyNotificationManager.getInstance(getContext()).create(titulo, texto, intent);
                    break;
                }
            }
        } catch (Exception ignored) {}
    }

    private void notificationPunterAceito(@NonNull Usuario usuario) {
        try {
            boolean inPrimeiroPlano = Import.activites.getMainActivity().isInPrimeiroPlano();
            switch (Import.activites.getMainActivity().getPagePosition()) {
                case Constantes.classes.fragments.pagerPosition.PERFIL:
                    Import.activites.getMainActivity().perfilFragment.updateNotificacao();
                    break;
                case Constantes.classes.fragments.pagerPosition.FEED:
                default: {
                    String titulo = getResources().getString(R.string.app_name);
                    String texto = getResources().getString(R.string.filiado_aceito) + "\n" + getResources().getString(R.string.tipster) + ": " + usuario.getNome();

                    Intent intent = null;
                    if (!inPrimeiroPlano)
                        intent = new Intent(getContext(), MainActivity.class);
//                    int channelId = Constantes.notification.id.NOVO_PUNTER_ACEITO;
                    MyNotificationManager.getInstance(getContext()).create(titulo, texto, intent);
                    break;
                }
            }
        } catch (Exception ignored) {}
    }

    private void notificationNewPost(@NonNull User usuario, @NonNull Post post) {
        try {
            boolean inPrimeiroPlano = Import.activites.getMainActivity().isInPrimeiroPlano();
            if (Import.activites.getMainActivity().getPagePosition() == Constantes.classes.fragments.pagerPosition.FEED &&
                    inPrimeiroPlano) {
                Import.activites.getMainActivity().feedFragment.haveNewPostes(true);
            } else  {
                String titulo = getResources().getString(R.string.app_name);
                String texto = getResources().getString(R.string.novo_poste) + ": " + usuario.getDados().getNome();
                texto += "\n" + getResources().getString(R.string.esporte) + ": " + post.getEsporte();
                texto += "\n" + getResources().getString(R.string.mercado) + ": " + post.getMercado();
                texto += "\n" + getResources().getString(R.string.odd) + ": " + post.getOdd_minima() + " - " + post.getOdd_maxima();
                texto += "\n" + getResources().getString(R.string.horario) + ": " + post.getHorario_minimo() + " - " + post.getHorario_maximo();

                Intent intent = null;
                if (!inPrimeiroPlano)
                    intent = new Intent(getContext(), MainActivity.class);
//                int channelId = Constantes.notification.id.NOVO_POST;
                MyNotificationManager.getInstance(getContext()).create(titulo, texto, intent);
            }
        } catch (Exception ignored) {}
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

    //region gets

    private void getTipster(String key, int notificationID, boolean notificationVisible) {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User item = dataSnapshot.getValue(User.class);
                        if (item == null)
                            return;

                        String id = item.getDados().getId();

                        if (item.getDados().isTipster()) {
                            Import.get.tipsters.add(item);
                        }

                        if (Import.getFirebase.getTipster().getSeguidores().containsValue(id)) {
                            Import.get.seguidores.add(item);
                        }

                        if (Import.getFirebase.getTipster().getSeguindo().containsValue(id)) {
                            Import.get.seguindo.add(item);
                        }

                        if (notificationVisible) {
                            switch (notificationID) {
                                case SEGUIDORES:
                                    break;
                                case SEGUINDO:
                                    notificationPunterAceito(item.getDados());
                                    break;
                                case PENDENTE:
                                    Import.get.solicitacao.add(item);
                                    notificationPunterPendente(item.getDados());
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    private void getSeguidor(String key, boolean notificationVisible) {
        User item = Import.get.tipsters.get(key);
        if (item == null) {
            getTipster(key, SEGUIDORES, notificationVisible);
        } else {
            if (notificationVisible)
                notificationPunterAceito(item.getDados());
            Import.get.seguidores.add(item);
        }
    }

    private void getSeguindo(String key, boolean notificationVisible) {
        User item = Import.get.tipsters.get(key);
        if (item == null) {
            getTipster(key, SEGUIDORES, notificationVisible);
        } else {
            if (notificationVisible)
                notificationPunterAceito(item.getDados());
            Import.get.seguindo.add(item);
        }
    }

    private void getSeguidorPendente(String id, boolean notificationVisible) {
        User item = Import.get.tipsters.get(id);
        if (item == null) {
            getTipster(id, PENDENTE, notificationVisible);
        } else {
            if (notificationVisible)
                notificationPunterPendente(item.getDados());
            Import.get.solicitacao.add(item);
        }
    }

    private void postAddChildEventList(String userId) {
        DatabaseReference ref =  Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(userId)
                .child(Constantes.firebase.child.POSTES);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Post item = dataSnapshot.getValue(Post.class);
                if (item == null)
                    return;

                Import.get.seguindo.add(item);
                User user = Import.get.tipsters.get(item.getId_tipster());
                if (user != null) {
                    notificationNewPost(user, item);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Post item = dataSnapshot.getValue(Post.class);
                if (item == null)
                    return;

                Import.get.seguindo.remove(item);

                try {
                    Import.activites.getMainActivity().feedFragment.adapterUpdate();
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    /*private boolean verifyApplicationRunning(@NonNull Context context) {
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
    }*/

    //endregion

}
