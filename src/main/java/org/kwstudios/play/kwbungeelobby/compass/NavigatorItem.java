package org.kwstudios.play.kwbungeelobby.compass;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.kwstudios.play.kwbungeelobby.toolbox.ConstantHolder;

public class NavigatorItem {

	public static ItemStack getCompassItem() {
		ItemStack compass = new ItemStack(Material.COMPASS);
		ItemMeta meta = compass.getItemMeta();
		meta.setDisplayName(ConstantHolder.NAVIGATOR_NAME);
		compass.setItemMeta(meta);
		return compass;
	}

}
