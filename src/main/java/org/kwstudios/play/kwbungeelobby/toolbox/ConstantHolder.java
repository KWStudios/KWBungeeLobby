package org.kwstudios.play.kwbungeelobby.toolbox;

import org.bukkit.ChatColor;

public class ConstantHolder {

	public static final String MOTD_PREFIX = ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD.toString() + "KWStudios"
			+ ChatColor.RESET + ":" + " " + ChatColor.GOLD + ChatColor.ITALIC;

	public static final String API_URL = "https://api.kwstudios.org/minecraft/server/:server/players/:player/storedata";

	public static final String JEDIS_SERVER = "46.101.230.181";

	public static final String NAVIGATOR_NAME = ChatColor.GREEN + "Navigator";

}
