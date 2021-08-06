package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;
import com.mojang.datafixers.util.Pair;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PacketPlayOutEntityEquipment extends Packet {

    public static final Class<?> clazz = Reflection.getMinecraftClass("PacketPlayOutEntityEquipment", "net.minecraft.network.protocol.game");

    public PacketPlayOutEntityEquipment(int id, List<Pair<EnumItemSlot, ItemStack>> slots) { // 1.16+
        List<Pair<?, ?>> slots1 = new ArrayList<>();
        for (Pair<EnumItemSlot, ItemStack> slot : slots)
            slots1.add(new Pair<>(slot.getFirst().instance, CraftItemStack.asNMSCopy(slot.getSecond()).instance));
        instance = getConstructor(int.class, List.class).invoke(id, slots1);
    }

    public PacketPlayOutEntityEquipment(int id, EnumItemSlot slot, ItemStack itemStack) { // 1.13-1.15.2
        instance = getConstructor(int.class, EnumItemSlot.clazz, Reflection.getMinecraftClass("ItemStack")).invoke(id, slot.instance, CraftItemStack.asNMSCopy(itemStack).instance);
    }

    private PacketPlayOutEntityEquipment(Object handle) {
        instance = handle;
    }

    public static PacketPlayOutEntityEquipment wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new PacketPlayOutEntityEquipment(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

}
