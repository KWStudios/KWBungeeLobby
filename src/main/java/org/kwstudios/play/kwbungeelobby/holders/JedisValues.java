package org.kwstudios.play.kwbungeelobby.holders;

public class JedisValues {

	private String host;
	private int port;
	private String password;
	private String[] channelsToListen;
	private String channelToSend;
	private String minigameCreationChannel;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String[] getChannelsToListen() {
		return channelsToListen;
	}

	public void setChannelsToListen(String[] channelsToListen) {
		this.channelsToListen = channelsToListen;
	}

	public String getChannelToSend() {
		return channelToSend;
	}

	public void setChannelToSend(String channelToSend) {
		this.channelToSend = channelToSend;
	}

	public String getMinigameCreationChannel() {
		return minigameCreationChannel;
	}

	public void setMinigameCreationChannel(String minigameCreationChannel) {
		this.minigameCreationChannel = minigameCreationChannel;
	}

}
