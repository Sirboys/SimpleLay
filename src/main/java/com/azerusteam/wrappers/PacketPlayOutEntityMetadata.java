package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public class PacketPlayOutEntityMetadata extends Packet {

    public static final Class<?> clazz = Reflection.getMinecraftClass("PacketPlayOutEntityMetadata");

    public PacketPlayOutEntityMetadata(int id, DataWatcher data, boolean forceUpdateAll) {
        instance = getConstructor(int.class, DataWatcher.clazz, boolean.class).invoke(id, data.instance, forceUpdateAll);
    }

    private PacketPlayOutEntityMetadata(Object handle) {
        instance = handle;
    }

    public static PacketPlayOutEntityMetadata wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new PacketPlayOutEntityMetadata(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

}
