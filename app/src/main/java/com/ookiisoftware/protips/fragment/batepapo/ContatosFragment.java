package com.ookiisoftware.protips.fragment.batepapo;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.activity.ConversaActivity;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Contato;
import com.ookiisoftware.protips.modelo.Usuario;
import com.ookiisoftware.protips.sqlite.SQLiteContato;

import java.util.ArrayList;

public class ContatosFragment extends Fragment {

    private static final String TAG = "ContatosFragment";

    //Variaveis
    private SQLiteContato db;
    private DatabaseReference firebase;
    private SingleItemContatoAdapter adapter;
    private ValueEventListener valueEventListener;
    private ArrayList<Contato> contatosNoDispositivo = new ArrayList<>();

    public ContatosFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_batepapo, container, false);
        Init(view);
        return view;
    }

    private void Init(View view) {
        /*
        // Elementos do Layout
        RecyclerView recyclerView = view.findViewById(R.id.batepapo_recyclerview);

        final String usuarioLogadoID = Import.getUsuario.getId(getContext());
        db = new SQLiteContato(getContext());
        contatosNoDispositivo = db.getAll(usuarioLogadoID);

        //Na hierarquia = root > usuarios > id_usuario_logado > contatos
        firebase = Import.getFirebase.getReference()
                .child(Constantes.USUARIO)
                .child(usuarioLogadoID)
                .child(Constantes.CONTATO);

        {
            // Se titer apenas o próprio usuário nos contatos, procura os contatos no firebase
            if (contatosNoDispositivo.size() == 0) {
                firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()){
                            if (data.getValue() != null){
                                Import.getFirebase.getReference()
                                        .child(Constantes.USUARIO)
                                        .child(data.getValue().toString())
                                        .child(Constantes.USUARIO_DADOS)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                                                if (usuario != null) {
                                                    Contato contato = Usuario.converterParaContato(usuario);
                                                    db.update(contato);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                                        });
                            }

                            contatosNoDispositivo.clear();
                            contatosNoDispositivo.addAll(db.getAll(usuarioLogadoID));
                            adapter.notifyDataSetChanged();

                            firebase.removeEventListener(this);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }
        }// Sincronizar contatos local com o firebase se estiver vazio

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    if (data.getValue() != null){
                        data.getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null){
                                    String id_contato = dataSnapshot.getValue().toString();

                                    Import.getFirebase.getReference().child(Constantes.USUARIO)
                                            .child(id_contato).child(Constantes.USUARIO_DADOS)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                                                    if (usuario != null) {
                                                        Contato contato = Usuario.converterParaContato(usuario);
                                                        db.update(contato);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) { }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                }
                contatosNoDispositivo.clear();
                contatosNoDispositivo.addAll(db.getAll(usuarioLogadoID));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };

        *//*
         * Lista de Contatos     <-- OK
         *//*
        {
            adapter = new SingleItemContatoAdapter(contatosNoDispositivo);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        }// Adaptar RecyclerView
        */
    }

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListener);
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
    public class SingleItemContatoAdapter extends RecyclerView.Adapter<ViewHolder> {

        ArrayList<Contato> contatos;

        SingleItemContatoAdapter(ArrayList<Contato> contatos) {
            this.contatos = contatos;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_batepapo, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            if (contatos.get(position).getImage_uri() != null)
                Glide.with(getActivity()).load(contatos.get(position).getImage_uri()).into(holder.img_foto_contato);
            holder.nome_contato.setText(contatos.get(position).getNome());
            holder.email_contato.setText(contatos.get(position).getEmail());

            {
                holder.btn_click.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ConversaActivity.class);
                        intent.putExtra(Constantes.CONVERSA_CONTATO_ID, contatos.get(position).getId());
                        intent.putExtra(Constantes.CONVERSA_CONTATO_NOME, contatos.get(position).getNome());
//                        intent.putExtra(Config.CONVERSA_CONTATO_EMAIL, contatos.get(position).getEmail());
                        intent.putExtra(Constantes.CONVERSA_CONTATO_FOTO, contatos.get(position).getImage_uri());
                        startActivity(intent);
                    }
                });
                holder.btn_click.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Log.e(TAG, "Long: " + position);
                        return false;
                    }
                });
            }// ClickListener();
        }

        @Override
        public int getItemCount() {
            return contatos != null ? contatos.size() : 0;
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
        CoordinatorLayout btn_click;
        ImageView img_foto_contato, ic_msg_lida, ic_email;
        TextView nome_contato, email_contato, data;

        @SuppressLint("ClickableViewAccessibility")
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            btn_click = itemView.findViewById(R.id.item_batepapo_btn_principal);
            img_foto_contato = itemView.findViewById(R.id.item_batepapo_foto);
            nome_contato = itemView.findViewById(R.id.item_batepapo_titulo);
            email_contato = itemView.findViewById(R.id.item_batepapo_subtitulo);

            ic_msg_lida = itemView.findViewById(R.id.item_batepapo_ic_msg_lida);
            ic_email = itemView.findViewById(R.id.item_batepapo_ic_email);
            data = itemView.findViewById(R.id.item_batepapo_data);
            ic_msg_lida.setVisibility(View.INVISIBLE);
            ic_email.setVisibility(View.INVISIBLE);
            data.setVisibility(View.INVISIBLE);
        }

    }
}
