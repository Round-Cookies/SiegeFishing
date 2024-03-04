package me.asakura_kukii.siegefishing.argument.command;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.Format;
import java.util.Random;

public class NMSHandler {
    public static String itemToJson(ItemStack itemStack) throws RuntimeException {
        CompoundTag nmsNbtTagCompoundObj;
        net.minecraft.world.item.ItemStack nmsItemStackObj;
        CompoundTag itemAsJsonObject;
        try {
            nmsNbtTagCompoundObj = new CompoundTag();
            nmsItemStackObj = CraftItemStack.asNMSCopy(itemStack);
            itemAsJsonObject = nmsItemStackObj.save(nmsNbtTagCompoundObj);
        } catch (Throwable t) {
            throw new RuntimeException("failed to serialize itemstack to nms item", t);
        }
        return itemAsJsonObject.toString();
    }
}
