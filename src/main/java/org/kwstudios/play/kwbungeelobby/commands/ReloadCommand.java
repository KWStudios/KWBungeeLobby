package org.kwstudios.play.kwbungeelobby.commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.kwstudios.play.kwbungeelobby.database.MySQLServerHandler;
import org.kwstudios.play.kwbungeelobby.loader.PluginLoader;
import org.kwstudios.play.kwbungeelobby.signs.SignCreator;

public class ReloadCommand extends BaseCommand {

	public ReloadCommand(PluginLoader plugin) {
		super(plugin);
	}

	@Override
	public String getPermission() {
		return "kwstudios.lobby.reload";
	}

	@Override
	public String getCommand() {
		return "reload";
	}

	@Override
	public String getName() {
		return "Reload command";
	}

	@Override
	public String getDescription() {
		return "Reloads the plugin.";
	}

	@Override
	public String[] getArguments() {
		return new String[] {};
	}

	@Override
	public boolean execute(CommandSender sender, ArrayList<String> args) {
		if (this.hasPermission(sender)) {
			getPlugin().reloadConfig();
			SignCreator.resetAllSigns();
			getPlugin().reloadJedisConfig();
			getPlugin().setupJedisPool();
			getPlugin().setupRedisClient();
			getPlugin().setupJedisListener();
			Bukkit.getServer().getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {
				@Override
				public void run() {
					MySQLServerHandler.initConnection();
				}
			});
			return true;
		}
		return false;
	}

}
