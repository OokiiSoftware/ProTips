package com.ookiisoftware.protips.modelo;

import java.util.Comparator;
import java.util.HashMap;

public class Esporte {

    private HashMap<String, HashMap<String, String>> esportes;

    public HashMap<String, HashMap<String, String>> getEsportes() {
        return esportes;
    }

    public void setEsportes(HashMap<String, HashMap<String, String>> esportes) {
        this.esportes = esportes;
    }

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
