package me.asakura_kukii.siegefishing.inventory;

import me.asakura_kukii.siegecore.inventory.PAbstractInventory;
import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.util.math.PVector;
import me.asakura_kukii.siegefishing.config.PConfig;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PFishMenu extends PAbstractInventory {
    @Override
    public void initializeLayout() {
        PType pT = PType.getPType(PLayout.class);
        if (pT == null) return;
        this.layout = (PLayout) pT.getPFileSafely("fish_menu");
    }

    @Override
    public List<ItemStack> finalizeItemStackList(Player p, List<ItemStack> list) {
        return list;
    }

    @Override
    public void command(Player p, String s, ItemStack iS) {
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
        PType pT = PType.getPType(PFishPlayer.class);
        if (pT == null) return;
        PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
        if (pFP == null) return;
        if (s.equals("open_fish_book")) {
            pFP.fishBook.open(p);
            for (PSound pS : pC.bookSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            return;
        }
        if (s.equals("open_seafood_book")) {
            pFP.seaFoodBook.open(p);
            for (PSound pS : pC.bookSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            return;
        }
        if (s.equals("open_insect_book")) {
            pFP.insectBook.open(p);
            for (PSound pS : pC.bookSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            return;
        }
        if (s.equals("open_achievement_book")) {
            pFP.achievementBook.open(p);
            for (PSound pS : pC.bookSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            return;
        }
        if (s.equals("emergency")) {
            if (pC.pFM != null && pC.pFM.world != null) {
                p.teleport(pC.spawn.getLocation(pC.pFM.world));
            }
            for (PSound pS : pC.clickSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            return;
        }
        if (s.equals("open_netease")) {
            pFP.fishNetEase.open(p);
            for (PSound pS : pC.clickSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            return;
        }
    }

    @Override
    public void trigger(Player p, int i, ItemStack iS) {

    }
}
