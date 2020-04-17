package com.ookiisoftware.protips.modelo;

import com.google.firebase.database.DatabaseReference;
import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tipster {

    private Usuario dados;
    private HashMap<String, Post> postes;
//    private ArrayList<Post> postes;
    private ArrayList<String> apostadores;

    public Tipster() {
        apostadores = new ArrayList<>();
        postes = new HashMap<>();
//        postes = new ArrayList<>();
        dados = new Usuario();
    }

    public void salvar() {
        DatabaseReference reference = Import.getFirebase.getReference();
        reference
                .child(Constantes.firebase.child.USUARIO)
                .child(Constantes.firebase.child.TIPSTERS)
                .child(dados.getId())
//                .child(Constantes.firebase.child.DADOS)
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

    public HashMap<String, Post> getPostes() {
        return postes;
    }

    public void setPostes(HashMap<String, Post> postes) {
        this.postes = postes;
    }

    /*public List<Post> getPostes() {
        if (postes == null)
            postes = new ArrayList<>();
        return postes;
    }

    public void setPostes(ArrayList<Post> postes) {
        this.postes = postes;
    }*/

    public ArrayList<String> getApostadores() {
        if (apostadores == null)
            apostadores = new ArrayList<>();
        return apostadores;
    }

    public void setApostadores(ArrayList<String> apostadores) {
        this.apostadores = apostadores;
    }

    //endregion
}
