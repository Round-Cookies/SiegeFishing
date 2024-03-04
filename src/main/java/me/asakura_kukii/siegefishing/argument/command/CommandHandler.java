package me.asakura_kukii.siegefishing.argument.command;

import me.asakura_kukii.siegecore.argument.PArgument;
import me.asakura_kukii.siegecore.argument.PSender;
import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.util.math.PMath;
import me.asakura_kukii.siegecore.util.math.PQuaternion;
import me.asakura_kukii.siegecore.util.math.PVector;
import me.asakura_kukii.siegefishing.config.PConfig;
import me.asakura_kukii.siegefishing.deco.PDeco;
import me.asakura_kukii.siegefishing.map.PFishMap;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import me.asakura_kukii.siegefishing.player.PFishPlayerLevel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;

public class CommandHandler {

    public static boolean onCommand(PSender sender, PArgument argument) {
        sender.nextLine();
        sender.log("Issued:");
        sender.raw(">> " + argument.colorize());

        String s = argument.nextString();
        if (!argument.success) {
            sender.error("Missing sub-argument");
            return false;
        }

        switch (s) {
            case "info":
                return onInfo(sender, argument);
            case "show":
                return onShow(sender, argument);
            case "head":
                return onHead(sender, argument);
        }

        if (!sender.hasPerm("siegefishing.admin")) return false;

        switch (s) {
            case "rank":
                Player p3 = sender.getPlayer();
                if (p3 == null) return false;
                PType pT3 = PType.getPType(PFishPlayer.class);
                if (pT3 == null) return false;
                PFishPlayer pFP3 = (PFishPlayer) pT3.getPFileSafely(p3.getUniqueId().toString());
                if (pFP3 == null) return false;
                pFP3.rankBook.open(p3);
                return true;
            case "populate":
                return onPopulate(sender, argument);
            case "level":
                return onLevel(sender, argument);
            case "bucket":
                return onBucket(sender, argument);
            case "craft":
                return onCraft(sender, argument);
            case "acquire":
                return onAcquire(sender, argument);
            case "stage":
                return onStage(sender, argument);
            case "purge":
                String s2 = argument.nextString();
                if (!argument.success) {
                    return false;
                }
                Player p = Bukkit.getPlayer(s2);
                if (p == null) return false;
                PType pT = PType.getPType(PFishPlayer.class);
                if (pT == null) return false;
                PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
                if (pFP == null) return false;
                pFP.purgeAchievement();
                return true;
            case "stage_npc":
                return onStageNPC(sender, argument);
            case "stage_npc_boy":
                return onStageNPCBoy(sender, argument);
            case "lottery":
                return onLotteryNPC(sender, argument);
            case "rod_shop":
                return onRodShopNPC(sender, argument);
            case "tool_shop":
                return onToolShopNPC(sender, argument);
            case "beginner_shop":
                return onBeginnerShopNPC(sender, argument);
            default:
                sender.error("Invalid sub-argument");
                return false;
        }
    }

    public static boolean onInfo(PSender sender, PArgument argument) {
        sender.info("Standby!");
        return true;
    }

    public static boolean onPopulate(PSender sender, PArgument argument) {
        if (sender.getPlayer() == null) return false;
        String s2 = argument.nextString();
        if (!argument.success) {
            sender.error("Missing sub-argument");
            return false;
        }
        PFishMap pFM = (PFishMap) PType.getPType(PFishMap.class).getPFile(s2);
        if (pFM == null) {
            sender.error("No world found");
            return false;
        }
        pFM.populate();
        return true;
    }

    public static boolean onLevel(PSender sender, PArgument argument) {
        if (sender.getPlayer() == null) return false;
        String s2 = argument.nextString();
        if (!argument.success) {
            sender.error("Invalid player name");
            return false;
        }
        Player p = Bukkit.getPlayer(s2);
        if (p == null) {
            sender.error("Invalid player name");
            return false;
        }
        PType pT = PType.getPType(PFishPlayer.class);
        if (pT == null) return false;
        PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
        if (pFP == null) return false;
        int i = argument.nextInt();
        if (!argument.success) {
            sender.error("Invalid level");
            return false;
        }
        if (argument.hasNext()) {
            int i2 = argument.nextInt();
            if (!argument.success) {
                sender.error("Invalid experience");
                return false;
            }
            pFP.experience = i2;
        }
        switch (i) {
            case 0:
                pFP.level = PFishPlayerLevel.BEGINNER;
                break;
            case 1:
                pFP.level = PFishPlayerLevel.AMATEUR;
                break;
            case 2:
                pFP.level = PFishPlayerLevel.VETERAN;
                break;
            case 3:
                pFP.level = PFishPlayerLevel.EXPERT;
                break;
            case 4:
                pFP.level = PFishPlayerLevel.LEGENDARY;
                break;
            default:
                sender.error("Invalid level");
                return false;
        }
        return true;
    }

    public static boolean onShow(PSender sender, PArgument argument) {
        if (!sender.isPlayer()) return false;
        Player p = sender.getPlayer();
        if (p == null) return false;
        PType pT = PType.getPType(PFishPlayer.class);
        if (pT == null) return false;
        PType pT2 = PType.getPType(PConfig.class);
        if (pT2 == null) return false;
        PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
        PConfig pC = (PConfig) pT2.getPFileSafely("config");
        ItemStack iS = p.getInventory().getItemInMainHand();
        pFP.sendMessageWithItem(pC.showItemBroadCastFormat.replaceAll("%player%", p.getName()), iS, true);
        return true;
    }

    public static boolean onHead(PSender sender, PArgument argument) {
        if (!sender.isPlayer()) return false;
        Player p = sender.getPlayer();
        if (p == null) return false;
        PType pT = PType.getPType(PFishPlayer.class);
        if (pT == null) return false;
        PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
        ItemStack iS = p.getInventory().getItemInMainHand();
        ItemStack helmetIS = p.getInventory().getHelmet();
        if (helmetIS != null) {
            ItemStack helmetISClone = helmetIS.clone();
            p.getInventory().setHelmet(null);
            pFP.giveItemStack(helmetISClone);
        }
        p.getInventory().setHelmet(iS);
        p.getInventory().setItemInMainHand(null);
        return true;
    }

    public static boolean onBucket(PSender sender, PArgument argument) {
        PType deco = PType.getPType(PDeco.class);
        Player p = sender.getPlayer();
        Location l = p.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);
        ItemDisplay iD = (ItemDisplay) p.getWorld().spawnEntity(l, EntityType.ITEM_DISPLAY);
        iD.setItemStack(((PDeco) deco.getPFileSafely("bucket")).getItemStack());
        iD.setPersistent(true);
        iD.setTransformation(new Transformation(new PVector(0, 0.5F, 0), new PQuaternion(), new PVector(1, 1, 1), new PQuaternion()));
        iD.addScoreboardTag("bucket");
        Interaction i = (Interaction) p.getWorld().spawnEntity(l, EntityType.INTERACTION);
        i.setInteractionHeight(2);
        i.setPersistent(true);
        i.addScoreboardTag("bucket");
        return true;
    }

    public static boolean onCraft(PSender sender, PArgument argument) {
        PType deco = PType.getPType(PDeco.class);
        Player p = sender.getPlayer();
        Location l = p.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);
        ItemDisplay iD = (ItemDisplay) p.getWorld().spawnEntity(l, EntityType.ITEM_DISPLAY);
        iD.setItemStack(((PDeco) deco.getPFileSafely("craft")).getItemStack());
        iD.setPersistent(true);
        iD.setTransformation(new Transformation(new PVector(0, 0.5F, 0), new PQuaternion(), new PVector(1, 1, 1), new PQuaternion()));
        iD.addScoreboardTag("craft");
        Interaction i = (Interaction) p.getWorld().spawnEntity(l, EntityType.INTERACTION);
        i.setInteractionHeight(2);
        i.setPersistent(true);
        i.addScoreboardTag("craft");
        return true;
    }

    public static boolean onAcquire(PSender sender, PArgument argument) {
        PType deco = PType.getPType(PDeco.class);
        Player p = sender.getPlayer();
        float yaw = p.getLocation().getYaw() / 180 * PMath.pi;
        PQuaternion rotation = new PVector(0, 0, 1).rotationToExceptZ(new PVector(-PMath.sin(yaw), 0.0F, PMath.cos(yaw)));
        Location l = p.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);
        ItemDisplay iD = (ItemDisplay) p.getWorld().spawnEntity(l, EntityType.ITEM_DISPLAY);
        iD.setItemStack(((PDeco) deco.getPFileSafely("acquire")).getItemStack());
        iD.setPersistent(true);
        iD.setTransformation(new Transformation(new PVector(0, 0.5F, 0), rotation, new PVector(1, 1, 1), new PQuaternion()));
        iD.addScoreboardTag("acquire");
        Interaction i = (Interaction) p.getWorld().spawnEntity(l, EntityType.INTERACTION);
        i.setInteractionHeight(2);
        i.setPersistent(true);
        i.addScoreboardTag("acquire");
        return true;
    }

    public static boolean onStage(PSender sender, PArgument argument) {
        PType deco = PType.getPType(PDeco.class);
        Player p = sender.getPlayer();
        Location l = p.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);
        ItemDisplay iD = (ItemDisplay) p.getWorld().spawnEntity(l, EntityType.ITEM_DISPLAY);
        iD.setItemStack(((PDeco) deco.getPFileSafely("stage")).getItemStack());
        iD.setPersistent(true);
        iD.setTransformation(new Transformation(new PVector(0, 0.5F, 0), new PQuaternion().rotateY(PMath.ran() * PMath.pi * 2), new PVector(1, 1, 1), new PQuaternion()));
        iD.addScoreboardTag("stage_player");
        Interaction i = (Interaction) p.getWorld().spawnEntity(l, EntityType.INTERACTION);
        i.setInteractionHeight(1);
        i.setPersistent(true);
        i.addScoreboardTag("stage_player");
        return true;
    }


    public static boolean onStageNPC(PSender sender, PArgument argument) {
        String id = argument.nextString();
        if (!argument.success) {
            sender.error("OHNO STH WRONG");
            return false;
        }
        PType deco = PType.getPType(PDeco.class);
        Player p = sender.getPlayer();
        float yaw = p.getLocation().getYaw() / 180 * PMath.pi;
        PQuaternion rotation = new PVector(0, 0, 1).rotationToExceptZ(new PVector(-PMath.sin(yaw), 0.0F, PMath.cos(yaw)));
        Location l = p.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);
        ItemDisplay iD = (ItemDisplay) p.getWorld().spawnEntity(l, EntityType.ITEM_DISPLAY);
        iD.setItemStack(((PDeco) deco.getPFileSafely("stage_npc")).getItemStack());
        iD.setPersistent(true);
        iD.setTransformation(new Transformation(new PVector(0, 0.5F, 0), rotation, new PVector(1, 1, 1), new PQuaternion()));
        iD.addScoreboardTag(id);
        iD.addScoreboardTag("stage_npc");
        Interaction i = (Interaction) p.getWorld().spawnEntity(l, EntityType.INTERACTION);
        i.setInteractionHeight(1);
        i.setPersistent(true);
        i.addScoreboardTag(id);
        iD.addScoreboardTag("stage_npc");
        return true;
    }

    public static boolean onStageNPCBoy(PSender sender, PArgument argument) {
        String id = argument.nextString();
        if (!argument.success) {
            sender.error("OHNO STH WRONG");
            return false;
        }
        PType deco = PType.getPType(PDeco.class);
        Player p = sender.getPlayer();
        float yaw = p.getLocation().getYaw() / 180 * PMath.pi;
        PQuaternion rotation = new PVector(0, 0, 1).rotationToExceptZ(new PVector(-PMath.sin(yaw), 0.0F, PMath.cos(yaw)));
        Location l = p.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);
        ItemDisplay iD = (ItemDisplay) p.getWorld().spawnEntity(l, EntityType.ITEM_DISPLAY);
        iD.setItemStack(((PDeco) deco.getPFileSafely(id)).getItemStack());
        iD.setPersistent(true);
        iD.setTransformation(new Transformation(new PVector(0, 0.5F, 0), rotation, new PVector(1, 1, 1), new PQuaternion()));
        iD.addScoreboardTag(id);
        iD.addScoreboardTag("stage_npc_boy");
        Interaction i = (Interaction) p.getWorld().spawnEntity(l, EntityType.INTERACTION);
        i.setInteractionHeight(2);
        i.setPersistent(true);
        i.addScoreboardTag(id);
        iD.addScoreboardTag("stage_npc_boy");
        return true;
    }


    public static boolean onLotteryNPC(PSender sender, PArgument argument) {
        PType deco = PType.getPType(PDeco.class);
        Player p = sender.getPlayer();
        float yaw = p.getLocation().getYaw() / 180 * PMath.pi;
        PQuaternion rotation = new PVector(0, 0, 1).rotationToExceptZ(new PVector(-PMath.sin(yaw), 0.0F, PMath.cos(yaw)));
        Location l = p.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);

        ItemDisplay iD = (ItemDisplay) p.getWorld().spawnEntity(l, EntityType.ITEM_DISPLAY);
        iD.setItemStack(((PDeco) deco.getPFileSafely("lottery")).getItemStack());
        iD.setPersistent(true);
        iD.setTransformation(new Transformation(new PVector(0, 1.5F, 0), rotation, new PVector(1, 1, 1), new PQuaternion()));
        iD.addScoreboardTag("lottery");
        Interaction i = (Interaction) p.getWorld().spawnEntity(l, EntityType.INTERACTION);
        i.setInteractionHeight(2);
        i.setPersistent(true);
        i.addScoreboardTag("lottery");
        return true;
    }

    public static boolean onRodShopNPC(PSender sender, PArgument argument) {
        PType deco = PType.getPType(PDeco.class);
        Player p = sender.getPlayer();
        float yaw = p.getLocation().getYaw() / 180 * PMath.pi;
        PQuaternion rotation = new PVector(0, 0, 1).rotationToExceptZ(new PVector(-PMath.sin(yaw), 0.0F, PMath.cos(yaw)));
        Location l = p.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);
        ItemDisplay iD = (ItemDisplay) p.getWorld().spawnEntity(l, EntityType.ITEM_DISPLAY);
        iD.setItemStack(((PDeco) deco.getPFileSafely("rod_shop")).getItemStack());
        iD.setPersistent(true);
        iD.setTransformation(new Transformation(new PVector(0, 0.5F, 0), rotation, new PVector(1, 1, 1), new PQuaternion()));
        iD.addScoreboardTag("rod_shop");
        Interaction i = (Interaction) p.getWorld().spawnEntity(l, EntityType.INTERACTION);
        i.setInteractionHeight(2);
        i.setPersistent(true);
        i.addScoreboardTag("rod_shop");
        return true;
    }

    public static boolean onToolShopNPC(PSender sender, PArgument argument) {
        PType deco = PType.getPType(PDeco.class);
        Player p = sender.getPlayer();
        float yaw = p.getLocation().getYaw() / 180 * PMath.pi;
        PQuaternion rotation = new PVector(0, 0, 1).rotationToExceptZ(new PVector(-PMath.sin(yaw), 0.0F, PMath.cos(yaw)));
        Location l = p.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);
        ItemDisplay iD = (ItemDisplay) p.getWorld().spawnEntity(l, EntityType.ITEM_DISPLAY);
        iD.setItemStack(((PDeco) deco.getPFileSafely("tool_shop")).getItemStack());
        iD.setPersistent(true);
        iD.setTransformation(new Transformation(new PVector(0, 0.5F, 0), rotation, new PVector(1, 1, 1), new PQuaternion()));
        iD.addScoreboardTag("tool_shop");
        Interaction i = (Interaction) p.getWorld().spawnEntity(l, EntityType.INTERACTION);
        i.setInteractionHeight(2);
        i.setPersistent(true);
        i.addScoreboardTag("tool_shop");
        return true;
    }

    public static boolean onBeginnerShopNPC(PSender sender, PArgument argument) {
        PType deco = PType.getPType(PDeco.class);
        Player p = sender.getPlayer();
        float yaw = p.getLocation().getYaw() / 180 * PMath.pi;
        PQuaternion rotation = new PVector(0, 0, 1).rotationToExceptZ(new PVector(-PMath.sin(yaw), 0.0F, PMath.cos(yaw)));
        Location l = p.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);
        ItemDisplay iD = (ItemDisplay) p.getWorld().spawnEntity(l, EntityType.ITEM_DISPLAY);
        iD.setItemStack(((PDeco) deco.getPFileSafely("beginner_shop")).getItemStack());
        iD.setPersistent(true);
        iD.setTransformation(new Transformation(new PVector(0, 0.5F, 0), rotation, new PVector(1, 1, 1), new PQuaternion()));
        iD.addScoreboardTag("beginner_shop");
        Interaction i = (Interaction) p.getWorld().spawnEntity(l, EntityType.INTERACTION);
        i.setInteractionHeight(2);
        i.setPersistent(true);
        i.addScoreboardTag("beginner_shop");
        return true;
    }
}
