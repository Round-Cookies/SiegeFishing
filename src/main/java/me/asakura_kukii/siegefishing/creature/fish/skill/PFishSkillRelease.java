package me.asakura_kukii.siegefishing.creature.fish.skill;

import me.asakura_kukii.lib.jackson.annotation.JsonTypeName;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.io.helper.PFileIdDeserializer;
import me.asakura_kukii.siegecore.io.helper.PFileIdSerializer;
import me.asakura_kukii.siegefishing.creature.fish.PFishEntity;
import me.asakura_kukii.siegefishing.creature.fish.PFishSession;
import me.asakura_kukii.siegefishing.effect.PParticle;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import me.asakura_kukii.siegefishing.rod.PFishRod;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@JsonTypeName("SiegeFishing.skill.release")
public class PFishSkillRelease extends PAbstractFishSkill{

    public PFishSkillRelease() {}

    public List<String> readyHint = new ArrayList<>();

    public List<String> earlyHint = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> notifySound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> notifyParticle = new ArrayList<>();

    public String iconString = "$skill.release$";

    public String chargingIconString = "$skill.release.charging$";

    public int holdTickTime = 10;

    public float damagePercent = (float) 0.1;

    public List<String> chargeHint = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> chargeSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> chargeParticle = new ArrayList<>();

    public List<String> castHint = new ArrayList<>();

    public List<String> inactiveHint = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> castParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> castSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> fishCastParticle = new ArrayList<>();

    @Override
    public Pair<Boolean, Integer> cast(PFishSession session, PFishPlayer fishPlayer, PFishRod rod, PFishEntity fishEntity, int holdTickTime, int successCount) {
        if (session.getProgress() >= rod.getLeveledHealth(fishPlayer.getPlayer())) {
            fishPlayer.sendHint(inactiveHint);
            return Pair.of(false, 0);
        }
        if (holdTickTime > this.holdTickTime) {
            return Pair.of(false, 0);
        }
        if (holdTickTime < this.holdTickTime) {
            fishPlayer.sendHint(chargeHint);
            for (PSound pS : chargeSound) pS.play(fishPlayer.getPlayer(), fishPlayer.getLocation(), (float) holdTickTime / this.holdTickTime);
            for (PParticle pP : chargeParticle) pP.spawn(fishPlayer.getPlayer(), fishPlayer.getLocation());
            return Pair.of(false, 0);
        }
        fishPlayer.sendHint(castHint);
        for (PSound pS : castSound) pS.play(fishPlayer.getPlayer(), fishPlayer.getLocation());
        for (PParticle pP : castParticle) pP.spawn(fishPlayer.getPlayer(), fishPlayer.getLocation());
        for (PParticle pP : fishCastParticle) pP.spawn(fishPlayer.getPlayer(), fishEntity.getLocation());
        float damage = session.getMaximumProgress() * damagePercent; //TODO: LEVEL FACTOR
        float progress = session.getProgress() + damage;
        if (progress > session.getMaximumProgress()) progress = session.getMaximumProgress();
        session.setProgress(progress);
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
        if (session.getProgress() >= rod.getLeveledHealth(fishPlayer.getPlayer())) {
            return "&8" + this.iconString;
        } else {
            return this.chargingIconString;
        }
    }

    @Override
    public String getIcon(PFishSession session, PFishPlayer fishPlayer, PFishRod rod, PFishEntity fishEntity, int holdTickTime, int successCount) {
        if (session.getProgress() >= rod.getLeveledHealth(fishPlayer.getPlayer())) {
            return "&8" + this.iconString;
        } else {
            return this.iconString;
        }
    }
}
