package com.azerusteam.sirmanager;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.azerusteam.players.SirPlayer;
import com.azerusteam.players.SitPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Stairs.Shape;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import com.azerusteam.players.LayPlayer;

public class UnseatListener implements Listener{
	private final SimpleLay plugin;
	public UnseatListener() {
		this.plugin = (SimpleLay) JavaPlugin.getPlugin(SimpleLay.class);
	}
	@EventHandler
    public void on(final PlayerTeleportEvent e) {
        final SirPlayer player = new SirPlayer((Player) e.getPlayer());
        if (player.isSitting()) {
        	player.walk();
        }
        if (player.isLay()) {
        	player.unLay();
        }
    }
	@EventHandler
	public void on(final PlayerQuitEvent e) {
		SirPlayer player = new SirPlayer(e.getPlayer());
        if (player.isSitting()) {
        	player.walk();
        }
        if (player.isLay()) {
        	player.unLay();
        	
        }
	}
	@EventHandler
	public void on(final PlayerDeathEvent e) {
		SirPlayer player = new SirPlayer(e.getEntity().getPlayer());
		if (player.isLay()) {
			player.unLay();
		}
		if (player.isSitting()) {
			player.walk();
		}
	}
	@EventHandler(priority = EventPriority.LOW)
	public void on(final EntityDamageByEntityEvent e) {
		Entity entity = e.getEntity();
		if (e.getDamager() instanceof Player && entity instanceof ArmorStand) {
			ArmorStand as = (ArmorStand) entity;
			List<Entity> riders = as.getPassengers();
			if (riders.size() == 0) return;
			Entity rider = riders.get(0);
			if (rider instanceof Player) {
				SirPlayer sp = new SirPlayer((Player) rider);
				if (!sp.isLay()) return;
				if (!(((Player) rider).getGameMode() == GameMode.ADVENTURE || ((Player) rider).getGameMode() == GameMode.SURVIVAL)) return;
				((Player) rider).damage(e.getFinalDamage());
				plugin.getLayingPlayers().get(rider.getUniqueId()).unLay(plugin.getConfig().getBoolean("lay.announce.command"));
			}
		}
	}
	
	@EventHandler
	public void onJoin(final PlayerJoinEvent e) {
		plugin.getServer().getScheduler().runTaskLater(plugin, () -> LayPlayer.showLayingPlayers(plugin, e.getPlayer()), 10L);
	}
	@EventHandler
	public void onRespawn(final PlayerRespawnEvent e) {
		SirPlayer player = new SirPlayer(e.getPlayer());
		if (player.isSitting()) {
			player.walk();
		}
		if (player.isLay()) {
			player.unLay();
		}
		plugin.getServer().getScheduler().runTaskLater(plugin, () -> LayPlayer.showLayingPlayers(plugin, e.getPlayer()), 10L);
	}
	@EventHandler
	public void onClick(final PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (e.getHand() != EquipmentSlot.HAND) return; 
		if (e.getBlockFace() != BlockFace.UP) return;
		if (e.useInteractedBlock() == Result.DENY && e.useItemInHand() == Result.DENY) return;
		if (!(e.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR)) return;
		if (Objects.requireNonNull(e.getClickedBlock()).getLocation().add(0, 1, 0).getBlock().getType() != Material.AIR) return;
		if (e.getPlayer().isSneaking()) return;
		if (e.getPlayer().getLocation().getPitch() < plugin.getConfig().getDouble("sit.pitch-limit")) return;
		if (e.getPlayer().getLocation().distance(e.getClickedBlock().getLocation()) > plugin.getConfig().getDouble("sit.click-distance")) return;
		boolean obstracted = false;
		Collection<SitPlayer> sPlayers = plugin.getSittingPlayers().values();
		for (SitPlayer sitPlayer : sPlayers) {
			if (sitPlayer.player.getUniqueId() == e.getPlayer().getUniqueId()) {
				continue;
			}
			if (sitPlayer.armorStand.getLocation().getBlock().getLocation().getBlockX() == e.getClickedBlock().getLocation().getBlockX() && sitPlayer.armorStand.getLocation().getBlock().getLocation().getBlockY() == e.getClickedBlock().getLocation().getBlockY() && sitPlayer.armorStand.getLocation().getBlock().getLocation().getBlockZ() == e.getClickedBlock().getLocation().getBlockZ()) {
				obstracted = true;
				break;
			}
		}
		if (obstracted) return;
		//if (!plugin.getConfig().getBoolean("click-sit")) return;
		if (e.getClickedBlock().getType().toString().matches(".*_SLAB") && plugin.getConfig().getBoolean("slab-sit") && ((Slab) e.getClickedBlock().getBlockData()).getType() == Type.BOTTOM) {
				SirPlayer sp = new SirPlayer(e.getPlayer());
				if (!sp.isSitting()) {
					Location loc = e.getClickedBlock().getLocation().add(0.5, 0.5, 0.5);
					loc.setDirection(e.getPlayer().getLocation().getDirection());
					//e.getPlayer().teleport(loc);
					sp.setSit(false,loc,plugin.getConfig().getBoolean("sit.announce.click"));
				}
			}
		if (e.getClickedBlock().getType().toString().matches(".*_CARPET") && this.plugin.getConfig().getBoolean("carpet-sit")){
        	final SirPlayer sp = new SirPlayer(e.getPlayer());
            if (!sp.isSitting()) {
                final Location loc = e.getClickedBlock().getLocation().add(0.5, 0.1, 0.5);
                loc.setDirection(e.getPlayer().getLocation().getDirection());
                sp.setSit(false, loc, this.plugin.getConfig().getBoolean("sit.announce.click"));
            }
        }
		if (e.getClickedBlock().getType().toString().matches(".*_STAIRS") && plugin.getConfig().getBoolean("stairs-sit") && ((Stairs) e.getClickedBlock().getBlockData()).getHalf() == Half.BOTTOM) {
				SirPlayer sp = new SirPlayer(e.getPlayer());
				if (!sp.isSitting()) {
					Location loc = e.getClickedBlock().getLocation().add(0.5, 0.5, 0.5);
					loc.setPitch(e.getPlayer().getLocation().getPitch());
					loc.setYaw(e.getPlayer().getLocation().getYaw());
					Stairs st = (Stairs) e.getClickedBlock().getBlockData();
					if ((st.getShape() == Shape.STRAIGHT) && (st.getFacing() == BlockFace.EAST)) {
						loc.setYaw(90);
						loc.add(-0.2,0,0);
					}else if ((st.getShape() == Shape.STRAIGHT) && (st.getFacing() == BlockFace.WEST)) {
						loc.setYaw(-90);
						loc.add(0.2,0,0);
					}else if ((st.getShape() == Shape.STRAIGHT) && (st.getFacing() == BlockFace.SOUTH)){
						loc.setYaw(180);
						loc.add(0,0,-0.2);
					}else if ((st.getShape() == Shape.STRAIGHT) && (st.getFacing() == BlockFace.NORTH)) {
						loc.setYaw(0);
						loc.add(0,0,0.2);
					}else if ((st.getFacing() == BlockFace.NORTH && st.getShape() == Shape.OUTER_RIGHT) || (st.getFacing() == BlockFace.EAST && st.getShape() == Stairs.Shape.OUTER_LEFT) || (st.getFacing() == BlockFace.NORTH && st.getShape() == Stairs.Shape.INNER_RIGHT) || (st.getFacing() == BlockFace.EAST && st.getShape() == Stairs.Shape.INNER_LEFT)) {
                        loc.setYaw(45);
						loc.add(-0.2,0,0.2);
                    }
                    else if ((st.getFacing() == BlockFace.NORTH && st.getShape() == Stairs.Shape.OUTER_LEFT) || (st.getFacing() == BlockFace.WEST && st.getShape() == Stairs.Shape.OUTER_RIGHT) || (st.getFacing() == BlockFace.NORTH && st.getShape() == Stairs.Shape.INNER_LEFT) || (st.getFacing() == BlockFace.WEST && st.getShape() == Stairs.Shape.INNER_RIGHT)) {
                        loc.setYaw(-45);
                    	loc.add(0.2,0,0.2);
                    }
                    else if ((st.getFacing() == BlockFace.SOUTH && st.getShape() == Stairs.Shape.OUTER_RIGHT) || (st.getFacing() == BlockFace.WEST && st.getShape()  == Stairs.Shape.OUTER_LEFT) || (st.getFacing() == BlockFace.SOUTH && st.getShape() == Stairs.Shape.INNER_RIGHT) || (st.getFacing() == BlockFace.WEST && st.getShape() == Stairs.Shape.INNER_LEFT)) {
                        loc.setYaw(-135);
                    	loc.add(0.2,0,-0.2);
                    }
                    else if ((st.getFacing() == BlockFace.SOUTH && st.getShape() == Stairs.Shape.OUTER_LEFT) || (st.getFacing() == BlockFace.EAST && st.getShape()  == Stairs.Shape.OUTER_RIGHT) || (st.getFacing() == BlockFace.SOUTH && st.getShape() == Stairs.Shape.INNER_LEFT) || (st.getFacing() == BlockFace.EAST && st.getShape() == Stairs.Shape.INNER_RIGHT)) {
                        loc.setYaw(135);
                    	loc.add(-0.2,0,-0.2);
                    }
					sp.setSit(true,loc,plugin.getConfig().getBoolean("sit.announce.click"));
					
				}
		}
		
	}
}
