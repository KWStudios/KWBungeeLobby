package org.kwstudios.play.kwbungeelobby.toolbox;

import java.util.Set;

import org.kwstudios.play.kwbungeelobby.loader.PluginLoader;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;

public class SlotManager {

	public static boolean shouldClearSlot(int slot) {
		int lower = ConfigFactory.getValueOrSetDefault("settings.inventory.slots", "lower", 1,
				PluginLoader.getInstance().getConfig());
		int upper = ConfigFactory.getValueOrSetDefault("settings.inventory.slots", "upper", 39,
				PluginLoader.getInstance().getConfig());

		Set<Integer> exclude = ImmutableSet
				.copyOf(PluginLoader.getInstance().getConfig().getIntegerList("settings.inventory.slots.exclude"));
		Set<Integer> include = ImmutableSet
				.copyOf(PluginLoader.getInstance().getConfig().getIntegerList("settings.inventory.slots.include"));

		Range<Integer> range = Range.closed(lower, upper);

		if ((range.contains(slot) && !exclude.contains(slot)) || include.contains(slot)) {
			return true;
		}
		return false;
	}

}
