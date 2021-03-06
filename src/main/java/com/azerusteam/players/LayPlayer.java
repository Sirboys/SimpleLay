package com.azerusteam.players;

import com.azerusteam.layhandler.*;
import com.azerusteam.sirmanager.SimpleLay;
import com.comphenix.tinyprotocol.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LayPlayer {
	public static ILayPlayer getLayPlayerInstance(Player player) {
		return getConstructor(Player.class).invoke(player);
	}
	public static void updateArmor(SimpleLay plugin) {
		getMethod("upDateArmor", SimpleLay.class).invoke(null, plugin);
	}

	public static void checkSeatBlock(SimpleLay plugin) {
		getMethod("checkSeatBlock", SimpleLay.class).invoke(null, plugin);
	}
	public static void showLayingPlayers(SimpleLay plugin, Player watcherPlayer) {
		getMethod("showLayingPlayers", SimpleLay.class, Player.class).invoke(null, plugin, watcherPlayer);
	}

	private static Reflection.ConstructorInvoker<? extends ILayPlayer> getConstructor(Class<?>... params) {
		return Reflection.getConstructor(getLayPlayerClass(), params);
	}

	private static Reflection.MethodInvoker<?> getMethod(String methodName, Class<?>... params) {
		return Reflection.getMethod(getLayPlayerClass(), methodName, params);
	}

	private static Class<? extends ILayPlayer> getLayPlayerClass() {
		Class<? extends ILayPlayer> clazz;
		String version = Bukkit.getServer().getClass().getPackage().getName().substring(23);
		switch (version) {
			case "v1_17_R2":
			case "v1_17_R1":
				clazz = LayPlayer_v1_17_R1.class;
				break;
			case "v1_16_R3":
			case "v1_16_R2":
			case "v1_16_R1":
				clazz = LayPlayer_v1_16_R1.class;
				break;
			case "v1_15_R1":
			case "v1_14_R1":
				clazz = LayPlayer_v1_14_R1.class;
				break;
			case "v1_13_R2":
			case "v1_13_R1":
				clazz = LayPlayer_v1_13_R1.class;
				break;
			default:
				SimpleLay.getInstance().getPluginLoader().disablePlugin(SimpleLay.getInstance());
				throw new UnsupportedOperationException("Unsupported server version (" + version + ")");
		}
		return clazz;
	}
}