package com.azerusteam.sirmanager;

import com.azerusteam.players.ILayPlayer;
import com.azerusteam.players.LayPlayer;
import com.azerusteam.players.SirPlayer;
import com.azerusteam.players.SitPlayer;
import com.azerusteam.util.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SimpleLay extends JavaPlugin {
	private final Map<UUID, SitPlayer> seats = new HashMap<>();
	private final Map<UUID, ILayPlayer> lays = new HashMap<>();

	//private Map<UUID, ArmorStand> laysRider = new HashMap<>();
	//public String prefix = ChatColor.WHITE+"["+ChatColor.LIGHT_PURPLE+"Simple Lay"+ChatColor.WHITE+"] "+ChatColor.GOLD;

	public String prefix = ChatColor.GOLD.toString();

	@Override
	public void onEnable() {
		loadConfiguration();
		bStats();
		Bukkit.getServer().getPluginManager().registerEvents(new UnseatListener(), this);
		Bukkit.getPluginCommand("sit").setExecutor(new SitCommand());
		Bukkit.getPluginCommand("lay").setExecutor(new LayCommand());
		Bukkit.getScheduler().runTaskTimer(this, () -> {
			SirPlayer.sitRotation(this);
			SitPlayer.checkSeatBlock();
			LayPlayer.updateArmor(this);;
			LayPlayer.checkSeatBlock(this);
		}, 0L, this.getConfig().getLong("updateEventCheck"));
		
	}
	public void loadConfiguration(){
		this.getConfig().addDefault("lang.lay.already", "You are already laying!");
		this.getConfig().addDefault("lang.sit.already", "You are already sitting!");
		this.getConfig().addDefault("lang.lay.notOnGround", "You can only lay on the ground!");
		this.getConfig().addDefault("lang.sit.notOnGround", "You can only sit on the ground!");
		this.getConfig().addDefault("lang.lay.noLonger", "You are no longer laying.");
		this.getConfig().addDefault("lang.sit.noLonger", "You are no longer sitting.");
		this.getConfig().addDefault("lang.lay.now", "You are now laying.");
		this.getConfig().addDefault("lang.sit.now", "You are now sitting.");
		this.getConfig().addDefault("sit.announce.command", true);
		this.getConfig().addDefault("sit.announce.click", false);
		this.getConfig().addDefault("lay.announce.command", true);
		this.getConfig().addDefault("lay.announce.invisible", true);
		this.getConfig().addDefault("lay.invisible", "You cannot lay down while you are invisible!");
		this.getConfig().addDefault("lang.no-permissions", "You don't have permissions to do that!");
		this.getConfig().addDefault("lang.sit.obstructed", "This place is obstructed!");
		this.getConfig().addDefault("sit.click-distance", 7.0D);
		this.getConfig().addDefault("sit.pitch-limit", 0.0f);
		this.getConfig().addDefault("updateEventCheck", 1L);
		this.getConfig().addDefault("slab-sit", true);
		this.getConfig().addDefault("stairs-sit", true);
		this.getConfig().addDefault("carpet-sit", true);
		
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
	}
	private void bStats() {
		Metrics metrics = new Metrics(this, 7611);
		 metrics.addCustomChart(new Metrics.MultiLineChart("players_and_servers", () -> {
			 Map<String, Integer> valueMap = new HashMap<>();
			 valueMap.put("servers", 1);
			 valueMap.put("players", Bukkit.getOnlinePlayers().size());
			 return valueMap;
		 }));
	}
	@Override
	public void onDisable() {
		for (SitPlayer sitPlayer : seats.values()) {
			sitPlayer.unSit(true);
		}
		for (ILayPlayer layPlayer : lays.values()) {
			layPlayer.unLay(true);
		}
	}

	/**
	 * Returns an <a href="Collection.html#unmodview">unmodifiable view</a> of the map containing sitting players.
	 * @return an unmodifiable view of the map containing sitting players.
	 */
	public Map<UUID, SitPlayer> getSittingPlayers() {
        return Collections.unmodifiableMap(this.seats);
    }

	/**
	 * Adds specified <code>sitPlayer</code> to the map containing sitting players.
	 * @param uuid {@link UUID} of the specified {@link SirPlayer}
	 * @param sitPlayer
	 * @return
	 */
	public Map<UUID, SitPlayer> setSitting(@NotNull UUID uuid, @NotNull SitPlayer sitPlayer){
		this.seats.put(uuid,sitPlayer);
		return this.seats;
	}
	public Map<UUID, SitPlayer> removeSitting(UUID uuid){
		this.seats.remove(uuid);
		return this.seats;
	}
	public Map<UUID, ILayPlayer> getLayingPlayers() {
        return this.lays;
    }
	public Map<UUID, ILayPlayer> setLay(UUID uuid, ILayPlayer lp){
		this.lays.put(uuid,lp);
		return this.lays;
	}
	public Map<UUID, ILayPlayer> removeLaying(UUID uuid){
		this.lays.remove(uuid);
		return this.lays;
	}

	@NotNull
	public static SimpleLay getInstance() {
		return JavaPlugin.getPlugin(SimpleLay.class);
	}
}
