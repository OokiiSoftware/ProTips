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
import com.ookiisoftware.protips.auxiliar.Import;
import com.ookiisoftware.protips.fragment.NotificationsFragment;
import com.ookiisoftware.protips.fragment.TipstersFragment;
import com.ookiisoftware.protips.fragment.FeedFragment;
import com.ookiisoftware.protips.fragment.PerfilFragment;
import com.ookiisoftware.protips.fragment.batepapo.ContatosFragment;
import com.ookiisoftware.protips.fragment.batepapo.ConversasFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    //region Variáveis

    @StringRes
    private static final int[] TAB_TITULOS_MAIN_ACTIVITY = new int[]{R.string.titulo_inicio, R.string.titulo_tipsters,  R.string.titulo_perfil, R.string.titulo_notificacoes};
   @StringRes
    private static final int[] TAB_TITULOS_MAIN_ACTIVITY_2 = new int[]{R.string.titulo_inicio, R.string.titulo_punters,  R.string.titulo_perfil, R.string.titulo_notificacoes};
    @StringRes
    private static final int[] TAB_TITULOS_BATEPAPO = new int[]{R.string.titulo_conversas, R.string.titulo_contatos};

    private final String className;
    private final Activity activity;
    private FeedFragment feedFragment;
    private TipstersFragment tipstersFragment;
    private PerfilFragment perfilFragment;
    private NotificationsFragment notificationsFragment;

    //endregion

    public SectionsPagerAdapter(FragmentManager fm, int behavior, Activity activity,
                                FeedFragment feedFragment, TipstersFragment tipstersFragment,
                                PerfilFragment perfilFragment, NotificationsFragment notificationsFragment) {
        super(fm, behavior);
        this.className = activity.getClass().getSimpleName();
        this.activity = activity;

        this.feedFragment = feedFragment;
        this.perfilFragment = perfilFragment;
        this.tipstersFragment = tipstersFragment;
        this.notificationsFragment = notificationsFragment;
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
                        return feedFragment;
                    case 1:
                        return tipstersFragment;
                    case 2:
                        return perfilFragment;
                    case 3:
                        return notificationsFragment;
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
                if (Import.getFirebase.isTipster())
                    return activity.getResources().getString(TAB_TITULOS_MAIN_ACTIVITY_2[position]);
                else
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
                if (Import.getFirebase.isTipster())
                    return TAB_TITULOS_MAIN_ACTIVITY.length;
                else
                    return TAB_TITULOS_MAIN_ACTIVITY.length -1;
            }
            case Constantes.CLASSE_BATEPAPO_NAME: {
                return TAB_TITULOS_BATEPAPO.length;
            }
        }
        return 0;
    }

}
