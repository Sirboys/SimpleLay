package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public class PlayerConnection extends AbstractWrapper {

    public static final Class<?> clazz = Reflection.getMinecraftClass("PlayerConnection", "net.minecraft.server.network");

    protected final Object instance;

    private PlayerConnection(Object handle) {
        instance = handle;
    }

    public static PlayerConnection wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new PlayerConnection(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

    public void sendPacket(Packet... packets) {
        for (Packet packet : packets) {
            getMethod("sendPacket", Packet.clazz).invoke(instance, packet.instance);
        }
    }

}
