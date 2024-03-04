package me.asakura_kukii.siegefishing.npc;

import me.asakura_kukii.siegecore.io.PFile;
import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PNPCListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        Entity entity = e.getRightClicked();
        if (entity instanceof Interaction) {
            Interaction i = (Interaction) entity;
            PType pT = PType.getPType(PNPCShop.class);
            if (pT == null) return;
            PType pT2 = PType.getPType(PFishPlayer.class);
            if (pT2 == null) return;
            PType pT3 = PType.getPType(PNPCLottery.class);
            if (pT3 == null) return;
            PFishPlayer pFP = (PFishPlayer) pT2.getPFileSafely(e.getPlayer().getUniqueId().toString());
            if (pFP == null) return;
            if (i.getScoreboardTags().contains("bucket")) {
                if (!pFP.collectBucket(i.getLocation().getBlock())) {
                    pFP.fishBag.open(e.getPlayer());
                }
                return;
            }
            if (i.getScoreboardTags().contains("craft")) {
                pFP.craft.open(e.getPlayer());
                return;
            }
            if (i.getScoreboardTags().contains("acquire")) {
                pFP.acquire.open(e.getPlayer());
                return;
            }
            if (i.getScoreboardTags().contains("lottery")) {
                pFP.fishLottery.open(e.getPlayer());
                return;
            }
            if (i.getScoreboardTags().contains("rod_shop")) {
                pFP.fishRodShop.open(e.getPlayer());
                return;
            }
            if (i.getScoreboardTags().contains("tool_shop")) {
                pFP.fishToolShop.open(e.getPlayer());
                return;
            }
            if (i.getScoreboardTags().contains("beginner_shop")) {
                pFP.fishBeginnerShop.open(e.getPlayer());
                return;
            }
        }
    }
}
