package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public class ScoreboardServer extends Scoreboard {

    public static final Class<?> clazz = Reflection.getMinecraftClass("ScoreboardServer", "net.minecraft.server");

    protected final Object instance;

    private ScoreboardServer(Object handle) {
        super();
        instance = handle;
    }

    public static ScoreboardServer wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new ScoreboardServer(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

}
