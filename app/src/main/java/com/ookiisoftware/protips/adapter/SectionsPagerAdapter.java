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
import com.ookiisoftware.protips.fragment.TipsFragment;
import com.ookiisoftware.protips.fragment.InicioFragment;
import com.ookiisoftware.protips.fragment.PerfilFragment;
import com.ookiisoftware.protips.fragment.batepapo.ContatosFragment;
import com.ookiisoftware.protips.fragment.batepapo.ConversasFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITULOS_MAIN_ACTIVITY = new int[]{R.string.titulo_inicio, R.string.titulo_pesquisa,  R.string.titulo_perfil/*, R.string.titulo_notificacoes*/};
    @StringRes
    private static final int[] TAB_TITULOS_BATEPAPO = new int[]{R.string.titulo_conversas, R.string.titulo_contatos};

    private final String className;
    private final Activity activity;
    private InicioFragment inicioFragment;
    private TipsFragment tipsFragment;
    private PerfilFragment perfilFragment;

    public SectionsPagerAdapter(FragmentManager fm, int behavior, Activity activity,
                                InicioFragment inicioFragment, TipsFragment tipsFragment, PerfilFragment perfilFragment) {
        super(fm, behavior);
        this.className = activity.getClass().getSimpleName();
        this.activity = activity;

        this.inicioFragment = inicioFragment;
        this.perfilFragment = perfilFragment;
        this.tipsFragment = tipsFragment;
    }

    public SectionsPagerAdapter(FragmentManager fm, int behavior, Activity activity) {
        super(fm, behavior);
        this.className = activity.getClass().getSimpleName();
        this.activity = activity;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (className) {
            case Constantes.CLASSE_MAIN_ACTIVITY_NAME: {
                switch (position){
                    case 0:
                        return inicioFragment;
                    case 1:
                        return tipsFragment;
                    case 2:
                        return perfilFragment;
//                    case 3:
//                        return new NotificationsFragment();
                }
            }
            case Constantes.CLASSE_BATEPAPO_NAME: {
                switch (position){
                    case 0:
                        return new ConversasFragment();
                    case 1:
                        return new ContatosFragment();
                }
            }
        }
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (className){
            case Constantes.CLASSE_MAIN_ACTIVITY_NAME: {
                return activity.getResources().getString(TAB_TITULOS_MAIN_ACTIVITY[position]);
            }
            case Constantes.CLASSE_BATEPAPO_NAME: {
                return activity.getResources().getString(TAB_TITULOS_BATEPAPO[position]);
            }
        }
        return "";
    }

    @Override
    public int getCount() {
        switch (className){
            case Constantes.CLASSE_MAIN_ACTIVITY_NAME: {
                return TAB_TITULOS_MAIN_ACTIVITY.length;
            }
            case Constantes.CLASSE_BATEPAPO_NAME: {
                return TAB_TITULOS_BATEPAPO.length;
            }
        }
        return 0;
    }
}
