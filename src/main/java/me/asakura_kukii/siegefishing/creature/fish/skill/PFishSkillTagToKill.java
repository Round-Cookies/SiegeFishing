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

@JsonTypeName("SiegeFishing.skill.tag_to_kill")
public class PFishSkillTagToKill extends PAbstractFishSkill{

    public PFishSkillTagToKill() {}

    public List<String> readyHint = new ArrayList<>();

    public List<String> earlyHint = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> notifySound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> notifyParticle = new ArrayList<>();

    public String iconString = "$skill.tag_to_kill$";

    public List<String> tagIconStringList = new ArrayList<>();

    public int tagCoolDown = 10;

    public int tagCountToKill = 6;

    public float missPenalty = 0.5F;

    public List<String> tagHint = new ArrayList<>();

    public List<String> missHint = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> tagSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> missSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> killSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> tagParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> fishTagParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> missParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> fishMissParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> killParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> fishKillParticle = new ArrayList<>();

    @Override
    public Pair<Boolean, Integer> cast(PFishSession session, PFishPlayer fishPlayer, PFishRod rod, PFishEntity fishEntity, int holdTickTime, int successCount) {
        if (session.getFlagHit()) {
            if (successCount >= tagCountToKill - 1) {
                // kill
                for (PSound pS : killSound) pS.play(fishPlayer.getPlayer(), fishPlayer.getLocation());
                for (PParticle pP : killParticle) pP.spawn(fishPlayer.getPlayer(), fishPlayer.getLocation());
                for (PParticle pP : fishKillParticle) pP.spawn(fishPlayer.getPlayer(), fishEntity.getLocation());
                session.setProgress(session.getMaximumProgress());
                return Pair.of(false, this.coolDown);
            } else {
                // tag
                for (PSound pS : tagSound) pS.play(fishPlayer.getPlayer(), fishPlayer.getLocation());
                for (PParticle pP : tagParticle) pP.spawn(fishPlayer.getPlayer(), fishPlayer.getLocation());
                for (PParticle pP : fishTagParticle) pP.spawn(fishPlayer.getPlayer(), fishEntity.getLocation());
                fishPlayer.sendHint(tagHint);
                return Pair.of(true, this.tagCoolDown);
            }
        } else {
            // damage & reset cool down
            for (PSound pS : missSound) pS.play(fishPlayer.getPlayer(), fishPlayer.getLocation());
            for (PParticle pP : missParticle) pP.spawn(fishPlayer.getPlayer(), fishPlayer.getLocation());
            for (PParticle pP : fishMissParticle) pP.spawn(fishPlayer.getPlayer(), fishEntity.getLocation());
            fishPlayer.sendHint(missHint);
            float progress = session.getProgress() - session.getMaximumProgress() * missPenalty;
            if (progress < 0) progress = 0;
            if (progress == 0) fishPlayer.collectSkillFailure();
            session.setProgress(progress);
            return Pair.of(false, this.coolDown);
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
        return this.iconString;
    }

    @Override
    public String getIcon(PFishSession session, PFishPlayer fishPlayer, PFishRod rod, PFishEntity fishEntity, int holdTickTime, int successCount) {
        if (successCount < this.tagIconStringList.size()) {
            return this.tagIconStringList.get(successCount);
        }
        return this.iconString;
    }
}
