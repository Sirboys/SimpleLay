package com.azerusteam.sirmanager;

import com.azerusteam.players.SirPlayer;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SitCommand implements CommandExecutor{
	private final SimpleLay plugin;
	public SitCommand() {
		this.plugin = SimpleLay.getInstance();
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
		if (!(sender instanceof Player)){
			sender.sendMessage("This command only for players!");
			return true;
		}

		SirPlayer sp = new SirPlayer((Player) sender);

		if (sp.isSitting()) {
			//sp.pl.sendMessage(sp.pl.toString());
			sp.walk();
		} else {
			if(sp.isLay()) {
				sp.getPlayer().sendMessage(plugin.prefix+plugin.getConfig().getString("lang.lay.now"));
				return false;
			} else if (sp.getPlayer().getLocation().subtract(0,0.2,0).getBlock().getType() == Material.AIR || !sp.getPlayer().isOnGround()) {
				sp.getPlayer().sendMessage(plugin.prefix+plugin.getConfig().getString("lang.sit.notOnGround"));
				return false;
			}
			sp.setSit(false,((Player) sender).getLocation(),plugin.getConfig().getBoolean("sit.announce.command"));
		}
		return true;
	}
}
