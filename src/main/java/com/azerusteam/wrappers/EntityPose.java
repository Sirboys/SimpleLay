package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public enum EntityPose {

    STANDING("a"),
    FALL_FLYING("b"),
    SLEEPING("c"),
    SWIMMING("d"),
    SPIN_ATTACK("e"),
    SNEAKING("f"),
    DYING("g");
    Object instance;
    public final String obfuscatedName;
    public static final Class<?> clazz = Reflection.getMinecraftClass("EntityPose", "net.minecraft.world.entity");
    public final Class<?> clazz1 = Reflection.getMinecraftClass("EntityPose", "net.minecraft.world.entity");

    EntityPose(String obfuscatedName) {
        this.obfuscatedName = obfuscatedName;
        //fixme obfuscated :(
        try {
            instance = Reflection.getEnumConstant(clazz1, this.name());
        } catch (Exception e) {
            instance = Reflection.getEnumConstant(clazz1, this.obfuscatedName);
        }
    }


}
