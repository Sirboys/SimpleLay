package ru.azerusteam.Classes;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.azerusteam.sirmanager.Main;

public class SitPlayer {
	public Main plugin;
	public Player player;
	public ArmorStand armorstand = null;
	public boolean stairs = false;
	public Location unseatLoc;
	public boolean announce;
	public SitPlayer(Player player) {
		this.plugin = (Main) JavaPlugin.getPlugin((Class)Main.class);
		this.player = player;
		
		
       //this.plugin.setLay(player.getUniqueId(),entityPlayer.getId(), seat);
	}
	public void setSit(ArmorStand as,boolean stairs, Location was, boolean announce) {
		this.plugin.setSeat(this.player.getUniqueId(), this);
		this.unseatLoc = was;
		this.stairs = stairs;
		this.armorstand = as;
		this.announce = announce;
	}
	public void unSit(boolean disable) {
		player.eject();
		ArmorStand seat = armorstand;
		Player p = player;
		plugin.removeSeat(player.getUniqueId());
		seat.remove();
		if (announce) {
			this.player.sendMessage(plugin.prefix+plugin.getConfig().getString("lang.sit.noLonger"));
		}
		if (disable) return;
		if (p.isDead()) return;
		if (stairs) {
			p.teleport(seat.getLocation().clone().add(0.0, 0.7, 0.0).setDirection(SitPlayer.this.player.getLocation().getDirection()));
		}else {
			p.teleport(seat.getLocation().clone().add(0.0, 0.2, 0.0).setDirection(SitPlayer.this.player.getLocation().getDirection()));	
		}
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				if (stairs) {
        			p.teleport(seat.getLocation().clone().add(0.0, 0.7, 0.0).setDirection(SitPlayer.this.player.getLocation().getDirection()));
				}else {
        			p.teleport(seat.getLocation().clone().add(0.0, 0.2, 0.0).setDirection(SitPlayer.this.player.getLocation().getDirection()));	
				}
			}
		}, 1L);
	}
	public static void checkSeatBlock(Main plugin){
		Collection<SitPlayer> spList = plugin.getSeats().values();
		for (Iterator iterator = spList.iterator(); iterator.hasNext();) {
			SitPlayer sitPlayer = (SitPlayer) iterator.next();
			if (sitPlayer == null) continue;
			if (sitPlayer.armorstand.getLocation().subtract(0, 0.0, 0).getBlock().getType() != Material.AIR) continue;
			sitPlayer.unSit(false);
		};
	}
}
