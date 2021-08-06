package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;
import org.jetbrains.annotations.Nullable;

public class MobEffect extends AbstractWrapper {

    public static final Class<?> clazz = Reflection.getMinecraftClass("MobEffect", "net.minecraft.world.effect");

    protected final Object instance;

    public MobEffect(MobEffectList mobEffect, int duration, int amplifier, boolean ambient, boolean visible) {
        instance = getConstructor(MobEffectList.clazz, int.class, int.class, boolean.class, boolean.class)
                .invoke(mobEffect.instance, duration, amplifier, ambient, visible);
    }

    public MobEffect(MobEffect mobEffectInstance) {
        instance = getConstructor(MobEffect.clazz).invoke(mobEffectInstance.instance);
    }

    private MobEffect(Object handle) {
        instance = handle;
    }

    @Nullable
    public static MobEffect wrap(Object handle) {
        if (handle == null) return null;
        if (clazz.isInstance(handle))
            return new MobEffect(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

}
