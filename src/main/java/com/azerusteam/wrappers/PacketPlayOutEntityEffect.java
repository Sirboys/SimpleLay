package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public class PacketPlayOutEntityEffect extends Packet {

    public static final Class<?> clazz = Reflection.getMinecraftClass("PacketPlayOutEntityEffect", "net.minecraft.network.protocol.game");

    public PacketPlayOutEntityEffect(int id, MobEffect mobEffectInstance) {
        instance = getConstructor(int.class, MobEffect.clazz).invoke(id, mobEffectInstance.instance);
    }

    private PacketPlayOutEntityEffect(Object handle) {
        instance = handle;
    }

    public static PacketPlayOutEntityEffect wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new PacketPlayOutEntityEffect(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

}
