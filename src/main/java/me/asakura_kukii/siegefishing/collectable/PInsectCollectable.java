package me.asakura_kukii.siegefishing.collectable;

import me.asakura_kukii.lib.jackson.annotation.JsonIgnore;
import me.asakura_kukii.siegecore.item.PAbstractItem;
import me.asakura_kukii.siegecore.trigger.PTriggerSlot;
import me.asakura_kukii.siegecore.trigger.PTriggerSubType;
import me.asakura_kukii.siegecore.trigger.PTriggerType;
import me.asakura_kukii.siegecore.util.format.PFormat;
import me.asakura_kukii.siegefishing.creature.insect.PInsectLevel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PInsectCollectable extends PAbstractItem {

    public PInsectLevel level = PInsectLevel.BLUE;

    @Override
    public void trigger(Player player, PTriggerType pTriggerType, PTriggerSubType pTriggerSubType, PTriggerSlot pTriggerSlot, ItemStack itemStack, long l) {

    }

    @JsonIgnore
    public ItemStack getItemStackForBook(int count, int starCount) {
        ItemStack iS = this.getItemStack();
        if (iS == null) return null;
        ItemMeta iM = iS.getItemMeta();
        if (iM == null) iM = Bukkit.getItemFactory().getItemMeta(iS.getType());
        assert iM != null;
        List<String> loreList = new ArrayList<>();
        for (String lore : this.lore) {
            if (lore.contains("%level%")) lore = lore.replaceAll("%level%", this.level.colorString + this.level.displayName);
            if (lore.contains("%count%")) lore = lore.replaceAll("%count%", count + "");
            if (lore.contains("%star%")) {
                if (starCount == 0) {
                    lore = lore.replaceAll("%star%", "☆");
                } else if (starCount == 10) {
                    lore = lore.replaceAll("%star%", "&c&l★".repeat(starCount));
                } else {
                    lore = lore.replaceAll("%star%", "★".repeat(starCount));
                }
            }
            loreList.add(lore);
        }
        iM.setDisplayName(PFormat.format(this.level.colorString) + iM.getDisplayName());
        iM.setLore(PFormat.format(loreList));
        iS.setItemMeta(iM);
        return iS;
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
