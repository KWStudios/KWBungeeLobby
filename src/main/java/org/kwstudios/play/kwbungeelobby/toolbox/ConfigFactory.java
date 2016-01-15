package org.kwstudios.play.kwbungeelobby.toolbox;

import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigFactory {

	// private static final File CONFIG_DIRECTORY = new File("KWPlugin");
	// private static final File CONFIG_FILE = new File("KWPlugin/config.yml");
	// private static YamlConfiguration config = null;

	// public static YamlConfiguration getConfig() {
	// createConfigFile();
	// if (config == null) {
	// config = YamlConfiguration.loadConfiguration(configFile);
	// }
	// return config;
	// }

	public static Boolean getBoolean(String path, String key, FileConfiguration fileConfiguration) {
		return fileConfiguration.getBoolean(path + "." + key);
	}

	public static void setBoolean(String path, String key, Boolean value, FileConfiguration fileConfiguration) {
		fileConfiguration.set(path + "." + key, value);
		// save();
	}

	public static String getString(String path, String key, FileConfiguration fileConfiguration) {
		return fileConfiguration.getString(path + "." + key);
	}

	public static void setString(String path, String key, String value, FileConfiguration fileConfiguration) {
		fileConfiguration.set(path + "." + key, value);
		// save();
	}

	public static int getInt(String path, String key, FileConfiguration fileConfiguration) {
		return fileConfiguration.getInt(path + "." + key);
	}

	public static void setInt(String path, String key, int value, FileConfiguration fileConfiguration) {
		fileConfiguration.set(path + "." + key, value);
		// save();
	}

	public static double getDouble(String path, String key, FileConfiguration fileConfiguration) {
		return fileConfiguration.getDouble(path + "." + key);
	}

	public static void setDouble(String path, String key, double value, FileConfiguration fileConfiguration) {
		fileConfiguration.set(path + "." + key, value);
		// save();
	}

	public static Set<String> getKeysUnderPath(String path, boolean deep, FileConfiguration fileConfiguration) {
		if(fileConfiguration != null) {
			if(fileConfiguration.getConfigurationSection(path) != null) {
				Set<String> keys = fileConfiguration.getConfigurationSection(path).getKeys(deep);
				return keys;			
			}			
		}

		return null;
	}

	public static String getValueOrSetDefault(String path, String key, String defaultValue,
			FileConfiguration fileConfiguration) {
		if (!fileConfiguration.isSet(path + "." + key)) {
			ConfigFactory.setString(path, key, defaultValue, fileConfiguration);
			return defaultValue;
		} else {
			String value = ConfigFactory.getString(path, key, fileConfiguration);
			if (value.trim().isEmpty()) {
				ConfigFactory.setString(path, key, defaultValue, fileConfiguration);
				return defaultValue;
			} else {
				return value;
			}
		}
	}

	public static int getValueOrSetDefault(String path, String key, int defaultValue,
			FileConfiguration fileConfiguration) {
		if (!fileConfiguration.isSet(path + "." + key)) {
			ConfigFactory.setInt(path, key, defaultValue, fileConfiguration);
			return defaultValue;
		} else {
			String value = ConfigFactory.getString(path, key, fileConfiguration);
			if (!ValueChecker.isInt(value)) {
				ConfigFactory.setInt(path, key, defaultValue, fileConfiguration);
				return defaultValue;
			} else {
				return Integer.parseInt(value);
			}
		}
	}

	public static double getValueOrSetDefault(String path, String key, double defaultValue,
			FileConfiguration fileConfiguration) {
		if (!fileConfiguration.isSet(path + "." + key)) {
			ConfigFactory.setDouble(path, key, defaultValue, fileConfiguration);
			return defaultValue;
		} else {
			String value = ConfigFactory.getString(path, key, fileConfiguration);
			if (!ValueChecker.isDouble(value)) {
				ConfigFactory.setDouble(path, key, defaultValue, fileConfiguration);
				return defaultValue;
			} else {
				return Double.parseDouble(value);
			}
		}
	}

	public static boolean getValueOrSetDefault(String path, String key, boolean defaultValue,
			FileConfiguration fileConfiguration) {
		if (!fileConfiguration.isSet(path + "." + key)) {
			ConfigFactory.setBoolean(path, key, defaultValue, fileConfiguration);
			return defaultValue;
		} else {
			boolean value = ConfigFactory.getBoolean(path, key, fileConfiguration);
			return value;
		}
	}

	// private static void save(FileConfiguration fileConfiguration) {
	// createConfigFile();
	// try {
	// fileConfiguration.sa;
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	// private static void createConfigFile() {
	// if (!configDirectory.exists()) {
	// configDirectory.mkdir();
	// }
	//
	// if (!configFile.exists()) {
	// try {
	// configFile.createNewFile();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }

}
