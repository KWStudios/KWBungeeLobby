package org.kwstudios.play.kwbungeelobby.enums;

public enum BungeeMessageAction {

	PARTY("party"), FRIENDS("friends");

	private String text;

	BungeeMessageAction(String text) {
		this.text = text.toLowerCase();
	}

	public String getText() {
		return this.text;
	}

	public static BungeeMessageAction fromString(String text) {
		if (text != null) {
			for (BungeeMessageAction b : BungeeMessageAction.values()) {
				if (text.equalsIgnoreCase(b.text)) {
					return b;
				}
			}
		}
		return null;
	}

}
