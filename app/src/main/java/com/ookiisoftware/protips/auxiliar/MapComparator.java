package com.ookiisoftware.protips.auxiliar;

import com.ookiisoftware.protips.modelo.Post;

import java.util.Comparator;
import java.util.HashMap;

public class MapComparator implements Comparator<HashMap<String, Post>> {
    private final String key;
//    private final String order;

    public MapComparator(String key) {
        this.key = key;
//        this.order = order;
    }

    public int compare(HashMap<String, Post> first, HashMap<String, Post> second) {
        // TODO: Null checking, both for maps and values
        Post itemF = first.get(key);
        Post itemS = second.get(key);

        String firstValue = null;
        if (itemF != null)
            firstValue = itemF.getData();

        String secondValue = null;
        if (itemS != null)
            secondValue = itemS.getData();

        assert firstValue != null;
        assert secondValue != null;
        return firstValue.compareTo(secondValue);
    }
}