package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public abstract class World extends AbstractWrapper { // dummy class for <? extends World> wildcard
    public static final Class<?> clazz = Reflection.getMinecraftClass("World");
}
