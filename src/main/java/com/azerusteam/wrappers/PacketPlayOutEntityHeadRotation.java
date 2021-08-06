package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public class PacketPlayOutEntityHeadRotation extends Packet {

    public static final Class<?> clazz = Reflection.getMinecraftClass("PacketPlayOutEntityHeadRotation", "net.minecraft.network.protocol.game");

    public PacketPlayOutEntityHeadRotation(Entity entity, byte angle) {
        instance = getConstructor(Entity.clazz, byte.class).invoke(entity.instance, angle);
    }

    private PacketPlayOutEntityHeadRotation(Object handle) {
        instance = handle;
    }

    public static PacketPlayOutEntityHeadRotation wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new PacketPlayOutEntityHeadRotation(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

}
