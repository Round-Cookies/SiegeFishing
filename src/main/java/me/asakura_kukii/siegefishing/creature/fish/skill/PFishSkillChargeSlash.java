package me.asakura_kukii.siegefishing.creature.fish.skill;

import me.asakura_kukii.lib.jackson.annotation.JsonTypeName;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.io.helper.PFileIdDeserializer;
import me.asakura_kukii.siegecore.io.helper.PFileIdSerializer;
import me.asakura_kukii.siegefishing.effect.PParticle;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.creature.fish.PFishEntity;
import me.asakura_kukii.siegefishing.rod.PFishRod;
import me.asakura_kukii.siegefishing.creature.fish.PFishSession;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@JsonTypeName("SiegeFishing.skill.charge_slash")
public class PFishSkillChargeSlash extends PAbstractFishSkill{

    public PFishSkillChargeSlash() {}

    public List<String> readyHint = new ArrayList<>();

    public List<String> earlyHint = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> notifyParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> notifySound = new ArrayList<>();

    public String iconString = "$skill.charge_slash$";

    public String chargingIconString = "$skill.charge_slash.charging$";

    public int holdTickTime = 40;

    public float damagePercent = (float) 0.3;

    public float riseSpeed = 0.5F;

    public List<String> chargeHint = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> chargeParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> chargeSound = new ArrayList<>();

    public List<String> hitHint = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> hitParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> hitSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> fishHitParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> missParticle = new ArrayList<>();

    public List<String> missHint = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> missSound = new ArrayList<>();

    @Override
    public Pair<Boolean, Integer> cast(PFishSession session, PFishPlayer fishPlayer, PFishRod rod, PFishEntity fishEntity, int holdTickTime, int successCount) {
        if (holdTickTime > this.holdTickTime) {
            return Pair.of(false, 0);
        }
        if (holdTickTime < this.holdTickTime) {
            fishPlayer.sendHint(chargeHint);
            for (PSound pS : chargeSound) pS.play(fishPlayer.getPlayer(), fishPlayer.getLocation(), (float) holdTickTime / this.holdTickTime);
            for (PParticle pP : chargeParticle) pP.spawn(fishPlayer.getPlayer(), fishPlayer.getLocation());
            return Pair.of(false, 0);
        }
        if (session.getFlagHit()) {
            fishPlayer.sendHint(hitHint);
            for (PSound pS : hitSound) pS.play(fishPlayer.getPlayer(), fishPlayer.getLocation());
            for (PParticle pP : hitParticle) pP.spawn(fishPlayer.getPlayer(), fishPlayer.getLocation());
            for (PParticle pP : fishHitParticle) pP.spawn(fishPlayer.getPlayer(), fishEntity.getLocation());
            fishEntity.getVelocity().add(0, this.riseSpeed, 0);
            float damage = session.getMaximumProgress() * damagePercent; //TODO: LEVEL FACTOR
            float progress = session.getProgress() + damage;
            if (progress > session.getMaximumProgress()) progress = session.getMaximumProgress();
            session.setProgress(progress);
        } else {
            fishPlayer.sendHint(missHint);
            for (PSound pS : missSound) pS.play(fishPlayer.getPlayer(), fishPlayer.getLocation());
            for (PParticle pP : missParticle) pP.spawn(fishPlayer.getPlayer(), fishPlayer.getLocation());
            float damage = session.getMaximumProgress() * damagePercent; //TODO: LEVEL FACTOR
            float progress = session.getProgress() - damage;
            if (progress < 0) progress = 0;
            if (progress == 0) fishPlayer.collectSkillFailure();
            session.setProgress(progress);
        }
        return Pair.of(true, this.coolDown);
    }

    @Override
    public void notify(PFishSession session, PFishPlayer fishPlayer, PFishRod rod, PFishEntity fishEntity) {
        for (PSound pS : notifySound) pS.play(fishPlayer.getPlayer(), fishPlayer.getLocation());
        for (PParticle pP : notifyParticle) pP.spawn(fishPlayer.getPlayer(), fishPlayer.getLocation());
    }

    @Override
    public void ready(PFishSession session, PFishPlayer fishPlayer, PFishRod rod, PFishEntity fishEntity) {
        fishPlayer.sendHint(readyHint);
    }

    @Override
    public void early(PFishSession session, PFishPlayer fishPlayer, PFishRod rod, PFishEntity fishEntity) {
        fishPlayer.sendHint(earlyHint);
    }

    @Override
    public String getCastIcon(PFishSession session, PFishPlayer fishPlayer, PFishRod rod, PFishEntity fishEntity, int holdTickTime, int successCount) {
        return this.chargingIconString;
    }

    @Override
    public String getIcon(PFishSession session, PFishPlayer fishPlayer, PFishRod rod, PFishEntity fishEntity, int holdTickTime, int successCount) {
        return this.iconString;
    }
}
