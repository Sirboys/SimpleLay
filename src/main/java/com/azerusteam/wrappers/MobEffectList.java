package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public class MobEffectList extends AbstractWrapper {

    public static final Class<?> clazz = Reflection.getMinecraftClass("MobEffectList", "net.minecraft.world.effect");
    protected final Object instance;

    //public static final MobEffectList INVISIBILITY = MobEffectList.wrap(Reflection.getField(clazz, "INVISIBILITY", MobEffectList.clazz));

    private MobEffectList(Object handle) {
        instance = handle;
    }
    public static MobEffectList wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new MobEffectList(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

}
