package me.asakura_kukii.siegefishing.creature.fish.skill;

import me.asakura_kukii.lib.jackson.annotation.JsonTypeName;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.io.helper.PFileIdDeserializer;
import me.asakura_kukii.siegecore.io.helper.PFileIdSerializer;
import me.asakura_kukii.siegecore.util.math.PMath;
import me.asakura_kukii.siegefishing.creature.fish.PFishEntity;
import me.asakura_kukii.siegefishing.creature.fish.PFishSession;
import me.asakura_kukii.siegefishing.effect.PParticle;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import me.asakura_kukii.siegefishing.rod.PFishRod;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@JsonTypeName("SiegeFishing.skill.lucky")
public class PFishSkillLucky extends PAbstractFishSkill{

    public PFishSkillLucky() {}

    public List<String> readyHint = new ArrayList<>();

    public List<String> earlyHint = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> notifySound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> notifyParticle = new ArrayList<>();

    public String iconString = "$skill.lucky$";

    public String activeIconString = "$skill.lucky.active$";

    public int coolDown = 10;

    public float activeProgressRatio = 0.75F;

    public float chance = 0.75F;

    public List<String> inactiveHint = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> successSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> successParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> failuresSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> failureParticle = new ArrayList<>();

    @Override
    public Pair<Boolean, Integer> cast(PFishSession session, PFishPlayer fishPlayer, PFishRod rod, PFishEntity fishEntity, int holdTickTime, int successCount) {
        if (session.getProgress() >= session.getMaximumProgress() * activeProgressRatio) {
            if (PMath.ranBoolean(chance)) {
                for (PSound pS : successSound) pS.play(fishPlayer.getPlayer(), fishPlayer.getLocation());
                for (PParticle pP : successParticle) pP.spawn(fishPlayer.getPlayer(), fishPlayer.getLocation());
                session.setProgress(session.getMaximumProgress());
            } else {
                for (PSound pS : failuresSound) pS.play(fishPlayer.getPlayer(), fishPlayer.getLocation());
                for (PParticle pP : failureParticle) pP.spawn(fishPlayer.getPlayer(), fishPlayer.getLocation());
                fishPlayer.collectSkillFailure();
                session.setProgress(0);
            }
            return Pair.of(true, coolDown);
        } else {
            fishPlayer.sendHint(inactiveHint);
            return Pair.of(false, coolDown);
        }
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
        if (session.getProgress() >= session.getMaximumProgress() * activeProgressRatio) {
            return this.activeIconString;
        } else {
            return "&8" + this.iconString;
        }
    }

    @Override
    public String getIcon(PFishSession session, PFishPlayer fishPlayer, PFishRod rod, PFishEntity fishEntity, int holdTickTime, int successCount) {
        if (session.getProgress() >= session.getMaximumProgress() * activeProgressRatio) {
            return this.activeIconString;
        } else {
            return "&8" + this.iconString;
        }
    }
}
