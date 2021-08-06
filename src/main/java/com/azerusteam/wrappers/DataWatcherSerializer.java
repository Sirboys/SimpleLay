package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public class DataWatcherSerializer<T> extends AbstractWrapper {

	public static final Class<?> clazz = Reflection.getMinecraftClass("DataWatcherSerializer", "net.minecraft.network.syncher");
	protected final Object instance;

	private DataWatcherSerializer(Object handle) {
		instance = handle;
	}

	public DataWatcherObject<T> createAccessor(int id) {
		return new DataWatcherObject<>(id, this);
//		return (DataWatcherObject<T>) getMethod("a", int.class).invoke(id);
	}

    public static <T> DataWatcherSerializer<T> wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new DataWatcherSerializer<>(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

}
