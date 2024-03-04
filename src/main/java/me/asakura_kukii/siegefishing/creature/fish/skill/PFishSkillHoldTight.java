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

@JsonTypeName("SiegeFishing.skill.hold_tight")
public class PFishSkillHoldTight extends PAbstractFishSkill{

    public PFishSkillHoldTight() {}

    public List<String> readyHint = new ArrayList<>();

    public List<String> earlyHint = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> notifySound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> notifyParticle = new ArrayList<>();

    public String iconString = "$skill.hold_tight$";

    public int coolDown = 10;

    public int holdTightTime = 100;

    public List<String> castHint = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> castSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> castParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> holdTightParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> fishHoldTightParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> holdTightSound = new ArrayList<>();

    public void tickHoldTight(PFishSession session, PFishPlayer fishPlayer, PFishRod rod, PFishEntity fishEntity, int specialCountDownForHoldTight) {
        if (specialCountDownForHoldTight >= 0) {
            for (PParticle pP : holdTightParticle) pP.spawn(fishPlayer.getPlayer(), fishPlayer.getLocation());
            for (PParticle pP : fishHoldTightParticle) pP.spawn(fishPlayer.getPlayer(), fishEntity.getLocation());
            for (PSound pS : holdTightSound) pS.play(fishPlayer.getPlayer(), fishEntity.getLocation(), specialCountDownForHoldTight / (float) holdTightTime);
        }
    }

    @Override
    public Pair<Boolean, Integer> cast(PFishSession session, PFishPlayer fishPlayer, PFishRod rod, PFishEntity fishEntity, int holdTickTime, int successCount) {
        for (PSound pS : castSound) pS.play(fishPlayer.getPlayer(), fishPlayer.getLocation());
        for (PParticle pP : castParticle) pP.spawn(fishPlayer.getPlayer(), fishPlayer.getLocation());
        fishPlayer.sendHint(castHint);
        session.setSpecialProgressForHoldTight(holdTightTime);
        return Pair.of(true, coolDown);
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
        return this.iconString;
    }

    @Override
    public String getIcon(PFishSession session, PFishPlayer fishPlayer, PFishRod rod, PFishEntity fishEntity, int holdTickTime, int successCount) {
        return this.iconString;
    }
}
