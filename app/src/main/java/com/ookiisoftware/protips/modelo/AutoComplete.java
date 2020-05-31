package com.ookiisoftware.protips.modelo;

import com.ookiisoftware.protips.auxiliar.Constantes;
import com.ookiisoftware.protips.auxiliar.Import;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class AutoComplete {

    private HashMap<String, String> esportes;
    private HashMap<String, String> linhas;
    private HashMap<String, String> campeonatos;

    //region Metodos

    public static void add(String categoria, String value) {
        String esportes = Constantes.firebase.child.ESPORTES;
        String linhas = Constantes.firebase.child.LINHAS;
        String campeonatos = Constantes.firebase.child.CAMPEONATOS;

        if (value == null || value.isEmpty())
            return;

        if (categoria.equals(esportes) || categoria.equals(linhas) || categoria.equals(campeonatos)) {
            Import.getFirebase.getReference()
                    .child(Constantes.firebase.child.AUTO_COMPLETE)
                    .child(categoria)
                    .child(value)
                    .setValue(value);
        }
    }

    //endregion

    //region get set

    public HashMap<String, String> getEsportes() {
        if (esportes == null)
            esportes = new LinkedHashMap<>();
        return esportes;
    }

    public void setEsportes(HashMap<String, String> esportes) {
        this.esportes = esportes;
    }

    public HashMap<String, String> getLinhas() {
        if (linhas == null)
            linhas = new LinkedHashMap<>();
        return linhas;
    }

    public void setLinhas(HashMap<String, String> linhas) {
        this.linhas = linhas;
    }

    public HashMap<String, String> getCampeonatos() {
        if (campeonatos == null)
            campeonatos = new LinkedHashMap<>();
        return campeonatos;
    }

    public void setCampeonatos(HashMap<String, String> campeonatos) {
        this.campeonatos = campeonatos;
    }

    //endregion

    public static class sortByNome implements Comparator<String> {
        private boolean reverse;
        public sortByNome() {}
        public sortByNome(boolean reverse) {
            this.reverse = reverse;
        }

        public int compare(String left, String right) {
            if (reverse) {
                return right.compareTo(left);
            } else {
                return left.compareTo(right);
            }
        }
    }

}
