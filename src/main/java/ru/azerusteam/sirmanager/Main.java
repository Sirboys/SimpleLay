package ru.azerusteam.sirmanager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import ru.azerusteam.Classes.ILayPlayer;
import ru.azerusteam.Classes.LayPlayer;
import ru.azerusteam.Classes.SirPlayer;
import ru.azerusteam.Classes.SitPlayer;
import ru.azerusteam.util.Metrics;

public class Main extends JavaPlugin{
	private final Map<UUID, SitPlayer> seats = new HashMap<>();
	private final Map<UUID, ILayPlayer> lays = new HashMap<>();
	private final int pluginID = 7611;
	//private Map<UUID, ArmorStand> laysRider = new HashMap<>();
	//public String prefix = ChatColor.WHITE+"["+ChatColor.LIGHT_PURPLE+"Simple Lay"+ChatColor.WHITE+"] "+ChatColor.GOLD;
	public String prefix = ChatColor.GOLD.toString();
	@Override
	public void onEnable() {
		System.out.println(Bukkit.getBukkitVersion());

		loadConfiguration();
		bStats();
		Bukkit.getServer().getPluginManager().registerEvents(new UnseatListener(), this);
		Bukkit.getPluginCommand("sit").setExecutor(new SitCommand());
		Bukkit.getPluginCommand("lay").setExecutor(new LayCommand());
		Main plugin = this;
		Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
			@Override
			public void run() {
				SirPlayer.sitRotation(plugin);
				SitPlayer.checkSeatBlock(plugin);
				LayPlayer.upDateArmor(plugin);;
				LayPlayer.checkSeatBlock(plugin);
			}
		}, 0L, plugin.getConfig().getLong("updateEventCheck"));
		
	}
	public void loadConfiguration(){
		this.getConfig().addDefault("lang.lay.already", "You are already laying!");
		this.getConfig().addDefault("lang.sit.already", "You are already siting!");
		this.getConfig().addDefault("lang.lay.notOnGround", "You can lay only on ground!");
		this.getConfig().addDefault("lang.sit.notOnGround", "You can sit only on ground!");
		this.getConfig().addDefault("lang.lay.noLonger", "You are no longer laying!");
		this.getConfig().addDefault("lang.sit.noLonger", "You are no longer siting!");
		this.getConfig().addDefault("lang.lay.now", "You are laying now!");
		this.getConfig().addDefault("lang.sit.now", "You are siting now!");
		this.getConfig().addDefault("sit.announce.command", true);
		this.getConfig().addDefault("sit.announce.click", false);
		this.getConfig().addDefault("lay.announce.command", true);
		this.getConfig().addDefault("lay.announce.invisible", true);
		this.getConfig().addDefault("lay.invisible", "You can't lay down while you're invisible!");
		this.getConfig().addDefault("lang.noperms", "You don't have permissions to do that!");
		//this.getConfig().addDefault("lang.sit.obstracted", "This block is !");
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
		 Metrics metrics = new Metrics(this, pluginID);
		 metrics.addCustomChart(new Metrics.MultiLineChart("players_and_servers", new Callable<Map<String, Integer>>() {
		        @Override
		        public Map<String, Integer> call() throws Exception {
		            Map<String, Integer> valueMap = new HashMap<>();
		            valueMap.put("servers", 1);
		            valueMap.put("players", Bukkit.getOnlinePlayers().size());
		            return valueMap;
		        }
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
	public Map<UUID, SitPlayer> getSeats() {
        return this.seats;
    }
	public Map<UUID, SitPlayer> setSeat(UUID uuid, SitPlayer sitPlayer){
		this.seats.put(uuid,sitPlayer);
		return this.seats;
	}
	public Map<UUID, SitPlayer> removeSeat(UUID uuid){
		this.seats.remove(uuid);
		return this.seats;
	}
	public Map<UUID, ILayPlayer> getLays() {
        return this.lays;
    }
	public Map<UUID, ILayPlayer> setLay(UUID uuid, ILayPlayer lp){
		this.lays.put(uuid,lp);
		return this.lays;
	}
	public Map<UUID, ILayPlayer> removeLay(UUID uuid){
		this.lays.remove(uuid);
		return this.lays;
	}
}
