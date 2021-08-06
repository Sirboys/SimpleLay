package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public enum EnumItemSlot {

    MAINHAND, OFFHAND, FEET, LEGS, CHEST, HEAD;
    Object instance;
    public static final Class<?> clazz = Reflection.getMinecraftClass("EnumItemSlot", "net.minecraft.world.entity");
    public final Class<?> clazz1 = Reflection.getMinecraftClass("EnumItemSlot", "net.minecraft.world.entity");

    EnumItemSlot() {
        //fixme obfuscated
        instance = Reflection.getEnumConstant(clazz1, this.name());
    }


}
