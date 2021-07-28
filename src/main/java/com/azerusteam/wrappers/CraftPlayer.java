package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;
import org.bukkit.entity.Player;

public class CraftPlayer extends AbstractWrapper {

    @SuppressWarnings("unchecked")
    public static final Class<? extends Player> clazz = (Class<? extends Player>) Reflection.getCraftBukkitClass("entity.CraftPlayer");

    protected final Object instance;

    private CraftPlayer(Object handle) {
        instance = handle;
    }

    public static CraftPlayer wrap(Player handle) {
        if (clazz.isInstance(handle))
            return new CraftPlayer(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

    public EntityPlayer getHandle() {
        return EntityPlayer.wrap(getTypedMethod("getHandle", EntityPlayer.clazz).invoke(instance));
    }

}
