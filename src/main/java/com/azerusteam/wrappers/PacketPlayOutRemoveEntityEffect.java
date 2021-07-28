package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public class PacketPlayOutRemoveEntityEffect extends Packet {

    public static final Class<?> clazz = Reflection.getMinecraftClass("PacketPlayOutEntityDestroy");

    public PacketPlayOutRemoveEntityEffect(int entityID, MobEffectList mobEffect) {
        instance = getConstructor(int.class, MobEffectList.clazz).invoke(entityID, mobEffect.instance);
    }

    private PacketPlayOutRemoveEntityEffect(Object handle) {
        instance = handle;
    }

    public static PacketPlayOutRemoveEntityEffect wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new PacketPlayOutRemoveEntityEffect(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

}
