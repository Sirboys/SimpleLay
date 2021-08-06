package com.azerusteam.players;

import com.azerusteam.sirmanager.SimpleLay;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class SirPlayer {
	private final Player player;
	private final SimpleLay plugin;
	public SirPlayer(final Player player) {
		this.player = player;
		this.plugin = SimpleLay.getInstance();
	}
	public void setSit(boolean stairs, Location loc, boolean announceToPlayer) {
		if (!this.player.hasPermission("simplelay.sit") && !this.player.hasPermission("simplelay.*")) {
			this.player.sendMessage(this.plugin.prefix + this.plugin.getConfig().getString("lang.no-permissions"));
			return;
		}
		final World world = loc.getWorld();
		if (world == null) {
			this.plugin.getLogger().warning("SirPlayer#setSit(...) is called, however player location's world is null");
			return;
		}
		final ArmorStand seat = (ArmorStand) loc.getWorld().spawn(loc.clone().subtract(0, 1.7, 0), ArmorStand.class);
		seat.setGravity(false);
		seat.setVisible(false);
		seat.addPassenger(player);
        SitPlayer sitPlayer = new SitPlayer(this.player);
        sitPlayer.setSit(seat, stairs, loc, announceToPlayer);
        if (announceToPlayer) {
        	this.player.sendMessage(this.plugin.prefix + plugin.getConfig().getString("lang.sit.now"));
        }
	}
	public void walk() {
		final SitPlayer sitPlayer = this.plugin.getSittingPlayers().get(this.player.getUniqueId());
		sitPlayer.unSit(false);
	}
	public Player getPlayer() {
		return this.player;
	}
	public boolean isSitting() {
		return this.plugin.getSittingPlayers().containsKey(this.player.getUniqueId());
	}
	public boolean isLay() {
		return this.plugin.getLayingPlayers().containsKey(this.player.getUniqueId());
	}
	public void setLay() {
		if (!this.player.hasPermission("simplelay.lay") && !this.player.hasPermission("simplelay.*")) {
			this.player.sendMessage(this.plugin.prefix + this.plugin.getConfig().getString("lang.noperms"));
			return;
		}
		if (this.player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
			this.player.sendMessage(this.plugin.prefix + this.plugin.getConfig().getString("lay.invisible"));
			return;
		}
		ILayPlayer lp = LayPlayer.getLayPlayerInstance(this.player);
		//PotionEffect effect = this.pl.getPotionEffect(PotionEffectType.INVISIBILITY);
		if (lp == null) {
			this.plugin.getPluginLoader().disablePlugin(this.plugin);
			this.player.sendMessage(this.plugin.prefix + this.plugin.getConfig().getString("lay.unsupported"));
			return;
		}
		lp.lay();
		if (plugin.getConfig().getBoolean("lay.announce.command")) {
        	this.player.sendMessage(plugin.prefix+plugin.getConfig().getString("lang.lay.now"));
        }
	}
	public void unLay() {
		ILayPlayer lp = LayPlayer.getLayPlayerInstance(this.player);
		lp.unLay(plugin.getConfig().getBoolean("lay.announce.command"));
	}
	public static void sitRotation(SimpleLay plugin) {
		for (SitPlayer sitPlayer : plugin.getSittingPlayers().values()) {
			if (sitPlayer.onStairs || sitPlayer.armorStand == null) continue;
			try {
				Location loc = sitPlayer.armorStand.getLocation();
				loc.setYaw(sitPlayer.player.getLocation().getYaw());
				sitPlayer.armorStand.teleport(loc);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
