package me.asakura_kukii.siegefishing.creature.seafood;

import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.io.helper.PFileIdDeserializer;
import me.asakura_kukii.siegecore.io.helper.PFileIdSerializer;
import me.asakura_kukii.siegecore.item.PAbstractItem;
import me.asakura_kukii.siegecore.trigger.PTriggerSlot;
import me.asakura_kukii.siegecore.trigger.PTriggerSubType;
import me.asakura_kukii.siegecore.trigger.PTriggerType;
import me.asakura_kukii.siegecore.util.format.PFormat;
import me.asakura_kukii.siegefishing.collectable.PSeaFoodCollectable;
import me.asakura_kukii.siegefishing.deco.PDeco;
import me.asakura_kukii.siegefishing.effect.PParticle;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.creature.fish.PFishLevel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PSeaFood extends PAbstractItem {

    public float spawnWeight = 1.0F;

    @JsonSerialize(using = PFileIdSerializer.class)
    @JsonDeserialize(using = PFileIdDeserializer.class)
    public PSeaFoodCollectable collectable = null;

    public PFishLevel level = PFishLevel.SHRIMP;

    public int catchCount = 3;

    public int fleeRadius = 8;

    public int fleeCountDown = 200;

    public float decoBias = 0.0F;

    @JsonSerialize(using = PFileIdSerializer.class)
    @JsonDeserialize(using = PFileIdDeserializer.class)
    public PDeco deco = null;

    public float stillFrequency = 0.1F;

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> stillSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> stillParticle = new ArrayList<>();

    public float movingFrequency = 0.5F;

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> movingSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> movingParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> digSuccessParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> digSuccessSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> getSuccessParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> getSuccessSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> fleeParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> fleeSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> itemParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> itemSound = new ArrayList<>();

    public List<String> digSuccessfulHint = new ArrayList<>();

    public List<String> getSuccessfulHint = new ArrayList<>();

    public List<String> fleeHint = new ArrayList<>();

    public float speed = 0.2F;

    @Override
    public void trigger(Player player, PTriggerType pTriggerType, PTriggerSubType pTriggerSubType, PTriggerSlot pTriggerSlot, ItemStack itemStack, long l) {

    }

    @Override
    public ItemStack finalizeGetItemStack(ItemStack itemStack) {
        ItemMeta iM = itemStack.getItemMeta();
        if (iM == null) Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        List<String> loreList = new ArrayList<>();
        for (String lore : this.lore) {
            boolean hide = false;
            if (lore.contains("%level%")) lore = lore.replaceAll("%level%", level.colorString + level.displayName);
            if (lore.contains("%price%")) {
                lore = lore.replaceAll("%price%", String.format("%.1f", this.price));
                if (this.price < 0) hide = true;
            }
            if (!hide) {
                loreList.add(lore);
            }
        }
        assert iM != null;
        iM.setDisplayName(PFormat.format(this.level.colorString) + iM.getDisplayName());
        iM.setLore(PFormat.format(loreList));
        itemStack.setItemMeta(iM);
        return itemStack;
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
