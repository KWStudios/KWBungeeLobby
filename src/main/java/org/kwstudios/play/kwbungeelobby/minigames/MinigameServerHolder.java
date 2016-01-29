package org.kwstudios.play.kwbungeelobby.minigames;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.kwstudios.play.kwbungeelobby.json.MiniGameResponse;
import org.kwstudios.play.kwbungeelobby.loader.PluginLoader;
import org.kwstudios.play.kwbungeelobby.signs.SignCreator;
import org.kwstudios.play.kwbungeelobby.signs.SignData;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;

public class MinigameServerHolder {

	private String channel;
	private HashMap<String, MinigameServer> connectedServers = new HashMap<String, MinigameServer>();

	public MinigameServerHolder(String channel) {
		this.channel = channel;
	}

	public synchronized void parseMessage(String message) {
		System.out.println("parseMessage is being called!");
		Gson gson = new Gson();
		MiniGameResponse miniGameResponse;
		try {
			miniGameResponse = gson.fromJson(message, MiniGameResponse.class);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		if (miniGameResponse.getServerName() == null || miniGameResponse.getAction() == null
				|| miniGameResponse.getGameType() == null) {
			return;
		}

		if (connectedServers.containsKey(miniGameResponse.getServerName())) {
			if (MinigameAction.fromString(miniGameResponse.getAction()) == MinigameAction.REMOVE) {
				// TODO Create method to reset the sign
				SignCreator.updateSign(connectedServers.get(miniGameResponse.getServerName()).getMiniGameSign(), 0);
				connectedServers.remove(miniGameResponse.getServerName());
			} else if (MinigameAction.fromString(miniGameResponse.getAction()) == MinigameAction.UPDATE) {
				SignCreator.updateSign(connectedServers.get(miniGameResponse.getServerName()).getMiniGameSign(),
						miniGameResponse.getCurrentPlayers());
			}
		} else {
			if (MinigameAction.fromString(miniGameResponse.getAction()) == MinigameAction.CREATE) {
				System.out.println("The Action is Create!");
				Sign miniGameSign = MinigameRequests.getQueuedSignForType(MinigameType.fromString(miniGameResponse
						.getGameType()));
				if (miniGameSign != null) {
					if(SignData.getRunningSignTimeouts().containsKey(miniGameSign)) {
						SignData.getRunningSignTimeouts().get(miniGameSign).cancel();
						SignData.getRunningSignTimeouts().remove(miniGameSign);
					}
					System.out.println("There was a waiting server!");
					MinigameServer server = new MinigameServer(miniGameResponse, miniGameSign,
							System.currentTimeMillis());
					ByteArrayDataOutput out = ByteStreams.newDataOutput();
					out.writeUTF("Connect");
					out.writeUTF(server.getMiniGameResponse().getServerName());
					for (Entry<Player, Sign> player : SignData.getWaitingPlayers().entrySet()) {
						if (player.getValue().equals(miniGameSign)) {
							player.getKey().sendPluginMessage(PluginLoader.getInstance(), "BungeeCord",
									out.toByteArray());
						}
					}
					MinigameRequests.removeQueuedRequest(miniGameSign);
					connectedServers.put(miniGameResponse.getServerName(), server);

				}
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

	public MinigameServer getActiveServerForSign(Sign sign) {
		for (Entry<String, MinigameServer> server : connectedServers.entrySet()) {
			if (server.getValue().getMiniGameSign().equals(sign)) {
				return server.getValue();
			}
		}
		return null;
	}

	public String getChannel() {
		return channel;
	}

}
