package org.kwstudios.play.kwbungeelobby.minigames;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.kwstudios.play.kwbungeelobby.database.MinecraftServerModel;
import org.kwstudios.play.kwbungeelobby.database.MySQLServerHandler;
import org.kwstudios.play.kwbungeelobby.loader.PluginLoader;
import org.kwstudios.play.kwbungeelobby.sender.JedisMessageSender;
import org.kwstudios.play.kwbungeelobby.signs.SignCreator;
import org.kwstudios.play.kwbungeelobby.toolbox.ConfigFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.gson.Gson;

public class MinigameRequests {

	private static HashMap<Sign, MinigameType> queuedRequests = new HashMap<Sign, MinigameType>();

	public static boolean createRequest(final MinigameType type, final Sign sign) {
		if (queuedRequests.containsKey(sign)) {
			return false;
		}
		System.out.println("It starts creating the request!");

		Bukkit.getServer().getScheduler().runTaskAsynchronously(PluginLoader.getInstance(), new Runnable() {
			@Override
			public void run() {
				System.out.println("It starts the asynchronous scheduler!");
				MinecraftServerModel server = MySQLServerHandler.getAvailableServer();
				if (server != null) {
					if (MinigameRequests.isLocalServer(server)) {
						System.out.println("It is a local server!");
						String command = ConfigFactory.getValueOrSetDefault("settings.minigames", "command",
								"ruby test.rb", PluginLoader.getInstance().getConfig());
						String commands[] = command.trim().split("\\s+");

						ProcessBuilder builder = new ProcessBuilder(commands);
						Map<String, String> map = builder.environment();
						map.put("GAME_TYPE", type.getText());
						map.put("MAP_NAME", SignCreator.getMapFromSign(sign));
						map.put("SERVER_NAME", server.getName());
						try {
							builder.start();
						} catch (IOException e) {
							e.printStackTrace();
						}

						queuedRequests.put(sign, type);
					} else {
						Gson gson = new Gson();
						String json = gson.toJson(server);
						JedisMessageSender.sendMessageToChannel(PluginLoader.getJedisValues().getHost(),
								PluginLoader.getJedisValues().getPort(), PluginLoader.getJedisValues().getPassword(),
								PluginLoader.getJedisValues().getMinigameCreationChannel(), json);
					}
				} else {
					// TODO No Servers available :-( Start self-destruction...
				}
			}
		});

		return true;
	}

	public static void removeQueuedRequest(Sign sign) {
		if (queuedRequests.containsKey(sign)) {
			queuedRequests.remove(sign);
		}
	}

	public static Sign getQueuedSignForType(MinigameType type) {
		for (Entry<Sign, MinigameType> request : queuedRequests.entrySet()) {
			if (request.getValue() == type) {
				return request.getKey();
			}
		}
		return null;
	}

	public static boolean isQueuedForRequest(MinigameType type) {
		for (Entry<Sign, MinigameType> request : queuedRequests.entrySet()) {
			if (request.getValue() == type) {
				return true;
			}
		}
		return false;
	}

	public static boolean isQueuedForRequest(Sign sign) {
		if (queuedRequests.containsKey(sign)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns true if the given MinecraftServerModel is a local server. More
	 * formally, returns true iff the Range r, which is a closed Range for the
	 * <i>lower</i> and <i>upper</i> values ({@code [lower..upper]} {@code {x
	 * |lower <= x <= upper}}) which should be set in the config.yml contains
	 * the Integer {@code server.getNumber()} n AND was not excluded in the
	 * {@code Set} e which should be set in the config.yml OR was included in
	 * the {@code Set} i which should also be set in the config.yml such that:
	 * <p>
	 * {@code ((r.contains(n) && !e.contains(n)) || i.contains(n)) == true}
	 * 
	 * @param server
	 * @return
	 */
	public static boolean isLocalServer(MinecraftServerModel server) {
		int lower = ConfigFactory.getValueOrSetDefault("settings.minigames.local-servers", "lower", 1,
				PluginLoader.getInstance().getConfig());
		int upper = ConfigFactory.getValueOrSetDefault("settings.minigames.local-servers", "upper", 1,
				PluginLoader.getInstance().getConfig());

		Set<Integer> exclude = ImmutableSet.copyOf(
				PluginLoader.getInstance().getConfig().getIntegerList("settings.minigames.local-servers.exclude"));
		Set<Integer> include = ImmutableSet.copyOf(
				PluginLoader.getInstance().getConfig().getIntegerList("settings.minigames.local-servers.include"));

		Range<Integer> range = Range.closed(lower, upper);

		if ((range.contains(server.getNumber()) && !exclude.contains(server.getNumber()))
				|| include.contains(server.getNumber())) {
			return true;
		}
		return false;
	}

}
