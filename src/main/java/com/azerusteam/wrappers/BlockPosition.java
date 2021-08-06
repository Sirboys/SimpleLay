package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;
import org.bukkit.Location;

public class BlockPosition extends AbstractWrapper {

    public static final Class<?> clazz = Reflection.getMinecraftClass("BlockPosition", "net.minecraft.core");

    protected final Object instance;

    public BlockPosition(int x, int y, int z) {
        instance = getConstructor(int.class, int.class, int.class).invoke(x, y, z);
    }

    public BlockPosition(double x, double y, double z) {
        instance = getConstructor(double.class, double.class, double.class).invoke(x, y, z);
    }

    public BlockPosition(Location loc) {
        this(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    private BlockPosition(Object handle) {
        instance = handle;
    }

    public static BlockPosition wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new BlockPosition(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

}
