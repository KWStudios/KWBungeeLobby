package org.kwstudios.play.kwbungeelobby.toolbox;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.kwstudios.play.kwbungeelobby.loader.PluginLoader;
import org.kwstudios.play.kwbungeelobby.minigames.LoadingScreenMessages;

public class FancyMessages {

	private static HashMap<Player, BukkitTask> randomMessages = new HashMap<Player, BukkitTask>();

	public static void sendFancyMessage(Player player, String boringMessage) {
		String characters = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
		player.sendMessage(ChatColor.GOLD.toString() + ChatColor.MAGIC.toString() + characters);
		player.sendMessage("");
		player.sendMessage(boringMessage);
		player.sendMessage("");
		player.sendMessage(ChatColor.GOLD.toString() + ChatColor.MAGIC.toString() + characters);
	}

	public static void startRandomMessages(final Player player) {
		if (!randomMessages.containsKey(player)) {
			BukkitTask task = Bukkit.getServer().getScheduler().runTaskTimer(PluginLoader.getInstance(),
					new Runnable() {
						@Override
						public void run() {
							if (!player.isOnline()) {
								randomMessages.get(player).cancel();
							}
							player.sendMessage(ChatColor.GOLD + LoadingScreenMessages.getInstance().getRandomMessage());
						}
					}, 64, 64);
			randomMessages.put(player, task);
		}
	}

	public static HashMap<Player, BukkitTask> getRandomMessages() {
		return randomMessages;
	}

}
