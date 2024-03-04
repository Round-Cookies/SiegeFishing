package me.asakura_kukii.siegefishing.inventory;

import me.asakura_kukii.siegecore.inventory.PAbstractInventory;
import me.asakura_kukii.siegecore.io.PFile;
import me.asakura_kukii.siegecore.io.PType;
import me.asakura_kukii.siegecore.item.PAbstractItem;
import me.asakura_kukii.siegecore.util.math.PVector;
import me.asakura_kukii.siegefishing.config.PConfig;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.player.PFishPlayer;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PFishNetEase extends PAbstractInventory {
    @Override
    public void initializeLayout() {
        PType pT = PType.getPType(PLayout.class);
        if (pT == null) return;
        this.layout = (PLayout) pT.getPFileSafely("fish_netease");
    }

    @Override
    public List<ItemStack> finalizeItemStackList(Player p, List<ItemStack> list) {
        PType pT = PType.getPType(PFishPlayer.class);
        if (pT == null) return list;
        PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
        if (pFP == null) return list;
        PType pT3 = PType.getPType(PAlbum.class);
        if (pT3 == null) return list;
        List<PAlbum> playAlbumList = pFP.albumList;
        List<PFile> totalAlbumList = pT3.getPFileList();
        List<ItemStack> albumItemStackList = new ArrayList<>();
        for (PFile pF : totalAlbumList) {
            if (playAlbumList.contains((PAlbum) pF)) {
                albumItemStackList.add(((PAlbum) pF).playItem.getItemStack());
            } else {
                albumItemStackList.add(((PAlbum) pF).pauseItem.getItemStack());
            }
        }
        return albumItemStackList;
    }

    @Override
    public void command(Player p, String s, ItemStack iS) {
        PType pT2 = PType.getPType(PConfig.class);
        if (pT2 == null) return;
        PConfig pC = (PConfig) pT2.getPFileSafely("config");
        if (pC == null) return;
        if (s.equals("next_page")) {
            for (PSound pS : pC.clickSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            this.nextPage(p);
        }
        if (s.equals("last_page")) {
            for (PSound pS : pC.clickSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            this.lastPage(p);
        }
        if (s.equals("close")) {
            for (PSound pS : pC.clickSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            this.close(p);
        }
        PType pT = PType.getPType(PFishPlayer.class);
        if (pT == null) return;
        PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
        if (pFP == null) return;
        if (s.equals("playpause")) {
            if (pFP.autoPlay) {
                p.stopSound(SoundCategory.RECORDS);
                pFP.autoPlay = false;
                pFP.currentSongTickTime = 0;
                pFP.currentSongLength = 0;
            } else {
                pFP.autoPlay = true;
            }
        }
        if (s.equals("next")) {
            if (pFP.autoPlay) {
                p.stopSound(SoundCategory.RECORDS);
                pFP.currentSongTickTime = 0;
                pFP.currentSongLength = 0;
            } else {
                pFP.autoPlay = true;
            }
        }
        if (s.equals("return")) {
            pFP.fishMenu.open(p);
            for (PSound pS : pC.bookSound) pS.play(p, PVector.fromLocation(p.getLocation()));
            return;
        }
    }

    public PFile getPFileFromString(String s) {
        if (!s.contains(".") && s.split("\\.").length != 3) return null;
        String typeId = s.split("\\.")[0] + "." + s.split("\\.")[1];
        String fileId = s.split("\\.")[2];
        PType type = PType.getPType(typeId);
        if (type == null) return null;
        if (type.getPFile(fileId) != null) {
            return type.getPFile(fileId);
        }
        return null;
    }

    @Override
    public void trigger(Player p, int i, ItemStack iS) {
        PFile pAI = PAbstractItem.getPItem(iS);
        if (pAI == null) return;
        if (!(pAI instanceof PAbstractItem)) return;
        if (!(pAI instanceof PAlbumItem)) return;
        PAlbumItem albumItem = (PAlbumItem) pAI;
        String path = albumItem.album;
        PFile pF = getPFileFromString(path);
        if (pF == null) return;
        if (!(pF instanceof PAlbum)) return;
        PType pT2 = PType.getPType(PAlbum.class);
        if (pT2 == null) return;
        PType pT3 = PType.getPType(PConfig.class);
        if (pT3 == null) return;
        PConfig pC = (PConfig) pT3.getPFileSafely("config");
        if (pC == null) return;
        PAlbum pA = (PAlbum) pF;
        PType pT = PType.getPType(PFishPlayer.class);
        if (pT == null) return;
        PFishPlayer pFP = (PFishPlayer) pT.getPFileSafely(p.getUniqueId().toString());
        if (pFP == null) return;
        if (pFP.albumList.contains(pA)) {
            pFP.albumList.remove(pA);
        } else {
            pFP.albumList.add(pA);
        }

        if (!pFP.albumList.isEmpty()) {
            List<PSong> songList = new ArrayList<>();
            for (PAlbum pAlbum : pFP.albumList) {
                songList.addAll(pAlbum.songList);
            }
            if (pFP.albumList.size() == pT2.getPFileList().size()) {
                songList.addAll(pC.hiddenSongList);
            }
            Collections.shuffle(songList);
            pFP.songList = songList;
        }
        save();
        load(p);
    }
}
