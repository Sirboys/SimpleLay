package ru.azerusteam.layhandler;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import ru.azerusteam.Classes.ILayPlayer;
import org.bukkit.craftbukkit.v1_13_R1.CraftServer;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import net.minecraft.server.v1_13_R1.BlockBed;
import net.minecraft.server.v1_13_R1.BlockPosition;
import net.minecraft.server.v1_13_R1.BlockPropertyBedPart;
import net.minecraft.server.v1_13_R1.Blocks;
import net.minecraft.server.v1_13_R1.DataWatcher;
import net.minecraft.server.v1_13_R1.DataWatcherRegistry;
import net.minecraft.server.v1_13_R1.EntityPlayer;
import net.minecraft.server.v1_13_R1.EnumDirection;
import net.minecraft.server.v1_13_R1.EnumItemSlot;
import net.minecraft.server.v1_13_R1.Fluid;
import net.minecraft.server.v1_13_R1.IBlockAccess;
import net.minecraft.server.v1_13_R1.IBlockData;
import net.minecraft.server.v1_13_R1.MathHelper;
import net.minecraft.server.v1_13_R1.MobEffect;
import net.minecraft.server.v1_13_R1.MobEffects;
import net.minecraft.server.v1_13_R1.Packet;
import net.minecraft.server.v1_13_R1.PacketPlayOutBed;
import net.minecraft.server.v1_13_R1.PacketPlayOutBlockChange;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityEffect;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_13_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_13_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_13_R1.PacketPlayOutRemoveEntityEffect;
import net.minecraft.server.v1_13_R1.PlayerInteractManager;
import net.minecraft.server.v1_13_R1.TileEntity;
import net.minecraft.server.v1_13_R1.ItemStack;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook;
import net.minecraft.server.v1_13_R1.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_13_R1.ScoreboardTeam;
import net.minecraft.server.v1_13_R1.ScoreboardTeamBase.EnumNameTagVisibility;
import ru.azerusteam.sirmanager.Main;

public class LayPlayer_v1_13_R1 implements ILayPlayer{
	private int idE;
	private ArmorStand rider;
	private Main plugin;
	private Player player;
	private EntityPlayer fakePlayer;
	private DataWatcher datawatcher;
	private float rotation;
	public LayPlayer_v1_13_R1(Player player) {
		this.plugin = (Main) JavaPlugin.getPlugin(Main.class);
		this.player = player;
	}
	public void lay() {
		Player player = this.player;
		EntityPlayer p = (EntityPlayer) ((CraftPlayer) player).getHandle();
		CraftPlayer cp = (CraftPlayer) player;
		DataWatcher data = ((CraftLivingEntity)cp).getHandle().getDataWatcher();
		GameProfile gameProfile = new GameProfile(player.getUniqueId(), player.getName());
		try{
			Property textures = (Property) ((CraftPlayer) player).getHandle().getProfile().getProperties().get("textures").toArray()[0];
	        String signature = textures.getSignature();
	        String value = textures.getValue();
	        gameProfile.getProperties().put("textures", new Property("textures", value, signature));
		}catch (Exception e) {
		}
        this.fakePlayer = new EntityPlayer(
                ((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld) player.getWorld()).getHandle(),
                gameProfile,
                new PlayerInteractManager(((CraftWorld) player.getWorld()).getHandle())
                );
        
        this.fakePlayer.setPositionRotation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getEyeLocation().getPitch());
        this.rotation = player.getLocation().getYaw();
        IBlockAccess iblockaccess = bedBlockAccess();
        PacketPlayOutRelEntityMoveLook relMoveLook = new PacketPlayOutRelEntityMoveLook(this.fakePlayer.getId(),(short)0,(short)2,(short)0,(byte)0,(byte)0,true);
        PacketPlayOutNamedEntitySpawn ppones = new PacketPlayOutNamedEntitySpawn(this.fakePlayer);
        this.fakePlayer.setCustomNameVisible(false);
        PacketPlayOutPlayerInfo ppopi = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, this.fakePlayer);

        ScoreboardTeam team = new ScoreboardTeam(((CraftServer) Bukkit.getServer()).getServer().getScoreboard(), "SimpleLay_TEAM");
        team.setNameTagVisibility(EnumNameTagVisibility.NEVER);
        PacketPlayOutScoreboardTeam ppostC = new PacketPlayOutScoreboardTeam(team, 0);
        //PacketPlayOutScoreboardTeam ppostF = new PacketPlayOutScoreboardTeam(team, 2);
        PacketPlayOutScoreboardTeam ppostU = new PacketPlayOutScoreboardTeam(team, Collections.singletonList(player.getName()), 3);
        BlockPosition bedLoc = new BlockPosition(player.getLocation().getX(), 0, player.getLocation().getZ());
        PacketPlayOutBlockChange ppobc = new PacketPlayOutBlockChange(iblockaccess, bedLoc);
        PacketPlayOutEntityDestroy ppoed = new PacketPlayOutEntityDestroy(p.getId());
        PacketPlayOutBed ppod = new PacketPlayOutBed(this.fakePlayer, bedLoc);
		DataWatcher watcher = this.fakePlayer.getDataWatcher();
	    watcher.set(DataWatcherRegistry.a.a(13), (byte) p.getDataWatcher().get(DataWatcherRegistry.a.a(13)));
	    PacketPlayOutEntityMetadata ppoeme = new PacketPlayOutEntityMetadata(this.fakePlayer.getId(), watcher, false);
        this.idE = this.fakePlayer.getId();
        this.rotation = this.player.getLocation().getYaw();
        Collection<? extends Player> plist = player.getServer().getOnlinePlayers();
        for (Iterator<?> iterator = plist.iterator(); iterator.hasNext();) {
			Player onlinePlayer = (Player) iterator.next();
			EntityPlayer ep = (EntityPlayer) ((CraftPlayer) onlinePlayer).getHandle();
			if (ep != p) {
				ep.playerConnection.sendPacket(ppopi);
			}
			if (ep == p) {
				ep.playerConnection.sendPacket((Packet<?>) new PacketPlayOutEntityEffect(p.getId(),new MobEffect(MobEffects.INVISIBILITY, 25122001, 0, false, false)));
				ep.setInvisible(true);
			}
			ep.playerConnection.sendPacket(ppones);
			ep.playerConnection.sendPacket(ppoeme);
			ep.playerConnection.sendPacket(ppobc);
			
			ep.playerConnection.sendPacket(relMoveLook);
			if (ep != p) {
				ep.playerConnection.sendPacket(ppoed);
			}else {
				ep.playerConnection.sendPacket(ppostC);
				ep.playerConnection.sendPacket(ppostU);
			}
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					ep.playerConnection.sendPacket(ppod);
				}
			}, 1L);
		
        }
        //player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 25122001, 244, true));
        final Location location = player.getLocation();
        this.rider = (ArmorStand)location.getWorld().spawn(location.clone().subtract(0, 1.7, 0), ArmorStand.class);
        this.rider.setGravity(false);
        //rider.setMarker(true);
        this.rider.setVisible(false);
        this.rider.addPassenger(player);
        this.plugin.setLay(this.getPlayer().getUniqueId(),this);
	}
	public void unLay(boolean announce) {
		EntityPlayer p = (EntityPlayer) ((CraftPlayer) this.player).getHandle();
		PacketPlayOutEntityDestroy ppoed = new PacketPlayOutEntityDestroy(this.plugin.getLays().get(this.player.getUniqueId()).getIdE());
		PacketPlayOutNamedEntitySpawn ppones = new PacketPlayOutNamedEntitySpawn(p);
		PacketPlayOutPlayerInfo ppopi = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, p);
		DataWatcher watcher = p.getDataWatcher();
	    watcher.set(DataWatcherRegistry.a.a(13), (byte) watcher.get(DataWatcherRegistry.a.a(13)));
		PacketPlayOutEntityMetadata ppoeme = new PacketPlayOutEntityMetadata(p.getId(), watcher, true);
		PacketPlayOutEntityHeadRotation ppoehr = new PacketPlayOutEntityHeadRotation(p, (byte) MathHelper.d((float) p.getHeadRotation() * 256.0F / 360.0F));
		Collection<? extends Player> plist = this.player.getServer().getOnlinePlayers();
		ItemStack isHelm = p.getEquipment(EnumItemSlot.HEAD);
		ItemStack isChest = p.getEquipment(EnumItemSlot.CHEST);
		ItemStack isLegs = p.getEquipment(EnumItemSlot.LEGS);
		ItemStack isBoots = p.getEquipment(EnumItemSlot.FEET);
		ItemStack isMain = p.getEquipment(EnumItemSlot.MAINHAND);
		ItemStack isOff = p.getEquipment(EnumItemSlot.OFFHAND);
		PacketPlayOutEntityEquipment ppoee0 = isHelm==null?null:new PacketPlayOutEntityEquipment(p.getId(), EnumItemSlot.HEAD, isHelm);
		PacketPlayOutEntityEquipment ppoee1 = isChest==null?null:new PacketPlayOutEntityEquipment(p.getId(), EnumItemSlot.CHEST, isChest);
		PacketPlayOutEntityEquipment ppoee2 = isLegs==null?null:new PacketPlayOutEntityEquipment(p.getId(), EnumItemSlot.LEGS, isLegs);
		PacketPlayOutEntityEquipment ppoee3 = isBoots==null?null:new PacketPlayOutEntityEquipment(p.getId(), EnumItemSlot.FEET, isBoots);
		PacketPlayOutEntityEquipment ppoee4 = isMain==null?null:new PacketPlayOutEntityEquipment(p.getId(), EnumItemSlot.MAINHAND, isMain);
		PacketPlayOutEntityEquipment ppoee5 = isOff==null?null:new PacketPlayOutEntityEquipment(p.getId(), EnumItemSlot.OFFHAND, isOff);
		MobEffect ppl = p.getEffect(MobEffects.INVISIBILITY);
		//PacketPlayOutRemoveEntityEffect ppoedE = new PacketPlayOutRemoveEntityEffect(p.getId(), MobEffects.INVISIBILITY);
		//Packet<?> ppoee = ppl==null?null:(Packet<?>)new PacketPlayOutEntityEffect(p.getId(), ppl);
		this.player.showPlayer(plugin, player);
		p.playerConnection.sendPacket((Packet<?>) new PacketPlayOutRemoveEntityEffect(p.getId(), MobEffects.INVISIBILITY));
		if (ppl != null) {
			p.playerConnection.sendPacket((Packet) new PacketPlayOutEntityEffect(p.getId(), new MobEffect(ppl)) );
		}else {
			p.setInvisible(false);
		}
        for (Iterator<?> iterator = plist.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			Location loc = this.player.getLocation();
			loc.setY(0);
			player.sendBlockChange(loc, loc.getBlock().getBlockData());
			EntityPlayer ep = (EntityPlayer) ((CraftPlayer) player).getHandle();
			ep.playerConnection.sendPacket(ppoed);
			if (ep != p) {
				ep.playerConnection.sendPacket(ppones);
				ep.playerConnection.sendPacket(ppopi);
				ep.playerConnection.sendPacket(ppoeme);
				ep.playerConnection.sendPacket(ppoehr);
				if (isHelm != null) ep.playerConnection.sendPacket(ppoee0);
				if (isChest != null) ep.playerConnection.sendPacket(ppoee1);
				if (isLegs != null) ep.playerConnection.sendPacket(ppoee2);
				if (isBoots != null) ep.playerConnection.sendPacket(ppoee3);
				if (isMain != null) ep.playerConnection.sendPacket(ppoee4);
				if (isOff != null) ep.playerConnection.sendPacket(ppoee5);
			}
		}
        ArmorStand seat = (ArmorStand) ((LayPlayer_v1_13_R1) this.plugin.getLays().get(this.player.getUniqueId())).getRider();
        seat.remove();
        this.plugin.removeLay(this.player.getUniqueId());
        if (announce) {
			this.player.sendMessage(plugin.prefix+plugin.getConfig().getString("lang.lay.noLonger"));
		}
        if (player.isDead()) return;
        player.teleport(seat.getLocation().add(0,1.7,0).setDirection(player.getLocation().getDirection()));
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					@Override
					public void run() {
						player.teleport(seat.getLocation().add(0,1.7,0).setDirection(player.getLocation().getDirection()));
					}
				}, 1L);
	}
	public int getIdE() {
		return idE;
	}
	public ArmorStand getRider() {
		return rider;
	}
	public Player getPlayer() {
		return player;
	}
	public static void checkSeatBlock(Main plugin){
		//Collection<LayPlayer> spList = plugin.getLays().values();
		for (ILayPlayer ilayPlayer : plugin.getLays().values()) {
			//LayPlayer layPlayer = (LayPlayer) iterator.next();
			LayPlayer_v1_13_R1 layPlayer = (LayPlayer_v1_13_R1) ilayPlayer;
			if (layPlayer == null) continue;
			if (layPlayer.player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
				layPlayer.player.sendMessage(plugin.prefix+plugin.getConfig().getString("lay.invisible"));
				layPlayer.unLay(false);
				continue;
			}
			if (layPlayer.rider.getLocation().subtract(0, -1.6, 0).getBlock().getType() != Material.AIR) continue;
			layPlayer.unLay(plugin.getConfig().getBoolean("lay.announce.command"));
		};
	}
	public IBlockAccess bedBlockAccess() {
		Float f = this.rotation;
		return new IBlockAccess() {
			@Override
			public IBlockData getType(BlockPosition arg0) {
				//Float f = p.getLocation().getYaw();
				EnumDirection a = bedDirection(f);
				IBlockData ibd = Blocks.RED_BED.getBlockData().set(BlockBed.FACING, a).set(BlockBed.PART, BlockPropertyBedPart.HEAD);
				return ibd;
			}
			@Override
			public TileEntity getTileEntity(BlockPosition arg0) {
				return null;
			}
			@Override
			public Fluid b(BlockPosition arg0) {
				return null;
			}
		};
	}
	public static EnumDirection bedDirection(Float f) {
		f=(f<0.0f)?(360f+f):f;
		f=f%360;
		EnumDirection a = null;
		if (f>=315 || f<=45) {a = EnumDirection.NORTH;}
		if (f>=45 && f<=135) {a = EnumDirection.EAST;}
		if (f>=135 && f<=225) {a = EnumDirection.SOUTH;}
		if (f>=225 && f<=315) {a = EnumDirection.WEST;}
		return a;
	}
	public static void upDateArmor(Main plugin) {
		Collection<? extends Player> plist = plugin.getServer().getOnlinePlayers();
		for (Iterator<?> iterator = plist.iterator(); iterator.hasNext();) {
			Player player = (Player) iterator.next();
			EntityPlayer ep = (EntityPlayer) ((CraftPlayer) player).getHandle();
			Collection<ILayPlayer> lay = plugin.getLays().values();
			for (Iterator<ILayPlayer> iterator2 = lay.iterator(); iterator2.hasNext();) {
				LayPlayer_v1_13_R1 layPlayer = (LayPlayer_v1_13_R1) iterator2.next();
				EntityPlayer p = (EntityPlayer) ((CraftPlayer) layPlayer.player).getHandle();
				ItemStack isHelm = p.getEquipment(EnumItemSlot.HEAD);
				ItemStack isChest = p.getEquipment(EnumItemSlot.CHEST);
				ItemStack isLegs = p.getEquipment(EnumItemSlot.LEGS);
				ItemStack isBoots = p.getEquipment(EnumItemSlot.FEET);
				ItemStack isMain = p.getEquipment(EnumItemSlot.MAINHAND);
				ItemStack isOff = p.getEquipment(EnumItemSlot.OFFHAND);
				if (isHelm != null) {
					PacketPlayOutEntityEquipment ppoee0 = new PacketPlayOutEntityEquipment(layPlayer.fakePlayer.getId(), EnumItemSlot.HEAD, isHelm);
					ep.playerConnection.sendPacket(ppoee0);
				}
				if (isChest != null) {
					PacketPlayOutEntityEquipment ppoee1 = new PacketPlayOutEntityEquipment(layPlayer.fakePlayer.getId(), EnumItemSlot.CHEST, isChest);
					ep.playerConnection.sendPacket(ppoee1);
				}
				if (isLegs != null) {
					PacketPlayOutEntityEquipment ppoee2 = new PacketPlayOutEntityEquipment(layPlayer.fakePlayer.getId(), EnumItemSlot.LEGS, isLegs);
					ep.playerConnection.sendPacket(ppoee2);
				}
				if (isBoots != null) {
					PacketPlayOutEntityEquipment ppoee3 = new PacketPlayOutEntityEquipment(layPlayer.fakePlayer.getId(), EnumItemSlot.FEET, isBoots);
					ep.playerConnection.sendPacket(ppoee3);
				}
				if (isMain != null) {
					PacketPlayOutEntityEquipment ppoee4 = new PacketPlayOutEntityEquipment(layPlayer.fakePlayer.getId(), EnumItemSlot.MAINHAND, isMain);
					ep.playerConnection.sendPacket(ppoee4);
				}
				if (isOff != null) {
					PacketPlayOutEntityEquipment ppoee5 = new PacketPlayOutEntityEquipment(layPlayer.fakePlayer.getId(), EnumItemSlot.OFFHAND, isOff);
					ep.playerConnection.sendPacket(ppoee5);
				}
				ep.playerConnection.sendPacket(new PacketPlayOutBlockChange(layPlayer.bedBlockAccess(), new BlockPosition(layPlayer.getPlayer().getLocation().getX(), 0, layPlayer.getPlayer().getLocation().getZ())));
				if (ep == p) continue;
				ep.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(p.getId()));
				
			}
		}
	}
	public static void showLayingPlayers(Main plugin,Player watcherPlayer) {
		for (Iterator<?> iterator = plugin.getLays().values().iterator(); iterator.hasNext();) {
			LayPlayer_v1_13_R1 LayPlayer = (LayPlayer_v1_13_R1) iterator.next();
	        EntityPlayer entityPlayer = (EntityPlayer) ((CraftPlayer) LayPlayer.getPlayer()).getHandle();;
	        PacketPlayOutRelEntityMoveLook relMoveLook = new PacketPlayOutRelEntityMoveLook(LayPlayer.fakePlayer.getId(),(short)0,(short)2,(short)0,(byte)0,(byte)0,true);
	        PacketPlayOutNamedEntitySpawn ppones = new PacketPlayOutNamedEntitySpawn(LayPlayer.fakePlayer);
	        PacketPlayOutPlayerInfo ppopi = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, LayPlayer.fakePlayer);
	        BlockPosition bedLoc = new BlockPosition(LayPlayer.getPlayer().getLocation().getX(), 0, LayPlayer.getPlayer().getLocation().getZ());
	        PacketPlayOutBlockChange ppobc = new PacketPlayOutBlockChange(LayPlayer.bedBlockAccess(), bedLoc);
	        PacketPlayOutEntityDestroy ppoed = new PacketPlayOutEntityDestroy(entityPlayer.getId());
	        PacketPlayOutBed ppod = new PacketPlayOutBed(LayPlayer.fakePlayer, bedLoc);
	      try {
	        PacketPlayOutEntityMetadata ppoeme = new PacketPlayOutEntityMetadata(LayPlayer.fakePlayer.getId(), LayPlayer.datawatcher, true);
			EntityPlayer ep = (EntityPlayer) ((CraftPlayer) watcherPlayer).getHandle();
			ep.playerConnection.sendPacket(ppoed);
			ep.playerConnection.sendPacket(ppones);
			ep.playerConnection.sendPacket(ppopi);
			ep.playerConnection.sendPacket(ppoeme);
			ep.playerConnection.sendPacket(ppobc);
			ep.playerConnection.sendPacket(ppod);
			ep.playerConnection.sendPacket(relMoveLook);
	        }catch(Exception e) {
	        	e.printStackTrace();
	        }
		}
	}
}