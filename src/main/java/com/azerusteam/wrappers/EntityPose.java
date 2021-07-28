package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public enum EntityPose {

    STANDING, FALL_FLYING, SLEEPING, SWIMMING, SPIN_ATTACK, SNEAKING, DYING;
    Object instance;
    public static final Class<?> clazz = Reflection.getMinecraftClass("EntityPose");
    public final Class<?> clazz1 = Reflection.getMinecraftClass("EntityPose");

    EntityPose() {
        instance = Reflection.getEnumConstant(clazz1, this.name());
    }


}
