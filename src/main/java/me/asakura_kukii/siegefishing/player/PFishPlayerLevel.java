package me.asakura_kukii.siegefishing.player;

import me.asakura_kukii.siegecore.util.format.PFormat;

public enum PFishPlayerLevel {
    BEGINNER(PFormat.format("&a&l"), "新手级", 0, 100),
    AMATEUR(PFormat.format("&3&l"), "业余级", 1, 400),
    VETERAN(PFormat.format("&9&l"), "老鸟级", 2, 900),
    EXPERT(PFormat.format("&5&l"), "专业级", 3, 1600),
    LEGENDARY(PFormat.format("&e&l"), "老嗨级", 4, 2500),
    WEIRD(PFormat.format("&c&l"), "啊？？", 4, 2500);

    PFishPlayerLevel(String colorString, String displayName, int level, int experience) {
        this.colorString = colorString;
        this.displayName = displayName;
        this.level = level;
        this.experience = experience;
    }

    public String colorString = "";
    public String displayName = "";
    public int level = 1;
    public int experience = 0;
}
