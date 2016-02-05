package org.kwstudios.play.kwbungeelobby.signs;

import java.util.HashMap;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.kwstudios.play.kwbungeelobby.minigames.MinigameServer;

public class SignData {
	private static HashMap<Player, Sign> waitingPlayers = new HashMap<Player, Sign>();
	private static HashMap<Sign, Integer> signPlayerCount = new HashMap<Sign, Integer>();
	private static HashMap<Sign, BukkitTask> runningSignTimeouts = new HashMap<Sign, BukkitTask>();

	private static HashMap<Player, MinigameServer> queuedPartyRequests = new HashMap<Player, MinigameServer>();

	public static HashMap<Player, Sign> getWaitingPlayers() {
		return waitingPlayers;
	}

	public static HashMap<Sign, Integer> getSignPlayerCount() {
		return signPlayerCount;
	}

	public static HashMap<Sign, BukkitTask> getRunningSignTimeouts() {
		return runningSignTimeouts;
	}

	public static HashMap<Player, MinigameServer> getQueuedPartyRequests() {
		return queuedPartyRequests;
	}

}
