package ru.azerusteam.Classes;

import org.bukkit.entity.ArmorStand;

public interface ILayPlayer {
	void lay();
	void unLay(boolean announce);
	ArmorStand getRider();
	int getIdE();
}
