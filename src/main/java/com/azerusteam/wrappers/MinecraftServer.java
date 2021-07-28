package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public class MinecraftServer extends AbstractWrapper {

    public static final Class<?> clazz = Reflection.getMinecraftClass("MinecraftServer");

    protected final Object instance;

    private MinecraftServer(Object handle) {
        instance = handle;
    }

    public static MinecraftServer wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new MinecraftServer(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

    public ScoreboardServer getScoreboard() {
        return ScoreboardServer.wrap(getTypedMethod("getScoreboard", ScoreboardServer.clazz).invoke(instance));
    }

}
