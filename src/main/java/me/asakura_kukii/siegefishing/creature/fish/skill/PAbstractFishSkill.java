package me.asakura_kukii.siegefishing.creature.fish.skill;

import me.asakura_kukii.lib.jackson.annotation.JsonSubTypes;
import me.asakura_kukii.lib.jackson.annotation.JsonTypeInfo;
import me.asakura_kukii.siegefishing.creature.fish.PFishEntity;
import me.asakura_kukii.siegefishing.rod.PFishRod;
import me.asakura_kukii.siegefishing.creature.fish.PFishSession;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import org.apache.commons.lang3.tuple.Pair;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PFishSkillRelease.class, name = "SiegeFishing.skill.release"),
        @JsonSubTypes.Type(value = PFishSkillTagToKill.class, name = "SiegeFishing.skill.tag_to_kill"),
        @JsonSubTypes.Type(value = PFishSkillConcreteSink.class, name = "SiegeFishing.skill.concrete_sink"),
        @JsonSubTypes.Type(value = PFishSkillLucky.class, name = "SiegeFishing.skill.lucky"),
        @JsonSubTypes.Type(value = PFishSkillHoldTight.class, name = "SiegeFishing.skill.hold_tight"),
        @JsonSubTypes.Type(value = PFishSkillChargeSlash.class, name = "SiegeFishing.skill.charge_slash"),
        @JsonSubTypes.Type(value = PFishSkillArtAppreciation.class, name = "SiegeFishing.skill.art_appreciation"),
        @JsonSubTypes.Type(value = PFishSkillCat.class, name = "SiegeFishing.skill.cat"),
        @JsonSubTypes.Type(value = PFishSkillGun.class, name = "SiegeFishing.skill.gun")
})
public abstract class PAbstractFishSkill {

    public int coolDown = 10;

    public String iconString = "$skill.null$";

    public abstract Pair<Boolean, Integer> cast(PFishSession session, PFishPlayer fishPlayer, PFishRod rod, PFishEntity fishEntity, int holdTickTime, int successCount);

    public abstract void notify(PFishSession session, PFishPlayer fishPlayer, PFishRod rod, PFishEntity fishEntity);

    public abstract void ready(PFishSession session, PFishPlayer fishPlayer, PFishRod rod, PFishEntity fishEntity);

    public abstract void early(PFishSession session, PFishPlayer fishPlayer, PFishRod rod, PFishEntity fishEntity);

    public abstract String getCastIcon(PFishSession session, PFishPlayer fishPlayer, PFishRod rod, PFishEntity fishEntity, int holdTickTime, int successCount);

    public abstract String getIcon(PFishSession session, PFishPlayer fishPlayer, PFishRod rod, PFishEntity fishEntity, int holdTickTime, int successCount);
}
