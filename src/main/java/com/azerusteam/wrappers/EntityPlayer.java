package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;
import com.mojang.authlib.GameProfile;
import org.bukkit.entity.Player;

public class EntityPlayer extends EntityHuman {

    public static final Class<?> clazz = Reflection.getMinecraftClass("EntityPlayer", "net.minecraft.server.level");
    public static final Class<?> arrayClazz = Reflection.getArrayOfMinecraftClass("EntityPlayer", "net.minecraft.server.level");

    public EntityPlayer(MinecraftServer minecraftServer, WorldServer worldServer, GameProfile gameProfile, PlayerInteractManager playerInteractManager) {
        instance = getConstructor(MinecraftServer.clazz, WorldServer.clazz, GameProfile.class, PlayerInteractManager.clazz)
                .invoke(minecraftServer.instance, worldServer.instance, gameProfile, playerInteractManager.instance);
    }
    public EntityPlayer(MinecraftServer minecraftServer, WorldServer worldServer, GameProfile gameProfile) {
        instance = getConstructor(MinecraftServer.clazz, WorldServer.clazz, GameProfile.class)
                .invoke(minecraftServer.instance, worldServer.instance, gameProfile);
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
        try {
            return PlayerConnection.wrap(getField("playerConnection", PlayerConnection.clazz).get(instance));
        } catch (Exception e) {
            return PlayerConnection.wrap(getField("b", PlayerConnection.clazz).get(instance));
        }
    }
    public void setListName(String listName) {
        getField("listName", String.class).set(instance, listName);
    }
    public void setListName(IChatBaseComponent listName) {
        getField("listName", IChatBaseComponent.class).set(instance, listName);
    }
    public String getPlayerListName() {
        return (String) getMethod("getPlayerListName", String.class).invoke(instance);
    }
    public IChatBaseComponent getPlayerListNameAsChatComponent() {
        return IChatBaseComponent.wrap(getTypedMethod("getPlayerListName", IChatBaseComponent.clazz).invoke(instance));
    }
    public Player getBukkitEntity() {
        return getTypedMethod("getBukkitEntity", CraftPlayer.clazz).invoke(instance);
    }

}
