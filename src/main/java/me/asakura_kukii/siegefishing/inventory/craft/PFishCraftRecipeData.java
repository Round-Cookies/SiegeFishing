package me.asakura_kukii.siegefishing.inventory.craft;

import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.io.helper.PFileIdDeserializer;
import me.asakura_kukii.siegecore.io.helper.PFileIdKeyDeserializer;
import me.asakura_kukii.siegecore.io.helper.PFileIdKeySerializer;
import me.asakura_kukii.siegecore.io.helper.PFileIdSerializer;
import me.asakura_kukii.siegecore.item.PAbstractItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PFishCraftRecipeData {
    @JsonSerialize(keyUsing = PFileIdKeySerializer.class, contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(keyUsing = PFileIdKeyDeserializer.class, contentUsing = PFileIdDeserializer.class)
    public HashMap<PAbstractItem, PAbstractItem> recipeMap = new HashMap<>();

    public int count = 1;
}
