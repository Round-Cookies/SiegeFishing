package me.asakura_kukii.siegefishing.creature.insect;

import me.asakura_kukii.siegecore.io.PFile;
import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.item.PAbstractItem;
import me.asakura_kukii.siegefishing.npc.PNPCInsectFight;
import me.asakura_kukii.siegefishing.npc.PNPCInsectFightBoy;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class PInsectFightSessionListener implements Listener {

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        PType pT = PType.getPType(PFishPlayer.class);
        if (pT == null) return;
        PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
        if (pFP == null) return;
        if (e.getRightClicked() instanceof Interaction) {
            Interaction i = (Interaction) e.getRightClicked();
            PType pT4 = PType.getPType(PNPCInsectFightBoy.class);
            for (PFile pF : pT4.getPFileList()) {
                if (e.getRightClicked().getScoreboardTags().contains(pF.id)) {
                    PNPCInsectFightBoy pnpcInsectFightBoy = (PNPCInsectFightBoy) pF;
                    pnpcInsectFightBoy.sendLog(p);
                }
            }
        }

        if (p.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
            if (e.getRightClicked() instanceof Interaction) {
                Interaction i = (Interaction) e.getRightClicked();
                if (e.getRightClicked().getScoreboardTags().contains("stage_player")) {
                    if (PInsectFightSession.activeInsectFightSession.containsKey(i)) {
                        PInsectFightSession pIFS = PInsectFightSession.activeInsectFightSession.get(i);
                        pIFS.removeInsect(pFP);
                    }
                }
                PType pT2 = PType.getPType(PNPCInsectFight.class);
                if (pT2 == null) return;
                for (PFile pF : pT2.getPFileList()) {
                    if (e.getRightClicked().getScoreboardTags().contains(pF.id)) {
                        if (PInsectFightSession.activeInsectFightSession.containsKey(i)) {
                            PInsectFightSession pIFS = PInsectFightSession.activeInsectFightSession.get(i);
                            pIFS.removeInsect(pFP);
                        }
                    }
                }
            }
        }
        ItemStack iS = p.getInventory().getItemInMainHand();
        PAbstractItem pAI = PAbstractItem.getPItem(iS);
        if (pAI == null || pAI.type != PType.getPType(PInsect.class)) return;
        if (e.getRightClicked() instanceof Interaction) {
            Interaction i = (Interaction) e.getRightClicked();
            PInsect pI = (PInsect) pAI;
            if (e.getRightClicked().getScoreboardTags().contains("stage_player")) {
                PInsectFightSession pIFS;
                if (PInsectFightSession.activeInsectFightSession.containsKey(i)) {
                    pIFS = PInsectFightSession.activeInsectFightSession.get(i);
                } else {
                    pIFS = new PInsectFightSession(i, false, null);
                }
                ItemStack iS2 = iS.clone();
                iS2.setAmount(1);
                if (pIFS.addInsect(pFP, pI, iS2)) {
                    iS.setAmount(iS.getAmount() - 1);
                    p.getInventory().setItemInMainHand(iS);
                }
            }
            PType pT2 = PType.getPType(PNPCInsectFight.class);
            if (pT2 == null) return;
            for (PFile pF : pT2.getPFileList()) {
                if (e.getRightClicked().getScoreboardTags().contains(pF.id) && ((PNPCInsectFight) pF).insect != null) {
                    PInsectFightSession pIFS;
                    boolean flagNewSession = false;
                    if (PInsectFightSession.activeInsectFightSession.containsKey(i)) {
                        pIFS = PInsectFightSession.activeInsectFightSession.get(i);
                    } else {
                        pIFS = new PInsectFightSession(i, true, (PNPCInsectFight) pF);
                        flagNewSession = true;
                    }
                    ItemStack iS2 = iS.clone();
                    iS2.setAmount(1);
                    if (pIFS.addInsect(pFP, pI, iS2)) {
                        iS.setAmount(iS.getAmount() - 1);
                        p.getInventory().setItemInMainHand(iS);
                        if (flagNewSession) {
                            pIFS.addNPCInsect(((PNPCInsectFight) pF).insect, ((PNPCInsectFight) pF).health, ((PNPCInsectFight) pF).attack, ((PNPCInsectFight) pF).avoidance);
                        }
                    }
                }
            }
        }
    }
}
