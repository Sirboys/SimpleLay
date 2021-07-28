package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;
import com.mojang.authlib.GameProfile;

public abstract class EntityHuman extends EntityLiving {

    public static final Class<?> clazz = Reflection.getMinecraftClass("EntityHuman");

    public GameProfile getProfile() {
        return getTypedMethod("getProfile", GameProfile.class).invoke(this.instance);
    }

}
