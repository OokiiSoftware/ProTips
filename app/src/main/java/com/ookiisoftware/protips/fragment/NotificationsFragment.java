package com.ookiisoftware.protips.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.activity.PerfilPunterActivity;
import com.ookiisoftware.protips.adapter.PunterAdapter;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Punter;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;

    private Activity activity;
    private PunterAdapter adapter;
    private ArrayList<Punter> punters;

    public NotificationsFragment() {}
    public NotificationsFragment(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        Init(view);
        ConfigurarSwippeDoRecyclerView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapterUpdate();
    }

    private void Init(View view) {
        recyclerView = view.findViewById(R.id.recycler);

        punters = Import.get.tipsters.getPuntersPendentes();

        adapter = new PunterAdapter(activity, punters) {
            @Override
            public void onClick(View v) {
                int position = recyclerView.getChildAdapterPosition(v);
                Punter item = adapter.getItem(position);
                Intent intent = new Intent(activity, PerfilPunterActivity.class);
                intent.putExtra(Constantes.intent.USER_ID, item.getDados().getId());
                Import.Alert.msg("", "onClick", item.getDados().getId());
                activity.startActivity(intent);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    public void adapterUpdate() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    private void ConfigurarSwippeDoRecyclerView() {
        ItemTouchHelper.SimpleCallback itemTouchHelper = new ItemTouchHelper.SimpleCallback((int)0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                punters.remove(viewHolder.getAdapterPosition());
                adapter.notifyDataSetChanged();
            }
        };

        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView);
    }

    /*public class SingleItemNotificacaoAdapter extends RecyclerView.Adapter<ViewHolder> {

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

    }*/
}