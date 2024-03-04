package me.asakura_kukii.siegefishing.inventory.craft;


import me.asakura_kukii.siegecore.io.PFile;
import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.item.PAbstractItem;
import me.asakura_kukii.siegecore.util.math.PMath;
import me.asakura_kukii.siegefishing.bait.PFishBait;
import me.asakura_kukii.siegefishing.bait.PFishBaitBuff;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PFishCraftRecipe extends PFile {

    public List<PFishCraftRecipeData> recipeList = new ArrayList<>();

    public Pair<List<ItemStack>, List<ItemStack>> getCraftResult(List<ItemStack> input) {
        if (input.size() != 2) return Pair.of(input, new ArrayList<>());
        if (input.get(0) == null && input.get(1) == null) return Pair.of(input, new ArrayList<>());
        if (input.get(0) == null || input.get(1) == null) {
            ItemStack iS = input.get(0) == null ? input.get(1) : input.get(0);
            PAbstractItem pAI = PAbstractItem.getPItem(iS);
            if (pAI == null) return Pair.of(input, new ArrayList<>());
            for (PFishCraftRecipeData data : recipeList) {
                if (data.recipeMap.containsKey(pAI)) {
                    PAbstractItem result = data.recipeMap.get(pAI);
                    List<ItemStack> resultItemStackList = new ArrayList<>();
                    ItemStack resultIS = result.getItemStack();
                    int resultMaxStackSize = resultIS.getMaxStackSize();
                    for (int i = 0; i < data.count * iS.getAmount(); i = i + resultMaxStackSize) {
                        ItemStack stackIS = resultIS.clone();
                        stackIS.setAmount(PMath.min(resultMaxStackSize, data.count * iS.getAmount() - i));
                        resultItemStackList.add(stackIS.clone());
                    }
                    input.set(0, null);
                    input.set(1, null);
                    return Pair.of(input, resultItemStackList);
                }
            }
        } else {
            ItemStack iS1 = input.get(0);
            ItemStack iS2 = input.get(1);
            PFile pF1 = PAbstractItem.getPItem(iS1);
            PFile pF2 = PAbstractItem.getPItem(iS2);
            if (pF1 == null || pF2 == null) return Pair.of(input, new ArrayList<>());
            PFishBait bait;
            ItemStack baitItemStack;
            int baitCount;
            int buffCount;
            PFishBaitBuff buff;
            if (pF1.type.equals(PType.getPType(PFishBait.class)) && pF2.type.equals(PType.getPType(PFishBaitBuff.class))) {
                bait = (PFishBait) pF1;
                baitItemStack = iS1.clone();
                baitCount = iS1.getAmount();
                buffCount = iS2.getAmount();
                buff = (PFishBaitBuff) pF2;
            } else if (pF1.type.equals(PType.getPType(PFishBaitBuff.class)) && pF2.type.equals(PType.getPType(PFishBait.class))) {
                bait = (PFishBait) pF2;
                baitItemStack = iS2.clone();
                baitCount = iS2.getAmount();
                buffCount = iS1.getAmount();
                buff = (PFishBaitBuff) pF1;
            } else {
                return Pair.of(input, new ArrayList<>());
            }
            float[] modifierList = PFishBait.getBaitModifierList(baitItemStack);

            int baitCountTillHealthLimit = buff.health == 0 ? 1000 : (int) PMath.floor((bait.healthBuffLimit - modifierList[0]) / buff.health);
            int baitCountTillSpeedLimit = buff.speed == 0 ? 1000 : (int) PMath.floor((bait.speedBuffLimit - modifierList[1]) / buff.speed);
            int baitCountTillStrengthLimit = buff.strength == 0 ? 1000 : (int) PMath.floor((bait.strengthBuffLimit - modifierList[2]) / buff.strength);
            int baitCountTillRadiusLimit = buff.radius == 0 ? 1000 : (int) PMath.floor((bait.radiusBuffLimit - modifierList[3]) / buff.radius);

            int consumedBaitCount = PMath.min(baitCount, buffCount);

            int buffCountPerBait = (int) PMath.floor((float) buffCount / consumedBaitCount);
            buffCountPerBait = PMath.min(buffCountPerBait, baitCountTillHealthLimit);
            buffCountPerBait = PMath.min(buffCountPerBait, baitCountTillSpeedLimit);
            buffCountPerBait = PMath.min(buffCountPerBait, baitCountTillStrengthLimit);
            buffCountPerBait = PMath.min(buffCountPerBait, baitCountTillRadiusLimit);

            int consumedBuffCount = buffCountPerBait * consumedBaitCount;

            modifierList[0] = modifierList[0] + buff.health * buffCountPerBait;
            modifierList[1] = modifierList[1] + buff.speed * buffCountPerBait;
            modifierList[2] = modifierList[2] + buff.strength * buffCountPerBait;
            modifierList[3] = modifierList[3] + buff.radius * buffCountPerBait;

            baitItemStack = bait.setBaitModifierList(baitItemStack, modifierList);
            baitItemStack.setAmount(consumedBaitCount);

            if (pF1.type.equals(PType.getPType(PFishBait.class)) && pF2.type.equals(PType.getPType(PFishBaitBuff.class))) {
                input.get(0).setAmount(baitCount - consumedBaitCount);
                input.get(1).setAmount(buffCount - consumedBuffCount);
            } else if (pF1.type.equals(PType.getPType(PFishBaitBuff.class)) && pF2.type.equals(PType.getPType(PFishBait.class))) {
                input.get(1).setAmount(baitCount - consumedBaitCount);
                input.get(0).setAmount(buffCount - consumedBuffCount);
            }

            return Pair.of(input, Collections.singletonList(baitItemStack));
        }
        return Pair.of(input, new ArrayList<>());
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
