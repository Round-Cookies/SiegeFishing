package me.asakura_kukii.siegefishing;

import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.util.format.PFormat;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class SiegeFishingListener implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        PType pT = PType.getPType(PFishPlayer.class);
        if (pT == null) return;
        PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
        if (pFP == null) return;
        if (pFP.region != null) {
            e.setFormat(PFormat.format(pFP.region.name + "&7<&f%s&7> &f%s"));
            e.setMessage(PFormat.format(e.getMessage()));
        }
    }
}
