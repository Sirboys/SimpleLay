package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public class PlayerInteractManager extends AbstractWrapper {

    public static final Class<?> clazz = Reflection.getMinecraftClass("PlayerInteractManager");

    protected final Object instance;

    public PlayerInteractManager(WorldServer worldServer) {
        instance = getConstructor(WorldServer.clazz).invoke(worldServer.instance);
    }

    private PlayerInteractManager(Object handle) {
        instance = handle;
    }

    public static PlayerInteractManager wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new PlayerInteractManager(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

}
