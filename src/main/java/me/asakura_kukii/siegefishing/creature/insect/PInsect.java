package me.asakura_kukii.siegefishing.creature.insect;

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
import me.asakura_kukii.siegefishing.collectable.PInsectCollectable;
import me.asakura_kukii.siegefishing.deco.PDeco;
import me.asakura_kukii.siegefishing.effect.PParticle;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.creature.fish.PFishEmotion;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PInsect extends PAbstractItem {

    public float spawnWeight = 1.0F;

    @JsonSerialize(using = PFileIdSerializer.class)
    @JsonDeserialize(using = PFileIdDeserializer.class)
    public PInsectCollectable collectable = null;

    public int detectCoolDown = 5;

    public float detectRadius = 8.0F;

    public float speedLimit = 0.01F;

    public PInsectLevel level = PInsectLevel.GREEN;

    public int totalGiftMin = 0;

    public int totalGiftMax = 0;

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

    public List<String> itemHint = new ArrayList<>();

    public List<String> fleeHint = new ArrayList<>();

    @JsonIgnore
    public static NamespacedKey attackKey = new NamespacedKey(SiegeFishing.pluginInstance, "attack");

    @JsonIgnore
    public static NamespacedKey healthKey = new NamespacedKey(SiegeFishing.pluginInstance, "health");

    @JsonIgnore
    public static NamespacedKey avoidanceKey = new NamespacedKey(SiegeFishing.pluginInstance, "avoidance");

    @JsonIgnore
    public static NamespacedKey emotionKey = new NamespacedKey(SiegeFishing.pluginInstance, "emotion");

    @JsonIgnore
    public static NamespacedKey starKey = new NamespacedKey(SiegeFishing.pluginInstance, "star");

    public static final float healthFactor = 0.5F;

    public static final float attackFactor = 0.15F;

    public static final float avoidanceFactor = 0.004F;

    @Override
    public void trigger(Player player, PTriggerType pTriggerType, PTriggerSubType pTriggerSubType, PTriggerSlot pTriggerSlot, ItemStack itemStack, long l) {

    }

    @JsonIgnore
    public ItemStack setInsectModifierList(ItemStack itemStack, float[] modifierList, int starCount) {
        if (itemStack == null) return null;
        if (modifierList.length != 3) return itemStack;
        ItemMeta iM = itemStack.getItemMeta();
        if (iM == null) iM = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        assert iM != null;
        String emotion = PFishEmotion.getEmotion();
        PersistentDataContainer pDC = iM.getPersistentDataContainer();
        pDC.set(healthKey, PersistentDataType.FLOAT, modifierList[0]);
        pDC.set(attackKey, PersistentDataType.FLOAT, modifierList[1]);
        pDC.set(avoidanceKey, PersistentDataType.FLOAT, modifierList[2]);
        pDC.set(emotionKey, PersistentDataType.STRING, emotion);
        pDC.set(starKey, PersistentDataType.INTEGER, starCount);
        List<String> loreList = new ArrayList<>();
        for (String lore : this.lore) {
            boolean hide = false;
            if (lore.contains("%level%")) lore = lore.replaceAll("%level%", level.colorString + level.displayName);
            if (lore.contains("%health%")) lore = lore.replaceAll("%health%", String.format("%.1f", modifierList[0]));
            if (lore.contains("%attack%")) lore = lore.replaceAll("%attack%", String.format("%.1f", modifierList[1]));
            if (lore.contains("%avoidance%")) lore = lore.replaceAll("%avoidance%", String.format("%.1f", modifierList[2] * 100));
            if (lore.contains("%emotion%")) lore = lore.replaceAll("%emotion%", emotion);
            if (lore.contains("%star%")) {
                if (starCount == 0) {
                    lore = lore.replaceAll("%star%", "☆");
                } else if (starCount == 10) {
                    lore = lore.replaceAll("%star%", "&c&l★".repeat(starCount));
                } else {
                    lore = lore.replaceAll("%star%", "★".repeat(starCount));
                }
            }
            if (lore.contains("%price%")) {
                lore = lore.replaceAll("%price%", String.format("%.1f", this.price));
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

    @JsonIgnore
    public static float[] getInsectModifierList(ItemStack itemStack) {
        float[] modifierList = new float[]{0, 0, 0};
        if (itemStack == null) return modifierList;
        ItemMeta iM = itemStack.getItemMeta();
        if (iM == null) iM = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        assert iM != null;
        PersistentDataContainer pDC = iM.getPersistentDataContainer();
        Float health = pDC.get(healthKey, PersistentDataType.FLOAT);
        modifierList[0] = health == null ? 80F : health;
        Float attack = pDC.get(attackKey, PersistentDataType.FLOAT);
        modifierList[1] = attack == null ? 20F : attack;
        Float avoidance = pDC.get(avoidanceKey, PersistentDataType.FLOAT);
        modifierList[2] = avoidance == null ? 0.1F : avoidance;
        return modifierList;
    }

    @JsonIgnore
    public static int getInsectStarCount(ItemStack itemStack) {
        ItemMeta iM = itemStack.getItemMeta();
        if (iM == null) iM = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        assert iM != null;
        PersistentDataContainer pDC = iM.getPersistentDataContainer();
        Integer starCount = pDC.get(starKey, PersistentDataType.INTEGER);
        starCount = starCount == null ? 0 : starCount;
        return starCount;
    }

    @Override
    public ItemStack finalizeGetItemStack(ItemStack itemStack) {
        ItemMeta iM = itemStack.getItemMeta();
        if (iM == null) Bukkit.getItemFactory().getItemMeta(itemStack.getType());

        int totalGift = PMath.ranInt(this.totalGiftMin, this.totalGiftMax);
        int giftPoint1 = PMath.ranInt(0, totalGift);
        int giftPoint2 = PMath.ranInt(0, totalGift);
        int attackGift = 0;
        int healthGift = 0;
        int avoidanceGift = 0;
        if (giftPoint1 < giftPoint2) {
            attackGift = giftPoint1;
            healthGift = giftPoint2 - giftPoint1;
            avoidanceGift = totalGift - giftPoint2;
        } else {
            attackGift = giftPoint2;
            healthGift = giftPoint1 - giftPoint2;
            avoidanceGift = totalGift - giftPoint1;
        }
        float health = 80F + healthGift * healthFactor;
        float attack = 20F + attackGift * attackFactor;
        float avoidance = 0.1F + avoidanceGift * avoidanceFactor;
        String emotion = PFishEmotion.getEmotion();
        assert iM != null;

        PersistentDataContainer pDC = iM.getPersistentDataContainer();
        pDC.set(healthKey, PersistentDataType.FLOAT, health);
        pDC.set(attackKey, PersistentDataType.FLOAT, attack);
        pDC.set(avoidanceKey, PersistentDataType.FLOAT, avoidance);
        pDC.set(emotionKey, PersistentDataType.STRING, emotion);
        pDC.set(starKey, PersistentDataType.INTEGER, 0);

        List<String> loreList = new ArrayList<>();
        for (String lore : this.lore) {
            boolean hide = false;
            if (lore.contains("%level%")) lore = lore.replaceAll("%level%", level.colorString + level.displayName);
            if (lore.contains("%health%")) lore = lore.replaceAll("%health%", String.format("%.1f", health));
            if (lore.contains("%attack%")) lore = lore.replaceAll("%attack%", String.format("%.1f", attack));
            if (lore.contains("%avoidance%")) lore = lore.replaceAll("%avoidance%", String.format("%.1f", avoidance * 100));
            if (lore.contains("%emotion%")) lore = lore.replaceAll("%emotion%", emotion);
            if (lore.contains("%star%")) {lore = lore.replaceAll("%star%", "☆");}
            if (lore.contains("%price%")) {
                lore = lore.replaceAll("%price%", String.format("%.1f", this.price));
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
