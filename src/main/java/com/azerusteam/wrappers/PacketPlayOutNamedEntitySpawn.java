package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public class PacketPlayOutNamedEntitySpawn extends Packet {

    public static final Class<?> clazz = Reflection.getMinecraftClass("PacketPlayOutNamedEntitySpawn");

    public PacketPlayOutNamedEntitySpawn(EntityPlayer entityPlayer) {
        instance = getConstructor(EntityPlayer.clazz).invoke(entityPlayer);
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
