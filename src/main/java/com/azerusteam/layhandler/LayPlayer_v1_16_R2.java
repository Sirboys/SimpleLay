package com.azerusteam.layhandler;

import com.azerusteam.players.ILayPlayer;
import com.azerusteam.sirmanager.SimpleLay;
import com.azerusteam.wrappers.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class LayPlayer_v1_16_R2 implements ILayPlayer {
    private int fakePlayerID;
    private ArmorStand rider;
    private final SimpleLay plugin;
    private final Player player;
    private EntityPlayer fakePlayer;
    private float rotation;

    public LayPlayer_v1_16_R2(Player player) {
        this.plugin = SimpleLay.getInstance();
        this.player = player;
    }
    public void lay() {
        Player player = this.player;
        EntityPlayer entityPlayer = (EntityPlayer) ((CraftPlayer) player).getHandle();
        CraftPlayer craftPlayer = (CraftPlayer) player;
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
        this.rotation = player.getLocation().getYaw(); // TODO
        IBlockAccess iblockaccess = bedBlockAccess();
        PacketPlayOutRelEntityMoveLook relMoveLook = new PacketPlayOutRelEntityMoveLook(this.fakePlayer.getId(),(short)0,(short)2,(short)0,(byte)0,(byte)0,true);
        PacketPlayOutNamedEntitySpawn ppones = new PacketPlayOutNamedEntitySpawn(this.fakePlayer);
        this.fakePlayer.setCustomNameVisible(false);
        PacketPlayOutPlayerInfo ppopi = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, this.fakePlayer);

        //PacketPlayOutPlayerInfo ppopi_remove = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction., this.fakePlayer);
        BlockPosition bedLoc = new BlockPosition(player.getLocation().getX(), 0, player.getLocation().getZ());
        PacketPlayOutBlockChange ppobc = new PacketPlayOutBlockChange(iblockaccess, bedLoc);
        PacketPlayOutEntityDestroy ppoed = new PacketPlayOutEntityDestroy(entityPlayer.getId());
        //

        ScoreboardTeam team = new ScoreboardTeam(((CraftServer) Bukkit.getServer()).getServer().getScoreboard(), "SimpleLay_TEAM");
        team.setNameTagVisibility(EnumNameTagVisibility.NEVER);
        PacketPlayOutScoreboardTeam ppostC = new PacketPlayOutScoreboardTeam(team, 0);
        //PacketPlayOutScoreboardTeam ppostF = new PacketPlayOutScoreboardTeam(team, 2);
        PacketPlayOutScoreboardTeam ppostU = new PacketPlayOutScoreboardTeam(team, Collections.singletonList(player.getName()), 3);
        try {
            Method meth = Entity.class.getDeclaredMethod("setPose", new Class[] {EntityPose.class});
            meth.setAccessible(true);
            meth.invoke(this.fakePlayer, EntityPose.SLEEPING);
            DataWatcher watcher = this.fakePlayer.getDataWatcher();
            Field field = EntityLiving.class.getDeclaredField("bj");
            field.setAccessible(true);
            DataWatcherObject<Optional> obj = (DataWatcherObject<Optional>) field.get(null);
            watcher.set(DataWatcherRegistry.a.a(16), (byte) entityPlayer.getDataWatcher().get(DataWatcherRegistry.a.a(16)));
            watcher.set(obj, Optional.of(bedLoc));
            this.datawatcher = watcher;
            PacketPlayOutEntityMetadata ppoeme = new PacketPlayOutEntityMetadata(this.fakePlayer.getId(), watcher, false);

            this.fakePlayerID = this.fakePlayer.getId();
            this.rotation = this.player.getLocation().getYaw();
            Collection<? extends Player> plist = player.getServer().getOnlinePlayers();
            //this.fakePlayer.setCustomNameVisible(false);
            player.hidePlayer(plugin, player);
            for (Iterator<?> iterator = plist.iterator(); iterator.hasNext();) {
                Player onlinePlayer = (Player) iterator.next();
                EntityPlayer ep = (EntityPlayer) ((CraftPlayer) onlinePlayer).getHandle();
                if (ep != entityPlayer) {
                    ep.playerConnection.sendPacket(ppopi);
                }
                if (ep == entityPlayer) {
                    ep.playerConnection.sendPacket((Packet<?>) new PacketPlayOutEntityEffect(entityPlayer.getId(),new MobEffect(MobEffects.INVISIBILITY, 25122001, 0, false, false)));
                    ep.setInvisible(true);

                }
                ep.playerConnection.sendPacket(ppones);
                ep.playerConnection.sendPacket(ppoeme);
                ep.playerConnection.sendPacket(ppobc);
                ep.playerConnection.sendPacket(relMoveLook);
                if (ep != entityPlayer) {
                    ep.playerConnection.sendPacket(ppoed);
                }else {
                    //ep.playerConnection.sendPacket(ppostC);
                    //ep.playerConnection.sendPacket(ppostU);
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
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
        PacketPlayOutEntityDestroy ppoed = new PacketPlayOutEntityDestroy(this.plugin.getLayingPlayers().get(this.player.getUniqueId()).getFakePlayerID());
        PacketPlayOutNamedEntitySpawn ppones = new PacketPlayOutNamedEntitySpawn(p);
        PacketPlayOutPlayerInfo ppopi = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, p);
        DataWatcher watcher = p.getDataWatcher();
        watcher.set(DataWatcherRegistry.a.a(16), (byte) watcher.get(DataWatcherRegistry.a.a(16)));
        PacketPlayOutEntityMetadata ppoeme = new PacketPlayOutEntityMetadata(p.getId(), watcher, true);
        PacketPlayOutEntityHeadRotation ppoehr = new PacketPlayOutEntityHeadRotation(p, (byte) MathHelper.d((float) p.getHeadRotation() * 256.0F / 360.0F));
        Collection<? extends Player> plist = this.player.getServer().getOnlinePlayers();
        ItemStack isHelm = p.getEquipment(EnumItemSlot.HEAD);
        ItemStack isChest = p.getEquipment(EnumItemSlot.CHEST);
        ItemStack isLegs = p.getEquipment(EnumItemSlot.LEGS);
        ItemStack isBoots = p.getEquipment(EnumItemSlot.FEET);
        ItemStack isMain = p.getEquipment(EnumItemSlot.MAINHAND);
        ItemStack isOff = p.getEquipment(EnumItemSlot.OFFHAND);
        List<Pair<EnumItemSlot, ItemStack>> list = Arrays.asList(
                new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.HEAD,isHelm),
                new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.CHEST,isChest),
                new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.LEGS,isLegs),
                new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.FEET,isBoots),
                new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.MAINHAND,isMain),
                new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.OFFHAND,isOff)
        );/*
		//new PacketPlayOutEntityEquipment(p.getId(), )
		PacketPlayOutEntityEquipment ppoee0 = isHelm==null?null:new PacketPlayOutEntityEquipment(p.getId(), EnumItemSlot.HEAD, isHelm);
		PacketPlayOutEntityEquipment ppoee1 = isChest==null?null:new PacketPlayOutEntityEquipment(p.getId(), EnumItemSlot.CHEST, isChest);
		PacketPlayOutEntityEquipment ppoee2 = isLegs==null?null:new PacketPlayOutEntityEquipment(p.getId(), EnumItemSlot.LEGS, isLegs);
		PacketPlayOutEntityEquipment ppoee3 = isBoots==null?null:new PacketPlayOutEntityEquipment(p.getId(), EnumItemSlot.FEET, isBoots);
		PacketPlayOutEntityEquipment ppoee4 = isMain==null?null:new PacketPlayOutEntityEquipment(p.getId(), EnumItemSlot.MAINHAND, isMain);
		PacketPlayOutEntityEquipment ppoee5 = isOff==null?null:new PacketPlayOutEntityEquipment(p.getId(), EnumItemSlot.OFFHAND, isOff);*/
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
				/*if (isHelm != null) ep.playerConnection.sendPacket(ppoee0);
				if (isChest != null) ep.playerConnection.sendPacket(ppoee1);
				if (isLegs != null) ep.playerConnection.sendPacket(ppoee2);
				if (isBoots != null) ep.playerConnection.sendPacket(ppoee3);
				if (isMain != null) ep.playerConnection.sendPacket(ppoee4);
				if (isOff != null) ep.playerConnection.sendPacket(ppoee5);*/
                ep.playerConnection.sendPacket(new PacketPlayOutEntityEquipment(p.getId(), list));

            }
        }
        // p.playerConnection.sendPacket(ppoe);
        //this.player.removePotionEffect(PotionEffectType.INVISIBILITY);
        //this.player.sendMessage(this.effect.toString());
        //if (effect != null) {
        //player.addPotionEffect(effect);
        //}
        ArmorStand seat = (ArmorStand) ((LayPlayer_v1_16_R2) this.plugin.getLayingPlayers().get(this.player.getUniqueId())).getRider();
        seat.remove();
        this.plugin.removeLaying(this.player.getUniqueId());
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
    public int getFakePlayerID() {
        return fakePlayerID;
    }
    public ArmorStand getRider() {
        return rider;
    }
    public Player getPlayer() {
        return player;
    }
    public static void checkSeatBlock(SimpleLay plugin){
        //Collection<LayPlayer> spList = plugin.getLays().values();
        for (ILayPlayer ilayPlayer : plugin.getLayingPlayers().values()) {
            //LayPlayer layPlayer = (LayPlayer) iterator.next();
            LayPlayer_v1_16_R2 layPlayer = (LayPlayer_v1_16_R2) ilayPlayer;
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
            public Fluid getFluid(BlockPosition arg0) {
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
    public static void upDateArmor(SimpleLay plugin) {
        Collection<? extends Player> plist = plugin.getServer().getOnlinePlayers();
        for (Iterator<?> iterator = plist.iterator(); iterator.hasNext();) {
            Player player = (Player) iterator.next();
            EntityPlayer ep = (EntityPlayer) ((CraftPlayer) player).getHandle();

            Collection<ILayPlayer> lay = plugin.getLayingPlayers().values();
            for (Iterator<ILayPlayer> iterator2 = lay.iterator(); iterator2.hasNext();) {
                LayPlayer_v1_16_R2 layPlayer = (LayPlayer_v1_16_R2) iterator2.next();
                EntityPlayer p = (EntityPlayer) ((CraftPlayer) layPlayer.player).getHandle();
                ItemStack isHelm = p.getEquipment(EnumItemSlot.HEAD);
                ItemStack isChest = p.getEquipment(EnumItemSlot.CHEST);
                ItemStack isLegs = p.getEquipment(EnumItemSlot.LEGS);
                ItemStack isBoots = p.getEquipment(EnumItemSlot.FEET);
                ItemStack isMain = p.getEquipment(EnumItemSlot.MAINHAND);
                ItemStack isOff = p.getEquipment(EnumItemSlot.OFFHAND);
                List<Pair<EnumItemSlot, ItemStack>> list = Arrays.asList(
                        new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.HEAD,isHelm),
                        new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.CHEST,isChest),
                        new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.LEGS,isLegs),
                        new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.FEET,isBoots),
                        new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.MAINHAND,isMain),
                        new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.OFFHAND,isOff)
                );
				/*if (isHelm != null) {
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
				}*/
                ep.playerConnection.sendPacket(new PacketPlayOutEntityEquipment(layPlayer.fakePlayer.getId(), list));
                ep.playerConnection.sendPacket(new PacketPlayOutBlockChange(layPlayer.bedBlockAccess(), new BlockPosition(layPlayer.getPlayer().getLocation().getX(), 0, layPlayer.getPlayer().getLocation().getZ())));
                if (ep == p) {
                    ep.setInvisible(true);
                    continue;
                }
                ep.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(p.getId()));

            }
        }
    }
    public static void showLayingPlayers(SimpleLay plugin, Player watcherPlayer) {

        for (Iterator<?> iterator = plugin.getLayingPlayers().values().iterator(); iterator.hasNext();) {
            LayPlayer_v1_16_R2 LayPlayer = (LayPlayer_v1_16_R2) iterator.next();
            EntityPlayer entityPlayer = (EntityPlayer) ((CraftPlayer) LayPlayer.getPlayer()).getHandle();;
            PacketPlayOutRelEntityMoveLook relMoveLook = new PacketPlayOutRelEntityMoveLook(LayPlayer.fakePlayer.getId(),(short)0,(short)2,(short)0,(byte)0,(byte)0,true);
            PacketPlayOutNamedEntitySpawn ppones = new PacketPlayOutNamedEntitySpawn(LayPlayer.fakePlayer);
            PacketPlayOutPlayerInfo ppopi = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, LayPlayer.fakePlayer);
            BlockPosition bedLoc = new BlockPosition(LayPlayer.getPlayer().getLocation().getX(), 0, LayPlayer.getPlayer().getLocation().getZ());
            PacketPlayOutBlockChange ppobc = new PacketPlayOutBlockChange(LayPlayer.bedBlockAccess(), bedLoc);
            PacketPlayOutEntityDestroy ppoed = new PacketPlayOutEntityDestroy(entityPlayer.getId());
            try {
                PacketPlayOutEntityMetadata ppoeme = new PacketPlayOutEntityMetadata(LayPlayer.fakePlayer.getId(), LayPlayer.datawatcher, true);
                EntityPlayer ep = (EntityPlayer) ((CraftPlayer) watcherPlayer).getHandle();
                ep.playerConnection.sendPacket(ppoed);
                ep.playerConnection.sendPacket(ppones);
                ep.playerConnection.sendPacket(ppopi);
                ep.playerConnection.sendPacket(ppoeme);
                ep.playerConnection.sendPacket(ppobc);
                ep.playerConnection.sendPacket(relMoveLook);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}