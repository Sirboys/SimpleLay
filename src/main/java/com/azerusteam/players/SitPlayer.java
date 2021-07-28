package com.azerusteam.players;

import com.azerusteam.sirmanager.SimpleLay;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public class SitPlayer {
	public SimpleLay plugin;
	public Player player;
	public ArmorStand armorStand = null;
	public boolean onStairs = false;
	public Location unseatLoc;
	public boolean announceToPlayer;
	public SitPlayer(Player player) {
		this.plugin = SimpleLay.getInstance();
		this.player = player;

       //this.plugin.setLay(player.getUniqueId(),entityPlayer.getId(), seat);
	}

	public void setSit(ArmorStand armorStand, boolean onStairs, Location was, boolean announceToPlayer) {
		this.plugin.setSitting(this.player.getUniqueId(), this);
		this.unseatLoc = was;
		this.onStairs = onStairs;
		this.armorStand = armorStand;
		this.announceToPlayer = announceToPlayer;
	}

	public void unSit(boolean fixLocation) {
		this.player.eject();
		this.plugin.removeSitting(player.getUniqueId());
		this.armorStand.remove();
		if (this.announceToPlayer) {
			this.player.sendMessage(this.plugin.prefix + this.plugin.getConfig().getString("lang.sit.noLonger"));
		}
		if (fixLocation || this.player.isDead())
			return;
		Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.player.teleport(this.armorStand.getLocation().add(0.0, this.onStairs ? 0.7 : 0.2, 0.0).setDirection(this.player.getLocation().getDirection())), 1L);
	}
	public static void checkSeatBlock() {
		for (SitPlayer sitPlayer : SimpleLay.getInstance().getSittingPlayers().values()) {
			if (sitPlayer != null && !sitPlayer.armorStand.getLocation().getBlock().isEmpty()) {
				sitPlayer.unSit(false);
			}
		}
	}
}
