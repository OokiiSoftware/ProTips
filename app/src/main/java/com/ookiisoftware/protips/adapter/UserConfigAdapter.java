package com.ookiisoftware.protips.adapter;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.Constantes;

public class UserConfigAdapter extends FragmentPagerAdapter {

    private final String fragmentID;
    private final Activity activity;

    @StringRes
    private static final int[] TAB_TITULOS = new int[]{R.string.titulo_edit_user, R.string.titulo_preferencias};

    public UserConfigAdapter(@NonNull FragmentManager fm, int behavior, Activity activity, String fragmentID) {
        super(fm, behavior);
        this.fragmentID = fragmentID;
        this.activity = activity;

        switch (fragmentID){
            case Constantes.FRAGMENT_EDIT: {

            }
            case Constantes.FRAGMENT_PREFERENCIAS: {

            }
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (fragmentID){
            case Constantes.FRAGMENT_EDIT: {

            }
            case Constantes.FRAGMENT_PREFERENCIAS: {

            }
        }
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (fragmentID){
            case Constantes.FRAGMENT_EDIT: {
                return activity.getResources().getString(TAB_TITULOS[0]);
            }
            case Constantes.FRAGMENT_PREFERENCIAS: {
                return activity.getResources().getString(TAB_TITULOS[1]);
            }
            default:
                return "";
        }
    }

    @Override
    public int getCount() {
        return 1;
    }
}
