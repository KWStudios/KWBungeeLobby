package org.kwstudios.play.kwbungeelobby.minigames;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import com.google.gson.Gson;

public class LoadingScreenMessages {

	private static final class InstanceHolder {
		static final LoadingScreenMessages INSTANCE = new LoadingScreenMessages();
	}

	private Gson gson;
	private Random random;
	private String[] messages;

	private LoadingScreenMessages() {
		this.gson = new Gson();
		this.random = new Random();
		getMessagesFromFile();
	}

	private void getMessagesFromFile() {
		try {
			InputStream input = getClass().getResourceAsStream("/loading_messages.json");
			BufferedReader reader;
			reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
			messages = gson.fromJson(reader, String[].class);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public String getRandomMessage() {
		return messages[random.nextInt(messages.length)];
	}

	public static LoadingScreenMessages getInstance() {
		return InstanceHolder.INSTANCE;
	}

}
