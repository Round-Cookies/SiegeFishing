package me.asakura_kukii.siegefishing.deco;

import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.item.PAbstractItem;
import me.asakura_kukii.siegecore.trigger.PTriggerSlot;
import me.asakura_kukii.siegecore.trigger.PTriggerSubType;
import me.asakura_kukii.siegecore.trigger.PTriggerType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class PDeco extends PAbstractItem {

    public PDeco() {}

    public PDeco(String id, File file, PType type) {
        super(id, file, type);
    }

    @Override
    public void trigger(Player player, PTriggerType pTriggerType, PTriggerSubType pTriggerSubType, PTriggerSlot pTriggerSlot, ItemStack itemStack, long hTT) {
    }

    @Override
    public ItemStack finalizeGetItemStack(ItemStack itemStack) {
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
