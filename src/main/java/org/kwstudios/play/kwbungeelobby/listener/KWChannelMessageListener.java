package org.kwstudios.play.kwbungeelobby.listener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.kwstudios.play.kwbungeelobby.enums.BungeeMessageAction;
import org.kwstudios.play.kwbungeelobby.json.BungeeRequest;
import org.kwstudios.play.kwbungeelobby.json.PartyRequest;
import org.kwstudios.play.kwbungeelobby.loader.PluginLoader;
import org.kwstudios.play.kwbungeelobby.minigames.MinigameJoinHandler;
import org.kwstudios.play.kwbungeelobby.minigames.MinigameServer;
import org.kwstudios.play.kwbungeelobby.signs.SignData;
import org.kwstudios.play.kwbungeelobby.toolbox.ConstantHolder;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class KWChannelMessageListener implements PluginMessageListener {

	public KWChannelMessageListener() {
		PluginLoader.getInstance().getServer().getMessenger().registerOutgoingPluginChannel(PluginLoader.getInstance(),
				ConstantHolder.KW_CHANNEL_NAME);
		PluginLoader.getInstance().getServer().getMessenger().registerIncomingPluginChannel(PluginLoader.getInstance(),
				ConstantHolder.KW_CHANNEL_NAME, this);
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals(ConstantHolder.KW_CHANNEL_NAME)) {
			return;
		}
		ByteArrayInputStream stream = new ByteArrayInputStream(message);
		DataInputStream input = new DataInputStream(stream);

		try {
			parseMessage(input.readUTF());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseMessage(String message) {
		Gson gson = new Gson();
		BungeeRequest response = null;
		try {
			response = gson.fromJson(message, BungeeRequest.class);
		} catch (JsonSyntaxException e) {
			throw new JsonSyntaxException("The message received was corrupt. It should be JSON Syntax");
		}
		if (response == null) {
			return;
		}
		if (response.isRequest()) {
			return;
		}

		for (BungeeMessageAction action : response.getActions()) {
			if (action == BungeeMessageAction.PARTY) {
				PartyRequest partyResponse = response.getPartyRequest();
				Player player = Bukkit.getPlayer(UUID.fromString(partyResponse.getUuid()));
				if (SignData.getQueuedPartyRequests().containsKey(player)) {
					MinigameServer minigameServer = SignData.getQueuedPartyRequests().get(player);
					if (partyResponse.isLeader()) {
						MinigameJoinHandler.doPartyJoin(Bukkit.getPlayer(UUID.fromString(partyResponse.getUuid())),
								partyResponse.getPlayers_in_party().length, minigameServer);
					} else {
						MinigameJoinHandler.doSingleJoin(player, minigameServer);
					}
				}
			}
		}
	}

	public static void sendMessage(String message, Player player) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);
		try {
			out.writeUTF(message);
		} catch (IOException e) {
			e.printStackTrace();
		}

		player.sendPluginMessage(PluginLoader.getInstance(), ConstantHolder.KW_CHANNEL_NAME, stream.toByteArray());
	}

}
