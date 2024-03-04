package me.asakura_kukii.siegefishing.boat;

import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.io.helper.PFileIdDeserializer;
import me.asakura_kukii.siegecore.io.helper.PFileIdSerializer;
import me.asakura_kukii.siegecore.item.PAbstractItem;
import me.asakura_kukii.siegecore.trigger.PTriggerSlot;
import me.asakura_kukii.siegecore.trigger.PTriggerSubType;
import me.asakura_kukii.siegecore.trigger.PTriggerType;
import me.asakura_kukii.siegecore.util.math.PMath;
import me.asakura_kukii.siegecore.util.math.PQuaternion;
import me.asakura_kukii.siegecore.util.math.PVector;
import me.asakura_kukii.siegefishing.deco.PDeco;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import me.asakura_kukii.siegefishing.util.ItemDisplayHandler;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

import java.io.IOException;

public class PBoatListener implements Listener {
    @EventHandler
    public void onLeaveBoat(VehicleExitEvent e) {
        if (e.getExited() instanceof Player) {
            if (!e.getVehicle().getPassengers().get(0).equals(e.getExited())) return;
            e.getVehicle().remove();
            PType pT = PType.getPType(PFishPlayer.class);
            if (pT == null) return;
            PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(e.getExited().getUniqueId().toString());
            if (pFP == null) return;
            if (pFP.boatEntity != null) {
                ItemDisplayHandler.remove(pFP.boatEntity);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        PType pT = PType.getPType(PFishPlayer.class);
        if (pT == null) return;
        PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(e.getPlayer().getUniqueId().toString());
        if (pFP == null) return;
        if (pFP.boatEntity != null) {
            ItemDisplayHandler.remove(pFP.boatEntity);
        }
        if (e.getPlayer().getVehicle() != null && e.getPlayer().getVehicle().getPassengers().get(0).equals(e.getPlayer())) {
            e.getPlayer().getVehicle().remove();
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        PType pT = PType.getPType(PFishPlayer.class);
        if (pT == null) return;
        PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(e.getPlayer().getUniqueId().toString());
        if (pFP == null) return;
        if (pFP.boatEntity != null) {
            ItemDisplayHandler.remove(pFP.boatEntity);
        }
        if (e.getPlayer().getVehicle() != null && e.getPlayer().getVehicle().getPassengers().get(0).equals(e.getPlayer())) {
            e.getPlayer().getVehicle().remove();
        }
    }
}
