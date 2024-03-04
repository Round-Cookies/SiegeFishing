package me.asakura_kukii.siegefishing.player;

import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.item.PAbstractItem;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class PFishPlayerListener implements Listener {
    @EventHandler
    public void onPickUp(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        Item i = e.getItem();
        ItemStack iS = i.getItemStack();
        PAbstractItem pAI = PAbstractItem.getPItem(iS);
        if (pAI == null) return;
        PType pT = PType.getPType(PFishPlayer.class);
        if (pT == null) return;
        PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
        if (pFP == null) return;
        pFP.collectItem(pAI);
    }
}
