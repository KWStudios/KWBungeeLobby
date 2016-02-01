package org.kwstudios.play.kwbungeelobby.compass;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.kwstudios.play.kwbungeelobby.loader.PluginLoader;

public class CompassManager {

	private static Inventory compass;

	public static void createCompass() {
		compass = Bukkit.createInventory(null, InventoryType.CHEST, ChatColor.GREEN + "Navigator");

		@SuppressWarnings("unchecked")
		List<CompassItem> items = (List<CompassItem>) (List<?>) PluginLoader.getInstance().getConfig()
				.getList("settings.compass.items");

		if (items != null) {
			for (CompassItem item : items) {
				if (item != null) {
					compass.setItem(item.getPosition(), item.getItemStack());
				}
			}
		} else {
			List<String> l = new ArrayList<String>();
			l.add("To the JoinSigns");
			Location loc = new Location(Bukkit.getWorld("world"), 0, 0, 0);
			CompassItem value = new CompassItem(Material.BOW, 0, "RageMode", l, loc);

			compass.setItem(value.getPosition(), value.getItemStack());

			List<CompassItem> defaults = new ArrayList<CompassItem>();
			defaults.add(value);

			PluginLoader.getInstance().getConfig().set("settings.compass.items", defaults);
			PluginLoader.getInstance().saveConfig();
		}
	}

	/**
	 * Opens the compass for the given player.
	 * <p>
	 * This method is NOT Thread-safe. Never call it from a background Thread.
	 * 
	 * @param player
	 *            The compass will be opened for this player
	 */
	public static void openCompass(Player player) {
		if (compass == null) {
			CompassManager.createCompass();
		}
		player.openInventory(compass);
	}

	/**
	 * Returns the globally unique instance of Inventory which is basically the
	 * inventory of the compass.
	 * <p>
	 * This method is NOT Thread-safe. Never call it from a background Thread.
	 * 
	 * @return The compass inventory
	 */
	public static Inventory getCompass() {
		if (compass == null) {
			CompassManager.createCompass();
		}
		return compass;
	}

	/**
	 * Returns the Location (if any) which was set in the config.yml for the
	 * given Slot.
	 * <p>
	 * This method is NOT Thread-safe. Never call it from a background Thread.
	 * 
	 * @param slot
	 *            The slot, where the Location should be read from
	 * @return The Location for the slot which was set in the config.yml or null
	 */
	public static Location getLocation(int slot) {
		if (compass == null) {
			CompassManager.createCompass();
		}

		@SuppressWarnings("unchecked")
		List<CompassItem> items = (List<CompassItem>) (List<?>) PluginLoader.getInstance().getConfig()
				.getList("settings.compass.items");

		if (items != null) {
			for (CompassItem item : items) {
				if (item != null) {
					if (item.getPosition() == slot) {
						Location loc = item.getLocation();
						if (loc != null) {
							return loc;
						}
					}
				}
			}
		}
		return null;

	}

}
