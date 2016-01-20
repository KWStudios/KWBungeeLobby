package org.kwstudios.play.kwbungeelobby.database;

public class MinecraftServerModel {

	private String name;
	private String server;
	private String type;
	private String map;

	private int number;

	private boolean isUsed;

	public MinecraftServerModel(String name, String server, int number, boolean isUsed) {
		super();
		this.name = name;
		this.server = server;
		this.number = number;
		this.isUsed = isUsed;
	}

	public String getName() {
		return name;
	}

	public String getServer() {
		return server;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMap() {
		return map;
	}

	public void setMap(String map) {
		this.map = map;
	}

	public int getNumber() {
		return number;
	}

	public boolean isUsed() {
		return isUsed;
	}

}
