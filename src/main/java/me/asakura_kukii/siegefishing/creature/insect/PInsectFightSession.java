package me.asakura_kukii.siegefishing.creature.insect;

import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.util.math.PMath;
import me.asakura_kukii.siegecore.util.math.PQuaternion;
import me.asakura_kukii.siegecore.util.math.PVector;
import me.asakura_kukii.siegefishing.config.PConfig;
import me.asakura_kukii.siegefishing.effect.PParticle;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.npc.PNPCInsectFight;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import me.asakura_kukii.siegefishing.util.ItemDisplayHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class PInsectFightSession {

    public static HashMap<Interaction, PInsectFightSession> activeInsectFightSession = new HashMap<>();

    public int tick = 0;

    public boolean flagWait = true;

    public Interaction interaction = null;

    public static final float heightBias = 0.51F;

    public static final float stillRadius = 0.5F;

    public static final float fightRadius = 0.25F;

    public PQuaternion angleOfFight = new PQuaternion();

    public PInsect insectA = null;
    public float healthA = 0.0F;
    public float healthMaximumA = 0.0F;
    public float attackA = 0.0F;
    public float avoidanceA = 0.0F;
    public ItemStack iSA = null;
    public PFishPlayer pFPA = null;
    public ItemDisplay entityA = null;

    public PInsect insectB = null;
    public float healthB = 0.0F;
    public float healthMaximumB = 0.0F;
    public float attackB = 0.0F;
    public float avoidanceB = 0.0F;
    public ItemStack iSB = null;
    public PFishPlayer pFPB = null;
    public ItemDisplay entityB = null;

    public boolean flagNPCFight = false;

    public PNPCInsectFight insectFightNPC = null;

    public PInsectFightSession(Interaction interaction, boolean flagNPCFight, PNPCInsectFight insectFightNPC) {
        this.interaction = interaction;
        this.flagNPCFight = flagNPCFight;
        this.insectFightNPC = insectFightNPC;
        activeInsectFightSession.put(interaction, this);
    }

    public void removeInsect(PFishPlayer pFP) {
        if (interaction == null || interaction.isDead()) return;
        if (insectA == null && insectB == null) return;
        if (!flagWait) return;
        if (pFP == null) return;
        if (pFP == this.pFPA && this.insectA != null && this.iSA != null) {
            ejectA();
        }
        if (pFP == this.pFPB && this.insectB != null && this.iSB != null) {
            ejectB();
        }
        if (insectA == null && insectB == null) {
            kill();
        }
    }

    public void addNPCInsect(PInsect pI, float health, float attack, float avoidance) {
        if (this.insectB != null) return;
        this.insectB = pI;
        this.healthB = health;
        this.healthMaximumB = healthB;
        this.attackB = attack;
        this.avoidanceB = avoidance;
        this.iSB = pI.getItemStack(1);
        PVector location = (PVector) new PVector(0, heightBias + this.insectB.decoBias, 0).add(new PVector(0, 0, stillRadius).rotate(this.angleOfFight));
        PQuaternion rotation = (PQuaternion) this.angleOfFight.clone().rotateY(PMath.pi);
        this.entityB = ItemDisplayHandler.spawn(interaction.getWorld(), PVector.fromLocation(interaction.getLocation()), location, rotation, 1F, insectB.deco);
        this.tick = 0;
        this.flagWait = true;
        if (insectA != null && insectB != null) {
            this.flagWait = false;
        }
    }

    public boolean addInsect(PFishPlayer pFP, PInsect pI, ItemStack iS) {
        if (pFP == null || pFP.getPlayer() == null) return false;
        if (interaction == null || interaction.isDead()) return false;
        if (insectA != null && insectB != null) return false;
        if (insectA == null && insectB == null) {
            float yaw = pFP.getPlayer().getLocation().getYaw() / 180 * PMath.pi;
            this.angleOfFight = new PVector(0, 0, 1).rotationToExceptZ(new PVector(-PMath.sin(yaw), 0F, PMath.cos(yaw)));
        }
        if (insectA == null) {
            this.insectA = pI;
            this.iSA = iS;
            this.pFPA = pFP;

            PVector location = (PVector) new PVector(0, heightBias + this.insectA.decoBias, 0).add(new PVector(0, 0, -stillRadius).rotate(this.angleOfFight));
            PQuaternion rotation = this.angleOfFight.clone();
            this.entityA = ItemDisplayHandler.spawn(interaction.getWorld(), PVector.fromLocation(interaction.getLocation()), location, rotation, 1F, insectA.deco);

            float[] modifierList = PInsect.getInsectModifierList(iS);
            this.healthA = modifierList[0];
            this.healthMaximumA = this.healthA;
            this.attackA = modifierList[1];
            this.avoidanceA = modifierList[2];

            this.tick = 0;
            this.flagWait = true;
            if (insectA != null && insectB != null) {
                this.flagWait = false;
            }
            PType pT = PType.getPType(PConfig.class);
            PConfig pC = null;
            if (pT != null) pC = (PConfig) pT.getPFileSafely("config");
            if (pC != null) {
                HashMap<String, String> replaceMap = new HashMap<>();
                replaceMap.put("%name%", insectA.level.colorString + insectA.name);
                pFP.sendLog(pC.insectPlaceHint, replaceMap);
                for (PParticle pP : pC.insectPlaceParticle) pP.spawn(this.interaction.getWorld(), (PVector) PVector.fromLocation(this.interaction.getLocation()).add(location));
                for (PSound pS : pC.insectPlaceSound) pS.play(this.interaction.getWorld(), (PVector) PVector.fromLocation(this.interaction.getLocation()).add(location));
            }
            return true;
        }
        if (insectB == null && !this.flagNPCFight) {
            this.insectB = pI;
            this.iSB = iS;
            this.pFPB = pFP;

            PVector location = (PVector) new PVector(0, heightBias + this.insectB.decoBias, 0).add(new PVector(0, 0, stillRadius).rotate(this.angleOfFight));
            PQuaternion rotation = (PQuaternion) this.angleOfFight.clone().rotateY(PMath.pi);
            this.entityB = ItemDisplayHandler.spawn(interaction.getWorld(), PVector.fromLocation(interaction.getLocation()), location, rotation, 1F, insectB.deco);

            float[] modifierList = PInsect.getInsectModifierList(iS);
            this.healthB = modifierList[0];
            this.healthMaximumB = this.healthB;
            this.attackB = modifierList[1];
            this.avoidanceB = modifierList[2];

            this.tick = 0;
            this.flagWait = true;
            if (insectA != null && insectB != null) {
                this.flagWait = false;
            }
            PType pT = PType.getPType(PConfig.class);
            PConfig pC = null;
            if (pT != null) pC = (PConfig) pT.getPFileSafely("config");
            if (pC != null) {
                HashMap<String, String> replaceMap = new HashMap<>();
                replaceMap.put("%name%", insectB.level.colorString + insectB.name);
                pFP.sendLog(pC.insectPlaceHint, replaceMap);
            }
            return true;
        }
        return false;
    }

    public void eject() {
        if (interaction == null || interaction.isDead()) return;
        if (!flagWait) return;
        ejectA();
        ejectB();
        kill();
    }

    public void ejectA() {
        if (iSA == null || insectA == null) return;
        PVector location = (PVector) new PVector(0, heightBias + this.insectA.decoBias, 0).add(new PVector(0, 0, -stillRadius).rotate(this.angleOfFight));
        if (iSA != null) {
            Item i = this.interaction.getWorld().dropItem(this.interaction.getLocation().add(location.x, location.y, location.z), iSA);
            i.setVelocity(Vector.fromJOML(new PVector(0, 0.2F, -0.2F).rotate(this.angleOfFight)));
            PType pT = PType.getPType(PConfig.class);
            PConfig pC = null;
            if (pT != null) pC = (PConfig) pT.getPFileSafely("config");
            if (pC != null) {
                for (PParticle pP : pC.insectEjectParticle) pP.spawn(this.interaction.getWorld(), (PVector) PVector.fromLocation(this.interaction.getLocation()).add(location));
                for (PSound pS : pC.insectEjectSound) pS.play(this.interaction.getWorld(), (PVector) PVector.fromLocation(this.interaction.getLocation()).add(location));
            }
        }
        ItemDisplayHandler.remove(entityA);
        this.insectA = null;
        this.healthA = 0.0F;
        this.healthMaximumA = 0.0F;
        this.attackA = 0.0F;
        this.avoidanceA = 0.0F;
        this.iSA = null;
        this.pFPA = null;
        this.entityA = null;
    }

    public void ejectB() {
        if (iSB == null || insectB == null) return;
        PVector location = (PVector) new PVector(0, heightBias + this.insectB.decoBias, 0).add(new PVector(0, 0, stillRadius).rotate(this.angleOfFight));
        if (iSB != null && !flagNPCFight) {
            Item i = this.interaction.getWorld().dropItem(this.interaction.getLocation().add(location.x, location.y, location.z), iSB);
            i.setVelocity(Vector.fromJOML(new PVector(0, 0.2F, 0.2F).rotate(this.angleOfFight)));
            PType pT = PType.getPType(PConfig.class);
            PConfig pC = null;
            if (pT != null) pC = (PConfig) pT.getPFileSafely("config");
            if (pC != null) {
                for (PParticle pP : pC.insectEjectParticle) pP.spawn(this.interaction.getWorld(), (PVector) PVector.fromLocation(this.interaction.getLocation()).add(location));
                for (PSound pS : pC.insectEjectSound) pS.play(this.interaction.getWorld(), (PVector) PVector.fromLocation(this.interaction.getLocation()).add(location));
            }
        }
        ItemDisplayHandler.remove(entityB);
        this.insectB = null;
        this.healthB = 0.0F;
        this.healthMaximumB = 0.0F;
        this.attackB = 0.0F;
        this.avoidanceB = 0.0F;
        this.iSB = null;
        this.pFPB = null;
        this.entityB = null;
    }

    public void kill() {
        if (interaction == null || interaction.isDead()) return;
        activeInsectFightSession.remove(this.interaction);
    }

    public Pair<ItemStack, Integer> manipulateStar(PInsect insect, ItemStack iS, int change) {
        if (change == 0) return Pair.of(iS, -1);
        int starCount = PInsect.getInsectStarCount(iS);
        if (starCount + change > 10) change = 10 - starCount;
        if (starCount + change >= 0 && starCount + change <= 10) {
            starCount = starCount + change;
            float[] modifierList = PInsect.getInsectModifierList(iS);
            int index = PMath.ranIndex(3);
            if (index == 0) {
                modifierList[index] = modifierList[index] + change * PInsect.healthFactor * 2;
                iS = insect.setInsectModifierList(iS, modifierList, starCount);
            } else if (index == 1) {
                modifierList[index] = modifierList[index] + change * PInsect.attackFactor * 2;
                iS = insect.setInsectModifierList(iS, modifierList, starCount);
            } else if (index == 2) {
                modifierList[index] = modifierList[index] + change * PInsect.avoidanceFactor * 2;
                iS = insect.setInsectModifierList(iS, modifierList, starCount);
            }
            return Pair.of(iS, index);
        }
        return Pair.of(iS, -1);
    }

    public void update() {
        if (interaction == null || interaction.isDead()) {
            eject();
            return;
        }
        PType pT = PType.getPType(PConfig.class);
        PConfig pC = null;
        if (pT != null) pC = (PConfig) pT.getPFileSafely("config");
        assert pC != null;
        if (this.flagWait) {
            if (this.tick >= 200) {
                eject();
                if (pFPA != null) pFPA.sendLog(pC.insectWaitEjectHint);
                if (pFPB != null) pFPB.sendLog(pC.insectWaitEjectHint);
            }
            this.tick++;
            return;
        }
        if (pFPA == null || (pFPB == null && !flagNPCFight)) {
            eject();
            return;
        }
        if (insectA == null || insectB == null || iSA == null || iSB == null) {
            eject();
            return;
        }

        PVector green = new PVector(167, 255, 110);
        PVector red = new PVector(255, 98, 98);
        PVector diff = (PVector) green.clone().sub(red);
        Color colorA = Color.fromRGB(167, 255, 110);
        Color colorB = Color.fromRGB(167, 255, 110);
        if (this.healthMaximumA != 0) {
            PVector colorAVector = (PVector) red.clone().add(diff.clone().mul(this.healthA / this.healthMaximumA));
            colorA = Color.fromRGB(colorAVector.r(), colorAVector.g(), colorAVector.b());
        }
        if (this.healthMaximumB != 0) {
            PVector colorBVector = (PVector) red.clone().add(diff.clone().mul(this.healthB / this.healthMaximumB));
            colorB = Color.fromRGB(colorBVector.r(), colorBVector.g(), colorBVector.b());
        }

        PVector tableLocation = PVector.fromLocation(interaction.getLocation());
        World w = interaction.getWorld();
        HashMap<String, String> replaceAMap = new HashMap<>();
        replaceAMap.put("%name%", insectA.level.colorString + insectA.name);
        replaceAMap.put("%name_opponent%", insectB.level.colorString + insectB.name);

        HashMap<String, String> replaceBMap = new HashMap<>();
        replaceBMap.put("%name%", insectB.level.colorString + insectB.name);
        replaceBMap.put("%name_opponent%", insectA.level.colorString + insectA.name);

        if (this.tick == 0) {
            pFPA.sendLog(pC.insectFightStartHint, replaceAMap);
            if (pFPA != pFPB && !flagNPCFight) pFPB.sendLog(pC.insectFightStartHint, replaceBMap);
            for (PParticle pP : pC.insectFightStartParticle) pP.spawn(w, tableLocation);
            for (PSound pS : pC.insectFightStartSound) pS.play(w, tableLocation);
            this.tick++;
            return;
        }
        if (this.tick < 40) {
            this.tick++;
            return;
        }
        if (this.tick % 40 == 0) {
            PVector locationA = (PVector) new PVector(0, heightBias + this.insectA.decoBias, 0).add(new PVector(0, 0, -fightRadius).rotate(this.angleOfFight));
            PQuaternion rotationA = (PQuaternion) this.angleOfFight.clone().rotateY(PMath.ranFloat(- 1F / 6, 1F / 6) * PMath.pi);
            PVector locationB = (PVector) new PVector(0, heightBias + this.insectB.decoBias, 0).add(new PVector(0, 0, fightRadius).rotate(this.angleOfFight));
            PQuaternion rotationB = (PQuaternion) this.angleOfFight.clone().rotateY(PMath.pi + PMath.ranFloat(- 1F / 6, 1F / 6) * PMath.pi);
            ItemDisplayHandler.update(this.entityA, locationA, rotationA, new PVector(1, 1, 1), 1, true, colorA);
            ItemDisplayHandler.update(this.entityB, locationB, rotationB, new PVector(1, 1, 1), 1, true, colorB);
        }
        if (this.tick % 40 != 1) {
            this.tick++;
            return;
        }
        PVector locationA = (PVector) new PVector(0, heightBias + this.insectA.decoBias, 0).add(new PVector(0, 0, -stillRadius).rotate(this.angleOfFight));
        PQuaternion rotationA = this.angleOfFight.clone();
        PVector locationB = (PVector) new PVector(0, heightBias + this.insectB.decoBias, 0).add(new PVector(0, 0, stillRadius).rotate(this.angleOfFight));
        PQuaternion rotationB = (PQuaternion) this.angleOfFight.clone().rotateY(PMath.pi);
        boolean flagAvoidanceA = PMath.ranBoolean(PMath.max(0, PMath.min(1, this.avoidanceA)));
        boolean flagAvoidanceB = PMath.ranBoolean(PMath.max(0, PMath.min(1, this.avoidanceB)));
        boolean flagHugeAttackA = false;
        boolean flagHugeAttackB = false;
        PVector particleLocationA = (PVector) new PVector(0, heightBias + this.insectA.decoBias, 0).add(new PVector(0, 0, -fightRadius).rotate(this.angleOfFight)).add(tableLocation);
        PVector particleLocationB = (PVector) new PVector(0, heightBias + this.insectB.decoBias, 0).add(new PVector(0, 0, fightRadius).rotate(this.angleOfFight)).add(tableLocation);

        if (!flagAvoidanceA) {
            if (insectB.level == PInsectLevel.BLUE && insectA.level == PInsectLevel.RED || insectB.level == PInsectLevel.RED && insectA.level == PInsectLevel.GREEN || insectB.level == PInsectLevel.GREEN && insectA.level == PInsectLevel.BLUE) {
                // critical
                flagHugeAttackB = true;
                this.healthA = this.healthA - this.attackB * 1.3F;
                for (PParticle pP : pC.insectHugeDamageParticle) pP.spawn(w, particleLocationA);
                for (PSound pS : pC.insectHugeDamageSound) pS.play(w, particleLocationA);
            } else {
                // normal
                this.healthA = this.healthA - this.attackB;
                for (PParticle pP : pC.insectDamageParticle) pP.spawn(w, particleLocationA);
                for (PSound pS : pC.insectDamageSound) pS.play(w, particleLocationA);
            }
        } else {
            // block
            for (PParticle pP : pC.insectAvoidanceParticle) pP.spawn(w, particleLocationA);
            for (PSound pS : pC.insectAvoidanceSound) pS.play(w, particleLocationA);
        }
        if (!flagAvoidanceB) {
            if (insectA.level == PInsectLevel.BLUE && insectB.level == PInsectLevel.RED || insectA.level == PInsectLevel.RED && insectB.level == PInsectLevel.GREEN || insectA.level == PInsectLevel.GREEN && insectB.level == PInsectLevel.BLUE) {
                // critical
                flagHugeAttackA = true;
                this.healthB = this.healthB - this.attackA * 1.3F;
                for (PParticle pP : pC.insectHugeDamageParticle) pP.spawn(w, particleLocationB);
                for (PSound pS : pC.insectHugeDamageSound) pS.play(w, particleLocationB);
            } else {
                // normal
                this.healthB = this.healthB - this.attackA;
                for (PParticle pP : pC.insectDamageParticle) pP.spawn(w, particleLocationB);
                for (PSound pS : pC.insectDamageSound) pS.play(w, particleLocationB);
            }
        } else {
            // block
            for (PParticle pP : pC.insectAvoidanceParticle) pP.spawn(w, particleLocationB);
            for (PSound pS : pC.insectAvoidanceSound) pS.play(w, particleLocationB);
        }
        if (this.healthMaximumA != 0) {
            PVector colorAVector = (PVector) red.clone().add(diff.clone().mul(this.healthA / this.healthMaximumA));
            colorA = Color.fromRGB(colorAVector.r(), colorAVector.g(), colorAVector.b());
        }
        if (this.healthMaximumB != 0) {
            PVector colorBVector = (PVector) red.clone().add(diff.clone().mul(this.healthB / this.healthMaximumB));
            colorB = Color.fromRGB(colorBVector.r(), colorBVector.g(), colorBVector.b());
        }
        ItemDisplayHandler.update(this.entityA, locationA, rotationA, new PVector(1, 1, 1), 20, true, colorA);
        ItemDisplayHandler.update(this.entityB, locationB, rotationB, new PVector(1, 1, 1), 20, true, colorB);

        if (flagAvoidanceA && flagAvoidanceB) {
            // send to A
            pFPA.sendLog(pC.insectAvoidanceAvoidanceHint, replaceAMap);
            // send to B
            if (pFPA != pFPB && !flagNPCFight) pFPB.sendLog(pC.insectAvoidanceAvoidanceHint, replaceBMap);
        }
        if (flagAvoidanceA && !flagAvoidanceB) {
            if (flagHugeAttackA) {
                // send to A
                pFPA.sendLog(pC.insectAvoidanceHugeDamageHint, replaceAMap);
                // send to B
                if (pFPA != pFPB && !flagNPCFight) pFPB.sendLog(pC.insectHugeDamageAvoidanceHint, replaceBMap);
            } else {
                // send to A
                pFPA.sendLog(pC.insectAvoidanceDamageHint, replaceAMap);
                // send to B
                if (pFPA != pFPB && !flagNPCFight) pFPB.sendLog(pC.insectDamageAvoidanceHint, replaceBMap);
            }
        }
        if (!flagAvoidanceA && flagAvoidanceB) {
            if (flagHugeAttackB) {
                // send to A
                pFPA.sendLog(pC.insectHugeDamageAvoidanceHint, replaceAMap);
                // send to B
                if (pFPA != pFPB && !flagNPCFight) pFPB.sendLog(pC.insectAvoidanceHugeDamageHint, replaceBMap);
            } else {
                // send to A
                pFPA.sendLog(pC.insectDamageAvoidanceHint, replaceAMap);
                // send to B
                if (pFPA != pFPB && !flagNPCFight) pFPB.sendLog(pC.insectAvoidanceDamageHint, replaceBMap);
            }
        }
        if (!flagAvoidanceA && !flagAvoidanceB) {
            if (!flagHugeAttackA && flagHugeAttackB) {
                // send to A
                pFPA.sendLog(pC.insectHugeDamageDamageHint, replaceAMap);
                // send to B
                if (pFPA != pFPB && !flagNPCFight) pFPB.sendLog(pC.insectDamageHugeDamageHint, replaceBMap);
            }
            if (flagHugeAttackA && !flagHugeAttackB) {
                // send to A
                pFPA.sendLog(pC.insectDamageHugeDamageHint, replaceAMap);
                // send to B
                if (pFPA != pFPB && !flagNPCFight) pFPB.sendLog(pC.insectHugeDamageDamageHint, replaceBMap);
            }
            if (!flagHugeAttackA && !flagHugeAttackB) {
                // send to A
                pFPA.sendLog(pC.insectDamageDamageHint, replaceAMap);
                // send to B
                if (pFPA != pFPB && !flagNPCFight) pFPB.sendLog(pC.insectDamageDamageHint, replaceBMap);
            }
        }

        if (this.healthA < 0 && this.healthB < 0) {
            // draw
            this.pFPA.sendLog(pC.insectDrawHint, replaceAMap);
            if (this.pFPA != this.pFPB && !flagNPCFight) this.pFPB.sendLog(pC.insectDrawHint, replaceBMap);
            for (PParticle pP : pC.insectFightEndParticle) pP.spawn(w, tableLocation);
            for (PSound pS : pC.insectFightEndSound) pS.play(w, tableLocation);
            if (this.entityA != null) this.entityA.setGlowing(false);
            if (this.entityB != null) this.entityB.setGlowing(false);
            ejectA();
            ejectB();
            kill();
            return;
        }

        String previousStar = "";
        String currentStar = "";
        int previousStarCount;
        int currentStarCount;

        if (this.healthA < 0) {
            // b win
            int starCountChangeA = -1;
            if (flagNPCFight) {
                starCountChangeA = 0;
            }
            previousStarCount = PInsect.getInsectStarCount(this.iSA);
            if (previousStarCount == 0) {
                previousStar = "☆";
            } else if (previousStarCount == 10) {
                previousStar = "&c&l★".repeat(previousStarCount);
            } else {
                previousStar = "★".repeat(previousStarCount);
            }
            Pair<ItemStack, Integer> resultA = manipulateStar(this.insectA, this.iSA, starCountChangeA);
            this.iSA = resultA.getLeft();
            currentStarCount = PInsect.getInsectStarCount(this.iSA);
            if (this.pFPA != null) pFPA.collectInsectFromSession(this.insectA, currentStarCount, false);
            if (currentStarCount == 0) {
                currentStar = "☆";
            } else if (currentStarCount == 10) {
                currentStar = "&c&l★".repeat(currentStarCount);
            } else {
                currentStar = "★".repeat(currentStarCount);
            }
            replaceAMap.put("%star_prev%", previousStar);
            replaceAMap.put("%star_curr%", currentStar);
            switch (resultA.getRight()) {
                case -1:
                    this.pFPA.sendLog(pC.insectLoseHint, replaceAMap);
                    break;
                case 0:
                    replaceAMap.put("%extra%", String.format("%+.2f", (currentStarCount - previousStarCount) * PInsect.healthFactor));
                    this.pFPA.sendLog(pC.insectLoseHealthHint, replaceAMap);
                    break;
                case 1:
                    replaceAMap.put("%extra%", String.format("%+.2f", (currentStarCount - previousStarCount) * PInsect.attackFactor));
                    this.pFPA.sendLog(pC.insectLoseAttackHint, replaceAMap);
                    break;
                case 2:
                    replaceAMap.put("%extra%", String.format("%+.2f", (currentStarCount - previousStarCount) * PInsect.avoidanceFactor * 100));
                    this.pFPA.sendLog(pC.insectLoseAvoidanceHint, replaceAMap);
                    break;
            }
            int starCountChangeB = 1;
            if (flagNPCFight) {
                starCountChangeB = 0;
            }
            previousStarCount = PInsect.getInsectStarCount(this.iSB);
            if (previousStarCount == 0) {
                previousStar = "☆";
            } else if (previousStarCount == 10) {
                previousStar = "&c&l★".repeat(previousStarCount);
            } else {
                previousStar = "★".repeat(previousStarCount);
            }
            Pair<ItemStack, Integer> resultB = manipulateStar(this.insectB, this.iSB, starCountChangeB);
            this.iSB = resultB.getLeft();
            currentStarCount = PInsect.getInsectStarCount(this.iSB);
            if (this.pFPB != null) pFPB.collectInsectFromSession(this.insectB, currentStarCount, false);
            if (currentStarCount == 0) {
                currentStar = "☆";
            } else if (currentStarCount == 10) {
                currentStar = "&c&l★".repeat(currentStarCount);
            } else {
                currentStar = "★".repeat(currentStarCount);
            }
            replaceBMap.put("%star_prev%", previousStar);
            replaceBMap.put("%star_curr%", currentStar);
            switch (resultB.getRight()) {
                case -1:
                    if (!flagNPCFight) this.pFPB.sendLog(pC.insectWinHint, replaceBMap);
                    break;
                case 0:
                    replaceBMap.put("%extra%", String.format("%+.2f", (currentStarCount - previousStarCount) * PInsect.healthFactor));
                    if (!flagNPCFight) this.pFPB.sendLog(pC.insectWinHealthHint, replaceBMap);
                    break;
                case 1:
                    replaceBMap.put("%extra%", String.format("%+.2f", (currentStarCount - previousStarCount) * PInsect.attackFactor));
                    if (!flagNPCFight) this.pFPB.sendLog(pC.insectWinAttackHint, replaceBMap);
                    break;
                case 2:
                    replaceBMap.put("%extra%", String.format("%+.2f", (currentStarCount - previousStarCount) * PInsect.avoidanceFactor * 100));
                    if (!flagNPCFight) this.pFPB.sendLog(pC.insectWinAvoidanceHint, replaceBMap);
                    break;
            }

            float[] modifierList = PInsect.getInsectModifierList(this.iSB);
            this.healthB = modifierList[0];
            this.tick = 0;
            this.flagWait = true;
            for (PParticle pP : pC.insectFightEndParticle) pP.spawn(w, tableLocation);
            for (PSound pS : pC.insectFightEndSound) pS.play(w, tableLocation);
            if (this.entityA != null) this.entityA.setGlowing(false);
            if (this.entityB != null) this.entityB.setGlowing(false);
            if (this.pFPB != null && !flagNPCFight) this.pFPB.collectAndCheckInsectFight(false, this.insectFightNPC);
            ejectA();
            return;
        }
        if (this.healthB < 0) {
            int starCountChangeA = 1;
            if (flagNPCFight) {
                starCountChangeA = 3;
            }
            previousStarCount = PInsect.getInsectStarCount(this.iSA);
            if (previousStarCount == 0) {
                previousStar = "☆";
            } else if (previousStarCount == 10) {
                previousStar = "&c&l★".repeat(previousStarCount);
            } else {
                previousStar = "★".repeat(previousStarCount);
            }
            Pair<ItemStack, Integer> resultA = manipulateStar(this.insectA, this.iSA, starCountChangeA);
            this.iSA = resultA.getLeft();
            currentStarCount = PInsect.getInsectStarCount(this.iSA);
            if (this.pFPA != null) pFPA.collectInsectFromSession(this.insectA, currentStarCount, false);
            if (currentStarCount == 0) {
                currentStar = "☆";
            } else if (currentStarCount == 10) {
                currentStar = "&c&l★".repeat(currentStarCount);
            } else {
                currentStar = "★".repeat(currentStarCount);
            }
            replaceAMap.put("%star_prev%", previousStar);
            replaceAMap.put("%star_curr%", currentStar);
            switch (resultA.getRight()) {
                case -1:
                    this.pFPA.sendLog(pC.insectWinHint, replaceAMap);
                    break;
                case 0:
                    replaceAMap.put("%extra%", String.format("%+.2f", (currentStarCount - previousStarCount) * PInsect.healthFactor));
                    this.pFPA.sendLog(pC.insectWinHealthHint, replaceAMap);
                    break;
                case 1:
                    replaceAMap.put("%extra%", String.format("%+.2f", (currentStarCount - previousStarCount) * PInsect.attackFactor));
                    this.pFPA.sendLog(pC.insectWinAttackHint, replaceAMap);
                    break;
                case 2:
                    replaceAMap.put("%extra%", String.format("%+.2f", (currentStarCount - previousStarCount) * PInsect.avoidanceFactor * 100));
                    this.pFPA.sendLog(pC.insectWinAvoidanceHint, replaceAMap);
                    break;
            }
            int starCountChangeB = -1;
            if (flagNPCFight) {
                starCountChangeB = 0;
            }
            previousStarCount = PInsect.getInsectStarCount(this.iSB);
            if (previousStarCount == 0) {
                previousStar = "☆";
            } else if (previousStarCount == 10) {
                previousStar = "&c&l★".repeat(previousStarCount);
            } else {
                previousStar = "★".repeat(previousStarCount);
            }
            Pair<ItemStack, Integer> resultB = manipulateStar(this.insectB, this.iSB, starCountChangeB);
            this.iSB = resultB.getLeft();
            currentStarCount = PInsect.getInsectStarCount(this.iSB);
            if (this.pFPB != null) pFPB.collectInsectFromSession(this.insectB, currentStarCount, false);
            if (currentStarCount == 0) {
                currentStar = "☆";
            } else if (currentStarCount == 10) {
                currentStar = "&c&l★".repeat(currentStarCount);
            } else {
                currentStar = "★".repeat(currentStarCount);
            }
            replaceBMap.put("%star_prev%", previousStar);
            replaceBMap.put("%star_curr%", currentStar);
            switch (resultB.getRight()) {
                case -1:
                    if (!flagNPCFight) this.pFPB.sendLog(pC.insectLoseHint, replaceBMap);
                    break;
                case 0:
                    replaceBMap.put("%extra%", String.format("%+.2f", (currentStarCount - previousStarCount) * PInsect.healthFactor));
                    if (!flagNPCFight) this.pFPB.sendLog(pC.insectLoseHealthHint, replaceBMap);
                    break;
                case 1:
                    replaceBMap.put("%extra%", String.format("%+.2f", (currentStarCount - previousStarCount) * PInsect.attackFactor));
                    if (!flagNPCFight) this.pFPB.sendLog(pC.insectLoseAttackHint, replaceBMap);
                    break;
                case 2:
                    replaceBMap.put("%extra%", String.format("%+.2f", (currentStarCount - previousStarCount) * PInsect.avoidanceFactor * 100));
                    if (!flagNPCFight) this.pFPB.sendLog(pC.insectLoseAvoidanceHint, replaceBMap);
                    break;
            }
            float[] modifierList = PInsect.getInsectModifierList(this.iSA);
            this.healthA = modifierList[0];
            this.tick = 0;
            this.flagWait = true;
            for (PParticle pP : pC.insectFightEndParticle) pP.spawn(w, tableLocation);
            for (PSound pS : pC.insectFightEndSound) pS.play(w, tableLocation);
            if (this.entityA != null) this.entityA.setGlowing(false);
            if (this.entityB != null) this.entityB.setGlowing(false);
            if (this.pFPA != null) this.pFPA.collectAndCheckInsectFight(flagNPCFight, insectFightNPC);
            ejectB();
            return;
        }

        this.tick++;
    }
}
