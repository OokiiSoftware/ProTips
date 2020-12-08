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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.activity.PerfilPunterActivity;
//import com.ookiisoftware.protips.adapter.PunterAdapter;
import com.ookiisoftware.protips.activity.PerfilTipsterActivity;
import com.ookiisoftware.protips.adapter.UserAdapter;
import com.ookiisoftware.protips.auxiliar.Const;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.User;
//import com.ookiisoftware.protips.modelo.Punter;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    public SwipeRefreshLayout refreshLayout;

    private Activity activity;
    private UserAdapter adapter;
    private ArrayList<User> data;

    private boolean isTipster;
    private boolean isGerencia;

    public NotificationsFragment() {}
    public NotificationsFragment(Activity activity, boolean isGerencia) {
        this.activity = activity;
        this.isGerencia = isGerencia;
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
        removeNotification();
    }

    private void Init(View view) {
        recyclerView = view.findViewById(R.id.recycler);
        refreshLayout = view.findViewById(R.id.swipeRefresh);

        refreshLayout.setEnabled(isGerencia);

        data = Import.get.solicitacao.getAll();
        isTipster = Import.getFirebase.isTipster();

        adapter = new UserAdapter(activity, data) {
            @Override
            public void onClick(View v) {
                int position = recyclerView.getChildAdapterPosition(v);
                User item = adapter.getItem(position);
                Intent intent;
                if (item.getDados().isTipster())
                    intent = new Intent(activity, PerfilTipsterActivity.class);
                else
                    intent = new Intent(activity, PerfilPunterActivity.class);
                intent.putExtra(Const.intent.USER_ID, item.getDados().getId());
                intent.putExtra(Const.intent.IS_GERENCIA, isGerencia);
                activity.startActivity(intent);
            }
        };
        recyclerView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(() -> {
            adapterUpdate();
            refreshLayout.setRefreshing(false);
        });
    }

    public void adapterUpdate() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    private void ConfigurarSwippeDoRecyclerView() {
        ItemTouchHelper.SimpleCallback itemTouchHelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                punters.remove(viewHolder.getAdapterPosition());
//                adapter.notifyDataSetChanged();
            }
        };

        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView);
    }

    private void removeNotification() {
        if (isTipster)
            Import.notificacaoCancel(activity, Const.notification.id.NOVO_PUNTER_PENDENTE);
        else
            Import.notificacaoCancel(activity, Const.notification.id.NOVO_PUNTER_ACEITO);
    }

}