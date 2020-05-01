package com.ookiisoftware.protips.modelo;

import com.google.firebase.database.DatabaseReference;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;

import java.util.ArrayList;
import java.util.HashMap;

public class Punter {

    private Usuario dados;
    private HashMap<String,String> tipsters;

    public Punter() {
        tipsters = new HashMap<>();
        dados = new Usuario();
    }

    public void salvar() {
        DatabaseReference reference = Import.getFirebase.getReference();
        reference
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.PUNTERS)
                .child(dados.getId())
//                .child(Constantes.firebase.child.DADOS)
                .setValue(this);

        reference
                .child(Constantes.firebase.child.IDENTIFICADOR)
                .child(dados.getTipname())
                .setValue(dados.getId());
    }

    public void addTipster(String id) {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.PUNTERS)
                .child(dados.getId())
                .child(Constantes.firebase.child.TIPSTERS)
                .child(id)
                .setValue(id);
    }

    public void removerTipster(String id) {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.PUNTERS)
                .child(dados.getId())
                .child(Constantes.firebase.child.TIPSTERS)
                .child(id)
                .removeValue();
    }

    public void excluir() {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.PUNTERS)
                .child(dados.getId())
                .removeValue();
    }

    public void bloquear() {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.PUNTERS)
                .child(getDados().getId())
                .child(Constantes.firebase.child.DADOS)
                .child(Constantes.firebase.child.BLOQUEADO)
                .setValue(true);
        getDados().setBloqueado(true);
    }

    public void desbloquear() {
        Import.getFirebase.getReference()
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.PUNTERS)
                .child(getDados().getId())
                .child(Constantes.firebase.child.DADOS)
                .child(Constantes.firebase.child.BLOQUEADO)
                .setValue(false);
        getDados().setBloqueado(false);
    }

    //region gets sets

    public Usuario getDados() {
        return dados;
    }

    public void setDados(Usuario dados) {
        this.dados = dados;
    }

    public HashMap<String, String> getTipsters() {
        if (tipsters == null)
            tipsters = new HashMap<>();
        return tipsters;
    }

    public void setTipsters(HashMap<String, String> tipsters) {
        this.tipsters = tipsters;
    }

    //endregion

}
