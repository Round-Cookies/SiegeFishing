package me.asakura_kukii.siegefishing;

import me.asakura_kukii.siegecore.SiegeCore;
import me.asakura_kukii.siegecore.argument.PArgument;
import me.asakura_kukii.siegecore.argument.PSender;
import me.asakura_kukii.siegecore.io.PFile;
import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.util.format.PFormat;
import me.asakura_kukii.siegefishing.achievement.PAchievementCollectable;
import me.asakura_kukii.siegefishing.argument.command.CommandHandler;
import me.asakura_kukii.siegefishing.argument.tab.TabHandler;
import me.asakura_kukii.siegefishing.bait.PFishBait;
import me.asakura_kukii.siegefishing.bait.PFishBaitBuff;
import me.asakura_kukii.siegefishing.boat.PBoat;
import me.asakura_kukii.siegefishing.boat.PBoatListener;
import me.asakura_kukii.siegefishing.collectable.PFishCollectable;
import me.asakura_kukii.siegefishing.collectable.PInsectCollectable;
import me.asakura_kukii.siegefishing.collectable.PRegionCollectable;
import me.asakura_kukii.siegefishing.collectable.PSeaFoodCollectable;
import me.asakura_kukii.siegefishing.config.PConfig;
import me.asakura_kukii.siegefishing.deco.PDeco;
import me.asakura_kukii.siegefishing.effect.PParticle;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.creature.fish.*;
import me.asakura_kukii.siegefishing.creature.insect.PInsect;
import me.asakura_kukii.siegefishing.creature.insect.PInsectFightSession;
import me.asakura_kukii.siegefishing.creature.insect.PInsectFightSessionListener;
import me.asakura_kukii.siegefishing.creature.insect.PNet;
import me.asakura_kukii.siegefishing.inventory.*;
import me.asakura_kukii.siegefishing.inventory.craft.PFishCraftRecipe;
import me.asakura_kukii.siegefishing.npc.*;
import me.asakura_kukii.siegefishing.map.PFishMap;
import me.asakura_kukii.siegefishing.map.PFishRegion;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import me.asakura_kukii.siegefishing.player.PFishPlayerListener;
import me.asakura_kukii.siegefishing.rod.PFishRod;
import me.asakura_kukii.siegefishing.creature.seafood.PSeaFood;
import me.asakura_kukii.siegefishing.creature.seafood.PShovel;
import me.asakura_kukii.siegefishing.util.ItemDisplayHandler;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Interaction;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SiegeFishing extends JavaPlugin {
    public static String pluginColorCode = "&8";
    public static Server server = null;
    public static String pluginName;
    public static String pluginPrefix;
    public static String consolePluginPrefix;
    public static JavaPlugin pluginInstance = null;
    public static File pluginFolder = null;
    public static HashMap<JavaPlugin, BukkitTask> updaterRegister = new HashMap<>();

    public static void registerEvent() {
        Bukkit.getPluginManager().registerEvents(new PFishPlayerListener(), pluginInstance);
        Bukkit.getPluginManager().registerEvents(new PBoatListener(), pluginInstance);
        Bukkit.getPluginManager().registerEvents(new PNPCListener(), pluginInstance);
        Bukkit.getPluginManager().registerEvents(new PInsectFightSessionListener(), pluginInstance);
        Bukkit.getPluginManager().registerEvents(new SiegeFishingListener(), pluginInstance);
    }

    public static void registerType() {
        PType.putPType(pluginInstance, "particle", PParticle.class);
        PType.putPType(pluginInstance, "sound", PSound.class);
        PType.putPType(pluginInstance, "deco", PDeco.class);
        PType.putPType(pluginInstance, "emotion", PFishEmotion.class);

        PType.putPType(pluginInstance, "boat", PBoat.class);

        PType.putPType(pluginInstance, "cellphone", PCellPhone.class);

        PType.putPType(pluginInstance, "buff", PFishBaitBuff.class);

        PType.putPType(pluginInstance, "shovel", PShovel.class);
        PType.putPType(pluginInstance, "net", PNet.class);

        PType.putPType(pluginInstance, "fish_collectable", PFishCollectable.class);
        PType.putPType(pluginInstance, "seafood_collectable", PSeaFoodCollectable.class);
        PType.putPType(pluginInstance, "insect_collectable", PInsectCollectable.class);
        PType.putPType(pluginInstance, "region_collectable", PRegionCollectable.class);

        PType.putPType(pluginInstance, "fish", PFish.class);
        PType.putPType(pluginInstance, "seafood", PSeaFood.class);
        PType.putPType(pluginInstance, "insect", PInsect.class);
        PType.putPType(pluginInstance, "region", PFishRegion.class);

        PType.putPType(pluginInstance, "insect_fight", PNPCInsectFight.class);
        PType.putPType(pluginInstance, "insect_fight_boy", PNPCInsectFightBoy.class);

        //PType.putPType(pluginInstance, "fishfood", PFishFood.class);
        PType.putPType(pluginInstance, "rod", PFishRod.class);
        PType.putPType(pluginInstance, "bait", PFishBait.class);
        PType.putPType(pluginInstance, "recipe", PFishCraftRecipe.class);

        PType.putPType(pluginInstance, "song", PSong.class);
        PType.putPType(pluginInstance, "album_item", PAlbumItem.class);
        PType.putPType(pluginInstance, "album", PAlbum.class);
        PType.putPType(pluginInstance, "button", PButton.class);
        PType.putPType(pluginInstance, "layout", PLayout.class);

        PType.putPType(pluginInstance, "achievement_collectable", PAchievementCollectable.class);

        PType.putPType(pluginInstance, "shop_npc", PNPCShop.class);
        PType.putPType(pluginInstance, "lottery_npc", PNPCLottery.class);

        PType.putPType(pluginInstance, "map", PFishMap.class);

        PType.putPType(pluginInstance, "config", PConfig.class);

        PType.putPType(pluginInstance, "player", PFishPlayer.class);
    }

    @Override
    public void onEnable() {
        server = getServer();
        pluginName = getName();
        pluginPrefix = PFormat.format("&8[" + pluginColorCode + pluginName + "&8] &f");
        consolePluginPrefix = "[" + pluginName + "]->>";
        SiegeCore.info(consolePluginPrefix, "Enabling " + pluginName);
        pluginInstance = this;
        pluginFolder = getDataFolder();
        if (!pluginFolder.exists() && pluginFolder.mkdirs()) SiegeCore.warn(consolePluginPrefix, "Creating plugin folder [" + pluginName + "]");

        registerEvent();
        registerType();

        updater();

        SiegeCore.info(consolePluginPrefix, pluginName + " enabled");
    }

    public void onDisable() {
        if (PType.getPType(PFishPlayer.class) != null) {
            PType.getPType(PFishPlayer.class).savePType();
        }
        updaterRegister.get(pluginInstance).cancel();
        updaterRegister.remove(pluginInstance);
        ItemDisplayHandler.removeAll();
        PInsectFightSession.activeInsectFightSession.clear();
        SiegeCore.info(consolePluginPrefix, "Disabling " + pluginName);
        SiegeCore.info(consolePluginPrefix, pluginName + " disabled");
    }

    public static void updater() {
        if (updaterRegister.containsKey(pluginInstance)) {
            updaterRegister.get(pluginInstance).cancel();
            updaterRegister.remove(pluginInstance);
        }
        updaterRegister.put(pluginInstance, new BukkitRunnable() {
            @Override
            public void run() {
                if (!SiegeCore.flagEnabled) return;
                for (Interaction i : PInsectFightSession.activeInsectFightSession.keySet()) {
                    PInsectFightSession.activeInsectFightSession.get(i).update();
                }
                for (PFile pF : PType.getPType(PFishMap.class).getPFileList()) {
                    try {
                        ((PFishMap) pF).update();
                    } catch (Exception e) {
                        SiegeCore.warn(e.getLocalizedMessage());
                    }
                }
                for (PFile pF : PType.getPType(PFishPlayer.class).getPFileList()) {
                    try {
                        ((PFishPlayer) pF).update();
                    } catch (Exception e) {
                        SiegeCore.warn(e.getLocalizedMessage());
                    }
                }
            }
        }.runTaskTimer(pluginInstance , 0, 1));
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        List<String> sL = new ArrayList<>();
        if (args.length > 0) {
            PArgument argument = new PArgument(label, args);
            PSender sender = new PSender(pluginName, pluginPrefix, commandSender);
            sL = TabHandler.onTab(sender, argument);
            return sL;
        }
        return sL;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase(pluginName)) {
            PArgument argument = new PArgument(label, args);
            PSender sender = new PSender(pluginName, pluginPrefix, commandSender);
            return CommandHandler.onCommand(sender, argument);
        }
        return true;
    }
}