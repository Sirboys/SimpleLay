package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

import java.util.Optional;

public abstract class EntityLiving extends Entity {

    public static final Class<?> clazz = Reflection.getMinecraftClass("EntityLiving");

    public static final DataWatcherObject<Optional<BlockPosition>> SLEEPING_POS_ID = DataWatcher.defineId(clazz, DataWatcherRegistry.OPTIONAL_BLOCK_POS);

    public float getHeadRotation() {
        return getTypedMethod("getHeadRotation", float.class).invoke(instance);
    }

    public MobEffect getEffect(MobEffectList mobEffect) {
        return MobEffect.wrap(getTypedMethod("getEffect", MobEffect.clazz, MobEffectList.clazz).invoke(instance, mobEffect));
    }

}
