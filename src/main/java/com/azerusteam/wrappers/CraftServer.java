package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;
import org.bukkit.Server;

public class CraftServer extends AbstractWrapper {

    public static final Class<?> clazz = Reflection.getCraftBukkitClass("CraftServer");

    protected final Object instance;

    private CraftServer(Object handle) {
        instance = handle;
    }

    public static CraftServer wrap(Server handle) {
        if (clazz.isInstance(handle))
            return new CraftServer(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

    public MinecraftServer getServer() {
        return MinecraftServer.wrap(getTypedMethod("getHandle", MinecraftServer.clazz).invoke(instance));
    }

}
