package me.asakura_kukii.siegefishing.inventory;

import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.io.helper.PFileIdDeserializer;
import me.asakura_kukii.siegecore.io.helper.PFileIdSerializer;
import me.asakura_kukii.siegecore.item.PAbstractButton;
import me.asakura_kukii.siegecore.item.PAbstractItem;
import me.asakura_kukii.siegecore.util.format.PFormat;
import me.asakura_kukii.siegefishing.achievement.PAchievementCollectable;
import me.asakura_kukii.siegefishing.collectable.PFishCollectable;
import me.asakura_kukii.siegefishing.collectable.PInsectCollectable;
import me.asakura_kukii.siegefishing.collectable.PSeaFoodCollectable;
import me.asakura_kukii.siegefishing.creature.fish.PFishEmotion;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PButton extends PAbstractButton {

    @Override
    public ItemStack finalizeGetItemStack(Player p, ItemStack itemStack) {
        ItemMeta iM = itemStack.getItemMeta();
        if (iM == null) iM = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        PType pT = PType.getPType(PFishPlayer.class);
        if (pT == null) return itemStack;
        PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
        if (pFP == null) return itemStack;
        int fishCollectableCount = 35;
        if (PType.getPType(PFishCollectable.class) != null) fishCollectableCount = PType.getPType(PFishCollectable.class).getPFileList().size();
        int seaFoodCollectableCount = 20;
        if (PType.getPType(PSeaFoodCollectable.class) != null) seaFoodCollectableCount = PType.getPType(PSeaFoodCollectable.class).getPFileList().size();
        int insectCollectableCount = 15;
        if (PType.getPType(PInsectCollectable.class) != null) insectCollectableCount = PType.getPType(PInsectCollectable.class).getPFileList().size();
        int achievementCollectableCount = 100;
        if (PType.getPType(PAchievementCollectable.class) != null) achievementCollectableCount = PType.getPType(PAchievementCollectable.class).getPFileList().size();
        List<String> loreList = new ArrayList<>();
        for (String lore : this.lore) {
            if (lore.contains("%name%")) lore = lore.replaceAll("%name%", p.getName());
            if (lore.contains("%experience%")) lore = lore.replaceAll("%experience%", String.format("%.0f", pFP.experience));
            if (lore.contains("%level%")) lore = lore.replaceAll("%level%", pFP.level.colorString + pFP.level.displayName);
            if (lore.contains("%balance%")) lore = lore.replaceAll("%balance%", String.format("%.1f", pFP.balance));
            if (lore.contains("%emotion%")) lore = lore.replaceAll("%emotion%", PFishEmotion.getEmotion());
            if (lore.contains("%fish_count%")) lore = lore.replaceAll("%fish_count%", String.valueOf(pFP.fishCount));
            if (lore.contains("%seafood_count%")) lore = lore.replaceAll("%seafood_count%", String.valueOf(pFP.seaFoodCount));
            if (lore.contains("%insect_count%")) lore = lore.replaceAll("%insect_count%", String.valueOf(pFP.insectCount));
            if (lore.contains("%achievement_count%")) lore = lore.replaceAll("%achievement_count%", String.valueOf(pFP.achievementCounter.size()));
            if (lore.contains("%fish_type_progress%")) lore = lore.replaceAll("%fish_type_progress%", String.format("%.1f", pFP.fishCountCounter.size() / (float) fishCollectableCount * 100F));
            if (lore.contains("%seafood_type_progress%")) lore = lore.replaceAll("%seafood_type_progress%", String.format("%.1f", pFP.seaFoodCountCounter.size() / (float) seaFoodCollectableCount * 100F));
            if (lore.contains("%insect_type_progress%")) lore = lore.replaceAll("%insect_type_progress%", String.format("%.1f", pFP.insectCountCounter.size() / (float) insectCollectableCount * 100F));
            if (lore.contains("%achievement_type_progress%")) lore = lore.replaceAll("%achievement_type_progress%", String.format("%.1f", pFP.achievementCounter.size() / (float) achievementCollectableCount * 100F));
            loreList.add(lore);
        }
        assert iM != null;
        if (itemStack.getType().equals(Material.PLAYER_HEAD)) {
            ((SkullMeta) iM).setOwningPlayer(p);
        }

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
