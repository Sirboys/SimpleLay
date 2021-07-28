package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public class ScoreboardTeam extends AbstractWrapper {

    public static final Class<?> clazz = Reflection.getMinecraftClass("ScoreboardTeam");
    public static final Class<?> enumClazz = Reflection.getMinecraftClass("ScoreboardTeamBase$EnumNameTagVisibility");

    protected final Object instance;

    public ScoreboardTeam(ScoreboardServer scoreboardServer, String name) {
        instance = getConstructor(ScoreboardServer.clazz, String.class).invoke(scoreboardServer.instance, name);
    }

    private ScoreboardTeam(Object handle) {
        instance = handle;
    }

    public static ScoreboardTeam wrap(Object handle) {
        if (clazz.isInstance(handle))
            return new ScoreboardTeam(handle);
        else
            throw new IllegalArgumentException("handle isn't an instance of " + clazz);
    }

    public ScoreboardTeam setNameTagVisibility(EnumNameTagVisibility visibility) {
        getMethod("setNameTagVisibility", enumClazz).invoke(visibility.instance);
        return this;
    }

    public enum EnumNameTagVisibility {
        ALWAYS, NEVER, HIDE_FOR_OTHER_TEAMS, HIDE_FOR_OWN_TEAM;

        protected final Object instance;

        EnumNameTagVisibility() {
            instance = Reflection.getEnumConstant(enumClazz, this.name());
        }

        public static EnumNameTagVisibility wrap(Object handle) {
            if (enumClazz.isInstance(handle))
                return Enum.valueOf(EnumNameTagVisibility.class, handle.toString());
            else
                throw new IllegalArgumentException("handle isn't an instance of " + enumClazz);
        }
    }

}
