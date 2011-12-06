package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import library.Character;
import library.ClassType;
import library.DatabaseConnection;
import library.Item;
import library.ItemType;
import library.Mob;
import library.MobAdjective;
import library.MobClass;
import library.NonPlayerCharacter;
import library.Room;
import library.User;

/**
 * This class listens on a clientPort for Clients with a ServerSocket to connect
 * to and provides a majority of the game functionality.
 * 
 * @author Nicholas Eddy, Kyohei Mizokami, Mike Novak, Chris Panzero
 * 
 */
public class Server extends Observable implements Runnable, Serializable {

	private static final long serialVersionUID = 1L;
	private ServerSocket clientServerSocket;
	private static Map<ClientHandler, ObjectOutputStream> clients;
	private static List<Integer> npcUIDs;
	private int clientPort;
	private static List<String> log;
	private static MobSpawner mobSpawner;
	private static MobMover mobMover;
	private static DoorLocker doorLocker;
	private static LightChanger lightChanger;
	private static DatabaseConnection dbc;
	private static final int NUMBER_OF_MOBS = 15;
	// Spawn mobs every MOB_SPAWN_RATE seconds.
	private static final long MOB_SPAWN_RATE = 60 * 1000;
	// Move mobs every MOB_MOVE_RATE seconds.
	private static final long MOB_MOVE_RATE = 45 * 1000;
	// Lock doors every LOCK_DOOR_RATE seconds.
	private static final long LOCK_DOOR_RATE = 10 * 60 * 1000;
	// Turn off lights every LIGHT_CHANGE_RATE seconds.
	private static final long LIGHT_CHANGE_RATE = 5 * 60 * 1000;
	// Mob difficulty multiplier
	private static int MOB_DIFFICULTY; // Recommend 2-4

	public Server(int port, int mobDifficulty) {
		this.clientPort = port;
		MOB_DIFFICULTY = mobDifficulty;
		clients = new HashMap<ClientHandler, ObjectOutputStream>();
		npcUIDs = new ArrayList<Integer>();
		log = new ArrayList<String>();
		mobSpawner = new MobSpawner();
		mobMover = new MobMover();
		doorLocker = new DoorLocker();
		lightChanger = new LightChanger();
		dbc = DatabaseConnection.getInstance();
	}

	@Override
	public void run() {
		try {
			// Empty mobs on first run.
			DatabaseConnection.removeAllMobs();
			// Start up Mob Spawner
			Timer t = new Timer();
			t.schedule(mobSpawner, 0, MOB_SPAWN_RATE);
			// Start up Mob Mover
			t.schedule(mobMover, MOB_MOVE_RATE, MOB_MOVE_RATE);
			// Start up Door Locker
			t.schedule(doorLocker, 0, LOCK_DOOR_RATE);
			// Start up Light Changer
			t.schedule(lightChanger, 0, LIGHT_CHANGE_RATE);

			// Attempt to start listening on given clientPort.
			clientServerSocket = new ServerSocket(clientPort);

			/*
			 * ------- main server loop -------
			 */
			while (true) {
				Socket s;
				ClientHandler c;
				// Accept Client connections.
				s = clientServerSocket.accept();
				// Only allow them to continue if the Client's IP isn't banned.
				if (!DatabaseConnection.isBanned(s.getInetAddress().toString())) {
					addToLog("Got Client: " + s);
					c = new ClientHandler(s);
					clients.put(c, new ObjectOutputStream(s.getOutputStream()));
					// Start the Client's ClientHandler thread.
					c.start();
				} else {
					addToLog(s + " tried to log in but was IP banned.");
					new ObjectOutputStream(s.getOutputStream())
							.writeObject(new Message(
									"You have been IP banned.",
									MessageType.CLIENT_KICKED));
					s.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Map<ClientHandler, ObjectOutputStream> getClients() {
		return clients;
	}

	/**
	 * Sends a Message to all of the Clients connected to the Server.
	 * 
	 * @param m
	 *            The Message to send to all of the Clients.
	 */
	public static void notifyAll(Message m) {
		for (Entry<ClientHandler, ObjectOutputStream> c : clients.entrySet()) {
			try {
				c.getValue().writeObject(m);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Adds a String to the activity log.
	 * 
	 * @param s
	 *            The String to add.
	 */
	public void addToLog(String s) {
		log.add(s);
		setChanged();
		notifyObservers(log);
	}

	/**
	 * Gets the average Character level of all Characters regardless of them
	 * being online or off-line.
	 * 
	 * @return The average Character level of all Characters.
	 */
	public int getAverageCharacterLevel() {

		int totalLevel = 0;
		int numCharacters = 0;

		List<Integer> charLevels = DatabaseConnection.getCharacterLevels();
		numCharacters = charLevels.size();
		for (int i : charLevels) {
			totalLevel += i;
		}
		return numCharacters == 0 ? 3 : (totalLevel / numCharacters);
	}

	public static int getLevelFromExp(int exp) {
		if (exp < 200) {
			return 1;
		} else if (exp < 550) {
			return 2;
		} else if (exp < 1050) {
			return 3;
		} else if (exp < 1700) {
			return 4;
		} else if (exp < 2500) {
			return 5;
		} else if (exp < 3450) {
			return 6;
		} else if (exp < 4550) {
			return 7;
		} else if (exp < 5800) {
			return 8;
		} else if (exp < 7200) {
			return 9;
		} else if (exp < 8750) {
			return 10;
		} else if (exp < 10450) {
			return 11;
		} else if (exp < 12300) {
			return 12;
		} else if (exp < 14300) {
			return 13;
		} else if (exp < 16450) {
			return 14;
		} else if (exp < 18750) {
			return 15;
		} else if (exp < 21200) {
			return 16;
		} else if (exp < 23800) {
			return 17;
		} else if (exp < 26550) {
			return 18;
		} else if (exp < 29450) {
			return 19;
		} else {
			return 20;
		}
	}

	public int getNextExp(int level) {

		switch (level) {
		case 1:
			return 200;
		case 2:
			return 550;
		case 3:
			return 1050;
		case 4:
			return 1700;
		case 5:
			return 2500;
		case 6:
			return 3450;
		case 7:
			return 4550;
		case 8:
			return 5800;
		case 9:
			return 7200;
		case 10:
			return 8750;
		case 11:
			return 10450;
		case 12:
			return 12300;
		case 13:
			return 14300;
		case 14:
			return 16450;
		case 15:
			return 18750;
		case 16:
			return 21200;
		case 17:
			return 23800;
		case 18:
			return 26550;
		case 19:
			return 29450;
		default:
			return 0;
		}
	}

	/**
	 * 
	 * @author Nicholas Eddy, Kyohei Mizokami, Mike Novak, Chris Panzero
	 * 
	 */
	private class MobMover extends TimerTask {

		@Override
		public void run() {
			Random moveGenerator = new Random();

			int numMobs = DatabaseConnection.getNumberOfMobs();

			for (int i = 0; i < numMobs; i++) {
				boolean doesMove = moveGenerator.nextBoolean();
				if (doesMove) {

					int uid = npcUIDs.get(i);

					if (DatabaseConnection.isMobAttacked(uid)) {
						continue;
					}

					int location = DatabaseConnection.getMobLocation(uid);

					Room room = new Room(location);

					int north = Integer.parseInt(room.get("north"));
					int south = Integer.parseInt(room.get("south"));
					int east = Integer.parseInt(room.get("east"));
					int west = Integer.parseInt(room.get("west"));

					int whichRoom = moveGenerator.nextInt(5);

					switch (whichRoom) {

					case 0: // north
						if (north != 0 && north != 1) {
							DatabaseConnection.setMobLocation(uid, north);
							notifyPlayers(uid, location, north, "north");
							doMobAttack(north);
							break;
						}

					case 1: // east
						if (east != 0 && east != 1) {
							DatabaseConnection.setMobLocation(uid, east);
							notifyPlayers(uid, location, east, "east");
							doMobAttack(east);
							break;
						}

					case 2: // south
						if (south != 0 && south != 1) {
							DatabaseConnection.setMobLocation(uid, south);
							notifyPlayers(uid, location, south, "south");
							doMobAttack(south);
							break;
						}

					case 3: // west
						if (west != 0 && west != 1) {
							DatabaseConnection.setMobLocation(uid, west);
							notifyPlayers(uid, location, west, "west");
							doMobAttack(west);
							break;
						}
					}
				}
			}
		}

		private void notifyPlayers(int uid, int location, int destination,
				String direction) {
			for (Entry<ClientHandler, ObjectOutputStream> c : getClients()
					.entrySet()) {

				Character character = c.getKey().character;
				if (character != null) {
					if (Integer.parseInt(character.get("location")) == location) {
						c.getKey()
								.sendMessage(
										new Message(
												"<span class=\"enemy\">"
														+ DatabaseConnection
																.getMobName(uid)
														+ "</span><span class=\"normal\"> has gone "
														+ direction
														+ ".</span><br />",
												MessageType.DISPLAY));
					}
				}
			}
			for (Entry<ClientHandler, ObjectOutputStream> c : getClients()
					.entrySet()) {

				Character character = c.getKey().character;
				if (character != null) {
					if (Integer.parseInt(character.get("location")) == destination) {
						c.getKey()
								.sendMessage(
										new Message(
												"<span class=\"enemy\">"
														+ DatabaseConnection
																.getMobName(uid)
														+ "</span><span class=\"normal\"> has entered the room!</span><br />",
												MessageType.DISPLAY));
					}
				}
			}
		}
	}

	/**
	 * Attacks any Character located in room with roomID if the Mob is hostile.
	 * 
	 * @param roomID
	 *            The roomID to check for Characters to attack.
	 */
	public static void doMobAttack(final int roomID) {

		// get list of mobs, see which ones are hostile.

		List<Integer> mobUIDs = DatabaseConnection.getMobs(roomID);
		final List<Integer> hostileMobs = new ArrayList<Integer>();

		for (int i : mobUIDs) {
			Mob m = new Mob(DatabaseConnection.getMobIDFromID(i));

			if (Boolean.parseBoolean(m.get("hostile"))) {
				hostileMobs.add(i);
			}
		}

		TimerTask task = new TimerTask() {

			@Override
			public void run() {

				for (int uid : hostileMobs) {

					Random randomGenerator = new Random();
					Mob m = new Mob(DatabaseConnection.getMobIDFromID(uid));

					// Make attacking Random.
					List<Integer> charsInRoom = getCharacterIDsInRoom(roomID);

					int charToAttack = 0;

					for (int charInRoom : charsInRoom) {

						if (randomGenerator.nextBoolean()) {
							charToAttack = charInRoom;
						}
					}

					if (charsInRoom.size() == 0) {
						charToAttack = 0;
					}

					ClientHandler c = null;

					for (Entry<ClientHandler, ObjectOutputStream> e : getClients()
							.entrySet()) {

						if (e.getKey().character == null)
							continue;

						if (e.getKey().getCharacterID() == charToAttack) {
							c = e.getKey();
						}

						if (c != null) {

							int characterHP = Integer.parseInt(c.character
									.get("hp"));
							int defense = c.character.getDefense();
							int mobLevel = DatabaseConnection.getMobLevel(uid);
							int mobDamage = m.getDamage(mobLevel,
									MOB_DIFFICULTY) - defense;

							mobDamage = mobDamage < 1 ? mobLevel : mobDamage;

							int level = Integer.parseInt(c.character
									.get("level"));

							if (characterHP - mobDamage <= 0) {
								// Character is dead. Move back to room 1
								// and
								// reduce
								// experience by 5%

								int reducedXP = (int) (Integer
										.parseInt(c.character.get("experience")) * .95);

								c.character.set("hp",
										c.character.getMaxHP(level) + "");
								c.character.set("location", "1");
								c.character.set("experience", reducedXP + "");

								try {
									c.character.save();
								} catch (SQLException e2) {
									e2.printStackTrace();
								}

								c.sendMessage(new Message(
										"<span class=\"enemy\">"
												+ DatabaseConnection
														.getMobName(uid)
												+ "</span><span class=\"normal\"> attacked you for "
												+ mobDamage
												+ " and you nearly died!</span><br /><span class=\"normal\">You barely escape from death"
												+ " and find yourself at the Vault entrance.</span><br />",
										MessageType.DISPLAY));
								// Mob isn't attacked anymore.
								DatabaseConnection
										.setMobAttacked(uid, 0, false);
							} else {
								c.character.set("hp", (characterHP - mobDamage)
										+ "");

								try {
									c.character.save();
								} catch (SQLException e1) {
									e1.printStackTrace();
								}

								c.sendMessage(new Message(
										"<span class=\"enemy\">"
												+ DatabaseConnection
														.getMobName(uid)
												+ "</span><span class=\"normal\"> attacks you for "
												+ mobDamage + "!</span><br />",
										MessageType.DISPLAY));
							}
						}
					}

				}
				this.cancel();
			}
		};
		Timer t = new Timer();
		// Attack in 5 seconds. First run will cancel and not repeat.
		t.schedule(task, 5000, 100000);
	}

	private class MobSpawner extends TimerTask {

		@Override
		public void run() {

			// Add mobs to rooms while there aren't enough of them.
			// Checks every minute.
			Random uidGenerator = new Random();
			Random classGenerator = new Random();
			Random locationGenerator = new Random();
			Random mobAdjectiveGenerator = new Random();

			while (DatabaseConnection.getNumberOfMobs() < NUMBER_OF_MOBS) {

				int uid = uidGenerator.nextInt();
				int classType = classGenerator
						.nextInt(MobClass.values().length);

				int mobAdjective = mobAdjectiveGenerator.nextInt(MobAdjective
						.values().length);

				String mobAdjectiveString = "";

				int k = 0;
				for (MobAdjective adj : MobAdjective.values()) {
					if (k == mobAdjective) {
						mobAdjectiveString = adj.name();
					}
					k++;
				}

				MobClass mobClass = null;
				int i = 0;
				for (MobClass mob : MobClass.values()) {
					if (i == classType) {
						mobClass = mob;
					}
					i++;
				}

				int mobid = DatabaseConnection.getMobIDFromClass(mobClass
						.name());

				Mob mob = new Mob(mobid);

				int location = locationGenerator.nextInt(28) + 2;
				int mobLevel = classGenerator.nextBoolean() ? getAverageCharacterLevel()
						+ uidGenerator.nextInt(3)
						: getAverageCharacterLevel() - uidGenerator.nextInt(3);

				if (mobLevel < 1)
					mobLevel = 1;

				DatabaseConnection.addMob(mobid, uid,
						Integer.parseInt(mob.get("hp")), location,
						mobAdjectiveString + " " + mob.get("name"), mobLevel);

				addToLog("Got Mob " + uid + ": " + mobAdjectiveString + " "
						+ mob.get("name") + " in room " + location);

				npcUIDs.add(uid);
				setChanged();
				notifyObservers(log);
			}
		}
	}

	/**
	 * Shuts down the server.
	 */
	public static void shutdown() {

		mobSpawner.cancel();
		mobMover.cancel();

		TimerTask task = new TimerTask() {

			private int TIMES_EXECUTED = 0;

			@Override
			public void run() {

				if (TIMES_EXECUTED == 10) {

					int i = 0;
					ClientHandler[] toDisconnect = new ClientHandler[clients
							.size()];

					for (Entry<ClientHandler, ObjectOutputStream> e : clients
							.entrySet()) {
						toDisconnect[i] = e.getKey();
						i++;
					}

					for (int j = 0; j < toDisconnect.length; j++) {
						toDisconnect[j].disconnect();
					}

					dbc.close(); // SUPER DUPER IMPORTANT.
					this.cancel();
					System.exit(0);
				}
				Server.notifyAll(new Message(
						"<span class=\"normal\">Server is shutting down in "
								+ (10 - TIMES_EXECUTED)
								+ " seconds.</span><br />", MessageType.DISPLAY));
				log.add("Server is shutting down in " + (10 - TIMES_EXECUTED)
						+ " seconds.");
				TIMES_EXECUTED++;
			}
		};
		Timer t = new Timer();
		t.schedule(task, 0, 1000);
	}

	/**
	 * Gets a list of online players in a given room.
	 * 
	 * @param roomID
	 *            ID of the room to search.
	 * @return A list of online players in a given room.
	 */
	public static List<String> getCharactersInRoom(int roomID) {

		List<String> charactersInRoom = new ArrayList<String>();

		for (Entry<ClientHandler, ObjectOutputStream> e : getClients()
				.entrySet()) {

			Character c = e.getKey().character;

			if (c != null && e.getKey().online
					&& (Integer.parseInt(c.get("location")) == roomID)) {
				// Character is not null, character is online, and character
				// is in the same room.
				charactersInRoom.add(c.get("name"));
			}
		}
		return charactersInRoom;
	}

	public static List<Integer> getCharacterIDsInRoom(int roomID) {

		List<Integer> charactersInRoom = new ArrayList<Integer>();

		for (Entry<ClientHandler, ObjectOutputStream> e : getClients()
				.entrySet()) {

			Character c = e.getKey().character;

			if (c != null && e.getKey().online
					&& (Integer.parseInt(c.get("location")) == roomID)) {
				// Character is not null, character is online, and character
				// is in the same room.
				charactersInRoom.add(c.getID());
			}
		}
		return charactersInRoom;
	}

	/**
	 * ClientHandler is a Runnable that is spawned on a per-Client basis. Each
	 * Client gets their own thread in order to have interaction that happens in
	 * real time. It sends Messages for a Client to log in or create a user,
	 * select or create a character, and also sends Messages in response to
	 * Client interaction by receiving Messages from Clients and parsing them
	 * into a ClientCommand.
	 * 
	 * @author Nicholas Eddy, Kyohei Mizokami, Mike Novak, Chris Panzero
	 * 
	 */
	private class ClientHandler implements Runnable {

		private Socket socket;
		private ObjectInputStream ois;
		private User user;
		private Thread thread;
		private Character character;
		private boolean online;
		private String tradeItem;
		private int tradingTo;
		private boolean tradeAccept;

		public ClientHandler(Socket s) {
			this.socket = s;
			this.online = false;
			thread = new Thread(this);
			tradeItem = "";
			tradingTo = -1;
			tradeAccept = false;
		}

		/**
		 * Starts this ClientHandler by opening an ObjectInputStream and
		 * starting itself in a Thread.
		 */
		public void start() {
			try {
				ois = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			thread.start();
		}

		public int getCharacterID() {
			return this.character.getID();
		}

		@Override
		public void run() {
			// Run this while the ClientHandler has not quit the game.
			Message m;
			try {
				while ((m = (Message) ois.readObject()) != null) {

					switch (m.getMessageType()) {
					case LOGIN:
						login(m);
						break;
					case SELECT_CHARACTER:
						selectCharacter(m);
						break;
					case CREATE_USER:
						createUser(m);
						break;
					case CREATE_CHARACTER:
						createCharacter(m);
						break;
					case COMMAND:
						executeCommand(m);
						break;
					case QUIT:
						disconnect();
						break;
					}
				}
			} catch (IOException e) {
				getClients().remove(this);
			} catch (ClassNotFoundException e) {
				getClients().remove(this);
			}
		}

		public void trade(String item, int characterToTradeID) {
			tradeItem = item;
			tradingTo = characterToTradeID;
		}

		public String getTradingItem() {
			return this.tradeItem;
		}

		public int getCharacterTradingTo() {
			return this.tradingTo;
		}

		public void setTrade(boolean accept) {
			tradeAccept = accept;
		}

		public boolean getTrade() {
			return this.tradeAccept;
		}

		/**
		 * Try to set the Client's character to off line and remove it from the
		 * list of connected Clients.
		 */
		public void disconnect() {
			getClients().remove(this);
			if (this.character != null) {
				Server.notifyAll(new Message(
						"<span class=\"player\">"
								+ this.character.get("name")
								+ "</span><span class=\"normal\"> has disconnected.</span><br />",
						MessageType.DISPLAY));
				this.online = false;
				try {
					this.character.save();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			try {
				this.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Logs the Client in with data from the Message with MessageType LOGIN.
		 * 
		 * @param m
		 *            The Message containing login data.
		 */
		private void login(Message m) {
			// Data comes in the form of a String array.
			// [0] = user name
			// [1] = password
			String[] login = (String[]) m.getData();

			this.user = new User(login[0]);
			// User doesn't exist.
			if (user.getID() == -1) {
				addToLog(this.socket + " failed to login. User doesn't exist.");
				sendMessage(new Message("User doesn't exist.",
						MessageType.LOGIN_UNSUCCESSFUL));
				return;
			}

			// Make sure the User isn't already logged in.

			for (Entry<ClientHandler, ObjectOutputStream> e : getClients()
					.entrySet()) {

				User loggedInUser = e.getKey().user;

				if (loggedInUser == null) {
					continue;
				}

				if (loggedInUser.getID() == user.getID()
						&& e.getKey().character != null) {
					// User is already logged in.
					addToLog("User " + user.get("name")
							+ " tried to log in twice.");
					sendMessage(new Message("User already logged in.",
							MessageType.LOGIN_UNSUCCESSFUL));
					return;
				}
			}

			// If the user exists, make sure the password is correct.
			if (!user.validatePassword(login[1])) {
				// Wrong password was entered.
				addToLog(this.socket + " failed to validate password.");
				sendMessage(new Message("Incorrect password.",
						MessageType.LOGIN_UNSUCCESSFUL));
				return;
			}

			// User is banned.
			if (Boolean.parseBoolean(user.get("banned"))) {
				sendMessage(new Message("User is banned.",
						MessageType.LOGIN_UNSUCCESSFUL));
				return;
			}

			// Now the user needs to be notified they are logged in.
			// Send the available class types and list of characters.

			List<String> classTypes = new ArrayList<String>();

			for (ClassType ct : ClassType.values()) {
				classTypes.add(ct.toString());
			}

			Object[] data = { classTypes,
					DatabaseConnection.getCharactersForUser(login[0]) };
			sendMessage(new Message(data, MessageType.LOGIN_SUCCESSFUL));

		}

		/**
		 * Selects the character in with data from the Message with MessageType
		 * SELECT_CHARACTER.
		 * 
		 * @param m
		 *            The Message containing the selected character.
		 */
		private void selectCharacter(Message m) {
			// Data for SELECT_CHARACTER comes as a String.
			String character = (String) m.getData();
			this.character = new Character(
					DatabaseConnection.getIDFromCharacter(character));

			addToLog(this.user.get("username") + " logged in with character "
					+ character + ".");
			// Sets the character to online.
			this.online = true;
			try {
				this.character.save();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// Tell everyone on the server that this character has logged in.
			Server.notifyAll(new Message(
					"<span class=\"player\">"
							+ this.character.get("name")
							+ "</span><span class=\"normal\"> has logged in!</span><br />",
					MessageType.DISPLAY));
			sendMessage(new Message(null,
					MessageType.SELECT_CHARACTER_SUCCESSFUL));
			// Force the client to execute a look command.
			executeCommand(new Message("look", MessageType.COMMAND));
			sendStats();
		}

		/**
		 * Creates a new user with data from a Message of MessageType
		 * CREATE_USER
		 * 
		 * @param m
		 *            The Message with data to create a new user.
		 */
		private void createUser(Message m) {
			// Data for CREATE_USER comes in the form of a String[]
			// String[0] = username
			// String[1] = password
			// String[2] = Name
			String[] data = (String[]) m.getData();
			String username = data[0];
			String password = data[1];
			String name = data[2];

			int userIdExists = DatabaseConnection.getIDFromUsername(username);

			if (userIdExists > 0) {
				// User already exists.
				sendMessage(new Message("User already exists.",
						MessageType.CREATE_USER_UNSUCCESSFUL));
				return;
			}

			DatabaseConnection.createUser(username, password, name);

			this.user = new User(username);
			addToLog(this.socket + " created new user " + username + ".");
			sendMessage(new Message("User created successfully.",
					MessageType.CREATE_USER_SUCCESSFUL));

		}

		/**
		 * Creates a new character with data from a Message of MessageType
		 * CREATE_CHARACTER
		 * 
		 * @param m
		 *            The Message with data to create a new character.
		 */
		private void createCharacter(Message m) {
			// Data for CREATE_CHARACTER comes in the form of a String[]
			// String[0] = name
			// String[1] = classid
			// String[2] = username
			String[] data = (String[]) m.getData();
			String name = data[0];
			String classid = data[1];
			String username = data[2];

			int characterIDExists = DatabaseConnection.getIDFromCharacter(name);

			if (characterIDExists > 0) {
				// Character already exists.
				sendMessage(new Message("Character already exists.",
						MessageType.CREATE_CHARACTER_UNSUCCESSFUL));
				return;
			}

			DatabaseConnection.createCharacter(name, classid, username);
			this.character = new Character(
					DatabaseConnection.getIDFromCharacter(name));
			addToLog(this.user.get("username") + " created new character "
					+ name + ".");
			sendMessage(new Message("Character created successfully",
					MessageType.CREATE_CHARACTER_SUCCESSFUL));
			updateCharacterReference();
		}

		/**
		 * Sends this Client's Character's stats to the Client.
		 */
		public void sendStats() {

			// Stats will be sent over the network as an Array of Objects.
			// Object[0] = Character's stats as int[]
			// int[0] = HP (max), int[1] = AP (max), int[2] = strength,
			// int[3] =perception, int[4] = endurance, int[5] = charisma,
			// int[6] = intelligence, int[7] = agility, int[8] = luck

			// Object[1] = Character's HP (int)
			// Object[2] = Character's AP (int)
			// Object[3] = Character's Experience (int)
			// Object[4] = Character's Location (int)
			// Object[5] = Character's Class (String)
			updateCharacterReference();
			int[] basicStats = character.getStats();
			int hp = Integer.parseInt(character.get("hp"));
			int ap = Integer.parseInt(character.get("ap"));
			int exp = Integer.parseInt(character.get("experience"));
			Room room = new Room(Integer.parseInt(character.get("location")));
			String roomid = room.get("name");
			String classname = character.getClassType().toString() + " lv. "
					+ character.get("level");
			int nextExp = getNextExp(Integer.parseInt(character.get("level")));
			Object[] messageData = { basicStats, hp, ap, exp, roomid,
					classname, nextExp };

			this.sendMessage(new Message(messageData,
					MessageType.CHARACTER_STATS));
		}

		/**
		 * Sends given Message to the Client.
		 * 
		 * @param m
		 *            The Message to send to the Client.
		 */
		public void sendMessage(Message m) {
			try {
				clients.get(this).writeObject(m);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Grabs updated data from the Database.
		 */
		private void updateCharacterReference() {
			this.character.load();
		}

		/**
		 * 
		 */
		public void sendFonts() {
			if (Boolean.parseBoolean((this.character.get("lights")))) {
				this.sendMessage(new Message(true, MessageType.SET_CLIENT_FONT));
			} else {
				this.sendMessage(new Message(false, MessageType.SET_CLIENT_FONT));
			}
			this.sendMessage(new Message("", MessageType.DISPLAY));
		}

		/**
		 * executeCommand parses Messages of MessageType COMMAND and executes
		 * the relevant ClientCommand.
		 * 
		 * @param m
		 *            The Message with data the Client typed, such as
		 *            "say hello"
		 */
		private void executeCommand(Message m) {

			updateCharacterReference();

			// Data for COMMAND comes in the form of a String
			String whole = (String) m.getData();
			String command = ""; // The command in String form
			String rest = ""; // The rest of the whole String without the
								// command.
			ClientCommand commandToExecute = null;
			int spaceIndex = whole.indexOf(" ");
			if (spaceIndex >= 0) {
				// take out the first "word" from the String, commands always
				// come first.
				// ex: tell Nick Hello!
				// we're merely extracting "tell"
				command = whole.substring(0, spaceIndex);
				rest = whole.substring(spaceIndex, whole.length()).trim();
			} else {
				command = whole.trim();
			}
			// Find out which command the Client wishes to execute.
			for (ClientCommand sc : ClientCommand.values()) {
				String description = sc.getDescription();
				String commandName = description.substring(
						description.indexOf("<b>") + 3,
						description.indexOf("</b>")).toLowerCase();
				if (command.equals(commandName)
						|| command.equals(sc.abbreviatedCommand)) {
					commandToExecute = sc;
				}
			}
			// If the command exists, proceed with parsing the arguments for
			// that command if there are any.
			if (commandToExecute != null) {
				String[] arguments = new String[commandToExecute.args];
				for (int i = 0; i < arguments.length; i++) {
					if (i == arguments.length - 1) {
						arguments[i] = rest.trim();
					} else {
						if (rest.indexOf(" ") <= (rest.length() - 1)
								&& rest.indexOf(" ") != -1) {
							arguments[i] = rest.substring(0, rest.indexOf(" "))
									.trim();
							rest = rest.substring(rest.indexOf(" "),
									rest.length()).trim();
						} else {
							this.sendMessage(new Message(
									"<span class=\"normal\">Please adhere to syntax.</span><br />",
									MessageType.DISPLAY));
							return;
						}
					}
				}
				addToLog(character.get("name") + " executed command " + "\""
						+ whole + "\"");
				commandToExecute.execute(arguments, this);
			} else {
				this.sendMessage(new Message(
						"<span class=\"normal\">Command not recognized. To see all commands say: 'commands'<br />Commands are CaSe SeNsItIvE.</span><br />",
						MessageType.DISPLAY));
			}
			updateCharacterReference();
			// Send stats and light updates.
			if (commandToExecute != ClientCommand.quit) {
				sendStats();
				sendFonts();
			}
			setChanged();
			notifyObservers(log);
		}

	}

	/**
	 * ClientCommand is a Command Pattern implemented in a convenient enum. Each
	 * constant is constructed with two arguments, an integer for number of
	 * arguments for the ClientCommand, and a String for the ClientCommand's
	 * description. Each constant also overrides an execute method, so each
	 * constant has individual behavior.
	 * 
	 * @author Nicholas Eddy, Kyohei Mizokami, Mike Novak, Chris Panzero
	 * 
	 */
	public enum ClientCommand implements Serializable {

		look(
				0,
				"<span class=\"normal\"><b>look</b> at your surroundings.</span>",
				"l") {

			@Override
			void execute(String[] arguments, ClientHandler client) {
				// Create a "map" of where the player is.

				int characterLocation = Integer.parseInt(client.character
						.get("location"));

				Room r = new Room(characterLocation);

				client.sendMessage(new Message("<span class=\"normal\">"
						+ r.get("description") + "</span><br />",
						MessageType.DISPLAY));

				int north = Integer.parseInt(r.get("north"));
				int east = Integer.parseInt(r.get("east"));
				int south = Integer.parseInt(r.get("south"));
				int west = Integer.parseInt(r.get("west"));
				String map = "<table border=\"0\"><tr><td></td>";
				if (north != 0) {
					map += "<td class=\"normal\" border=\"1\">north";
				} else {
					map += "<td class=\"normal\">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
				}
				map += "</td><td></td></tr><tr>";
				if (west != 0) {
					map += "<td class=\"normal\" border=\"1\">west";
				} else {
					map += "<td class=\"normal\">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
				}
				map += "</td><td class=\"player\" border=\"1\"><center>you</center></td>";
				if (east != 0) {
					map += "<td class=\"normal\" border=\"1\">east";
				} else {
					map += "<td class=\"normal\">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
				}
				map += "</td></tr><tr><td></td>";
				if (south != 0) {
					map += "<td class=\"normal\" border=\"1\">south";
				} else {
					map += "<td class=\"normal\">&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
				}
				map += "</td><td></td></tr></table>";

				client.sendMessage(new Message(map, MessageType.DISPLAY));

				Map<String, String> items = DatabaseConnection
						.getItemNamesForRoom(r.getID());

				String itemList = "";
				if (items.size() > 0) {
					itemList = "<span class=\"normal\">You see the following items in the room:</span><br />";
				} else {
					itemList = "<span class=\"normal\">There are no items in this room.</span><br />";
				}

				for (Entry<String, String> s : items.entrySet()) {
					itemList += "<span class=\"item\">" + s.getKey()
							+ "</span><span class=\"normal\"> : "
							+ s.getValue() + "</span><br />";
				}
				client.sendMessage(new Message(itemList, MessageType.DISPLAY));

				// ------------------- Display users -------------------
				List<String> charactersInRoom = Server.getCharactersInRoom(r
						.getID());
				charactersInRoom.remove(client.character.get("name"));
				String charsInRoom = "";
				if (charactersInRoom.size() > 0) {
					charsInRoom = "<span class=\"normal\">You see </span>";
				} else {
					charsInRoom = "<span class=\"normal\">There's no one</span>";
				}
				int i = 0;
				for (String s : charactersInRoom) {
					if (i == charactersInRoom.size() - 1) {
						charsInRoom += "<span class=\"player\">" + s
								+ "</span>";
					} else {
						charsInRoom += "<span class=\"player\">" + s
								+ "</span><span class=\"normal\">, </span>";
					}
					i++;
				}
				client.sendMessage(new Message(
						charsInRoom
								+ "<span class=\"normal\"> in the room with you.</span><br />",
						MessageType.DISPLAY));

				// ------------------- Display NPCs in room -------------------
				String npcsInRoom = "";
				List<Integer> permNPCs = DatabaseConnection
						.getPermanentNPCs(characterLocation);

				if (permNPCs.size() != 0) {
					npcsInRoom = "<span class=\"normal\">You see </span>";
				}

				int j = 0;

				for (int k : permNPCs) {

					NonPlayerCharacter nonPlayerCharacter = new NonPlayerCharacter(
							k);
					if (j == permNPCs.size() - 1) {
						npcsInRoom += "<span class=\"friendly\">"
								+ nonPlayerCharacter.get("name") + "</span>";
					} else {
						npcsInRoom += "<span class=\"friendly\">"
								+ nonPlayerCharacter.get("name")
								+ "</span><span class=\"normal\">, </span>";
					}
					j++;
				}

				if (permNPCs.size() != 0) {
					client.sendMessage(new Message(
							npcsInRoom
									+ "<span class=\"normal\"> in the room with you.</span><br />",
							MessageType.DISPLAY));
				}

				// ----------------- Display enemies in room -----------------

				List<Integer> mobList = DatabaseConnection
						.getMobs(characterLocation);
				int l = 0;
				String mobString = "";

				for (int m : mobList) {
					if (l == mobList.size() - 1) {
						mobString += "<span class=\"enemy\">"
								+ DatabaseConnection.getMobName(m) + "</span>";
					} else {
						mobString += "<span class=\"enemy\">"
								+ DatabaseConnection.getMobName(m)
								+ "</span><span class=\"normal\">, </span>";
					}
					l++;
				}

				if (mobList.size() == 1) {
					client.sendMessage(new Message(
							mobString
									+ "<span class =\"normal\"> is in the room with you!</span><br />",
							MessageType.DISPLAY));
				} else if (mobList.size() > 1) {
					client.sendMessage(new Message(
							mobString
									+ "<span class =\"normal\"> are in the room with you!</span><br />",
							MessageType.DISPLAY));
				}
			}
		},
		move(
				1,
				"<span class=\"normal\"><b>move</b> north/south/east/west.</span>",
				"m") {
			@Override
			void execute(String[] arguments, ClientHandler client) {

				int currentLocation = Integer.parseInt(client.character
						.get("location"));
				Room r = new Room(currentLocation);
				int roomID = r.getID();

				int north = Integer.parseInt(r.get("north"));
				int east = Integer.parseInt(r.get("east"));
				int south = Integer.parseInt(r.get("south"));
				int west = Integer.parseInt(r.get("west"));

				boolean isLocked = DatabaseConnection.getDoorLocked(roomID);

				boolean northLocked = DatabaseConnection.getLockedDoor(roomID)
						.equals("north");
				boolean eastLocked = DatabaseConnection.getLockedDoor(roomID)
						.equals("east");
				boolean southLocked = DatabaseConnection.getLockedDoor(roomID)
						.equals("south");
				boolean westLocked = DatabaseConnection.getLockedDoor(roomID)
						.equals("west");

				// Move north
				if (arguments[0].equals("north") || arguments[0].equals("n")) {
					if (north == 0) {
						client.sendMessage(new Message(
								"<span class=\"normal\">You can't go north!</span><br />",
								MessageType.DISPLAY));
					} else if (!isLocked || !northLocked) {

						client.character.set("location", "" + north);
						try {
							client.character.save();
						} catch (SQLException e) {
							e.printStackTrace();
						}

						roomChangeNotifier(north, currentLocation,
								client.character.getID(), "north");

						client.executeCommand(new Message("look",
								MessageType.COMMAND));
						doMobAttack(north);
					} else if (northLocked && isLocked) {
						int requiredItem = DatabaseConnection
								.getRequiredLock(roomID);
						Item item = new Item(requiredItem);

						client.sendMessage(new Message(
								"<span class=\"normal\">The northern door is locked!<br />You need </span><span class=\"item\">"
										+ item.get("name")
										+ "</span><span class=\"normal\"> to unlock the door.</span><br />",
								MessageType.DISPLAY));
					}
				}
				// Move east
				else if (arguments[0].equals("east")
						|| arguments[0].equals("e")) {
					if (east == 0) {
						client.sendMessage(new Message(
								"<span class=\"normal\">You can't go east!</span><br />",
								MessageType.DISPLAY));
					} else if (!isLocked || !eastLocked) {

						client.character.set("location", "" + east);
						try {
							client.character.save();
						} catch (SQLException e) {
							e.printStackTrace();
						}

						roomChangeNotifier(east, currentLocation,
								client.character.getID(), "east");

						client.executeCommand(new Message("look",
								MessageType.COMMAND));
						doMobAttack(east);
					} else if (isLocked && eastLocked) {
						int requiredItem = DatabaseConnection
								.getRequiredLock(roomID);
						Item item = new Item(requiredItem);

						client.sendMessage(new Message(
								"<span class=\"normal\">The eastern door is locked!<br />You need </span><span class=\"item\">"
										+ item.get("name")
										+ "</span><span class=\"normal\"> to open the door.</span><br />",
								MessageType.DISPLAY));
					}
				}
				// Move south
				else if (arguments[0].equals("south")
						|| arguments[0].equals("s")) {
					if (south == 0) {
						client.sendMessage(new Message(
								"<span class=\"normal\">You can't go south!</span><br />",
								MessageType.DISPLAY));
					} else if (!isLocked || !southLocked) {

						client.character.set("location", "" + south);
						try {
							client.character.save();
						} catch (SQLException e) {
							e.printStackTrace();
						}

						roomChangeNotifier(south, currentLocation,
								client.character.getID(), "south");

						client.executeCommand(new Message("look",
								MessageType.COMMAND));
						doMobAttack(south);
					} else if (isLocked && southLocked) {
						int requiredItem = DatabaseConnection
								.getRequiredLock(roomID);

						Item item = new Item(requiredItem);

						client.sendMessage(new Message(
								"<span class=\"normal\">The southern door is locked!<br />You need </span><span class=\"item\">"
										+ item.get("name")
										+ "</span><span class=\"normal\"> to open the door.</span><br />",
								MessageType.DISPLAY));
					}
				}
				// Move west
				else if (arguments[0].equals("west")
						|| arguments[0].equals("w")) {
					if (west == 0) {
						client.sendMessage(new Message(
								"<span class=\"normal\">You can't go west!</span><br />",
								MessageType.DISPLAY));
					} else if (!isLocked || !westLocked) {
						client.character.set("location", "" + west);
						try {
							client.character.save();
						} catch (SQLException e) {
							e.printStackTrace();
						}

						roomChangeNotifier(west, currentLocation,
								client.character.getID(), "west");

						client.executeCommand(new Message("look",
								MessageType.COMMAND));
						doMobAttack(west);
					} else if (isLocked && westLocked) {
						int requiredItem = DatabaseConnection
								.getRequiredLock(roomID);
						Item item = new Item(requiredItem);

						client.sendMessage(new Message(
								"<span class=\"normal\">The western door is locked!<br />You need </span><span class=\"item\">"
										+ item.get("name")
										+ "</span><span class=\"normal\"> to open the door.</span><br />",
								MessageType.DISPLAY));
					}
				}
			}

			private void roomChangeNotifier(int currentRoom, int previousRoom,
					int movingCharacter, String direction) {

				for (Entry<ClientHandler, ObjectOutputStream> e : getClients()
						.entrySet()) {

					ClientHandler ch = e.getKey();

					if (ch.online) {
						Character character = new Character(movingCharacter);
						if (Integer.parseInt(ch.character.get("location")) == currentRoom
								&& ch.character.getID() != movingCharacter) {

							ch.sendMessage(new Message(
									"<span class=\"player\">"
											+ character.get("name")
											+ "</span><span class=\"normal\"> has entered the room.</span><br />",
									MessageType.DISPLAY));

						} else if (Integer.parseInt(ch.character
								.get("location")) == previousRoom
								&& ch.character.getID() != movingCharacter) {

							ch.sendMessage(new Message(
									"<span class=\"player\">"
											+ character.get("name")
											+ "</span><span class=\"normal\"> has left the room "
											+ direction + "ward.</span><br />",
									MessageType.DISPLAY));

						}

					}

				}

			}

		},
		commands(
				0,
				"<span class=\"normal\"><b>commands</b> lists the available commands.</span>",
				"cmds") {

			@Override
			void execute(String[] arguments, ClientHandler client) {
				String output = "<span class=\"normal\">";
				for (ClientCommand c : ClientCommand.values()) {
					output += c.getDescription() + " - alias: <b>"
							+ c.abbreviatedCommand + "</b><br />";
				}
				output += "</span>";
				client.sendMessage(new Message(output, MessageType.DISPLAY));
				client.sendStats();
			}

		},
		say(
				1,
				"<span class=\"normal\"><b>say</b> 'message' - speak to the room.</span>",
				"say") {
			@Override
			void execute(String[] arguments, ClientHandler client) {

				if (arguments[0].length() < 1) {
					// Can't say nothing.
					client.sendMessage(new Message(
							"<span class=\"normal\">You can't 'say' nothing!</span><br />",
							MessageType.DISPLAY));
					return;
				}

				int location = Integer.parseInt(client.character
						.get("location"));
				for (Entry<ClientHandler, ObjectOutputStream> c : getClients()
						.entrySet()) {

					Character character = c.getKey().character;
					if (character != null) {
						if (Integer.parseInt(character.get("location")) == location
								&& c.getKey().online) {
							// Character is in the same room and online.
							c.getKey()
									.sendMessage(
											new Message(
													"<span class=\"player\">"
															+ client.character
																	.get("name")
															+ "</span><span class=\"normal\">: "
															+ arguments[0]
															+ "</span><br />",
													MessageType.DISPLAY));
						}
					}
				}
			}
		},
		who(
				0,
				"<span class=\"normal\"><b>who</b> lists all players online.</span>",
				"who") {
			@Override
			void execute(String[] arguments, ClientHandler client) {

				String online = "<span class=\"normal\">Online players:</span><br />";

				for (Entry<ClientHandler, ObjectOutputStream> c : getClients()
						.entrySet()) {
					Character character = c.getKey().character;
					if (character != null) {
						online += "<span class=\"player\">"
								+ character.get("name") + "</span><br />";
					}
				}
				client.sendMessage(new Message(online, MessageType.DISPLAY));

			}
		},
		north(
				0,
				"<span class=\"normal\"><b>north</b> move through the north exit.</span>",
				"n") {
			@Override
			void execute(String[] arguments, ClientHandler client) {
				client.executeCommand(new Message("move north",
						MessageType.COMMAND));

			}
		},
		east(
				0,
				"<span class=\"normal\"><b>east</b> move through the east exit.</span>",
				"e") {
			@Override
			void execute(String[] arguments, ClientHandler client) {
				client.executeCommand(new Message("move east",
						MessageType.COMMAND));

			}
		},
		south(
				0,
				"<span class=\"normal\"><b>south</b> move through the south exit.</span>",
				"s") {
			@Override
			void execute(String[] arguments, ClientHandler client) {
				client.executeCommand(new Message("move south",
						MessageType.COMMAND));
			}
		},
		west(
				0,
				"<span class=\"normal\"><b>west</b> move through the west exit.</span>",
				"w") {
			@Override
			void execute(String[] arguments, ClientHandler client) {
				client.executeCommand(new Message("move west",
						MessageType.COMMAND));
			}
		},
		tell(
				2,
				"<span class=\"normal\"><b>tell</b> 'player' 'message' - speak privately to another player.</span>",
				"t") {
			@Override
			void execute(String[] arguments, ClientHandler client) {
				String characterToTell = arguments[0];
				String message = arguments[1];

				if (message.length() < 1) {
					// Can't tell nothing.
					client.sendMessage(new Message(
							"<span class=\"normal\">Who are you trying to tell?</span><br />",
							MessageType.DISPLAY));
					return;
				}
				int characterToTellID = DatabaseConnection
						.getIDFromCharacter(characterToTell);

				Character otherChar = new Character(characterToTellID);

				if (otherChar.getID() == -1) {
					// Character doesn't exist.
					client.sendMessage(new Message(
							"<span class=\"player\">"
									+ arguments[0]
									+ "</span><span class=\"normal\"> doesn't exist.</span><br />",
							MessageType.DISPLAY));
					return;
				}

				if (otherChar.getID() == client.character.getID()) {
					// Player is 'telling' themselves.
					client.sendMessage(new Message(
							"<span class=\"normal\">Talking to yourself?</span><br />",
							MessageType.DISPLAY));
					return;
				}

				// Character is online. Get his ClientHandler so we can send it
				// a message.
				for (Entry<ClientHandler, ObjectOutputStream> e : getClients()
						.entrySet()) {
					if (e.getKey().character.get("name")
							.equals(characterToTell)) {
						if (e.getKey().online) {
							// Character is online.
							// Show on Character to tell side.
							e.getKey()
									.sendMessage(
											new Message(
													"<span class=\"player\">"
															+ client.character
																	.get("name")
															+ "</span><span class=\"normal\"> whispers: "
															+ message
															+ "</span><br />",
													MessageType.DISPLAY));
							// Show on client side as well.
							client.sendMessage(new Message(
									"<span class=\"normal\">You whisper to </span><span class=\"player\">"
											+ e.getKey().character.get("name")
											+ "</span><span class=\"normal\">: "
											+ message + "</span><br />",
									MessageType.DISPLAY));
							return;
						}
					}
				}

				// If it reaches past the return;, the character is not online.
				client.sendMessage(new Message(
						"<span class=\"player\">"
								+ arguments[0]
								+ "</span><span class=\"normal\"> isn't online.</span><br />",
						MessageType.DISPLAY));
			}
		},
		grab(
				1,
				"<span class=\"normal\"><b>grab</b> 'item' - Grabs the item off the ground.</span>",
				"g") {
			@Override
			void execute(String[] arguments, ClientHandler client) {

				if (arguments[0].length() < 1) {
					// Can't grab nothing.
					client.sendMessage(new Message(
							"<span class=\"normal\">What are you trying to grab?</span><br />",
							MessageType.DISPLAY));
					return;
				}

				int itemid = DatabaseConnection.getIDFromItemName(arguments[0]);
				Item item = new Item(itemid);

				if (item.getID() == -1) {
					client.sendMessage(new Message(
							"<span class=\"normal\">What's a </span><span class=\"item\">"
									+ arguments[0]
									+ "</span><span class=\"normal\">?</span><br />",
							MessageType.DISPLAY));
					return;
				}

				if (item.getType() == ItemType.PERMANENT) {
					client.sendMessage(new Message(
							"<span class=\"item\">"
									+ item.get("name")
									+ "</span><span class=\"normal\"> cannot be picked up!</span><br />",
							MessageType.DISPLAY));
					return;
				}

				Map<Integer, Integer> itemsInRoom = DatabaseConnection
						.getItemsForRoom(Integer.parseInt(client.character
								.get("location")));

				if (!itemsInRoom.containsKey(item.getID())) {
					client.sendMessage(new Message(
							"<span class=\"normal\">There's no </span><span class=\"item\">"
									+ arguments[0]
									+ "</span><span class=\"normal\"> to pick up!</span><br />",
							MessageType.DISPLAY));
					return;
				}

				DatabaseConnection.addItemToCharacter(client.getCharacterID(),
						item.getID());
				DatabaseConnection.removeItemFromRoom(
						Integer.parseInt(client.character.get("location")),
						item.getID());
				client.sendMessage(new Message(
						"<span class=\"normal\">You pick up </span><span class=\"item\">"
								+ arguments[0]
								+ "</span><span class=\"normal\">.</span><br />",
						MessageType.DISPLAY));

			}
		},
		inventory(
				0,
				"<span class=\"normal\"><b>inventory</b> lists the items in your inventory.</span>",
				"i") {
			@Override
			void execute(String[] arguments, ClientHandler client) {
				Map<String, String> inventory = DatabaseConnection
						.getInventoryForCharacter(client.getCharacterID(), true);
				String inv = "<span class=\"normal\">Inventory:</span><br />";

				if (inventory.size() == 0) {
					inv += "<span class=\"normal\">You have no items.</span><br />";
				}
				for (Entry<String, String> s : inventory.entrySet()) {
					inv += "<span class=\"item\">" + s.getKey()
							+ "</span><span class=\"normal\"> : "
							+ s.getValue() + "</span><br />";
				}
				client.sendMessage(new Message(inv, MessageType.DISPLAY));
			}
		},
		drop(
				1,
				"<span class=\"normal\"><b>drop</b> 'item' - drops the item on the ground.</span>",
				"d") {
			@Override
			void execute(String[] arguments, ClientHandler client) {

				if (arguments[0].length() < 1) {
					// Can't drop nothing.
					client.sendMessage(new Message(
							"<span class=\"normal\">What are you trying to drop?</span><br />",
							MessageType.DISPLAY));
					return;
				}
				Map<Integer, Integer> inventory = DatabaseConnection
						.getInventoryForCharacter(client.getCharacterID());
				int itemID = DatabaseConnection.getIDFromItemName(arguments[0]);
				Item item = new Item(itemID);

				if (item.getID() < 0) {
					// Item doesn't even exist in the database.
					client.sendMessage(new Message(
							"<span class=\"normal\">What's </span><span class=\"item\">"
									+ arguments[0]
									+ "</span><span class=\"normal\">?</span><br />",
							MessageType.DISPLAY));
					return;
				}

				if (inventory.containsKey(itemID)) {
					// Inventory contains Item
					// Remove it from Inventory, Add it to Room, Unequip it.
					DatabaseConnection.addItemToRoom(
							Integer.parseInt(client.character.get("location")),
							itemID);
					DatabaseConnection.removeItemFromCharacter(
							client.getCharacterID(), itemID);
					DatabaseConnection.unequipItem(client.getCharacterID(),
							itemID);
					client.executeCommand(new Message("inventory",
							MessageType.COMMAND));
				} else {
					client.sendMessage(new Message(
							"<span class=\"normal\">You don't have </span><span class=\"item\">"
									+ arguments[0]
									+ "</span><span class=\"normal\"> to drop!</span>",
							MessageType.DISPLAY));
				}

			}
		},
		use(
				1,
				"<span class=\"normal\"><b>use</b> 'item' - uses the item.</span>",
				"u") {
			@Override
			void execute(String[] arguments, ClientHandler client) {

				Map<Integer, Integer> inventory = DatabaseConnection
						.getInventoryForCharacter(client.getCharacterID());
				Map<Integer, Integer> room = DatabaseConnection
						.getItemIDsForRoom(Integer.parseInt(client.character
								.get("location")));
				int itemID = DatabaseConnection.getIDFromItemName(arguments[0]);

				Item itemToUse = new Item(itemID);

				if (itemToUse.getID() == -1) {
					// Item doesn't even exist in the database.
					client.sendMessage(new Message(
							"<span class=\"normal\">What's </span><span class=\"item\">"
									+ arguments[0]
									+ "</span><span class=\"normal\">?</span><br />",
							MessageType.DISPLAY));
					return;
				}

				if (!Boolean.parseBoolean(itemToUse.get("usable"))) {
					// Item isn't usable.
					client.sendMessage(new Message(
							"<span class=\"normal\">You cannot use </span><span class=\"item\">"
									+ arguments[0]
									+ "</span><span class=\"normal\">!</span><br />",
							MessageType.DISPLAY));
					return;
				}

				int requiredItemID = Integer.parseInt(itemToUse
						.get("requireditem"));
				int requiredQuantity = Integer.parseInt(itemToUse
						.get("requiredquantity"));

				if (Integer.parseInt(client.character.get("level")) < Integer
						.parseInt(itemToUse.get("requiredlevel"))) {
					// Character isn't of sufficient level to use the item.
					client.sendMessage(new Message(
							"<span class=\"normal\">You need to be level "
									+ itemToUse.get("requiredlevel")
									+ " to use </span><span class=\"item\">"
									+ arguments[0]
									+ "</span><span class=\"normal\">!</span><br />",
							MessageType.DISPLAY));
					return;
				}

				if (inventory.containsKey(itemID)) {
					// Item is in the Character's inventory.
					if (requiredItemID == 0) {
						// No required items.
						itemToUse.doEffect(client.getCharacterID());
						client.sendMessage(new Message(
								"<span class=\"normal\">You use </span><span class=\"item\">"
										+ arguments[0]
										+ "</span><span class=\"normal\">.</span><br />",
								MessageType.DISPLAY));
						client.sendMessage(new Message(
								"<span class=\"normal\">"
										+ itemToUse.get("effectdescription")
										+ "</span><br />", MessageType.DISPLAY));
					} else {
						Item requiredItem = new Item(requiredItemID);
						if (inventory.containsKey(requiredItemID)) {
							// Required Item is in Character's Inventory.

							if (inventory.get(requiredItemID) >= requiredQuantity) {
								// Character has sufficient amount of Required
								// Item.
								// Do the Item's Effect and remove Required
								// Quantity amount of Required Item from
								// Character's Inventory.
								itemToUse.doEffect(client.getCharacterID());
								for (int i = 0; i < requiredQuantity; i++) {
									DatabaseConnection.removeItemFromCharacter(
											client.getCharacterID(),
											requiredItemID);
								}
								client.sendMessage(new Message(
										"<span class=\"normal\">You use </span><span class=\"item\">"
												+ arguments[0]
												+ "</span><span class=\"normal\">.</span><br />",
										MessageType.DISPLAY));
								client.sendMessage(new Message(
										"<span class=\"normal\">"
												+ itemToUse
														.get("effectdescription")
												+ "</span><br />",
										MessageType.DISPLAY));
							} else {
								// Character has insufficient amount of Required
								// Item.
								client.sendMessage(new Message(
										"<span class=\"normal\">You need more </span><span class=\"item\">"
												+ requiredItem.get("name")
												+ "</span><span class=\"normal\"> to use </span><span class=\"item\">"
												+ arguments[0]
												+ "</span><br />",
										MessageType.DISPLAY));
							}
						}
					}
				} else if (room.containsKey(itemID)) {
					// Item is in the Room.

					if (requiredItemID == 0) {
						// No required items.
						itemToUse.doEffect(client.getCharacterID());
						client.sendMessage(new Message(
								"<span class=\"normal\">You use </span><span class=\"item\">"
										+ arguments[0]
										+ "</span><span class=\"normal\">.</span><br />",
								MessageType.DISPLAY));
						client.sendMessage(new Message(
								"<span class=\"normal\">"
										+ itemToUse.get("effectdescription")
										+ "</span><br />", MessageType.DISPLAY));
					} else {
						Item requiredItem = new Item(requiredItemID);
						if (inventory.containsKey(requiredItemID)) {
							// Required Item is in Character's Inventory.

							if (inventory.get(requiredItemID) >= requiredQuantity) {
								// Character has sufficient amount of Required
								// Item.
								// Do the Item's Effect and remove Required
								// Quantity amount of Required Item from
								// Character's Inventory.
								itemToUse.doEffect(client.getCharacterID());
								for (int i = 0; i < requiredQuantity; i++) {
									DatabaseConnection.removeItemFromCharacter(
											client.getCharacterID(),
											requiredItemID);
								}
								client.sendMessage(new Message(
										"<span class=\"normal\">You use </span><span class=\"item\">"
												+ arguments[0]
												+ "</span><span class=\"normal\">.</span><br />",
										MessageType.DISPLAY));
								client.sendMessage(new Message(
										"<span class=\"normal\">"
												+ itemToUse
														.get("effectdescription")
												+ "</span><br />",
										MessageType.DISPLAY));
							} else {
								// Character has insufficient amount of Required
								// Item.
								client.sendMessage(new Message(
										"<span class=\"normal\">You need more </span><span class=\"item\">"
												+ requiredItem.get("name")
												+ "</span><span class=\"normal\"> to use </span><span class=\"item\">"
												+ arguments[0]
												+ "</span><br />",
										MessageType.DISPLAY));
							}

						} else {
							// Required Item is not in Character's Inventory.
							client.sendMessage(new Message(
									"<span class=\"normal\">You do not have </span><span class=\"item\">"
											+ arguments[0]
											+ "</span><span class=\"normal\">!</span><br />",
									MessageType.DISPLAY));
						}
					}

				} else {
					// Item is not found.
					client.sendMessage(new Message(
							"<span class=\"normal\">There's no </span><span class=\"item\">"
									+ arguments[0]
									+ "</span><span class=\"normal\"> to use!</span><br />",
							MessageType.DISPLAY));
				}
			}
		},
		quit(
				0,
				"<span class=\"normal\"><b>quit</b> saves and quits the game.</span>",
				"q") {
			@Override
			void execute(String[] arguments, ClientHandler client) {
				client.disconnect();
			}
		},
		shutdown(
				0,
				"<span class=\"normal\"><b>shutdown</b> shuts the server down if you have admin rights.</span>",
				"shutdown") {
			@Override
			void execute(String[] arguments, ClientHandler client) {
				if (DatabaseConnection.isAdmin(client.user.getID())) {
					shutdown();
				} else {
					client.sendMessage(new Message(
							"<span class=\"normal\">You don't have administrator rights.</span><br />",
							MessageType.DISPLAY));
				}
			}
		},
		emote(
				1,
				"<span class=\"normal\"><b>emote</b> 'message' - emotifies a message.</span>",
				"em") {
			@Override
			void execute(String[] arguments, ClientHandler client) {

				if (arguments[0].length() < 1) {
					// Can't EMOTE nothing.
					client.sendMessage(new Message(
							"<span class=\"normal\">You can't 'emote' nothing!</span><br />",
							MessageType.DISPLAY));
					return;
				}

				int location = Integer.parseInt(client.character
						.get("location"));
				for (Entry<ClientHandler, ObjectOutputStream> c : getClients()
						.entrySet()) {

					Character character = c.getKey().character;
					if (character != null) {
						if (Integer.parseInt(character.get("location")) == location) {
							c.getKey().sendMessage(
									new Message("<span class=\"player\">"
											+ client.character.get("name")
											+ "</span><span class=\"normal\"> "
											+ arguments[0] + "</span><br />",
											MessageType.DISPLAY));
						}
					}
				}

			}
		},
		attack(
				1,
				"<span class=\"normal\"><b>attack</b> 'target' - attacks the given target.</span>",
				"a") {
			@Override
			void execute(String[] arguments, ClientHandler client) {

				List<Integer> mobsInRoom = DatabaseConnection.getMobs(Integer
						.parseInt(client.character.get("location")));

				Mob mob = null;
				int uid = 0;

				for (int i : mobsInRoom) {
					if (DatabaseConnection.getMobName(i).equals(arguments[0])) {
						mob = new Mob(DatabaseConnection.getMobIDFromID(i));
						uid = i;
					}
				}

				if (mob == null) {
					// Mob doesn't exist in this room.
					client.sendMessage(new Message(
							"<span class=\"normal\">There's no </span><span class=\"enemy\">"
									+ arguments[0]
									+ "</span><span class=\"normal\"> in this room."
									+ "</span><br />", MessageType.DISPLAY));
					return;
				}

				if (DatabaseConnection.isMobAttacked(uid)) {
					// Mob is already being attacked.
					int attackerID = DatabaseConnection.getMobAttacker(uid);
					if (attackerID == client.getCharacterID()) {
						// This client is the one already attacking the mob.
						doAttack(uid, client, arguments, mob);
					} else {
						// Someone else is attacking the mob.
						Character attackingCharacter = new Character(attackerID);
						client.sendMessage(new Message(
								"<span class=\"enemy\">"
										+ arguments[0]
										+ "</span><span class=\"normal\"> is already being attacked by </span><span class=\"player\">"
										+ attackingCharacter.get("name")
										+ "</span><span class=\"normal\">.</span><br />",
								MessageType.DISPLAY));

					}
					return;
				}

				// Set the mob to being attacked by this character.
				DatabaseConnection.setMobAttacked(uid, client.getCharacterID(),
						true);
				doAttack(uid, client, arguments, mob);
			}

			private void doAttack(int uid, ClientHandler client,
					String[] arguments, Mob mob) {

				int mobHP = DatabaseConnection.getMobHP(uid);
				int characterHP = Integer.parseInt(client.character.get("hp"));
				int defense = client.character.getDefense();
				int characterDamage = client.character.getDamage();
				int mobLevel = DatabaseConnection.getMobLevel(uid);
				int mobDamage = mob.getDamage(mobLevel, MOB_DIFFICULTY)
						- defense;

				mobDamage = mobDamage < 1 ? 1 : mobDamage;

				int mobXP = DatabaseConnection
						.getMobExpValue(DatabaseConnection.getMobIDFromID(uid));

				if ((mobHP - characterDamage) <= 0) {
					// Mob died, so remove it from the UID list, add
					// XP to character, and do drops.

					int npcIndex = 0;

					for (int i : npcUIDs) {
						if (i == uid)
							break;
						npcIndex++;
					}

					npcUIDs.remove(npcIndex);

					int currentXP = Integer.parseInt(client.character
							.get("experience"));
					client.character.set("experience", ""
							+ (currentXP + (mobXP * mobLevel)));
					try {
						client.character.save();
					} catch (SQLException e) {
						e.printStackTrace();
					}

					int numBottleCaps = new Random().nextInt(mobXP);

					for (int i = 0; i < numBottleCaps; i++) {
						// add numBottleCaps to character.
						DatabaseConnection.addItemToCharacter(
								client.getCharacterID(), 21);
						// 21 == bottle cap
					}
					if (numBottleCaps != 0)
						client.sendMessage(new Message(
								"<span class=\"normal\">You killed </span><span class=\"enemy\">"
										+ arguments[0]
										+ "</span><span class=\"normal\">!<br />You gain "
										+ mobXP + " experience.<br />You find "
										+ numBottleCaps
										+ " bottle caps.</span><br />",
								MessageType.DISPLAY));

					// update level whenever we get experience.

					int characterLevel = Integer.parseInt(client.character
							.get("level"));

					int newLevel = Server.getLevelFromExp(currentXP + mobXP);

					if (newLevel > characterLevel) {
						// character has leveled up!

						client.character.set("level", newLevel + "");
						try {
							client.character.save();
						} catch (SQLException e) {
							e.printStackTrace();
						}

						client.sendMessage(new Message(
								"<span class=\"normal\">You have leveled up! You are now level "
										+ newLevel + ".</span><br />",
								MessageType.DISPLAY));
					}
					DatabaseConnection.removeMob(uid);
					return; // mob can't attack back if we kill it.
				} else {
					DatabaseConnection.setMobHP(uid, (mobHP - characterDamage));
					client.sendMessage(new Message(
							"<span class=\"normal\">You attack </span><span class=\"enemy\">"
									+ DatabaseConnection.getMobName(uid)
									+ "</span><span class=\"normal\"> for "
									+ characterDamage + "!</span><br />",
							MessageType.DISPLAY));
				}

				if ((characterHP - mobDamage) <= 0) {
					// Character is dead. Move back to room 1 and
					// reduce
					// experience by 5%

					int reducedXP = (int) (Integer.parseInt(client.character
							.get("experience")) * .95);

					int level = Integer.parseInt(client.character.get("level"));

					client.character
							.set("hp",
									(int) (client.character.getMaxHP(level) * .75)
											+ "");
					client.character.set("location", "1");
					client.character.set("experience", reducedXP + "");

					try {
						client.character.save();
					} catch (SQLException e) {
						e.printStackTrace();
					}

					client.sendMessage(new Message(
							"<span class=\"enemy\">"
									+ DatabaseConnection.getMobName(uid)
									+ "</span><span class=\"normal\"> attacked you for "
									+ mobDamage
									+ " and you nearly died!</span><br /><span class=\"normal\">You barely escape from death"
									+ " and find yourself at the Vault entrance.</span><br />",
							MessageType.DISPLAY));
					// Mob isn't attacked anymore.
					DatabaseConnection.setMobAttacked(uid, 0, false);
					return;
				} else {
					// Character is still alive, take damage and
					// save.
					client.character.set("hp", "" + (characterHP - mobDamage));
					try {
						client.character.save();
					} catch (SQLException e) {
						e.printStackTrace();
					}

					client.sendMessage(new Message(
							"<span class=\"enemy\">"
									+ DatabaseConnection.getMobName(uid)
									+ "</span><span class=\"normal\"> attacked you for "
									+ mobDamage + "!</span><br />",
							MessageType.DISPLAY));
				}
			}

		},
		ooc(
				1,
				"<span class=\"normal\"><b>ooc</b> 'message' - messages everyone connected to UAMUD.</span>",
				"ooc") {

			@Override
			void execute(String[] arguments, ClientHandler client) {

				if (arguments[0].length() < 1) {
					// Can't OOC nothing.
					client.sendMessage(new Message(
							"<span class=\"normal\">You can't 'ooc' nothing!</span><br />",
							MessageType.DISPLAY));
					return;
				}

				for (Entry<ClientHandler, ObjectOutputStream> c : getClients()
						.entrySet()) {
					c.getKey().sendMessage(
							new Message("<span class=\"normal\">(ooc)</span>"
									+ "<span class=\"player\">"
									+ client.character.get("name")
									+ "</span><span class=\"normal\">: "
									+ arguments[0] + "</span><br />",
									MessageType.DISPLAY));
				}
			}

		},
		equip(
				1,
				"<span class=\"normal\"><b>equip</b> 'item' - equips an item.</span>",
				"eq") {

			@Override
			void execute(String[] arguments, ClientHandler client) {

				if (arguments[0].length() == 0) {
					// Display the Character's equipped items.
					Map<Integer, String> equippedItems = DatabaseConnection
							.getEquippedItems(client.getCharacterID());
					String equipped = "<span class=\"normal\">Equipped items:</span><br />";

					int i = 0;
					for (Entry<Integer, String> e : equippedItems.entrySet()) {

						Item item = new Item(e.getKey());

						if (i == (equippedItems.size() - 1)) {
							equipped += "<span class=\"item\">"
									+ item.get("name") + "</span><br />";
						} else {
							equipped += "<span class=\"item\">"
									+ item.get("name")
									+ "</span><span class=\"normal\">, </span>";
						}
						i++;
					}

					if (equippedItems.size() == 0) {
						client.sendMessage(new Message(
								"<span class=\"normal\">You don't have any items equipped.</span><br />",
								MessageType.DISPLAY));
					} else {
						client.sendMessage(new Message(equipped,
								MessageType.DISPLAY));
					}
					return;
				}

				int itemID = DatabaseConnection.getIDFromItemName(arguments[0]);
				Item item = new Item(itemID);
				Map<Integer, Integer> inventory = DatabaseConnection
						.getInventoryForCharacter(client.getCharacterID());

				if (item.getID() < 0) {
					// Item doesn't exist.
					client.sendMessage(new Message(
							"<span class=\"normal\">What's a </span><span class=\"item\">"
									+ arguments[0]
									+ "</span><span class=\"normal\">?</span><br />",
							MessageType.DISPLAY));
					return;
				}

				if (!Boolean.parseBoolean(item.get("equippable"))) {
					// Item isn't equippable.
					client.sendMessage(new Message(
							"<span class=\"item\">"
									+ arguments[0]
									+ "</span><span class=\"normal\"> isn't equippable!</span><br />",
							MessageType.DISPLAY));
					return;
				}

				int requiredLevel = Integer.parseInt(item.get("requiredlevel"));

				if (Integer.parseInt(client.character.get("level")) < requiredLevel) {
					// Character isn't of sufficient level.
					client.sendMessage(new Message(
							"<span class=\"normal\">You need to be level "
									+ requiredLevel
									+ " to equip </span><span class=\"item\">"
									+ arguments[0] + "</span><br />",
							MessageType.DISPLAY));
					return;
				}

				Map<Integer, String> equippedItems = DatabaseConnection
						.getEquippedItems(client.getCharacterID());

				if (equippedItems.containsKey(itemID)) {
					// User already has the item equipped.
					client.sendMessage(new Message(
							"<span class=\"normal\">You already have </span><span class=\"item\">"
									+ arguments[0]
									+ "</span><span class=\"normal\"> equipped!</span><br />",
							MessageType.DISPLAY));
					return;
				}

				String itemType = item.get("type");

				if (equippedItems.containsValue(itemType)) {
					// User already has the same ItemType equipped.
					client.sendMessage(new Message(
							"<span class=\"normal\">You already have a "
									+ itemType.toLowerCase()
									+ " equipped!</span><br />",
							MessageType.DISPLAY));
					return;
				}

				if (!inventory.containsKey(itemID)) {
					// Character doesn't have the Item.
					client.sendMessage(new Message(
							"<span class=\"normal\">You don't have a </span><span class=\"item\">"
									+ arguments[0]
									+ "</span><span class=\"normal\"> to equip!</span><br />",
							MessageType.DISPLAY));
					return;
				} else {
					DatabaseConnection.equipItem(client.getCharacterID(),
							itemID);
					client.sendMessage(new Message(
							"<span class=\"normal\">You equip </span><span class=\"item\">"
									+ arguments[0]
									+ "</span><span class=\"normal\">.</span><br />",
							MessageType.DISPLAY));
				}
			}
		},
		unequip(
				1,
				"<span class=\"normal\"><b>unequip</b> 'item' - unequips an item.</span>",
				"uneq") {

			@Override
			void execute(String[] arguments, ClientHandler client) {
				int itemID = DatabaseConnection.getIDFromItemName(arguments[0]);
				Item item = new Item(itemID);
				Map<Integer, Integer> inventory = DatabaseConnection
						.getInventoryForCharacter(client.getCharacterID());
				Map<Integer, String> equippedItems = DatabaseConnection
						.getEquippedItems(client.getCharacterID());

				if (arguments[0].length() == 0) {
					client.sendMessage(new Message(
							"<span class=\"normal\">What are you trying to unequip?</span><br />",
							MessageType.DISPLAY));
				}

				if (item.getID() < 0) {
					// Item doesn't exist.
					client.sendMessage(new Message(
							"<span class=\"normal\">What's a </span><span class=\"item\">"
									+ arguments[0]
									+ "</span><span class=\"normal\">?</span><br />",
							MessageType.DISPLAY));
					return;
				}

				if (!inventory.containsKey(itemID)) {
					// Character doesn't have the item.
					client.sendMessage(new Message(
							"<span class=\"normal\">You don't have a </span><span class=\"item\">"
									+ arguments[0]
									+ "</span><span class=\"normal\">!</span><br />",
							MessageType.DISPLAY));
					return;
				}

				if (equippedItems.containsKey(itemID)) {
					// Unequip the item.
					DatabaseConnection.unequipItem(client.getCharacterID(),
							itemID);
					client.sendMessage(new Message(
							"<span class=\"normal\">You unequip </span><span class=\"item\">"
									+ arguments[0]
									+ "</span><span class=\"normal\">.</span><br />",
							MessageType.DISPLAY));
				} else {
					// Item isn't equipped.
					if (Boolean.parseBoolean(item.get("equippable"))) {
						// Item is equippable but Character doesn't have it
						// equipped.
						client.sendMessage(new Message(
								"<span class=\"normal\">You don't have </span><span class=\"item\">"
										+ arguments[0]
										+ "</span><span class=\"normal\"> equipped!</span><br />",
								MessageType.DISPLAY));
					} else {
						// Item isn't even equippable.
						client.sendMessage(new Message(
								"<span class=\"item\">"
										+ arguments[0]
										+ "</span><span class=\"normal\"> isn't equippable!</span><br />",
								MessageType.DISPLAY));
					}
				}
			}
		},
		inspect(
				1,
				"<span class=\"normal\"><b>inspect</b> 'item' - inspects an item.</span>",
				"ins") {
			@Override
			void execute(String[] arguments, ClientHandler client) {

				// Make sure item actually exists in the room or in the
				// character's inventory and that the item actually exists.
				int itemID = DatabaseConnection.getIDFromItemName(arguments[0]);
				int roomid = Integer.parseInt(client.character.get("location"));
				Item item = new Item(itemID);
				Map<Integer, Integer> inventory = DatabaseConnection
						.getInventoryForCharacter(client.getCharacterID());
				Map<Integer, Integer> roomItems = DatabaseConnection
						.getItemIDsForRoom(roomid);

				if (item.getID() < 0) {
					// Item doesn't exist.
					client.sendMessage(new Message(
							"<span class=\"normal\">What's a </span><span class=\"item\">"
									+ arguments[0]
									+ "</span><span class=\"normal\">?</span><br />",
							MessageType.DISPLAY));
					return;
				}

				if (inventory.containsKey(itemID)
						|| roomItems.containsKey(itemID)) {
					String itemDescription = DatabaseConnection
							.getItemDescription(DatabaseConnection
									.getIDFromItemName(arguments[0]));
					client.sendMessage(new Message(
							"<span class=\"normal\">You inspect </span><span class=\"item\">"
									+ arguments[0]
									+ "</span><span class=\"normal\">.</span><br />",
							MessageType.DISPLAY));
					client.sendMessage(new Message("<span class=\"normal\">"
							+ itemDescription + "</span><br />",
							MessageType.DISPLAY));
					return;
				} else {
					// Item doesn't exist in the room nor Character doesn't have
					// the item.
					client.sendMessage(new Message(
							"<span class=\"normal\">There's no </span><span class=\"item\">"
									+ arguments[0]
									+ "</span><span class=\"normal\"> anywhere to inspect!</span><br />",
							MessageType.DISPLAY));
					return;
				}
			}
		},
		buy(
				2,
				"<span class=\"normal\"><b>buy</b> 'item' - buys an item from a merchant.</span>",
				"buy") {
			@Override
			void execute(String[] arguments, ClientHandler client) {
				// Check if the NPC exists.

				int npcID = DatabaseConnection.getIDFromNPCName(arguments[0]);

				if (npcID <= 0) {
					// NPC doesn't exist.
					client.sendMessage(new Message(
							"<span class=\"normal\">Who is </span><span class=\"friendly\">"
									+ arguments[0]
									+ "</span><span class=\"normal\">?</span><br />",
							MessageType.DISPLAY));
					return;
				}
				NonPlayerCharacter npc = new NonPlayerCharacter(npcID);
				int npcLocation = Integer.parseInt(npc.get("location"));
				int characterLocation = Integer.parseInt(client.character
						.get("location"));

				if (npcLocation != characterLocation) {
					// Character isn't in the same room.
					client.sendMessage(new Message(
							"<span class=\"normal\">There's no </span><span class=\"friendly\">"
									+ arguments[0]
									+ "</span><span class=\"normal\"> to be seen.</span><br />",
							MessageType.DISPLAY));
					return;
				}

				// Check that the item is valid and that the NPC has it.

				int itemID = DatabaseConnection.getIDFromItemName(arguments[1]);
				Item item = new Item(itemID);

				if (item.getID() <= 0) {
					// Item isn't valid
					client.sendMessage(new Message(
							"<span class=\"normal\">What's a </span><span class=\"item\">"
									+ arguments[1]
									+ "</span><span class=\"normal\">?</span><br />",
							MessageType.DISPLAY));
					return;
				}

				List<Integer[]> npcInventory = DatabaseConnection
						.getNPCInventory(npc.getID());

				boolean npcHasItem = false;
				int price = 0;
				for (Integer[] i : npcInventory) {
					if (i[0] == item.getID()) {
						npcHasItem = true;
						price = i[2];
					}
				}

				if (!npcHasItem) {
					// NPC doesn't have the requested Item.
					client.sendMessage(new Message(
							"<span class=\"friendly\">"
									+ arguments[0]
									+ "</span><span class=\"normal\"> doesn't have </span><span class=\"item\">"
									+ item.get("name")
									+ "</span><span class=\"normal\">.</span><br />",
							MessageType.DISPLAY));
					return;
				}

				// Check Character has the correct amount of Bottle Caps.
				int caps = DatabaseConnection.getBottleCapsForCharacter(client
						.getCharacterID());

				if (caps < price) {
					// Character doesn't have enough Caps
					client.sendMessage(new Message(
							"<span class=\"normal\">You need at least "
									+ price
									+ " caps in order to buy </span><span class=\"item\">"
									+ item.get("name")
									+ "</span><span class=\"normal\">.</span><br />",
							MessageType.DISPLAY));
					return;
				}

				// Character has enough Caps. Add item to character, remove
				// 'price' amount of Caps, remove 1 item from NPC inventory.

				DatabaseConnection.addItemToCharacter(client.getCharacterID(),
						item.getID());

				for (int k = 0; k < price; k++) {
					DatabaseConnection.removeItemFromCharacter(
							client.getCharacterID(), 21);
				}

				DatabaseConnection.removeItemFromNPC(npc.getID(), item.getID());

				client.sendMessage(new Message(
						"<span class=\"normal\">You buy </span><span class=\"item\">"
								+ item.get("name")
								+ "</span><span class=\"normal\"> for " + price
								+ " caps.</span><br />", MessageType.DISPLAY));
			}
		},
		sell(
				2,
				"<span class=\"normal\"><b>sell</b> 'merchant 'item' - sell an item to a merchant.</span>",
				"sell") {
			@Override
			void execute(String[] arguments, ClientHandler client) {

				int npcID = DatabaseConnection.getIDFromNPCName(arguments[0]);

				if (npcID <= 0) {
					// NPC doesn't exist.
					client.sendMessage(new Message(
							"<span class=\"normal\">Who is </span><span class=\"friendly\">"
									+ arguments[0]
									+ "</span><span class=\"normal\">?</span><br />",
							MessageType.DISPLAY));
					return;
				}

				NonPlayerCharacter npc = new NonPlayerCharacter(npcID);

				int npcLocation = Integer.parseInt(npc.get("location"));
				int characterLocation = Integer.parseInt(client.character
						.get("location"));

				if (npcLocation != characterLocation) {
					// Character isn't in the same room.
					client.sendMessage(new Message(
							"<span class=\"normal\">There's no </span><span class=\"friendly\">"
									+ arguments[0]
									+ "</span><span class=\"normal\"> to be seen.</span><br />",
							MessageType.DISPLAY));
					return;
				}

				// Check that the item is valid.

				int itemID = DatabaseConnection.getIDFromItemName(arguments[1]);
				Item item = new Item(itemID);

				if (item.getID() <= 0) {
					// Item isn't valid
					client.sendMessage(new Message(
							"<span class=\"normal\">What's a </span><span class=\"item\">"
									+ arguments[1]
									+ "</span><span class=\"normal\">?</span><br />",
							MessageType.DISPLAY));
					return;
				}

				// Item is valid.

				int price = Integer.parseInt(item.get("requiredlevel")) * 10;
				price += Integer.parseInt(item.get("effectamount")) * 3;

				// Add price amount of caps to the Character. Remove the item.
				DatabaseConnection.removeItemFromCharacter(
						client.getCharacterID(), item.getID());

				for (int i = 0; i < price; i++) {
					DatabaseConnection.addItemToCharacter(
							client.getCharacterID(), 21);
				}

				// Add item to NPC

				DatabaseConnection.addItemToNPC(npc.getID(), item.getID(),
						(int) (price * 1.5));

				client.sendMessage(new Message(
						"<span class=\"normal\">You sell </span><span class=\"item\">"
								+ item.get("name")
								+ "</span><span class=\"normal\"> for " + price
								+ " caps.</span><br />", MessageType.DISPLAY));
			}
		},
		trade(
				2,
				"<span class=\"normal\"><b>trade</b> 'player'  'item you want to trade' or 'accept/refuse' - trade an item to another player.</span>",
				"trade") {

			@Override
			void execute(String[] arguments, ClientHandler client) {
				String characterToTrade = arguments[0];
				String itemYouTrading = arguments[1];

				int itemID = DatabaseConnection
						.getIDFromItemName(itemYouTrading);

				int characterToTradeID = DatabaseConnection
						.getIDFromCharacter(characterToTrade);

				Character otherChar = new Character(characterToTradeID);

				if (characterToTradeID == -1) {
					// Character doesn't exist.
					client.sendMessage(new Message(
							"<span class=\"normal\">Who is </span><span class=\"player\">"
									+ characterToTrade
									+ "</span><span class=\"normal\">?</span><br />",
							MessageType.DISPLAY));
					return;
				}

				// take character you trading to
				ClientHandler otherClient = null;

				for (Entry<ClientHandler, ObjectOutputStream> e : getClients()
						.entrySet()) {
					if (e.getKey().getCharacterID() == characterToTradeID) {
						otherClient = e.getKey();
					}
				}

				if (itemYouTrading.equals("accept")) {
					if (client.getTrade()
							&& otherClient.getCharacterTradingTo() == client
									.getCharacterID()
							&& client.getCharacterTradingTo() == otherClient
									.getCharacterID()) {
						// trade is accepted by both characters
						// ------------------------sending item----------------
						Map<Integer, Integer> inventory = DatabaseConnection
								.getInventoryForCharacter(client
										.getCharacterID());
						int itemSendingID = DatabaseConnection
								.getIDFromItemName(client.getTradingItem());
						Item itemSending = new Item(itemSendingID);

						// check your inventory
						if (itemSending.getID() < 0) {
							// Item doesn't even exist in the database.
							client.sendMessage(new Message(
									"<span class=\"normal\">What's </span><span class=\"item\">"
											+ client.getTradingItem()
											+ "</span><span class=\"normal\">?</span><br />",
									MessageType.DISPLAY));
							return;
						}

						if (inventory.containsKey(itemSendingID)) {
							// Inventory contains Item
							// Remove it from Inventory, Add it to other
							// character you trading with
							DatabaseConnection.removeItemFromCharacter(
									client.getCharacterID(), itemSendingID);
							DatabaseConnection.addItemToCharacter(
									characterToTradeID, itemSendingID);

						}

						// -------------------Receiving item-------------
						Map<Integer, Integer> otherInventory = DatabaseConnection
								.getInventoryForCharacter(characterToTradeID);
						int itemRecievingID = DatabaseConnection
								.getIDFromItemName(otherClient.getTradingItem());
						Item itemRecieving = new Item(itemRecievingID);

						// check other character's inventory

						if (itemRecieving.getID() < 0) {
							// Item doesn't even exist in the database.
							client.sendMessage(new Message(
									"<span class=\"normal\">What's </span><span class=\"item\">"
											+ otherClient.getTradingItem()
											+ "</span><span class=\"normal\">?</span><br />",
									MessageType.DISPLAY));
							return;
						}

						if (otherInventory.containsKey(itemRecievingID)) {
							// Inventory contains Item
							// Remove it from Inventory, Add it to other
							// character you trading with
							DatabaseConnection.removeItemFromCharacter(
									otherClient.getCharacterID(),
									itemRecievingID);
							DatabaseConnection.addItemToCharacter(
									client.getCharacterID(), itemRecievingID);

						}

						// trading transaction is cleared
						client.trade("", -1);
						client.setTrade(false);
						otherClient.trade("", -1);
						otherClient.setTrade(false);

						client.executeCommand(new Message("inventory",
								MessageType.COMMAND));
						otherClient.executeCommand(new Message("inventory",
								MessageType.COMMAND));

						return;
					} else if (otherClient.getCharacterTradingTo() == client
							.getCharacterID()) {
						// one side accept

						otherClient.setTrade(true);

						for (Entry<ClientHandler, ObjectOutputStream> e : getClients()
								.entrySet()) {
							if (e.getKey().character.get("name").equals(
									characterToTrade)) {
								if (e.getKey().online) {
									// Character is online.
									// Show on Character to trade side.
									e.getKey()
											.sendMessage(
													new Message(
															"<span class=\"player\">"
																	+ client.character
																			.get("name")
																	+ "</span><span class=\"normal\"> accepted your offer"
																	+ "\"</span><br />",
															MessageType.DISPLAY));
									// Show on client side as well.
									client.sendMessage(new Message(
											"<span class=\"normal\">What is your offer to </span><span class=\"player\">"
													+ otherClient.character
															.get("name")
													+ "\"</span><br />",
											MessageType.DISPLAY));
								}
							}
						}

						return;
					}
				}

				if (itemYouTrading.equals("refuse")) {
					// trading transaction is cleared
					client.trade(null, -1);
					client.setTrade(false);
					otherClient.trade(null, -1);
					client.setTrade(false);

					// Character is online. Get his ClientHandler so we can send
					// it
					// a message.
					for (Entry<ClientHandler, ObjectOutputStream> e : getClients()
							.entrySet()) {
						if (e.getKey().character.get("name").equals(
								characterToTrade)) {
							if (e.getKey().online) {
								// Character is online.
								// Show on Character to trade side.
								e.getKey()
										.sendMessage(
												new Message(
														"<span class=\"player\">"
																+ e.getKey().character
																		.get("name")
																+ "</span><span class=\"normal\"> refused your offer.</span><br />",
														MessageType.DISPLAY));
								// Show on client side as well.
								client.sendMessage(new Message(
										"<span class=\"normal\">You refused </span><span class=\"player\">"
												+ client.character.get("name")
												+ "</span><span class=\"normal\">'s offer.</span><br />",
										MessageType.DISPLAY));

							}
						}
					}

					return;
				}

				// Characters inventory
				Map<Integer, Integer> inventory = DatabaseConnection
						.getInventoryForCharacter(client.getCharacterID());

				if (!inventory.containsKey(itemID)) {
					// You don't have the Item.
					client.sendMessage(new Message(
							"<span class=\"normal\">You don't have a </span><span class=\"item\">"
									+ itemYouTrading
									+ "</span><span class=\"normal\"> to trade!</span><br />",
							MessageType.DISPLAY));
					return;
				}

				if (otherChar.getID() == client.character.getID()) {
					// Player is 'trading' themselves.
					client.sendMessage(new Message(
							"<span class=\"normal\">Trading with yourself?</span><br />",
							MessageType.DISPLAY));
					return;
				}

				client.trade(itemYouTrading, characterToTradeID);

				// Character is online. Get his ClientHandler so we can send it
				// a message.
				for (Entry<ClientHandler, ObjectOutputStream> e : getClients()
						.entrySet()) {
					if (e.getKey().character.get("name").equals(
							characterToTrade)) {
						if (e.getKey().online) {
							// Character is online.
							// Show on Character to trade side.
							e.getKey()
									.sendMessage(
											new Message(
													"<span class=\"player\">"
															+ client.character
																	.get("name")
															+ "</span><span class=\"normal\"> want to trade you: \""
															+ itemYouTrading
															+ "</span><span class=\"normal\"> <br />"
															+ "Will you accpet/refuse?"
															+ "\"</span><br />",
													MessageType.DISPLAY));
							// Show on client side as well.
							client.sendMessage(new Message(
									"<span class=\"normal\">You try to trade </span><span class=\"player\">"
											+ e.getKey().character.get("name")
											+ "</span><span class=\"normal\">: "
											+ "</span><span class=\"normal\">"
											+ itemYouTrading
											+ "\"</span><br />",
									MessageType.DISPLAY));
							return;
						}
					}
				}

			}
		},
		unlock(
				1,
				"<span class=\"normal\"><b>unlock</b> 'door' unlocks the north/south/east/west door.</span>",
				"unlock") {

			@Override
			void execute(String[] arguments, ClientHandler client) {
				// determine if the door is even locked.

				int roomID = Integer.parseInt(client.character.get("location"));

				boolean isLocked = DatabaseConnection.getDoorLocked(roomID);

				boolean northLocked = DatabaseConnection.getLockedDoor(roomID)
						.equals("north");
				boolean eastLocked = DatabaseConnection.getLockedDoor(roomID)
						.equals("east");
				boolean southLocked = DatabaseConnection.getLockedDoor(roomID)
						.equals("south");
				boolean westLocked = DatabaseConnection.getLockedDoor(roomID)
						.equals("west");

				if (arguments[0].equals("north")) {

					if (isLocked && northLocked) {
						// Check if they have the required item.

						Map<Integer, Integer> inventory = DatabaseConnection
								.getInventoryForCharacter(client
										.getCharacterID());

						int requiredItem = DatabaseConnection
								.getRequiredLock(roomID);
						Item item = new Item(requiredItem);
						if (inventory.containsKey(requiredItem)) {
							// Unlock the door.

							DatabaseConnection.removeItemFromCharacter(
									client.getCharacterID(), requiredItem);
							DatabaseConnection.setDoorLocked(roomID, false);
							client.sendMessage(new Message(
									"<span class=\"normal\">You use </span><span class=\"item\">"
											+ item.get("name")
											+ "</span><span class=\"normal\"> to unlock the north door.</span><br />",
									MessageType.DISPLAY));
						} else {
							// Character doesn't have the item.
							client.sendMessage(new Message(
									"<span class=\"normal\">You need </span><span class=\"item\">"
											+ item.get("name")
											+ "</span><span class=\"normal\"> to unlock the north door.</span><br />",
									MessageType.DISPLAY));
						}
					} else {
						// Door isn't even locked.
						client.sendMessage(new Message(
								"<span class=\"normal\">That door isn't locked.</span><br />",
								MessageType.DISPLAY));
					}

				} else if (arguments[0].equals("east")) {

					if (isLocked && eastLocked) {
						// Check if they have the required item.

						Map<Integer, Integer> inventory = DatabaseConnection
								.getInventoryForCharacter(client
										.getCharacterID());

						int requiredItem = DatabaseConnection
								.getRequiredLock(roomID);
						Item item = new Item(requiredItem);
						if (inventory.containsKey(requiredItem)) {
							// Unlock the door.

							DatabaseConnection.removeItemFromCharacter(
									client.getCharacterID(), requiredItem);
							DatabaseConnection.setDoorLocked(roomID, false);
							client.sendMessage(new Message(
									"<span class=\"normal\">You use </span><span class=\"item\">"
											+ item.get("name")
											+ "</span><span class=\"normal\"> to unlock the east door.</span><br />",
									MessageType.DISPLAY));
						} else {
							// Character doesn't have the item.
							client.sendMessage(new Message(
									"<span class=\"normal\">You need </span><span class=\"item\">"
											+ item.get("name")
											+ "</span><span class=\"normal\"> to unlock the east door.</span><br />",
									MessageType.DISPLAY));
						}

					} else {
						// Door isn't even locked.
						client.sendMessage(new Message(
								"<span class=\"normal\">That door isn't locked.</span><br />",
								MessageType.DISPLAY));
					}

				} else if (arguments[0].equals("south")) {

					if (isLocked && southLocked) {
						// Check if they have the required item.

						Map<Integer, Integer> inventory = DatabaseConnection
								.getInventoryForCharacter(client
										.getCharacterID());

						int requiredItem = DatabaseConnection
								.getRequiredLock(roomID);
						Item item = new Item(requiredItem);
						if (inventory.containsKey(requiredItem)) {
							// Unlock the door.
							DatabaseConnection.removeItemFromCharacter(
									client.getCharacterID(), requiredItem);
							DatabaseConnection.setDoorLocked(roomID, false);
							client.sendMessage(new Message(
									"<span class=\"normal\">You use </span><span class=\"item\">"
											+ item.get("name")
											+ "</span><span class=\"normal\"> to unlock the south door.</span><br />",
									MessageType.DISPLAY));
						} else {
							// Character doesn't have the item.
							client.sendMessage(new Message(
									"<span class=\"normal\">You need </span><span class=\"item\">"
											+ item.get("name")
											+ "</span><span class=\"normal\"> to unlock the south door.</span><br />",
									MessageType.DISPLAY));
						}
					} else {
						// Door isn't even locked.
						client.sendMessage(new Message(
								"<span class=\"normal\">That door isn't locked.</span><br />",
								MessageType.DISPLAY));
					}
				} else if (arguments[0].equals("west")) {

					if (isLocked && westLocked) {
						// Check if they have the required item.

						Map<Integer, Integer> inventory = DatabaseConnection
								.getInventoryForCharacter(client
										.getCharacterID());

						int requiredItem = DatabaseConnection
								.getRequiredLock(roomID);
						Item item = new Item(requiredItem);
						if (inventory.containsKey(requiredItem)) {
							// Unlock the door.

							DatabaseConnection.removeItemFromCharacter(
									client.getCharacterID(), requiredItem);
							DatabaseConnection.setDoorLocked(roomID, false);
							client.sendMessage(new Message(
									"<span class=\"normal\">You use </span><span class=\"item\">"
											+ item.get("name")
											+ "</span><span class=\"normal\"> to unlock the west door.</span><br />",
									MessageType.DISPLAY));
						} else {
							// Character doesn't have the item.
							client.sendMessage(new Message(
									"<span class=\"normal\">You need </span><span class=\"item\">"
											+ item.get("name")
											+ "</span><span class=\"normal\"> to unlock the west door.</span><br />",
									MessageType.DISPLAY));
						}
					} else {
						// Door isn't even locked.
						client.sendMessage(new Message(
								"<span class=\"normal\">That door isn't locked.</span><br />",
								MessageType.DISPLAY));
					}
				}
			}
		},
		talk(1, "<b>talk</b> 'npc/mob' - talks to the non-player character.",
				"talk") {

			@Override
			void execute(String[] arguments, ClientHandler client) {
				List<Integer> mobsInRoom = DatabaseConnection.getMobs(Integer
						.parseInt(client.character.get("location")));

				int mobID = -1;
				int mobUID = -1;

				for (int i : mobsInRoom) {
					if (DatabaseConnection.getMobName(i).equals(arguments[0])) {
						mobID = DatabaseConnection.getMobIDFromID(i);
						mobUID = i;
					}
				}

				int npcID = DatabaseConnection.getIDFromNPCName(arguments[0]);

				if (npcID > 0) {
					NonPlayerCharacter npc = new NonPlayerCharacter(npcID);

					if (Integer.parseInt(npc.get("homeroom")) != Integer
							.parseInt(client.character.get("location"))) {
						// Not in the same room.
						client.sendMessage(new Message(
								"<span class=\"normal\">There's no </span><span class=\"friendly\">"
										+ arguments[0]
										+ "</span><span class=\"normal\"> to be seen.</span><br />",
								MessageType.DISPLAY));
						return;
					}

					List<Integer[]> npcInventory = DatabaseConnection
							.getNPCInventory(npc.getID());
					String toSay = npc.getNPCClass().talk();
					client.sendMessage(new Message("<span class=\"friendly\">"
							+ arguments[0] + "</span><span class=\"normal\">: "
							+ toSay + "</span><br />", MessageType.DISPLAY));
					String itemList = "";
					for (Integer[] itemInfo : npcInventory) {
						// itemid,quantity,price
						Item item = new Item(itemInfo[0]);
						itemList += "<span class=\"normal\">" + itemInfo[1]
								+ " - " + "</span><span class=\"item\">"
								+ item.get("name")
								+ "</span><span class=\"normal\"> - "
								+ itemInfo[2] + " caps</span><br />";
					}
					client.sendMessage(new Message(itemList,
							MessageType.DISPLAY));

				} else if (mobID > 0) {

					if (DatabaseConnection.getMobLocation(mobUID) != Integer
							.parseInt(client.character.get("location"))) {
						// Not in the same room.
						client.sendMessage(new Message(
								"<span class=\"normal\">There's no </span><span class=\"enemy\">"
										+ arguments[0]
										+ "</span><span class=\"normal\"> to be seen.</span><br />",
								MessageType.DISPLAY));
						return;
					}

					Mob mob = new Mob(mobID);
					String mobTalk = mob.getMobClass().talk();
					client.sendMessage(new Message("<span class=\"enemy\">"
							+ arguments[0] + "</span><span class=\"normal\">: "
							+ mobTalk + "</span><br />", MessageType.DISPLAY));

				} else {
					client.sendMessage(new Message(
							"<span class=\"normal\">Who are you trying to talk to?</span><br />",
							MessageType.DISPLAY));
				}

			}
		};

		private final int args;
		private final String description;
		private final String abbreviatedCommand;

		ClientCommand(int args, String description, String abbreviatedCommand) {
			this.args = args;
			this.description = description;
			this.abbreviatedCommand = abbreviatedCommand;
		}

		public String getDescription() {
			return this.description;
		}

		/**
		 * Allows each constant in ClientCommand to have different behavior.
		 * 
		 * @param arguments
		 *            The arguments given by the Client.
		 * @param clientHandler
		 *            The ClientHandler executing this ClientCommand.
		 */
		abstract void execute(String[] arguments, ClientHandler client);
	}

	public void executeServerCommand(String whole) {

		// Data for COMMAND comes in the form of a String
		String command = ""; // The command in String form
		String rest = ""; // The rest of the whole String without the
							// command.
		ServerCommand commandToExecute = null;
		int spaceIndex = whole.indexOf(" ");
		if (spaceIndex >= 0) {
			// take out the first "word" from the String, commands always
			// come first.
			// ex: tell Nick Hello!
			// we're merely extracting "tell"
			command = whole.substring(0, spaceIndex);
			rest = whole.substring(spaceIndex, whole.length()).trim();
		} else {
			command = whole.trim();
		}
		// Find out which command the Client wishes to execute.
		for (ServerCommand sc : ServerCommand.values()) {
			String description = sc.description;
			String commandName = description
					.substring(description.indexOf("<b>") + 3,
							description.indexOf("</b>")).toLowerCase();
			if (command.equals(commandName)) {
				commandToExecute = sc;
			}
		}
		// If the command exists, proceed with parsing the arguments for
		// that command if there are any.
		if (commandToExecute != null) {
			String[] arguments = new String[commandToExecute.args];
			for (int i = 0; i < arguments.length; i++) {
				if (i == arguments.length - 1) {
					arguments[i] = rest.trim();
				} else {
					if (rest.indexOf(" ") <= (rest.length() - 1)
							&& rest.indexOf(" ") != -1) {
						arguments[i] = rest.substring(0, rest.indexOf(" "))
								.trim();
						rest = rest.substring(rest.indexOf(" "), rest.length())
								.trim();
					} else {
						addToLog("Please adhere to syntax.");
						return;
					}
				}
			}
			commandToExecute.execute(arguments);
		} else {
			addToLog("Command not recognized. To see all commands say: 'commands'.");
		}
		setChanged();
		notifyObservers(log);
	}

	private enum ServerCommand {

		shutdown(0, "<b>shutdown</b> - shuts down the server") {
			@Override
			void execute(String[] arguments) {
				shutdown();
			}
		},
		kick(2, "<b>kick</b> 'user' 'reason' - kicks a user") {
			@Override
			void execute(String[] arguments) {

				ClientHandler c = null;

				for (Entry<ClientHandler, ObjectOutputStream> e : getClients()
						.entrySet()) {

					if (e.getKey().character.get("name").equals(arguments[0])) {

						log.add(e.getKey().character.get("name")
								+ " was kicked.");
						c = e.getKey();
					}
				}

				if (c != null) {
					c.sendMessage(new Message("You were kicked. Reason: "
							+ arguments[1], MessageType.CLIENT_KICKED));
					c.disconnect();
				} else {
					log.add(arguments[0] + " not found.");
				}
			}
		},
		ban(2, "<b>ban</b> 'user' 'reason' - bans a user") {

			@Override
			void execute(String[] arguments) {

				ClientHandler c = null;

				for (Entry<ClientHandler, ObjectOutputStream> e : getClients()
						.entrySet()) {

					if (arguments[0].equals(e.getKey().user.get("username"))) {
						log.add("UserID " + e.getKey().user.getID()
								+ " was banned.");
						c = e.getKey();
						DatabaseConnection.setBanned(e.getKey().user.getID(),
								true);
					}
				}

				if (c != null) {
					c.sendMessage(new Message("You were banned. Reason: "
							+ arguments[1], MessageType.CLIENT_KICKED));
					c.disconnect();
				} else {
					log.add(arguments[0] + " not found.");
				}
			}
		},
		banbyIP(2, "<b>banbyip</b> - 'ip' 'reason' - bans an ip") {
			@Override
			void execute(String[] arguments) {

				ClientHandler c = null;

				for (Entry<ClientHandler, ObjectOutputStream> e : getClients()
						.entrySet()) {

					if (arguments[0].equals(e.getKey().user.get("username"))) {
						log.add("UserID " + e.getKey().user.getID()
								+ " was banned by ip.");
						DatabaseConnection.banByIP(e.getKey().socket
								.getInetAddress().toString());
						c = e.getKey();
					}
				}

				if (c != null) {
					c.sendMessage(new Message("You were IP banned. Reason: "
							+ arguments[1], MessageType.CLIENT_KICKED));
					c.disconnect();
				} else {
					log.add(arguments[0] + " not found.");
				}

			}
		},
		deleteuser(1, "<b>deleteuser</b> 'user' - deletes a user.") {
			@Override
			void execute(String[] arguments) {

				User toDelete = null;
				ClientHandler ch = null;

				for (Entry<ClientHandler, ObjectOutputStream> e : getClients()
						.entrySet()) {
					if (arguments[0].equals(e.getKey().user.get("username"))) {
						ch = e.getKey();
						toDelete = ch.user;
					}
				}

				if (ch != null) {
					ch.disconnect();
				} else {
					log.add(arguments[0] + " not found.");
				}

				if (toDelete != null) {
					DatabaseConnection.deleteUser(toDelete.getID());
				}

			}
		},
		deletecharacter(1,
				"<b>deletecharacter</b> 'character' - deletes a character") {
			@Override
			void execute(String[] arguments) {
				Character toDelete = null;
				ClientHandler ch = null;
				for (Entry<ClientHandler, ObjectOutputStream> e : getClients()
						.entrySet()) {
					if (arguments[0].equals(e.getKey().user.get("username"))) {
						ch = e.getKey();
						toDelete = ch.character;
					}
				}

				if (ch != null) {
					ch.disconnect();
				} else {
					log.add(arguments[0] + " not found.");
				}

				if (toDelete != null) {
					DatabaseConnection.deleteUser(toDelete.getID());
				}
			}
		},
		move(2,
				"<b>move</b> 'character' 'roomid' - moves specified player to roomid") {

			@Override
			void execute(String[] arguments) {
				// TODO Auto-generated method stub

			}
		},
		commands(0, "<b>commands</b> - shows all commands.") {

			@Override
			void execute(String[] arguments) {
				String output = "";
				for (ServerCommand c : ServerCommand.values()) {
					output += c.description.substring(
							c.description.indexOf("<b>") + 3,
							c.description.indexOf("</b>")).toLowerCase()
							+ "\n";
				}
				log.add(output);
			}

		},
		listusers(0,
				"<b>listusers</b> - shows all users connected and their IP.") {

			@Override
			void execute(String[] arguments) {

				String output = "Connected Users: \n";

				for (Entry<ClientHandler, ObjectOutputStream> e : getClients()
						.entrySet()) {

					output += e.getKey().user.get("username") + " @ "
							+ e.getKey().socket.getInetAddress().toString();

					if (e.getKey().character != null) {
						output += " with character: "
								+ e.getKey().character.get("name");
					}

				}
				log.add(output);
			}
		};

		private final int args;
		private final String description;

		ServerCommand(int args, String description) {
			this.args = args;
			this.description = description;
		}

		/**
		 * Allows each constant in ClientCommand to have different behavior.
		 * 
		 * @param arguments
		 *            The arguments given by the Client.
		 * @param clientHandler
		 *            The ClientHandler executing this ClientCommand.
		 */
		abstract void execute(String[] arguments);

	}

}
