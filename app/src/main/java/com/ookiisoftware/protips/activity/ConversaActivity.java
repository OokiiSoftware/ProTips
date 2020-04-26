package com.ookiisoftware.protips.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Criptografia;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Conversa;
import com.ookiisoftware.protips.modelo.Mensagem;
import com.ookiisoftware.protips.modelo.Usuario;
import com.ookiisoftware.protips.sqlite.SQLiteConversa;

import java.util.ArrayList;

public class ConversaActivity extends AppCompatActivity {

    private static final String TAG = "ConversaActivity";
    private RecyclerView recyclerView;
    private EditText txt_caixa_de_texto;
    //==================================== Firebase
    private DatabaseReference fbRefUserDestino;
    private DatabaseReference fbRefMensagens;

    private ValueEventListener valueEventListenerMensagens;
    //======================= Dados dos usuarios da conversa
    private Usuario usuarioDestino = new Usuario();
    private String usuarioLogadoId;
    //======================================================
//    private SingleItemMensagemAdapter adapter;
    private ArrayList<Mensagem> mensagems = new ArrayList<>();

    private boolean ERRO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);
        try {
            Init();
        }catch (Exception e){
            ERRO = true;
//            Alert("Ocorreu um erro ao abrir esta conversa");
            Log.e(TAG, "onCreate" + e.getMessage());
        }
    }

    private void Init() {
        ImageButton btn_enviar;
        //=================Elementos do Layout
        Toolbar toolbar;
        {
            txt_caixa_de_texto = findViewById(R.id.conversa_caixa_de_texto);
            recyclerView = findViewById(R.id.conversa_recyclerView);
            btn_enviar = findViewById(R.id.conversa_tbn_enviar);
            toolbar = findViewById(R.id.conversa_toolbar);
        }// pegar elementos do layout pelo ID

//        usuarioLogadoId = Import.getUsuario.getId(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            usuarioDestino.setId(bundle.getString(Constantes.CONVERSA_CONTATO_ID));
            usuarioDestino.setNome(bundle.getString(Constantes.CONVERSA_CONTATO_NOME));
            usuarioDestino.setFoto(bundle.getString(Constantes.CONVERSA_CONTATO_FOTO));

            usuarioDestino.setId(Criptografia.criptografar(usuarioDestino.getId()));
        }
        else{
            onBackPressed();
            return;
        }

        {
            SQLiteConversa db = new SQLiteConversa(this);
            Conversa conversa = new Conversa();
            conversa.setId(usuarioDestino.getId());
            conversa.setLido(Constantes.CONVERSA_MENSAGEM_LIDA);
//            db.update(this, conversa);

        }// Atualizar o banco de dados ao entrar na conversa, coloca a msg como lida

        /*
         * ID do usuário logado     <-- OK
         * ID do contato da conversa    <-- OK
         */
        PegarReferenciaDaConversa();

        /*
         * NOME do contato da conversa    <-- OK
         */
        {
            toolbar.setTitle(usuarioDestino.getNome());
            toolbar.setNavigationIcon(R.drawable.ic_back);
            setSupportActionBar(toolbar);
        }// toolbar

        /*
         * ID do contato da conversa    <-- OK
         */
        {
//            SQLiteMensagem dbMensagem = new SQLiteMensagem(this);
//            mensagems = dbMensagem.getAll(usuarioDestino.getId());
        }// Pegar dados da conversa salva no dispositivo

        /*
         * LISTA_DE_MENSAGENS   <-- OK
         */
        {
//            adapter = new SingleItemMensagemAdapter(mensagems);
//            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//            recyclerView.setLayoutManager(layoutManager);
//            recyclerView.setAdapter(adapter);
//            recyclerView.scrollToPosition(adapter.getItemCount()-1);
        }// adaptar recyclerView


        /*
         * ID do contato da conversa    <-- OK
         * FbRefUserLogado
         * ADAPTER
         */
        {
            valueEventListenerMensagens = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for (DataSnapshot data : dataSnapshot.getChildren()) {
//                        Mensagem mensagem = data.getValue(Mensagem.class);
//                        if (mensagem != null){
//                            SQLiteMensagem.Descriptografar(mensagem);
//                            mensagems.add(mensagem);
//                        }
//                    }
//                    adapter.notifyDataSetChanged();
//                    recyclerView.scrollToPosition(adapter.getItemCount()-1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            };
        }// evento pra receber mensagens do firebase

        {
            btn_enviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String texto = txt_caixa_de_texto.getText().toString();
                    if(texto.trim().isEmpty())
                        return;
                    OgranizarDados(texto);
                    txt_caixa_de_texto.setText("");
//                    recyclerView.scrollToPosition(adapter.getItemCount()-1);
                }
            });
        }// botão enviar msg
    }

    /*  Dados que preciso
    *
    * ID do usuário logado    <-- OK
    * ID do contato da conversa    <-- OK
    * */
    private void PegarReferenciaDaConversa() {
//
//        //Na hierarquia firebaseReferenciaConversa = root > usuarios > id_usuario_logado > conversas > id_usuario_destino
//        fbRefMensagens = Import.getFirebase.getReference()
//                .child(Constantes.USUARIO)
//                .child(usuarioLogadoId)
//                .child(Constantes.USUARIO_CONVERSAS)
//                .child(usuarioDestino.getId());
//        //Na hierarquia firebaseReferenciaConversa = root > usuarios > id_usuario_destino > conversas > id_usuario_logado
//        fbRefUserDestino = Import.getFirebase.getReference()
//                .child(Constantes.USUARIO)
//                .child(usuarioDestino.getId())
//                .child(Constantes.USUARIO_CONVERSAS).child(usuarioLogadoId);

//        fbRefMensagens = fbRefUserLogado;
    }

/*
    @Override
    protected void onStart() {
        super.onStart();
        if (ERRO)
            return;
        fbRefMensagens.addValueEventListener(valueEventListenerMensagens);
        Import.getUsuario.setUsuarioConversa(usuarioDestino);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ERRO)
            return;
        fbRefMensagens.removeEventListener(valueEventListenerMensagens);
        Import.getUsuario.setUsuarioConversa(null);
    }
*/

    private void OgranizarDados(String texto) {
        String data_de_envio = Import.get.Data();

        // Todos os dados devem estar descriptografados, na hora de salvar que criptografa
        Mensagem mensagem = new Mensagem();
        mensagem.setId_remetente(usuarioLogadoId);
        mensagem.setId_conversa(usuarioDestino.getId());
        mensagem.setData_de_envio(data_de_envio);
        mensagem.setMensagem(texto);
        mensagem.setStatus(Constantes.CONVERSA_MENSAGEM_NAO_ENVIADA);
        mensagem.setArquivo(0);

        if (Import.SalvarMensagemNoDispositivo(this, mensagem)) {// aqui salva a msg

            mensagems.add(mensagem);
//            adapter.notifyDataSetChanged();

            Conversa conversa = new Conversa();
            conversa.setId(mensagem.getId_conversa());// C
            conversa.setNome_contato(usuarioDestino.getNome());
            conversa.setUltima_msg(mensagem.getMensagem());// C
            conversa.setData(mensagem.getData_de_envio());
            conversa.setFoto(usuarioDestino.getFoto());
            conversa.setLido(Constantes.CONVERSA_MENSAGEM_LIDA);

//            Import.SalvarConversaNoDispositivo(this, conversa);
//            mensagem.setId_conversa(usuarioLogadoId);
//            SQLiteMensagem.Criptografar(mensagem);
//            if(SalvarDadosNoFirebaseUsuarioDestino(fbRefUserDestino, mensagem)){
//                mensagem.setStatus(Constantes.CONVERSA_MENSAGEM_ENVIADA);
//                mensagem.setId_conversa(usuarioDestino.getId());
//                if(Import.SalvarMensagemNoDispositivo(this, mensagem)) {// aqui atualiza a msg (se foi enviada ou não)
//                    SQLiteMensagem.Descriptografar(mensagem);
//                    mensagems.set(mensagems.size()-1, mensagem);
//                    adapter.notifyDataSetChanged();
//                }
//            } else {
//                Alert("Não foi possível enviar esta mensagem");
//            }
//        } else
//            Alert("Não foi possível enviar esta mensagem!");
        }
    }
    private void Alert(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private boolean SalvarDadosNoFirebaseUsuarioLogado(DatabaseReference reference, Mensagem mensagem){
        try {
            DatabaseReference referenceTemp = reference.child(mensagem.getData_de_envio());
            referenceTemp.setValue(mensagem);
            return true;
        }catch (Exception e){
            Log.e(TAG, "SalvarDadosNoFirebaseUsuarioLogado: " + e.getMessage());
            return false;
        }
    }
    private boolean SalvarDadosNoFirebaseUsuarioDestino(DatabaseReference reference, Mensagem mensagem){
        try {
            DatabaseReference referenceTemp = reference.child(mensagem.getData_de_envio());
            referenceTemp.setValue(mensagem);
            return true;
        }catch (Exception e){
            Log.e(TAG, "SalvarDadosNoFirebaseUsuarioDestino: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    /*
     *
     * *
     * *
     * *
     * * perfil usuario OK
     * *notificações (as tips recebidas) OK
     * *
     * *procurar Tipsters
     * *postagens de tipsters gratis (tipo pagina de feed)
     * *
     * *
     *
     */
    private class SingleItemMensagemAdapter extends RecyclerView.Adapter<ViewHolder> {

        ArrayList<Mensagem> mensagems;

        SingleItemMensagemAdapter(ArrayList<Mensagem> mensagems) {
            this.mensagems = mensagems;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ConversaActivity.this).inflate(R.layout.item_conversa_mensagem, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            {
                holder.mensagem.setText(mensagems.get(position).getMensagem());
                holder.data.setText(Import.splitData(mensagems.get(position).getData_de_envio()));
                RelativeLayout.LayoutParams paramsGeral = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams paramsData = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                paramsData.addRule(RelativeLayout.BELOW, R.id.item_conversa_layout);

                if (Criptografia.criptografar(mensagems.get(position).getId_remetente()).equals(usuarioLogadoId)) {
                    holder.mensagem.setBackground(getDrawable(R.drawable.bg_retangulo_conversa_mensagem_direita));
                    paramsGeral.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    paramsGeral.addRule(RelativeLayout.END_OF, R.id.item_conversa_layout_geral);
                    paramsData.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    paramsData.addRule(RelativeLayout.END_OF, R.id.item_conversa_layout_geral);
                    switch (mensagems.get(position).getStatus()){
                        case Constantes.CONVERSA_MENSAGEM_NAO_ENVIADA:{
                            holder.data.setTextColor(getResources().getColor(R.color.vermelho));
                            break;
                        }
                        case Constantes.CONVERSA_MENSAGEM_ENVIADA:{
                            holder.data.setTextColor(getResources().getColor(R.color.amareloDark));
                            break;
                        }
                        case Constantes.CONVERSA_MENSAGEM_LIDA:{
                            holder.data.setTextColor(getResources().getColor(R.color.verde));
                            break;
                        }
                    }
                } else {
                    holder.mensagem.setBackground(getDrawable(R.drawable.bg_retangulo_conversa_mensagem_esquerda));
                    paramsGeral.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    paramsGeral.addRule(RelativeLayout.START_OF, R.id.item_conversa_layout_geral);
                    paramsData.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    paramsData.addRule(RelativeLayout.START_OF, R.id.item_conversa_layout_geral);
                    holder.data.setTextColor(getResources().getColor(R.color.verde));
                }
                holder.relativeLayout.setLayoutParams(paramsGeral);
                holder.data.setLayoutParams(paramsData);
            }

        }

        @Override
        public int getItemCount() {
            return mensagems != null ? mensagems.size() : 0;
        }
    }
    /*
     *
     * *
     * *
     * *
     * *
     * *
     * *
     * *
     * *
     * *
     * *
     *
     */
    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView mensagem, data;
        RelativeLayout relativeLayout;

        @SuppressLint("ClickableViewAccessibility")
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.item_conversa_layout);
            mensagem = itemView.findViewById(R.id.item_conversa_mensagem);
            data = itemView.findViewById(R.id.item_conversa_data);
        }

    }
}
