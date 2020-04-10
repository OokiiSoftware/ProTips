package com.ookiisoftware.protips.auxiliar;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.activity.BatepapoActivity;
import com.ookiisoftware.protips.activity.ConversaActivity;
import com.ookiisoftware.protips.modelo.Contato;
import com.ookiisoftware.protips.modelo.Conversa;
import com.ookiisoftware.protips.modelo.Mensagem;

public class SegundoPlanoService extends Service {

    private static final String TAG = "SegundoPlanoService";

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        CommandPostes();
//        CommandConversas();

        // START_STICKY serve para executar seu serviço até que você pare ele, é reiniciado automaticamente sempre que termina
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void CommandConversas(){
        final String usuarioLogadoId = Import.getFirebase.getId();
        final DatabaseReference fbRefConversas = Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(usuarioLogadoId)
                .child(Constantes.firebase.child.CONVERSAS);

        /*ValueEventListener valueEventListenerConversas = new ValueEventListener() {
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
        };*/
//        fbRefConversas.addValueEventListener(valueEventListenerConversas);
    }

    private void CommandPostes() {
        final String usuarioLogadoId = Import.getFirebase.getId();
        //Na hierarquia = root > postes > id_usuario_logado > conversas
        final DatabaseReference reference = Import.getFirebase.getReference()
                .child(Constantes.firebase.child.POSTES);
    }

    private void CriarNotificacaoDaMensagem(String id_conversa, String titulo, String texto) {
        id_conversa = Criptografia.descriptografar(id_conversa);

        Intent intent;
        String[] tituloAux = titulo.split(",");
        String tituloReal;

        if(tituloAux.length == 1) {
            tituloReal = tituloAux[0];
            intent = new Intent(this, ConversaActivity.class);
            intent.putExtra(Constantes.CONVERSA_CONTATO_ID, id_conversa);
            intent.putExtra(Constantes.CONVERSA_CONTATO_NOME, tituloReal);
        } else {
            tituloReal = "ProTips";
            intent = new Intent(getApplicationContext(), BatepapoActivity.class);
        }

        Import.Notificacao(this, intent, R.drawable.ic_icon_notificacao, tituloReal, texto);
    }
}
