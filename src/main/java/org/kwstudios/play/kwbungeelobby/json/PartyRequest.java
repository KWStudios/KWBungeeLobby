package org.kwstudios.play.kwbungeelobby.json;

public class PartyRequest {

	private String player;
	private String uuid;
	private String players_in_party[];
	private String uuids_in_party[];
	private boolean isLeader;

	public PartyRequest(String player, String uuid, String[] players_in_party, String[] uuids_in_party,
			boolean isLeader) {
		super();
		this.player = player;
		this.uuid = uuid;
		this.players_in_party = players_in_party;
		this.uuids_in_party = uuids_in_party;
		this.isLeader = isLeader;
	}

	public String getPlayer() {
		return player;
	}

	public String getUuid() {
		return uuid;
	}

	public String[] getPlayers_in_party() {
		return players_in_party;
	}

	public String[] getUuids_in_party() {
		return uuids_in_party;
	}

	public boolean isLeader() {
		return isLeader;
	}

}
