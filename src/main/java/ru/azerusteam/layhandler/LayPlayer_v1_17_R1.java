package ru.azerusteam.layhandler;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.BlockBed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyBedPart;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.scores.ScoreboardTeam;
import net.minecraft.world.scores.ScoreboardTeamBase;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import ru.azerusteam.Classes.ILayPlayer;
import ru.azerusteam.sirmanager.Main;

import java.util.*;

public class LayPlayer_v1_17_R1 implements ILayPlayer {
    private int idE;
    private ArmorStand rider;
    private final Main plugin;
    private final Player player;
    private EntityPlayer fakePlayer;
    private DataWatcher datawatcher;
    private float rotation;
    public LayPlayer_v1_17_R1(Player player) {
        this.plugin = JavaPlugin.getPlugin(Main.class);
        this.player = player;
    }
    public void lay() {
        Player player = this.player;
        CraftPlayer cp = (CraftPlayer) player;
        EntityPlayer p = cp.getHandle();
        //p.b.sendPacket(new PacketPlayOutGameStateChange());
        DataWatcher data = ((CraftLivingEntity)cp).getHandle().getDataWatcher();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), p.getName());
        try{
            Property textures = (Property) ((CraftPlayer) player).getHandle().getProfile().getProperties().get("textures").toArray()[0];
            String signature = textures.getSignature();
            String value = textures.getValue();
            gameProfile.getProperties().put("textures", new Property("textures", value, signature));
        }catch (Exception e) {
        }

        this.fakePlayer = new EntityPlayer(
                Objects.requireNonNull(p.getMinecraftServer()),
                p.getWorldServer(),
                gameProfile
        );
        //this.fakePlayer.setRespawnPosition(p.getWorld().getDimensionKey(), new BlockPosition(p.getPositionVector()), 0, true, false);
        //this.fakePlayer.spawnIn(p.getWorld());

        this.fakePlayer.getInventory().setItem(0, new ItemStack(Items.kn));
        //p.getWorld().addEntity(this.fakePlayer);
        //this.fakePlayer.spawnIn(p.getWorld());
       // new PacketPlayOutNamedEntitySpawn(this.fakePlayer)
        //this.fakePlayer.spawnIn(((CraftWorld) player.getWorld()).getHandle());

        this.fakePlayer.setPositionRotation(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getEyeLocation().getPitch());
        this.rotation = player.getLocation().getYaw();
        IBlockAccess iblockaccess = bedBlockAccess();
        PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook relMoveLook = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(this.fakePlayer.getId(),(short)0,(short)2,(short)0,(byte)0,(byte)0,true);
        PacketPlayOutNamedEntitySpawn ppones = new PacketPlayOutNamedEntitySpawn(this.fakePlayer);
        p.b.sendPacket(ppones);
        this.fakePlayer.setCustomNameVisible(true);
        this.fakePlayer.listName = cp.getHandle().getPlayerListName();
        PacketPlayOutPlayerInfo ppopi = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, this.fakePlayer);
        PacketPlayOutPlayerInfo ppopie = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, this.fakePlayer);
        //new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, this.fakePlayer);
        //PacketPlayOutPlayerInfo ppopi_remove = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction., this.fakePlayer);
        BlockPosition bedLoc = new BlockPosition(player.getLocation().getX(), 0, player.getLocation().getZ());
        PacketPlayOutBlockChange ppobc = new PacketPlayOutBlockChange(iblockaccess, bedLoc);
        PacketPlayOutEntityDestroy ppoed = new PacketPlayOutEntityDestroy(p.getId());
        //

        ScoreboardTeam team = new ScoreboardTeam(((CraftServer) Bukkit.getServer()).getServer().getScoreboard(), "SimpleLay_TEAM");
        team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.b);
        team.getPlayerNameSet().add(player.getName());
        PacketPlayOutScoreboardTeam ppost = PacketPlayOutScoreboardTeam.a(team);
        //Scoreboard newScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        //Team t = newScoreboard.registerNewTeam("simplelay_tm");
        //t.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        //new PacketPlayOutScoreboardTeam() TODO
        //PacketPlayOutScoreboardTeam ppostC = new PacketPlayOutScoreboardTeam(team, 0);
        //PacketPlayOutScoreboardTeam ppostF = new PacketPlayOutScoreboardTeam(team, 2);
        //PacketPlayOutScoreboardTeam ppostU = new PacketPlayOutScoreboardTeam(team, Collections.singletonList(player.getName()), 3);
        this.fakePlayer.setPose(EntityPose.c);
        this.fakePlayer.e(bedLoc);
        DataWatcher watcher = this.fakePlayer.getDataWatcher();
        watcher.set(DataWatcherRegistry.a.a(17), p.getDataWatcher().get(DataWatcherRegistry.a.a(17)));
        PacketPlayOutEntityMetadata ppoeme = new PacketPlayOutEntityMetadata(this.fakePlayer.getId(), watcher, false);
        try {
//            DataWatcher watcher = this.fakePlayer.getDataWatcher();
//            Field field = EntityLiving.class.getDeclaredField("bj");
//            field.setAccessible(true);
//          DataWatcherObject<Optional> obj = (DataWatcherObject<Optional>) field.get(null);
            watcher.set(DataWatcherRegistry.a.a(17), p.getDataWatcher().get(DataWatcherRegistry.a.a(17)));
//          watcher.set(obj, Optional.of(bedLoc));
            this.datawatcher = watcher;

            this.idE = this.fakePlayer.getId();
            this.rotation = this.player.getLocation().getYaw();
            Collection<? extends Player> plist = player.getServer().getOnlinePlayers();
            //this.fakePlayer.setCustomNameVisible(false);
            player.hidePlayer(plugin, player);
            Bed bedBlockData = ((Bed) Material.WHITE_BED.createBlockData());
            bedBlockData.setFacing(BlockFace.EAST);
            bedBlockData.setPart(Bed.Part.HEAD);
            Location bedLocation = this.player.getLocation().clone();
            bedLocation.setY(0.5);

            for (Player onlinePlayer : plist) {
                EntityPlayer ep = (EntityPlayer) ((CraftPlayer) onlinePlayer).getHandle();
                if (ep != p) {
                }
                ep.b.sendPacket(ppopi);
                if (ep == p) {
                    ep.b.sendPacket(new PacketPlayOutEntityEffect(p.getId(), new MobEffect(MobEffects.n, 25122001, 0, false, false)));
                    ep.setInvisible(true);
                    ep.b.sendPacket(ppost);

                }
                System.out.println("Sending packets +6");
                ep.b.sendPacket(ppones);
                //Thread.sleep(3000);
                Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
                    ep.b.sendPacket(ppopie);
                    onlinePlayer.sendBlockChange(bedLocation, bedBlockData);
                }, 2L);
                ep.b.sendPacket(ppoeme);

                //ep.b.sendPacket(ppobc);
                //ep.
                ep.b.sendPacket(relMoveLook);
                if (ep != p) {
                    ep.b.sendPacket(ppoed);
                } else {
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
        PacketPlayOutEntityDestroy ppoed = new PacketPlayOutEntityDestroy(this.plugin.getLays().get(this.player.getUniqueId()).getIdE());
        PacketPlayOutNamedEntitySpawn ppones = new PacketPlayOutNamedEntitySpawn(p);
        PacketPlayOutPlayerInfo ppopi = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, p);
        DataWatcher watcher = p.getDataWatcher();
        watcher.set(DataWatcherRegistry.a.a(17), (byte) watcher.get(DataWatcherRegistry.a.a(17)));
        PacketPlayOutEntityMetadata ppoeme = new PacketPlayOutEntityMetadata(p.getId(), watcher, true);
        PacketPlayOutEntityHeadRotation ppoehr = new PacketPlayOutEntityHeadRotation(p, (byte) MathHelper.d((float) p.getHeadRotation() * 256.0F / 360.0F));
        Collection<? extends Player> plist = this.player.getServer().getOnlinePlayers();
        ItemStack isHelm = p.getEquipment(EnumItemSlot.f);
        ItemStack isChest = p.getEquipment(EnumItemSlot.e);
        ItemStack isLegs = p.getEquipment(EnumItemSlot.d);
        ItemStack isBoots = p.getEquipment(EnumItemSlot.c);
        ItemStack isMain = p.getEquipment(EnumItemSlot.a);
        ItemStack isOff = p.getEquipment(EnumItemSlot.b);
        List<Pair<EnumItemSlot, ItemStack>> list = Arrays.asList(
                new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.f,isHelm),
                new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.e,isChest),
                new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.d,isLegs),
                new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.c,isBoots),
                new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.a,isMain),
                new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.b,isOff)
        );/*
		//new PacketPlayOutEntityEquipment(p.getId(), )
		PacketPlayOutEntityEquipment ppoee0 = isHelm==null?null:new PacketPlayOutEntityEquipment(p.getId(), EnumItemSlot.HEAD, isHelm);
		PacketPlayOutEntityEquipment ppoee1 = isChest==null?null:new PacketPlayOutEntityEquipment(p.getId(), EnumItemSlot.CHEST, isChest);
		PacketPlayOutEntityEquipment ppoee2 = isLegs==null?null:new PacketPlayOutEntityEquipment(p.getId(), EnumItemSlot.LEGS, isLegs);
		PacketPlayOutEntityEquipment ppoee3 = isBoots==null?null:new PacketPlayOutEntityEquipment(p.getId(), EnumItemSlot.FEET, isBoots);
		PacketPlayOutEntityEquipment ppoee4 = isMain==null?null:new PacketPlayOutEntityEquipment(p.getId(), EnumItemSlot.MAINHAND, isMain);
		PacketPlayOutEntityEquipment ppoee5 = isOff==null?null:new PacketPlayOutEntityEquipment(p.getId(), EnumItemSlot.OFFHAND, isOff);*/
        MobEffect ppl = p.getEffect(MobEffects.n);
        //PacketPlayOutRemoveEntityEffect ppoedE = new PacketPlayOutRemoveEntityEffect(p.getId(), MobEffects.INVISIBILITY);
        //Packet<?> ppoee = ppl==null?null:(Packet<?>)new PacketPlayOutEntityEffect(p.getId(), ppl);
        this.player.showPlayer(plugin, player);
        p.b.sendPacket(new PacketPlayOutRemoveEntityEffect(p.getId(), MobEffects.n));
        if (ppl != null) {
            p.b.sendPacket(new PacketPlayOutEntityEffect(p.getId(), new MobEffect(ppl)) );
        }else {
            p.setInvisible(false);
        }
        for (Iterator<?> iterator = plist.iterator(); iterator.hasNext();) {
            Player player = (Player) iterator.next();
            Location loc = this.player.getLocation();
            loc.setY(0);
            player.sendBlockChange(loc, loc.getBlock().getBlockData());
            EntityPlayer ep = (EntityPlayer) ((CraftPlayer) player).getHandle();
            ep.b.sendPacket(ppoed);
            if (ep != p) {
                ep.b.sendPacket(ppones);
                ep.b.sendPacket(ppopi);
                ep.b.sendPacket(ppoeme);
                ep.b.sendPacket(ppoehr);
				/*if (isHelm != null) ep.playerConnection.sendPacket(ppoee0);
				if (isChest != null) ep.playerConnection.sendPacket(ppoee1);
				if (isLegs != null) ep.playerConnection.sendPacket(ppoee2);
				if (isBoots != null) ep.playerConnection.sendPacket(ppoee3);
				if (isMain != null) ep.playerConnection.sendPacket(ppoee4);
				if (isOff != null) ep.playerConnection.sendPacket(ppoee5);*/
                ep.b.sendPacket(new PacketPlayOutEntityEquipment(p.getId(), list));

            }
        }
        // p.playerConnection.sendPacket(ppoe);
        //this.player.removePotionEffect(PotionEffectType.INVISIBILITY);
        //this.player.sendMessage(this.effect.toString());
        //if (effect != null) {
        //player.addPotionEffect(effect);
        //}
        ArmorStand seat = (ArmorStand) ((LayPlayer_v1_17_R1) this.plugin.getLays().get(this.player.getUniqueId())).getRider();
        seat.remove();
        this.plugin.removeLay(this.player.getUniqueId());
        if (announce) {
            this.player.sendMessage(plugin.prefix+plugin.getConfig().getString("lang.lay.noLonger"));
        }
        if (player.isDead()) return;
        player.teleport(seat.getLocation().add(0,1.7,0).setDirection(player.getLocation().getDirection()));
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                player.teleport(seat.getLocation().add(0,1.7,0).setDirection(player.getLocation().getDirection())), 1L
        );

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
            LayPlayer_v1_17_R1 layPlayer = (LayPlayer_v1_17_R1) ilayPlayer;
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
    @Deprecated
    public IBlockAccess bedBlockAccess() {
        Float f = this.rotation;
        //return this.fakePlayer.t;
        return new IBlockAccess() {
            @Override
            public int getHeight() {
                return 255;
            }

            @Override
            public int getMinBuildHeight() {
                return 0;
            }

            @Override
            public IBlockData getType(BlockPosition arg0) {
                //Float f = p.getLocation().getYaw();
                EnumDirection a = bedDirection(f);
                return Blocks.aD.getBlockData().set(BlockProperties.P, a).set(BlockProperties.aX, BlockPropertyBedPart.a);
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
    public BlockData bedBlockData() {
        Bed bedBlockData = ((Bed) Material.WHITE_BED.createBlockData());
        bedBlockData.setFacing(bedFace(this.rotation));
        bedBlockData.setPart(Bed.Part.HEAD);
        return bedBlockData;
    }
    public static EnumDirection bedDirection(Float f) {
        f = (f < 0.0f) ? (360f + f) : f;
        f = f % 360;
        EnumDirection enumDirection = null;
        if (f >= 315 || f <= 45) { enumDirection = EnumDirection.c; }
        if (f >= 45 && f <= 135) { enumDirection = EnumDirection.f; }
        if (f >= 135 && f <= 225) { enumDirection = EnumDirection.d; }
        if (f >= 225 && f <= 315) { enumDirection = EnumDirection.e; }
        return enumDirection;
    }
    public static BlockFace bedFace(Float f) {
        f = (f < 0.0f) ? (360f + f) : f;
        f = f % 360;
        BlockFace enumDirection = null;
        if (f >= 315 || f <= 45) { enumDirection = BlockFace.NORTH; }
        if (f >= 45 && f <= 135) { enumDirection = BlockFace.EAST; }
        if (f >= 135 && f <= 225) { enumDirection = BlockFace.SOUTH; }
        if (f >= 225 && f <= 315) { enumDirection = BlockFace.WEST; }
        return enumDirection;
    }
    public static void upDateArmor(Main plugin) {
        Collection<? extends Player> plist = plugin.getServer().getOnlinePlayers();
        for (Iterator<?> iterator = plist.iterator(); iterator.hasNext();) {
            Player player = (Player) iterator.next();
            EntityPlayer ep = (EntityPlayer) ((CraftPlayer) player).getHandle();

            Collection<ILayPlayer> lay = plugin.getLays().values();
            for (Iterator<ILayPlayer> iterator2 = lay.iterator(); iterator2.hasNext();) {
                LayPlayer_v1_17_R1 layPlayer = (LayPlayer_v1_17_R1) iterator2.next();
                EntityPlayer p = (EntityPlayer) ((CraftPlayer) layPlayer.player).getHandle();
                ItemStack isHelm = p.getEquipment(EnumItemSlot.f);
                ItemStack isChest = p.getEquipment(EnumItemSlot.e);
                ItemStack isLegs = p.getEquipment(EnumItemSlot.d);
                ItemStack isBoots = p.getEquipment(EnumItemSlot.c);
                ItemStack isMain = p.getEquipment(EnumItemSlot.a);
                ItemStack isOff = p.getEquipment(EnumItemSlot.b);
                List<Pair<EnumItemSlot, ItemStack>> list = Arrays.asList(
                        new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.f,isHelm),
                        new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.e,isChest),
                        new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.d,isLegs),
                        new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.c,isBoots),
                        new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.a,isMain),
                        new Pair<EnumItemSlot, ItemStack>(EnumItemSlot.b,isOff)
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
                ep.b.sendPacket(new PacketPlayOutEntityEquipment(layPlayer.fakePlayer.getId(), list));
                Location location = layPlayer.getPlayer().getLocation();
                location.setY(0);
                player.sendBlockChange(location, layPlayer.bedBlockData());
                //ep.b.sendPacket(new PacketPlayOutBlockChange(layPlayer.fakePlayer.t, new BlockPosition(layPlayer.getPlayer().getLocation().getX(), 0, layPlayer.getPlayer().getLocation().getZ())));
                if (ep == p) {
                    ep.setInvisible(true);
                    continue;
                }
                ep.b.sendPacket(new PacketPlayOutEntityDestroy(p.getId()));

            }
        }
    }
    public static void showLayingPlayers(Main plugin,Player watcherPlayer) {

        for (Iterator<?> iterator = plugin.getLays().values().iterator(); iterator.hasNext();) {
            LayPlayer_v1_17_R1 LayPlayer = (LayPlayer_v1_17_R1) iterator.next();
            EntityPlayer entityPlayer = (EntityPlayer) ((CraftPlayer) LayPlayer.getPlayer()).getHandle();;
            PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook relMoveLook = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(LayPlayer.fakePlayer.getId(),(short)0,(short)2,(short)0,(byte)0,(byte)0,true);
            PacketPlayOutNamedEntitySpawn ppones = new PacketPlayOutNamedEntitySpawn(LayPlayer.fakePlayer);
            PacketPlayOutPlayerInfo ppopi = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, LayPlayer.fakePlayer);
            BlockPosition bedLoc = new BlockPosition(LayPlayer.getPlayer().getLocation().getX(), 0, LayPlayer.getPlayer().getLocation().getZ());
            PacketPlayOutBlockChange ppobc = new PacketPlayOutBlockChange(LayPlayer.bedBlockAccess(), bedLoc);
            PacketPlayOutEntityDestroy ppoed = new PacketPlayOutEntityDestroy(entityPlayer.getId());
            try {
                PacketPlayOutEntityMetadata ppoeme = new PacketPlayOutEntityMetadata(LayPlayer.fakePlayer.getId(), LayPlayer.datawatcher, true);
                EntityPlayer ep = (EntityPlayer) ((CraftPlayer) watcherPlayer).getHandle();
                ep.b.sendPacket(ppoed);
                ep.b.sendPacket(ppones);
                ep.b.sendPacket(ppopi);
                ep.b.sendPacket(ppoeme);
                ep.b.sendPacket(ppobc);
                ep.b.sendPacket(relMoveLook);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}