package com.ookiisoftware.protips.modelo;

import java.util.ArrayList;

public class Esporte {

    public enum Esporte1 {
        Esporte, Futebol, Volei, Boxe, Galgos
    }
    public enum Mercado {
        Mercado, Escanteios , ML, Empates, Live
    }

    private String nome;
    private ArrayList<String> mercados;


    public Esporte() {
        mercados = new ArrayList<>();
    }



    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ArrayList<String> getMercados() {
        if (mercados == null)
            mercados = new ArrayList<>();
        return mercados;
    }

    public void setMercados(ArrayList<String> mercados) {
        this.mercados = mercados;
    }
}
