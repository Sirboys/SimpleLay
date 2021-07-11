package ru.azerusteam.Classes;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import ru.azerusteam.sirmanager.Main;
public class SirPlayer {
	private Player pl;
	private Main plugin;
	public SirPlayer(final Player player) {
		this.pl = player;
		this.plugin = (Main) JavaPlugin.getPlugin((Class)Main.class);
	}
	public void setSit(boolean stairs,Location loc,boolean announce){
		if (!pl.hasPermission("simplelay.sit") && !pl.hasPermission("simplelay.*")) {
			pl.sendMessage(plugin.prefix+plugin.getConfig().getString("lang.noperms"));
			return;
		}
		final Location location = this.pl.getLocation();
		final ArmorStand seat = (ArmorStand)location.getWorld().spawn(loc.clone().subtract(0.0, 0.2, 0.0), ArmorStand.class, b -> {
			b.setGravity(false);
            b.setMarker(true);
            b.setSmall(true);
            b.setVisible(false);
            b.addPassenger((Entity) pl);
            b.setCollidable(false);
			
	        b.addScoreboardTag("simpleLay");
		});
        SitPlayer sp = new SitPlayer(this.pl);
        sp.setSit(seat,stairs,location,announce);
        if (announce) {
        	this.pl.sendMessage(plugin.prefix+plugin.getConfig().getString("lang.sit.now"));
        }
	}
	public void walk() {
		final SitPlayer sitPlayer = this.plugin.getSeats().get(this.pl.getUniqueId());
		sitPlayer.unSit(false);
	}
	public Player getPlayer() {
		return this.pl;
	}
	public boolean isSitting() {
		return this.plugin.getSeats().containsKey(this.pl.getUniqueId());
	}
	public boolean isLay() {
		return this.plugin.getLays().containsKey(this.pl.getUniqueId());
	}
	public void setLay() {
		if (!pl.hasPermission("simplelay.lay") && !pl.hasPermission("simplelay.*")) {
			pl.sendMessage(plugin.prefix+plugin.getConfig().getString("lang.noperms"));
			return;
		}
		if (this.pl.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
			pl.sendMessage(plugin.prefix+plugin.getConfig().getString("lay.invisible"));
			return;
		}
		ILayPlayer lp = (ILayPlayer) LayPlayer.getLayPlayerInstance(this.pl);
		//PotionEffect effect = this.pl.getPotionEffect(PotionEffectType.INVISIBILITY);
		if (lp == null) {
			plugin.getPluginLoader().disablePlugin(plugin);
			pl.sendMessage(plugin.prefix+plugin.getConfig().getString("lay.unsupported"));
			return;
		}
		lp.lay();
		//this.pl.removePotionEffect(PotionEffectType.INVISIBILITY);
        //this.pl.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 25122001, 244, true));
        if (plugin.getConfig().getBoolean("lay.announce.command")) {
        	this.pl.sendMessage(plugin.prefix+plugin.getConfig().getString("lang.lay.now"));
        }
	}
	public void unLay() {
		ILayPlayer lp = (ILayPlayer) LayPlayer.getLayPlayerInstance(this.pl);
		lp.unLay(plugin.getConfig().getBoolean("lay.announce.command"));
	}
	public static void sitRotation(Main plugin) {
		Collection<?> sitRot = plugin.getSeats().values();
		for (Iterator<?> iterator = sitRot.iterator(); iterator.hasNext();) {
			SitPlayer sPlayer = (SitPlayer) iterator.next();
			ArmorStand as = sPlayer.armorstand;
			if (sPlayer.stairs) continue;
			if (as == null) continue;
	        try {
	            final Object entityArmorstand = as.getClass().getMethod("getHandle", (Class<?>[])new Class[0]).invoke(as, new Object[0]);
	            final Field yaw = entityArmorstand.getClass().getField("yaw");
	            yaw.set(entityArmorstand, sPlayer.player.getLocation().getYaw());
	        }
	        catch (Exception ex2) {
        		ex2.printStackTrace();
        	}
		}
	}
}
