package library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a thread-safe singleton implementation of a connection to an
 * SQL database, in order to read and write to an SQLite database. It contains
 * all of the database manipulating methods for the Server to interact with the
 * database.
 * 
 * @author Nicholas Eddy, Mike Novak, Kyohei Mizokami, Chris Panzero
 * 
 */
public class DatabaseConnection {

	private Connection connection;
	private static DatabaseConnection databaseConnection;

	/*
	 * This is a singleton implementation, so the constructor is private.
	 */
	private DatabaseConnection() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:"
					+ System.getProperty("user.dir") + "/src/UAMUD.db");
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets an instance of the singleton implementation of this SQL Database
	 * Connection.
	 * 
	 * @return An instance of the singleton implementation of this SQL Database
	 *         Connection.
	 */
	public static DatabaseConnection getInstance() {
		/*
		 * Singleton implementation
		 */
		if (databaseConnection == null) {
			databaseConnection = new DatabaseConnection();
			return databaseConnection;
		} else {
			return databaseConnection;
		}
	}

	/**
	 * Executes the given SQL query on this database.
	 * 
	 * @param sql
	 *            SQL query to run on the database.
	 * @throws SQLException
	 */
	public synchronized void execute(String sql) throws SQLException {
		Statement s = connection.createStatement();
		s.execute(sql);
		s.close();
		// connection.commit();
	}

	/**
	 * Executes the given SQL query and returns the ResultSet that the SQL
	 * database returns. This method is synchronized so it is thread safe.
	 * 
	 * @param sql
	 *            SQL query to run on the database.
	 * @return The resultant set from the SQL query that was ran on this
	 *         database.
	 */
	public synchronized ResultSet executeWithResult(String sql) {
		try {
			Statement s = connection.createStatement();
			ResultSet result = s.executeQuery(sql);
			// connection.commit();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Attempts to commit changes to the database.
	 */
	public void commit() {
		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Attempts to close the database.
	 * 
	 * @return True if successful, false if unsuccessful.
	 */
	public boolean close() {
		try {
			connection.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Creates an SQL INSERT INTO query using the given Map. Maps are key-value
	 * paired, so the key is the column name and the value is the data for that
	 * column.
	 * 
	 * @param parameters
	 *            The data to be inserted in to the database.
	 * @param table
	 *            The table for which the data will be inserted into.
	 * @return The SQL INSERT INTO query.
	 */
	public static String createInsertQuery(Map<String, String> parameters,
			String table) {
		String firstHalf = "INSERT INTO " + table + " (";
		String lastHalf = ") VALUES ('";
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			firstHalf += entry.getKey() + ", ";
			lastHalf += entry.getValue() + "', '";
		}

		String query = firstHalf.substring(0, firstHalf.length() - 2)
				+ lastHalf.substring(0, lastHalf.length() - 3) + ");";
		try {
			DatabaseConnection.getInstance().execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return query;

	}

	/**
	 * * Creates an SQL UPDATE query using the given Map. Maps are key-value
	 * paired, so the key is the column name and the value is the data for that
	 * column.
	 * 
	 * @param parameters
	 *            The data to be updated in the database.
	 * @param table
	 *            The table for which the data will be updated in.
	 * @param id
	 *            The ID of the row to be updated.
	 * @return The SQL UPDATE query.
	 */
	public static String createUpdateQuery(Map<String, String> parameters,
			String table, int id) {
		String sql = "UPDATE " + table + " SET ";
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			sql += entry.getKey() + " = '" + entry.getValue() + "', ";
		}
		return sql.substring(0, sql.length() - 2) + " WHERE id = '" + id + "';";
	}

	/**
	 * Gets the ID for the given user name.
	 * 
	 * @param username
	 *            The user name to search for.
	 * @return The ID of the user name.
	 * @throws NoSuchItemException
	 */
	public static int getIDFromUsername(String username) {
		int id = -1;

		DatabaseConnection dbc = DatabaseConnection.getInstance();
		ResultSet result = dbc
				.executeWithResult("SELECT id FROM users WHERE username = '"
						+ username + "';");
		try {
			if (!result.isClosed())
				id = result.getInt("id");
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return id;
	}

	/**
	 * Returns a List of characters for a user.
	 * 
	 * @param username
	 *            The user whose characters we want to retrieve.
	 * @return List of characters for a user.
	 * @throws NoSuchItemException
	 */
	public static List<String> getCharactersForUser(String username) {
		DatabaseConnection dbc = DatabaseConnection.getInstance();
		List<String> characterList = new ArrayList<String>();
		ResultSet result;
		try {
			int id = DatabaseConnection.getIDFromUsername(username);
			result = dbc
					.executeWithResult("SELECT name FROM characters JOIN usercharacters "
							+ "ON characters.id = usercharacters.characterid WHERE usercharacters.userid = '"
							+ id + "';");
			while (result.next()) {
				characterList.add(result.getString(1));
			}
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return characterList;
	}

	/**
	 * Gets the ID for the given character.
	 * 
	 * @param character
	 *            The character to search for.
	 * @return The ID of the character.
	 * @throws NoSuchItemException
	 */
	public static int getIDFromCharacter(String character) {
		int id = -1;
		String cleaned = character.replace("'", "''");
		DatabaseConnection dbc = DatabaseConnection.getInstance();
		ResultSet result = dbc
				.executeWithResult("SELECT id FROM characters WHERE name = '"
						+ cleaned + "';");
		try {
			id = result.getInt("id");
			if (!result.isClosed())
				result.close();
		} catch (SQLException e) {

		}
		return id;
	}

	/**
	 * Creates a new user with parameters user name, password, and name.
	 * 
	 * @param username
	 *            The user name of the user.
	 * @param password
	 *            The password of the user.
	 * @param name
	 *            The name of the user.
	 * @throws SQLException
	 */
	public static void createUser(String username, String password, String name) {
		DatabaseConnection dbc = DatabaseConnection.getInstance();
		username = username.replace("'", "");
		username = username.replace(";", "");
		try {
			dbc.execute("INSERT INTO users (username, password, name, createdate, admin) VALUES ('"
					+ username
					+ "', '"
					+ password
					+ "', '"
					+ name
					+ "', datetime('now'), 'false');");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new character with parameters name and class id.
	 * 
	 * @param name
	 *            The name of the character.
	 * @param classid
	 *            The class id of the character.
	 * @param user
	 *            The name of the user creating this character.
	 * @throws NoSuchItemException
	 * @throws SQLException
	 */
	public static void createCharacter(String name, String classid, String user) {

		DatabaseConnection dbc = DatabaseConnection.getInstance();
		name = name.replace("'", "");
		name = name.replace(";", "");
		name = name.replace(" ", "");

		ClassType classType = ClassType.valueOf(classid);

		int hp = classType.getMaxHP(1);
		int ap = classType.getMaxAP(1);
		int maxhp = classType.getMaxHP(1);
		int maxap = classType.getMaxAP(1);

		try {
			dbc.execute("INSERT INTO characters (name, level, hp, ap, location, classid, isonline, experience, lights, maxhp, maxap) VALUES ('"
					+ name
					+ "', '1', '"
					+ hp
					+ "', '"
					+ ap
					+ "', '1', '"
					+ classid
					+ "', 'true', '0','false', '"
					+ maxhp
					+ "', '"
					+ maxap + "');");
			dbc.execute("INSERT INTO usercharacters (userid, characterid) VALUES ('"
					+ DatabaseConnection.getIDFromUsername(user)
					+ "', '"
					+ DatabaseConnection.getIDFromCharacter(name) + "');");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Retrieves a Map<"Item name", "Quantity"> of items in a given room.
	 * 
	 * @param roomID
	 *            The room whose items we are querying.
	 * @param names
	 *            Whether or not names are displayed instead of item IDs.
	 * @return Map<"Item name", "Quantity"> of items in a given room.
	 */
	public static Map<String, String> getItemNamesForRoom(int roomID) {
		DatabaseConnection dbc = DatabaseConnection.getInstance();
		Map<String, String> itemsInRoom = new HashMap<String, String>();
		ResultSet result;
		try {
			result = dbc
					.executeWithResult("SELECT itemid, quantity FROM roomitems WHERE roomid = '"
							+ roomID + "';");
			while (result.next()) {
				Item item = new Item(Integer.parseInt(result
						.getString("itemid")));
				itemsInRoom.put(item.get("name"), result.getString("quantity"));
			}
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return itemsInRoom;
	}

	public static Map<Integer, Integer> getItemIDsForRoom(int roomID) {
		DatabaseConnection dbc = DatabaseConnection.getInstance();
		Map<Integer, Integer> itemsInRoom = new HashMap<Integer, Integer>();
		ResultSet result;
		try {
			result = dbc
					.executeWithResult("SELECT itemid, quantity FROM roomitems WHERE roomid = '"
							+ roomID + "';");
			while (result.next()) {
				Item item = new Item(Integer.parseInt(result
						.getString("itemid")));

				itemsInRoom.put(item.getID(),
						Integer.parseInt(result.getString("quantity")));

			}
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return itemsInRoom;
	}

	/**
	 * Retrieves a Map<"Item name", "Quantity"> of items in a given room.
	 * 
	 * @param roomID
	 *            The room whose items we are querying.
	 * @return Map<Item name, Quantity> of items in a given room.
	 */
	public static Map<Integer, Integer> getItemsForRoom(int roomID) {
		DatabaseConnection dbc = DatabaseConnection.getInstance();
		Map<Integer, Integer> itemsInRoom = new HashMap<Integer, Integer>();

		try {
			ResultSet rs = dbc
					.executeWithResult("SELECT itemid, quantity FROM roomitems WHERE roomid = '"
							+ roomID + "';");
			while (rs.next()) {
				itemsInRoom.put(rs.getInt(1), rs.getInt(2));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return itemsInRoom;
	}

	/**
	 * Retrieves the inventory for a given character.
	 * 
	 * @param characterID
	 *            The ID of the character whose inventory we are querying.
	 * @param names
	 *            Whether or not to display a Map<"Item ID", "Quantity" or
	 *            Map<"Item name", "Quantity">
	 * @return Map<"Item ID", "Quantity"> or Map<"Item name", "Quantity"> if
	 *         names==true.
	 */
	public static Map<String, String> getInventoryForCharacter(int characterID,
			boolean names) {
		Map<String, String> inventory = new HashMap<String, String>();
		DatabaseConnection dbc = DatabaseConnection.getInstance();
		ResultSet rs = dbc
				.executeWithResult("SELECT item, quantity FROM inventory JOIN characters ON characters.id = inventory.character WHERE inventory.character = '"
						+ characterID + "';");
		try {
			while (rs.next()) {

				Item item = new Item(Integer.parseInt(rs.getString(1)));
				if (names) {
					inventory.put(item.get("name"), rs.getString(2));
				} else {
					inventory.put(item.getID() + "", rs.getString(2));
				}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return inventory;
	}

	/**
	 * Retrieves the inventory for a given character.
	 * 
	 * @param characterID
	 *            The ID of the character whose inventory we are querying.
	 * @param names
	 *            Whether or not to display a Map<"Item ID", "Quantity" or
	 *            Map<"Item name", "Quantity">
	 * @return Map<ItemID, Quantity>
	 */
	public static Map<Integer, Integer> getInventoryForCharacter(int characterID) {
		Map<Integer, Integer> inventory = new HashMap<Integer, Integer>();
		DatabaseConnection dbc = DatabaseConnection.getInstance();
		ResultSet rs = dbc
				.executeWithResult("SELECT item, quantity FROM inventory JOIN characters ON characters.id = inventory.character WHERE inventory.character = '"
						+ characterID + "';");
		try {
			while (rs.next()) {
				inventory.put(rs.getInt(1), rs.getInt(2));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return inventory;
	}

	/**
	 * Adds the given itemID to the given characterID.
	 * 
	 * @param characterID
	 *            The ID of the character to add the item to.
	 * @param itemID
	 *            The ID of the item to add to the character.
	 */
	public static void addItemToCharacter(int characterID, int itemID) {

		DatabaseConnection dbc = DatabaseConnection.getInstance();
		ResultSet rs = dbc
				.executeWithResult("SELECT item, quantity FROM inventory JOIN characters ON characters.id = inventory.character WHERE inventory.character = '"
						+ characterID + "';");

		Map<Integer, Integer> itemIDquantity = new HashMap<Integer, Integer>();

		try {
			while (rs.next()) {
				itemIDquantity.put(rs.getInt(1), rs.getInt(2));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (itemIDquantity.containsKey(itemID)) {
			// update quantities.
			int quantity = itemIDquantity.get(itemID);
			try {
				dbc.execute("UPDATE inventory SET quantity = '"
						+ (quantity + 1) + "' WHERE character = '"
						+ characterID + "' AND item = '" + itemID + "';");

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			try {
				dbc.execute("INSERT INTO inventory (character, item, quantity) VALUES ('"
						+ characterID + "', '" + itemID + "', '1');");

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Removes the given item ID from the given room.
	 * 
	 * @param roomID
	 *            The room ID to remove the item from.
	 * @param itemID
	 *            The item ID to remove from the room.
	 * @return Whether or not the item was removed.
	 */
	public static void removeItemFromRoom(int roomID, int itemID) {

		Map<Integer, Integer> itemsInRoom = DatabaseConnection
				.getItemIDsForRoom(roomID);

		if (itemsInRoom.containsKey(itemID)) {
			DatabaseConnection dbc = DatabaseConnection.getInstance();

			int quantity = itemsInRoom.get(itemID);

			if (quantity < 2) {
				// remove entry from roomitems
				try {
					dbc.execute("DELETE FROM roomitems WHERE roomid = '"
							+ roomID + "' AND itemID = '" + itemID + "';");

				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (quantity >= 2) {
				// update entry in roomitems
				try {
					dbc.execute("UPDATE roomitems SET quantity ='"
							+ (quantity - 1) + "' WHERE roomid ='" + roomID
							+ "' AND itemid ='" + itemID + "';");

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Removes the given item ID from the given character.
	 * 
	 * @param characterID
	 *            The character ID to remove the item from.
	 * @param itemID
	 *            The item ID to remove from the character.
	 * @return Whether or not the item was removed.
	 */
	public static void removeItemFromCharacter(int characterID, int itemID) {

		Map<Integer, Integer> characterItems = DatabaseConnection
				.getInventoryForCharacter(characterID);

		if (characterItems.containsKey(itemID)) {
			int quantity = characterItems.get(itemID);

			DatabaseConnection dbc = DatabaseConnection.getInstance();

			if (quantity < 2) {
				try {
					dbc.execute("DELETE FROM inventory WHERE character ='"
							+ characterID + "' AND item = '" + itemID + "';");

				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (quantity >= 2) {
				try {
					dbc.execute("UPDATE inventory SET quantity ='"
							+ (quantity - 1) + "' WHERE character = '"
							+ characterID + "' AND item = '" + itemID + "';");

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Adds the given item to the given room.
	 * 
	 * @param roomID
	 *            The room ID to add the item to.
	 * @param itemID
	 *            The item ID to add to the room.
	 */
	public static void addItemToRoom(int roomID, int itemID) {
		DatabaseConnection dbc = DatabaseConnection.getInstance();

		Map<Integer, Integer> itemIDquantity = DatabaseConnection
				.getItemIDsForRoom(roomID);

		if (itemIDquantity.containsKey(itemID)) {
			// update quantities.
			int quantity = itemIDquantity.get(itemID);
			try {
				dbc.execute("UPDATE roomitems SET quantity = '"
						+ (quantity + 1) + "' WHERE roomid = '" + roomID
						+ "' AND itemid = '" + itemID + "';");

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			try {
				dbc.execute("INSERT INTO roomitems (roomid, itemid, quantity) VALUES ('"
						+ roomID + "', '" + itemID + "', '1');");

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Retrieves the item id of the given item name.
	 * 
	 * @param name
	 *            The name of the item whose ID we are querying.
	 * @return Item id of the given item name.
	 */
	public static int getIDFromItemName(String name) {

		DatabaseConnection dbc = DatabaseConnection.getInstance();
		ResultSet rs = dbc
				.executeWithResult("SELECT id FROM items WHERE name = '" + name
						+ "';");
		int id = -1;
		try {
			id = rs.getInt(1);
			rs.close();
		} catch (SQLException e) {
			try {
				rs.close();
			} catch (SQLException e1) {
			}
			return id;
		}

		return id;
	}

	/**
	 * Gets the damage bonus from items equipped for the given Character.
	 * 
	 * @param character
	 *            The Character ID of the Character whose damage bonus we want.
	 * @return The damage bonus from items equipped for the given Character.
	 */
	public static int getDamageBonusForCharacter(int characterID) {

		DatabaseConnection dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc
				.executeWithResult("SELECT item FROM characterequip WHERE character = '"
						+ characterID + "';");
		int damage = 0;
		try {
			while (rs.next()) {
				ResultSet rs2 = dbc
						.executeWithResult("SELECT effectamount FROM items WHERE id = '"
								+ rs.getString(1) + "' AND type = 'WEAPON';");
				if (!rs2.isClosed())
					damage += Integer.parseInt(rs2.getString(1));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return damage;
	}

	/**
	 * Gets the armor bonus from items equipped for the given Character.
	 * 
	 * @param character
	 *            The Character ID of the Character whose armor bonus we want.
	 * @return The armor bonus from items equipped for the given Character.
	 */
	public static int getArmorBonusForCharacter(int characterID) {
		DatabaseConnection dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc
				.executeWithResult("SELECT item FROM characterequip WHERE character = '"
						+ characterID + "';");
		int armor = 0;
		try {
			while (rs.next()) {
				ResultSet rs2 = dbc
						.executeWithResult("SELECT effectamount FROM items WHERE id = '"
								+ rs.getString(1) + "' AND type = 'ARMOR';");
				if (!rs2.isClosed())
					armor += Integer.parseInt(rs2.getString(1));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return armor;
	}

	/**
	 * Gets the kills the given Character has made.
	 * 
	 * @param character
	 *            The Character ID of the Character whose kills we want.
	 * @return The kills the given Character has made.
	 */
	public static Map<Integer, Integer> getKillsForCharacter(int characterID) {

		DatabaseConnection dbc = DatabaseConnection.getInstance();
		ResultSet rs = dbc
				.executeWithResult("SELECT mob, quantity FROM characterkills WHERE character = '"
						+ characterID + "';");

		Map<Integer, Integer> kills = new HashMap<Integer, Integer>();

		try {
			while (rs.next()) {
				kills.put(Integer.parseInt(rs.getString(1)),
						Integer.parseInt(rs.getString(2)));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return kills;
	}

	/**
	 * Adds a kill to the given Character.
	 * 
	 * @param characterID
	 *            The Character to add the kill to.
	 * @param mobID
	 *            The Mob that the Character killed.
	 */
	public static void addKill(int characterID, int mobID) {

		DatabaseConnection dbc = DatabaseConnection.getInstance();

		Map<Integer, Integer> kills = DatabaseConnection
				.getKillsForCharacter(characterID);
		if (kills.containsKey(mobID)) {
			// The kill is already in the database, so let's update the
			// quantity.
			try {
				dbc.execute("UPDATE characterkills SET quantity = '"
						+ (kills.get(mobID) + 1) + "' WHERE character = '"
						+ characterID + "' AND mob = '" + mobID + "';");

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			// The kill is not in the database, so let's insert it into the
			// database.
			try {
				dbc.execute("INSERT INTO characterkills (character, mob, quantity) VALUES ('"
						+ characterID + "','" + mobID + "', '1');");

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets the item ID and and item type of items equipped on a given
	 * Character.
	 * 
	 * @param characterID
	 *            The ID of the Character whose equipped items we want.
	 * @return The item ID and and item type of items equipped on a given
	 *         Character.
	 */
	public static Map<Integer, String> getEquippedItems(int characterID) {

		DatabaseConnection dbc = DatabaseConnection.getInstance();
		Map<Integer, String> equippedItems = new HashMap<Integer, String>();

		ResultSet rs = dbc
				.executeWithResult("SELECT item, type FROM characterequip WHERE character = '"
						+ characterID + "';");

		try {
			while (rs.next()) {
				equippedItems.put(Integer.parseInt(rs.getString(1)),
						rs.getString(2));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return equippedItems;
	}

	/**
	 * Equip an item to a Character.
	 * 
	 * @param characterID
	 *            The ID of the Character to equip an Item to.
	 * @param itemID
	 *            The ID of the Item to equip to the Character.
	 */
	public static void equipItem(int characterID, int itemID) {

		DatabaseConnection dbc = DatabaseConnection.getInstance();
		Item item = new Item(itemID);
		try {
			dbc.execute("INSERT INTO characterequip (character, item, type) VALUES ('"
					+ characterID
					+ "', '"
					+ itemID
					+ "', '"
					+ item.get("type")
					+ "');");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Un-equip an item to a Character.
	 * 
	 * @param characterID
	 *            The ID of the Character to remove the Item from.
	 * @param itemID
	 *            The ID of the Item to remove from the Character.
	 */
	public static void unequipItem(int characterID, int itemID) {
		DatabaseConnection dbc = DatabaseConnection.getInstance();
		try {
			dbc.execute("DELETE FROM characterequip WHERE character ='"
					+ characterID + "' AND item = '" + itemID + "';");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void healCharacterHP(int characterID, int effectAmount) {
		DatabaseConnection dbc = DatabaseConnection.getInstance();
		try {
			Character c = new Character(characterID);

			if (c.getID() == -1) {
				return;
			}

			int level = Integer.parseInt(c.get("level"));

			int newHP = Integer.parseInt(c.get("hp")) + effectAmount;

			if (newHP > c.getMaxHP(level))
				dbc.execute("UPDATE characters SET hp = '" + c.getMaxHP(level)
						+ "' WHERE id = '" + characterID + "';");
			else
				dbc.execute("UPDATE characters SET hp = '" + newHP
						+ "' WHERE id = '" + characterID + "';");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void healCharacterAP(int characterID, int effectAmount) {
		DatabaseConnection dbc = DatabaseConnection.getInstance();
		try {
			Character c = new Character(characterID);

			if (c.getID() == -1) {
				return;
			}

			int level = Integer.parseInt(c.get("level"));

			int newAP = Integer.parseInt(c.get("hp")) + effectAmount;

			if (newAP > c.getMaxAP(level))
				dbc.execute("UPDATE characters SET ap = '" + c.getMaxAP(level)
						+ "' WHERE id = '" + characterID + "';");
			else
				dbc.execute("UPDATE characters SET ap = '" + newAP
						+ "' WHERE id = '" + characterID + "';");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Turns lights on for a Character.
	 * 
	 * @param characterID
	 *            The Character whose lights to turn on.
	 */
	public static void turnLightsOn(int characterID) {
		DatabaseConnection dbc = DatabaseConnection.getInstance();
		try {
			dbc.execute("UPDATE characters SET lights = 'true';");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Turns lights off for a Character.
	 * 
	 * @param characterID
	 *            The Character whose lights to turn off.
	 */
	public static void turnLightsOff(int characterID) {
		DatabaseConnection dbc = DatabaseConnection.getInstance();
		try {
			dbc.execute("UPDATE characters SET lights = 'false';");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns whether or not a User is an administrator.
	 * 
	 * @param userID
	 *            The User ID to query.
	 * @return Whether or not a User is an administrator.
	 */
	public static boolean isAdmin(int userID) {

		DatabaseConnection dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc
				.executeWithResult("SELECT admin FROM users WHERE id = '"
						+ userID + "';");

		boolean isAdmin = false;
		try {
			isAdmin = Boolean.parseBoolean(rs.getString(1));
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return isAdmin;
	}

	/**
	 * Gets a list of IDs for NPCs that are permanent and unattackable.
	 * 
	 * @param i
	 * 
	 * @return A list of IDs for NPCs that are permanent and unattackable.
	 */
	public static List<Integer> getPermanentNPCs(int roomID) {

		List<Integer> npcs = new ArrayList<Integer>();
		DatabaseConnection dbc = DatabaseConnection.getInstance();
		ResultSet rs = dbc
				.executeWithResult("SELECT id FROM npcs WHERE homeroom ='"
						+ roomID + "';");
		try {
			while (rs.next()) {
				npcs.add(rs.getInt(1));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return npcs;
	}

	public static String getItemDescription(int itemID) {

		String description = "";

		DatabaseConnection dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc
				.executeWithResult("SELECT description FROM items WHERE id = '"
						+ itemID + "';");
		try {
			description = rs.getString(1);
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return description;
	}

	public static int getNumberOfMobs() {

		DatabaseConnection dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc.executeWithResult("SELECT COUNT(*) FROM roommobs;");
		int numMobs = 0;
		try {
			numMobs = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return numMobs;
	}

	public static int getNumberOfRooms() {

		DatabaseConnection dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc.executeWithResult("SELECT COUNT(*) FROM rooms;");
		int numRooms = 0;
		try {
			numRooms = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return numRooms;
	}

	public static void addMob(int mobid, int uid, int hp, int location,
			String name, int level) {

		DatabaseConnection dbc = DatabaseConnection.getInstance();

		try {
			dbc.execute("INSERT INTO roommobs (mobid, uid, hp, location, name, attacked, attackerid, lastattacked, level) VALUES ('"
					+ mobid
					+ "', '"
					+ uid
					+ "', '"
					+ hp
					+ "', '"
					+ location
					+ "', '"
					+ name
					+ "', 'false', '0', '"
					+ Calendar.getInstance().getTimeInMillis()
					+ "', '"
					+ level
					+ "');");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String getMobName(int uid) {

		String name = "";

		DatabaseConnection dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc
				.executeWithResult("SELECT name FROM roommobs WHERE uid = '"
						+ uid + "';");

		try {
			if (rs.next())
				name = rs.getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return name;
	}

	public static int getMobIDFromClass(String mobClass) {

		DatabaseConnection dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc
				.executeWithResult("SELECT id FROM mobs WHERE mobclass = '"
						+ mobClass + "';");

		int mobid = 0;

		try {
			mobid = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mobid;
	}

	public static void removeAllMobs() {
		DatabaseConnection dbc = DatabaseConnection.getInstance();
		try {
			dbc.execute("DELETE FROM roommobs;");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<Integer> getMobs(int roomID) {
		List<Integer> mobs = new ArrayList<Integer>();

		DatabaseConnection dbc = DatabaseConnection.getInstance();
		ResultSet rs = dbc
				.executeWithResult("SELECT uid FROM roommobs WHERE location ='"
						+ roomID + "';");
		try {
			while (rs.next()) {
				mobs.add(rs.getInt(1));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mobs;
	}

	public static boolean isBanned(String IP) {

		DatabaseConnection dbc = DatabaseConnection.getInstance();
		ResultSet rs = dbc
				.executeWithResult("SELECT id FROM bannedips WHERE ip = '" + IP
						+ "';");

		int i = 0;
		try {
			if (!rs.isClosed())
				i = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return (i > 0);
	}

	public static void banByIP(String IP) {
		DatabaseConnection dbc = DatabaseConnection.getInstance();
		try {
			dbc.execute("INSERT INTO bannedips (ip) VALUES ('" + IP + "');");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static int getMobIDFromID(int uid) {

		DatabaseConnection dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc
				.executeWithResult("SELECT mobid FROM roommobs WHERE uid = '"
						+ uid + "';");

		int mobID = -1;
		try {
			mobID = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mobID;
	}

	public static int getMobLocation(int uid) {

		int location = 0;

		DatabaseConnection dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc
				.executeWithResult("SELECT location FROM roommobs WHERE uid = '"
						+ uid + "';");

		try {
			location = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return location;
	}

	public static void setMobLocation(int uid, int roomID) {

		DatabaseConnection dbc = DatabaseConnection.getInstance();
		try {
			dbc.execute("UPDATE roommobs SET location = '" + roomID
					+ "' WHERE uid = '" + uid + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static boolean isMobAttacked(int uid) {

		boolean isAttacked = false;

		DatabaseConnection dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc
				.executeWithResult("SELECT attacked FROM roommobs WHERE uid = '"
						+ uid + "';");

		try {
			isAttacked = Boolean.parseBoolean(rs.getString(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isAttacked;
	}

	public static int getMobAttacker(int uid) {

		int id = 0;

		DatabaseConnection dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc
				.executeWithResult("SELECT attackerid FROM roommobs WHERE uid = '"
						+ uid + "';");

		try {
			id = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}

	public static void setMobAttacked(int uid, int characterID, boolean attacked) {
		DatabaseConnection dbc = DatabaseConnection.getInstance();

		try {
			dbc.execute("UPDATE roommobs SET attacked = '" + attacked
					+ "', attackerid = '" + characterID + "' WHERE uid = '"
					+ uid + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static int getMobHP(int uid) {
		DatabaseConnection dbc = DatabaseConnection.getInstance();
		int hp = 0;
		ResultSet rs = dbc
				.executeWithResult("SELECT hp FROM roommobs WHERE uid ='" + uid
						+ "';");
		try {
			hp = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hp;
	}

	public static void setMobHP(int uid, int hp) {
		DatabaseConnection dbc = DatabaseConnection.getInstance();

		try {
			dbc.execute("UPDATE roommobs SET hp = '" + hp + "' WHERE uid = '"
					+ uid + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static int getMobExpValue(int id) {
		int exp = 0;

		DatabaseConnection dbc = DatabaseConnection.getInstance();
		ResultSet rs = dbc
				.executeWithResult("SELECT expvalue FROM mobs WHERE id ='" + id
						+ "';");

		try {
			exp = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return exp;
	}

	public static void removeMob(int uid) {
		DatabaseConnection dbc = DatabaseConnection.getInstance();

		try {
			dbc.execute("DELETE FROM roommobs WHERE uid = '" + uid + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void setBanned(int userID, boolean banned) {
		DatabaseConnection dbc = DatabaseConnection.getInstance();

		try {
			dbc.execute("UPDATE users SET banned = '" + banned
					+ "' WHERE id = '" + userID + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void deleteUser(int id) {
		DatabaseConnection dbc = DatabaseConnection.getInstance();

		try {
			dbc.execute("DELETE FROM users WHERE id = '" + id + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void deleteCharacter(int id) {
		DatabaseConnection dbc = DatabaseConnection.getInstance();

		try {
			dbc.execute("DELETE FROM characters WHERE id = '" + id + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static int getMobLevel(int uid) {
		int level = 0;

		DatabaseConnection dbc = DatabaseConnection.getInstance();
		ResultSet rs = dbc
				.executeWithResult("SELECT level FROM roommobs WHERE uid ='"
						+ uid + "';");

		try {
			level = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return level;
	}

	public static List<Integer> getCharacters() {
		List<Integer> idList = new ArrayList<Integer>();

		DatabaseConnection dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc.executeWithResult("SELECT id FROM characters;");

		try {
			while (rs.next()) {
				idList.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return idList;
	}

	public static List<Integer> getCharacterLevels() {

		List<Integer> levels = new ArrayList<Integer>();
		List<Integer> idList = new ArrayList<Integer>();
		DatabaseConnection dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc
				.executeWithResult(" SELECT characterid FROM usercharacters "
						+ "JOIN users WHERE users.banned = 'false' "
						+ "AND users.id = usercharacters.userid;");

		try {
			while (rs.next()) {
				idList.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		for (int i : idList) {
			ResultSet rs2 = dbc
					.executeWithResult("SELECT level FROM characters WHERE id = '"
							+ i + "';");
			try {
				if (!rs.isClosed())
					levels.add(rs2.getInt(1));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return levels;
	}

	public static String getLockedDoor(int roomID) {

		String lockedDoor = "";

		DatabaseConnection dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc.executeWithResult(" SELECT door FROM rooms "
				+ "WHERE id = '" + roomID + "';");
		try {
			if (!rs.isClosed())
				lockedDoor = rs.getString(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lockedDoor;
	}

	public static int getRequiredLock(int roomID) {

		int itemID = 0;
		DatabaseConnection dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc.executeWithResult(" SELECT requireditem FROM rooms "
				+ "WHERE id = '" + roomID + "';");
		try {
			if (!rs.isClosed())
				itemID = rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemID;
	}

	public static void setDoorLocked(int roomID, boolean locked) {

		DatabaseConnection dbc = DatabaseConnection.getInstance();

		try {
			dbc.execute("UPDATE rooms SET locked = '" + locked
					+ "' WHERE id = '" + roomID + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static boolean getDoorLocked(int roomID) {

		boolean locked = false;

		DatabaseConnection dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc.executeWithResult(" SELECT locked FROM rooms "
				+ "WHERE id = '" + roomID + "';");
		try {
			locked = Boolean.parseBoolean(rs.getString(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return locked;
	}

	public static List<Integer> getNpcs() {

		List<Integer> npcs = new ArrayList<Integer>();

		DatabaseConnection dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc.executeWithResult("SELECT id FROM npcs;");

		try {
			while (rs.next()) {
				npcs.add(rs.getInt(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return npcs;
	}

	public static int getIDFromNPCName(String name) {

		int id = -1;

		DatabaseConnection dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc
				.executeWithResult("SELECT id FROM npcs WHERE name = '" + name
						+ "';");

		try {
			if (!rs.isClosed()) {
				id = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return id;
	}

	public static List<Integer[]> getNPCInventory(int id) {
		List<Integer[]> npcInventory = new ArrayList<Integer[]>();
		DatabaseConnection dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc
				.executeWithResult("SELECT itemid, quantity, price FROM npcinventory WHERE npcid = '"
						+ id + "';");

		try {
			while (rs.next()) {
				Integer[] entry = { rs.getInt(1), rs.getInt(2), rs.getInt(3) };
				npcInventory.add(entry);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return npcInventory;
	}

	public static int getBottleCapsForCharacter(int id) {

		int caps = 0;

		DatabaseConnection dbc = DatabaseConnection.getInstance();

		ResultSet rs = dbc
				.executeWithResult("SELECT quantity FROM inventory WHERE item='21' AND character='"
						+ id + "';");

		try {
			if (!rs.isClosed()) {
				caps = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return caps;
	}

	public static void removeItemFromNPC(int npcID, int itemID) {

		List<Integer[]> npcItems = DatabaseConnection.getNPCInventory(npcID);

		boolean containsItem = false;
		int quantity = 0;
		for (Integer[] i : npcItems) {
			if (i[0] == itemID) {
				containsItem = true;
				quantity = i[1];
			}
		}

		if (!containsItem) {
			return;
		}

		DatabaseConnection dbc = DatabaseConnection.getInstance();

		if (quantity < 2) {
			try {
				dbc.execute("DELETE FROM npcinventory WHERE npcid = '" + npcID
						+ "' AND itemid = '" + itemID + "';");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (quantity >= 2) {
			try {
				dbc.execute("UPDATE npcinventory SET quantity = '"
						+ (quantity - 1) + "' WHERE npcid = '" + npcID
						+ "' AND itemid = '" + itemID + "';");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void addItemToNPC(int npcID, int itemID, int price) {

		List<Integer[]> npcItems = DatabaseConnection.getNPCInventory(npcID);

		boolean containsItem = false;
		int quantity = 0;
		for (Integer[] i : npcItems) {
			if (i[0] == itemID) {
				containsItem = true;
				quantity = i[1];
			}
		}

		DatabaseConnection dbc = DatabaseConnection.getInstance();

		if (containsItem) {
			try {
				dbc.execute("UPDATE npcinventory SET quantity = '"
						+ (quantity + 1) + "' WHERE npcid = '" + npcID
						+ "' AND itemid = '" + itemID + "';");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			try {
				dbc.execute("INSERT INTO npcinventory (npcid, itemid, quantity, price) VALUES ('"
						+ npcID + "', '" + itemID + "', '1', '" + price + "');");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
