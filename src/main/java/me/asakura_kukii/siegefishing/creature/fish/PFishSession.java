package me.asakura_kukii.siegefishing.creature.fish;

import me.asakura_kukii.siegecore.trigger.PTask;
import me.asakura_kukii.siegecore.util.format.PFormat;
import me.asakura_kukii.siegecore.util.math.PAxis;
import me.asakura_kukii.siegecore.util.math.PMath;
import me.asakura_kukii.siegecore.util.math.PQuaternion;
import me.asakura_kukii.siegecore.util.math.PVector;
import me.asakura_kukii.siegefishing.bait.PFishBait;
import me.asakura_kukii.siegefishing.creature.fish.skill.PFishSkillHoldTight;
import me.asakura_kukii.siegefishing.deco.PDeco;
import me.asakura_kukii.siegefishing.effect.PParticle;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.map.PFishChunk;
import me.asakura_kukii.siegefishing.map.PFishMap;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import me.asakura_kukii.siegefishing.rod.PFishRod;
import me.asakura_kukii.siegefishing.util.ItemDisplayHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

import java.util.HashSet;

public class PFishSession {

    private PFishMap fishMap = null;

    private PFishChunk fishChunk = null;

    private PFishPlayer fishPlayer = null;

    private Player player = null;

    private PFishRod rod = null;

    private PFishBait bait = null;

    private float baitStrengthReduction = 0F;

    private float baitHealthReduction = 0F;

    private PFishEntity fishEntity = null;

    private ItemDisplay floatEntity = null;

    private ItemDisplay baitEntity = null;

    private PVector location = new PVector();

    private PVector velocity = new PVector();

    private PQuaternion rotation = new PQuaternion();

    private PFishSessionStatus status = PFishSessionStatus.NULL;

    private int runTickTime = 0;

    private float wireLength = 0F;

    private float progress = 0F;

    private boolean flagInWater = false;

    private boolean flagSkillButton = false;

    private int skillCoolDown = 0;

    private int skillHoldTickTime = 0;

    private int skillSuccessCount = 0;

    public void setSpecialProgressForHoldTight(int specialProgressForHoldTight) {
        this.specialProgressForHoldTight = specialProgressForHoldTight;
    }

    private int specialProgressForHoldTight = -1;

    private boolean flagSkillHold = false;

    private static final float collisionMinimalDistance = 0.01F;

    private static final float waterLevelBias = 0.75F;

    private static final float minWireLength = 3.0F;

    private static final float getTickTime = 15;

    private static final HashSet<Material> inWaterExtraMaterialSet = new HashSet<>();

    static {
        inWaterExtraMaterialSet.add(Material.WATER);
        inWaterExtraMaterialSet.add(Material.KELP);
        inWaterExtraMaterialSet.add(Material.KELP_PLANT);
        inWaterExtraMaterialSet.add(Material.SEAGRASS);
        inWaterExtraMaterialSet.add(Material.TALL_SEAGRASS);
    }

    public PFishSession(PFishMap fishMap, PFishChunk fishChunk, PFishPlayer fishPlayer, Player p, PFishRod rod) {
        this.fishMap = fishMap;
        this.fishChunk = fishChunk;
        this.fishPlayer = fishPlayer;
        this.player = p;
        this.rod = rod;
    }

    public float getMaximumProgress() {
        if (this.rod == null || this.fishEntity == null) return 0;
        return this.fishEntity.getFishLeveledDiffedHealth() * (1 - this.baitHealthReduction) + this.rod.getLeveledHealth(this.player);
    }

    public PFishSessionStatus getStatus() {
        return status;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public void castRod() {
        // safety
        if (getFlagUnsafe()) {
            return;
        }

        // check axis
        PAxis pA = this.fishPlayer.getAxis(this.rod.bias);
        if (pA == null) {
            this.status = PFishSessionStatus.NULL;
            return;
        }

        World w = this.fishMap.world;
        this.location = pA.getO();
        this.velocity = (PVector) pA.getZ().clone().normalize().mul(this.rod.speed);
        this.wireLength = this.rod.wireLength;
        this.rotation = new PVector(0, 0, 1).rotationToExceptZ(pA.getZ());
        this.floatEntity = ItemDisplayHandler.spawn(w, this.location, new PVector(), this.rotation, this.rod.floatDecoScale, this.rod.floatDeco);

        PDeco baitDeco = this.fishPlayer.bait == null ? null : this.fishPlayer.bait.baitDeco;
        this.baitEntity = ItemDisplayHandler.spawn(w, this.location, new PVector(), this.rotation, this.rod.floatDecoScale, baitDeco);

        // switch status
        this.status = PFishSessionStatus.CAST;
        this.runTickTime = 0;

        // play effects
        this.fishPlayer.sendHint(this.rod.castHint);
        for (PSound pS : this.rod.castSound) pS.play(this.player, PVector.fromLocation(this.player.getLocation()));
        for (PParticle pP : this.rod.castParticle) pP.spawn(this.player, this.location);
    }

    public void cutWire() {
        // kill rod due to left-click
        // special kill the fish if the status is goal
        if (this.status == PFishSessionStatus.GOAL && this.fishEntity != null) this.fishEntity.kill();

        // switch status
        this.status = PFishSessionStatus.NULL;
        this.runTickTime = 0;

        // safety check
        if (getFlagUnsafe()) return;

        PAxis pA = this.fishPlayer.getAxis(this.rod.bias);
        if (pA == null) return;

        // play effects
        this.fishPlayer.sendHint(this.rod.cutHint);
        for (PSound pS : this.rod.snapSound) pS.play(this.player, PVector.fromLocation(this.player.getLocation()));
        for (PParticle pP : this.rod.snapParticle)  pP.spawn(this.player, pA.getO());
    }

    public void pullWire() {
        // safety
        if (getFlagUnsafe()) {
            return;
        }

        // pull wire in cast
        if (this.wireLength > minWireLength) {
            this.wireLength = this.wireLength - this.rod.pullSpeed;
            if (this.status == PFishSessionStatus.CAST) for (PSound pS : this.rod.operateSound) pS.play(this.player, PVector.fromLocation(this.player.getLocation()));
        }

        // if status is bite
        if (this.status != PFishSessionStatus.BITE) return;

        // safety
        if (this.fishEntity == null) {
            this.status = PFishSessionStatus.NULL;
            return;
        }

        if (getFlagHit()) {
            this.progress = this.progress + this.rod.getLeveledStrength(this.player);
            for (PSound pS : this.rod.operateSound) pS.play(this.player, PVector.fromLocation(this.player.getLocation()));
        } else {
            if (this.specialProgressForHoldTight < 0) {
                this.progress = this.progress - this.rod.getLeveledStrength(this.player) * this.rod.strengthReflectRatio;
            }
            for (PSound pS : this.rod.stuckSound) pS.play(this.player, PVector.fromLocation(this.player.getLocation()));
        }
    }

    public void castSkill() {
        // status check
        if (this.status != PFishSessionStatus.BITE || this.rod.skill == null) return;

        // safety
        if (getFlagUnsafe()) {
            return;
        }

        this.flagSkillButton = true;
    }

    public void update() {
        // safety
        if (getFlagUnsafe()) {
            this.status = PFishSessionStatus.NULL;
        }

        // if entities are missing, kill this session
        if (floatEntity == null || floatEntity.isDead()) this.status = PFishSessionStatus.NULL;
        if (baitEntity == null || baitEntity.isDead()) this.status = PFishSessionStatus.NULL;

        // update cast session
        if (status == PFishSessionStatus.CAST) updateCast();

        // update bite session
        if (status == PFishSessionStatus.BITE) updateBite();

        // update goal session
        if (status == PFishSessionStatus.GOAL) updateGoal();

        // update null session
        if (status == PFishSessionStatus.NULL) updateNull();

        // increment tick time
        this.runTickTime++;
    }

    private void updateCast() {
        // safety checked
        // get axis
        PAxis pA = this.fishPlayer.getAxis(this.rod.bias);
        if (pA == null) {
            this.status = PFishSessionStatus.NULL;
            return;
        }

        // check wire length
        if (this.location.distance(pA.getO()) > this.rod.wireLength * 2) {
            this.status = PFishSessionStatus.NULL;
            this.fishPlayer.sendHint(this.rod.snapHint);
            for (PSound pS : this.rod.snapSound) pS.play(this.player, PVector.fromLocation(this.player.getLocation()));
            for (PParticle pP : this.rod.snapParticle)  pP.spawn(this.player, pA.getO());
            return;
        }

        // check whether the float is in water
        PVector probe = (PVector) this.location.clone().add(0, waterLevelBias, 0);
        BlockData bD = probe.getLocation(this.fishMap.world).getBlock().getBlockData();
        Material m = bD.getMaterial();
        boolean flagInWater = inWaterExtraMaterialSet.contains(m) || (bD instanceof Waterlogged && ((Waterlogged) bD).isWaterlogged());

        // splash detection
        if (!this.flagInWater && flagInWater && this.velocity.length() > 0.5F) {
            for (PSound pS : this.rod.splashSound) pS.play(this.player, (PVector) probe.clone().sub(this.velocity));
            for (PParticle pP : this.rod.splashParticle) pP.spawn(this.player, (PVector) probe.clone().sub(this.velocity));
            for (PParticle pP : this.rod.bubbleParticle) pP.spawn(this.player, probe.clone());
        }

        // ambient particle controlled by a percentage
        if (flagInWater && PMath.ranBoolean(0.1F)) {
            for (PParticle pP : this.rod.ambientParticle) pP.spawn(this.player, this.location);
        }

        // gravity and drag
        PVector velocity = this.velocity.clone();
        if (flagInWater) {
            velocity.mul(0.8F);
            velocity.add(0, 0.02F, 0);
        } else {
            velocity.mul(0.98F);
            velocity.add(0, -0.02F, 0);
        }

        // acceleration
        velocity.add(getPullAcceleration(this.location));

        // speed limit
        float speed = velocity.length();
        if (flagInWater && speed > 0.5F) {
            velocity.normalize().mul(0.5F);
            speed = 0.5F;
        }
        if (!flagInWater && speed > 1.0F) {
            velocity.normalize();
            speed = 1F;
        }

        this.flagInWater = flagInWater;

        // collision
        if (speed != 0) {
            RayTraceResult rTR = fishMap.world.rayTraceBlocks(this.location.getLocation(fishMap.world), velocity.getVector(), speed + collisionMinimalDistance, FluidCollisionMode.NEVER, true);
            if (rTR != null && rTR.getHitBlock() != null && rTR.getHitBlockFace() != null) {
                PVector normalVector = (PVector) PVector.fromVector(rTR.getHitBlockFace().getDirection()).mul(collisionMinimalDistance);
                PVector translation = (PVector) PVector.fromVector(rTR.getHitPosition()).add(normalVector);
                velocity = (PVector) translation.clone().sub(this.location);
                this.location = translation;
            } else {
                this.location.add(velocity);
            }
        }

        // update item display
        PVector direction = (PVector) velocity.clone().add(0, -0.5F, 0);
        if (direction.length() == 0) direction = new PVector(0, -1, 0);
        direction.normalize();
        this.rotation = (PQuaternion) new PVector(0, 0, 1).rotationTo(direction, new PQuaternion());
        this.velocity = velocity;
        ItemDisplayHandler.update(this.floatEntity, this.location.clone(), (PVector) new PVector(0, 0, -waterLevelBias).rotate(this.rotation), this.rotation, this.rod.floatDecoScale, 1, false);
        ItemDisplayHandler.update(this.baitEntity, this.location.clone(), (PVector) new PVector(0, 0, -waterLevelBias).rotate(this.rotation), this.rotation, this.rod.floatDecoScale, 1, false);

        if (flagInWater) {
            // bait attraction
            if (this.fishPlayer.bait != null) this.fishEntity = this.fishPlayer.bait.attract(fishMap, this.location, this.fishPlayer.baitItemStack);
            // check for bite
            if (this.fishEntity != null) {
                // update fish chunk
                this.fishChunk = this.fishMap.getFishChunk(this.location);
                this.fishEntity.bite(this, this.location.clone(), this.fishPlayer.baitItemStack);
                float[] modifierList = PFishBait.getBaitModifierList(this.fishPlayer.baitItemStack);
                // consume bait
                this.baitStrengthReduction = modifierList[2];
                this.baitHealthReduction = modifierList[0];
                this.bait = this.fishPlayer.bait;
                this.fishPlayer.bait = null;
                this.fishPlayer.baitItemStack = null;
                // init progress
                this.progress = this.rod.getLeveledHealth(this.player);
                // switch status
                this.status = PFishSessionStatus.BITE;
                this.runTickTime = 0;
                // player effects
                for (PParticle pP : this.rod.biteParticle) pP.spawn(this.player, this.fishEntity.getLocation());
                for (PSound pS : this.rod.biteSound) pS.play(this.player, this.fishEntity.getLocation());
            }
        }
    }

    private void updateBite() {
        // get axis
        PAxis pA = this.fishPlayer.getAxis(this.rod.bias);
        if (pA == null) {
            this.status = PFishSessionStatus.NULL;
            return;
        }

        // check wire length
        if (this.location.distance(pA.getO()) > this.rod.wireLength * 2) {
            this.status = PFishSessionStatus.NULL;
            this.fishPlayer.sendHint(this.rod.fishEscapeHint);
            for (PSound pS : this.rod.snapSound) pS.play(this.player, PVector.fromLocation(this.player.getLocation()));
            for (PParticle pP : this.rod.snapParticle) pP.spawn(this.player, pA.getO());
            return;
        }

        // check fish entity
        if (this.fishEntity == null) {
            this.status = PFishSessionStatus.NULL;
            return;
        }

        // ambient effects
        if (this.flagInWater && PMath.ranBoolean(0.1F)) {
            for (PParticle pP : this.rod.fishAmbientParticle) pP.spawn(this.player, this.fishEntity.getLocation());
            for (PSound pS : this.rod.fishAmbientSound) pS.play(this.player, this.fishEntity.getLocation());
        }

        // dynamics
        PVector location = this.fishEntity.getMouthLocation().clone();
        PVector velocity = (PVector) location.clone().sub(this.location);
        PVector direction = (PVector) velocity.clone().add(0, -0.5F, 0);
        if (direction.length() == 0) direction = new PVector(0, -1, 0);
        direction.normalize();
        this.rotation = (PQuaternion) new PVector(0, 0, 1).rotationTo(direction, new PQuaternion());
        this.location = location;
        this.velocity = velocity;

        // stretch wire length if the player try to constrain the fish
        PVector pV = (PVector) this.fishEntity.getLocation().clone().sub(pA.getO());
        float distance = pV.length();
        if (distance == 0 || distance > this.wireLength && pV.clone().normalize().dot(new PVector(0, 0, 1).rotate(this.fishEntity.getRotation())) > 0) {
            this.wireLength = this.wireLength + this.rod.pullSpeed * 2;
        }

        // update item display
        ItemDisplayHandler.update(this.floatEntity, location, (PVector) new PVector(0, 0, -waterLevelBias).rotate(this.rotation), this.rotation, this.rod.floatDecoScale, 1, false);
        ItemDisplayHandler.update(this.baitEntity, location, (PVector) new PVector(0, 0, -waterLevelBias).rotate(this.rotation), this.rotation, this.rod.floatDecoScale, 1, false);

        // get fish
        PFish fish = this.fishEntity.getFish();

        // progress froze count down
        // update progress due to fish strength
        if (this.specialProgressForHoldTight >= 0) this.specialProgressForHoldTight--;
        if (this.specialProgressForHoldTight == 0) this.progress = 0;
        if (this.specialProgressForHoldTight < 0) {
            this.progress = this.progress - this.fishEntity.getFishLeveledDiffedStrength() * (1 - this.baitStrengthReduction);
        }

        // skill cast
        // skill cool down
        String iconString = "";
        if (this.rod.skill != null) {
            if (this.rod.skill instanceof PFishSkillHoldTight) {
                ((PFishSkillHoldTight) this.rod.skill).tickHoldTight(this, this.fishPlayer, this.rod, this.fishEntity, this.specialProgressForHoldTight);
            }

            if (this.skillCoolDown > 0) this.skillCoolDown--;
            if (this.skillCoolDown == 1) this.rod.skill.notify(this, this.fishPlayer, this.rod, this.fishEntity);
            if (this.skillCoolDown == 0) this.rod.skill.ready(this, this.fishPlayer, this.rod, this.fishEntity);
            PVector iconColor = new PVector(255, 255, 255);
            if (this.rod.skill.coolDown != 0 && this.skillCoolDown != 0) iconColor = (PVector) new PVector(255, 255, 255).mul((this.rod.skill.coolDown - this.skillCoolDown) / 2F / (float) this.rod.skill.coolDown);
            iconString = PFormat.gen(iconColor.r(), iconColor.g(), iconColor.b()) + this.rod.skill.getIcon(this, this.fishPlayer, this.rod, this.fishEntity, this.skillHoldTickTime, this.skillSuccessCount);
            if (this.flagSkillButton && this.skillCoolDown == 0) {
                iconString = this.rod.skill.getCastIcon(this, this.fishPlayer, this.rod, this.fishEntity, this.skillHoldTickTime, this.skillSuccessCount);
                Pair<Boolean, Integer> result = this.rod.skill.cast(this, this.fishPlayer, this.rod, this.fishEntity, this.skillHoldTickTime, this.skillSuccessCount);
                if (result.getLeft()) {
                    this.skillSuccessCount++;
                } else {
                    this.skillSuccessCount = 0;
                }
                this.skillCoolDown = result.getRight();
                if (this.flagSkillHold) this.skillHoldTickTime++;
                this.flagSkillHold = true;
            } else {
                if (this.skillCoolDown > 0) this.rod.skill.early(this, this.fishPlayer, this.rod, this.fishEntity);
                this.skillHoldTickTime = 0;
                this.flagSkillHold = false;
            }
            this.flagSkillButton = false;
        }

        // progress check
        if (this.progress >= this.getMaximumProgress()) {
            // dynamics

            float speed = pV.length() / getTickTime;
            if (speed != 0) {
                PVector pullVelocity = (PVector) pV.clone().normalize().mul(-speed).add(0, PFishEntity.gravity * getTickTime / 2, 0);
                this.fishEntity.turn(pA.getO(), 0, Integer.MAX_VALUE);
                this.fishEntity.setVelocity(pullVelocity);
                this.fishEntity.setFlagSpeedLimit(false);
                this.fishEntity.setFlagActive(false);
                this.fishEntity.setFlagGlow(false);
            }

            // hide float entity
            if (this.floatEntity != null) this.floatEntity.setViewRange(0);
            if (this.baitEntity != null) this.baitEntity.setViewRange(0);

            // register task for item give, this is safer!
            ItemStack iS = fish.getItemStack(1, this.fishEntity.getFishDiff(), PFishEmotion.getEmotion());
            new PTask() {
                @Override
                public void init() {}

                @Override
                public void hold() {}

                @Override
                public void goal() {
                    fishPlayer.giveItemStack(iS);
                }
            }.runPTask((long) getTickTime);

            // switch status
            this.status = PFishSessionStatus.GOAL;
            this.runTickTime = 0;

            // play effects
            this.fishPlayer.sendHint(this.rod.fishRetrieveHint);
            for (PSound pS : this.rod.retrieveSound) pS.play(this.player, PVector.fromLocation(this.player.getLocation()));
            for (PParticle pP : this.rod.retrieveParticle) pP.spawn(this.player, this.fishEntity.getLocation());

            this.fishPlayer.collectFishFromSessionSuccess(this.fishMap, this.fishChunk, this.rod, this.fishEntity.getFish(), this.bait, this.fishEntity.getFishDiffedWeight());

            // give experience
            this.fishPlayer.collectExperience(this.fishEntity.getFish().level.experience);
            return;
        }
        if (this.progress <= 0) {
            // switch status
            this.status = PFishSessionStatus.NULL;
            this.runTickTime = 0;

            // play effects
            this.fishPlayer.sendHint(this.rod.fishSnapHint);
            for (PSound pS : this.rod.snapSound) pS.play(this.player, PVector.fromLocation(this.player.getLocation()));
            for (PParticle pP : this.rod.snapParticle)  pP.spawn(this.player, pA.getO());

            this.fishPlayer.collectFishFromSessionFailure(this.fishMap, this.fishChunk, this.fishEntity.getFish(), this.fishEntity.getFishDiffedWeight());

            return;
        }

        // update title
        sendUI(iconString);
    }

    private void updateGoal() {
        // check fish entity
        if (this.fishEntity == null) {
            this.status = PFishSessionStatus.NULL;
            return;
        }

        if (this.runTickTime >= getTickTime) {
            // kill fish
            fishEntity.kill();

            // switch status
            this.status = PFishSessionStatus.NULL;
            this.runTickTime = 0;

            for (PSound pS : rod.itemSound) pS.play(player, PVector.fromLocation(player.getLocation()));
            for (PParticle pP : rod.itemParticle) pP.spawn(player, fishEntity.getLocation());
        }
    }

    private void updateNull() {
        ItemDisplayHandler.remove(this.floatEntity);
        ItemDisplayHandler.remove(this.baitEntity);
        if (this.fishEntity != null) this.fishEntity.restore();
    }

    public PVector getPullAcceleration(PVector location) {
        // this is the method to control wire length and give acceleration value
        PAxis pA = this.fishPlayer.getAxis(this.rod.bias);
        if (pA == null) return new PVector();
        PVector pV = (PVector) location.clone().sub(pA.getO());
        if (pV.length() >= this.wireLength) {
            return (PVector) pV.clone().normalize().mul(-(pV.length() - this.wireLength) * 0.02F);
        }
        return new PVector();
    }

    public boolean getFlagHit() {
        if (this.status != PFishSessionStatus.BITE || this.fishEntity == null) return false;
        PAxis pA = this.fishPlayer.getAxis();
        if (pA == null) return false;
        PVector pV = (PVector) this.fishEntity.getLocation().clone().sub(pA.getO());
        float difference = pA.getZ().clone().mul(pV.length()).sub(pV).length();
        return difference <= this.fishEntity.getFishDiffedScale();
    }

    private boolean getFlagUnsafe() {
        return this.player == null || this.fishMap == null || this.fishMap.world == null || !this.player.isOnline() || this.player.isDead() || this.player.getWorld() != this.fishMap.world;
    }

    private void sendUI(String iconString) {
        float ratio = this.progress / this.getMaximumProgress();
        int leftBarCount = (int) PMath.floor(ratio * 32);
        String bar = "$ui.bar." + leftBarCount + "$";
        this.player.sendTitle(PFormat.format(iconString), PFormat.format(bar), 0, 4, 0);
    }
}
