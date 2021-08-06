package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public class WorldServer extends World {

    public static final Class<?> clazz = Reflection.getMinecraftClass("WorldServer", "net.minecraft.server.level");

    protected final Object instance;

    protected WorldServer(Object handle) {
        instance = handle;
    }

    public static WorldServer wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new WorldServer(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

}
