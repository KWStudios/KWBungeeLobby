package org.kwstudios.play.kwbungeelobby.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.kwstudios.play.kwbungeelobby.minigames.GetMaps;
import org.kwstudios.play.kwbungeelobby.minigames.MinigameRequests;
import org.kwstudios.play.kwbungeelobby.minigames.MinigameServer;
import org.kwstudios.play.kwbungeelobby.minigames.MinigameServerHolder;
import org.kwstudios.play.kwbungeelobby.minigames.MinigameType;
import org.kwstudios.play.kwbungeelobby.signs.SignCreator;
import org.kwstudios.play.kwbungeelobby.signs.SignData;
import org.kwstudios.play.kwbungeelobby.toolbox.ConfigFactory;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public final class EventListener implements Listener {

	private FileConfiguration fileConfiguration;

	public EventListener(PluginLoader plugin, FileConfiguration fileConfiguration) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.fileConfiguration = fileConfiguration;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onRightClick(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null && event.getClickedBlock().getState() != null) {
			if (event.getClickedBlock().getState() instanceof Sign) {
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Sign sign = (Sign) event.getClickedBlock().getState();
					if (SignCreator.isJoinSign(sign)) {
						Player player = event.getPlayer();
						if (player.hasPermission("kwbungee.sign.use")) {
							if (player.hasPermission("kwbungee.sign.use." + SignCreator.getSignRestrictionValue(sign))
									|| (SignCreator.getSignRestrictionValue(sign) == null)) {
								System.out.println("He has the permission!");
								if (SignData.getWaitingPlayers().containsKey(player)) {
									// TODO Fancy colors! ~~~~
									player.sendMessage("You are already waiting for a server!");
									return;
								}

								HashMap<String, MinigameServerHolder> severHolders = PluginLoader.getServerHolders();
								boolean hasRequest = false;
								boolean isConnected = false;
								MinigameServer requestedServer = null;
								for (Entry<String, MinigameServerHolder> serverHolder : severHolders.entrySet()) {
									MinigameServer server = serverHolder.getValue().getActiveServerForSign(sign);
									if (server != null) {
										isConnected = true;
										requestedServer = server;
										break;
									}
									hasRequest = MinigameRequests.isQueuedForRequest(sign);
								}
								// If there is no request, a new Request will be
								// made

								if (isConnected) {
									// Server is connected. Send player there if
									// the server isn't full.
									if (requestedServer.getMiniGameResponse().getCurrentPlayers() <= (ConfigFactory
											.getValueOrSetDefault("settings.maps." + SignCreator.getMapFromSign(sign),
													"teams", 1, PluginLoader.getInstance().getConfig()) * ConfigFactory
											.getValueOrSetDefault("settings.maps." + SignCreator.getMapFromSign(sign),
													"players-per-team", 1, PluginLoader.getInstance().getConfig()))) {
										ByteArrayDataOutput out = ByteStreams.newDataOutput();
										out.writeUTF("Connect");
										out.writeUTF(requestedServer.getMiniGameResponse().getServerName());
										player.sendPluginMessage(PluginLoader.getInstance(), "BungeeCord",
												out.toByteArray());
									} else {
										// TODO Fancy colors! ~~~~
										player.sendMessage("This server is full!");
									}
								} else if (!hasRequest) {
									// Server is not connected and has not been
									// requested yet.
									System.out.println("Everything set up!");
									MinigameRequests.createRequest(MinigameType.fromString(ConfigFactory
											.getValueOrSetDefault("settings.maps." + SignCreator.getMapFromSign(sign),
													"type", "bedwars", fileConfiguration)), sign);

									if (SignData.getSignPlayerCount().containsKey(sign)) {
										SignData.getSignPlayerCount().remove(sign);
										List<Player> toDelete = new ArrayList<Player>();
										for(Entry<Player, Sign> playerOfWaitingPLayers : SignData.getWaitingPlayers().entrySet()) {
											if(playerOfWaitingPLayers.getValue().equals(sign)) {
												toDelete.add(playerOfWaitingPLayers.getKey());
											}
										}
										for(Player del : toDelete) {
											SignData.getWaitingPlayers().remove(del);
										}
									}
									
									SignData.getSignPlayerCount().put(sign, 1);
									SignData.getWaitingPlayers().put(player, sign);
									// TODO Fancy colors! ~~~~
									player.sendMessage("~Starting the server. -- Please wait a few seconds.");
								} else {
									// A Request was already made
									if (SignData.getSignPlayerCount().containsKey(sign)) {
										int i = SignData.getSignPlayerCount().get(sign);
										if (i >= (ConfigFactory.getValueOrSetDefault(
												"settings.maps." + SignCreator.getMapFromSign(sign), "teams", 1,
												PluginLoader.getInstance().getConfig()) * ConfigFactory
												.getValueOrSetDefault(
														"settings.maps." + SignCreator.getMapFromSign(sign),
														"players-per-team", 1, PluginLoader.getInstance().getConfig()))) {
											// TODO Fancy colors! ~~~~
											player.sendMessage("This server is full!");
										} else {
											i++;
											SignData.getSignPlayerCount().remove(sign);
											SignData.getSignPlayerCount().put(sign, i);
											SignData.getWaitingPlayers().put(player, sign);
											// TODO Fancy colors! ~~~~
											player.sendMessage("Starting the server. -- Please wait a few seconds.");
										}
									}
								}
							} else {
								// TODO Fancy colors! ~~~~
								player.sendMessage("You don't have the required permissions to use this sign!");
							}

						} else {
							// TODO Fancy colors! ~~~~
							player.sendMessage("You don't have the required permissions to use this sign!");
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Sign sign = (Sign) event.getBlock().getState();

		if (event.getPlayer().hasPermission("kwbungee.signs.create")) {
			if (event.getLine(1).trim().equalsIgnoreCase("[kwbungee]")) {
				String[] allMaps = GetMaps.getMapNames(PluginLoader.getInstance().getConfig());
				for (String map : allMaps) {
					if (event.getLine(2).trim().equalsIgnoreCase(map.trim())) {
						SignCreator.createNewSign(sign, map);
						if (!event.getLine(3).trim().equalsIgnoreCase(""))
							SignCreator.setSignRestrictionValue(sign, event.getLine(3).trim());
						SignCreator.resetSign(event);
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getState() instanceof Sign && !event.isCancelled()) {
			SignCreator.removeSign((Sign) event.getBlock().getState());
		}
	}

	@EventHandler
	public void onDisconnet(PlayerQuitEvent event) {
		if (SignData.getWaitingPlayers().containsKey(event.getPlayer())) {
			int i = SignData.getSignPlayerCount().get(SignData.getWaitingPlayers().get(event.getPlayer()));
			i--;
			Sign sign = SignData.getWaitingPlayers().get(event.getPlayer());
			SignData.getSignPlayerCount().remove(SignData.getWaitingPlayers().get(event.getPlayer()));
			SignData.getSignPlayerCount().put(sign, i);
			SignData.getWaitingPlayers().remove(event.getPlayer());
		}
	}
}
