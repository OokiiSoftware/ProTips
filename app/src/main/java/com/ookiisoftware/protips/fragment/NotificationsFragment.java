package com.ookiisoftware.protips.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.OnSwipeListener;
import com.ookiisoftware.protips.modelo.Usuario;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private static final String TAG = "NotificationsFragment";

    private ImageButton btn_pesquisar;
    private EditText txt_pesquisar;
    private LinearLayout btn_1, btn_2;
    private RelativeLayout btn_ordenar_por_nome, btn_ordenar_por_status;
    private RecyclerView recyclerView;

    private SingleItemNotificacaoAdapter adapter;
//    private final Usuario usuarioLogado;
    private ArrayList<Usuario> aaaa = new ArrayList<>();

    private OnSwipeListener onSwipeListener = new OnSwipeListener(){
        @Override
        public void onSwipeRight() {
            super.onSwipeRight();
        }

        @Override
        public void onDoubleTouch() {
            super.onDoubleTouch();
        }

        @Override
        public void onSwipeBottom() {
            super.onSwipeBottom();
        }

        @Override
        public void onLongTouch() {
            super.onLongTouch();
        }

        @Override
        public void onSingleTouch() {
            super.onSingleTouch();
        }

        @Override
        public void onSwipeLeft() {
            super.onSwipeLeft();
        }

        @Override
        public void onSwipeTop() {
            super.onSwipeTop();
        }
    };

    public NotificationsFragment() {}


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        Init(view);
        ProgramarElementos();
        ConfigurarSwippeDoRecyclerView();
        return view;
    }


    private void Init(View view) {
        btn_pesquisar = view.findViewById(R.id.notificacoes_btn_pesquisar);
        txt_pesquisar = view.findViewById(R.id.notificacoes_txt_pesquisar);
        btn_1 = view.findViewById(R.id.notificacoes_btn_1);
        btn_2 = view.findViewById(R.id.notificacoes_btn_2);
        btn_ordenar_por_nome = view.findViewById(R.id.notificacoes_btn_ordenar_por_nome);
        btn_ordenar_por_status = view.findViewById(R.id.notificacoes_btn_ordenar_por_status);
        recyclerView = view.findViewById(R.id.notificacoes_recycler_view);
    }

    private void ProgramarElementos() {
        // Configurando o gerenciador de layout para ser uma lista.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SingleItemNotificacaoAdapter(aaaa);
        recyclerView.setAdapter(adapter);
        // Configurando um dividr entre linhas, para uma melhor visualização.
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    private void ConfigurarSwippeDoRecyclerView() {
        ItemTouchHelper.SimpleCallback itemTouchHelper = new ItemTouchHelper.SimpleCallback((int)0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                aaaa.remove(viewHolder.getAdapterPosition());
                adapter.notifyDataSetChanged();
            }
        };

        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView);
    }






    public class SingleItemNotificacaoAdapter extends RecyclerView.Adapter<ViewHolder> {

        ArrayList<Usuario> aaaa;

        SingleItemNotificacaoAdapter(ArrayList<Usuario> aaaa) {
            this.aaaa = aaaa;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_batepapo, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
//            Glide.with(getActivity()).load(usuarioLogado.getImage_uri()).into(holder.img_remetente);
//            holder.nome_remetente.setText(usuarioLogado.getNome());
//            holder.email_remetente.setText(usuarioLogado.getEmail());

            {
                holder.btn_click.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e(TAG, "Single: " + position);
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
            return aaaa != null ? aaaa.size() : 0;
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
        Button btn_click;
        ImageView img_remetente, icone_nao_lido, email;
        TextView nome_remetente, email_remetente;

        @SuppressLint("ClickableViewAccessibility")
        ViewHolder(@NonNull View itemView) {
            super(itemView);

            btn_click = itemView.findViewById(R.id.item_batepapo_btn_principal);
            img_remetente = itemView.findViewById(R.id.item_batepapo_foto);
            nome_remetente = itemView.findViewById(R.id.item_batepapo_titulo);
            email_remetente = itemView.findViewById(R.id.item_batepapo_subtitulo);
            icone_nao_lido = itemView.findViewById(R.id.item_batepapo_ic_msg_lida);
            email = itemView.findViewById(R.id.item_batepapo_ic_email);
        }

    }
}