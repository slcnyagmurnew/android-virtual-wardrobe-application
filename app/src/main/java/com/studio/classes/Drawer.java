package com.studio.classes;

import java.util.ArrayList;

public class Drawer {
    private String drawerName;
    private ArrayList<Clothes> clothesList;

    public String getDrawerName() {
        return drawerName;
    }

    public void setDrawerName(String drawerName) {
        this.drawerName = drawerName;
    }

    public ArrayList<Clothes> getClothesList() {
        return clothesList;
    }

    public void setClothesList(ArrayList<Clothes> clothesList) {
        this.clothesList = clothesList;
    }

    public Drawer() {}

    public Drawer(String drawerName) {
        this.drawerName = drawerName;
    }
}
