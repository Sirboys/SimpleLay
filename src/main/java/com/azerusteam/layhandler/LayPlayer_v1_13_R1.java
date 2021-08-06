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

public class LayPlayer_v1_13_R1 implements ILayPlayer {
	private int fakePlayerID;
	private ArmorStand rider;
	private final SimpleLay plugin;
	private final Player player;
	private EntityPlayer fakePlayer;
	private float rotation;

	public LayPlayer_v1_13_R1(Player player) {
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
		System.out.println(this.fakePlayerID);
		Location loc = player.getLocation();
		this.fakePlayer.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), player.getEyeLocation().getPitch());

        this.rotation = player.getLocation().getYaw();
        BlockData bedBlockData = this.bedBlockData();
        Packet relMoveLook = new PacketPlayOutRelEntityMoveLook(this.fakePlayerID, (long) 0, (long) 2, (long) 0, (byte) 0, (byte) 0, true);
		Packet namedEntitySpawn = new PacketPlayOutNamedEntitySpawn(this.fakePlayer);
		Packet playerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, this.fakePlayer);
		this.fakePlayer.setCustomNameVisible(false);

		Location bukkitBedLocation = new Location(player.getWorld(), player.getLocation().getX(), 0, player.getLocation().getZ());
		BlockPosition nmsBedLocation = new BlockPosition(bukkitBedLocation);
		Packet entityDestroy = new PacketPlayOutEntityDestroy(id);

		ScoreboardTeam team = new ScoreboardTeam((Scoreboard) minecraftServer.getScoreboard(), "SimpleLay_TEAM");
		//fixme		.setNameTagVisibility(ScoreboardTeam.EnumNameTagVisibility.NEVER);
		Packet scoreboardTeam1 = new PacketPlayOutScoreboardTeam(team, 0),
				scoreboardTeam2 = new PacketPlayOutScoreboardTeam(team, Collections.singletonList(player.getName()), 3);

		Packet bed = new PacketPlayOutBed(this.fakePlayer, nmsBedLocation);
		DataWatcher watcher = entityPlayer.getDataWatcher(), fakeWatcher = this.fakePlayer.getDataWatcher();
		fakeWatcher.set(DataWatcherRegistry.BYTE.createAccessor(13), watcher.get(DataWatcherRegistry.BYTE.createAccessor(13)));
		Packet entityMetadata = new PacketPlayOutEntityMetadata(this.fakePlayerID, fakeWatcher, false);
		this.rotation = this.player.getLocation().getYaw();
		for (Player onlinePlayer : player.getServer().getOnlinePlayers()) {
			EntityPlayer entityPlayer1 = CraftPlayer.wrap(onlinePlayer).getHandle();
			PlayerConnection playerConnection = entityPlayer1.getPlayerConnection();
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
			Bukkit.getScheduler().runTaskLater(plugin, () -> playerConnection.sendPacket(bed), 1L);
		}
        final Location location = player.getLocation();
        this.rider = location.getWorld().spawn(location.clone().subtract(0, 1.7, 0), ArmorStand.class);
        this.rider.setGravity(false);
        this.rider.setVisible(false);
        this.rider.addPassenger(player);
        this.plugin.setLay(this.getPlayer().getUniqueId(),this);
	}
	public void unLay(boolean announceToPlayer) {
		EntityPlayer sitEntityPlayer = CraftPlayer.wrap(this.player).getHandle();
		int fakePlayerId = this.plugin.getLayingPlayers().get(this.player.getUniqueId()).getFakePlayer().getId();

		Packet entityDestroy = new PacketPlayOutEntityDestroy(fakePlayerId);

		Packet namedEntitySpawn = new PacketPlayOutNamedEntitySpawn(sitEntityPlayer);
		Packet playerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, sitEntityPlayer);
		DataWatcher watcher = sitEntityPlayer.getDataWatcher();
	    watcher.set(DataWatcherRegistry.BYTE.createAccessor(13), watcher.get(DataWatcherRegistry.BYTE.createAccessor(13)));
		Packet entityMetadata = new PacketPlayOutEntityMetadata(sitEntityPlayer.getId(), watcher, true);
		Packet entityHeadRotation = new PacketPlayOutEntityHeadRotation(sitEntityPlayer, (byte) NumberConversions.floor(sitEntityPlayer.getHeadRotation() * 256.0F / 360.0F));
		Packet[] equipmentPackets = ILayPlayer.getEquipmentPackets(player, fakePlayerID);
		MobEffect mobEffectInstance = sitEntityPlayer.getEffect(MobEffects.INVISIBILITY);
		this.player.showPlayer(plugin, player);
		sitEntityPlayer.getPlayerConnection().sendPacket(new PacketPlayOutRemoveEntityEffect(sitEntityPlayer.getId(), MobEffects.INVISIBILITY));
		if (mobEffectInstance != null)
			sitEntityPlayer.getPlayerConnection().sendPacket(new PacketPlayOutEntityEffect(sitEntityPlayer.getId(), new MobEffect(mobEffectInstance)));
		else
			sitEntityPlayer.setInvisible(false);
		for (Player player : this.player.getServer().getOnlinePlayers()) {
			Location loc = this.player.getLocation();
			loc.setY(0);
			EntityPlayer entityPlayer = CraftPlayer.wrap(player).getHandle();
			PlayerConnection playerConnection = entityPlayer.getPlayerConnection();
			playerConnection.sendPacket(entityDestroy);
			if (!entityPlayer.getBukkitEntity().getUniqueId().equals(player.getUniqueId())) {
				playerConnection.sendPacket(playerInfo, namedEntitySpawn, entityMetadata, entityHeadRotation);
				playerConnection.sendPacket(equipmentPackets);
				System.out.println("someone get this packet");
			}
			player.sendBlockChange(loc, loc.getBlock().getBlockData());
		}
        ArmorStand seat = this.plugin.getLayingPlayers().get(this.player.getUniqueId()).getRider();
        seat.remove();
        this.plugin.removeLaying(this.player.getUniqueId());
        if (announceToPlayer) this.player.sendMessage(plugin.prefix + plugin.getConfig().getString("lang.lay.noLonger"));
        if (player.isDead()) return;
        player.teleport(seat.getLocation().add(0, 1.7, 0).setDirection(player.getLocation().getDirection()));
		Bukkit.getScheduler().runTaskLater(plugin, () -> player.teleport(seat.getLocation().add(0, 1.7, 0).setDirection(player.getLocation().getDirection())), 1L);
	}

	@Override
	public int getFakePlayerID() {
		return this.fakePlayerID;
	}

	@Override
	public ArmorStand getRider() {
		return this.rider;
	}

	@Override
	public Player getPlayer() {
		return this.player;
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
		for (ILayPlayer ilayPlayer : plugin.getLayingPlayers().values()) {
			//LayPlayer layPlayer = (LayPlayer) iterator.next();
			LayPlayer_v1_13_R1 layPlayer = (LayPlayer_v1_13_R1) ilayPlayer;
			if (layPlayer == null) continue;
			if (layPlayer.player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
				layPlayer.player.sendMessage(plugin.prefix + plugin.getConfig().getString("lay.invisible"));
				layPlayer.unLay(false);
				continue;
			}
			if (layPlayer.rider.getLocation().subtract(0, -1.6, 0).getBlock().getType() != Material.AIR) continue;
			layPlayer.unLay(plugin.getConfig().getBoolean("lay.announce.command"));
		}
	}

	public static void upDateArmor(SimpleLay plugin) { // not unused; used using reflection
		plugin.getServer().getOnlinePlayers().stream().map(CraftPlayer::wrap).map(CraftPlayer::getHandle).forEach(entityPlayer -> plugin.getLayingPlayers().values().forEach(iLayPlayer -> {
			LayPlayer_v1_13_R1 layPlayer = (LayPlayer_v1_13_R1) iLayPlayer;
			EntityPlayer p = CraftPlayer.wrap(layPlayer.player).getHandle();
			PlayerConnection playerConnection = entityPlayer.getPlayerConnection();
			playerConnection.sendPacket(ILayPlayer.getEquipmentPackets(p, layPlayer.fakePlayerID));
			Location loc = layPlayer.getPlayer().getLocation();
			loc.setY(0);
			entityPlayer.getBukkitEntity().sendBlockChange(loc, layPlayer.bedBlockData());
			if (!entityPlayer.equals(p)) playerConnection.sendPacket(new PacketPlayOutEntityDestroy(p.getId()));
		}));
	}
	public static void showLayingPlayers(SimpleLay plugin, Player watcherPlayer) { // not unused; used using reflection
		for (ILayPlayer iLayPlayer : plugin.getLayingPlayers().values()) {
			LayPlayer_v1_13_R1 layPlayer = (LayPlayer_v1_13_R1) iLayPlayer;
			EntityPlayer entityPlayer = CraftPlayer.wrap(layPlayer.getPlayer()).getHandle();
			Packet relMoveLook = new PacketPlayOutRelEntityMoveLook(layPlayer.fakePlayer.getId(), (short) 0, (short) 2, (short) 0, (byte) 0, (byte) 0, true);
			Packet namedEntitySpawn = new PacketPlayOutNamedEntitySpawn(layPlayer.fakePlayer);
			Packet playerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, layPlayer.fakePlayer);
			Location loc = layPlayer.getPlayer().getLocation();
			loc.setY(0);
			BlockData blockData = layPlayer.bedBlockData();
			Packet entityDestroy = new PacketPlayOutEntityDestroy(entityPlayer.getId());
			Packet bed = new PacketPlayOutBed(layPlayer.fakePlayer, new BlockPosition(loc));
			try {
				Packet entityMetadata = new PacketPlayOutEntityMetadata(layPlayer.fakePlayer.getId(), layPlayer.fakePlayer.getDataWatcher(), true);
				PlayerConnection playerConnection = CraftPlayer.wrap(watcherPlayer).getHandle().getPlayerConnection();
				playerConnection.sendPacket(entityDestroy, namedEntitySpawn, playerInfo, entityMetadata);
				watcherPlayer.sendBlockChange(loc, blockData);
				playerConnection.sendPacket(bed, relMoveLook);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
