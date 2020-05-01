package com.ookiisoftware.protips.fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ookiisoftware.protips.R;

public class GInicioFragment extends Fragment {

    //region Variáveis

    private Activity activity;

    //endregion

    public GInicioFragment() {}

    public GInicioFragment(Activity activity) {
        this.activity = activity;
    }

    //region Overrides

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_g_inicio, container, false);
        init(view);
        return view;
    }

    //endregion

    //region Métodos

    private void init(View view) {

    }

    //endregion
}
