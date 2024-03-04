package me.asakura_kukii.siegefishing.player;

import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.io.helper.*;
import me.asakura_kukii.siegecore.item.PAbstractItem;
import me.asakura_kukii.siegecore.player.PAbstractPlayer;
import me.asakura_kukii.siegecore.util.format.PFormat;
import me.asakura_kukii.siegecore.util.math.PAxis;
import me.asakura_kukii.siegecore.util.math.PMath;
import me.asakura_kukii.siegecore.util.math.PQuaternion;
import me.asakura_kukii.siegecore.util.math.PVector;
import me.asakura_kukii.siegefishing.achievement.PAchievementCollectable;
import me.asakura_kukii.siegefishing.argument.command.NMSHandler;
import me.asakura_kukii.siegefishing.bait.PFishBait;
import me.asakura_kukii.siegefishing.boat.PBoat;
import me.asakura_kukii.siegefishing.collectable.PInsectCollectable;
import me.asakura_kukii.siegefishing.collectable.PFishCollectable;
import me.asakura_kukii.siegefishing.collectable.PSeaFoodCollectable;
import me.asakura_kukii.siegefishing.config.PConfig;
import me.asakura_kukii.siegefishing.creature.fish.*;
import me.asakura_kukii.siegefishing.creature.insect.PInsect;
import me.asakura_kukii.siegefishing.creature.seafood.PShovel;
import me.asakura_kukii.siegefishing.effect.PParticle;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.inventory.*;
import me.asakura_kukii.siegefishing.inventory.book.*;
import me.asakura_kukii.siegefishing.inventory.craft.PFishCraft;
import me.asakura_kukii.siegefishing.map.PFishChunk;
import me.asakura_kukii.siegefishing.map.PFishMap;
import me.asakura_kukii.siegefishing.map.PFishRegion;
import me.asakura_kukii.siegefishing.creature.seafood.PSeaFood;
import me.asakura_kukii.siegefishing.npc.PNPCInsectFight;
import me.asakura_kukii.siegefishing.rod.PFishRod;
import me.asakura_kukii.siegefishing.util.ItemDisplayHandler;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PFishPlayer extends PAbstractPlayer {

    @JsonSerialize(using = ItemStackSerializer.class)
    @JsonDeserialize(using = ItemStackDeserializer.class)
    public ItemStack baitItemStack = null;

    @JsonSerialize(using = PFileIdSerializer.class)
    @JsonDeserialize(using = PFileIdDeserializer.class)
    public PFishBait bait = null;

    public int fishCount = 0;

    public int seaFoodCount = 0;

    public int insectCount = 0;

    @JsonSerialize(keyUsing = PFileIdKeySerializer.class)
    @JsonDeserialize(keyUsing = PFileIdKeyDeserializer.class)
    public HashMap<PFishCollectable, Integer> fishCountCounter = new HashMap<>();

    @JsonSerialize(keyUsing = PFileIdKeySerializer.class)
    @JsonDeserialize(keyUsing = PFileIdKeyDeserializer.class)
    public HashMap<PSeaFoodCollectable, Integer> seaFoodCountCounter = new HashMap<>();

    @JsonSerialize(keyUsing = PFileIdKeySerializer.class)
    @JsonDeserialize(keyUsing = PFileIdKeyDeserializer.class)
    public HashMap<PInsectCollectable, Integer> insectCountCounter = new HashMap<>();

    @JsonSerialize(keyUsing = PFileIdKeySerializer.class)
    @JsonDeserialize(keyUsing = PFileIdKeyDeserializer.class)
    public HashMap<PFishCollectable, Float> fishWeightCounter = new HashMap<>();

    @JsonSerialize(keyUsing = PFileIdKeySerializer.class)
    @JsonDeserialize(keyUsing = PFileIdKeyDeserializer.class)
    public HashMap<PInsectCollectable, Integer> insectStarCountCounter = new HashMap<>();

    @JsonSerialize(keyUsing = PFileIdKeySerializer.class, contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(keyUsing = PFileIdKeyDeserializer.class, contentUsing = PFileIdDeserializer.class)
    public HashMap<PFishCollectable, PFishRegion> fishRegionCounter = new HashMap<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public Set<PFishRegion> regionCounter = new HashSet<>();

    public Set<String> bucketCounter = new HashSet<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public Set<PBoat> boatCounter = new HashSet<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public Set<PFishRod> rodCounter = new HashSet<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public Set<PNPCInsectFight> insectNPCFightSuccessCounter = new HashSet<>();

    public int insectFightPlayerSuccessCount = 0;

    public int acquireCount = 0;

    public float acquireTrashMaxPrice = 0;

    public int lotteryCount = 0;

    public int continuousFishSuccessCount = 0;

    public int continuousFishFailureCount = 0;

    public int continuousFishMixSuccessCount = 0;

    public int craftCount = 0;

    public int lotteryLevel4Count = 0;

    public int lotteryLevel5Count = 0;

    public int skillFailureCount = 0;

    public double meter = 0;

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public Set<PAchievementCollectable> achievementCounter = new HashSet<>();

    public float balance = 0.0F;

    public PFishPlayerLevel level = PFishPlayerLevel.BEGINNER;

    public float experience = 0.0F;

    @JsonSerialize(using = PFileIdSerializer.class)
    @JsonDeserialize(using = PFileIdDeserializer.class)
    public PFishRegion region = null;

    public PFishMenu fishMenu = new PFishMenu();

    public PFishBook fishBook = new PFishBook();

    public PSeaFoodBook seaFoodBook = new PSeaFoodBook();

    public PInsectBook insectBook = new PInsectBook();

    public PAchievementBook achievementBook = new PAchievementBook();

    public PRankBook rankBook = new PRankBook();

    public PFishBag fishBag = new PFishBag();

    public PFishCraft craft = new PFishCraft();

    public PFishAcquire acquire = new PFishAcquire();

    public PFishLottery fishLottery = new PFishLottery();

    public PFishBeginnerShop fishBeginnerShop = new PFishBeginnerShop();

    public PFishToolShop fishToolShop = new PFishToolShop();

    public PFishRodShop fishRodShop = new PFishRodShop();

    public PFishNetEase fishNetEase = new PFishNetEase();

    public int lotteryCounterForLevel4 = 0;

    public int lotteryCounterForLevel5 = 0;

    public transient PFishSession session = null;

    public transient PVector location = new PVector();

    public transient PVector velocity = new PVector();

    public transient ItemDisplay boatEntity = null;

    public transient boolean autoPlay = true;

    public transient int currentSongTickTime = 0;

    public transient int currentSongLength = 0;

    public transient List<PAlbum> albumList = new ArrayList<>();

    public transient List<PSong> songList = new ArrayList<>();

    public transient int songIndex = 0;

    public void update() {
        if (session != null) {
            session.update();
            if (session.getStatus() == PFishSessionStatus.NULL) session = null;
        }
        Player p = getPlayer();
        if (p == null) return;
        p.setHealth(20F);
        p.setSaturation(20F);
        updateLevel(p);
        updateRegion(p);
        updateVelocity(p);
        updateBoat(p);
        updateNetEase(p);
        checkBalanceAchievement();
        checkAchievementAchievement();
    }

    public void checkAchievementAchievement() {
        PType pT = PType.getPType(PAchievementCollectable.class);
        if (pT == null) return;
        if (this.achievementCounter.size() >= 20) {
            collectAchievement("achievement_20");
        }
        if (this.achievementCounter.size() >= 40) {
            collectAchievement("achievement_40");
        }
        if (this.achievementCounter.size() >= 60) {
            collectAchievement("achievement_60");
        }
        if (this.achievementCounter.size() >= 80) {
            collectAchievement("achievement_80");
        }
        if (this.achievementCounter.size() == pT.getPFileList().size() - 1) {
            collectAchievement("achievement_all");
        }
    }

    public void updateNetEase(Player p) {
        if (currentSongTickTime < currentSongLength) {
            currentSongTickTime++;
        } else {
            // switch
            if (!autoPlay) return;
            if (this.albumList.isEmpty() || this.songList.isEmpty()) {
                autoPlay = false;
                this.songIndex = 0;
                this.songList = new ArrayList<>();
                this.currentSongLength = 0;
                this.currentSongTickTime = 0;
                return;
            }
            this.songIndex++;
            if (songIndex >= this.songList.size()) {
                songIndex = 0;
            }
            PSong pS = this.songList.get(this.songIndex);
            if (pS == null) return;
            for (PSound pSound : pS.soundList) pSound.play(p, getLocation(), SoundCategory.RECORDS);
            PType pT2 = PType.getPType(PConfig.class);
            if (pT2 == null) return;
            PConfig pC = (PConfig) pT2.getPFileSafely("config");
            if (pC == null) return;
            HashMap<String, String> replace = new HashMap<>();
            replace.put("%songName%", pS.songName);
            replace.put("%artist%", pS.artist);
            this.currentSongLength = pS.length;
            this.currentSongTickTime = 0;
            this.sendLog(pC.songLogFormat, replace);
        }
    }

    public void updateLevel(Player p) {
        if (this.experience > this.level.experience) {
            switch (this.level) {
                case BEGINNER:
                    this.level = PFishPlayerLevel.AMATEUR;
                    this.experience = 0;
                    break;
                case AMATEUR:
                    this.level = PFishPlayerLevel.VETERAN;
                    this.experience = 0;
                    break;
                case VETERAN:
                    this.level = PFishPlayerLevel.EXPERT;
                    this.experience = 0;
                    break;
                case EXPERT:
                    this.level = PFishPlayerLevel.LEGENDARY;
                    break;
                case LEGENDARY:
                    this.experience = this.level.experience;
                    break;
            }
            collectLevel();
        }
        p.setLevel(this.level.level + 1);
        p.setExp(this.experience / this.level.experience);
    }

    public void updateRegion(Player p) {
        PType pT = PType.getPType(PFishMap.class);
        if (pT == null) return;
        PFishMap pFM = (PFishMap) pT.getPFile(p.getWorld().getName());
        if (pFM != null) {
            PFishChunk pFC = pFM.getFishChunk(getLocation());
            if (pFC == null || pFC.region == null) return;
            if (!this.regionCounter.contains(pFC.region)) {
                collectRegion(pFC);
            }
            this.region = pFC.region;
        }
    }

    public void updateVelocity(Player p) {
        velocity.x = (float) p.getLocation().getX() - location.x;
        velocity.y = (float) p.getLocation().getY() - location.y;
        velocity.z = (float) p.getLocation().getZ() - location.z;
        location.x = (float) p.getLocation().getX();
        location.y = (float) p.getLocation().getY();
        location.z = (float) p.getLocation().getZ();
        PVector planarVelocity = velocity.clone();
        planarVelocity.y = 0;
        float l = velocity.clone().length();
        if (l < 2) this.meter = this.meter + l;
        checkMeterAchievement();
    }

    public void updateBoat(Player p) {
        if (PType.getPType(PConfig.class) == null) return;
        PConfig pC = (PConfig) PType.getPType(PConfig.class).getPFileSafely("config");
        PAxis pA = getAxis();
        if (pA == null) return;
        Entity v = p.getVehicle();
        float yaw = p.getLocation().getYaw() / 180 * PMath.pi;
        if (v != null) yaw = v.getLocation().getYaw() / 180 * PMath.pi;
        for (Entity e : p.getPassengers()) {
            if (!(e instanceof ItemDisplay)) continue;
            ItemDisplay iD = (ItemDisplay) e;
            PQuaternion rotation = new PVector(0, 0, 1).rotationToExceptZ(new PVector(-PMath.sin(yaw), 0.0F, PMath.cos(yaw)));
            ItemDisplayHandler.update(iD, (PVector) pC.boatBias.clone().rotate(rotation), rotation, new PVector(pC.boatScale, pC.boatScale, pC.boatScale), 1, false);
        }
    }

    public void sendLog(List<String> hintList) {
        if (getPlayer() == null) return;
        if (hintList.isEmpty()) return;
        String hint = hintList.get(PMath.ranIndex(hintList.size()));
        hint = PFormat.format("&7> " + hint.replaceAll("\n", "\n&7> "));
        getPlayer().sendMessage(hint);
    }

    public void sendLog(List<String> hintList, HashMap<String, String> replaceMap) {
        if (getPlayer() == null) return;
        if (hintList.isEmpty()) return;
        String hint = hintList.get(PMath.ranIndex(hintList.size()));
        for (String key : replaceMap.keySet()) {
            hint = hint.replaceAll(key, replaceMap.get(key) + "&7");
        }
        hint = PFormat.format("&7> " + hint.replaceAll("\n", "\n&7> "));
        getPlayer().sendMessage(hint);
    }

    public void sendMessageWithItem(String prefix, ItemStack iS, boolean flagBroadcast) {
        Player p = getPlayer();
        if (iS == null) return;
        if (p == null) return;
        try {
            prefix = prefix.replaceAll("%player%", p.getName());
            prefix = PFormat.format("&7> " + prefix.replaceAll("\n", "\n&7> "));
            String[] componentArray = prefix.split("%item%");

            ComponentBuilder componentBuilder = new ComponentBuilder().append(new TextComponent(PFormat.format(componentArray[0])));
            for (int index = 1; index < componentArray.length; index++) {
                ItemMeta iM = iS.getItemMeta();
                if (iM == null) return;
                String itemStackName = "";
                if (iM.getDisplayName().equals("")) {
                    return;
                } else {
                    itemStackName = ChatColor.getLastColors(iM.getDisplayName()) + ChatColor.stripColor(iM.getDisplayName());
                }
                BaseComponent itemStackNameComponent = new TextComponent(itemStackName);
                BaseComponent[] itemStackComponent = new BaseComponent[]{new TextComponent(NMSHandler.itemToJson(iS))};
                itemStackNameComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, itemStackComponent));
                componentBuilder.append(itemStackNameComponent);
                componentBuilder.append(new TextComponent(PFormat.format(componentArray[index])));
            }
            if (flagBroadcast) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.spigot().sendMessage(componentBuilder.create());
                }
            } else {
                p.spigot().sendMessage(componentBuilder.create());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendHint(List<String> hintList) {
        if (getPlayer() == null) return;
        if (hintList.isEmpty()) return;
        String hint = hintList.get(PMath.ranIndex(hintList.size()));
        TextComponent text = new TextComponent(hint);
        getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, text);
    }

    public void sendHint(String hint) {
        if (getPlayer() == null) return;
        if (hint.equals("")) return;
        TextComponent text = new TextComponent(hint);
        getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, text);
    }

    public void collectAndCheckInsectFight(boolean flagNPCFight, PNPCInsectFight insectFightNPC) {
        if (flagNPCFight && insectFightNPC != null) {
            this.insectNPCFightSuccessCounter.add(insectFightNPC);
        } else {
            this.insectFightPlayerSuccessCount++;
        }
        checkInsectFightAchievement();
    }

    public void checkMeterAchievement() {
        if (this.meter >= 42195) {
            collectAchievement("meter_42195");
        }
    }

    public void collectCraft() {
        this.craftCount++;
        checkCraftCountAchievement();
    }

    public void checkCraftCountAchievement() {
        if (this.craftCount >= 1) {
            collectAchievement("craft_1");
        }
        if (this.craftCount >= 100) {
            collectAchievement("craft_100");
        }
    }

    public void checkInsectFightAchievement() {
        int count = this.insectNPCFightSuccessCounter.size();
        if (count >= 2) {
            collectAchievement("insect_npc_2");
        }
        if (count >= 4) {
            collectAchievement("insect_npc_4");
        }
        if (count >= 6) {
            collectAchievement("insect_npc_6");
        }
        if (count >= 8) {
            collectAchievement("insect_npc_8");
        }
        if (count >= 10) {
            collectAchievement("insect_npc_10");
        }
        if (this.insectFightPlayerSuccessCount >= 1) {
            collectAchievement("insect_player_1");
        }
    }

    public void collectSkillFailure() {
        this.skillFailureCount++;
        checkSkillFailureAchievement();
    }

    public void collectItem(PAbstractItem pAI) {
        if (pAI.type == PType.getPType(PFishRod.class)) {
            switch (pAI.id) {
                case "rod1":
                    collectAchievement("rod1");
                    break;
                case "rod2":
                    collectAchievement("rod2");
                    break;
                case "rod3":
                    collectAchievement("rod3");
                    break;
                case "rod4":
                    collectAchievement("rod4");
                    break;
                case "rod5":
                    collectAchievement("rod5");
                    break;
                case "rod6":
                case "rod7":
                case "rod8":
                case "rod9":
                case "rod10":
                    collectAchievement("rod_rider");
                    break;
                case "rod11":
                case "rod12":
                case "rod13":
                case "rod14":
                case "rod15":
                    collectAchievement("rod_fuze");
                    break;
                case "rod16":
                case "rod17":
                case "rod18":
                case "rod19":
                case "rod20":
                    collectAchievement("rod_carp");
                    break;
                case "rod21":
                case "rod22":
                case "rod23":
                case "rod24":
                case "rod25":
                    collectAchievement("rod_eat");
                    break;
                case "rod26":
                case "rod27":
                case "rod28":
                case "rod29":
                case "rod30":
                    collectAchievement("rod_sword");
                    break;
                case "rod31":
                case "rod32":
                case "rod33":
                case "rod34":
                case "rod35":
                    collectAchievement("rod_lighthouse");
                    break;
                case "rod36":
                case "rod37":
                case "rod38":
                case "rod39":
                case "rod40":
                    collectAchievement("rod_special");
                    break;
            }
            this.rodCounter.add(((PFishRod) pAI));
        }
        if (pAI.type == PType.getPType(PShovel.class)) {
            collectAchievement("shovel");
        }
        if (pAI.type == PType.getPType(PBoat.class)) {
            this.boatCounter.add(((PBoat) pAI));
        }
        checkRodAchievement();
        checkBoatAchievement();
    }

    public void collectAcquire(float trashPrice) {
        this.acquireCount++;
        if (trashPrice > this.acquireTrashMaxPrice) {
            this.acquireTrashMaxPrice = trashPrice;
        }
        checkAcquireAchievement();
    }

    public void collectLottery(boolean flagLevel4, boolean flagLevel5) {
        this.lotteryCount++;
        if (flagLevel4) {
            this.lotteryLevel4Count++;
        }
        if (flagLevel5) {
            this.lotteryLevel5Count++;
        }
        checkLotteryAchievement();
    }

    public void collectExperience(float experience) {
        this.experience = this.experience + experience;
    }

    // get fish
    public void collectFishFromSessionSuccess(PFishMap pFishMap, PFishChunk pFishChunk, PFishRod rod, PFish fish, PFishBait bait, float weight) {
        if (pFishMap == null || pFishChunk == null || pFishChunk.region == null) return;
        this.continuousFishSuccessCount++;
        this.continuousFishFailureCount = 0;
        if (bait != null) {
            if (bait.level == PFishLevel.ALL) {
                this.continuousFishMixSuccessCount++;
            }
        }
        this.fishCount++;
        if (fish.collectable == null) return;
        PFishCollectable pFC = fish.collectable;
        if (fishCountCounter.containsKey(pFC)) fishCountCounter.put(pFC, fishCountCounter.get(pFC) + 1);
        if (!fishCountCounter.containsKey(pFC)) fishCountCounter.put(pFC, 1);
        if (fishWeightCounter.containsKey(pFC) && fishWeightCounter.get(pFC) < weight) fishWeightCounter.put(pFC, weight);
        if (!fishWeightCounter.containsKey(pFC)) fishWeightCounter.put(pFC, weight);
        fishRegionCounter.put(pFC, pFishChunk.region);
        checkFishAchievement();
        checkFishSuccessAchievement(rod, weight, fish);
        checkBookAchievement();
    }

    public void collectFishFromSessionFailure(PFishMap pFishMap, PFishChunk pFishChunk, PFish fish, float weight) {
        this.continuousFishSuccessCount = 0;
        this.continuousFishMixSuccessCount = 0;
        this.continuousFishFailureCount++;
        checkFishAchievement();
    }

    // get seafood
    public void collectSeaFoodFromSession(PSeaFood seaFood) {
        if (seaFood.collectable == null) return;
        PSeaFoodCollectable pSFC = seaFood.collectable;
        this.seaFoodCount++;
        if (seaFoodCountCounter.containsKey(pSFC)) seaFoodCountCounter.put(pSFC, seaFoodCountCounter.get(pSFC) + 1);
        if (!seaFoodCountCounter.containsKey(pSFC)) seaFoodCountCounter.put(pSFC, 1);
        checkSeaFoodAchievement();
        checkBookAchievement();
    }

    // get insect & insect fight
    public void collectInsectFromSession(PInsect insect, int starCount, boolean updateCount) {
        if (insect.collectable == null) return;
        PInsectCollectable pIC = insect.collectable;
        if (updateCount) {
            this.insectCount++;
            if (insectCountCounter.containsKey(pIC)) insectCountCounter.put(pIC, insectCountCounter.get(pIC) + 1);
            if (!insectCountCounter.containsKey(pIC)) insectCountCounter.put(pIC, 1);
        }
        if (insectStarCountCounter.containsKey(pIC) && insectStarCountCounter.get(pIC) < starCount) insectStarCountCounter.put(pIC, starCount);
        if (!insectStarCountCounter.containsKey(pIC)) insectStarCountCounter.put(pIC, starCount);
        checkInsectAchievement();
        checkBookAchievement();
    }

    public boolean collectBucket(Block b) {
        String key = b.getLocation().getX() + "_" + b.getLocation().getY() + "_" + b.getLocation().getZ();
        if (this.bucketCounter.contains(key)) return false;
        this.bucketCounter.add(key);
        checkBucketAchievement();
        if (PType.getPType(PConfig.class) == null) return true;
        PConfig pC = (PConfig) PType.getPType(PConfig.class).getPFileSafely("config");
        this.sendLog(pC.bucketHint);
        for (PParticle pP : pC.bucketParticle) pP.spawn(getPlayer(), (PVector) PVector.fromLocation(b.getLocation()).add(0.5F, 0.5F, 0.5F));
        for (PSound pS : pC.bucketSound) pS.play(getPlayer(), (PVector) PVector.fromLocation(b.getLocation()).add(0.5F, 0.5F, 0.5F));
        this.experience = this.experience + pC.bucketExperience;
        return true;
    }

    public void collectLevel() {
        checkLevelAchievement();
    }

    // region
    public void collectRegion(PFishChunk pFishChunk) {
        if (pFishChunk == null || pFishChunk.region == null) return;
        this.regionCounter.add(pFishChunk.region);
        checkRegionAchievement(pFishChunk.region);
    }

    public void checkAcquireAchievement() {
        if (this.acquireCount >= 1) {
            collectAchievement("acquire_1");
        }
        if (this.acquireTrashMaxPrice >= 500) {
            collectAchievement("acquire_trash_500");
        }
    }

    public void checkSkillFailureAchievement() {
        if (this.skillFailureCount >= 1) {
            collectAchievement("skill_bad_1");
        }
    }

    public void checkBalanceAchievement() {
        if (this.balance >= 1000) {
            collectAchievement("balance_1000");
        }
        if (this.balance >= 10000) {
            collectAchievement("balance_10000");
        }
    }

    public void checkLotteryAchievement() {
        if (this.lotteryCount >= 1) {
            collectAchievement("lottery_1");
        }
        if (this.lotteryCount >= 50) {
            collectAchievement("lottery_50");
        }
        if (this.lotteryCount >= 200) {
            collectAchievement("lottery_200");
        }
        if (this.lotteryLevel4Count >= 1) {
            collectAchievement("lottery_level_4");
        }
        if (this.lotteryLevel5Count >= 1) {
            collectAchievement("lottery_level_5");
        }
    }

    public void checkBookAchievement() {
        PType fishType = PType.getPType(PFishCollectable.class);
        PType seaFoodType = PType.getPType(PSeaFoodCollectable.class);
        PType insectType = PType.getPType(PInsectCollectable.class);
        if (fishType == null || seaFoodType == null || insectType == null) return;
        if (this.fishCountCounter.keySet().size() != fishType.getPFileList().size()) return;
        if (this.seaFoodCountCounter.keySet().size() != seaFoodType.getPFileList().size()) return;
        if (this.insectCountCounter.keySet().size() != insectType.getPFileList().size()) return;
        collectAchievement("book_complete");
    }

    public void checkRodAchievement() {
        int count = this.rodCounter.size();
        if (count >= 10) {
            collectAchievement("rod_type_10");
        }
        if (count >= 20) {
            collectAchievement("rod_type_20");
        }
    }

    public void checkBoatAchievement() {
        PType boatType = PType.getPType(PBoat.class);
        if (boatType == null) return;
        int count = this.boatCounter.size();
        if (count >= 1) {
            collectAchievement("boat_type_1");
        }
        if (count >= 5) {
            collectAchievement("boat_type_5");
        }
        if (count == boatType.getPFileList().size()) {
            collectAchievement("boat_type_all");
        }
    }

    public void checkRegionAchievement(PFishRegion pFR) {
        if (this.regionCounter.size() >= 1) {
            collectAchievement("region_1");
        }
        switch (pFR.id) {
            case "lake_5":
                collectAchievement("region_xiangqing");
                break;
            case "santanshequ":
                collectAchievement("region_santan");
                break;
            case "houtancun":
                collectAchievement("region_houtan");
                break;
            case "niangnianggong":
                collectAchievement("region_niangnianggong");
                break;
            case "xiangqingxinhaota":
                collectAchievement("region_xiangqingxinhaota");
                break;
            case "sanhetanxinhaota":
                collectAchievement("region_sanhetanxinhaota");
                break;
            case "fengche":
                collectAchievement("region_fengche");
                break;
            case "lake_4":
                collectAchievement("region_huangjinshu");
                break;
            case "liangting":
                collectAchievement("region_liangting");
                break;
            case "foxiang":
                collectAchievement("region_foxiang");
                break;
        }
    }

    public void checkLevelAchievement() {
        if (this.level.level >= 1) {
            collectAchievement("level_2");
        }
        if (this.level.level >= 2) {
            collectAchievement("level_3");
        }
        if (this.level.level >= 3) {
            collectAchievement("level_4");
        }
        if (this.level.level >= 4) {
            collectAchievement("level_5");
        }
    }

    public void checkBucketAchievement() {
        int bucketCount = this.bucketCounter.size();
        if (bucketCount >= 25) {
            collectAchievement("bucket_count_25");
        }
        if (bucketCount >= 20) {
            collectAchievement("bucket_count_20");
        }
        if (bucketCount >= 15) {
            collectAchievement("bucket_count_15");
        }
        if (bucketCount >= 10) {
            collectAchievement("bucket_count_10");
        }
        if (bucketCount >= 5) {
            collectAchievement("bucket_count_5");
        }
        if (bucketCount >= 1) {
            collectAchievement("bucket_count_1");
        }
    }

    public void checkFishSuccessAchievement(PFishRod rod, float weight, PFish fish) {
        if (weight >= 80) {
            collectAchievement("fish_weight_80");
        }
        if (weight >= 160 && fish.level == PFishLevel.MEAT) {
            collectAchievement("fish_weight_meat_160");
        }
        if (rod != null && rod.id.equals("rod36")) {
            collectAchievement("fish_rod36");
        }
        if (rod != null && rod.id.equals("rod37")) {
            collectAchievement("fish_rod37");
        }
        if (rod != null && rod.id.equals("rod38")) {
            collectAchievement("fish_rod38");
        }
        if (rod != null && rod.id.equals("rod39")) {
            collectAchievement("fish_rod39");
        }
        if (rod != null && rod.id.equals("rod40")) {
            collectAchievement("fish_rod40");
        }
    }

    public void checkFishAchievement() {
        PType fishType = PType.getPType(PFishCollectable.class);
        if (fishType == null) return;
        if (continuousFishSuccessCount >= 10) {
            collectAchievement("fish_success_10");
        }
        if (continuousFishFailureCount >= 10) {
            collectAchievement("fish_failure_10");
        }
        if (continuousFishMixSuccessCount >= 10) {
            collectAchievement("fish_mix_10");
        }
        if (fishCount >= 200) {
            collectAchievement("fish_count_200");
        }
        if (fishCount >= 100) {
            collectAchievement("fish_count_100");
        }
        if (fishCount >= 50) {
            collectAchievement("fish_count_50");
        }
        if (fishCount >= 10) {
            collectAchievement("fish_count_10");
        }
        if (fishCount >= 1) {
            collectAchievement("fish_count_1");
        }
        if (fishCountCounter.keySet().size() == fishType.getPFileList().size()) {
            collectAchievement("fish_type_all");
        }
        if (fishCountCounter.keySet().size() >= 30) {
            collectAchievement("fish_type_30");
        }
        if (fishCountCounter.keySet().size() >= 25) {
            collectAchievement("fish_type_25");
        }
        if (fishCountCounter.keySet().size() >= 20) {
            collectAchievement("fish_type_20");
        }
        if (fishCountCounter.keySet().size() >= 15) {
            collectAchievement("fish_type_15");
        }
        if (fishCountCounter.keySet().size() >= 10) {
            collectAchievement("fish_type_10");
        }
        if (fishCountCounter.keySet().size() >= 5) {
            collectAchievement("fish_type_5");
        }
    }

    public void checkSeaFoodAchievement() {
        PType type = PType.getPType(PSeaFoodCollectable.class);
        if (type == null) return;
        if (seaFoodCount >= 100) {
            collectAchievement("seafood_count_100");
        }
        if (seaFoodCount >= 50) {
            collectAchievement("seafood_count_50");
        }
        if (seaFoodCount >= 10) {
            collectAchievement("seafood_count_10");
        }
        if (seaFoodCount >= 1) {
            collectAchievement("seafood_count_1");
        }
        if (seaFoodCountCounter.keySet().size() == type.getPFileList().size()) {
            collectAchievement("seafood_type_all");
        }
        if (seaFoodCountCounter.keySet().size() >= 15) {
            collectAchievement("seafood_type_15");
        }
        if (seaFoodCountCounter.keySet().size() >= 10) {
            collectAchievement("seafood_type_10");
        }
        if (seaFoodCountCounter.keySet().size() >= 5) {
            collectAchievement("seafood_type_5");
        }
    }

    public void checkInsectAchievement() {
        PType type = PType.getPType(PInsectCollectable.class);
        if (type == null) return;
        if (insectCount >= 30) {
            collectAchievement("insect_count_30");
        }
        if (insectCount >= 10) {
            collectAchievement("insect_count_10");
        }
        if (insectCount >= 1) {
            collectAchievement("insect_count_1");
        }
        if (insectCountCounter.keySet().size() ==  type.getPFileList().size()) {
            collectAchievement("insect_type_all");
        }
        if (insectCountCounter.keySet().size() >= 10) {
            collectAchievement("insect_type_10");
        }
        if (insectCountCounter.keySet().size() >= 5) {
            collectAchievement("insect_type_5");
        }
    }

    public void purgeAchievement() {
        balance = 0;
        level = PFishPlayerLevel.BEGINNER;
        experience = 0;
        fishCount = 0;
        seaFoodCount = 0;
        insectCount = 0;
        fishCountCounter.clear();
        seaFoodCountCounter.clear();
        insectCountCounter.clear();
        fishWeightCounter.clear();
        insectStarCountCounter.clear();
        fishRegionCounter.clear();
        regionCounter.clear();
        bucketCounter.clear();
        boatCounter.clear();
        rodCounter.clear();
        insectNPCFightSuccessCounter.clear();
        insectFightPlayerSuccessCount = 0;
        acquireCount = 0;
        acquireTrashMaxPrice = 0;
        lotteryCount = 0;
        continuousFishSuccessCount = 0;
        continuousFishFailureCount = 0;
        continuousFishMixSuccessCount = 0;
        craftCount = 0;
        lotteryLevel4Count = 0;
        lotteryLevel5Count = 0;
        skillFailureCount = 0;
        meter = 0;
        achievementCounter.clear();
    }

    public void collectAchievement(String id) {
        Player p = getPlayer();
        if (p == null) return;
        PType pT2 = PType.getPType(PConfig.class);
        if (pT2 == null) return;
        PConfig pC = (PConfig) pT2.getPFileSafely("config");
        if (pC == null) return;
        PType pT = PType.getPType(PAchievementCollectable.class);
        PAchievementCollectable pA = (PAchievementCollectable) pT.getPFileSafely(id);
        if (pA == null) return;
        if (!this.achievementCounter.contains(pA)) {
            this.achievementCounter.add(pA);

            for (PParticle pP : pC.achievementParticle) pP.spawn(p, getLocation());
            for (PSound pS : pC.achievementSound) pS.play(p, getLocation());

            this.sendMessageWithItem(pC.achievementItemBroadcastFormat.replaceAll("%player%", p.getName()), pA.getItemStackForBook(getPlayer()), true);

            for (PAbstractItem pAI : pA.awardMap.keySet()) {
                ItemStack iS = pAI.getItemStack(pA.awardMap.get(pAI));
                if (iS == null) continue;
                this.sendMessageWithItem(pC.achievementItemLogFormat.replaceAll("%player%", p.getName()), iS, false);
                this.giveItemStack(iS);
            }
            if (pA.awardIS != null) {
                this.sendMessageWithItem(pC.achievementItemLogFormat.replaceAll("%player%", p.getName()), pA.awardIS, false);
                this.giveItemStack(pA.awardIS);
            }
            this.balance = this.balance + pA.money;
        }
    }

    @Override
    public void finalizeDeserialization() {

    }

    @Override
    public void defaultValue() {

    }

    @Override
    public void load() {

    }

    @Override
    public void unload() {
        this.type.savePFile(this);
    }
}
