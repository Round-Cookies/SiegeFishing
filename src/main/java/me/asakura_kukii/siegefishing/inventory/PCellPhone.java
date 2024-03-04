package me.asakura_kukii.siegefishing.inventory;

import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.item.PAbstractItem;
import me.asakura_kukii.siegecore.trigger.PTriggerSlot;
import me.asakura_kukii.siegecore.trigger.PTriggerSubType;
import me.asakura_kukii.siegecore.trigger.PTriggerType;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class PCellPhone extends PAbstractItem {
    @Override
    public void trigger(Player player, PTriggerType pTriggerType, PTriggerSubType pTriggerSubType, PTriggerSlot pTriggerSlot, ItemStack itemStack, long l) {
        if (pTriggerType == PTriggerType.RIGHT && pTriggerSlot == PTriggerSlot.MAIN && pTriggerSubType == PTriggerSubType.INIT) {
            PType pT = PType.getPType(PFishPlayer.class);
            if (pT == null) return;
            PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(player.getUniqueId().toString());
            pFP.fishMenu.open(player);
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
