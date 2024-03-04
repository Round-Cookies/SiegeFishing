package me.asakura_kukii.siegefishing.bait;

import me.asakura_kukii.lib.jackson.annotation.JsonIgnore;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.io.helper.PFileIdDeserializer;
import me.asakura_kukii.siegecore.io.helper.PFileIdSerializer;
import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.item.PAbstractItem;
import me.asakura_kukii.siegecore.trigger.PTriggerSlot;
import me.asakura_kukii.siegecore.trigger.PTriggerSubType;
import me.asakura_kukii.siegecore.trigger.PTriggerType;
import me.asakura_kukii.siegecore.util.format.PFormat;
import me.asakura_kukii.siegecore.util.math.PMath;
import me.asakura_kukii.siegecore.util.math.PVector;
import me.asakura_kukii.siegefishing.SiegeFishing;
import me.asakura_kukii.siegefishing.deco.PDeco;
import me.asakura_kukii.siegefishing.effect.PParticle;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.creature.fish.PFishEntity;
import me.asakura_kukii.siegefishing.creature.fish.PFishLevel;
import me.asakura_kukii.siegefishing.map.PFishMap;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class PFishBait extends PAbstractItem {

    public PFishLevel level = PFishLevel.ALGAE;

    public List<PFishLevel> attractLevelList = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> equipSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> equipParticle = new ArrayList<>();

    public List<String> equipHint = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public PDeco baitDeco = null;

    public float radius = 1.0F;

    public float period = 20.0F;

    public float speed = 0.15F;

    public int duration = 100;

    public float biteDistance = 0.25F;

    public float healthBuffLimit = 0.5F;

    public float speedBuffLimit = 0.5F;

    public float strengthBuffLimit = 0.5F;

    public float radiusBuffLimit = 0.5F;

    @JsonIgnore
    public static NamespacedKey healthKey = new NamespacedKey(SiegeFishing.pluginInstance, "health");

    @JsonIgnore
    public static NamespacedKey speedKey = new NamespacedKey(SiegeFishing.pluginInstance, "speed");

    @JsonIgnore
    public static NamespacedKey strengthKey = new NamespacedKey(SiegeFishing.pluginInstance, "strength");

    @JsonIgnore
    public static NamespacedKey radiusKey = new NamespacedKey(SiegeFishing.pluginInstance, "radius");


    public PFishEntity attract(PFishMap fishMap, PVector target, ItemStack baitItemStack) {

        PFishEntity bite = null;
        float[] modifierList = PFishBait.getBaitModifierList(baitItemStack);
        for (PFishEntity fishEntity : fishMap.getFishEntityList(target, 1)) {
            if (fishEntity.getSession() != null) continue;
            if (!this.attractLevelList.contains(fishEntity.getFish().level)) continue;
            PVector location = fishEntity.getLocation();
            if (PMath.abs(location.x - target.x) > radius * (1 + modifierList[3])) continue;
            if (PMath.abs(location.z - target.z) > radius * (1 + modifierList[3])) continue;
            fishEntity.attract(target, this);
            if (location.distanceSquared(target) <= biteDistance * biteDistance) bite = fishEntity;
        }
        return bite;
    }

    @Override
    public void trigger(Player player, PTriggerType pTriggerType, PTriggerSubType pTriggerSubType, PTriggerSlot pTriggerSlot, ItemStack itemStack, long hTT) {
        if (pTriggerType == PTriggerType.RIGHT && pTriggerSubType == PTriggerSubType.INIT) {
            // get fish player
            PFishPlayer pFP = (PFishPlayer) PType.getPType(PFishPlayer.class).getPFileSafely(player.getUniqueId().toString());
            // safety check
            if (pFP == null || pFP.getPlayer() == null || pFP.getPlayer() != player || !pFP.getPlayer().isOnline() || pFP.getPlayer().isDead()) return;
            // not available if in fish session
            if (pFP.session != null) return;
            // get previous lure
            ItemStack previousBaitItemStack = pFP.baitItemStack;
            // set current lure
            pFP.baitItemStack = itemStack.clone();
            pFP.baitItemStack.setAmount(1);
            pFP.bait = this;
            // reduce current lure item stack amount
            if (itemStack.getAmount() > 1) {
                itemStack.setAmount(itemStack.getAmount() - 1);
                player.getInventory().setItemInMainHand(itemStack);
            } else {
                player.getInventory().setItemInMainHand(null);
            }
            // increment previous lure item stack amount
            if (previousBaitItemStack != null) {
                pFP.giveItemStack(previousBaitItemStack);
            }
            // play effects
            pFP.sendHint(equipHint);
            for (PSound pS : this.equipSound) pS.play(player, PVector.fromLocation(player.getLocation()));
            for (PParticle pP : this.equipParticle) pP.spawn(player, PVector.fromLocation(player.getLocation()));
        }
    }

    @JsonIgnore
    public static float[] getBaitModifierList(ItemStack itemStack) {
        float[] modifierList = new float[]{0, 0, 0, 0};
        if (itemStack == null) return modifierList;
        ItemMeta iM = itemStack.getItemMeta();
        if (iM == null) iM = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        assert iM != null;
        PersistentDataContainer pDC = iM.getPersistentDataContainer();
        Float health = pDC.get(healthKey, PersistentDataType.FLOAT);
        modifierList[0] = health == null ? 0 : health;
        Float speed = pDC.get(speedKey, PersistentDataType.FLOAT);
        modifierList[1] = speed == null ? 0 : speed;
        Float strength = pDC.get(strengthKey, PersistentDataType.FLOAT);
        modifierList[2] = strength == null ? 0 : strength;
        Float radius = pDC.get(radiusKey, PersistentDataType.FLOAT);
        modifierList[3] = radius == null ? 0 : radius;
        return modifierList;
    }

    @JsonIgnore
    public ItemStack setBaitModifierList(ItemStack itemStack, float[] modifierList) {
        if (itemStack == null) return null;
        if (modifierList.length != 4) return null;
        ItemMeta iM = itemStack.getItemMeta();
        if (iM == null) iM = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        assert iM != null;
        PersistentDataContainer pDC = iM.getPersistentDataContainer();
        pDC.set(healthKey, PersistentDataType.FLOAT, modifierList[0]);
        pDC.set(speedKey, PersistentDataType.FLOAT, modifierList[1]);
        pDC.set(strengthKey, PersistentDataType.FLOAT, modifierList[2]);
        pDC.set(radiusKey, PersistentDataType.FLOAT, modifierList[3]);

        List<String> loreList = new ArrayList<>();
        for (String lore : this.lore) {
            boolean hide = false;
            if (lore.contains("%level%")) lore = lore.replaceAll("%level%", this.level.colorString + this.level.displayName);
            if (lore.contains("%health%")) {
                lore = lore.replaceAll("%health%", "-" + String.format("%.1f", (double) modifierList[0] * 100) + "%");
                if (modifierList[0] == 0) hide = true;
            }
            if (lore.contains("%speed%")) {
                lore = lore.replaceAll("%speed%", "-" + String.format("%.1f", (double) modifierList[1] * 100) + "%");
                if (modifierList[1] == 0) hide = true;
            }
            if (lore.contains("%strength%")) {
                lore = lore.replaceAll("%strength%", "-" + String.format("%.1f", (double) modifierList[2] * 100) + "%");
                if (modifierList[2] == 0) hide = true;
            }
            if (lore.contains("%radius%")) {
                if (modifierList[3] == 0) {
                    lore = lore.replaceAll("%radius%", "");
                } else {
                    lore = lore.replaceAll("%radius%", "+" + String.format("%.1f", (double) modifierList[3] * 100) + "%");
                }
            }
            if (lore.contains("%radius_base%")) {
                lore = lore.replaceAll("%radius_base%", String.format("%.1f", this.radius));
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
    public ItemStack finalizeGetItemStack(ItemStack itemStack) {
        if (itemStack == null) return null;
        ItemMeta iM = itemStack.getItemMeta();
        if (iM == null) iM = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        assert iM != null;
        PersistentDataContainer pDC = iM.getPersistentDataContainer();
        pDC.set(healthKey, PersistentDataType.FLOAT, 0F);
        pDC.set(speedKey, PersistentDataType.FLOAT, 0F);
        pDC.set(strengthKey, PersistentDataType.FLOAT, 0F);
        pDC.set(radiusKey, PersistentDataType.FLOAT, 0F);

        List<String> loreList = new ArrayList<>();
        for (String lore : this.lore) {
            boolean hide = false;
            if (lore.contains("%level%")) lore = lore.replaceAll("%level%", this.level.colorString + this.level.displayName);
            if (lore.contains("%health%")) {
                lore = lore.replaceAll("%health%", "");
                hide = true;
            }
            if (lore.contains("%speed%")) {
                lore = lore.replaceAll("%speed%", "");
                hide = true;
            }
            if (lore.contains("%strength%")) {
                lore = lore.replaceAll("%strength%", "");
                hide = true;
            }
            if (lore.contains("%radius%")) {
                lore = lore.replaceAll("%radius%", "");
            }
            if (lore.contains("%radius_base%")) {
                lore = lore.replaceAll("%radius_base%", String.format("%.1f", this.radius));
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