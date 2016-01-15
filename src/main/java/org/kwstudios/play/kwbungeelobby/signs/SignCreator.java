package org.kwstudios.play.kwbungeelobby.signs;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.block.SignChangeEvent;
import org.kwstudios.play.kwbungeelobby.loader.PluginLoader;
import org.kwstudios.play.kwbungeelobby.minigames.GetMaps;
import org.kwstudios.play.kwbungeelobby.toolbox.ConfigFactory;

public class SignCreator {

	public synchronized static boolean createNewSign(Sign sign, String map) {
		// TODO Check the way the signs are stored in the SignConfiguration
		File file = SignConfiguration.getYamlSignsFile();
		FileConfiguration fileConfiguration = SignConfiguration.getSignConfiguration();

		Set<String> signs = ConfigFactory.getKeysUnderPath("signs", false, fileConfiguration);
		if (signs != null) {
			if (signs.contains(Integer.toString(sign.getX()) + Integer.toString(sign.getY())
					+ Integer.toString(sign.getZ()) + sign.getWorld().getName())) {
				return false;
			}
		}

		int x = sign.getX();
		int y = sign.getY();
		int z = sign.getZ();
		String world = sign.getWorld().getName();
		String path = "signs." + Integer.toString(x) + Integer.toString(y) + Integer.toString(z) + world;

		ConfigFactory.setString(path, "map", map, fileConfiguration);
		ConfigFactory.setString(path, "world", world, fileConfiguration);
		ConfigFactory.setInt(path, "x", x, fileConfiguration);
		ConfigFactory.setInt(path, "y", y, fileConfiguration);
		ConfigFactory.setInt(path, "z", z, fileConfiguration);

		try {
			fileConfiguration.save(file);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	public synchronized static boolean removeSign(Sign sign) {
		File file = SignConfiguration.getYamlSignsFile();
		FileConfiguration fileConfiguration = SignConfiguration.getSignConfiguration();

		int x = sign.getX();
		int y = sign.getY();
		int z = sign.getZ();
		String world = sign.getWorld().getName();
		String path = "signs." + Integer.toString(x) + Integer.toString(y) + Integer.toString(z) + world;

		if (fileConfiguration.contains(path)) {
			fileConfiguration.set(path, null);
			try {
				fileConfiguration.save(file);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	public static boolean resetSign(SignChangeEvent sign) {
		FileConfiguration fileConfiguration = SignConfiguration.getSignConfiguration();

		int x = sign.getBlock().getState().getX();
		int y = sign.getBlock().getState().getY();
		int z = sign.getBlock().getState().getZ();
		String world = sign.getBlock().getState().getWorld().getName();
		String path = "signs." + Integer.toString(x) + Integer.toString(y) + Integer.toString(z) + world;

		if (fileConfiguration.contains(path)) {
			String map = ConfigFactory.getString(path, "map", fileConfiguration);

			String first = getLineFormatted(map,
					ConfigFactory.getString("settings.signs", "first-line", PluginLoader.getInstance().getConfig()), 0);

			String second = getLineFormatted(map,
					ConfigFactory.getString("settings.signs", "second-line", PluginLoader.getInstance().getConfig()),
					0);

			String third = getLineFormatted(map,
					ConfigFactory.getString("settings.signs", "third-line", PluginLoader.getInstance().getConfig()), 0);

			String fourth = getLineFormatted(map,
					ConfigFactory.getString("settings.signs", "fourth-line", PluginLoader.getInstance().getConfig()),
					0);

			if (!first.isEmpty()) {
				sign.setLine(0, first);
			}
			if (!second.isEmpty()) {
				sign.setLine(1, second);
			}
			if (!third.isEmpty()) {
				sign.setLine(2, third);
			}
			if (!fourth.isEmpty()) {
				sign.setLine(3, fourth);
			}

			if (sign.getBlock().getState().update()) {
				return true;
			}
		}

		return false;
	}

	public static boolean updateSign(Sign sign, int currentPlayers) {
		FileConfiguration fileConfiguration = SignConfiguration.getSignConfiguration();

		int x = sign.getX();
		int y = sign.getY();
		int z = sign.getZ();
		String world = sign.getWorld().getName();
		String path = "signs." + Integer.toString(x) + Integer.toString(y) + Integer.toString(z) + world;

		if (fileConfiguration.contains(path)) {
			String map = ConfigFactory.getString(path, "map", fileConfiguration);

			String first = getLineFormatted(map,
					ConfigFactory.getString("settings.signs", "first-line", PluginLoader.getInstance().getConfig()),
					currentPlayers);

			String second = getLineFormatted(map,
					ConfigFactory.getString("settings.signs", "second-line", PluginLoader.getInstance().getConfig()),
					currentPlayers);

			String third = getLineFormatted(map,
					ConfigFactory.getString("settings.signs", "third-line", PluginLoader.getInstance().getConfig()),
					currentPlayers);

			String fourth = getLineFormatted(map,
					ConfigFactory.getString("settings.signs", "fourth-line", PluginLoader.getInstance().getConfig()),
					currentPlayers);

			if (!first.isEmpty()) {
				sign.setLine(0, first);
			}
			if (!second.isEmpty()) {
				sign.setLine(1, second);
			}
			if (!third.isEmpty()) {
				sign.setLine(2, third);
			}
			if (!fourth.isEmpty()) {
				sign.setLine(3, fourth);
			}

			if (sign.update()) {
				return true;
			}
		}

		return false;
	}

	private static String getLineFormatted(String map, String line, int currentPlayers) {
		if (line == null) {
			return "";
		}
		if (line.isEmpty()) {
			return "";
		}

		String size = Integer
				.toString(ConfigFactory.getValueOrSetDefault("settings.maps." + map, "teams", 1,
						PluginLoader.getInstance().getConfig()))
				+ "x" + Integer.toString(ConfigFactory.getValueOrSetDefault("settings.maps." + map, "players-per-team",
						1, PluginLoader.getInstance().getConfig()));

		int maxPlayers = ConfigFactory.getValueOrSetDefault("settings.maps." + map, "teams", 1,
				PluginLoader.getInstance().getConfig())
				* ConfigFactory.getValueOrSetDefault("settings.maps." + map, "players-per-team", 1,
						PluginLoader.getInstance().getConfig());
		String slots = Integer.toString(currentPlayers) + "/" + Integer.toString(maxPlayers);

		String status;
		if (currentPlayers >= maxPlayers) {
			status = "[" + ChatColor.GOLD + "Lobby" + "]";
		} else {
			status = "[" + ChatColor.GREEN + "Lobby" + "]";
		}

		String mapCropped;
		int characterCount = map.length() + " ".length() + size.length();
		if (characterCount > 16) {
			mapCropped = map.substring(0, map.length() - (characterCount - 16));
		}

		String rawLine = line.replace("$$", "").replace("$STATUS$", status).replace("$MAP_NAME$", map)
				.replace("$SIZE$", size).replace("$SLOTS$", slots);
		return ChatColor.translateAlternateColorCodes('&', rawLine);
	}

	/**
	 * Checks whether the given Sign is properly configured to be an JoinSign or
	 * not.
	 * 
	 * @param sign
	 *            The Sign for which the check should be done.
	 * @return True if the signs.yml contains a correct value for the given Sign
	 *         instance.
	 */
	public static boolean isJoinSign(Sign sign) {
		// TODO Validate sign via its location and a list of sign locations.
		FileConfiguration fileConfiguration = SignConfiguration.getSignConfiguration();

		Location signPosition = sign.getLocation();

		Set<String> signs = ConfigFactory.getKeysUnderPath("signs", false, fileConfiguration);
		if (signs != null) {
			for (String signString : signs) {
				String path = "signs." + signString;
				String map = ConfigFactory.getString(path, "map", fileConfiguration);
				for (String mapName : GetMaps.getMapNames(PluginLoader.getInstance().getConfig())) {
					if (map.trim().equalsIgnoreCase(mapName.trim())) {
						int x = ConfigFactory.getInt(path, "x", fileConfiguration);
						int y = ConfigFactory.getInt(path, "y", fileConfiguration);
						int z = ConfigFactory.getInt(path, "z", fileConfiguration);
						String world = ConfigFactory.getString(path, "world", fileConfiguration);
						Location signLocation = new Location(Bukkit.getWorld(world), x, y, z);
						if (signPosition.getBlockX() == x && signPosition.getBlockY() == y
								&& signPosition.getBlockZ() == z) {
							if (signLocation.getBlock().getState() instanceof Sign) {
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}

	public static String getMapFromSign(Sign sign) {
		FileConfiguration fileConfiguration = SignConfiguration.getSignConfiguration();

		int x = sign.getX();
		int y = sign.getY();
		int z = sign.getZ();
		String world = sign.getWorld().getName();
		String signString = Integer.toString(x) + Integer.toString(y) + Integer.toString(z) + world;
		String path = "signs." + signString;
		String map = ConfigFactory.getString(path, "map", fileConfiguration);
		for (String mapName : GetMaps.getMapNames(PluginLoader.getInstance().getConfig())) {
			if (map.trim().equalsIgnoreCase(mapName.trim())) {
				return map;
			}
		}

		return "";
	}

}
