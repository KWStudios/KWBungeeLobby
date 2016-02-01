package org.kwstudios.play.kwbungeelobby.compass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.NumberConversions;

public class CompassItem implements ConfigurationSerializable {

	private Material material;
	private int position;
	private String name;
	private List<String> lore;
	private Location location;

	private ItemStack itemStack;

	public CompassItem(Material material, int position, String name, List<String> lore, Location location) {
		this.material = material;
		this.position = position;
		this.name = name;
		this.lore = lore;
		this.location = location;
		createItemStack();
	}

	private void createItemStack() {
		itemStack = new ItemStack(material);
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		itemStack.setItemMeta(meta);
	}

	public Material getMaterial() {
		return material;
	}

	public int getPosition() {
		return position;
	}

	public String getName() {
		return name;
	}

	public List<String> getLore() {
		return lore;
	}

	public Location getLocation() {
		return location;
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("material", this.material.toString());

		data.put("position", this.position);
		data.put("name", this.name);
		data.put("lore", this.lore);
		data.put("location", this.location);

		return data;
	}

	public static CompassItem deserialize(Map<String, Object> args) {
		Material m = Material.matchMaterial((String) args.get("material"));
		int p = NumberConversions.toInt(args.get("position"));
		String n = (String) args.get("name");
		@SuppressWarnings("unchecked")
		List<String> l = (List<String>) (List<?>) args.get("lore");
		Location loc = (Location) args.get("location");

		if (m == null || n == null || l == null || loc == null) {
			throw new IllegalArgumentException("One or more values are not set properly");
		}

		return new CompassItem(m, p, n, l, loc);
	}

}
