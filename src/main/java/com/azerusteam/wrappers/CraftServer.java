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
        //org.bukkit.craftbukkit.v1_17_R1.CraftServer;
        try { //for 1.14 +
            return MinecraftServer.wrap(getTypedMethod("getServer", DedicatedServer.clazz).invoke(instance));
        } catch (Exception e) { //for 1.13 +
            return MinecraftServer.wrap(getTypedMethod("getServer", MinecraftServer.clazz).invoke(instance));
        }
    }

}
