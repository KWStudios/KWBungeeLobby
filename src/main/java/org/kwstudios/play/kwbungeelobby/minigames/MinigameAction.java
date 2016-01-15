package org.kwstudios.play.kwbungeelobby.minigames;

public enum MinigameAction {

	CREATE("create"), UPDATE("update"), REMOVE("remove");

	private String text;

	MinigameAction(String text) {
		this.text = text.toLowerCase();
	}

	public String getText() {
		return this.text;
	}

	public static MinigameAction fromString(String text) {
		if (text != null) {
			for (MinigameAction b : MinigameAction.values()) {
				if (text.equalsIgnoreCase(b.text)) {
					return b;
				}
			}
		}
		return null;
	}

}
