package me.asakura_kukii.siegefishing.map;

import me.asakura_kukii.siegecore.io.PFile;
import me.asakura_kukii.siegecore.trigger.PTask;
import me.asakura_kukii.siegecore.util.math.PMath;
import me.asakura_kukii.siegecore.util.math.PVector;
import me.asakura_kukii.siegefishing.creature.fish.PFishEntity;
import me.asakura_kukii.siegefishing.creature.insect.PInsectEntity;
import me.asakura_kukii.siegefishing.creature.seafood.PSeaFoodEntity;
import me.asakura_kukii.siegefishing.util.ItemDisplayHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PFishMap extends PFile {

    public PFishMap() {}

    public transient World world = null;

    public int chunkXMin = 0;

    public int chunkXMax = 16;

    public int chunkZMin = 0;

    public int chunkZMax = 16;

    public int spawnerSpacing = 2;

    public int spawnerToFishRatio = 3;

    public boolean spawnAtLeastOneFish = true;

    public int spawnerToSeaFoodRatio = 3;

    public boolean spawnAtLeastOneSeaFood = true;

    public int spawnerToInsectRatio = 1;

    public boolean spawnAtLeastOneInsect = false;

    public int seaFoodSpawnerHeight = 63;

    public int spawnerWaterDepthMin = 3;

    public int spawnerWaterDepthMax = 9;

    public int fishSpawnTimeMin = 1200;

    public int fishSpawnTimeMax = 6000;

    public int seaFoodSpawnTimeMin = 1200;

    public int seaFoodSpawnTimeMax = 6000;

    public int insectSpawnTimeMin = 1200;

    public int insectSpawnTimeMax = 6000;

    public int playerChunkLoadRadius = 2;

    public int playerChunkLoadPerTick = 3;

    public List<List<PFishChunk>> chunkListList = new ArrayList<>();

    public transient int playerChunkLoadGroupIndex = 0;

    public PFishChunk getFishChunk(PVector pV) {
        int x = (int) PMath.floor(pV.x) >> 4;
        int z = (int) PMath.floor(pV.z) >> 4;
        if (x < this.chunkXMin || x > this.chunkXMax) return null;
        if (x - this.chunkXMin >= this.chunkListList.size()) return null;
        List<PFishChunk> fishChunkList = this.chunkListList.get(x - this.chunkXMin);
        if (fishChunkList == null || fishChunkList.isEmpty()) return null;
        if (z < this.chunkZMin || z > this.chunkZMax) return null;
        if (z - this.chunkZMin >= fishChunkList.size()) return null;
        PFishChunk pFC = fishChunkList.get(z - this.chunkZMin);
        if (pFC == null) return null;
        return pFC;
    }

    public List<PInsectEntity> getInsectEntityList(PVector pV, int chunkRadius) {
        List<PInsectEntity> insectEntityList = new ArrayList<>();
        int x = (int) PMath.floor(pV.x) >> 4;
        int z = (int) PMath.floor(pV.z) >> 4;
        for (int i = x - chunkRadius; i <= x + chunkRadius; i++) {
            if (i < this.chunkXMin || i > this.chunkXMax) continue;
            if (i - this.chunkXMin >= this.chunkListList.size()) continue;
            List<PFishChunk> fishChunkList = this.chunkListList.get(i - this.chunkXMin);
            if (fishChunkList == null || fishChunkList.isEmpty()) continue;
            for (int j = z - chunkRadius; j <= z + chunkRadius; j++) {
                if (j < this.chunkZMin || j > this.chunkZMax) continue;
                if (j - this.chunkZMin >= fishChunkList.size()) continue;
                PFishChunk pFC = fishChunkList.get(j - this.chunkZMin);
                if (pFC == null) continue;
                insectEntityList.addAll(pFC.activeInsectMap.values());
            }
        }
        return insectEntityList;
    }

    public List<PSeaFoodEntity> getSeaFoodEntityList(PVector pV, int chunkRadius) {
        List<PSeaFoodEntity> seaFoodEntityList = new ArrayList<>();
        int x = (int) PMath.floor(pV.x) >> 4;
        int z = (int) PMath.floor(pV.z) >> 4;
        for (int i = x - chunkRadius; i <= x + chunkRadius; i++) {
            if (i < this.chunkXMin || i > this.chunkXMax) continue;
            if (i - this.chunkXMin >= this.chunkListList.size()) continue;
            List<PFishChunk> fishChunkList = this.chunkListList.get(i - this.chunkXMin);
            if (fishChunkList == null || fishChunkList.isEmpty()) continue;
            for (int j = z - chunkRadius; j <= z + chunkRadius; j++) {
                if (j < this.chunkZMin || j > this.chunkZMax) continue;
                if (j - this.chunkZMin >= fishChunkList.size()) continue;
                PFishChunk pFC = fishChunkList.get(j - this.chunkZMin);
                if (pFC == null) continue;
                seaFoodEntityList.addAll(pFC.activeSeaFoodMap.values());
            }
        }
        return seaFoodEntityList;
    }

    public List<PFishEntity> getFishEntityList(PVector pV, int chunkRadius) {
        List<PFishEntity> fishEntityList = new ArrayList<>();
        int x = (int) PMath.floor(pV.x) >> 4;
        int z = (int) PMath.floor(pV.z) >> 4;
        for (int i = x - chunkRadius; i <= x + chunkRadius; i++) {
            if (i < this.chunkXMin || i > this.chunkXMax) continue;
            if (i - this.chunkXMin >= this.chunkListList.size()) continue;
            List<PFishChunk> fishChunkList = this.chunkListList.get(i - this.chunkXMin);
            if (fishChunkList == null || fishChunkList.isEmpty()) continue;
            for (int j = z - chunkRadius; j <= z + chunkRadius; j++) {
                if (j < this.chunkZMin || j > this.chunkZMax) continue;
                if (j - this.chunkZMin >= fishChunkList.size()) continue;
                PFishChunk pFC = fishChunkList.get(j - this.chunkZMin);
                if (pFC == null) continue;
                fishEntityList.addAll(pFC.activeFishMap.values());
            }
        }
        return fishEntityList;
    }

    public void update() {
        if (this.world == null) return;
        List<Player> playerList = this.world.getPlayers();

        int playerCount = playerList.size();
        int playerIndexFrom = this.playerChunkLoadGroupIndex * this.playerChunkLoadPerTick;
        int playerIndexTo = (this.playerChunkLoadGroupIndex + 1) * this.playerChunkLoadPerTick;
        if (playerIndexTo > playerCount) {
            playerIndexTo = playerCount;
            this.playerChunkLoadGroupIndex = 0;
        } else {
            this.playerChunkLoadGroupIndex++;
        }
        for (int index = playerIndexFrom; index < playerIndexTo; index++) {
            Player p = playerList.get(index);
            if (!p.isOnline() || p.isDead()) continue;
            Location l = p.getLocation();
            int x = l.getBlockX() >> 4;
            int z = l.getBlockZ() >> 4;
            for (int i = x - playerChunkLoadRadius; i <= x + playerChunkLoadRadius; i++) {
                if (i < this.chunkXMin || i > this.chunkXMax) continue;
                if (i - this.chunkXMin >= this.chunkListList.size()) continue;
                List<PFishChunk> fishChunkList = this.chunkListList.get(i - this.chunkXMin);
                if (fishChunkList == null || fishChunkList.isEmpty()) continue;
                for (int j = z - playerChunkLoadRadius; j <= z + playerChunkLoadRadius; j++) {
                    if (j < this.chunkZMin || j > this.chunkZMax) continue;
                    if (j - this.chunkZMin >= fishChunkList.size()) continue;
                    PFishChunk pFC = fishChunkList.get(j - this.chunkZMin);
                    if (pFC == null) continue;
                    pFC.unloadCountDown = (int) PMath.ceil(playerCount / (float) this.playerChunkLoadPerTick) + 1;
                }
            }
        }
        for (List<PFishChunk> fishChunkList : this.chunkListList) {
            if (fishChunkList == null || fishChunkList.isEmpty()) continue;
            for (PFishChunk pFC : fishChunkList) {
                if (pFC == null) continue;
                pFC.update(this);
            }
        }
    }

    public void populate() {
        Bukkit.broadcastMessage("Purging all entities and chunks...");
        for (List<PFishChunk> chunkList : this.chunkListList) {
            if (chunkList == null) continue;
            for (PFishChunk pSC : chunkList) {
                if (pSC == null) continue;
                pSC.unload(this);
            }
        }
        this.chunkListList.clear();
        this.chunkListList = new ArrayList<>();
        for (int x = this.chunkXMin; x <= this.chunkXMax; x++) {
            List<PFishChunk> chunkList = new ArrayList<>();
            for (int z = this.chunkZMin; z <= this.chunkZMax; z++) {
                PFishChunk pSC = new PFishChunk(x, z);
                chunkList.add(pSC);
            }
            this.chunkListList.add(chunkList);
        }
        Bukkit.broadcastMessage("Populating designated chunks...");
        PFishMap pFM = this;
        PTask pT = new PTask() {
            int rowIndex = 0;
            int colIndex = 0;
            @Override
            public void init() {}

            @Override
            public void hold() {
                for (int i = 0; i < 16; i++) {
                    List<PFishChunk> fishChunkList = chunkListList.get(rowIndex);
                    if (colIndex >= fishChunkList.size()) {
                        rowIndex++;
                        colIndex = 0;
                        if (rowIndex >= chunkListList.size()) {
                            stop();
                            return;
                        }
                        return;
                    }
                    fishChunkList = chunkListList.get(rowIndex);
                    PFishChunk pSC = fishChunkList.get(colIndex);
                    pSC.populate(pFM);
                    colIndex++;
                    Bukkit.broadcastMessage("Populating fish chunk [" + pSC.chunkIndexX + ", " + pSC.chunkIndexZ + "]");
                }
            }

            @Override
            public void goal() {
                Bukkit.broadcastMessage("Populated all fish chunks");
            }
        };
        pT.runPTask();
    }

    @Override
    public void finalizeDeserialization() throws IOException {
        World world = Bukkit.getWorld(this.id);
        if (world == null) throw new IOException("Could not resolve world [" + this.id + "]");
        this.world = world;
    }

    @Override
    public void defaultValue() {

    }

    @Override
    public void load() {
    }

    @Override
    public void unload() {
        if (this.world == null) return;
        for (List<PFishChunk> fishChunkList : this.chunkListList) {
            if (fishChunkList == null || fishChunkList.isEmpty()) continue;
            for (PFishChunk pFC : fishChunkList) {
                if (pFC == null) continue;
                pFC.unload(this);
            }
        }
        ItemDisplayHandler.removeAll();
    }
}
