package com.tradetrack.cryptolist.dataBase;// Colors.java

import java.util.ArrayList;
import java.util.List;

public class DefinedColors {

    public static List<ColorItem> colorList = new ArrayList<>();

    static {
        colorList.add(new ColorItem("Cornflower Lilac", "#FFACAC"));
        colorList.add(new ColorItem("Dusty Pink", "#D28989"));
        colorList.add(new ColorItem("Grey Blue", "#6D96A3"));
        colorList.add(new ColorItem("Lavender", "#A989D2"));
        colorList.add(new ColorItem("Olive Green", "#B2D289"));
        colorList.add(new ColorItem("Vista Blue", "#89D2AF"));
        colorList.add(new ColorItem("Medium Orchid", "#B44AB6"));
    }

    public static class ColorItem {
        public String name;
        public String code;

        public ColorItem(String name, String code) {
            this.name = name;
            this.code = code;
        }
    }
}
