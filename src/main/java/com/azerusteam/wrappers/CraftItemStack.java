package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

import java.util.Objects;

public class CraftItemStack extends AbstractWrapper {

    public static final Class<?> clazz = Reflection.getCraftBukkitClass("inventory.CraftItemStack");
    Object instance;

    private CraftItemStack(Object handle) {
        instance = handle;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof CraftItemStack && Objects.equals(instance, ((CraftItemStack) o).instance));
    }

    public static CraftItemStack wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new CraftItemStack(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

    public ItemStack getHandle() {
        return ItemStack.wrap(instance);
    }

    public static ItemStack asNMSCopy(org.bukkit.inventory.ItemStack original) {
        return ItemStack.wrap(Reflection.getTypedMethod(clazz, "asNMSCopy",
                ItemStack.clazz, org.bukkit.inventory.ItemStack.class)
                .invoke(null, original));
    }

}
