package ru.azerusteam.Classes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.azerusteam.layhandler.*;
import ru.azerusteam.sirmanager.Main;

public class LayPlayer{
	String ver = "";
	public LayPlayer(Player player) {
		
	}
	public static ILayPlayer getLayPlayerInstance(Player player) {
		String ver = Bukkit.getServer().getClass().getPackage().getName().substring(23);
		//System.out.println(">>"+ver+"<<");
		String bukkitVersion = Bukkit.getBukkitVersion();
		if (bukkitVersion.equals("1.17-R0.1-SNAPSHOT")) {
			return new LayPlayer_v1_17_R1(player);
		} else if (ver.equals("v1_16_R3")) {
			return new LayPlayer_v1_16_R3(player);
		} else if (ver.equals("v1_16_R2")) {
			return new LayPlayer_v1_16_R2(player);
		} else if (ver.equals("v1_16_R1")) {
			return new LayPlayer_v1_16_R1(player);
		} else if (ver.equals("v1_15_R1")) {
			return new LayPlayer_v1_15_R1(player);
		} else if (ver.equals("v1_14_R1")) {
			return new LayPlayer_v1_14_R1(player);
		} else if (ver.equals("v1_13_R1")){
			return new LayPlayer_v1_13_R1(player);
		} else if (ver.equals("v1_13_R2")){
			return new LayPlayer_v1_13_R2(player);

		}else{
			return null;
		}
	}
	public static void upDateArmor(Main plugin) {
		String ver = Bukkit.getServer().getClass().getPackage().getName().substring(23);
		String bukkitVersion = Bukkit.getBukkitVersion();
		if (bukkitVersion.equals("1.17-R0.1-SNAPSHOT")) {
			LayPlayer_v1_17_R1.upDateArmor(plugin);
		} else if (ver.equals("v1_16_R3")) {
			LayPlayer_v1_16_R3.upDateArmor(plugin);
		}else if (ver.equals("v1_16_R2")) {
			LayPlayer_v1_16_R2.upDateArmor(plugin);
		}else if (ver.equals("v1_16_R1")) {
			LayPlayer_v1_16_R1.upDateArmor(plugin);
		}else if (ver.equals("v1_15_R1")) {
			LayPlayer_v1_15_R1.upDateArmor(plugin);
		}else if (ver.equals("v1_14_R1")) {
			LayPlayer_v1_14_R1.upDateArmor(plugin);
		}else if (ver.equals("v1_13_R1")) {
			LayPlayer_v1_13_R1.upDateArmor(plugin);
		}else if (ver.equals("v1_13_R2")) {
			LayPlayer_v1_13_R2.upDateArmor(plugin);
		}
	}
	public static void checkSeatBlock(Main plugin) {
		String ver = Bukkit.getServer().getClass().getPackage().getName().substring(23);
		String bukkitVersion = Bukkit.getBukkitVersion();
		if (bukkitVersion.equals("1.17-R0.1-SNAPSHOT")) {
			LayPlayer_v1_17_R1.checkSeatBlock(plugin);
		} else if (ver.equals("v1_16_R3")) {
			LayPlayer_v1_16_R3.checkSeatBlock(plugin);
		}else if (ver.equals("v1_16_R2")) {
			LayPlayer_v1_16_R2.checkSeatBlock(plugin);
		}else if (ver.equals("v1_16_R1")) {
			LayPlayer_v1_16_R1.checkSeatBlock(plugin);
		}else if (ver.equals("v1_15_R1")) {
			LayPlayer_v1_15_R1.checkSeatBlock(plugin);
		}else if (ver.equals("v1_14_R1")) {
			LayPlayer_v1_14_R1.checkSeatBlock(plugin);
		}else if (ver.equals("v1_13_R1")) {
			LayPlayer_v1_13_R1.checkSeatBlock(plugin);
		}else if (ver.equals("v1_13_R2")) {
			LayPlayer_v1_13_R2.checkSeatBlock(plugin);
		}
	}
	public static void showLayingPlayers(Main plugin,Player watcherPlayer) {
		String ver = Bukkit.getServer().getClass().getPackage().getName().substring(23);
		String bukkitVersion = Bukkit.getBukkitVersion();
		if (bukkitVersion.equals("1.17-R0.1-SNAPSHOT")) {
			LayPlayer_v1_17_R1.showLayingPlayers(plugin, watcherPlayer);
		} else if (ver.equals("v1_16_R3")) {
			LayPlayer_v1_16_R3.showLayingPlayers(plugin, watcherPlayer);
		}else if (ver.equals("v1_16_R2")) {
			LayPlayer_v1_16_R2.showLayingPlayers(plugin, watcherPlayer);
		}else if (ver.equals("v1_16_R1")) {
			LayPlayer_v1_16_R1.showLayingPlayers(plugin, watcherPlayer);
		}else if (ver.equals("v1_15_R1")) {
			LayPlayer_v1_15_R1.showLayingPlayers(plugin, watcherPlayer);
		}else if (ver.equals("v1_14_R1")) {
			LayPlayer_v1_14_R1.showLayingPlayers(plugin, watcherPlayer);
		}else if (ver.equals("v1_13_R1")) {
			LayPlayer_v1_13_R1.showLayingPlayers(plugin, watcherPlayer);
		}else if (ver.equals("v1_13_R2")) {
			LayPlayer_v1_13_R2.showLayingPlayers(plugin, watcherPlayer);
		}
	}
}