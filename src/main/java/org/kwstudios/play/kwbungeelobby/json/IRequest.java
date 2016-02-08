package org.kwstudios.play.kwbungeelobby.json;

import org.kwstudios.play.kwbungeelobby.enums.BungeeMessageAction;

public interface IRequest {

	public BungeeMessageAction getAction();

	public boolean isRequest();

}
