package org.kwstudios.play.kwbungeelobby.minigames;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.block.Sign;
import org.kwstudios.play.kwbungeelobby.json.MiniGameResponse;

import com.google.gson.Gson;

public class MinigameServerHolder {

	private String channel;
	private HashMap<String, MinigameServer> connectedServers = new HashMap<String, MinigameServer>();

	public MinigameServerHolder(String channel) {
		this.channel = channel;
	}

	public synchronized void parseMessage(String message) {
		Gson gson = new Gson();
		MiniGameResponse miniGameResponse = gson.fromJson(message, MiniGameResponse.class);

		if (connectedServers.containsKey(miniGameResponse.getServerName())) {
			// TODO Handle the Sign update and other stuff or remove this server
			// if it wants to
		} else {
			// TODO Create new MiniGameServer if there is a need for one (Check
			// the HashMap with signs which requested a server with this
			// minigame type)
			Sign miniGameSign = MinigameRequests
					.getQueuedSignForType(MinigameType.fromString(miniGameResponse.getGameType()));
			if (miniGameSign != null) {
				MinigameServer server = new MinigameServer(miniGameResponse, miniGameSign, System.currentTimeMillis());
				MinigameRequests.removeQueuedRequest(miniGameSign);
				connectedServers.put(miniGameResponse.getServerName(), server);
			}
		}
	}

	public boolean hasSignActiveServer(Sign sign) {
		for (Entry<String, MinigameServer> server : connectedServers.entrySet()) {
			if (server.getValue().getMiniGameSign().equals(sign)) {
				return true;
			}
		}
		return false;
	}

	public String getChannel() {
		return channel;
	}

}
