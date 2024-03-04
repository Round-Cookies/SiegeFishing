package me.asakura_kukii.siegefishing.creature.insect;

import me.asakura_kukii.lib.jackson.annotation.JsonIgnore;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.io.helper.PFileIdDeserializer;
import me.asakura_kukii.siegecore.io.helper.PFileIdSerializer;
import me.asakura_kukii.siegecore.util.math.PMath;
import me.asakura_kukii.siegecore.util.math.PQuaternion;
import me.asakura_kukii.siegecore.util.math.PVector;
import me.asakura_kukii.siegefishing.effect.PParticle;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.map.PFishMap;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import me.asakura_kukii.siegefishing.util.ItemDisplayHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PInsectEntity {

    public PInsectEntity() {}

    public PInsectEntity(PInsect insect) {
        this.insect = insect;
    }

    @JsonIgnore
    public boolean getFlagAlive() {
        return flagAlive;
    }

    // fish info
    @JsonSerialize(using = PFileIdSerializer.class)
    @JsonDeserialize(using = PFileIdDeserializer.class)
    private PInsect insect = null;

    public PVector location = new PVector();

    public ItemDisplay entity = null;

    public boolean flagAlive = true;

    public int tick = 0;

    public void spawn(PFishMap pFM, PVector location) {
        this.location = (PVector) location.clone().add(0, 0.5F + this.insect.decoBias, 0);
        this.entity = ItemDisplayHandler.spawn(pFM.world, (PVector) this.location, new PVector(), (PQuaternion) new PQuaternion().rotateY(PMath.ranFloat(0F, PMath.pi * 2)), 1, insect.deco);
        this.entity.setViewRange(32);
        this.flagAlive = true;
    }

    public void kill() {
        ItemDisplayHandler.remove(this.entity);
        this.flagAlive = false;
    }

    public void net(PFishMap pFM, Player p) {
        kill();
        if (this.insect == null) return;
        PType pT = PType.getPType(PFishPlayer.class);
        if (pT == null) return;
        PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
        if (pFP == null) return;
        for (PParticle pP : this.insect.itemParticle) pP.spawn(pFM.world, this.location);
        for (PSound pS : this.insect.itemSound) pS.play(pFM.world, this.location);
        pFP.sendHint(this.insect.itemHint);
        ItemStack iS = this.insect.getItemStack();
        pFM.world.dropItemNaturally(this.location.getLocation(pFM.world), iS);
        pFP.collectInsectFromSession(this.insect, 0, true);
    }

    public void flee(PFishMap pFM, Player p) {
        kill();
        if (this.insect == null) return;
        PType pT = PType.getPType(PFishPlayer.class);
        if (pT == null) return;
        PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
        if (pFP == null) return;
        for (PParticle pP : this.insect.fleeParticle) pP.spawn(pFM.world, this.location);
        for (PSound pS : this.insect.fleeSound) pS.play(pFM.world, this.location);
        pFP.sendHint(this.insect.fleeHint);
    }

    public void update(PFishMap pFM) {
        if (this.entity == null || this.entity.isDead()) {
            kill();
            return;
        }

        if (this.insect == null) {
            kill();
            return;
        }

        PType pT = PType.getPType(PFishPlayer.class);
        if (pT == null) return;

        if (PMath.ranBoolean(this.insect.stillFrequency)) {
            for (PSound pS : this.insect.stillSound) pS.play(pFM.world, (PVector) this.location.clone().add(0, 0, 0));
            for (PParticle pP : this.insect.stillParticle) pP.spawn(pFM.world, (PVector) this.location.clone().add(0, 0, 0));
        }

        this.tick++;
        if (this.tick % this.insect.detectCoolDown == 0) {
            for (Entity e : pFM.world.getNearbyEntities(this.location.getLocation(pFM.world), this.insect.detectRadius, this.insect.detectRadius, this.insect.detectRadius)) {
                if (e instanceof Player) {
                    Player p = (Player) e;
                    PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
                    if (!p.isSneaking() && pFP.velocity.length() > this.insect.speedLimit) {
                        flee(pFM, p);
                        break;
                    }
                }
            }
        }
    }
}
