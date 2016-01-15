package org.kwstudios.play.kwbungeelobby.listener;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.kwstudios.play.kwbungeelobby.loader.PluginLoader;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class BungeeMessageListener implements PluginMessageListener {

	public BungeeMessageListener() {
		PluginLoader.getInstance().getServer().getMessenger().registerIncomingPluginChannel(PluginLoader.getInstance(),
				"BungeeCord", this);
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (channel.equals("BungeeCord")) {
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
			String subchannel = in.readUTF();
			if (subchannel.equals("signupdate")) {

			}
		}
	}

}
