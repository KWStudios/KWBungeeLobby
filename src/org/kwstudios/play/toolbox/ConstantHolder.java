package org.kwstudios.play.toolbox;

import org.bukkit.ChatColor;

public class ConstantHolder {

	public static final String MOTD_PREFIX = ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD.toString() + "KWStudios"
			+ ChatColor.RESET + ":" + " " + ChatColor.GOLD + ChatColor.ITALIC;
	
	public static final String API_URL = "https://api.kwstudios.org/minecraft/server/:server/players/:player/storedata";

}
