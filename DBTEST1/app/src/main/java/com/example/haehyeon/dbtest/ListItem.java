package com.example.haehyeon.dbtest;

import com.skp.Tmap.TMapPOIItem;

public class ListItem {
    public TMapPOIItem item;
    public String id;
    @Override
    public String toString() {
        return item.getPOIName();
    }
}
