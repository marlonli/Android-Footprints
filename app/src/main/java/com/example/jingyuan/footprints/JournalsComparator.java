package com.example.jingyuan.footprints;

import java.util.Comparator;

/**
 * Created by jingyuan on 12/6/17.
 */

public class JournalsComparator implements Comparator<Journal> {
    @Override
    public int compare(Journal j1, Journal j2) {
        if (j1.getDateTimeLong() > j2.getDateTimeLong()) return -1;
        else if (j1.getDateTimeLong() < j2.getDateTimeLong()) return 1;
        else return 0;
    }
}
