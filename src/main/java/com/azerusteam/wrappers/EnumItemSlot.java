package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

import java.util.Objects;

public enum EnumItemSlot {

    MAINHAND, OFFHAND, FEET, LEGS, CHEST, HEAD;
    Object instance;
    public static final Class<?> clazz = Reflection.getMinecraftClass("EnumItemSlot");
    public final Class<?> clazz1 = Reflection.getMinecraftClass("EnumItemSlot");

    EnumItemSlot() {
        instance = Reflection.getEnumConstant(clazz1, this.name());
    }


}
