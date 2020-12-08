package com.ookiisoftware.protips.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.activity.PerfilTipsterActivity;
import com.ookiisoftware.protips.adapter.UserAdapter;
import com.ookiisoftware.protips.auxiliar.Const;
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.modelo.User;

import java.util.ArrayList;
import java.util.Collections;

public class TipstersFragment extends Fragment {

    //region Variáveis
//    private static final String TAG = "TipstersFragment";

    private Activity activity;
    private UserAdapter userAdapter;
    private ArrayList<User> data;

    private Spinner sp_order;
    private CheckBox cb_order;
    public SwipeRefreshLayout refreshLayout;

    private boolean isGerencia;

    //endregion

    public TipstersFragment(){}
    public TipstersFragment(Activity activity, boolean isGerencia) {
        this.activity = activity;
        this.isGerencia = isGerencia;
    }

    //region Overrides

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tipsters, container, false);
        init(view);
        return view;
    }

    //endregion

    //region Métodos

    private void init(View view) {
        //region findViewById
        final RecyclerView recyclerView = view.findViewById(R.id.recycler);
        refreshLayout = view.findViewById(R.id.swipeRefresh);
        sp_order = view.findViewById(R.id.sp_order);
        cb_order = view.findViewById(R.id.cb_order);
        //endregion

        data = Import.get.tipsters.getAll();
        userAdapter = new UserAdapter(activity, data) {
            @Override
            public void onClick(View v) {
                int position = recyclerView.getChildAdapterPosition(v);
                User item = userAdapter.getItem(position);

                Intent intent = new Intent(activity, PerfilTipsterActivity.class);
                intent.putExtra(Const.intent.USER_ID, item.getDados().getId());
                intent.putExtra(Const.intent.IS_GERENCIA, isGerencia);
                activity.startActivity(intent);
            }
        };
        recyclerView.setAdapter(userAdapter);

        refreshLayout.setOnRefreshListener(() -> {
            if (isGerencia)
                Import.activites.getGerenciaActivity().feedUpdate();
            else
                Import.activites.getMainActivity().feedUpdate();
        });

        sp_order.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean asc = cb_order.isChecked();
                ordenar(position, asc);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        cb_order.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ordenar(sp_order.getSelectedItemPosition(), isChecked);
        });
    }

    private void ordenar(int position, boolean asc) {
        switch (position) {
            case 0:
                Collections.sort(data, new User.sortByMedia(asc));
                break;
            case 1:
                Collections.sort(data, new User.sortByNome(asc));
                break;
            case 2:
                Collections.sort(data, new User.sortByGreen(asc));
                break;
            case 3:
                Collections.sort(data, new User.sortByRed(asc));
                break;
            case 4:
                Collections.sort(data, new User.sortByPostCount(asc));
                break;
        }
        adapterUpdate();
    }

    public void adapterUpdate() {
        if (userAdapter != null)
            userAdapter.notifyDataSetChanged();
    }

    public UserAdapter getUserAdapter() {
        return userAdapter;
    }

    //endregion
}