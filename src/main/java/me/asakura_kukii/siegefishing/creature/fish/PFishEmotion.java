package me.asakura_kukii.siegefishing.creature.fish;

import me.asakura_kukii.siegecore.io.PFile;
import me.asakura_kukii.siegecore.util.math.PMath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PFishEmotion extends PFile {

    public static List<String> staticEmotionList = new ArrayList<>();

    public List<String> emotionList = new ArrayList<>();

    public static String getEmotion() {
        if (staticEmotionList.isEmpty()) return "";
        return staticEmotionList.get(PMath.ranIndex(staticEmotionList.size()));
    }

    @Override
    public void finalizeDeserialization() throws IOException {
        staticEmotionList.addAll(this.emotionList);
    }

    @Override
    public void defaultValue() {

    }

    @Override
    public void load() {

    }

    @Override
    public void unload() {

    }
}
