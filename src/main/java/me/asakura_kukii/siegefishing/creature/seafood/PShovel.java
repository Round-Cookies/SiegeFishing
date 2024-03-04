package me.asakura_kukii.siegefishing.creature.seafood;

import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.effect.PParticle;
import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.io.helper.PFileIdDeserializer;
import me.asakura_kukii.siegecore.io.helper.PFileIdSerializer;
import me.asakura_kukii.siegecore.item.PAbstractItem;
import me.asakura_kukii.siegecore.trigger.PTriggerSlot;
import me.asakura_kukii.siegecore.trigger.PTriggerSubType;
import me.asakura_kukii.siegecore.trigger.PTriggerType;
import me.asakura_kukii.siegecore.util.math.PVector;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.map.PFishMap;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PShovel extends PAbstractItem {

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> digParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> digSound = new ArrayList<>();

    @Override
    public void trigger(Player player, PTriggerType pTriggerType, PTriggerSubType pTriggerSubType, PTriggerSlot pTriggerSlot, ItemStack itemStack, long l) {
        if (pTriggerType != PTriggerType.RIGHT || pTriggerSlot != PTriggerSlot.MAIN || pTriggerSubType != PTriggerSubType.INIT) return;
        RayTraceResult rTR = player.rayTraceBlocks(5, FluidCollisionMode.NEVER);
        if (rTR != null && rTR.getHitBlock() != null && (rTR.getHitBlock().getType() == Material.MUD || rTR.getHitBlock().getType() == Material.SAND)) {
            PType pT = PType.getPType(PFishMap.class);
            if (pT == null) return;
            PFishMap pFM = (PFishMap) pT.getPFile(player.getWorld().getName());
            if (pFM == null) return;
            PVector digVector = PVector.fromVector(rTR.getHitPosition());
            for (PParticle pP : digParticle) pP.spawn(player, digVector, rTR.getHitBlock().getType());
            for (PSound pS : digSound) pS.play(player, digVector);
            List<PSeaFoodEntity> seaFoodEntityList = pFM.getSeaFoodEntityList(digVector, 2);
            for (PSeaFoodEntity pSFE : seaFoodEntityList) {
                if (pSFE.location.distance(digVector) <= 1) {
                    pSFE.dig(pFM, player);
                }
            }
        }
    }

    @Override
    public ItemStack finalizeGetItemStack(ItemStack itemStack) {
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
