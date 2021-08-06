package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

import java.util.Objects;

public class ItemStack extends AbstractWrapper {

    public static final Class<?> clazz = Reflection.getMinecraftClass("ItemStack", "net.minecraft.world.item");
    Object instance;

    private ItemStack(Object handle) {
        instance = handle;
    }

    public static ItemStack wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new ItemStack(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof ItemStack && Objects.equals(instance, ((ItemStack) o).instance));
    }

}
