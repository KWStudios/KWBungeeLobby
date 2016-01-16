package org.kwstudios.play.kwbungeelobby.loader;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.kwstudios.play.kwbungeelobby.commands.CommandParser;
import org.kwstudios.play.kwbungeelobby.holders.JedisValues;
import org.kwstudios.play.kwbungeelobby.listener.BungeeMessageListener;
import org.kwstudios.play.kwbungeelobby.listener.JedisMessageListener;
import org.kwstudios.play.kwbungeelobby.minigames.MinigameServerHolder;
import org.kwstudios.play.kwbungeelobby.signs.SignConfiguration;
import org.kwstudios.play.kwbungeelobby.toolbox.ConfigFactory;
import redis.clients.jedis.Protocol;

public class PluginLoader extends JavaPlugin {

	private static PluginLoader instance = null;

	private static JedisMessageListener lobbyChannelListener = null;
	private static JedisValues jedisValues = new JedisValues();
	private static HashMap<String, MinigameServerHolder> serverHolders = new HashMap<String, MinigameServerHolder>();

	@Override
	public void onEnable() {
		super.onEnable();
		
		this.saveDefaultConfig();

		PluginLoader.instance = this;

		PluginDescriptionFile pluginDescriptionFile = getDescription();
		Logger logger = Logger.getLogger("Minecraft");

		new EventListener(this, getConfig());

		logger.info(pluginDescriptionFile.getName() + " was loaded successfully! (Version: "
				+ pluginDescriptionFile.getVersion() + ")");
		// getConfig().options().copyDefaults(true);
		// saveConfig();

		SignConfiguration.initSignConfiguration();

		// TODO Use BungeeCord messaging for Player-save actions
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		new BungeeMessageListener();

		// Jedis Listener Setup

		reloadJedisConfig();

		setupJedisListener();

		setupServerHolders();

		reloadSignConfig();

		saveConfig();
	}

	@Override
	public void onDisable() {
		super.onDisable();

		// Jedis stuff
		lobbyChannelListener.getJedisPubSub().unsubscribe();

		PluginDescriptionFile pluginDescriptionFile = getDescription();
		Logger logger = Logger.getLogger("Minecraft");

		logger.info(pluginDescriptionFile.getName() + " was unloaded successfully! (Version: "
				+ pluginDescriptionFile.getVersion() + ")");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a Player!");
			return false;
		}

		Player player = (Player) sender;

		CommandParser commandParser = new CommandParser(player, command, label, args, getConfig());
		if (!commandParser.isCommand()) {
			return false;
		}

		saveConfig();

		return true;
	}

	public void reloadJedisConfig() {
		String host = ConfigFactory.getValueOrSetDefault("settings.jedis", "host", Protocol.DEFAULT_HOST, getConfig());
		jedisValues.setHost(host);

		int port = ConfigFactory.getValueOrSetDefault("settings.jedis", "port", Protocol.DEFAULT_PORT, getConfig());
		jedisValues.setPort(port);

		String password = ConfigFactory.getValueOrSetDefault("settings.jedis", "password", null, getConfig());
		jedisValues.setPassword(password);

		List<String> channelsToListen = getConfig().getStringList("settings.jedis.channelsToListen");
		if (channelsToListen.isEmpty()) {
			channelsToListen.add("lobby");
			channelsToListen.add("anotherChannelToListen");
			getConfig().set("settings.jedis.channelsToListen", channelsToListen);
		}
		jedisValues.setChannelsToListen(channelsToListen.toArray(new String[channelsToListen.size()]));

		String channelToSend = ConfigFactory.getValueOrSetDefault("settings.jedis", "channelToSend", "minigame",
				getConfig());
		jedisValues.setChannelToSend(channelToSend);

		String creationChannel = ConfigFactory.getValueOrSetDefault("settings.minigames.jedis", "creation-channel",
				"minigame-server-creation", getConfig());
		jedisValues.setMinigameCreationChannel(creationChannel);
	}

	private void setupJedisListener() {
		PluginLoader.lobbyChannelListener = new JedisMessageListener(jedisValues.getHost(), jedisValues.getPort(),
				jedisValues.getPassword(), jedisValues.getChannelsToListen()) {
			@Override
			public synchronized void taskOnMessageReceive(String channel, String message) {
				if (PluginLoader.getServerHolders().containsKey(channel)) {
					PluginLoader.getServerHolders().get(channel).parseMessage(message);
				} else {
					MinigameServerHolder parser = new MinigameServerHolder(channel);
					parser.parseMessage(message);
					PluginLoader.getServerHolders().put(channel, parser);
				}
			}
		};
	}

	private void setupServerHolders() {
		for (String channel : jedisValues.getChannelsToListen()) {
			if (!PluginLoader.getServerHolders().containsKey(channel)) {
				MinigameServerHolder holder = new MinigameServerHolder(channel);
				PluginLoader.getServerHolders().put(channel, holder);
			}
		}
	}

	private void reloadSignConfig() {
		ConfigFactory.getValueOrSetDefault("settings.signs", "first-line", "$$", getConfig());
		ConfigFactory.getValueOrSetDefault("settings.signs", "second-line", "$STATUS$", getConfig());
		ConfigFactory.getValueOrSetDefault("settings.signs", "third-line", "$MAP_NAME$ $SIZE$", getConfig());
		ConfigFactory.getValueOrSetDefault("settings.signs", "fourth-line", "$SLOTS$", getConfig());
	}

	public static HashMap<String, MinigameServerHolder> getServerHolders() {
		return serverHolders;
	}

	public static JedisValues getJedisValues() {
		return jedisValues;
	}

	public static PluginLoader getInstance() {
		return PluginLoader.instance;
	}

}
