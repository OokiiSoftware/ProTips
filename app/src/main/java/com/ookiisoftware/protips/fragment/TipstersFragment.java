package com.ookiisoftware.protips.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.activity.PerfilActivity;
import com.ookiisoftware.protips.activity.PerfilVisitanteActivity;
import com.ookiisoftware.protips.adapter.PunterAdapter;
import com.ookiisoftware.protips.adapter.TipsterAdapter;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Punter;
import com.ookiisoftware.protips.modelo.Tipster;

public class TipstersFragment extends Fragment {

    //region Variáveis
//    private static final String TAG = "TipstersFragment";

//    final private ArrayList<Tipster> tipsters;
    private Activity activity;
    private TipsterAdapter tipsterAdapter;
    private PunterAdapter punterAdapter;


    public SwipeRefreshLayout refreshLayout;
    //endregion

    public TipstersFragment(){

    }
    public TipstersFragment(Activity activity) {
        this.activity = activity;
//        tipsters = Import.get.tipsters.getAll();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tipsters, container, false);
        Init(view);
        return view;
    }

    //region Métodos

    private void Init(View view) {
        //region findViewById
//        final SearchView et_pesquisa = view.findViewById(R.id.et_pesquisa);
        LinearLayout btn_filtro = view.findViewById(R.id.btn_filtro);
        final RecyclerView recyclerView = view.findViewById(R.id.recycler);
        refreshLayout = view.findViewById(R.id.swipe_refresh);
        //endregion

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Import.activites.getMainActivity().feedUpdate();
            }
        });

        boolean isTipster = Import.getFirebase.isTipster();

        if (isTipster) {
            punterAdapter = new PunterAdapter(activity, Import.get.punter.getAll()) {
                @Override
                public void onClick(View v) {
                    int position = recyclerView.getChildAdapterPosition(v);
                    Punter item = punterAdapter.getItem(position);

                    Intent intent = new Intent(activity, PerfilVisitanteActivity.class);
                    intent.putExtra(Constantes.intent.USER_ID, item.getDados().getId());
                    activity.startActivity(intent);
                }
            };
            recyclerView.setAdapter(punterAdapter);
        } else {
            tipsterAdapter = new TipsterAdapter(activity, Import.get.tipsters.getAll()) {
                @Override
                public void onClick(View v) {
                    int position = recyclerView.getChildAdapterPosition(v);
                    Tipster item = tipsterAdapter.getItem(position);

                    Intent intent = new Intent(activity, PerfilVisitanteActivity.class);
                    intent.putExtra(Constantes.intent.USER_ID, item.getDados().getId());
                    activity.startActivity(intent);
                }
            };
            recyclerView.setAdapter(tipsterAdapter);
        }

        btn_filtro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupFiltro();
            }
        });
    }

    public void adapterUpdate() {
        if (tipsterAdapter != null)
            tipsterAdapter.notifyDataSetChanged();
        if (punterAdapter != null)
            punterAdapter.notifyDataSetChanged();
    }

    public TipsterAdapter getTipsterAdapter() {
        return tipsterAdapter;
    }
    public PunterAdapter getPunterAdapter() {
        return punterAdapter;
    }

    private void popupFiltro() {

    }

    //endregion
}