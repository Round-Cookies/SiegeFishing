package me.asakura_kukii.siegefishing.creature.seafood;

import me.asakura_kukii.lib.jackson.annotation.JsonIgnore;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.io.helper.*;
import me.asakura_kukii.siegecore.util.math.PMath;
import me.asakura_kukii.siegecore.util.math.PQuaternion;
import me.asakura_kukii.siegecore.util.math.PVector;
import me.asakura_kukii.siegefishing.effect.PParticle;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.map.PFishMap;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import me.asakura_kukii.siegefishing.util.ItemDisplayHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PSeaFoodEntity {

    public PSeaFoodEntity() {}

    public PSeaFoodEntity(PSeaFood pSF) {
        this.seaFood = pSF;
    }

    @JsonIgnore
    public boolean getFlagAlive() {
        return flagAlive;
    }

    // fish info
    @JsonSerialize(using = PFileIdSerializer.class)
    @JsonDeserialize(using = PFileIdDeserializer.class)
    private PSeaFood seaFood = null;

    public PVector location = new PVector();

    public PVector target = null;

    public ItemDisplay entity = null;

    public boolean flagAlive = true;

    public boolean flagDig = false;

    public int catchCount = 0;

    public int fleeCountDown = -1;

    public int getCountDown = -1;

    public PFishPlayer lastFishPlayer;

    public void spawn(PFishMap pFM, PVector location) {
        this.location = location.clone();
        this.flagAlive = true;
        this.flagDig = true;
    }

    public void kill() {
        ItemDisplayHandler.remove(this.entity);
        this.flagAlive = false;
    }

    public void dig(PFishMap pFM, Player p) {
        if (!this.flagDig) return;
        if (this.entity != null) return;
        this.catchCount++;
        this.fleeCountDown = this.seaFood.fleeCountDown;
        PType pT = PType.getPType(PFishPlayer.class);
        if (pT == null) return;
        PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
        if (pFP == null) return;
        this.lastFishPlayer = pFP;

        if (this.catchCount >= this.seaFood.catchCount) {
            for (PParticle pP : this.seaFood.getSuccessParticle) pP.spawn(pFM.world, (PVector) this.location.clone().add(0, 0.5F, 0));
            for (PSound pS : this.seaFood.getSuccessSound) pS.play(pFM.world, (PVector) this.location.clone().add(0, 0.5F, 0));
            pFP.sendHint(seaFood.getSuccessfulHint);
            this.entity = ItemDisplayHandler.spawn(pFM.world, (PVector) this.location.clone().add(0, 0.5F + this.seaFood.decoBias, 0), new PVector(), (PQuaternion) new PQuaternion().rotateY(PMath.ranFloat(0, PMath.pi * 2)), 1, this.seaFood.deco);
            this.entity.setViewRange(32);
            this.getCountDown = 40;
            this.fleeCountDown = -1;
            this.flagDig = false;
        } else {
            List<Block> blockList = new ArrayList<>();
            for (int x = -this.seaFood.fleeRadius; x <= this.seaFood.fleeRadius; x++) {
                for (int z = -this.seaFood.fleeRadius; z <= this.seaFood.fleeRadius; z++) {
                    Location l = ((PVector) this.location.clone().add(x, 0, z)).getLocation(pFM.world);
                    if (l.getBlock().getType() == Material.MUD || l.getBlock().getType() == Material.SAND) {
                        blockList.add(l.getBlock());
                    }
                }
            }
            if (!blockList.isEmpty()) {
                Block b = blockList.get(PMath.ranIndex(blockList.size()));
                this.target = PVector.fromLocation(b.getLocation().add(0.5, 0.5, 0.5));
            }
            for (PParticle pP : this.seaFood.digSuccessParticle) pP.spawn(pFM.world, (PVector) this.location.clone().add(0, 0.5F, 0));
            for (PSound pS : this.seaFood.digSuccessSound) pS.play(pFM.world, (PVector) this.location.clone().add(0, 0.5F, 0));
            pFP.sendHint(seaFood.digSuccessfulHint);
        }
    }

    @JsonIgnore
    public void get(PFishMap pFM) {
        ItemStack iS = this.seaFood.getItemStack();
        pFM.world.dropItemNaturally(this.location.getLocation(pFM.world), iS);
        if (this.lastFishPlayer == null) return;
        for (PParticle pP : this.seaFood.itemParticle) pP.spawn(pFM.world, (PVector) this.location.clone().add(0, 0.5F, 0));
        for (PSound pS : this.seaFood.itemSound) pS.play(pFM.world, (PVector) this.location.clone().add(0, 0.5F, 0));
        this.lastFishPlayer.collectSeaFoodFromSession(this.seaFood);
    }

    public void flee(PFishMap pFM) {
        kill();
        if (this.lastFishPlayer == null) return;
        for (PParticle pP : this.seaFood.fleeParticle) pP.spawn(pFM.world, (PVector) this.location.clone().add(0, 0.5F, 0));
        for (PSound pS : this.seaFood.fleeSound) pS.play(pFM.world, (PVector) this.location.clone().add(0, 0.5F, 0));
        this.lastFishPlayer.sendHint(seaFood.fleeHint);
    }

    public void update(PFishMap pFM) {

        if (getCountDown > 0) getCountDown--;
        if (getCountDown == 0) {
            this.get(pFM);
            this.kill();
        }

        if (this.target != null) {
            if (this.target.clone().sub(this.location).length() < 0.5) {
                this.location = this.target;
                this.target = null;
                this.flagDig = true;
                for (PSound pS : seaFood.stillSound) pS.play(pFM.world, (PVector) this.location.clone().add(0, 0.5F, 0));
                for (PParticle pP : seaFood.stillParticle) pP.spawn(pFM.world, (PVector) this.location.clone().add(0, 0.5F, 0));
            } else {
                this.location.add(this.target.clone().sub(this.location).normalize().mul(this.seaFood.speed));
                this.flagDig = false;
                if (PMath.ranBoolean(seaFood.movingFrequency)) {
                    for (PSound pS : seaFood.movingSound) pS.play(pFM.world, (PVector) this.location.clone().add(0, 0.5F, 0));
                    for (PParticle pP : seaFood.movingParticle) pP.spawn(pFM.world, (PVector) this.location.clone().add(0, 0.5F, 0));
                }

            }
        } else {
            if (getCountDown < 0) {
                if (fleeCountDown > 0) fleeCountDown--;
                if (fleeCountDown == 0) this.flee(pFM);
                if (PMath.ranBoolean(seaFood.stillFrequency)) {
                    for (PSound pS : seaFood.stillSound) pS.play(pFM.world, (PVector) this.location.clone().add(0, 0.5F, 0));
                    for (PParticle pP : seaFood.stillParticle) pP.spawn(pFM.world, (PVector) this.location.clone().add(0, 0.5F, 0));
                }
            }
            this.flagDig = true;
        }
    }
}
