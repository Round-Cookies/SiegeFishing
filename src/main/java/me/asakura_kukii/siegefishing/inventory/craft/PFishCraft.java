package me.asakura_kukii.siegefishing.inventory.craft;

import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.inventory.PAbstractInventory;
import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.io.helper.PFileIdDeserializer;
import me.asakura_kukii.siegecore.io.helper.PFileIdSerializer;
import me.asakura_kukii.siegecore.util.math.PVector;
import me.asakura_kukii.siegefishing.config.PConfig;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.inventory.PLayout;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PFishCraft extends PAbstractInventory {

    @JsonSerialize(using = PFileIdSerializer.class)
    @JsonDeserialize(using = PFileIdDeserializer.class)
    public PFishCraftRecipe recipe = null;

    @Override
    public void initializeLayout() {
        PType pT = PType.getPType(PLayout.class);
        if (pT == null) return;
        this.layout = (PLayout) pT.getPFileSafely("bait_craft");
        PType pT2 = PType.getPType(PFishCraftRecipe.class);
        if (pT2 == null) return;
        this.recipe = (PFishCraftRecipe) pT2.getPFileSafely("bait_craft_recipe");
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
        if (s.equals("craft")) {
            PType pT = PType.getPType(PFishPlayer.class);
            if (pT == null) return;
            PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
            if (pFP == null) return;
            List<Integer> indexList = this.layout.getPageContentIndexList(this.pageIndex).getRight();
            if (indexList.size() != 2) return;
            List<ItemStack> input = new ArrayList<>();
            for (Integer index : indexList) {
                input.add(this.inventory.getItem(index));
            }
            if (recipe == null) return;
            Pair<List<ItemStack>, List<ItemStack>> result = recipe.getCraftResult(input);
            List<ItemStack> modifiedInput = result.getLeft();
            List<ItemStack> output = result.getRight();
            for (int i = 0; i < 2; i++) {
                this.inventory.setItem(indexList.get(i), modifiedInput.get(i));
            }
            boolean flagCraftSuccess = false;
            for (ItemStack iS : output) {
                pFP.giveItemStack(iS);
                if (iS != null && !iS.getType().equals(Material.AIR)) flagCraftSuccess = true;
            }
            if (flagCraftSuccess) {
                pFP.collectCraft();
                for (PSound pS : pC.clickSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            }
        }
    }

    @Override
    public void trigger(Player p, int i, ItemStack iS) {
    }
}
