package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public class PlayerInteractManager extends AbstractWrapper {

    public static final Class<?> clazz = Reflection.getMinecraftClass("PlayerInteractManager", "net.minecraft.server.level");

    protected Object instance;

    public PlayerInteractManager(World world) {
        instance = getConstructor(World.clazz).invoke(world.instance);
    }
    public PlayerInteractManager(WorldServer worldServer) {
        try {
            instance = getConstructor(WorldServer.clazz).invoke(worldServer.instance);
        } catch (Exception e) {
            instance = getConstructor(World.clazz).invoke(worldServer.instance);
        }
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
