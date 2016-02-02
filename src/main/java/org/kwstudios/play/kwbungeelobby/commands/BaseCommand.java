package org.kwstudios.play.kwbungeelobby.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kwstudios.play.kwbungeelobby.loader.PluginLoader;

public abstract class BaseCommand implements ICommand {

	private PluginLoader plugin = null;

	public BaseCommand(PluginLoader plugin) {
		this.plugin = plugin;
	}

	protected PluginLoader getPlugin() {
		return this.plugin;
	}

	@Override
	public abstract String getCommand();

	@Override
	public abstract String getName();

	@Override
	public abstract String getDescription();

	@Override
	public abstract String[] getArguments();

	@Override
	public abstract boolean execute(CommandSender sender, ArrayList<String> args);

	@Override
	public boolean hasPermission(CommandSender sender) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a Player!");
			return false;
		}

		if (!sender.hasPermission(this.getPermission())) {
			sender.sendMessage(ChatColor.RED
					+ "There are some sort of commands which are not intended to be used by certain players...");
			return false;
		}

		return true;
	}

}
