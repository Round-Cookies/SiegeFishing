package me.asakura_kukii.siegefishing.inventory;

import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.inventory.PAbstractInventory;
import me.asakura_kukii.siegecore.io.PFile;
import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.io.helper.PFileIdDeserializer;
import me.asakura_kukii.siegecore.io.helper.PFileIdSerializer;
import me.asakura_kukii.siegecore.item.PAbstractItem;
import me.asakura_kukii.siegecore.util.math.PVector;
import me.asakura_kukii.siegefishing.config.PConfig;
import me.asakura_kukii.siegefishing.creature.fish.PFish;
import me.asakura_kukii.siegefishing.creature.fish.PFishLevel;
import me.asakura_kukii.siegefishing.effect.PParticle;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PFishAcquire extends PAbstractInventory {

    @Override
    public void initializeLayout() {
        PType pT = PType.getPType(PLayout.class);
        if (pT == null) return;
        this.layout = (PLayout) pT.getPFileSafely("fish_acquire");
    }

    @Override
    public List<ItemStack> finalizeItemStackList(Player p, List<ItemStack> list) {
        return list;
    }

    @Override
    public void command(Player p, String s, ItemStack no) {
        PType pT2 = PType.getPType(PConfig.class);
        if (pT2 == null) return;
        PConfig pC = (PConfig) pT2.getPFileSafely("config");
        if (pC == null) return;
        if (s.equals("next_page")) {
            for (PSound pS : pC.clickSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            this.nextPage(p);
        }
        if (s.equals("last_page")) {
            for (PSound pS : pC.clickSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            this.lastPage(p);
        }
        if (s.equals("close")) {
            for (PSound pS : pC.clickSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            this.close(p);
        }
        if (s.equals("sell")) {
            PType pT = PType.getPType(PFishPlayer.class);
            if (pT == null) return;
            PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
            if (pFP == null) return;
            List<Integer> indexList = this.layout.getPageContentIndexList(this.pageIndex).getRight();
            float totalPrice = 0.0F;
            float trashPrice = 0.0F;
            for (int index : indexList) {
                ItemStack iS = this.inventory.getItem(index);
                if (iS == null) continue;
                int amount = iS.getAmount();
                PAbstractItem pAI = PAbstractItem.getPItem(iS);
                if (pAI == null || pAI.price == -1) continue;
                if (pAI instanceof PFish && ((PFish) pAI).level == PFishLevel.TRASH) {
                    trashPrice = trashPrice + pAI.price * amount;
                }
                totalPrice = totalPrice + pAI.price * amount;
                this.inventory.setItem(index, new ItemStack(Material.AIR));
                pFP.collectAcquire(trashPrice);
            }
            if (totalPrice > 0) {
                for (PSound pS : pC.purchaseSuccessSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            } else {
                for (PSound pS : pC.purchaseFailureSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            }
            pFP.balance = pFP.balance + totalPrice;
            save();
            load(p);
        }
    }

    public PFile getPFileFromString(String s) {
        if (!s.contains(".") && s.split("\\.").length != 3) return null;
        String typeId = s.split("\\.")[0] + "." + s.split("\\.")[1];
        String fileId = s.split("\\.")[2];
        PType type = PType.getPType(typeId);
        if (type == null) return null;
        if (!type.isItem) return null;
        if (type.getPFile(fileId) != null) {
            return (PAbstractItem) type.getPFile(fileId);
        }
        return null;
    }

    @Override
    public void trigger(Player p, int i, ItemStack iS) {
    }
}
