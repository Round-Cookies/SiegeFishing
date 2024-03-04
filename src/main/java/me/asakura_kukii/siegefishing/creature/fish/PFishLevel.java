package me.asakura_kukii.siegefishing.creature.fish;

import me.asakura_kukii.siegecore.util.format.PFormat;

public enum PFishLevel {
    TRASH(PFormat.format("&4&l"), "嗯？", 0, 10),
    ALGAE(PFormat.format("&a&l"), "藻类", 0, 10),
    SHRIMP(PFormat.format("&3&l"), "虾类", 1, 20),
    SHELL(PFormat.format("&9&l"), "贝类", 2, 30),
    CRAB(PFormat.format("&5&l"), "蟹类", 3, 40),
    MEAT(PFormat.format("&e&l"), "肉类", 4, 50),
    ALL(PFormat.format("&d&l"), "软体类", 4, 50);

    PFishLevel(String colorString, String displayName, int level, int experience) {
        this.colorString = colorString;
        this.displayName = displayName;
        this.level = level;
        this.experience = experience;
    }

    public String colorString = "";
    public String displayName = "";
    public int level = 1;
    public int experience = 10;
}
