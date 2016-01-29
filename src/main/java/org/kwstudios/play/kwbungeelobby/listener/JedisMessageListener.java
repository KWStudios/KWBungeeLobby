package org.kwstudios.play.kwbungeelobby.listener;

import org.bukkit.Bukkit;
import org.kwstudios.play.kwbungeelobby.loader.PluginLoader;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Protocol;

public abstract class JedisMessageListener {

	private JedisPubSub jedisPubSub;
	private String server;
	private int port;
	private String password;
	private String[] channel;

	public JedisMessageListener(String server, int port, String password, String... channel) {
		this.server = server;
		this.port = port;
		this.password = password;
		this.channel = channel;
		jedisPubSub = setupSubscriber();
	}

	@Deprecated
	public JedisMessageListener(String server, String password, String... channel) {
		this(server, Protocol.DEFAULT_PORT, password, channel);
	}

	@Deprecated
	public JedisMessageListener(String server, String[] channel) {
		this(server, Protocol.DEFAULT_PORT, null, channel);
	}

	@Deprecated
	public JedisMessageListener(String server, int port, String[] channel) {
		this(server, port, null, channel);
	}


	public abstract void taskOnMessageReceive(String channel, String message);

	private JedisPubSub setupSubscriber() {
		final JedisPubSub jedisPubSub = new JedisPubSub() {
			@Override
			public void onUnsubscribe(String channel, int subscribedChannels) {
				Bukkit.getConsoleSender().sendMessage("Jedis unsubscribed successfully from the Redis Host!");
			}

			@Override
			public void onSubscribe(String channel, int subscribedChannels) {
				Bukkit.getConsoleSender().sendMessage("Jedis subscribed successfully at the Redis Host!");
			}

			@Override
			public void onPUnsubscribe(String pattern, int subscribedChannels) {
			}

			@Override
			public void onPSubscribe(String pattern, int subscribedChannels) {
			}

			@Override
			public void onPMessage(String pattern, String channel, String message) {
			}

			@Override
			public void onMessage(String channel, String message) {
				Bukkit.getConsoleSender().sendMessage("Jedis received a new message from the Redis Host!");
				Bukkit.getConsoleSender().sendMessage(channel);
				Bukkit.getConsoleSender().sendMessage(message);
				taskOnMessageReceive(channel, message);
			}
		};
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Bukkit.getConsoleSender().sendMessage("Jedis is connecting to the Redis Host!");
					Jedis jedis = PluginLoader.getJedisPool().getResource();
					/*if (password != null) {
						jedis.auth(password);
					}*/
					Bukkit.getConsoleSender().sendMessage("Jedis is subscribing for a channel at the Redis Host!");
					jedis.subscribe(jedisPubSub, channel);
					jedis.quit();
					// jedis.close();
				} catch (Exception e) {
					e.printStackTrace();
					// e.printStackTrace();
				}
			}
		}).start();
		return jedisPubSub;
	}

	public JedisPubSub getJedisPubSub() {
		return jedisPubSub;
	}

}
