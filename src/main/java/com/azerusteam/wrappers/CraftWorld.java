package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CraftWorld extends AbstractWrapper {

    public static final Class<?> clazz = Reflection.getCraftBukkitClass("CraftWorld");

    protected final Object instance;

    private CraftWorld(Object handle) {
        instance = handle;
    }

    public static CraftWorld wrap(World handle) {
        if (clazz.isInstance(handle))
            return new CraftWorld(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

    public WorldServer getHandle() {
        return WorldServer.wrap(getTypedMethod("getHandle", WorldServer.clazz).invoke(instance));
    }

}
