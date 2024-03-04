package me.asakura_kukii.siegefishing.creature.insect;

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
import me.asakura_kukii.siegecore.util.math.PAxis;
import me.asakura_kukii.siegecore.util.math.PVector;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.creature.insect.PInsectEntity;
import me.asakura_kukii.siegefishing.map.PFishMap;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import org.bukkit.FluidCollisionMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PNet extends PAbstractItem {

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> netParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> netSound = new ArrayList<>();

    public float netSuccessDistance = 0.3F;

    public float netNoticeDistance = 5.0F;

    @Override
    public void trigger(Player player, PTriggerType pTriggerType, PTriggerSubType pTriggerSubType, PTriggerSlot pTriggerSlot, ItemStack itemStack, long l) {
        if (pTriggerType != PTriggerType.RIGHT || pTriggerSlot != PTriggerSlot.MAIN || pTriggerSubType != PTriggerSubType.INIT) return;
        PType pT2 = PType.getPType(PFishPlayer.class);
        if (pT2 != null) {
            PFishPlayer pFP = (PFishPlayer) pT2.getPFileSafely(player.getUniqueId().toString());
            if (pFP != null) {
                PAxis pA = pFP.getAxis();
                if (pA != null) {
                    PVector vector = (PVector) pA.getO().add(pA.getZ().mul(3));
                    for (PParticle pP : netParticle) pP.spawn(player, vector);
                    for (PSound pS : netSound) pS.play(player, vector);
                }
            }
        }
        RayTraceResult rTR = player.rayTraceBlocks(5, FluidCollisionMode.NEVER);
        if (rTR != null && rTR.getHitBlock() != null) {
            PType pT = PType.getPType(PFishMap.class);
            if (pT == null) return;
            PFishMap pFM = (PFishMap) pT.getPFile(player.getWorld().getName());
            if (pFM == null) return;
            PVector netVector = PVector.fromVector(rTR.getHitPosition());
            List<PInsectEntity> insectEntityList = pFM.getInsectEntityList(netVector, 2);
            for (PInsectEntity pIE : insectEntityList) {
                if (pIE.location.distance(netVector) <= netNoticeDistance) {
                    if (pIE.location.distance(netVector) <= netSuccessDistance) {
                        pIE.net(pFM, player);
                    } else {
                        pIE.flee(pFM, player);
                    }
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
