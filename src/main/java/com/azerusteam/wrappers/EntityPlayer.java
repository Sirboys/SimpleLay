package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;
import com.mojang.authlib.GameProfile;
import org.bukkit.entity.Player;

public class EntityPlayer extends EntityHuman {

    public static final Class<?> clazz = Reflection.getMinecraftClass("EntityPlayer");

    public EntityPlayer(MinecraftServer minecraftServer, WorldServer worldServer, GameProfile gameProfile, PlayerInteractManager playerInteractManager) {
        instance = getConstructor(MinecraftServer.clazz, WorldServer.clazz, GameProfile.class, PlayerInteractManager.clazz)
                .invoke(minecraftServer.instance, worldServer.instance, gameProfile, playerInteractManager.instance);
    }

    private EntityPlayer(Object handle) {
        instance = handle;
    }

    public static EntityPlayer wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new EntityPlayer(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

    public PlayerConnection getPlayerConnection() {
        return PlayerConnection.wrap(getField("playerConnection", PlayerConnection.clazz).get(instance));
    }

    public Player getBukkitEntity() {
        return getTypedMethod("getBukkitEntity", CraftPlayer.clazz).invoke(instance);
    }

}
