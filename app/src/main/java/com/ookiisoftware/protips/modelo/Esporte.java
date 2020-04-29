package com.ookiisoftware.protips.modelo;

import java.util.HashMap;

public class Esporte {

    private String nome;
    private HashMap<String,String> mercados;


    public Esporte() {
        mercados = new HashMap<>();
    }



    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public HashMap<String,String> getMercados() {
        if (mercados == null)
            mercados = new HashMap<>();
        return mercados;
    }

    public void setMercados(HashMap<String,String> mercados) {
        this.mercados = mercados;
    }

}
