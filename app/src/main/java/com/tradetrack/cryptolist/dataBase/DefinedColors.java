package com.tradetrack.cryptolist.dataBase;// Colors.java

import java.util.ArrayList;
import java.util.List;

public class DefinedColors {

    public static List<ColorItem> colorList = new ArrayList<>();

    static {
        colorList.add(new ColorItem("Light Yellow", "#FFFFE0"));
        colorList.add(new ColorItem("Pale Peach", "#FDF5E6"));
        colorList.add(new ColorItem("Light Blue", "#E0FFFF"));
        colorList.add(new ColorItem("Mint Green", "#CCE5FF"));
        colorList.add(new ColorItem("Cream", "#FFFDD0"));
        colorList.add(new ColorItem("Lavender Blush", "#FFF0F5"));
        colorList.add(new ColorItem("Light Gray", "#EEEEEE"));
        colorList.add(new ColorItem("Light Sky Blue", "#D3F7FF"));
        colorList.add(new ColorItem("Beige", "#F5F5DC"));
        colorList.add(new ColorItem("Pale Goldenrod", "#FAFAD2"));
        colorList.add(new ColorItem("Ivory", "#FFFFF0"));
        colorList.add(new ColorItem("White Smoke", "#F5F5F5"));
        colorList.add(new ColorItem("Honeydew", "#F0FFF0"));
        colorList.add(new ColorItem("Light Green", "#90EE90"));
        colorList.add(new ColorItem("Light Coral", "#F08080"));
        colorList.add(new ColorItem("Light Salmon", "#FFA07A"));
        colorList.add(new ColorItem("Antique White", "#FAEBD7"));
        colorList.add(new ColorItem("Thistle", "#D8BFD8"));
        colorList.add(new ColorItem("Light Goldenrod Yellow", "#FAFAD2"));
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
