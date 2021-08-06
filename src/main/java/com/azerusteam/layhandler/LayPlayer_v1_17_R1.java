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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.NumberConversions;

import java.util.*;

public class LayPlayer_v1_17_R1 implements ILayPlayer {
    private int fakePlayerID;
    private ArmorStand rider;
    private final SimpleLay plugin;
    private final Player player;
    private EntityPlayer fakePlayer;
    private float rotation;
    public LayPlayer_v1_17_R1(Player player) {
        this.plugin = JavaPlugin.getPlugin(SimpleLay.class);
        this.player = player;
    }
    public void lay() {
        Player player = this.player;
        CraftPlayer craftPlayer = CraftPlayer.wrap(player);
        EntityPlayer entityPlayer = craftPlayer.getHandle();

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), player.getName());
        try {
            Property textures = entityPlayer.getProfile().getProperties().get("textures").toArray(new Property[0])[0];
            gameProfile.getProperties().put("textures", new Property("textures", textures.getValue(), textures.getSignature()));
        } catch (Exception ignored) {}

        WorldServer worldServer = CraftWorld.wrap(player.getWorld()).getHandle();
        MinecraftServer minecraftServer = CraftServer.wrap(Bukkit.getServer()).getServer();
        this.fakePlayer = new EntityPlayer(minecraftServer, worldServer, gameProfile);
        this.fakePlayerID = this.fakePlayer.getId();
        this.fakePlayer.setPositionRotation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getEyeLocation().getPitch());
        this.rotation = player.getLocation().getYaw();

        Packet relMoveLook = new PacketPlayOutRelEntityMoveLook(this.fakePlayer.getId(),(short)0,(short)2,(short)0,(byte)0,(byte)0,true);
        PacketPlayOutNamedEntitySpawn namedEntitySpawn = new PacketPlayOutNamedEntitySpawn(this.fakePlayer);

        this.fakePlayer.setCustomNameVisible(false);
        //this.fakePlayer.listName = cp.getHandle().getPlayerListName();
        PacketPlayOutPlayerInfo playerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, this.fakePlayer);
        PacketPlayOutPlayerInfo playerInfoRemove = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, this.fakePlayer);

        Location bukkitBedLocation = player.getLocation().clone();
        bukkitBedLocation.setY(0);
        BlockPosition nmsBedLocation = new BlockPosition(bukkitBedLocation);
        PacketPlayOutEntityDestroy entityDestroy = new PacketPlayOutEntityDestroy(entityPlayer.getId());

        //fixme scoreboards

        this.fakePlayer.setPose(EntityPose.SLEEPING);
        this.fakePlayer.setBedPosition(nmsBedLocation);
        try {
            DataWatcher watcher = entityPlayer.getDataWatcher();
            DataWatcher fakeWatcher = fakePlayer.getDataWatcher();
            fakeWatcher.set(DataWatcherRegistry.BYTE.createAccessor(17), watcher.get(DataWatcherRegistry.BYTE.createAccessor(17)));

            this.rotation = this.player.getLocation().getYaw();

            player.hidePlayer(plugin, player);
            PacketPlayOutEntityMetadata entityMetadata = new PacketPlayOutEntityMetadata(this.fakePlayer.getId(), fakeWatcher, false);
            for (Player onlinePlayer : player.getServer().getOnlinePlayers()) {
                EntityPlayer entityPlayer1 = CraftPlayer.wrap(onlinePlayer).getHandle();
                PlayerConnection playerConnection = entityPlayer1.getPlayerConnection();
                playerConnection.sendPacket(playerInfo);
                playerConnection.sendPacket(namedEntitySpawn, entityMetadata);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    playerConnection.sendPacket(playerInfoRemove);
                }, 20L);

                if (entityPlayer1.equals(entityPlayer)) {
                    playerConnection.sendPacket(new PacketPlayOutEntityEffect(entityPlayer.getId(), new MobEffect(MobEffects.INVISIBILITY, 25122001, 0, false, false)));
                    entityPlayer1.setInvisible(true);
                }

                onlinePlayer.sendBlockChange(bukkitBedLocation, this.bedBlockData());
                playerConnection.sendPacket(relMoveLook);
                if (!entityPlayer1.equals(entityPlayer))
                    playerConnection.sendPacket(entityDestroy);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        final Location location = player.getLocation();
        this.rider = (ArmorStand)location.getWorld().spawn(location.clone().subtract(0, 1.7, 0), ArmorStand.class);
        this.rider.setGravity(false);
        this.rider.setVisible(false);
        this.rider.addPassenger(player);
        this.plugin.setLay(this.getPlayer().getUniqueId(),this);
    }
    public void unLay(boolean announce) {
        EntityPlayer entityPlayer = CraftPlayer.wrap(this.player).getHandle();
        Packet entityDestroy = new PacketPlayOutEntityDestroy(SimpleLay.getInstance().getLayingPlayers().get(this.player.getUniqueId()).getFakePlayerID());

        Packet namedEntitySpawn = new PacketPlayOutNamedEntitySpawn(entityPlayer);
        Packet playerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);
        DataWatcher watcher = entityPlayer.getDataWatcher();
        watcher.set(DataWatcherRegistry.BYTE.createAccessor(17), watcher.get(DataWatcherRegistry.BYTE.createAccessor(17)));
        Packet entityMetadata = new PacketPlayOutEntityMetadata(entityPlayer.getId(), watcher, true);
        Packet entityHeadRotation = new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) NumberConversions.floor(entityPlayer.getHeadRotation() * 256.0F / 360.0F));
        Packet equipmentPacket = ILayPlayer.getEquipmentPacket(player, this.fakePlayerID);
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
            //playerConnection.sendPacket(playerInfoRemove);
            playerConnection.sendPacket(entityDestroy);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
            }, 10L);
            if (!entityPlayer1.equals(entityPlayer)) {
                playerConnection.sendPacket(namedEntitySpawn, playerInfo, entityMetadata, entityHeadRotation);
                playerConnection.sendPacket(equipmentPacket);
            }
        }
        ArmorStand seat = this.plugin.getLayingPlayers().get(this.player.getUniqueId()).getRider();
        seat.remove();
        this.plugin.removeLaying(this.player.getUniqueId());
        if (announce) {
            this.player.sendMessage(plugin.prefix + plugin.getConfig().getString("lang.lay.noLonger"));
        }
        if (player.isDead()) return;
        player.teleport(seat.getLocation().add(0, 1.7, 0).setDirection(player.getLocation().getDirection()));
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.teleport(seat.getLocation().add(0, 1.7, 0).setDirection(player.getLocation().getDirection())), 1L);

    }
    public int getFakePlayerID() {
        return fakePlayerID;
    }

    @Override
    public EntityPlayer getFakePlayer() {
        return this.fakePlayer;
    }

    public ArmorStand getRider() {
        return rider;
    }
    public Player getPlayer() {
        return player;
    }

    @Override
    public float getRotation() {
        return this.rotation;
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
            playerConnection.sendPacket(ILayPlayer.getEquipmentPacket(p, iLayPlayer.getFakePlayerID()));
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