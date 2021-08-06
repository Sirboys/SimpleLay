package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public class World extends AbstractWrapper { // dummy class for <? extends World> wildcard

    public static final Class<?> clazz = Reflection.getMinecraftClass("World", "net.minecraft.world.level");

    //public static final Class<?> clazz = Reflection.getMinecraftClass("WorldServer");

    protected final Object instance;

    protected World(Object handle) {
        instance = handle;
    }

    public World() {
        instance = null;
    }

    public static World wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new World(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }
}
