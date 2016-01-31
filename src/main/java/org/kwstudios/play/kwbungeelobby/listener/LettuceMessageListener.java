package org.kwstudios.play.kwbungeelobby.listener;

import org.bukkit.Bukkit;
import org.kwstudios.play.kwbungeelobby.loader.PluginLoader;

import com.lambdaworks.redis.pubsub.RedisPubSubConnection;
import com.lambdaworks.redis.pubsub.RedisPubSubListener;

public abstract class LettuceMessageListener {

	private String channels[];

	public LettuceMessageListener(String... channels) {
		this.channels = channels;
		setupListener();
	}

	public abstract void taskOnMessageReceive(String channel, String message);

	private void setupListener() {
		RedisPubSubConnection<String, String> connection = PluginLoader.getRedisClient().connectPubSub();
		connection.addListener(new RedisPubSubListener<String, String>() {

			@Override
			public void unsubscribed(String channel, long count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void subscribed(String channel, long count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void punsubscribed(String pattern, long count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void psubscribed(String pattern, long count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void message(String pattern, String channel, String message) {
				// TODO Auto-generated method stub

			}

			@Override
			public void message(String channel, String message) {
				// TODO Auto-generated method stub
				Bukkit.getConsoleSender().sendMessage("Lettuce received a new message from the Redis Host!");
				Bukkit.getConsoleSender().sendMessage(channel);
				Bukkit.getConsoleSender().sendMessage(message);
				taskOnMessageReceive(channel, message);

			}
		});

		connection.subscribe(channels);
	}

}
