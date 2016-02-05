package org.kwstudios.play.kwbungeelobby.listener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.kwstudios.play.kwbungeelobby.json.PartyRequest;
import org.kwstudios.play.kwbungeelobby.loader.PluginLoader;
import org.kwstudios.play.kwbungeelobby.signs.SignData;
import org.kwstudios.play.kwbungeelobby.toolbox.ConstantHolder;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class BungeeMessageListener implements PluginMessageListener {

	public BungeeMessageListener() {
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
		PartyRequest response = null;
		try {
			response = gson.fromJson(message, PartyRequest.class);
		} catch (JsonSyntaxException e) {
			throw new JsonSyntaxException("The message received was corrupt. It should be JSON Syntax");
		}
		if (response == null) {
			return;
		}

		Player player = Bukkit.getPlayer(UUID.fromString(response.getUuid()));
		if (SignData.getQueuedPartyRequests().containsKey(player)) {
			// TODO Finish joining for the Player and his Party (Or message him
			// that the Server has not enough free slots)
		}
	}

}
