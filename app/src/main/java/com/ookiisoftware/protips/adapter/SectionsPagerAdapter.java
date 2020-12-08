package com.ookiisoftware.protips.adapter;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ookiisoftware.protips.R;
import com.ookiisoftware.protips.auxiliar.Const;
import com.ookiisoftware.protips.fragment.NotificationsFragment;
import com.ookiisoftware.protips.fragment.PostPerfilFragment;
import com.ookiisoftware.protips.fragment.TipstersFragment;
import com.ookiisoftware.protips.fragment.FeedFragment;
import com.ookiisoftware.protips.fragment.PerfilFragment;
import com.ookiisoftware.protips.fragment.batepapo.ContatosFragment;
import com.ookiisoftware.protips.fragment.batepapo.ConversasFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    //region Variáveis

    @StringRes
    private static final int[] TAB_TITULOS_MAIN_ACTIVITY = new int[]{R.string.titulo_feed, R.string.titulo_filiados,  R.string.titulo_perfil, R.string.titulo_notificacoes};
    @StringRes
    private static final int[] TAB_TITULOS_GERENCIA = new int[]{R.string.titulo_g_inicio, R.string.titulo_g_solicitacoes};
    @StringRes
    private static final int[] TAB_TITULOS_BATEPAPO = new int[]{R.string.titulo_conversas, R.string.titulo_contatos};
    @StringRes
    private static final int[] TAB_TITULOS_PERFIL = new int[]{R.string.titulo_post_perfil, R.string.titulo_perfil};

    private String className;
    private final Activity activity;

    private FeedFragment feedFragment;
    private TipstersFragment tipstersFragment;
    private PerfilFragment perfilFragment;
    private NotificationsFragment notificationsFragment;

    private PostPerfilFragment postPerfilFragment1;
    private PostPerfilFragment postPerfilFragment2;

    private static int behavior = FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;
    //endregion

    public SectionsPagerAdapter(FragmentManager fm, Activity activity, FeedFragment feedFragment, TipstersFragment tipstersFragment, PerfilFragment perfilFragment, NotificationsFragment notificationsFragment) {
        super(fm, behavior);
        this.className = activity.getClass().getSimpleName();
        this.activity = activity;

        this.feedFragment = feedFragment;
        this.perfilFragment = perfilFragment;
        this.tipstersFragment = tipstersFragment;
        this.notificationsFragment = notificationsFragment;
    }

    public SectionsPagerAdapter(FragmentManager fm, Activity activity, TipstersFragment tipstersFragment, NotificationsFragment notificationsFragment) {
        super(fm, behavior);
        this.activity = activity;
        this.className = activity.getClass().getSimpleName();
        this.notificationsFragment = notificationsFragment;
        this.tipstersFragment = tipstersFragment;
    }

    public SectionsPagerAdapter(FragmentManager fm, Activity activity, PostPerfilFragment postPerfilFragment1, PostPerfilFragment postPerfilFragment2) {
        super(fm, behavior);
        this.className = activity.getClass().getSimpleName();
        this.activity = activity;
        this.postPerfilFragment1 = postPerfilFragment1;
        this.postPerfilFragment2 = postPerfilFragment2;
        if (this.className.equals(Const.classes.activites.MAIN)) {
            this.className = Const.classes.fragments.PERFIL;
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (className) {
            case Const.classes.activites.MAIN: {
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
            case Const.classes.activites.GERENCIA: {
                switch (position) {
                    case 0:
                        return tipstersFragment;
                    case 1:
                        return notificationsFragment;
                }
            }
            case Const.classes.activites.BATEPAPO: {
                switch (position) {
                    case 0:
                        return new ConversasFragment();
                    case 1:
                        return new ContatosFragment();
                }
            }
            case Const.classes.activites.PERFIL_TIPSTER:
            case Const.classes.fragments.PERFIL: {
                switch (position) {
                    case 0:
                        return postPerfilFragment1;
                    case 1:
                        return postPerfilFragment2;
                }
            }
        }
        return new Fragment();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (className) {
            case Const.classes.activites.MAIN: {
                return activity.getResources().getString(TAB_TITULOS_MAIN_ACTIVITY[position]);
            }
            case Const.classes.activites.GERENCIA: {
                return activity.getResources().getString(TAB_TITULOS_GERENCIA[position]);
            }
            case Const.classes.activites.BATEPAPO: {
                return activity.getResources().getString(TAB_TITULOS_BATEPAPO[position]);
            }
            case Const.classes.activites.PERFIL_TIPSTER: {
                return "";
            }
            case Const.classes.fragments.PERFIL: {
                return activity.getResources().getString(TAB_TITULOS_PERFIL[position]);
            }
        }
        return "";
    }

    @Override
    public int getCount() {
        switch (className) {
            case Const.classes.activites.MAIN: {
                return TAB_TITULOS_MAIN_ACTIVITY.length;
            }
            case Const.classes.activites.GERENCIA: {
                return TAB_TITULOS_GERENCIA.length;
            }
            case Const.classes.activites.BATEPAPO: {
                return TAB_TITULOS_BATEPAPO.length;
            }
            case Const.classes.activites.PERFIL_TIPSTER: {
                return 2;
            }
            case Const.classes.fragments.PERFIL: {
                return TAB_TITULOS_PERFIL.length;
            }
        }
        return 0;
    }

}
