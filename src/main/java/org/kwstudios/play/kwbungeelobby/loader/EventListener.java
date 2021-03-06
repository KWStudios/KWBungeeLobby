package org.kwstudios.play.kwbungeelobby.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.kwstudios.play.kwbungeelobby.compass.CompassManager;
import org.kwstudios.play.kwbungeelobby.compass.NavigatorItem;
import org.kwstudios.play.kwbungeelobby.json.BungeeRequest;
import org.kwstudios.play.kwbungeelobby.json.PartyRequest;
import org.kwstudios.play.kwbungeelobby.listener.KWChannelMessageListener;
import org.kwstudios.play.kwbungeelobby.minigames.GetMaps;
import org.kwstudios.play.kwbungeelobby.minigames.MinigameRequests;
import org.kwstudios.play.kwbungeelobby.minigames.MinigameServer;
import org.kwstudios.play.kwbungeelobby.minigames.MinigameServerHolder;
import org.kwstudios.play.kwbungeelobby.minigames.MinigameType;
import org.kwstudios.play.kwbungeelobby.packets.WrapperPlayServerSpawnEntity;
import org.kwstudios.play.kwbungeelobby.packets.WrapperPlayServerSpawnEntity.ObjectTypes;
import org.kwstudios.play.kwbungeelobby.signs.SignCreator;
import org.kwstudios.play.kwbungeelobby.signs.SignData;
import org.kwstudios.play.kwbungeelobby.toolbox.ConfigFactory;
import org.kwstudios.play.kwbungeelobby.toolbox.ConstantHolder;
import org.kwstudios.play.kwbungeelobby.toolbox.FancyMessages;
import org.kwstudios.play.kwbungeelobby.toolbox.SlotManager;

import com.google.gson.Gson;

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
					final Sign sign = (Sign) event.getClickedBlock().getState();
					if (SignCreator.isJoinSign(sign)) {
						final Player player = event.getPlayer();
						if (player.hasPermission("kwbungee.sign.use")) {
							if (player.hasPermission("kwbungee.sign.use." + SignCreator.getSignRestrictionValue(sign))
									|| (SignCreator.getSignRestrictionValue(sign) == null)) {
								System.out.println("He has the permission!");
								if (SignData.getWaitingPlayers().containsKey(player)) {
									// TODO Fancy colors! ~~~~
									String message = ChatColor.RED + "You are already waiting for a server!";
									player.sendMessage(message);
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
									System.out.println("Server is connected, sending player to the server!");
									SignData.getQueuedPartyRequests().put(player, requestedServer);
									Gson gson = new Gson();
									PartyRequest request = new PartyRequest(player.getName(),
											player.getUniqueId().toString(), new String[] {}, new String[] {}, false,
											true);
									BungeeRequest bungeeRequest = new BungeeRequest(request, null, true);
									KWChannelMessageListener.sendMessage(gson.toJson(bungeeRequest), player);
								} else if (!hasRequest) {
									// Server is not connected and has not been
									// requested yet.
									System.out.println("Everything set up!");
									MinigameRequests.createRequest(MinigameType.fromString(ConfigFactory
											.getValueOrSetDefault("settings.maps." + SignCreator.getMapFromSign(sign),
													"type", "bedwars", PluginLoader.getInstance().getConfig())),
											sign);

									if (SignData.getSignPlayerCount().containsKey(sign)) {
										SignData.getSignPlayerCount().remove(sign);
										List<Player> toDelete = new ArrayList<Player>();
										for (Entry<Player, Sign> playerOfWaitingPLayers : SignData.getWaitingPlayers()
												.entrySet()) {
											if (playerOfWaitingPLayers.getValue().equals(sign)) {
												toDelete.add(playerOfWaitingPLayers.getKey());
											}
										}
										for (Player del : toDelete) {
											SignData.getWaitingPlayers().remove(del);
										}
									}

									SignData.getSignPlayerCount().put(sign, 1);
									SignData.getWaitingPlayers().put(player, sign);
									// TODO Fancy colors! ~~~~
									String message = ChatColor.DARK_AQUA
											+ "Starting the Server with some sort of Imagination...";
									FancyMessages.sendFancyMessage(player, message);
									FancyMessages.startRandomMessages(player);

									BukkitTask timeout = Bukkit.getServer().getScheduler()
											.runTaskLaterAsynchronously(PluginLoader.getInstance(), new Runnable() {

												@Override
												public void run() {
													MinigameRequests.removeQueuedRequest(sign);
													SignCreator.updateSign(sign, 0);
													if (SignData.getSignPlayerCount().containsKey(sign)) {
														SignData.getSignPlayerCount().remove(sign);
														List<Player> toDelete = new ArrayList<Player>();
														for (Entry<Player, Sign> playerOfWaitingPLayers : SignData
																.getWaitingPlayers().entrySet()) {
															if (playerOfWaitingPLayers.getValue().equals(sign)) {
																toDelete.add(playerOfWaitingPLayers.getKey());
															}
														}
														for (Player del : toDelete) {
															SignData.getWaitingPlayers().remove(del);
															// TODO Fancy
															// colors! ~~~~
															del.sendMessage(
																	"The Server you were waiting for timed out.");
															del.sendMessage("You can now join another game.");

															// Remove random
															// messages
															if (FancyMessages.getRandomMessages().containsKey(del)) {
																FancyMessages.getRandomMessages().get(del).cancel();
																FancyMessages.getRandomMessages().remove(del);
															}
														}
													}
												}

											}, 600);
									SignData.getRunningSignTimeouts().put(sign, timeout);
								} else {
									// A Request was already made
									if (SignData.getSignPlayerCount().containsKey(sign)) {
										int i = SignData.getSignPlayerCount().get(sign);
										if (i >= (ConfigFactory.getValueOrSetDefault(
												"settings.maps." + SignCreator.getMapFromSign(sign), "teams", 1,
												PluginLoader.getInstance().getConfig())
												* ConfigFactory.getValueOrSetDefault(
														"settings.maps." + SignCreator.getMapFromSign(sign),
														"players-per-team", 1,
														PluginLoader.getInstance().getConfig()))) {
											// TODO Fancy colors! ~~~~
											String message = ChatColor.RED
													+ "This server is full! VIPs can still join it. " + ChatColor.GOLD
													+ "/shop";
											player.sendMessage(message);
										} else {
											i++;
											SignData.getSignPlayerCount().remove(sign);
											SignData.getSignPlayerCount().put(sign, i);
											SignData.getWaitingPlayers().put(player, sign);
											// TODO Fancy colors! ~~~~
											String message = ChatColor.DARK_AQUA
													+ "Starting the Server with some sort of Imagination...";
											FancyMessages.sendFancyMessage(player, message);
											FancyMessages.startRandomMessages(player);
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
						PluginLoader.getInstance().saveConfig();
						SignCreator.resetSign(event);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSignBreak(BlockBreakEvent event) {
		if (!event.isCancelled()) {
			if (event.getBlock().getState() instanceof Sign && !event.isCancelled()) {
				SignCreator.removeSign((Sign) event.getBlock().getState());
			}
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

		if (SignData.getQueuedPartyRequests().containsKey(event.getPlayer())) {
			SignData.getQueuedPartyRequests().remove(event.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		for (int i = 1; i < 40; i++) {
			if (SlotManager.shouldClearSlot(i)) {
				player.getInventory().clear(i);
			}
		}
		player.setGameMode(GameMode.SURVIVAL);

		ItemStack item = player.getInventory().getItem(0);
		if (item != null) {
			if (item.getType() == Material.COMPASS) {
				if (item.getItemMeta().getDisplayName().equals(ConstantHolder.NAVIGATOR_NAME)) {
					return;
				}
			}
		}

		ItemStack compass = NavigatorItem.getCompassItem();
		player.getInventory().setItem(0, compass);
	}

	@EventHandler
	public void onCompassRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack item = event.getPlayer().getItemInHand();
			if (item != null) {
				if (item.getType() == Material.COMPASS) {
					if (item.getItemMeta().getDisplayName().equals(ConstantHolder.NAVIGATOR_NAME)) {
						CompassManager.openCompass(event.getPlayer());
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCompassItemClick(InventoryClickEvent event) {
		Inventory inventory = event.getClickedInventory();
		if (inventory != null) {
			if (inventory.equals(CompassManager.getCompass())) {
				Location loc = CompassManager.getLocation(event.getSlot());
				if (loc != null) {
					event.getWhoClicked().teleport(loc);
				}
				event.setCancelled(true);
			}
		}
	}

	// Remove possibility to interact with the Inventory

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryInteract(InventoryClickEvent event) {
		if (!event.getWhoClicked().hasPermission("kwstudios.lobby.interact")) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onItemDrop(PlayerDropItemEvent event) {
		if (!event.getPlayer().hasPermission("kwstudios.lobby.interact")) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (!event.getPlayer().hasPermission("kwstudios.lobby.interact")) {
			event.setCancelled(true);
		}
	}

	/*
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!event.getPlayer().hasPermission("kwstudios.lobby.interact")) {
			if (event.hasBlock()) {
				event.setCancelled(true);
			}
		}
	}
*/
	/*
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockHover(PlayerMoveEvent event) {
		if (!event.getPlayer().hasPermission("kwstudios.lobby.interact")) {
			// event.getPlayer().getTargetBlock((Set<Material>) null, 100);
			Location loc = event.getTo();

			//Entity entity = event.getPlayer().getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
			// entity.
			if (Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
				WrapperPlayServerSpawnEntity entity = new WrapperPlayServerSpawnEntity();
				entity.setType(78);
				entity.setX(loc.getX());
				entity.setY(loc.getY());
				entity.setZ(loc.getZ());
				entity.sendPacket(event.getPlayer());
			}
		}
	}
	*/

	/*
	 * @EventHandler(priority = EventPriority.NORMAL) public void
	 * onBlockDamage(BlockDamageEvent event) { if
	 * (!event.getPlayer().hasPermission("kwstudios.lobby.interact")) {
	 * event.setCancelled(true); } }
	 *
	 * @EventHandler(priority = EventPriority.HIGHEST) public void
	 * onBlockHover(PlayerMoveEvent event) { if
	 * (!event.getPlayer().hasPermission("kwstudios.lobby.interact")) {
	 * HashSet<Material> set = new HashSet<Material>(); set.add(Material.AIR);
	 * List<Block> blocks = event.getPlayer().getLineOfSight(set, 100); for (int
	 * i = 0; i < blocks.size(); i++) { blocks.remove(i); } } }
	 */
	// Remove join and quit messages

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoinMessage(PlayerJoinEvent event) {
		event.setJoinMessage("");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onQuitMessage(PlayerQuitEvent event) {
		event.setQuitMessage("");
	}
}
