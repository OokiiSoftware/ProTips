package com.ookiisoftware.protips.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.adapter.TipsterAdapter;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.Tipster;

import java.util.ArrayList;

public class TipsFragment extends Fragment {

    final private ArrayList<Tipster> tipsters;
    private Activity activity;
    private TipsterAdapter adapter;

    public TipsFragment(Activity activity) {
        this.activity = activity;
        tipsters = Import.get.tipsters.getAll();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tips, container, false);
        Init(view);
        return view;
    }

    //region Métodos

    private void Init(View view) {
        final EditText et_pesquisa = view.findViewById(R.id.et_pesquisa);
        //region Variáveis
        LinearLayout btn_filtro = view.findViewById(R.id.btn_filtro);
        RecyclerView recyclerView = view.findViewById(R.id.recycler);

        adapter = new TipsterAdapter(activity, tipsters);
        recyclerView.setAdapter(adapter);

        et_pesquisa.addTextChangedListener(new TextWatcher() {
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
        });

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

    private ArrayList<Tipster> pesquisa(String busca) {
        ArrayList<Tipster> items = new ArrayList<>();
        for (Tipster item : Import.get.tipsters.getAll())
            if (item.getDados().getNome().contains(busca))
                items.add(item);
        return items;
    }

    private void popupFiltro() {

    }

    //endregion
}