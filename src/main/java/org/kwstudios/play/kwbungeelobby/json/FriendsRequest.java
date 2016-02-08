package org.kwstudios.play.kwbungeelobby.json;

import org.kwstudios.play.kwbungeelobby.enums.BungeeMessageAction;

public class FriendsRequest implements IRequest {

	private String player;
	private String friends[];
	private boolean isRequest;

	public FriendsRequest(String player, String[] friends, boolean isRequest) {
		super();
		this.player = player;
		this.friends = friends;
		this.isRequest = isRequest;
	}

	public FriendsRequest(String player, String[] friends) {
		this(player, friends, false);
	}

	public String getPlayer() {
		return player;
	}

	public String[] getFriends() {
		return friends;
	}

	@Override
	public boolean isRequest() {
		return isRequest;
	}

	@Override
	public BungeeMessageAction getAction() {
		return BungeeMessageAction.FRIENDS;
	}

}
