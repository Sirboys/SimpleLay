package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public class ScoreboardServer extends AbstractWrapper {

    public static final Class<?> clazz = Reflection.getMinecraftClass("ScoreboardServer");

    protected final Object instance;

    private ScoreboardServer(Object handle) {
        instance = handle;
    }

    public static ScoreboardServer wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new ScoreboardServer(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

}
