package me.asakura_kukii.siegefishing.rod;

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
import me.asakura_kukii.siegecore.io.helper.PVectorDeserializer;
import me.asakura_kukii.siegecore.io.helper.PVectorSerializer;
import me.asakura_kukii.siegefishing.creature.fish.skill.PFishSkillGun;
import me.asakura_kukii.siegefishing.deco.PDeco;
import me.asakura_kukii.siegefishing.effect.PParticle;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.creature.fish.PFishSession;
import me.asakura_kukii.siegefishing.creature.fish.skill.PAbstractFishSkill;
import me.asakura_kukii.siegefishing.map.PFishChunk;
import me.asakura_kukii.siegefishing.map.PFishMap;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import me.asakura_kukii.siegefishing.player.PFishPlayerLevel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PFishRod extends PAbstractItem {

    public float speed = 1.0F;

    public float wireLength = 10F;

    @JsonSerialize(using = PFileIdSerializer.class)
    @JsonDeserialize(using = PFileIdDeserializer.class)
    public PDeco floatDeco = null;

    public float floatDecoScale = 1.0F;

    public float pullSpeed = 0.05F;

    public float strengthReflectRatio = 0.5F;

    public PFishPlayerLevel level = PFishPlayerLevel.BEGINNER;

    public float health = 30.0F;

    public float strength = 0.25F;

    @JsonSerialize(using = PVectorSerializer.class)
    @JsonDeserialize(using = PVectorDeserializer.class)
    public PVector bias = new PVector();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> castSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> splashSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> operateSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> stuckSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> fishAmbientSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> biteSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> snapSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> retrieveSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> itemSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> castParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> splashParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> bubbleParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> ambientParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> fishAmbientParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> biteParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> snapParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> retrieveParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> itemParticle = new ArrayList<>();

    public List<String> castHint = new ArrayList<>();

    public List<String> cutHint = new ArrayList<>();

    public List<String> snapHint = new ArrayList<>();

    public List<String> fishSnapHint = new ArrayList<>();

    public List<String> fishEscapeHint = new ArrayList<>();

    public List<String> fishRetrieveHint = new ArrayList<>();

    public PAbstractFishSkill skill = null;

    @JsonIgnore
    public float getLeveledStrength(Player p) {
        PType pT = PType.getPType(PFishPlayer.class);
        if (pT == null) return 0;
        PFishPlayer fishPlayer = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
        if (fishPlayer == null) return 0;
        int level = PMath.min(fishPlayer.level.level, this.level.level);
        return (float) (this.strength * Math.pow(2, level));
    }

    @JsonIgnore
    public float getLeveledHealth(Player p) {
        PType pT = PType.getPType(PFishPlayer.class);
        if (pT == null) return 0;
        PFishPlayer fishPlayer = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
        if (fishPlayer == null) return 0;
        int level = PMath.min(fishPlayer.level.level, this.level.level);
        return (float) (this.health * Math.pow(2, level));
    }

    @JsonIgnore
    public float getDesignedStrength() {
        return (float) (this.strength * Math.pow(2, this.level.level));
    }

    @JsonIgnore
    public float getDesignedHealth() {
        return (float) (this.health * Math.pow(2, this.level.level));
    }

    @Override
    public void trigger(Player player, PTriggerType pTriggerType, PTriggerSubType pTriggerSubType, PTriggerSlot pTriggerSlot, ItemStack itemStack, long hTT) {
        PType pT = PType.getPType(PFishPlayer.class);
        if (pT == null) return;
        PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(player.getUniqueId().toString());
        if (pFP == null) return;
        PType pT2 = PType.getPType(PFishMap.class);
        if (pT2 == null) return;
        PFishMap pFishMap = (PFishMap) pT2.getPFile(player.getWorld().getName());
        if (pFishMap == null) return;
        PFishChunk pFishChunk = pFishMap.getFishChunk(pFP.getLocation());
        if (pFishChunk == null) return;

        if (pTriggerType == PTriggerType.LEFT && pTriggerSubType == PTriggerSubType.INIT) {
            if (pFP.session == null) {
                pFP.session = new PFishSession(pFishMap, pFishChunk, pFP, player, this);
                pFP.session.castRod();
            } else {
                pFP.session.cutWire();
            }
        }
        if (pTriggerType == PTriggerType.RIGHT && pTriggerSubType == PTriggerSubType.HOLD) {
            if (pFP.session != null) pFP.session.pullWire();
        }
        if (pTriggerType == PTriggerType.STOCK && pTriggerSubType == PTriggerSubType.INIT) {
            if (pFP.session != null) pFP.session.cutWire();
        }
        if (pTriggerType == PTriggerType.SWAP && pTriggerSubType == PTriggerSubType.INIT) {
            if (pFP.session != null) pFP.session.castSkill();
        }
        if (pTriggerType == PTriggerType.SWAP && pTriggerSubType == PTriggerSubType.HOLD) {
            if (this.skill instanceof PFishSkillGun) return;
            if (pFP.session != null) pFP.session.castSkill();
        }
    }

    @Override
    public ItemStack finalizeGetItemStack(ItemStack itemStack) {
        ItemMeta iM = itemStack.getItemMeta();
        if (iM == null) Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        List<String> loreList = new ArrayList<>();
        for (String lore : this.lore) {
            if (lore.contains("%level%")) lore = lore.replaceAll("%level%", this.level.colorString + this.level.displayName);
            if (lore.contains("%strength%")) lore = lore.replaceAll("%strength%", String.format("%.1f", (double) getDesignedStrength()));
            if (lore.contains("%health%")) lore = lore.replaceAll("%health%", String.format("%.1f", (double) getDesignedHealth()));
            if (lore.contains("%bounce%")) lore = lore.replaceAll("%bounce%", String.format("%.1f", (double) (1 - this.strengthReflectRatio) * 100));
            loreList.add(lore);
        }
        assert iM != null;
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
