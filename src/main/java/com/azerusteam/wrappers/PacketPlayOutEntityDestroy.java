package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public class PacketPlayOutEntityDestroy extends Packet {

    public static final Class<?> clazz = Reflection.getMinecraftClass("PacketPlayOutEntityDestroy");

    public PacketPlayOutEntityDestroy(int... ids) {
        try {
            instance = getConstructor(int[].class).invoke((Object) ids); // <-- not actually a redundant cast
        } catch (Exception ex) {
            instance = getConstructor(int.class).invoke(ids[0]);
        }
    }

    private PacketPlayOutEntityDestroy(Object handle) {
        instance = handle;
    }

    public static PacketPlayOutEntityDestroy wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new PacketPlayOutEntityDestroy(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

}
