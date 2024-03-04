package me.asakura_kukii.siegefishing.map;

import me.asakura_kukii.lib.jackson.annotation.JsonIgnore;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.io.PFile;
import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.io.helper.*;
import me.asakura_kukii.siegecore.util.math.PMath;
import me.asakura_kukii.siegecore.util.math.PVector;
import me.asakura_kukii.siegefishing.creature.fish.*;
import me.asakura_kukii.siegefishing.creature.insect.PInsect;
import me.asakura_kukii.siegefishing.creature.insect.PInsectEntity;
import me.asakura_kukii.siegefishing.creature.seafood.PSeaFood;
import me.asakura_kukii.siegefishing.creature.seafood.PSeaFoodEntity;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.*;

public class PFishChunk {

    public PFishChunk() {}

    public int chunkIndexX = 0;

    public int chunkIndexZ = 0;

    public transient int fishSpawnCountDown = 0;

    public transient int seaFoodSpawnCountDown = 0;

    public transient int insectSpawnCountDown = 0;

    public transient int unloadCountDown = 0;

    public PFishChunk(int chunkIndexX, int chunkIndexZ) {
        this.chunkIndexX = chunkIndexX;
        this.chunkIndexZ = chunkIndexZ;
    }

    @JsonSerialize(using = PFileIdSerializer.class)
    @JsonDeserialize(using = PFileIdDeserializer.class)
    public PFishRegion region = null;

    @JsonSerialize(contentUsing = PVectorSerializer.class)
    @JsonDeserialize(contentUsing = PVectorDeserializer.class)
    public List<PVector> fishSpawnPointList = new ArrayList<>();

    @JsonSerialize(contentUsing = PVectorSerializer.class)
    @JsonDeserialize(contentUsing = PVectorDeserializer.class)
    public List<PVector> seaFoodSpawnPointList = new ArrayList<>();

    @JsonSerialize(contentUsing = PVectorSerializer.class)
    @JsonDeserialize(contentUsing = PVectorDeserializer.class)
    public List<PVector> insectSpawnPointList = new ArrayList<>();

    public transient HashMap<PVector, PFishEntity> activeFishMap = new HashMap<>();

    public transient HashMap<PVector, PSeaFoodEntity> activeSeaFoodMap = new HashMap<>();

    public transient HashMap<PVector, PInsectEntity> activeInsectMap = new HashMap<>();

    public void update(PFishMap pFM) {
        // check loaded status, if the chunk is both unloaded at current execution and previous execution, the chunk won't be updated
        if (this.unloadCountDown < 0) return;

        // remove dead fish
        if (this.unloadCountDown == 0) {
            purgeFish();
            purgeSeaFood();
            purgeInsect();
        } else {
            updateFish(pFM);
            updateSeaFood(pFM);
            updateInsect(pFM);
        }

        // if the chunk is loaded and active fish count is lesser than the maximum amount, spawn fish
        if (this.unloadCountDown > 0 && this.fishSpawnCountDown <= 0) {
            if (this.activeFishMap.size() < PMath.floor((float) this.fishSpawnPointList.size() / pFM.spawnerToFishRatio) || (this.activeFishMap.isEmpty() && pFM.spawnAtLeastOneFish)) {
                // spawn a fish
                PFish fish = spawnFish(pFM);
                if (fish != null && !fishSpawnPointList.isEmpty()) {
                    PVector pV = fishSpawnPointList.get(PMath.ranIndex(fishSpawnPointList.size()));
                    if (!this.activeFishMap.containsKey(pV)) {
                        PFishEntity pFE = new PFishEntity(fish);
                        pFE.spawn(pFM, pV);
                        this.activeFishMap.put(pV, pFE);
                    }
                }
            }
            this.fishSpawnCountDown = PMath.ranInt(pFM.fishSpawnTimeMin, pFM.fishSpawnTimeMax);
        }
        if (this.unloadCountDown > 0 && this.seaFoodSpawnCountDown <= 0) {
            if (this.activeSeaFoodMap.size() < PMath.floor((float) this.seaFoodSpawnPointList.size() / pFM.spawnerToSeaFoodRatio) || (this.activeSeaFoodMap.isEmpty() && pFM.spawnAtLeastOneSeaFood)) {
                // spawn a seafood
                PSeaFood seaFood = spawnSeaFood(pFM);
                if (seaFood != null && !seaFoodSpawnPointList.isEmpty()) {
                    PVector pV = seaFoodSpawnPointList.get(PMath.ranIndex(seaFoodSpawnPointList.size()));
                    if (!this.activeSeaFoodMap.containsKey(pV)) {
                        PSeaFoodEntity pSFE = new PSeaFoodEntity(seaFood);
                        pSFE.spawn(pFM, pV);
                        this.activeSeaFoodMap.put(pV, pSFE);
                    }
                }
            }
            this.seaFoodSpawnCountDown = PMath.ranInt(pFM.seaFoodSpawnTimeMin, pFM.seaFoodSpawnTimeMax);
        }
        if (this.unloadCountDown > 0 && this.insectSpawnCountDown <= 0) {
            if (this.activeInsectMap.size() < PMath.floor((float) this.insectSpawnPointList.size() / pFM.spawnerToInsectRatio) || (this.activeInsectMap.isEmpty() && pFM.spawnAtLeastOneInsect)) {
                // spawn an insect
                PInsect insect = spawnInsect(pFM);
                if (insect != null && !insectSpawnPointList.isEmpty()) {
                    PVector pV = insectSpawnPointList.get(PMath.ranIndex(insectSpawnPointList.size()));
                    if (!this.activeInsectMap.containsKey(pV)) {
                        PInsectEntity pIE = new PInsectEntity(insect);
                        pIE.spawn(pFM, pV);
                        this.activeInsectMap.put(pV, pIE);
                    }
                }
            }
            this.insectSpawnCountDown = PMath.ranInt(pFM.insectSpawnTimeMin, pFM.insectSpawnTimeMax);
        }

        this.fishSpawnCountDown--;
        this.seaFoodSpawnCountDown--;
        this.insectSpawnCountDown--;
        this.unloadCountDown--;
    }

    @JsonIgnore
    public PFish spawnFish(PFishMap pFM) {
        if (this.region != null) return this.region.spawnFish(pFM.world);
        return null;
    }

    @JsonIgnore
    public PSeaFood spawnSeaFood(PFishMap pFM) {
        PType pT = PType.getPType(PSeaFood.class);
        if (pT == null) return null;
        if (pT.getPFileList().isEmpty()) return null;
        List<Float> weightList = new ArrayList<>();
        for (PFile pF : pT.getPFileList()) weightList.add(((PSeaFood) pF).spawnWeight);
        return (PSeaFood) pT.getPFileList().get(PMath.ranIndexWeighted(weightList));
    }

    @JsonIgnore
    public PInsect spawnInsect(PFishMap pFM) {
        PType pT = PType.getPType(PInsect.class);
        if (pT == null) return null;
        if (pT.getPFileList().isEmpty()) return null;
        List<Float> weightList = new ArrayList<>();
        for (PFile pF : pT.getPFileList()) weightList.add(((PInsect) pF).spawnWeight);
        return (PInsect) pT.getPFileList().get(PMath.ranIndexWeighted(weightList));
    }

    public void purgeFish() {
        HashMap<PVector, PFishEntity> map = this.activeFishMap;
        List<PVector> removeKeyList = new ArrayList<>();
        for (PVector pV : map.keySet()) {
            removeKeyList.add(pV);
            map.get(pV).kill();
        }
        for (PVector pV : removeKeyList) {
            map.remove(pV);
        }
    }

    public void purgeSeaFood() {
        HashMap<PVector, PSeaFoodEntity> map = this.activeSeaFoodMap;
        List<PVector> removeKeyList = new ArrayList<>();
        for (PVector pV : map.keySet()) {
            removeKeyList.add(pV);
            map.get(pV).kill();
        }
        for (PVector pV : removeKeyList) {
            map.remove(pV);
        }
    }

    public void purgeInsect() {
        HashMap<PVector, PInsectEntity> map = this.activeInsectMap;
        List<PVector> removeKeyList = new ArrayList<>();
        for (PVector pV : map.keySet()) {
            removeKeyList.add(pV);
            map.get(pV).kill();
        }
        for (PVector pV : removeKeyList) {
            map.remove(pV);
        }
    }

    public void updateFish(PFishMap pFM) {
        HashMap<PVector, PFishEntity> map = this.activeFishMap;
        List<PVector> removeKeyList = new ArrayList<>();
        for (PVector pV : map.keySet()) {
            map.get(pV).update(pFM);
            if (!map.get(pV).getFlagAlive()) removeKeyList.add(pV);
        }
        for (PVector pV : removeKeyList) {
            map.remove(pV);
        }
    }

    public void updateSeaFood(PFishMap pFM) {
        HashMap<PVector, PSeaFoodEntity> map = this.activeSeaFoodMap;
        List<PVector> removeKeyList = new ArrayList<>();
        for (PVector pV : map.keySet()) {
            map.get(pV).update(pFM);
            if (!map.get(pV).getFlagAlive()) removeKeyList.add(pV);
        }
        for (PVector pV : removeKeyList) {
            map.remove(pV);
        }
    }

    public void updateInsect(PFishMap pFM) {
        HashMap<PVector, PInsectEntity> map = this.activeInsectMap;
        List<PVector> removeKeyList = new ArrayList<>();
        for (PVector pV : map.keySet()) {
            map.get(pV).update(pFM);
            if (!map.get(pV).getFlagAlive()) removeKeyList.add(pV);
        }
        for (PVector pV : removeKeyList) {
            map.remove(pV);
        }
    }


    public void unload(PFishMap pFM) {
        purgeFish();
        purgeSeaFood();
        purgeInsect();
        this.activeFishMap.clear();
        this.activeSeaFoodMap.clear();
        this.activeInsectMap.clear();
    }

    public void populate(PFishMap pFM) {
        List<Block> fishBlockList = new ArrayList<>();
        List<Block> insectBlockList = new ArrayList<>();
        List<Block> seaFoodBlockList = new ArrayList<>();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int waterBlockDepth = 0;
                for (int y = pFM.world.getMinHeight(); y <= pFM.world.getMaxHeight() - 1; y++) {
                    Block b = pFM.world.getChunkAt(this.chunkIndexX, this.chunkIndexZ).getBlock(x, y, z);
                    if (b.getType() == Material.FLOWERING_AZALEA && pFM.world.getChunkAt(this.chunkIndexX, this.chunkIndexZ).getBlock(x, y + 1, z).getType() == Material.AIR) {
                        insectBlockList.add(b);
                    }
                    if (b.getType() != Material.WATER) {
                        waterBlockDepth = 0;
                        continue;
                    }
                    waterBlockDepth++;
                    if (waterBlockDepth < pFM.spawnerWaterDepthMin || waterBlockDepth > pFM.spawnerWaterDepthMax) {
                        continue;
                    }
                    fishBlockList.add(b);
                }
                Block b = pFM.world.getChunkAt(this.chunkIndexX, this.chunkIndexZ).getBlock(x, pFM.seaFoodSpawnerHeight, z);
                if (b.getType() == Material.MUD && pFM.world.getChunkAt(this.chunkIndexX, this.chunkIndexZ).getBlock(x, pFM.seaFoodSpawnerHeight + 1, z).getType() == Material.AIR) {
                    seaFoodBlockList.add(b);
                }
                if (b.getType() == Material.SAND && pFM.world.getChunkAt(this.chunkIndexX, this.chunkIndexZ).getBlock(x, pFM.seaFoodSpawnerHeight + 1, z).getType() == Material.AIR) {
                    seaFoodBlockList.add(b);
                }
            }
        }
        if (fishBlockList.isEmpty() && insectBlockList.isEmpty() && seaFoodBlockList.isEmpty()) return;
        float fishSpawnPointCount = PMath.min(1 / (4 * PMath.pi * pFM.spawnerSpacing * pFM.spawnerSpacing * pFM.spawnerSpacing / 3) * fishBlockList.size(), fishBlockList.size());
        float seaFoodSpawnPointCount = PMath.min(1 / (PMath.pi * pFM.spawnerSpacing * pFM.spawnerSpacing) * seaFoodBlockList.size(), seaFoodBlockList.size());
        float insectSpawnPointCount = insectBlockList.size();

        Set<Block> fishSpawnBlockSet = new HashSet<>();
        for (int i = 0; i < PMath.ceil(fishSpawnPointCount); i++) {
            Block b;
            while (true) {
                b = fishBlockList.get(PMath.ranIndex(fishBlockList.size()));
                if (fishSpawnBlockSet.contains(b)) continue;
                fishSpawnBlockSet.add(b);
                break;
            }
            fishSpawnPointList.add(PVector.fromLocation(b.getLocation().clone().add(0.5, 0.5, 0.5)));
        }

        Set<Block> seaFoodSpawnBlockSet = new HashSet<>();
        for (int i = 0; i < PMath.ceil(seaFoodSpawnPointCount); i++) {
            Block b;
            while (true) {
                b = seaFoodBlockList.get(PMath.ranIndex(seaFoodBlockList.size()));
                if (seaFoodSpawnBlockSet.contains(b)) continue;
                seaFoodSpawnBlockSet.add(b);
                break;
            }
            seaFoodSpawnPointList.add(PVector.fromLocation(b.getLocation().clone().add(0.5, 0.5, 0.5)));
        }

        Set<Block> insectSpawnBlockSet = new HashSet<>();
        for (int i = 0; i < PMath.ceil(insectSpawnPointCount); i++) {
            Block b;
            while (true) {
                b = insectBlockList.get(PMath.ranIndex(insectBlockList.size()));
                if (insectSpawnBlockSet.contains(b)) continue;
                insectSpawnBlockSet.add(b);
                break;
            }
            insectSpawnPointList.add(PVector.fromLocation(b.getLocation().clone().add(0.5, 0.5, 0.5)));
        }
    }
}
