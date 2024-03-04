package me.asakura_kukii.siegefishing.inventory;

import me.asakura_kukii.lib.jackson.annotation.JsonIgnore;
import me.asakura_kukii.siegecore.inventory.PAbstractInventory;
import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.item.PAbstractItem;
import me.asakura_kukii.siegecore.util.math.PMath;
import me.asakura_kukii.siegecore.util.math.PVector;
import me.asakura_kukii.siegefishing.config.PConfig;
import me.asakura_kukii.siegefishing.effect.PParticle;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PFishLottery extends PAbstractInventory {

    public PFishLottery() {}

    @Override
    public void initializeLayout() {
        PType pT = PType.getPType(PLayout.class);
        if (pT == null) return;
        this.layout = (PLayout) pT.getPFileSafely("fish_lottery");
    }

    @Override
    public List<ItemStack> finalizeItemStackList(Player p, List<ItemStack> list) {
        return list;
    }

    @JsonIgnore
    public Pair<PAbstractItem, Integer> getAward(List<PAbstractItem> itemList, List<Float> weightList, List<Integer> itemCountList) {
        if (!itemList.isEmpty() && !weightList.isEmpty()) {
            int i = PMath.ranIndexWeighted(weightList);
            if (i >= itemList.size()) i = itemList.size() - 1;
            if (itemCountList == null) {
                return Pair.of(itemList.get(i), 1);
            } else {
                return Pair.of(itemList.get(i), itemCountList.get(i));
            }

        }
        return Pair.of(null, 1);
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
        if (s.equals("lottery")) {
            PType pT = PType.getPType(PFishPlayer.class);
            if (pT == null) return;
            PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
            if (pFP == null) return;
            if (pFP.balance >= pC.lotteryCost) {
                pFP.balance = pFP.balance - pC.lotteryCost;
                ItemStack award = null;
                List<Float> weight;
                if (pFP.lotteryCounterForLevel5 >= 69) {
                    weight = PConfig.lotteryWeightForLevel5;
                } else if (pFP.lotteryCounterForLevel4 >= 19) {
                    weight = PConfig.lotteryWeightForLevel4;
                } else {
                    weight = PConfig.lotteryWeightNormal;
                }
                int index = PMath.ranIndexWeighted(weight);
                if (index == 0) {
                    Pair<PAbstractItem, Integer> result = getAward(pC.lotteryBaitList, pC.lotteryBaitWeightList, pC.lotteryBaitCountList);
                    PAbstractItem pAI = result.getLeft();
                    if (pAI != null) award = pAI.getItemStack(result.getRight());
                    pFP.lotteryCounterForLevel4++;
                    pFP.lotteryCounterForLevel5++;
                    pFP.collectLottery(false, false);
                } else if (index == 1) {
                    Pair<PAbstractItem, Integer> result = getAward(pC.lotteryBuffList, pC.lotteryBuffWeightList, pC.lotteryBuffCountList);
                    PAbstractItem pAI = result.getLeft();
                    if (pAI != null) award = pAI.getItemStack(result.getRight());
                    pFP.lotteryCounterForLevel4++;
                    pFP.lotteryCounterForLevel5++;
                    pFP.collectLottery(false, false);
                } else if (index == 2) {
                    Pair<PAbstractItem, Integer> result = getAward(pC.lotteryRodLevel4List, pC.lotteryRodLevel4WeightList, null);
                    PAbstractItem pAI = result.getLeft();
                    if (pAI != null) {
                        award = pAI.getItemStack(result.getRight());
                        pFP.sendMessageWithItem(pC.lotteryItemBroadcastFormat.replaceAll("%player%", p.getName()), award, true);
                    }
                    pFP.lotteryCounterForLevel4 = 0;
                    pFP.lotteryCounterForLevel5++;
                    pFP.collectLottery(true, false);
                } else if (index == 3) {
                    Pair<PAbstractItem, Integer> result = getAward(pC.lotteryRodLevel5List, pC.lotteryRodLevel5WeightList, null);
                    PAbstractItem pAI = result.getLeft();
                    if (pAI != null) {
                        award = pAI.getItemStack(result.getRight());
                        pFP.sendMessageWithItem(pC.lotteryItemBroadcastFormat.replaceAll("%player%", p.getName()), award, true);
                    }
                    pFP.lotteryCounterForLevel4 = 0;
                    pFP.lotteryCounterForLevel5 = 0;
                    pFP.collectLottery(false, true);
                }
                pFP.giveItemStack(award);
                for (PParticle pP : pC.lotterySuccessParticle) pP.spawn(p, PVector.fromLocation(p.getLocation()));
                for (PSound pS : pC.lotterySuccessSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            } else {
                for (PParticle pP : pC.lotteryFailureParticle) pP.spawn(p, PVector.fromLocation(p.getLocation()));
                for (PSound pS : pC.lotteryFailureSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            }
            save();
            load(p);
        }
    }

    @Override
    public void trigger(Player p, int i, ItemStack iS) {
    }
}
