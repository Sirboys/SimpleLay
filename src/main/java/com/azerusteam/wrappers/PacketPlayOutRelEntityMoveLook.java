package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public class PacketPlayOutRelEntityMoveLook extends Packet {

    public static final Class<?> clazz = Reflection.getMinecraftClass("PacketPlayOutEntity$PacketPlayOutRelEntityMoveLook");

    public PacketPlayOutRelEntityMoveLook(int id, short deltaX, short deltaY, short deltaZ, byte yaw, byte pitch,
                                          boolean onGround) {
        instance = getConstructor(int.class, short.class, short.class, short.class, byte.class, byte.class, boolean.class)
                .invoke(id, deltaX, deltaY, deltaZ, yaw, pitch, onGround);
    }

    private PacketPlayOutRelEntityMoveLook(Object handle) {
        instance = handle;
    }

    public static PacketPlayOutRelEntityMoveLook wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new PacketPlayOutRelEntityMoveLook(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

}
