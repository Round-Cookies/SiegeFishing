package me.asakura_kukii.siegefishing.creature.fish;

import me.asakura_kukii.lib.jackson.annotation.JsonIgnore;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.io.helper.PFileIdDeserializer;
import me.asakura_kukii.siegecore.io.helper.PFileIdSerializer;
import me.asakura_kukii.siegecore.item.PAbstractItem;
import me.asakura_kukii.siegecore.trigger.PTriggerSlot;
import me.asakura_kukii.siegecore.trigger.PTriggerSubType;
import me.asakura_kukii.siegecore.trigger.PTriggerType;
import me.asakura_kukii.siegecore.util.format.PFormat;
import me.asakura_kukii.siegecore.util.math.PMath;
import me.asakura_kukii.siegefishing.SiegeFishing;
import me.asakura_kukii.siegefishing.collectable.PFishCollectable;
import me.asakura_kukii.siegefishing.deco.PDeco;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class PFish extends PAbstractItem {

    public float spawnWeight = 1.0F;

    @JsonSerialize(using = PFileIdSerializer.class)
    @JsonDeserialize(using = PFileIdDeserializer.class)
    public PFishCollectable collectable = null;

    @JsonSerialize(using = PFileIdSerializer.class)
    @JsonDeserialize(using = PFileIdDeserializer.class)
    public PDeco headDeco = null;

    @JsonSerialize(using = PFileIdSerializer.class)
    @JsonDeserialize(using = PFileIdDeserializer.class)
    public PDeco bodyDeco = null;

    @JsonSerialize(using = PFileIdSerializer.class)
    @JsonDeserialize(using = PFileIdDeserializer.class)
    public PDeco tailDeco = null;

    public float headMouthBias = 0.3F;

    public float headDecoBias = 0.3F;

    public float bodyDecoBias = -0.3F;

    public float tailDecoBias = -0.3F;

    public float bodyDecoLength = 0.5F;

    public float diffMultiplierMin = 1.0F;

    public float diffMultiplierMax = 2.0F;

    public PFishLevel level = PFishLevel.ALGAE;

    public float scale = 0.75F;

    public float weight = 30.0F;

    public float health = 30.0F;

    public float strength = 1F / 20F / 60F;

    public float period = 1.0F;

    public float speed = 1.0F;

    @JsonIgnore
    public static NamespacedKey diffKey = new NamespacedKey(SiegeFishing.pluginInstance, "diff");

    @JsonIgnore
    public static NamespacedKey emotionKey = new NamespacedKey(SiegeFishing.pluginInstance, "emotion");

    @Override
    public void trigger(Player player, PTriggerType pTriggerType, PTriggerSubType pTriggerSubType, PTriggerSlot pTriggerSlot, ItemStack itemStack, long hTT) {
    }

    @JsonIgnore
    public ItemStack getItemStack(int amount, float diff, String emotion) {
        ItemStack iS = this.getItemStack(amount);
        if (iS == null) return null;
        ItemMeta iM = iS.getItemMeta();
        if (iM == null) iM = Bukkit.getItemFactory().getItemMeta(iS.getType());
        assert iM != null;
        PersistentDataContainer pDC = iM.getPersistentDataContainer();
        pDC.set(diffKey, PersistentDataType.FLOAT, diff);
        pDC.set(emotionKey, PersistentDataType.STRING, emotion);

        List<String> loreList = new ArrayList<>();
        for (String lore : this.lore) {
            boolean hide = false;
            if (lore.contains("%level%")) lore = lore.replaceAll("%level%", this.level.colorString + this.level.displayName);
            if (lore.contains("%weight%")) lore = lore.replaceAll("%weight%", String.format("%.2f", this.weight * diff));
            if (lore.contains("%emotion%")) lore = lore.replaceAll("%emotion%", PFishEmotion.getEmotion());
            if (lore.contains("%price%")) {
                lore = lore.replaceAll("%price%", String.format("%.2f", this.price * diff));
                if (this.price < 0) hide = true;
            }
            if (!hide) {
                loreList.add(lore);
            }
        }
        iM.setDisplayName(PFormat.format(this.level.colorString) + iM.getDisplayName());
        iM.setLore(PFormat.format(loreList));
        iS.setItemMeta(iM);
        return iS;
    }

    @Override
    public ItemStack finalizeGetItemStack(ItemStack itemStack) {
        if (itemStack == null) return null;
        ItemMeta iM = itemStack.getItemMeta();
        if (iM == null) iM = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        assert iM != null;
        float diff = PMath.ranFloat(this.diffMultiplierMin, this.diffMultiplierMax);
        PersistentDataContainer pDC = iM.getPersistentDataContainer();
        pDC.set(diffKey, PersistentDataType.FLOAT, diff);
        pDC.set(emotionKey, PersistentDataType.STRING, PFishEmotion.getEmotion());

        List<String> loreList = new ArrayList<>();
        for (String lore : this.lore) {
            boolean hide = false;
            if (lore.contains("%level%")) lore = lore.replaceAll("%level%", this.level.colorString + this.level.displayName);
            if (lore.contains("%weight%")) lore = lore.replaceAll("%weight%", String.format("%.2f", this.weight * diff));
            if (lore.contains("%emotion%")) lore = lore.replaceAll("%emotion%", PFishEmotion.getEmotion());
            if (lore.contains("%price%")) {
                lore = lore.replaceAll("%price%", String.format("%.2f", this.price * diff));
                if (this.price < 0) hide = true;
            }
            if (!hide) {
                loreList.add(lore);
            }
        }
        iM.setDisplayName(PFormat.format(this.level.colorString) + iM.getDisplayName());
        iM.setLore(PFormat.format(loreList));

        itemStack.setItemMeta(iM);
        return itemStack;
    }

    @Override
    public void finalizeDeserialization() {

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
