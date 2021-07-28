package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public class PacketPlayOutPlayerInfo extends Packet {

    public static final Class<?> clazz = Reflection.getMinecraftClass("PacketPlayOutPlayerInfo");
    public static final Class<?> enumClazz = Reflection.getMinecraftClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction");

    public PacketPlayOutPlayerInfo(EnumPlayerInfoAction enumPlayerInfoAction, EntityPlayer entityPlayer) {
        instance = getConstructor(enumClazz, EntityPlayer.clazz).invoke(enumPlayerInfoAction.instance, entityPlayer.instance);
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

        ADD_PLAYER, UPDATE_GAME_MODE, UPDATE_LATENCY, UPDATE_DISPLAY_NAME, REMOVE_PLAYER;

        protected final Object instance;

        EnumPlayerInfoAction() {
            instance = Reflection.getEnumConstant(enumClazz, this.name());
        }

        public static EnumPlayerInfoAction wrap(Object handle) {
            if (enumClazz.isInstance(handle))
                return Enum.valueOf(EnumPlayerInfoAction.class, handle.toString());
            else
                throw new IllegalArgumentException("handle isn't an instance of " + enumClazz);
        }

    }
}
