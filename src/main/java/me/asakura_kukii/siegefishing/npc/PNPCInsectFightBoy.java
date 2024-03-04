package me.asakura_kukii.siegefishing.npc;

import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.io.PFile;
import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.io.helper.PFileIdDeserializer;
import me.asakura_kukii.siegecore.io.helper.PFileIdSerializer;
import me.asakura_kukii.siegefishing.achievement.PAchievementCollectable;
import me.asakura_kukii.siegefishing.creature.insect.PInsect;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PNPCInsectFightBoy extends PFile {

    public String displayName = "";

    @JsonSerialize(using = PFileIdSerializer.class)
    @JsonDeserialize(using = PFileIdDeserializer.class)
    public PNPCInsectFight pnpcInsectFight = null;

    public List<String> beforeLog = new ArrayList<>();

    public List<String> afterLog = new ArrayList<>();

    public void sendLog(Player p) {
        if (pnpcInsectFight == null || pnpcInsectFight.insect == null) return;
        PType pT = PType.getPType(PFishPlayer.class);
        if (pT == null) return;
        PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
        if (pFP == null) return;
        HashMap<String, String> replace = new HashMap<>();
        replace.put("%insect%", pnpcInsectFight.insect.level.colorString + pnpcInsectFight.insect.name);
        replace.put("%name%", displayName);
        replace.put("%player%", p.getName());
        if (pFP.insectNPCFightSuccessCounter.contains(pnpcInsectFight)) {
            pFP.sendLog(afterLog, replace);
        } else {
            pFP.sendLog(beforeLog, replace);
        }
    }

    @Override
    public void finalizeDeserialization() throws IOException {

    }

    @Override
    public void defaultValue() {

    }

    @Override
    public void load() {

    }

    @Override
    public void unload() {

    }
}
