package com.azerusteam.layhandler;

import com.azerusteam.players.ILayPlayer;
import com.azerusteam.sirmanager.SimpleLay;
import com.azerusteam.wrappers.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.NumberConversions;

import java.util.Collections;

public class LayPlayer_v1_14_R1 implements ILayPlayer {
	private int fakePlayerID;
	private ArmorStand rider;
	private final SimpleLay plugin;
	private final Player player;
	private EntityPlayer fakePlayer;
	private float rotation;

	public LayPlayer_v1_14_R1(Player player) {
		this.plugin = SimpleLay.getInstance();
		this.player = player;
	}

	public void lay() {
		CraftPlayer craftPlayer = CraftPlayer.wrap(player);
		EntityPlayer entityPlayer = craftPlayer.getHandle();
		GameProfile gameProfile = new GameProfile(player.getUniqueId(), player.getName());
		try {
			Property textures = entityPlayer.getProfile().getProperties().get("textures").toArray(new Property[0])[0];
			gameProfile.getProperties().put("textures", new Property("textures", textures.getValue(), textures.getSignature()));
		} catch (Exception ignored) {}
		WorldServer worldServer = CraftWorld.wrap(player.getWorld()).getHandle();
		MinecraftServer minecraftServer = CraftServer.wrap(Bukkit.getServer()).getServer();
		this.fakePlayer = new EntityPlayer(minecraftServer, worldServer, gameProfile, new PlayerInteractManager(worldServer));

		int id = entityPlayer.getId();
		this.fakePlayerID = this.fakePlayer.getId();

		Location loc = player.getLocation();
		this.fakePlayer.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), player.getEyeLocation().getPitch());

		this.rotation = player.getLocation().getYaw();
		BlockData bedBlockData = this.bedBlockData();
		Packet relMoveLook = new PacketPlayOutRelEntityMoveLook(this.fakePlayerID, (short) 0, (short) 2, (short) 0, (byte) 0, (byte) 0, true);
		Packet namedEntitySpawn = new PacketPlayOutNamedEntitySpawn(this.fakePlayer);
		this.fakePlayer.setCustomNameVisible(false);
		Packet playerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, this.fakePlayer);

		Location bukkitBedLocation = new Location(player.getWorld(), player.getLocation().getX(), 0, player.getLocation().getZ());
		BlockPosition nmsBedLocation = new BlockPosition(bukkitBedLocation);
		Packet entityDestroy = new PacketPlayOutEntityDestroy(id);

		ScoreboardTeam team = new ScoreboardTeam((Scoreboard) minecraftServer.getScoreboard(), "SimpleLay_TEAM");
		//fixme		.setNameTagVisibility(ScoreboardTeam.EnumNameTagVisibility.NEVER);
		Packet scoreboardTeam1 = new PacketPlayOutScoreboardTeam(team, 0), scoreboardTeam2 = new PacketPlayOutScoreboardTeam(team, Collections.singletonList(player.getName()), 3);
		this.fakePlayer.setPose(EntityPose.SLEEPING);
		this.fakePlayer.setBedPosition(nmsBedLocation);
		try {
			DataWatcher watcher = entityPlayer.getDataWatcher(), fakeWatcher = this.fakePlayer.getDataWatcher();
		    Packet entityMetadata = new PacketPlayOutEntityMetadata(this.fakePlayerID, fakeWatcher, false);
			player.hidePlayer(plugin, player);
			for (Player onlinePlayer : player.getServer().getOnlinePlayers()) {
				EntityPlayer entityPlayer1 = CraftPlayer.wrap(onlinePlayer).getHandle();
				PlayerConnection playerConnection = entityPlayer1.getPlayerConnection();
				playerConnection.sendPacket(namedEntitySpawn);
				if (!entityPlayer1.equals(entityPlayer)) {
					playerConnection.sendPacket(playerInfo);
				}
				if (entityPlayer1.equals(entityPlayer)) {
					playerConnection.sendPacket(new PacketPlayOutEntityEffect(id, new MobEffect(MobEffects.INVISIBILITY, 25122001, 0, false, false)));
					entityPlayer1.setInvisible(true);
				}
				playerConnection.sendPacket(namedEntitySpawn, entityMetadata);
				onlinePlayer.sendBlockChange(bukkitBedLocation, bedBlockData);
				playerConnection.sendPacket(relMoveLook);
				if (!entityPlayer1.equals(entityPlayer))
					playerConnection.sendPacket(entityDestroy);
				else
					playerConnection.sendPacket(scoreboardTeam1, scoreboardTeam2);
			}
        } catch (Exception e) {
        	e.printStackTrace();
        }
        final Location location = player.getLocation();
        this.rider = location.getWorld().spawn(location.clone().subtract(0, 1.7, 0), ArmorStand.class);
        this.rider.setGravity(false);
        this.rider.setVisible(false);
        this.rider.addPassenger(player);
        this.plugin.setLay(this.getPlayer().getUniqueId(), this);
	}
	public void unLay(boolean announceToPlayer) {
		EntityPlayer entityPlayer = CraftPlayer.wrap(this.player).getHandle();
		Packet entityDestroy = new PacketPlayOutEntityDestroy(this.plugin.getLayingPlayers().get(this.player.getUniqueId()).getFakePlayerID());
		Packet namedEntitySpawn = new PacketPlayOutNamedEntitySpawn(entityPlayer);
		Packet playerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);
		DataWatcher watcher = entityPlayer.getDataWatcher();
	    watcher.set(DataWatcherRegistry.BYTE.createAccessor(16), watcher.get(DataWatcherRegistry.BYTE.createAccessor(16)));
		Packet entityMetadata = new PacketPlayOutEntityMetadata(entityPlayer.getId(), watcher, true);
		Packet entityHeadRotation = new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) NumberConversions.floor(entityPlayer.getHeadRotation() * 256.0F / 360.0F));
		Packet[] equipmentPackets = ILayPlayer.getEquipmentPackets(player, fakePlayerID);
		MobEffect ppl = entityPlayer.getEffect(MobEffects.INVISIBILITY);
		this.player.showPlayer(plugin, player);
		entityPlayer.getPlayerConnection().sendPacket(new PacketPlayOutRemoveEntityEffect(entityPlayer.getId(), MobEffects.INVISIBILITY));
		if (ppl != null)
			entityPlayer.getPlayerConnection().sendPacket(new PacketPlayOutEntityEffect(entityPlayer.getId(), new MobEffect(ppl)) );
		else
			entityPlayer.setInvisible(false);

        for (Player player : this.player.getServer().getOnlinePlayers()) {
			Location loc = this.player.getLocation();
			loc.setY(0);
			player.sendBlockChange(loc, loc.getBlock().getBlockData());
			EntityPlayer entityPlayer1 = CraftPlayer.wrap(player).getHandle();
			PlayerConnection playerConnection = entityPlayer1.getPlayerConnection();
			playerConnection.sendPacket(entityDestroy);
			if (!entityPlayer1.equals(entityPlayer)) {
				playerConnection.sendPacket(namedEntitySpawn, playerInfo, entityMetadata, entityHeadRotation);
				playerConnection.sendPacket(equipmentPackets);
			}
		}
        ArmorStand seat = this.plugin.getLayingPlayers().get(this.player.getUniqueId()).getRider();
        seat.remove();
        this.plugin.removeLaying(this.player.getUniqueId());
        if (announceToPlayer) {
			this.player.sendMessage(plugin.prefix + plugin.getConfig().getString("lang.lay.noLonger"));
		}
        if (player.isDead()) return;
        player.teleport(seat.getLocation().add(0, 1.7, 0).setDirection(player.getLocation().getDirection()));
		Bukkit.getScheduler().runTaskLater(plugin, () -> player.teleport(seat.getLocation().add(0, 1.7, 0).setDirection(player.getLocation().getDirection())), 1L);
	}

	@Override
	public int getFakePlayerID() {
		return fakePlayerID;
	}

	@Override
	public ArmorStand getRider() {
		return rider;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public EntityPlayer getFakePlayer() {
		return fakePlayer;
	}

	@Override
	public float getRotation() {
		return rotation;
	}

	public static void checkSeatBlock(SimpleLay plugin) { // not unused; used using reflection
		//Collection<LayPlayer> spList = plugin.getLays().values();
		for (ILayPlayer iLayPlayer : plugin.getLayingPlayers().values()) {
			if (iLayPlayer == null) continue;
			if (iLayPlayer.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY)) {
				iLayPlayer.getPlayer().sendMessage(plugin.prefix+plugin.getConfig().getString("lay.invisible"));
				iLayPlayer.unLay(false);
				continue;
			}
			if (iLayPlayer.getRider().getLocation().subtract(0, -1.6, 0).getBlock().getType() != Material.AIR) continue;
			iLayPlayer.unLay(plugin.getConfig().getBoolean("lay.announce.command"));
		}
	}

	public static void upDateArmor(SimpleLay plugin) { // not unused; used using reflection
		plugin.getServer().getOnlinePlayers().stream().map(CraftPlayer::wrap).map(CraftPlayer::getHandle).forEach(entityPlayer -> plugin.getLayingPlayers().values().forEach(iLayPlayer -> {
			EntityPlayer p = CraftPlayer.wrap(iLayPlayer.getPlayer()).getHandle();
			PlayerConnection playerConnection = entityPlayer.getPlayerConnection();
			playerConnection.sendPacket(ILayPlayer.getEquipmentPackets(p, iLayPlayer.getFakePlayerID()));
			Location loc = iLayPlayer.getPlayer().getLocation();
			loc.setY(0);
			entityPlayer.getBukkitEntity().sendBlockChange(loc, iLayPlayer.bedBlockData());
			if (entityPlayer.equals(p))
				p.setInvisible(true);
			else
				playerConnection.sendPacket(new PacketPlayOutEntityDestroy(p.getId()));
		}));
	}
	public static void showLayingPlayers(SimpleLay plugin, Player watcherPlayer) { // not unused; used using reflection
		for (ILayPlayer iLayPlayer : plugin.getLayingPlayers().values()) {
			EntityPlayer entityPlayer = CraftPlayer.wrap(iLayPlayer.getPlayer()).getHandle();
			Packet relMoveLook = new PacketPlayOutRelEntityMoveLook(iLayPlayer.getFakePlayerID(), (short) 0, (short) 2, (short) 0, (byte) 0, (byte) 0, true);
			Packet namedEntitySpawn = new PacketPlayOutNamedEntitySpawn(iLayPlayer.getFakePlayer());
			Packet playerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, iLayPlayer.getFakePlayer());
			Location loc = iLayPlayer.getPlayer().getLocation();
			loc.setY(0);
			BlockData blockData = iLayPlayer.bedBlockData();
			Packet entityDestroy = new PacketPlayOutEntityDestroy(entityPlayer.getId());
			try {
				Packet entityMetadata = new PacketPlayOutEntityMetadata(iLayPlayer.getFakePlayerID(), iLayPlayer.getFakePlayer().getDataWatcher(), true);
				PlayerConnection playerConnection = CraftPlayer.wrap(watcherPlayer).getHandle().getPlayerConnection();
				playerConnection.sendPacket(entityDestroy, namedEntitySpawn, playerInfo, entityMetadata);
				watcherPlayer.sendBlockChange(loc, blockData);
				playerConnection.sendPacket(relMoveLook);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
