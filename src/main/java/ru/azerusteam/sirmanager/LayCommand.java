package ru.azerusteam.sirmanager;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import ru.azerusteam.Classes.SirPlayer;

public class LayCommand implements CommandExecutor {
	private Main plugin;
	public LayCommand() {
		this.plugin = (Main) JavaPlugin.getPlugin(Main.class);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
		if (!(sender instanceof Player)){
			sender.sendMessage("This command only for players!");
			return false;
		}
		SirPlayer sp = new SirPlayer((Player) sender);
		
		if (sp.isLay()) {
			sp.unLay();
		}else {
			if(sp.isSitting()) {
				sp.getPlayer().sendMessage(plugin.prefix+plugin.getConfig().getString("lang.sit.now"));
				return false;
			}else if (!sp.getPlayer().isOnGround() || sp.getPlayer().getLocation().subtract(0,0.2,0).getBlock().getType() == Material.AIR) {
				sp.getPlayer().sendMessage(plugin.prefix+plugin.getConfig().getString("lang.lay.notOnGround"));
				return false;
			} 
			sp.setLay();
		}
		return true;
	}

}
