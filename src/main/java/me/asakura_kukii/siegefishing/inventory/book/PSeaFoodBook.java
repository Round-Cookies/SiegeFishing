package me.asakura_kukii.siegefishing.inventory.book;

import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.inventory.PAbstractInventory;
import me.asakura_kukii.siegecore.io.PFile;
import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.io.helper.PFileIdDeserializer;
import me.asakura_kukii.siegecore.io.helper.PFileIdSerializer;
import me.asakura_kukii.siegecore.util.math.PVector;
import me.asakura_kukii.siegefishing.collectable.PFishCollectable;
import me.asakura_kukii.siegefishing.collectable.PRegionCollectable;
import me.asakura_kukii.siegefishing.collectable.PSeaFoodCollectable;
import me.asakura_kukii.siegefishing.config.PConfig;
import me.asakura_kukii.siegefishing.deco.PDeco;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.inventory.PLayout;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PSeaFoodBook extends PAbstractInventory {

    @JsonSerialize(using = PFileIdSerializer.class)
    @JsonDeserialize(using = PFileIdDeserializer.class)
    public PDeco undiscoveredDeco = null;

    @Override
    public void initializeLayout() {
        PType pT = PType.getPType(PLayout.class);
        if (pT == null) return;
        this.layout = (PLayout) pT.getPFileSafely("seafood_book");
        PType pT2 = PType.getPType(PDeco.class);
        if (pT2 == null) return;
        this.undiscoveredDeco = (PDeco) pT2.getPFileSafely("book_unknown");
    }

    @Override
    public List<ItemStack> finalizeItemStackList(Player p, List<ItemStack> list) {
        PType pT = PType.getPType(PFishPlayer.class);
        PType pT2 = PType.getPType(PSeaFoodCollectable.class);
        if (pT == null || pT2 == null) return new ArrayList<>();
        PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
        if (pFP == null) return new ArrayList<>();
        List<PSeaFoodCollectable> seaFoodCollectableList = new ArrayList<>();
        for (PFile pF : pT2.getPFileList()) {
            seaFoodCollectableList.add((PSeaFoodCollectable) pF);
        }
        seaFoodCollectableList.sort((o1, o2) -> {
            if (o1.level.level > o2.level.level) return 1;
            if (o1.level.level < o2.level.level) return -1;
            return o1.name.compareTo(o2.name);
        });
        List<ItemStack> itemStackList = new ArrayList<>();
        for (PSeaFoodCollectable seaFoodCollectable : seaFoodCollectableList) {
            if (pFP.seaFoodCountCounter.containsKey(seaFoodCollectable)) {
                int count = pFP.seaFoodCountCounter.get(seaFoodCollectable);
                itemStackList.add(seaFoodCollectable.getItemStackForBook(count));
            } else {
                if (this.undiscoveredDeco != null) {
                    itemStackList.add(this.undiscoveredDeco.getItemStack());
                } else {
                    itemStackList.add(new ItemStack(Material.COOKIE));
                }
            }
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
