package org.kwstudios.play.kwbungeelobby.json;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kwstudios.play.kwbungeelobby.enums.BungeeMessageAction;

import com.google.gson.annotations.SerializedName;

public class BungeeRequest {

	@SerializedName("actions")
	private Set<BungeeMessageAction> actions;
	@SerializedName("party_request")
	private PartyRequest partyRequest;
	@SerializedName("friends_request")
	private FriendsRequest friendsRequest;
	@SerializedName("is_request")
	private boolean isRequest;

	public BungeeRequest(PartyRequest partyRequest, FriendsRequest friendsRequest, boolean isRequest) {
		super();
		this.partyRequest = partyRequest;
		this.friendsRequest = friendsRequest;
		this.isRequest = isRequest;
		this.actions = new HashSet<BungeeMessageAction>();

		List<IRequest> params = new ArrayList<IRequest>();
		params.add(partyRequest);
		params.add(friendsRequest);

		for (IRequest request : params) {
			if (request != null) {
				actions.add(request.getAction());
			}
		}
	}

	public BungeeRequest(PartyRequest partyRequest, FriendsRequest friendsRequest) {
		this(partyRequest, friendsRequest, false);
	}

	public Set<BungeeMessageAction> getActions() {
		return actions;
	}

	public PartyRequest getPartyRequest() {
		return partyRequest;
	}

	public FriendsRequest getFriendsRequest() {
		return friendsRequest;
	}

	public boolean isRequest() {
		return isRequest;
	}

}
