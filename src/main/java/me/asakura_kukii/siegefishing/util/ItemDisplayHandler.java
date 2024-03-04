package me.asakura_kukii.siegefishing.util;

import me.asakura_kukii.siegecore.SiegeCore;
import me.asakura_kukii.siegecore.util.math.PMath;
import me.asakura_kukii.siegecore.util.math.PQuaternion;
import me.asakura_kukii.siegecore.util.math.PVector;
import me.asakura_kukii.siegefishing.deco.PDeco;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;

import java.util.HashMap;
import java.util.UUID;

public class ItemDisplayHandler {

    public static HashMap<UUID, ItemDisplay> itemDisplayHashMap = new HashMap<>();

    public static ItemDisplay spawn(World w, PVector location, PVector translation, PQuaternion rotation, float scale, PDeco deco) {
        return spawn(w, location, translation, rotation, new PVector(scale, scale, scale), deco);
    }

    public static ItemDisplay spawn(World w, PVector location, PVector translation, PQuaternion rotation, PVector scale, PDeco deco) {
        ItemDisplay iD = (ItemDisplay) w.spawnEntity(location.getLocation(w), EntityType.ITEM_DISPLAY);
        if (deco != null) iD.setItemStack(deco.getItemStack());
        if (deco == null) iD.setItemStack(new ItemStack(Material.AIR));
        iD.setTransformation(new Transformation(
                translation,
                rotation,
                scale,
                new PQuaternion()
        ));
        iD.teleport(location.getLocation(w));
        iD.setInterpolationDuration(0);
        iD.setTeleportDuration(0);
        iD.setInterpolationDelay(0);
        iD.getScoreboardTags().add(SiegeCore.sessionUUID.toString());
        iD.getScoreboardTags().add("plugin_generated");
        itemDisplayHashMap.put(iD.getUniqueId(), iD);
        return iD;
    }

    public static void update(ItemDisplay iD, PVector location, PVector translation, PQuaternion rotation, float scale, int duration, boolean flagGlow) {
        update(iD, location, translation, rotation, new PVector(scale, scale, scale), duration, flagGlow);
    }

    public static void update(ItemDisplay iD, PVector location, PVector translation, PQuaternion rotation, PVector scale, int duration, boolean flagGlow) {
        if (iD == null || iD.isDead()) {
            return;
        }
        iD.setTransformation(new Transformation(
                translation.clone().add(PMath.ran() * 0.00001F, 0, 0),
                rotation.clone().rotateX(PMath.ran() * 0.00001F),
                scale.clone().add(PMath.ran() * 0.00001F, 0, 0),
                new PQuaternion()
        ));
        iD.teleport(location.getLocation(iD.getWorld()).add(PMath.ran() * 0.00001F, 0, 0));
        iD.setInterpolationDuration(duration);
        iD.setTeleportDuration(duration);
        iD.setInterpolationDelay(0);
        iD.setGlowing(flagGlow);
    }

    public static void update(ItemDisplay iD, PVector translation, PQuaternion rotation, PVector scale, int duration, boolean flagGlow) {
        if (iD == null || iD.isDead()) {
            return;
        }
        iD.setTransformation(new Transformation(
                translation.clone().add(PMath.ran() * 0.00001F, 0, 0),
                rotation.clone().rotateX(PMath.ran() * 0.00001F),
                scale.clone().add(PMath.ran() * 0.00001F, 0, 0),
                new PQuaternion()
        ));
        iD.setInterpolationDuration(duration);
        iD.setTeleportDuration(duration);
        iD.setInterpolationDelay(0);
        iD.setGlowing(flagGlow);
    }


    public static void update(ItemDisplay iD, PVector translation, PQuaternion rotation, PVector scale, int duration, boolean flagGlow, Color c) {
        if (iD == null || iD.isDead()) {
            return;
        }
        iD.setTransformation(new Transformation(
                translation.clone().add(PMath.ran() * 0.00001F, 0, 0),
                rotation.clone().rotateX(PMath.ran() * 0.00001F),
                scale.clone().add(PMath.ran() * 0.00001F, 0, 0),
                new PQuaternion()
        ));
        iD.setInterpolationDuration(duration);
        iD.setTeleportDuration(duration);
        iD.setInterpolationDelay(0);
        iD.setGlowing(flagGlow);
        iD.setGlowColorOverride(c);
    }



    public static void remove(ItemDisplay iD) {
        if (iD == null) return;
        itemDisplayHashMap.remove(iD.getUniqueId());
        iD.remove();
    }

    public static void removeAll() {
        for (ItemDisplay iD : itemDisplayHashMap.values()) {
            iD.remove();
        }
        itemDisplayHashMap.clear();
    }
}
