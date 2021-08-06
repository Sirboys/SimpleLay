package com.azerusteam.sirmanager;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.azerusteam.players.SirPlayer;
import org.jetbrains.annotations.NotNull;

public class LayCommand implements CommandExecutor {
	private final SimpleLay plugin;
	public LayCommand() {
		this.plugin = SimpleLay.getInstance();
	}
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String string, String[] args) {
		if (!(sender instanceof Player)){
			sender.sendMessage("This command is for players only!");
			return false;
		}
		SirPlayer sp = new SirPlayer((Player) sender);
		
		if (sp.isLay()) {
			sp.unLay();
		} else {
			if (sp.isSitting()) {
				sp.getPlayer().sendMessage(plugin.prefix + plugin.getConfig().getString("lang.sit.now"));
				return false;
			} else if (!sp.getPlayer().isOnGround() || sp.getPlayer().getLocation().subtract(0, 0.2, 0).getBlock().getType() == Material.AIR) {
				sp.getPlayer().sendMessage(plugin.prefix + plugin.getConfig().getString("lang.lay.notOnGround"));
				return false;
			} 
			sp.setLay();
		}
		return true;
	}

}
