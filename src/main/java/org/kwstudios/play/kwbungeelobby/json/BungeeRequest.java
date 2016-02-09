package org.kwstudios.play.kwbungeelobby.json;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kwstudios.play.kwbungeelobby.enums.BungeeMessageAction;

import com.google.common.collect.FluentIterable;
import com.google.gson.annotations.SerializedName;

public class BungeeRequest {

	@SerializedName("actions")
	private BungeeMessageAction[] actions;
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

		List<IRequest> params = new ArrayList<IRequest>();
		params.add(partyRequest);
		params.add(friendsRequest);

		Set<BungeeMessageAction> actionSet = new HashSet<BungeeMessageAction>();
		for (IRequest request : params) {
			if (request != null) {
				actionSet.add(request.getAction());
			}
		}
		this.actions = FluentIterable.from(actionSet).toArray(BungeeMessageAction.class);
	}

	public BungeeRequest(PartyRequest partyRequest, FriendsRequest friendsRequest) {
		this(partyRequest, friendsRequest, false);
	}

	public BungeeMessageAction[] getActions() {
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
