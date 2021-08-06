package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

import java.util.Optional;

public abstract class EntityLiving extends Entity {

    public static final Class<?> clazz = Reflection.getMinecraftClass("EntityLiving", "net.minecraft.world.entity");

    public static final DataWatcherObject<Optional<BlockPosition>> SLEEPING_POS_ID = DataWatcher.defineId(clazz, DataWatcherRegistry.OPTIONAL_BLOCK_POS);

    public float getHeadRotation() {
        return getTypedMethod("getHeadRotation", float.class).invoke(instance);
    }

    public MobEffect getEffect(MobEffectList mobEffect) {
        return MobEffect.wrap(getTypedMethod("getEffect", MobEffect.clazz, MobEffectList.clazz).invoke(instance, mobEffect.instance));
    }
    public void setBedPosition(BlockPosition blockPosition) {
        try {
            //for 1.16+
            getMethod("e", BlockPosition.clazz).invoke(instance, blockPosition.instance);
        } catch (Exception e) {
            //for 1.15-
            getMethod("d", BlockPosition.clazz).invoke(instance, blockPosition.instance);
        }
    }


}
