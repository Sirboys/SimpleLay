package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public abstract class Packet extends AbstractWrapper { // dummy class for <? extends Packet> wildcard
    public static final Class<?> clazz = Reflection.getMinecraftClass("Packet");
    Object instance;
}
