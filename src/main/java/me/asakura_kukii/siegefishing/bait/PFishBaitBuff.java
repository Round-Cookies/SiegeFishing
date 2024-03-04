package me.asakura_kukii.siegefishing.bait;

import me.asakura_kukii.lib.jackson.annotation.JsonIgnore;
import me.asakura_kukii.siegecore.item.PAbstractItem;
import me.asakura_kukii.siegecore.trigger.PTriggerSlot;
import me.asakura_kukii.siegecore.trigger.PTriggerSubType;
import me.asakura_kukii.siegecore.trigger.PTriggerType;
import me.asakura_kukii.siegefishing.SiegeFishing;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class PFishBaitBuff extends PAbstractItem {

    public float health = 0.0F;
    public float speed = 0.0F;
    public float strength = 0.0F;
    public float radius = 0.0F;

    @Override
    public void trigger(Player player, PTriggerType pTriggerType, PTriggerSubType pTriggerSubType, PTriggerSlot pTriggerSlot, ItemStack itemStack, long l) {

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
