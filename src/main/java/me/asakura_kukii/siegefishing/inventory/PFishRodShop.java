package me.asakura_kukii.siegefishing.inventory;

import me.asakura_kukii.siegecore.inventory.PAbstractInventory;
import me.asakura_kukii.siegecore.io.PFile;
import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.item.PAbstractItem;
import me.asakura_kukii.siegecore.util.math.PVector;
import me.asakura_kukii.siegefishing.config.PConfig;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PFishRodShop extends PAbstractInventory {

    @Override
    public void initializeLayout() {
        PType pT = PType.getPType(PLayout.class);
        if (pT == null) return;
        this.layout = (PLayout) pT.getPFileSafely("fish_rod_shop");
    }

    @Override
    public List<ItemStack> finalizeItemStackList(Player p, List<ItemStack> list) {
        return new ArrayList<>();
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
        if (s.contains("buy")) {
            if (iS == null) return;
            PButton pB = (PButton) PButton.getPButton(iS);
            if (pB == null) return;
            PType pT = PType.getPType(PFishPlayer.class);
            if (pT == null) return;
            PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
            if (pFP == null) return;
            if (pB.sell == null) return;
            if (pFP.balance >= pB.price) {
                pFP.giveItemStack(pB.sell.getItemStack(pB.amount));
                pFP.balance = pFP.balance - pB.price;
                if (pFP.balance < 0) pFP.balance = 0;
                for (PSound pS : pC.purchaseSuccessSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            } else {
                for (PSound pS : pC.purchaseFailureSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            }
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
