package com.ookiisoftware.protips.modelo;

import com.google.firebase.database.DatabaseReference;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;

import java.util.ArrayList;

public class Apostador {

    private Usuario dados;
    private ArrayList<String> tipsters;

    public Apostador() {
        tipsters = new ArrayList<>();
        dados = new Usuario();
    }

    public void salvar() {
        DatabaseReference reference = Import.getFirebase.getReference();
        reference
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.APOSTADOR)
                .child(dados.getId())
                .child(Constantes.firebase.child.DADOS)
                .setValue(this);

        reference
                .child(Constantes.firebase.child.IDENTIFICADOR)
                .child(dados.getTipname())
                .setValue(dados.getId());
    }

    //region gets sets

    public Usuario getDados() {
        return dados;
    }

    public void setDados(Usuario dados) {
        this.dados = dados;
    }

    public ArrayList<String> getTipsters() {
        if (tipsters == null)
            tipsters = new ArrayList<>();
        return tipsters;
    }

    public void setTipsters(ArrayList<String> tipsters) {
        this.tipsters = tipsters;
    }

    //endregion
}
