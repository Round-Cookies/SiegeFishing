package me.asakura_kukii.siegefishing.inventory;

import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.io.PFile;
import me.asakura_kukii.siegecore.io.helper.PFileIdDeserializer;
import me.asakura_kukii.siegecore.io.helper.PFileIdSerializer;
import me.asakura_kukii.siegecore.item.PAbstractItem;
import me.asakura_kukii.siegecore.trigger.PTriggerSlot;
import me.asakura_kukii.siegecore.trigger.PTriggerSubType;
import me.asakura_kukii.siegecore.trigger.PTriggerType;
import me.asakura_kukii.siegefishing.effect.PSound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PAlbum extends PFile {

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSong> songList = new ArrayList<>();

    @JsonSerialize(using = PFileIdSerializer.class)
    @JsonDeserialize(using = PFileIdDeserializer.class)
    public PAlbumItem playItem = null;

    @JsonSerialize(using = PFileIdSerializer.class)
    @JsonDeserialize(using = PFileIdDeserializer.class)
    public PAlbumItem pauseItem = null;

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
