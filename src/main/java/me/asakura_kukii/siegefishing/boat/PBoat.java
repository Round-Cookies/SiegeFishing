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
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

import java.io.IOException;

public class PBoat extends PAbstractItem {

    @JsonSerialize(using = PFileIdSerializer.class)
    @JsonDeserialize(using = PFileIdDeserializer.class)
    public PDeco deco = null;

    @Override
    public void trigger(Player player, PTriggerType pTriggerType, PTriggerSubType pTriggerSubType, PTriggerSlot pTriggerSlot, ItemStack itemStack, long l) {
        if (pTriggerType == PTriggerType.RIGHT && pTriggerSubType == PTriggerSubType.INIT && pTriggerSlot == PTriggerSlot.MAIN) {
            RayTraceResult rTR = player.rayTraceBlocks(5, FluidCollisionMode.ALWAYS);
            if (rTR != null && rTR.getHitBlock() != null && rTR.getHitBlock().getType().equals(Material.WATER)) {
                PType pT = PType.getPType(PFishPlayer.class);
                if (pT == null) return;
                PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(player.getUniqueId().toString());
                if (pFP == null) return;
                if (pFP.boatEntity != null) {
                    ItemDisplayHandler.remove(pFP.boatEntity);
                    pFP.boatEntity = null;
                }
                if (player.getVehicle() != null) {
                    player.getVehicle().remove();
                }
                Location location = rTR.getHitPosition().toLocation(player.getWorld());
                location.setYaw(player.getLocation().getYaw());
                PQuaternion rotation = new PVector(0, 0, 1).rotationToExceptZ(new PVector(-PMath.sin(player.getLocation().getYaw() / 180 * PMath.pi), 0.0F, PMath.cos(player.getLocation().getYaw() / 180 * PMath.pi)));
                Boat b = (Boat) player.getWorld().spawnEntity(location, EntityType.BOAT);
                b.setBoatType(Boat.Type.BAMBOO);
                b.addPassenger(player);
                pFP.boatEntity = ItemDisplayHandler.spawn(player.getWorld(), PVector.fromLocation(location), new PVector(), rotation, new PVector(1, 1, 1), deco);
                player.addPassenger(pFP.boatEntity);
            }
        }
    }

    @Override
    public ItemStack finalizeGetItemStack(ItemStack itemStack) {
        return itemStack;
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
