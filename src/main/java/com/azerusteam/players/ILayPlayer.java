package com.azerusteam.players;

import com.azerusteam.wrappers.EntityPlayer;
import com.azerusteam.wrappers.EnumItemSlot;
import com.azerusteam.wrappers.Packet;
import com.azerusteam.wrappers.PacketPlayOutEntityEquipment;
import com.mojang.datafixers.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public interface ILayPlayer {
	void lay();
	void unLay(boolean announce);
	ArmorStand getRider();
	int getFakePlayerID();
	EntityPlayer getFakePlayer();
	Player getPlayer();
	float getRotation();

	default BlockData bedBlockData() {
		return Bukkit.createBlockData(Material.RED_BED, d -> {
			Bed data = (Bed) d;
			data.setFacing(bedDirection(this.getRotation()));
			data.setPart(Bed.Part.HEAD);
		});
	}

	static BlockFace bedDirection(float f) {
		f = (f < 0f ? 360f + f : f) % 360;
		if (f >= 315 || f <= 45) return BlockFace.NORTH;
		if (f >= 45 && f <= 135) return BlockFace.EAST;
		if (f >= 135 && f <= 225) return BlockFace.SOUTH;
		if (f >= 225 && f <= 315) return BlockFace.WEST;
		return null;
	}

	static Packet[] getEquipmentPackets(EntityPlayer entityPlayer, int fakePlayerId) { // 1.13-1.15.2
		return getEquipmentPackets(entityPlayer.getBukkitEntity(), fakePlayerId);
	}

	static Packet[] getEquipmentPackets(Player player, int fakePlayerId) { // 1.13-1.15.2

		EntityEquipment equipment = player.getEquipment();
		if (equipment == null) return new Packet[0];
		ItemStack helmet = equipment.getHelmet();
		ItemStack chestplate = equipment.getChestplate();
		ItemStack leggings = equipment.getLeggings();
		ItemStack boots = equipment.getBoots();
		ItemStack mainHand = equipment.getItemInMainHand();
		ItemStack offHand = equipment.getItemInOffHand();
		ArrayList<Packet> packets = new ArrayList<>();
		if (helmet != null) packets.add(new PacketPlayOutEntityEquipment(fakePlayerId, EnumItemSlot.HEAD, helmet));
		if (chestplate != null) packets.add(new PacketPlayOutEntityEquipment(fakePlayerId, EnumItemSlot.CHEST, chestplate));
		if (leggings != null) packets.add(new PacketPlayOutEntityEquipment(fakePlayerId, EnumItemSlot.LEGS, leggings));
		if (boots != null) packets.add(new PacketPlayOutEntityEquipment(fakePlayerId, EnumItemSlot.FEET, boots));
		if (mainHand != null) packets.add(new PacketPlayOutEntityEquipment(fakePlayerId, EnumItemSlot.MAINHAND, mainHand));
		if (offHand != null) packets.add(new PacketPlayOutEntityEquipment(fakePlayerId, EnumItemSlot.OFFHAND, offHand));
		return packets.toArray(new Packet[0]);
	}

	static Packet getEquipmentPacket(EntityPlayer entityPlayer, int fakePlayerId) { // 1.16+
		return getEquipmentPacket(entityPlayer.getBukkitEntity(), fakePlayerId);
	}

	static Packet getEquipmentPacket(Player player, int fakePlayerId) { // 1.16+
		ArrayList<Pair<EnumItemSlot, ItemStack>> slots = new ArrayList<>();
		slots.add(new Pair<>(EnumItemSlot.HEAD, player.getEquipment().getHelmet()));
		slots.add(new Pair<>(EnumItemSlot.CHEST, player.getEquipment().getChestplate()));
		slots.add(new Pair<>(EnumItemSlot.LEGS, player.getEquipment().getLeggings()));
		slots.add(new Pair<>(EnumItemSlot.FEET, player.getEquipment().getBoots()));
		slots.add(new Pair<>(EnumItemSlot.MAINHAND, player.getEquipment().getItemInMainHand()));
		slots.add(new Pair<>(EnumItemSlot.OFFHAND, player.getEquipment().getItemInOffHand()));
		return new PacketPlayOutEntityEquipment(fakePlayerId, slots);
	}

}
