package com.example.jingyuan.footprints;

import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by jingyuan on 12/6/17.
 */

public class MapComparator implements Comparator<HashMap<String, String>> {
    private final String key;
    private final String order;

    public MapComparator(String key, String order)
    {
        this.key = key;
        this.order = order;
    }

    @Override
    public int compare(HashMap<String, String> first,
                       HashMap<String, String> second)
    {
        // TODO: Null checking, both for maps and values
        String firstValue = first.get(key);
        String secondValue = second.get(key);
        if(this.order.toLowerCase().contentEquals("asc"))
        {
            return firstValue.compareTo(secondValue);
        }else{
            return secondValue.compareTo(firstValue);
        }

    }
}
