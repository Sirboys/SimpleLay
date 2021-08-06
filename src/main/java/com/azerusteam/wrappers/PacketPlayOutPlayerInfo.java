package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

import java.lang.reflect.Array;

public class PacketPlayOutPlayerInfo extends Packet {

    public static final Class<?> clazz = Reflection.getMinecraftClass("PacketPlayOutPlayerInfo", "net.minecraft.network.protocol.game");
    public static final Class<?> enumClazz = Reflection.getMinecraftClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction", "net.minecraft.network.protocol.game");

    public PacketPlayOutPlayerInfo(EnumPlayerInfoAction enumPlayerInfoAction, EntityPlayer... entityPlayer) {
        Object entityArray = Array.newInstance(EntityPlayer.clazz, entityPlayer.length);
        int length = Array.getLength(entityArray); // will be 5
        for (int i=0; i < length; i++)
            Array.set(entityArray, i, entityPlayer[i].instance); // set your val here
        instance = getConstructor(enumClazz, EntityPlayer.arrayClazz).invoke(
                enumClazz.cast(enumPlayerInfoAction.instance),
                entityArray
                );
    }

    private PacketPlayOutPlayerInfo(Object handle) {
        instance = handle;
    }

    public static PacketPlayOutPlayerInfo wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new PacketPlayOutPlayerInfo(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

    public enum EnumPlayerInfoAction {

        ADD_PLAYER("a"),
        UPDATE_GAME_MODE("b"),
        UPDATE_LATENCY("c"),
        UPDATE_DISPLAY_NAME("d"),
        REMOVE_PLAYER("e");

        private Object instance;
        public final String obfuscatedName;
        EnumPlayerInfoAction(String obfuscatedName) {
            this.obfuscatedName = obfuscatedName;
            try {
                instance = Reflection.getEnumConstant(enumClazz, this.name());
            } catch (Exception e) {
                instance = Reflection.getEnumConstant(enumClazz, this.obfuscatedName);
            }
        }

        public static EnumPlayerInfoAction wrap(Object handle) {
            if (enumClazz.isInstance(handle))
                return Enum.valueOf(EnumPlayerInfoAction.class, handle.toString());
            else
                throw new IllegalArgumentException("handle isn't an instance of " + enumClazz);
        }

    }
}
