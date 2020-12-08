package com.ookiisoftware.protips.fragment.batepapo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.activity.ConversaActivity;
import com.ookiisoftware.protips.auxiliar.Const;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Conversa;
import com.ookiisoftware.protips.sqlite.SQLiteConversa;

import java.util.ArrayList;

public class ConversasFragment extends Fragment {

    private static final String TAG = "ConversasFragment";

    //Variaveis
    private SQLiteConversa db;
    private DatabaseReference firebase;
    private SingleItemConversaAdapter adapter;
    private ValueEventListener eventListenerConversas;
    private ArrayList<Conversa> conversasNoDispositivo = new ArrayList<>();

    public ConversasFragment() {}

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

        PegarReferenciaDoFirebase();

        db = new SQLiteConversa(getContext());
        conversasNoDispositivo = db.getAll();

        *//*
         * LISTA_DE_CONVERSAS_DO_DISPOSITIVO <-- OK
         *//*
        {
            adapter = new SingleItemConversaAdapter(conversasNoDispositivo);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        }// Adaptar RecyclerView

        eventListenerConversas = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                conversasNoDispositivo.clear();
                conversasNoDispositivo.addAll(db.getAll());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        */
    }

    private void PegarReferenciaDoFirebase() {
        //Na hierarquia = root > usuarios > id_usuario_logado > conversas
//        firebase = Import.getFirebase.getReference()
//                .child(Constantes.USUARIO)
//                .child(Import.getUsuario.getId(getContext()))
//                .child(Constantes.USUARIO_CONVERSAS);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
        firebase.addValueEventListener(eventListenerConversas);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(eventListenerConversas);
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
    public class SingleItemConversaAdapter extends RecyclerView.Adapter<ViewHolder> {

        ArrayList<Conversa> conversas;

        SingleItemConversaAdapter(ArrayList<Conversa> conversas) {
            this.conversas = conversas;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_batepapo, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            if(conversas.get(position).getFoto() != null)
                Glide.with(getActivity()).load(conversas.get(position).getFoto()).into(holder.img_foto_contato);
            holder.nome_contato.setText(conversas.get(position).getNome_contato());
            holder.ultima_msg.setText(conversas.get(position).getUltima_msg());

            holder.data.setText(Import.splitData(conversas.get(position).getData()));

            {
                if(conversas.get(position).getLido() == Const.user.conversa.MENSAGEM_LIDA)
                    holder.img_msg_lida.setVisibility(View.INVISIBLE);
                else
                    holder.img_msg_lida.setVisibility(View.VISIBLE);
            }// Verificar se a mensagem foi lida

            {
                holder.btn_click.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ConversaActivity.class);
                        intent.putExtra(Const.intent.USER_ID, conversas.get(position).getId());
                        intent.putExtra(Const.intent.USER_NOME, conversas.get(position).getNome_contato());
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
            return conversas != null ? conversas.size() : 0;
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
        ImageView img_foto_contato, img_msg_lida, email;
        TextView nome_contato, ultima_msg, data;

        @SuppressLint("ClickableViewAccessibility")
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            btn_click = itemView.findViewById(R.id.item_batepapo_btn_principal);
            img_foto_contato = itemView.findViewById(R.id.item_batepapo_foto);
            img_msg_lida = itemView.findViewById(R.id.item_batepapo_ic_msg_lida);
            nome_contato = itemView.findViewById(R.id.item_batepapo_titulo);
            ultima_msg = itemView.findViewById(R.id.item_batepapo_subtitulo);
            data = itemView.findViewById(R.id.item_batepapo_data);

            email = itemView.findViewById(R.id.item_batepapo_ic_email);
            email.setVisibility(View.INVISIBLE);
        }
    }
}
