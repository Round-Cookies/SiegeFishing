package me.asakura_kukii.siegefishing.creature.fish;

import me.asakura_kukii.lib.jackson.annotation.JsonIgnore;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.io.helper.*;
import me.asakura_kukii.siegecore.util.math.PMath;
import me.asakura_kukii.siegecore.util.math.PQuaternion;
import me.asakura_kukii.siegecore.util.math.PVector;
import me.asakura_kukii.siegefishing.bait.PFishBait;
import me.asakura_kukii.siegefishing.map.PFishMap;
import me.asakura_kukii.siegefishing.util.ItemDisplayHandler;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

import java.util.HashSet;

public class PFishEntity {

    public PFishEntity() {}

    public PFishEntity(PFish pF) {
        this.fish = pF;
        this.diffMultiplier = PMath.ranFloat(pF.diffMultiplierMin, pF.diffMultiplierMax);
    }

    // fish info
    @JsonSerialize(using = PFileIdSerializer.class)
    @JsonDeserialize(using = PFileIdDeserializer.class)
    private PFish fish = null;

    private float diffMultiplier = 1.0F;

    // pose info
    @JsonSerialize(using = PVectorSerializer.class)
    @JsonDeserialize(using = PVectorDeserializer.class)
    private PVector anchor = new PVector();

    @JsonSerialize(using = PVectorSerializer.class)
    @JsonDeserialize(using = PVectorDeserializer.class)
    private PVector location = new PVector();

    @JsonSerialize(using = PQuaternionSerializer.class)
    @JsonDeserialize(using = PQuaternionDeserializer.class)
    private PQuaternion rotation = new PQuaternion();

    private transient PVector velocity = new PVector();

    // state info
    private transient PFishSession session = null;

    private transient PVector turnTarget = null;
    private transient float turnTolerance = 1.0F;
    private transient float turnPeriod = 80F;
    private transient float turnSpeed = 0.1F;
    private transient float bendOffset = 0;

    private transient int tick = 0;
    private transient int turnCountDown = 0;
    private transient int attractCountDown = -1;
    private transient int frozeCountDown = -1;
    private transient int buffCountDown = -1;
    private transient float buffTurnPeriodMultiplier = 1;
    private transient float buffTurnSpeedMultiplier = 1;
    private transient int collisionCountDown = collisionKillTickTime;

    private transient boolean flagAlive = false;
    private transient boolean flagGlow = false;
    private transient boolean flagSpeedLimit = true;
    private transient boolean flagActive = true;

    // transient info
    private transient float turnYaw = 0.0F;
    private transient float turnPitch = 0.0F;
    private transient float turnBend = 0.0F;
    private transient float turnAcceleration = 0.1F;
    private transient boolean flagCollision = false;

    private transient PVector mouthLocation = new PVector();
    private transient ItemDisplay headEntity = null;
    private transient ItemDisplay bodyEntity = null;
    private transient ItemDisplay tailEntity = null;

    // constants
    public static final float step = 1F;
    public static final float period = 8F;
    public static final float zeta = 0.25F * 2F * PMath.pi / period;
    public static final float omega = PMath.sqrt(4F * PMath.pi * PMath.pi / period / period - zeta * zeta);
    public static final float typicalTurnTolerance = PMath.pi / 4;
    public static final float typicalTurnPeriod = 80F;
    public static final float typicalTurnSpeed = 0.1F;
    public static final float collisionMinimalDistance = 0.01F;
    public static final int collisionKillTickTime = 60 * 20;
    public static final float waterLevelBias = 0.75F;
    public static final float gravity = 0.04F;

    private static final HashSet<Material> inWaterExtraMaterialSet = new HashSet<>();

    static {
        inWaterExtraMaterialSet.add(Material.WATER);
        inWaterExtraMaterialSet.add(Material.KELP);
        inWaterExtraMaterialSet.add(Material.KELP_PLANT);
        inWaterExtraMaterialSet.add(Material.SEAGRASS);
        inWaterExtraMaterialSet.add(Material.TALL_SEAGRASS);
    }

    @JsonIgnore
    public float getFishLeveledDiffedHealth() {
        return this.fish.health * this.diffMultiplier * (float) Math.pow(2, this.fish.level.level);
    }

    @JsonIgnore
    public float getFishLeveledDiffedStrength() {
        return this.fish.strength * this.diffMultiplier * (float) Math.pow(2, this.fish.level.level);
    }

    @JsonIgnore
    public float getFishDiff() {
        return this.diffMultiplier;
    }

    @JsonIgnore
    public float getFishDiffedScale() {
        return this.fish.scale * this.diffMultiplier;
    }

    @JsonIgnore
    public float getFishDiffedWeight() {
        return this.fish.weight * this.diffMultiplier;
    }

    @JsonIgnore
    public float getFishDiffedTurnSpeed() {
        return this.fish.speed * this.diffMultiplier;
    }

    @JsonIgnore
    public float getFishTurnPeriod() {
        return this.fish.period;
    }

    @JsonIgnore
    public PFish getFish() {
        return fish;
    }

    @JsonIgnore
    public void setFish(PFish fish) {
        this.fish = fish;
    }

    @JsonIgnore
    public PVector getLocation() {
        return location;
    }

    @JsonIgnore
    public PQuaternion getRotation() {
        return rotation;
    }

    @JsonIgnore
    public void setVelocity(PVector velocity) {
        this.velocity = velocity;
    }

    @JsonIgnore
    public PVector getVelocity() {
        return velocity;
    }

    @JsonIgnore
    public PVector getMouthLocation() {
        return mouthLocation;
    }

    @JsonIgnore
    public PFishSession getSession() {
        return session;
    }

    @JsonIgnore
    public void setSession(PFishSession session) {
        this.session = session;
    }

    @JsonIgnore
    public void setFrozeCountDown(int frozeCountDown) {
        this.frozeCountDown = frozeCountDown;
    }

    @JsonIgnore
    public boolean getFlagAlive() {
        return flagAlive;
    }

    @JsonIgnore
    public void setFlagSpeedLimit(boolean flagSpeedLimit) {
        this.flagSpeedLimit = flagSpeedLimit;
    }

    @JsonIgnore
    public void setFlagGlow(boolean flagGlow) {
        this.flagGlow = flagGlow;
    }

    @JsonIgnore
    public void setFlagActive(boolean flagActive) {
        this.flagActive = flagActive;
    }

    public void spawn(PFishMap pFM, PVector location) {
        this.location = location.clone();
        this.anchor = location.clone();
        this.rotation = (PQuaternion) new PQuaternion().rotateLocalY(PMath.ranFloat(- PMath.pi, PMath.pi)).rotateX(PMath.ranFloat(- PMath.pi / 12, PMath.pi / 12));
        float scale = getFishDiffedScale();
        PVector headVector = (PVector) new PVector(0, 0, (this.fish.bodyDecoLength / 2) * scale).rotate(this.rotation);
        PVector mouthVector = (PVector) headVector.clone().add(new PVector(0, 0, (this.fish.headMouthBias) * scale).rotate(this.rotation));
        headVector.add(new PVector(0, 0, this.fish.headDecoBias * scale).rotate(this.rotation));
        PVector bodyVector = (PVector) new PVector(0, 0, (this.fish.bodyDecoBias + this.fish.bodyDecoLength / 2) * scale).rotate(this.rotation);
        PVector tailVector = (PVector) new PVector(0, 0, (-this.fish.bodyDecoLength / 2) * scale).rotate(this.rotation);
        tailVector.add(new PVector(0, 0, this.fish.tailDecoBias * scale).rotate(this.rotation));
        this.mouthLocation = (PVector) this.location.clone().add(mouthVector);
        this.headEntity = ItemDisplayHandler.spawn(pFM.world, this.location, headVector, this.rotation, scale, this.fish.headDeco);
        this.bodyEntity = ItemDisplayHandler.spawn(pFM.world, this.location, bodyVector, this.rotation, scale, this.fish.bodyDeco);
        this.tailEntity = ItemDisplayHandler.spawn(pFM.world, this.location, tailVector, this.rotation, scale, this.fish.tailDeco);
        this.flagAlive = true;
    }

    public void kill() {
        ItemDisplayHandler.remove(this.headEntity);
        ItemDisplayHandler.remove(this.bodyEntity);
        ItemDisplayHandler.remove(this.tailEntity);
        this.flagAlive = false;
    }

    public void restore() {
        this.session = null;

        this.turnTarget = null;
        this.turnTolerance = typicalTurnTolerance;
        this.turnPeriod = typicalTurnPeriod;
        this.turnSpeed = typicalTurnSpeed;

        this.turnCountDown = 0;
        this.attractCountDown = -1;
        this.frozeCountDown = -1;
        this.buffCountDown = -1;
        this.buffTurnPeriodMultiplier = 1;
        this.buffTurnSpeedMultiplier = 1;

        this.bendOffset = 0;

        this.flagGlow = false;
        this.flagSpeedLimit = true;
        this.flagActive = true;
    }

    public void attract(PVector target, PFishBait pFB) {
        this.turnTarget = target;
        this.turnTolerance = 0F;
        this.turnPeriod = pFB.period;

        if (target.distance(this.location) < 2) {
            this.turnSpeed = (float) 0.05;
        } else {
            this.turnSpeed = pFB.speed;
        }

        this.attractCountDown = pFB.duration;
        this.frozeCountDown = -1;

        this.bendOffset = 0;

        this.flagGlow = false;
        this.flagSpeedLimit = true;
        this.flagActive = true;
    }

    public void bite(PFishSession session, PVector target, ItemStack baitItemStack) {

        float[] baitModifier = PFishBait.getBaitModifierList(baitItemStack);

        this.session = session;

        this.turnTarget = target;
        this.turnTolerance = PMath.pi / 4;
        this.turnPeriod = getFishTurnPeriod();
        this.turnSpeed = getFishDiffedTurnSpeed() * (1 - baitModifier[1]);

        this.attractCountDown = Integer.MAX_VALUE;
        this.frozeCountDown = -1;

        this.bendOffset = PMath.pi / 4;

        this.flagGlow = true;
        this.flagSpeedLimit = true;
        this.flagActive = true;
    }

    public void buff(int buffCountDown, float buffTurnPeriodMultiplier, float buffTurnSpeedMultiplier) {
        this.buffCountDown = buffCountDown;
        this.buffTurnPeriodMultiplier = buffTurnPeriodMultiplier;
        this.buffTurnSpeedMultiplier = buffTurnSpeedMultiplier;
        this.turnCountDown = 0;
    }

    public void turn(PVector target, float tolerance, int turnCountDown) {
        // STATE - COMPLETE
        this.tick = 0;
        this.turnCountDown = turnCountDown;
        PVector targetVector = (PVector) this.location.clone().sub(target.clone());
        if (targetVector.length() == 0) return;
        PQuaternion targetRotation = (PQuaternion) new PVector(0, 0, 1).rotationTo(targetVector.normalize().mul(-1), new PQuaternion());
        float targetYaw = targetRotation.getEulerAnglesYXZ(new PVector()).y;
        float targetPitch = targetRotation.getEulerAnglesYXZ(new PVector()).x;
        float yaw = targetYaw - this.rotation.getEulerAnglesYXZ(new PVector()).y;
        float pitch = targetPitch - this.rotation.getEulerAnglesYXZ(new PVector()).x;
        if (yaw > PMath.pi) yaw = yaw - 2 * PMath.pi;
        if (yaw < - PMath.pi) yaw = yaw + 2 * PMath.pi;
        yaw = yaw + PMath.ranFloat(-tolerance, tolerance);
        pitch = pitch * PMath.ran();
        this.turnYaw = yaw;
        this.turnPitch = pitch;
        this.turnAcceleration = this.turnSpeed * (1 + PMath.ranFloat(-tolerance, tolerance));
        this.turnBend = this.turnSpeed / typicalTurnSpeed * PMath.pi / 2;
    }

    public void update(PFishMap pFM) {
        // if entities are missing, kill this fish
        if (this.headEntity == null || this.headEntity.isDead()) kill();
        if (this.bodyEntity == null || this.bodyEntity.isDead()) kill();
        if (this.tailEntity == null || this.tailEntity.isDead()) kill();

        // update tick and count down
        this.tick++;

        // if active
        if (this.flagActive) {
            this.turnCountDown--;
            if (this.attractCountDown == 0) restore();
            if (this.attractCountDown >= 0) this.attractCountDown--;
            if (this.buffCountDown == 0) {
                this.buffTurnPeriodMultiplier = 1;
                this.buffTurnSpeedMultiplier = 1;
                this.turnCountDown = 0;
            }
            if (this.buffCountDown >= 0) this.buffCountDown--;
            // if frozen
            if (this.frozeCountDown == 0) {
                this.turnCountDown = 0;
            }
            if (this.frozeCountDown >= 0) {
                this.frozeCountDown--;
                return;
            }
            if (this.collisionCountDown == 0) kill();
            if (this.flagCollision) this.collisionCountDown--;
            if (!this.flagCollision) this.collisionCountDown = collisionKillTickTime;
        }

        if (this.tick % step != 0) return;
        if (!this.flagAlive) return;

        // check whether the fish is in water
        BlockData bD = this.location.getLocation(pFM.world).add(0, waterLevelBias, 0).getBlock().getBlockData();
        Material m = bD.getMaterial();
        boolean flagInWater = inWaterExtraMaterialSet.contains(m) || (bD instanceof Waterlogged && ((Waterlogged) bD).isWaterlogged());

        // if the fish is beached, make it bounce
        if (!flagInWater && this.flagCollision && this.velocity.y < 0) {
            this.turnCountDown = 0;
            this.velocity.y = 0.2F;
        }

        // if it's time to turn, turn.
        if (this.turnCountDown <= 0) turn();

        // formula : exp(-ax) .* a .* target
        float ratio = (float) (PMath.exp(-zeta * this.tick) + PMath.exp(-zeta * (this.tick + 1))) * zeta * step / 2;
        float angleYaw = ratio * this.turnYaw;
        float anglePitch = ratio * this.turnPitch;
        float angleBend = ratio / zeta / step * PMath.sin(this.tick * omega) * PMath.sign(angleYaw) * this.turnBend + PMath.sin(this.tick * omega) * PMath.sign(angleYaw) * this.bendOffset;
        float acceleration = ratio * this.turnAcceleration * this.buffTurnSpeedMultiplier;

        // limit fish pitch
        float pitch = this.rotation.getEulerAnglesYXZ(new PVector()).x;
        if (PMath.abs(pitch + anglePitch) > PMath.pi / 4) anglePitch = PMath.pi / 4 * PMath.sign(pitch + anglePitch) - pitch;

        // update rotation
        this.rotation.rotateLocalY(angleYaw).rotateX(anglePitch);

        // gravity and drag
        PVector velocity = this.velocity.clone();
        if (flagInWater) {
            velocity.mul(0.95F);
        } else {
            velocity.mul(0.98F);
            velocity.add(0, -gravity, 0);
        }

        // active
        if (this.flagActive) {
            if (flagInWater) velocity.rotate(new PQuaternion().rotateLocalY(angleYaw));
            velocity.add(new PVector(0, 0, acceleration).rotate(this.rotation));
            if (this.session != null && this.session.getStatus() == PFishSessionStatus.BITE) velocity.add(this.session.getPullAcceleration(this.location));
        }

        float speed = velocity.length();

        // speed limit
        if (this.flagSpeedLimit) {
            if (!flagInWater && speed > 1.0F) {
                velocity.normalize();
                speed = 1F;
            }
        }

        // collision detection
        if (speed != 0) {
            RayTraceResult rTR = pFM.world.rayTraceBlocks(this.location.getLocation(pFM.world), velocity.getVector(), speed + collisionMinimalDistance, FluidCollisionMode.NEVER, true);
            if (rTR != null && rTR.getHitBlock() != null && rTR.getHitBlockFace() != null) {
                PVector normalVector = (PVector) PVector.fromVector(rTR.getHitBlockFace().getDirection()).mul(collisionMinimalDistance);
                PVector translation = (PVector) PVector.fromVector(rTR.getHitPosition()).add(normalVector);
                velocity = (PVector) translation.clone().sub(this.location);
                this.location = translation;
                this.flagCollision = true;
            } else {
                this.location.add(velocity);
                this.flagCollision = false;
            }
        } else {
            this.flagCollision = false;
        }
        this.velocity = velocity;

        // update item display
        float angle = (angleBend + angleYaw) / 2;

        PQuaternion headQuaternion = (PQuaternion) new PQuaternion(0, 0, 0, 1).rotateLocalY(angle - angle / 3);
        PQuaternion bodyQuaternion = (PQuaternion) new PQuaternion(0, 0, 0, 1).rotateLocalY(0 - angle / 3);
        PQuaternion tailQuaternion = (PQuaternion) new PQuaternion(0, 0, 0, 1).rotateLocalY(- angle - angle / 3);
        headQuaternion = (PQuaternion) this.rotation.clone().mul(headQuaternion);
        bodyQuaternion = (PQuaternion) this.rotation.clone().mul(bodyQuaternion);
        tailQuaternion = (PQuaternion) this.rotation.clone().mul(tailQuaternion);
        float scale = getFishDiffedScale();
        PVector headVector = (PVector) new PVector(0, 0, (this.fish.bodyDecoLength / 2) * scale).rotate(bodyQuaternion);
        PVector mouthVector = (PVector) headVector.clone().add(new PVector(0, 0, (this.fish.headMouthBias) * scale).rotate(this.rotation));
        headVector.add(new PVector(0, 0, this.fish.headDecoBias * scale).rotate(headQuaternion));
        PVector bodyVector = (PVector) new PVector(0, 0, (this.fish.bodyDecoBias + this.fish.bodyDecoLength / 2) * scale).rotate(bodyQuaternion);
        PVector tailVector = (PVector) new PVector(0, 0, (-this.fish.bodyDecoLength / 2) * scale).rotate(bodyQuaternion);
        tailVector.add(new PVector(0, 0, this.fish.tailDecoBias * scale).rotate(tailQuaternion));
        ItemDisplayHandler.update(headEntity, this.location, headVector, headQuaternion, scale, (int) step, this.flagGlow);
        ItemDisplayHandler.update(bodyEntity, this.location, bodyVector, bodyQuaternion, scale, (int) step, this.flagGlow);
        ItemDisplayHandler.update(tailEntity, this.location, tailVector, tailQuaternion, scale, (int) step, this.flagGlow);
        this.mouthLocation = (PVector) this.location.clone().add(mouthVector);
    }

    private void turn() {
        // STATE - COMPLETE
        if (this.turnTarget == null) {
            this.turn(this.anchor, PMath.pi / 4);
        } else {
            this.turn(this.turnTarget, this.turnTolerance);
        }
    }

    private void turn(PVector target, float tolerance) {
        // STATE - COMPLETE
        this.turn(target, tolerance, PMath.ranInt((int) PMath.max(period, this.turnPeriod * this.buffTurnPeriodMultiplier), (int) PMath.max(period, (2 * this.turnPeriod * this.buffTurnPeriodMultiplier))));
    }
}
