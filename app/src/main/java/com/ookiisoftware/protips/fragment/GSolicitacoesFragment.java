package com.ookiisoftware.protips.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.activity.PerfilPunterActivity;
import com.ookiisoftware.protips.adapter.TipsterAdapter;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Tipster;

public class GSolicitacoesFragment extends Fragment {

    //region Variáveis
//    private static final String TAG = "TipstersFragment";

    private Activity activity;
    private TipsterAdapter tipsterAdapter;

    public SwipeRefreshLayout refreshLayout;

    //endregion

    public GSolicitacoesFragment(){}
    public GSolicitacoesFragment(Activity activity) {
        this.activity = activity;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tipsters, container, false);
        Init(view);
        return view;
    }

    //region Métodos

    private void Init(View view) {
        //region findViewById
        final RecyclerView recyclerView = view.findViewById(R.id.recycler);
        refreshLayout = view.findViewById(R.id.swipeRefresh);
        //endregion

        refreshLayout.setOnRefreshListener(() -> Import.activites.getMainActivity().feedUpdate());

        tipsterAdapter = new TipsterAdapter(activity, Import.get.tipsters.getAll()) {
            @Override
            public void onClick(View v) {
                int position = recyclerView.getChildAdapterPosition(v);
                Tipster item = tipsterAdapter.getItem(position);

                Intent intent = new Intent(activity, PerfilPunterActivity.class);
                intent.putExtra(Constantes.intent.USER_ID, item.getDados().getId());
                intent.putExtra(Constantes.intent.IS_GERENCIA, true);
                activity.startActivity(intent);
            }
        };
        recyclerView.setAdapter(tipsterAdapter);
    }

    public void adapterUpdate() {
        if (tipsterAdapter != null)
            tipsterAdapter.notifyDataSetChanged();
    }

    public TipsterAdapter getTipsterAdapter() {
        return tipsterAdapter;
    }

    //endregion
}