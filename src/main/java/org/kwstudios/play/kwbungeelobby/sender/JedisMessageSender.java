package org.kwstudios.play.kwbungeelobby.sender;

import org.bukkit.Bukkit;
import org.kwstudios.play.kwbungeelobby.loader.PluginLoader;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;

public class JedisMessageSender {

	@Deprecated
	public static void sendMessageToChannel(String server, String password, String channel, String message) {
		JedisMessageSender.sendMessageToChannel(server, Protocol.DEFAULT_PORT, password, channel, message);
	}

	@Deprecated
	public static void sendMessageToChannel(String server, String channel, String message) {
		JedisMessageSender.sendMessageToChannel(server, Protocol.DEFAULT_PORT, null, channel, message);
	}

	@Deprecated
	public static void sendMessageToChannel(String server, int port, String channel, String message) {
		JedisMessageSender.sendMessageToChannel(server, port, null, channel, message);
	}

	public static void sendMessageToChannel(final String server, final int port, final String password,
			final String channel, final String message) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Bukkit.getConsoleSender().sendMessage("Jedis is connecting to the Redis Host!");
					Jedis jedis = PluginLoader.getJedisPool().getResource();
					/*if (password != null) {
						jedis.auth(password);
					}*/
					Bukkit.getConsoleSender().sendMessage("Jedis is publishing the given Message to the Redis Host!");
					jedis.publish(channel, message);
					jedis.close();
//					jedis.quit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}
