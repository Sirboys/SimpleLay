package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;
import com.mojang.authlib.GameProfile;

import java.util.Objects;

public abstract class Entity extends AbstractWrapper {

    public static final Class<?> clazz = Reflection.getMinecraftClass("Entity");

    Object instance;

    public int getId() {
        return getTypedMethod("getId", int.class).invoke(this.instance);
    }

    public void setPositionRotation(double x, double y, double z, float yaw, float pitch) {
        getMethod("setPositionRotation", double.class, double.class, double.class, float.class, float.class).invoke(this.instance, x, y, z, yaw, pitch);
    }

    public void setCustomNameVisible(boolean visible) {
        getMethod("setCustomNameVisible", boolean.class).invoke(this.instance, visible);
    }

    public DataWatcher getDataWatcher() {
        return DataWatcher.wrap(getTypedMethod("getDataWatcher", DataWatcher.clazz).invoke(this.instance));
    }

    public float getHeadRotation() {
        return getTypedMethod("getHeadRotation", float.class).invoke(instance);
    }

    public void setInvisible(boolean invisible) {
        getMethod("setInvisible", boolean.class).invoke(instance, invisible);
    }

    public void setPose(EntityPose pose) {
        getMethod("setPose", EntityPose.clazz);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Entity && Objects.equals(instance, ((Entity) o).instance));
    }

}
