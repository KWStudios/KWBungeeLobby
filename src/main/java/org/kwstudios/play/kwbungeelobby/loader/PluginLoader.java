package org.kwstudios.play.kwbungeelobby.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.kwstudios.play.kwbungeelobby.commands.BaseCommand;
import org.kwstudios.play.kwbungeelobby.commands.ReloadCommand;
import org.kwstudios.play.kwbungeelobby.compass.CompassItem;
import org.kwstudios.play.kwbungeelobby.holders.JedisValues;
import org.kwstudios.play.kwbungeelobby.listener.JedisMessageListener;
import org.kwstudios.play.kwbungeelobby.listener.KWChannelMessageListener;
import org.kwstudios.play.kwbungeelobby.minigames.LoadingScreenMessages;
import org.kwstudios.play.kwbungeelobby.minigames.MinigameRequests;
import org.kwstudios.play.kwbungeelobby.minigames.MinigameServerHolder;
import org.kwstudios.play.kwbungeelobby.signs.SignConfiguration;
import org.kwstudios.play.kwbungeelobby.signs.SignCreator;
import org.kwstudios.play.kwbungeelobby.toolbox.ConfigFactory;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.common.collect.Iterables;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class PluginLoader extends JavaPlugin {

	private static PluginLoader instance = null;

	private static JedisMessageListener lobbyChannelListener = null;
	private static JedisValues jedisValues = new JedisValues();
	private static JedisPool jedisPool;

	private static HashMap<String, MinigameServerHolder> serverHolders = new HashMap<String, MinigameServerHolder>();

	private static List<BaseCommand> commands = new ArrayList<BaseCommand>();
	
	//ProtocolLib
	private static ProtocolManager protocolManager;

	@Override
	public void onEnable() {
		// Setup all ConfigurationSerialization Objects
		ConfigurationSerialization.registerClass(CompassItem.class);

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

		SignCreator.resetAllSigns();

		// Setup the LoadingScreenMessages singleton
		LoadingScreenMessages.getInstance();

		// TODO Use BungeeCord messaging for Player-save actions
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		new KWChannelMessageListener();

		// Jedis Listener Setup

		reloadJedisConfig();

		setupJedisPool();

		setupJedisListener();

		setupServerHolders();

		reloadSignConfig();

		registerCommands();

		saveConfig();
		
		//ProtocolLib
		if (Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
			protocolManager = ProtocolLibrary.getProtocolManager();
		}

	}

	@Override
	public void onDisable() {
		super.onDisable();

		// Jedis stuff
		lobbyChannelListener.getJedisPubSub().unsubscribe();
		jedisPool.destroy();

		PluginDescriptionFile pluginDescriptionFile = getDescription();
		Logger logger = Logger.getLogger("Minecraft");

		logger.info(pluginDescriptionFile.getName() + " was unloaded successfully! (Version: "
				+ pluginDescriptionFile.getVersion() + ")");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!label.equalsIgnoreCase("kwlobby")) {
			return false;
		}

		for (BaseCommand thisCommand : commands) {
			if (args[0].equalsIgnoreCase(thisCommand.getCommand())) {
				if (thisCommand.hasPermission(sender)) {
					thisCommand.execute(sender, new ArrayList<String>(Arrays.asList(args)));
					return true;
				}
			}
		}

		saveConfig();

		return false;
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

	public void setupJedisPool() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		if (jedisValues.getPassword() == null || jedisValues.getPassword().isEmpty()) {
			jedisPool = new JedisPool(poolConfig, jedisValues.getHost(), jedisValues.getPort(), 0);
		} else {
			jedisPool = new JedisPool(poolConfig, jedisValues.getHost(), jedisValues.getPort(), 0,
					jedisValues.getPassword());

		}
	}

	public void setupJedisListener() {
		List<String> channelList = new ArrayList<String>(Arrays.asList(jedisValues.getChannelsToListen()));
		channelList.add(jedisValues.getMinigameCreationChannel());
		String[] channels = Iterables.toArray(channelList, String.class);

		PluginLoader.lobbyChannelListener = new JedisMessageListener(jedisValues.getHost(), jedisValues.getPort(),
				jedisValues.getPassword(), channels) {
			@Override
			public synchronized void taskOnMessageReceive(String channel, String message) {
				System.out.println("taskOnMessageReceive is being called!");
				if (channel.equals(jedisValues.getMinigameCreationChannel())) {
					MinigameRequests.startRequestedServer(message);
				} else {
					if (PluginLoader.getServerHolders().containsKey(channel)) {
						PluginLoader.getServerHolders().get(channel).parseMessage(message);
					} else {
						MinigameServerHolder parser = new MinigameServerHolder(channel);
						parser.parseMessage(message);
						PluginLoader.getServerHolders().put(channel, parser);
					}
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

	private void registerCommands() {
		commands.add(new ReloadCommand(this));
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

	public static JedisMessageListener getLobbyChannelListener() {
		return lobbyChannelListener;
	}

	public static JedisPool getJedisPool() {
		return jedisPool;
	}

	public static ProtocolManager getProtocolManager() {
		return protocolManager;
	}

}
