package me.asakura_kukii.siegefishing.creature.insect;

import me.asakura_kukii.siegecore.util.format.PFormat;

public enum PInsectLevel {
    GREEN(PFormat.format("&a&l"), "萃系", 0),
    BLUE(PFormat.format("&b&l"), "苍系", 1),
    RED(PFormat.format("&c&l"), "绯系", 2);

    PInsectLevel(String colorString, String displayName, int level) {
        this.colorString = colorString;
        this.displayName = displayName;
        this.level = level;
    }

    public String colorString = "";
    public String displayName = "";
    public int level = 0;
}
