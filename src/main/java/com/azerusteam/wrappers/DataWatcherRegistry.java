package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

import java.util.Optional;

public class DataWatcherRegistry extends AbstractWrapper {

    public static final Class<?> clazz = Reflection.getMinecraftClass("DataWatcherRegistry");
    protected final Object instance;

    public static final DataWatcherSerializer<Byte> BYTE = DataWatcherSerializer.wrap(Reflection.getField(clazz, "a", DataWatcherSerializer.clazz).get(null));
    public static final DataWatcherSerializer<Optional<BlockPosition>> OPTIONAL_BLOCK_POS = DataWatcherSerializer.wrap(Reflection.getField(clazz, "m", DataWatcherSerializer.clazz).get(null));

    private DataWatcherRegistry(Object handle) {
        instance = handle;
    }

    public static DataWatcherRegistry wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new DataWatcherRegistry(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

}
