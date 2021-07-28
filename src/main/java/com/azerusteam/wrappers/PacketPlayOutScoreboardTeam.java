package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

import java.util.Collection;

public class PacketPlayOutScoreboardTeam extends Packet {

    public static final Class<?> clazz = Reflection.getMinecraftClass("PacketPlayOutNamedEntitySpawn");

    public PacketPlayOutScoreboardTeam(ScoreboardTeam team, int mode) {
        instance = getConstructor(ScoreboardTeam.clazz, int.class).invoke(team.instance, mode);
    }

    public PacketPlayOutScoreboardTeam(ScoreboardTeam team, Collection<String> entries, int mode) {
        instance = getConstructor(ScoreboardTeam.clazz, Collection.class, int.class).invoke(team.instance, entries, mode);
    }

    private PacketPlayOutScoreboardTeam(Object handle) {
        instance = handle;
    }

    public static PacketPlayOutScoreboardTeam wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new PacketPlayOutScoreboardTeam(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

}
