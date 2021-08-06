package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public class MobEffects extends AbstractWrapper {

    public static final Class<?> clazz = Reflection.getMinecraftClass("MobEffects", "net.minecraft.world.effect");
    protected final Object instance;
    public static final MobEffectList INVISIBILITY = getMobEffectList();

    private MobEffects(Object handle) {
        instance = handle;
    }
    public static MobEffects wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new MobEffects(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }
    private static MobEffectList getMobEffectList(){
        try {
            return MobEffectList.wrap(Reflection.getField(clazz, "INVISIBILITY", MobEffectList.clazz).get(null));
        } catch (Exception e) {
            return MobEffectList.wrap(Reflection.getField(clazz, "n", MobEffectList.clazz).get(null));
        }
    }

}
