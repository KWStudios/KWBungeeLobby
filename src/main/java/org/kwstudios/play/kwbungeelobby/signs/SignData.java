package org.kwstudios.play.kwbungeelobby.signs;

import java.util.HashMap;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class SignData {
	private static HashMap<Player, Sign> waitingPlayers = new HashMap<Player, Sign>();
	private static HashMap<Sign, Integer> signPlayerCount = new HashMap<Sign, Integer>();

	public static HashMap<Player, Sign> getWaitingPlayers() {
		return waitingPlayers;
	}

	public static HashMap<Sign, Integer> getSignPlayerCount() {
		return signPlayerCount;
	}

}
