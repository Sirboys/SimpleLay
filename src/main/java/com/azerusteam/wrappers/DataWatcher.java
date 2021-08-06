package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

import java.util.Optional;

public class DataWatcher extends AbstractWrapper {

    public static final Class<?> clazz = Reflection.getMinecraftClass("DataWatcher", "net.minecraft.network.syncher");

    protected final Object instance;

    private DataWatcher(Object handle) {
        instance = handle;
    }

    public static DataWatcher wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new DataWatcher(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

    public <T> void set(DataWatcherObject<T> accessor, T value) {
        // unwrapping value
        Object val = value;
        if (val instanceof Optional) {
            @SuppressWarnings("unchecked")
            Optional<Object> optional = (Optional<Object>) val;
            if (optional.isPresent()) {
                Object val1 = optional.get();
                if (val1 instanceof AbstractWrapper) {
                    try {
                        val = Optional.of(Reflection.getField(val1.getClass(), "instance", Object.class).get(val1));
                    } catch (Exception ignored) {
                    }
                }
            }
        } else if (val instanceof AbstractWrapper) {
            try {
                val = Optional.of(Reflection.getField(val.getClass(), "instance", Object.class).get(val));
            } catch (Exception ignored) {
            }
        }
        // original nms value is passed to the method
        getMethod("set", DataWatcherObject.clazz, Object.class).invoke(clazz.cast(instance), DataWatcherObject.clazz.cast(accessor.instance), val);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(DataWatcherObject<T> accessor) {

        return (T) getMethod("get", DataWatcherObject.clazz).invoke(clazz.cast(instance), DataWatcherObject.clazz.cast(accessor.instance));
    }

    public static <T> DataWatcherObject<T> defineId(Class<?> entityClass, DataWatcherSerializer<T> serializer) {
        return DataWatcherObject.wrap(Reflection.getTypedMethod(clazz, "a", DataWatcherObject.clazz, Class.class, DataWatcherSerializer.clazz).invoke(null, entityClass, serializer.instance));
    }

}
