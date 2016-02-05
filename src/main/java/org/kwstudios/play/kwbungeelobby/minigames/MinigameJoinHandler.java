package org.kwstudios.play.kwbungeelobby.minigames;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.kwstudios.play.kwbungeelobby.loader.PluginLoader;
import org.kwstudios.play.kwbungeelobby.signs.SignCreator;
import org.kwstudios.play.kwbungeelobby.toolbox.ConfigFactory;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

/**
 * Holds methods which try to send Players to MinigameServers
 * 
 * @author Ybrin, Zimonzk
 *
 */
public class MinigameJoinHandler {

	/**
	 * Attempts to send the given <code>player</code> to the given
	 * MinigameServer if there is at least one slot available.
	 * <p>
	 * If there are not enough free slots, the player will receive a message
	 * which tries to explain the situation.
	 * 
	 * @param player
	 *            The player who wants to join the MinigameServer.
	 * @param server
	 *            The MinigameServer which the given player wants to join.
	 */
	public static void doSingleJoin(Player player, MinigameServer server) {
		if (player == null) {
			throw new NullArgumentException("player");
		}
		if (server == null) {
			throw new NullArgumentException("requestedServer");
		}
		Sign sign = server.getMiniGameSign();
		if (sign == null) {
			throw new NullPointerException(
					"Neither the MinigameServer nor the sign within that instance shall be null.");
		}

		boolean isTeamGame = ConfigFactory.getValueOrSetDefault("settings.maps." + SignCreator.getMapFromSign(sign),
				"isTeamGame", false, PluginLoader.getInstance().getConfig());

		if (isTeamGame) {
			int currentSize = server.getMiniGameResponse().getCurrentPlayers();
			int maxSize = (ConfigFactory.getValueOrSetDefault("settings.maps." + SignCreator.getMapFromSign(sign),
					"teams", 1, PluginLoader.getInstance().getConfig())
					* ConfigFactory.getValueOrSetDefault("settings.maps." + SignCreator.getMapFromSign(sign),
							"players-per-team", 1, PluginLoader.getInstance().getConfig()));

			if (currentSize < maxSize) {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Connect");
				out.writeUTF(server.getMiniGameResponse().getServerName());
				player.sendPluginMessage(PluginLoader.getInstance(), "BungeeCord", out.toByteArray());
			} else {
				String message = ChatColor.RED + "This server is full! VIPs can still join it. " + ChatColor.GOLD
						+ "/shop";
				player.sendMessage(message);
			}
		} else {
			int currentSize = server.getMiniGameResponse().getCurrentPlayers();
			int maxSize = (ConfigFactory.getValueOrSetDefault("settings.maps." + SignCreator.getMapFromSign(sign),
					"max_players", 1, PluginLoader.getInstance().getConfig()));

			if (currentSize < maxSize) {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Connect");
				out.writeUTF(server.getMiniGameResponse().getServerName());
				player.sendPluginMessage(PluginLoader.getInstance(), "BungeeCord", out.toByteArray());
			} else {
				String message = ChatColor.RED + "This server is full! VIPs can still join it. " + ChatColor.GOLD
						+ "/shop";
				player.sendMessage(message);
			}
		}
	}

	/**
	 * Attempts to send the given <code>leader</code> to the given
	 * MinigameServer if there are enough slots available for all the
	 * <code>partyMembers</code>.
	 * <p>
	 * If there there are not enough slots for all the party members, the leader
	 * and only the leader will receive a message which tries to explain the
	 * situation.
	 * <p>
	 * This method will not send other players than the leader to the given
	 * MinigameServer. It is assumed that the party members will jump
	 * automatically to their leader on a Server change.
	 * 
	 * @param leader
	 *            The leader for the Party who wants to join the Server.
	 * @param partyMembers
	 *            The number of party members (inclusive the leader) who want to
	 *            join the Server.
	 * @param server
	 *            The MinigameServer where the party wants to join to.
	 */
	public static void doPartyJoin(Player leader, int partyMembers, MinigameServer server) {
		if (partyMembers <= 0) {
			throw new IllegalArgumentException("The party must have more than zero members.");
		}
		if (server == null) {
			throw new NullArgumentException("requestedServer");
		}
		Sign sign = server.getMiniGameSign();
		if (sign == null) {
			throw new NullPointerException(
					"Neither the MinigameServer nor the sign within that instance shall be null.");
		}

		boolean isTeamGame = ConfigFactory.getValueOrSetDefault("settings.maps." + SignCreator.getMapFromSign(sign),
				"isTeamGame", false, PluginLoader.getInstance().getConfig());

		if (isTeamGame) {
			int currentSize = server.getMiniGameResponse().getCurrentPlayers();
			int maxSize = (ConfigFactory.getValueOrSetDefault("settings.maps." + SignCreator.getMapFromSign(sign),
					"teams", 1, PluginLoader.getInstance().getConfig())
					* ConfigFactory.getValueOrSetDefault("settings.maps." + SignCreator.getMapFromSign(sign),
							"players-per-team", 1, PluginLoader.getInstance().getConfig()));

			if ((currentSize + partyMembers) <= maxSize) {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Connect");
				out.writeUTF(server.getMiniGameResponse().getServerName());
				leader.sendPluginMessage(PluginLoader.getInstance(), "BungeeCord", out.toByteArray());
			} else {
				String message = ChatColor.RED
						+ "There are not enough free slots for your party! VIPs can still join it. " + ChatColor.GOLD
						+ "/shop";
				leader.sendMessage(message);
			}
		} else {
			int currentSize = server.getMiniGameResponse().getCurrentPlayers();
			int maxSize = (ConfigFactory.getValueOrSetDefault("settings.maps." + SignCreator.getMapFromSign(sign),
					"max_players", 1, PluginLoader.getInstance().getConfig()));

			if ((currentSize + partyMembers) <= maxSize) {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Connect");
				out.writeUTF(server.getMiniGameResponse().getServerName());
				leader.sendPluginMessage(PluginLoader.getInstance(), "BungeeCord", out.toByteArray());
			} else {
				String message = ChatColor.RED
						+ "There are not enough free slots for your party! VIPs can still join it. " + ChatColor.GOLD
						+ "/shop";
				leader.sendMessage(message);
			}
		}
	}

}
