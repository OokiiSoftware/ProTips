package com.ookiisoftware.protips.modelo;

import com.ookiisoftware.protips.activity.GerenciaActivity;
import com.ookiisoftware.protips.activity.MainActivity;

public class Activites {
    private MainActivity mainActivity;
    private GerenciaActivity gerenciaActivity;

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public GerenciaActivity getGerenciaActivity() {
        return gerenciaActivity;
    }

    public void setGerenciaActivity(GerenciaActivity gerenciaActivity) {
        this.gerenciaActivity = gerenciaActivity;
    }
}
