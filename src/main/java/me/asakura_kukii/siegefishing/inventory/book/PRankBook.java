package me.asakura_kukii.siegefishing.inventory.book;

import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.inventory.PAbstractInventory;
import me.asakura_kukii.siegecore.io.PFile;
import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.io.helper.PFileIdDeserializer;
import me.asakura_kukii.siegecore.io.helper.PFileIdSerializer;
import me.asakura_kukii.siegecore.util.math.PVector;
import me.asakura_kukii.siegefishing.achievement.PAchievementCollectable;
import me.asakura_kukii.siegefishing.config.PConfig;
import me.asakura_kukii.siegefishing.deco.PDeco;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.inventory.PLayout;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class PRankBook extends PAbstractInventory {

    @JsonSerialize(using = PFileIdSerializer.class)
    @JsonDeserialize(using = PFileIdDeserializer.class)
    public PDeco headDeco = null;

    @Override
    public void initializeLayout() {
        PType pT = PType.getPType(PLayout.class);
        if (pT == null) return;
        this.layout = (PLayout) pT.getPFileSafely("rank_book");
        PType pT2 = PType.getPType(PDeco.class);
        if (pT2 == null) return;
        this.headDeco = (PDeco) pT2.getPFileSafely("player_head");
    }

    @Override
    public List<ItemStack> finalizeItemStackList(Player p, List<ItemStack> list) {
        PType pT = PType.getPType(PFishPlayer.class);
        List<PFishPlayer> pFishPlayerList = new ArrayList<>();
        for (PFile pF : pT.getPFileList()) {
            pFishPlayerList.add(((PFishPlayer) pF));
        }
        pFishPlayerList.sort((o1, o2) -> Float.compare(o2.balance, o1.balance));
        List<ItemStack> itemStackList = new ArrayList<>();
        for (PFishPlayer pFP : pFishPlayerList) {
            ItemStack iS = this.headDeco.getItemStack();
            if (iS.getType() == Material.PLAYER_HEAD) {
                ItemMeta iM = iS.getItemMeta() == null ? Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD) : iS.getItemMeta();
                assert iM != null;
                OfflinePlayer oP = Bukkit.getOfflinePlayer(UUID.fromString(pFP.id));
                ((SkullMeta) iM).setOwningPlayer(oP);
                List<String> loreList = new ArrayList<>();
                iM.setDisplayName(oP.getName());
                loreList.add("level: " + pFP.level.toString() + "-" + pFP.experience);
                loreList.add("money: " + pFP.balance);
                loreList.add("fishCount: " + pFP.fishCount + " typeCount: " + pFP.fishCountCounter.size());
                loreList.add("seaFoodCount: " + pFP.seaFoodCount + " typeCount: " + pFP.seaFoodCountCounter.size());
                loreList.add("insectCount: " + pFP.insectCount + " typeCount: " + pFP.insectCountCounter.size());
                loreList.add("achievement: " + pFP.achievementCounter.size());
                loreList.add("bucketCount: " + pFP.bucketCounter.size());
                loreList.add("meter: " + pFP.meter);
                iM.setLore(loreList);
                iS.setItemMeta(iM);
            }
            itemStackList.add(iS);
        }
        return itemStackList;
    }

    @Override
    public void command(Player p, String s, ItemStack iS) {
        PType pT2 = PType.getPType(PConfig.class);
        if (pT2 == null) return;
        PConfig pC = (PConfig) pT2.getPFileSafely("config");
        if (pC == null) return;
        if (s.equals("next_page")) {
            for (PSound pS : pC.bookSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            this.nextPage(p);
        }
        if (s.equals("last_page")) {
            for (PSound pS : pC.bookSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            this.lastPage(p);
        }
        if (s.equals("close")) {
            for (PSound pS : pC.bookSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            this.close(p);
        }
        PType pT = PType.getPType(PFishPlayer.class);
        if (pT == null) return;
        PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
        if (pFP == null) return;
        if (s.equals("return")) {
            pFP.fishMenu.open(p);
            for (PSound pS : pC.bookSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            return;
        }
    }

    @Override
    public void trigger(Player p, int i, ItemStack iS) {
    }
}
