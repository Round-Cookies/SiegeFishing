package me.asakura_kukii.siegefishing.map;

import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.io.PFile;
import me.asakura_kukii.siegecore.io.helper.PFileIdDeserializer;
import me.asakura_kukii.siegecore.io.helper.PFileIdSerializer;
import me.asakura_kukii.siegecore.util.math.PMath;
import me.asakura_kukii.siegefishing.collectable.PRegionCollectable;
import me.asakura_kukii.siegefishing.creature.fish.PFish;
import org.bukkit.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PFishRegion extends PFile {

    @JsonSerialize(using = PFileIdSerializer.class)
    @JsonDeserialize(using = PFileIdDeserializer.class)
    public PRegionCollectable collectable = null;

    public String name = "";

    public String daySuffix = "";

    public String nightSuffix = "";

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PFish> fishDay = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PFish> fishNight = new ArrayList<>();

    public PFish spawnFish(World world) {
        boolean flagDay = world.getTime() < 12000;
        if (flagDay) {
            if (this.fishDay.isEmpty()) return null;
            List<Float> weightList = new ArrayList<>();
            for (PFish fish : this.fishDay) weightList.add(fish.spawnWeight);
            return this.fishDay.get(PMath.ranIndexWeighted(weightList));
        } else {
            if (this.fishNight.isEmpty()) return null;
            List<Float> weightList = new ArrayList<>();
            for (PFish fish : this.fishNight) weightList.add(fish.spawnWeight);
            return this.fishNight.get(PMath.ranIndexWeighted(weightList));
        }
    }

    @Override
    public void finalizeDeserialization() throws IOException {

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
