package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public class PacketPlayOutBed extends Packet {

    public static final Class<?> clazz = Reflection.getMinecraftClass("PacketPlayOutBed", "net.minecraft.network.protocol.game");

    public PacketPlayOutBed(EntityHuman entityHuman, BlockPosition blockPosition) {
        instance = getConstructor(EntityHuman.clazz, BlockPosition.clazz).invoke(entityHuman.instance, blockPosition.instance);
    }

    private PacketPlayOutBed(Object handle) {
        instance = handle;
    }

    public static PacketPlayOutBed wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new PacketPlayOutBed(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

}
