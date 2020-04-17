package com.ookiisoftware.protips.fragment;

import android.app.Activity;
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
import com.ookiisoftware.protips.adapter.TipsterAdapter;
import com.ookiisoftware.protips.auxiliar.Import;

public class TipstersFragment extends Fragment {

    //region Variáveis
//    private static final String TAG = "TipstersFragment";

//    final private ArrayList<Tipster> tipsters;
    private Activity activity;
    private TipsterAdapter adapter;
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
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        refreshLayout = view.findViewById(R.id.swipe_refresh);
        //endregion

        refreshLayout.setRefreshing(true);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Import.activites.getMainActivity().feedUpdate();
            }
        });

        adapter = new TipsterAdapter(activity, Import.get.tipsters.getAll());
        recyclerView.setAdapter(adapter);

        /*final SearchView et_pesquisa = view.findViewById(R.id.et_pesquisa);
        et_pesquisa.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_pesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                refreshLayout.setEnabled(newText.isEmpty());
                Import.activites.getMainActivity().inicioFragment.refreshLayout.setEnabled(newText.isEmpty());
                adapter.getFilter().filter(newText);
                return false;
            }
        });*/
        /*et_pesquisa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String texto = et_pesquisa.getText().toString();
                tipsters.clear();
                if (texto.isEmpty()){
                    tipsters.addAll(Import.get.tipsters.getAll());
                } else {
                    tipsters.addAll(pesquisa(texto));
                }
                adapterUpdate();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });*/

        btn_filtro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupFiltro();
            }
        });
    }

    public void adapterUpdate() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    public TipsterAdapter getAdapter () {
        return adapter;
    }

    private void popupFiltro() {

    }

    //endregion
}