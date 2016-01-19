package org.kwstudios.play.kwbungeelobby.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.kwstudios.play.kwbungeelobby.loader.PluginLoader;
import org.kwstudios.play.kwbungeelobby.toolbox.ConfigFactory;

public class MySQLServerHandler {

	private static Connection connection = null;

	/**
	 * Initializes the globally unique instance of Connection for the database
	 * which should be set in the config.yml.
	 * <p>
	 * If the values are not set yet, default values will be used instead.
	 * 
	 * @return
	 */
	public synchronized static Connection initConnection() {
		String databaseURL = ConfigFactory.getValueOrSetDefault("settings.database", "url", "localhost",
				PluginLoader.getInstance().getConfig());
		int port = ConfigFactory.getValueOrSetDefault("settings.database", "port", 3306,
				PluginLoader.getInstance().getConfig());
		String database = ConfigFactory.getValueOrSetDefault("settings.database", "db", "database",
				PluginLoader.getInstance().getConfig());
		String userName = ConfigFactory.getValueOrSetDefault("settings.database", "user", "username",
				PluginLoader.getInstance().getConfig());
		String password = ConfigFactory.getValueOrSetDefault("settings.database", "password", "password",
				PluginLoader.getInstance().getConfig());

		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", userName);
		connectionProps.put("password", password);

		try {
			conn = DriverManager.getConnection(
					"jdbc:mysql://" + databaseURL + ":" + Integer.toString(port) + "/" + database, connectionProps);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		connection = conn;

		// System.out.println("RageMode connected successfully to the
		// database!");

		return connection;
	}

	/**
	 * Returns a pseudorandomly chosen MinecraftServerModel value which is
	 * currently not in use. More formally, returns a Java object representation
	 * of one random minecraft_server model for the globally set database, where
	 * the column <i>is_used</i> == false.
	 * <p>
	 * Before returning the instance of MinecraftServerModel, this method
	 * reserves the chosen Server with {@link #reserveServer(String)}, which
	 * means if there is no need for the Server anymore, it should be released
	 * by calling {@link #releaseServer(String)}}.
	 * 
	 * @return A new instance of MinecraftServerModel, or null iff there is no
	 *         row where <i>is_used</i> == false,.
	 */
	public static MinecraftServerModel getAvailableServer() {
		Statement statement = null;
		String query = "SELECT * FROM `minecraft_servers` WHERE `is_used` = '0';";

		List<MinecraftServerModel> availableServers = new ArrayList<MinecraftServerModel>();

		try {
			statement = MySQLServerHandler.getConnection().createStatement();
			ResultSet rs = statement.executeQuery(query);
			while (rs.next()) {
				String name = rs.getString("name");
				String server = rs.getString("server");
				int number = rs.getInt("number");
				boolean isUsed = rs.getBoolean("is_used");
				availableServers.add(new MinecraftServerModel(name, server, number, isUsed));
			}
		} catch (SQLException e) {
			// TODO No Servers available, contact all the players and empty the
			// HashMap
			return null;
		}
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (!availableServers.isEmpty()) {
			Random random = new Random();
			MinecraftServerModel randomKey = availableServers.get(random.nextInt(availableServers.size()));
			reserveServer(randomKey.getName());

			return randomKey;
		}
		return null;
	}

	/**
	 * Reserves the given Server in the minecraft_servers table by setting the
	 * corresponding <i>is_used</i> column to true.
	 * <p>
	 * If <i>is_used</i> is already true, this does nothing.
	 * 
	 * @param name
	 *            The name for the MinecraftServer which should be reserved
	 *            which is the Unique identification key for the MySQL table
	 *            minecraft_servers.
	 */
	public static void reserveServer(String name) {
		Statement statement = null;
		String query = "UPDATE `minecraft_servers` SET `is_used` = '1' WHERE `name` = '" + name + "';";
		try {
			statement = MySQLServerHandler.getConnection().createStatement();
			statement.executeUpdate(query);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Releases the given Server in the minecraft_servers table by setting the
	 * corresponding <i>is_used</i> column to false.
	 * <p>
	 * If <i>is_used</i> is already false, this does nothing.
	 * 
	 * @param name
	 *            The name for the MinecraftServer which should be reserved
	 *            which is the Unique identification key for the MySQL table
	 *            minecraft_servers.
	 */
	public static void releaseServer(String name) {
		Statement statement = null;
		String query = "UPDATE `minecraft_servers` SET `is_used` = '0' WHERE `name` = '" + name + "';";
		try {
			statement = MySQLServerHandler.getConnection().createStatement();
			statement.executeUpdate(query);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the globally unique instance of Connection.
	 * <p>
	 * If the instance is null or the connection is not valid anymore,
	 * {@link #initConnection()} is being called before returning.
	 * 
	 * @return The globally unique instance of Connection
	 */
	public static Connection getConnection() {
		if (connection != null) {
			try {
				if (connection.isValid(2)) {
					return connection;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return initConnection();
	}

}
