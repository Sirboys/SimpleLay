package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public class DataWatcherObject<T> extends AbstractWrapper {

    public static final Class<?> clazz = Reflection.getMinecraftClass("DataWatcherObject", "net.minecraft.network.syncher");
    protected final Object instance;

    public DataWatcherObject(int id, DataWatcherSerializer<T> serializer) {
        instance = getConstructor(int.class, DataWatcherSerializer.clazz).invoke(id, serializer.instance);
    }

    private DataWatcherObject(Object handle) {
        instance = handle;
    }

    public static <T> DataWatcherObject<T> wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new DataWatcherObject<>(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

}
