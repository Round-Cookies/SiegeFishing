package me.asakura_kukii.siegefishing.achievement;

import me.asakura_kukii.lib.jackson.annotation.JsonIgnore;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.io.PFile;
import me.asakura_kukii.siegecore.io.helper.*;
import me.asakura_kukii.siegecore.item.PAbstractItem;
import me.asakura_kukii.siegecore.trigger.PTriggerSlot;
import me.asakura_kukii.siegecore.trigger.PTriggerSubType;
import me.asakura_kukii.siegecore.trigger.PTriggerType;
import me.asakura_kukii.siegecore.util.format.PFormat;
import me.asakura_kukii.siegefishing.map.PFishRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PAchievementCollectable extends PAbstractItem {

    @JsonSerialize(keyUsing = PFileIdKeySerializer.class)
    @JsonDeserialize(keyUsing = PFileIdKeyDeserializer.class)
    public HashMap<PAbstractItem, Integer> awardMap = new HashMap<>();

    @JsonSerialize(using = ItemStackSerializer.class)
    @JsonDeserialize(using = ItemStackDeserializer.class)
    public ItemStack awardIS = null;

    public float money = 0;

    @JsonIgnore
    public ItemStack getItemStackForBook(Player p) {
        ItemStack iS = this.getItemStack();
        if (iS == null) return null;
        ItemMeta iM = iS.getItemMeta();
        if (iM == null) iM = Bukkit.getItemFactory().getItemMeta(iS.getType());
        assert iM != null;
        List<String> loreList = new ArrayList<>();
        for (String lore : this.lore) {
            if (lore.contains("%player%")) lore = lore.replaceAll("%player%", PFormat.format(p.getName()));
            loreList.add(lore);
        }
        iM.setLore(PFormat.format(loreList));
        iS.setItemMeta(iM);
        return iS;
    }

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

    @Override
    public void trigger(Player player, PTriggerType pTriggerType, PTriggerSubType pTriggerSubType, PTriggerSlot pTriggerSlot, ItemStack itemStack, long l) {

    }

    @Override
    public ItemStack finalizeGetItemStack(ItemStack itemStack) {
        return itemStack;
    }
}
