package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public class PacketPlayOutNamedEntitySpawn extends Packet {

    public static final Class<?> clazz = Reflection.getMinecraftClass("PacketPlayOutNamedEntitySpawn", "net.minecraft.network.protocol.game");

    public PacketPlayOutNamedEntitySpawn(EntityHuman entityHuman) {
        instance = getConstructor(EntityHuman.clazz).invoke(entityHuman.instance);
    }

    private PacketPlayOutNamedEntitySpawn(Object handle) {
        instance = handle;
    }

    public static PacketPlayOutNamedEntitySpawn wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new PacketPlayOutNamedEntitySpawn(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

}
