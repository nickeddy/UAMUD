package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import library.Client;
import library.NumberDocument;
import server.Message;
import server.MessageType;
import javax.swing.UIManager;

/**
 * This class represents a client connecting to UAMUD. It connects via a socket,
 * and allows the user to log in to the server with a user name and password or
 * create a new user. Data is handled by the private inner class ServerHandler
 * which waits for a Message from the Server and handles the data based on the
 * Message's MessageType.
 * 
 * @author Nicholas Eddy, Mike Novak, Kyohei Mizokami, Chris Panzero
 * 
 */
public class ClientGUI extends JFrame {

	private static final long serialVersionUID = -4941736266017508095L;
	private JEditorPane mainTextArea;
	private Client client;
	private ConnectionForm connectionForm;
	private LoginForm loginForm;
	private String hostname;
	private ServerHandler serverHandler;
	private int port;
	private JMenuItem fontMenuItem;
	private JMenu preferencesMenu;
	private JLabel characterInfoLabel;
	private JMenuItem quitMenuItem;
	private JMenu fileMenu;
	private JMenuBar menuBar;
	private JPanel infoPanel;
	private JButton sendButton;
	private JTextField inputTextField;
	private SelectCharacterForm selectCharacterForm;
	private static ClientGUI inst;
	private String displayHistory;
	private JScrollPane scrollPane;
	private String cssString, fontString, normalString, playerString,
			enemyString, itemString, backgroundString, friendlyString;
	private FontPreferencesForm fontPreferencesForm;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				inst = new ClientGUI();
				inst.setLocationRelativeTo(null);
			}
		});
	}

	public ClientGUI() {
		super();
		client = null;
		fontString = " body {font-family:monospace;";
		backgroundString = " background : black;} ";
		normalString = " .normal { color: white; }";
		cssString = "<html><head><style type=\"text/css\">" + fontString
				+ backgroundString + normalString + playerString + itemString
				+ enemyString + friendlyString + " </style><body>";
		initGUI();
		fontPreferencesForm = new FontPreferencesForm(this);
		displayHistory = "";
	}

	private boolean fontLoad() {

		// Try and load font from preferences file.
		File file = new File(System.getProperty("user.dir") + "/src/font.pref");
		try {
			Scanner scan = new Scanner(file);
			for (int i = 0; i < 4; i++) {
				String s = scan.nextLine();
				switch (i) {

				case 0:
					playerString = s;
					break;
				case 1:
					enemyString = s;
					break;
				case 2:
					itemString = s;
					break;
				case 3:
					friendlyString = s;
					break;
				}
			}
		} catch (FileNotFoundException e) {
			return false; // Will load default colors.
		}
		return true;
	}

	private void initGUI() {
		getContentPane().setLayout(null);
		this.setTitle("UAMUD");
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setSize(821, 446);
		this.setResizable(false);
		this.setVisible(false);
		{

			mainTextArea = new JEditorPane();
			// this.add(mainTextArea, null);
			mainTextArea.setContentType("text/html");
			mainTextArea.setEditable(false);
			// mainTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
			// mainTextArea.setBounds(12, 12, 581, 336);

			scrollPane = new JScrollPane(mainTextArea);
			scrollPane.setBorder(BorderFactory.createEtchedBorder());
			scrollPane.setBounds(12, 12, 581, 336);
			scrollPane.setAutoscrolls(true);
			scrollPane
					.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			getContentPane().add(scrollPane);

		}
		{
			inputTextField = new JTextField();
			getContentPane().add(inputTextField);
			inputTextField.setBorder(BorderFactory.createEtchedBorder());
			inputTextField.setBounds(12, 354, 537, 21);
			inputTextField.addKeyListener(new KeyListener() {

				@Override
				public void keyPressed(KeyEvent arg0) {
				}

				@Override
				public void keyReleased(KeyEvent arg0) {
				}

				@Override
				public void keyTyped(KeyEvent arg0) {
					if (arg0.getKeyChar() == KeyEvent.VK_ENTER) {
						sendButton.doClick();
					}
				}
			});
		}
		{
			sendButton = new JButton();
			getContentPane().add(sendButton);
			sendButton.setText("Send");
			sendButton.setBorder(BorderFactory.createEtchedBorder());
			sendButton.setBounds(555, 354, 38, 21);
			sendButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (inputTextField.getText().length() > 0) {
						client.send(new Message(new String(inputTextField
								.getText()), MessageType.COMMAND));
						inputTextField.setText("");
						inputTextField.requestFocus();
					}
				}
			});
		}
		{
			infoPanel = new JPanel();
			getContentPane().add(infoPanel);
			infoPanel.setLayout(null);
			infoPanel.setBorder(BorderFactory.createEtchedBorder());
			infoPanel.setBounds(599, 12, 202, 363);
			{
				characterInfoLabel = new JLabel();
				infoPanel.add(characterInfoLabel);
				characterInfoLabel.setText("Character Information:");
				characterInfoLabel.setBounds(14, 14, 174, 335);
				characterInfoLabel.setVerticalAlignment(SwingConstants.TOP);
			}
		}
		{
			menuBar = new JMenuBar();
			setJMenuBar(menuBar);
			{
				fileMenu = new JMenu();
				menuBar.add(fileMenu);
				fileMenu.setText("File");
				{
					quitMenuItem = new JMenuItem();
					fileMenu.add(quitMenuItem);
					quitMenuItem.setText("Quit");
					quitMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							client.send(new Message(null, MessageType.QUIT));
							System.exit(0);
						}
					});
				}
			}
			{
				preferencesMenu = new JMenu();
				menuBar.add(preferencesMenu);
				preferencesMenu.setText("Preferences");
				{
					fontMenuItem = new JMenuItem();
					preferencesMenu.add(fontMenuItem);
					fontMenuItem.setText("Font");
					fontMenuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							fontPreferencesForm.setVisible(true);
						}
					});
				}
			}
		}
		{
			connectionForm = new ConnectionForm(this);
			connectionForm.setLocationRelativeTo(this);
			connectionForm.setVisible(true);
		}
		{
			loginForm = new LoginForm(this);
			loginForm.setLocationRelativeTo(this);
			loginForm.setVisible(false);
		}
		{
			selectCharacterForm = new SelectCharacterForm(this);
			selectCharacterForm.setLocationRelativeTo(this);
			selectCharacterForm.setVisible(false);
		}
		serverHandler = new ServerHandler();
	}

	@SuppressWarnings("unchecked")
	private void showCharactersAndClassTypes(Message m) {
		if (m.getMessageType() == MessageType.LOGIN_SUCCESSFUL) {

			Object[] data = (Object[]) m.getData();

			List<String> characters = (List<String>) data[1];
			String[] chars = new String[characters.size()];
			int i = 0;
			for (String s : characters) {
				chars[i] = s;
				i++;
			}
			selectCharacterForm.setList(chars);

			List<String> classTypes = (List<String>) data[0];

			ComboBoxModel classListModel = new DefaultComboBoxModel(
					classTypes.toArray());
			selectCharacterForm.classList.setModel(classListModel);

		}

		selectCharacterForm.setVisible(true);
	}

	private void display(Message m) {
		// Expecting Strings only via a MessageType.DISPLAY
		String data = (String) m.getData();
		displayHistory += data;
		mainTextArea.setText(cssString + displayHistory);
		mainTextArea.setCaretPosition(mainTextArea.getDocument().getLength());
	}

	private class ServerHandler extends Thread {

		public ServerHandler() {
			super();
		}

		@Override
		public void run() {
			if (!fontLoad()) {
				playerString = " .player { color : #00FF00; }";
				enemyString = " .enemy { color : #FF3333; }";
				itemString = " .item { color :  #0099FF; }";
				friendlyString = " .friendly { color : #99CC00; }";
			}
			while (true) {
				Message m;
				try {
					m = client.receive();
					switch (m.getMessageType()) {

					case SELECT_CHARACTER_SUCCESSFUL:
						selectCharacterForm.dispose();
						inst.setVisible(true);
						break;
					case SELECT_CHARACTER_UNSUCCESSFUL:
						client.setCharactername("");
						JOptionPane.showMessageDialog(null,
								"Could not login with that character. Wat?");
						break;
					case LOGIN_SUCCESSFUL:
						loginForm.dispose();
						showCharactersAndClassTypes(m);
						break;
					case LOGIN_UNSUCCESSFUL:
						client.setUsername("");
						JOptionPane.showMessageDialog(
								null,
								"Login was unsuccessful! "
										+ (String) m.getData());
						break;
					case CREATE_CHARACTER_UNSUCCESSFUL:
						JOptionPane
								.showMessageDialog(
										null,
										"Could not create character.\nCharacter name may already exist.\nTry another name.");
						break;
					case CREATE_CHARACTER_SUCCESSFUL:
						// selectCharacterForm.dispose();
						// inst.setVisible(true);
						JOptionPane
								.showMessageDialog(null,
										"Character created successfully. Please log in again.");
						client.disconnect();
						System.exit(0);
						break;
					case CREATE_USER_SUCCESSFUL:
						// loginForm.dispose();
						// showCharacters(m);
						JOptionPane.showMessageDialog(null,
								"Created user successfully. Please log in.");
						break;
					case CREATE_USER_UNSUCCESSFUL:
						client.setUsername("");
						JOptionPane.showMessageDialog(
								null,
								"Could not create user. "
										+ (String) m.getData());
						break;
					case DISPLAY:
						display(m);
						break;
					case SET_CLIENT_FONT:
						setFonts(m);
						break;
					case CHARACTER_STATS:
						updateStats(m);
						break;
					case QUIT:
						client.send(new Message("quit", MessageType.COMMAND));
						break;
					case CLIENT_KICKED:
						JOptionPane.showMessageDialog(null,
								"" + (String) m.getData());
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
					client.disconnect();
					System.exit(1);
				}
			}
		}
	}

	private void updateStats(Message m) {
		// Stats will be sent over the network as an Array of Objects.
		// Object[0] = Character's stats as int[]
		// int[0] = HP (max), int[1] = AP (max), int[2] = strength,
		// int[3] =perception, int[4] = endurance, int[5] = charisma,
		// int[6] = intelligence, int[7] = agility, int[8] = luck

		// Object[1] = Character's HP (int)
		// Object[2] = Character's AP (int)
		// Object[3] = Character's Experience (int)
		// Object[4] = Character's Location Name (String)
		// Object[5] = Character's Class (String)

		Object[] data = (Object[]) m.getData();

		int[] stats = (int[]) data[0];
		String infoString = "Character Information:<br /><br />" + "Max HP: "
				+ stats[0] + "<br />Str: " + stats[2] + ", Per: " + stats[3]
				+ "<br />End: " + stats[4] + ", Cha:" + stats[5]
				+ "<br />Int: " + stats[6] + ", Agi: " + stats[7]
				+ "<br />Lck: " + stats[8] + "<br /><br />HP: " + data[1]
				+ "<br />Exp: " + data[3] + "/" + data[6] + "<br />Room: "
				+ data[4] + "<br />Class: " + data[5];

		characterInfoLabel.setText("<html>" + infoString + "</html>");
	}

	public void setFonts(Message m) {
		if (mainTextArea != null) {
			boolean lights = (Boolean) m.getData();
			if (lights) {
				backgroundString = " background : white;} ";
				normalString = " .normal { color: black; }";
			} else {
				backgroundString = " background : black;} ";
				normalString = " .normal { color: white; }";
			}
			cssString = "<html><head><style type=\"text/css\">" + fontString
					+ backgroundString + normalString + playerString
					+ itemString + enemyString + friendlyString
					+ " </style><body>";
			mainTextArea.setText(cssString + displayHistory);
		}
	}

	private class SelectCharacterForm extends JDialog {

		private static final long serialVersionUID = 1L;
		private JList characterList;
		private JTextField characternameTextField;
		private JButton createCharacterButton;
		private JLabel orLabel;
		private JButton loginButton;
		private JComboBox classList;

		public SelectCharacterForm(JFrame frame) {
			super(frame);
			initGUI();
		}

		public void setList(String[] listdata) {
			ListModel listModel = new DefaultComboBoxModel(listdata);
			characterList.setModel(listModel);
		}

		private void initGUI() {

			getContentPane().setLayout(null);
			this.setTitle("Select Character");
			this.setResizable(false);
			{
				characterList = new JList();
				characterList.setBorder(BorderFactory.createEtchedBorder());
				// Don't let them select more than one character
				characterList
						.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				getContentPane().add(characterList);
				characterList.setBounds(12, 12, 268, 217);
			}
			{
				loginButton = new JButton();
				getContentPane().add(loginButton);
				loginButton.setBorder(BorderFactory.createEtchedBorder());
				loginButton.setText("Select Character & Login");
				loginButton.setBounds(12, 235, 268, 21);
				loginButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						String charname = (String) characterList
								.getSelectedValue();
						if (characterList.getSelectedValue() == null) {
							JOptionPane
									.showMessageDialog(null,
											"Please select a character or create a new one.");
						} else {
							client.setCharactername(charname);
							client.send(new Message(charname,
									MessageType.SELECT_CHARACTER));
						}
					}
				});
			}
			{
				orLabel = new JLabel();
				getContentPane().add(orLabel);
				orLabel.setText("- or -");
				orLabel.setHorizontalAlignment(SwingConstants.CENTER);
				orLabel.setBounds(12, 262, 268, 14);
			}
			{
				characternameTextField = new JTextField();
				getContentPane().add(characternameTextField);
				characternameTextField.setBounds(12, 282, 268, 21);
				characternameTextField.setBorder(BorderFactory
						.createEtchedBorder());
			}
			{
				createCharacterButton = new JButton();
				getContentPane().add(createCharacterButton);
				createCharacterButton.setText("Create Character & Login");
				createCharacterButton.setBorder(BorderFactory
						.createEtchedBorder());
				createCharacterButton.setBounds(12, 338, 268, 21);
				createCharacterButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						String name = characternameTextField.getText();
						if (name.length() < 4) {
							JOptionPane
									.showMessageDialog(null,
											"Please enter a character name 4 letters or longer.");
						} else if (classList.getSelectedIndex() < 0) {
							JOptionPane.showMessageDialog(null,
									"Please select a character class.");
						} else {
							client.setCharactername(name);

							String classType = (String) classList
									.getSelectedItem();
							classType = classType.toUpperCase();

							String[] info = { name, classType,
									client.getUsername() };
							client.send(new Message(info,
									MessageType.CREATE_CHARACTER));
						}
					}
				});
			}
			{
				classList = new JComboBox();
				getContentPane().add(classList);
				// classList.setBorder(BorderFactory.createEtchedBorder());
				classList.setBounds(12, 309, 268, 23);
			}
			this.setSize(300, 400);
		}
	}

	private class LoginForm extends JDialog {

		private static final long serialVersionUID = 1L;
		private JLabel usernameLabel;
		private JTextField usernameTextField;
		private JPasswordField passwordField;
		private JButton createNewUserButton;
		private JButton loginButton;
		private JLabel passwordLabel;

		public LoginForm(JFrame frame) {
			super(frame);
			initGUI();
		}

		private void initGUI() {
			getContentPane().setLayout(null);
			this.setTitle("Login");
			{
				usernameLabel = new JLabel();
				getContentPane().add(usernameLabel);
				usernameLabel.setText("Username:");
				usernameLabel.setBounds(12, 12, 112, 16);
			}
			{
				usernameTextField = new JTextField();
				usernameTextField.setBorder(BorderFactory.createEtchedBorder());
				getContentPane().add(usernameTextField);
				usernameTextField.setBounds(12, 34, 257, 23);
			}
			{
				passwordLabel = new JLabel();
				getContentPane().add(passwordLabel);
				passwordLabel.setText("Password:");
				passwordLabel.setBounds(12, 69, 109, 16);
			}
			{
				passwordField = new JPasswordField();
				passwordField.setBorder(BorderFactory.createEtchedBorder());
				getContentPane().add(passwordField);
				passwordField.setBounds(12, 91, 257, 23);
			}
			{
				loginButton = new JButton();
				getContentPane().add(loginButton);
				loginButton.setText("Login");
				loginButton.setBorder(BorderFactory.createEtchedBorder());
				loginButton.setBounds(12, 126, 118, 23);
				loginButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						// Send the login info to the server in a message with
						// type LOGIN.
						String username = usernameTextField.getText();
						char[] passwordChar = passwordField.getPassword();
						if (username.length() == 0 || passwordChar.length == 0) {
							JOptionPane
									.showMessageDialog(null,
											"Please make sure you filled out both username and password fields!");
						} else {

							String password = "";

							for (int i = 0; i < passwordChar.length; i++) {
								password += passwordChar[i];
							}

							client.setUsername(username);
							String[] loginInfo = { username, password };
							client.send(new Message(loginInfo,
									MessageType.LOGIN));
						}
					}
				});
			}
			{
				createNewUserButton = new JButton();
				getContentPane().add(createNewUserButton);
				createNewUserButton.setBorder(BorderFactory
						.createEtchedBorder());
				createNewUserButton.setText("Create New User");
				createNewUserButton.setBounds(135, 126, 134, 23);
				createNewUserButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						String username = usernameTextField.getText();
						char[] passwordChar = passwordField.getPassword();

						String name = "";

						while (name.length() == 0) {
							name = JOptionPane
									.showInputDialog("Please enter your name:");
						}
						if (username.length() == 0 || passwordChar.length == 0) {
							JOptionPane
									.showMessageDialog(null,
											"Please make sure you filled out both username and password fields!");
						} else {
							String password = "";

							for (int i = 0; i < passwordChar.length; i++) {
								password += passwordChar[i];
							}
							String[] loginInfo = { username, password, name };
							client.setUsername(username);
							client.send(new Message(loginInfo,
									MessageType.CREATE_USER));
						}
					}
				});
			}
			this.setSize(290, 196);
		}
	}

	private class ConnectionForm extends JDialog {

		private static final long serialVersionUID = 1L;
		private JTextField hostnameTextField;
		private JLabel hostnameLabel;
		private JTextField portTextField;
		private JButton connectButton;
		private JLabel portLabel;

		public ConnectionForm(JFrame frame) {
			super(frame);
			initGUI();
		}

		private void initGUI() {
			getContentPane().setLayout(null);
			this.setTitle("Connect to UAMUD");
			{
				hostnameTextField = new JTextField("localhost");
				getContentPane().add(hostnameTextField);
				hostnameTextField.setBorder(BorderFactory.createEtchedBorder());
				hostnameTextField.setBounds(12, 32, 287, 20);

			}
			{
				hostnameLabel = new JLabel();
				getContentPane().add(hostnameLabel);
				hostnameLabel.setText("hostname:");
				hostnameLabel.setBounds(12, 12, 102, 14);
			}
			{
				portLabel = new JLabel();
				getContentPane().add(portLabel);
				portLabel.setText("port:");
				portLabel.setBounds(12, 58, 52, 14);
			}
			{
				portTextField = new JTextField(new NumberDocument(), "", 0);
				getContentPane().add(portTextField);
				portTextField.setBounds(12, 78, 287, 21);
				portTextField.setBorder(BorderFactory.createEtchedBorder());
			}
			{
				connectButton = new JButton();
				getContentPane().add(connectButton);
				connectButton.setText("Connect");
				connectButton.setBounds(12, 111, 287, 28);
				connectButton.setBorder(BorderFactory.createEtchedBorder());
				connectButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {

						if (hostnameTextField.getText().length() == 0) {
							JOptionPane.showMessageDialog(null,
									"Please enter a host!");
						} else if (portTextField.getText().length() == 0) {
							JOptionPane.showMessageDialog(null,
									"Please enter a port!");
						} else {
							hostname = hostnameTextField.getText();
							port = Integer.parseInt(portTextField.getText());
							client = new Client(hostname, port);
							connectionForm.dispose();
							loginForm.setVisible(true);
							serverHandler.start();
						}
					}
				});
			}
			this.setResizable(false);
			this.setSize(319, 185);
		}
	}

	private class FontPreferencesForm extends JDialog {

		private static final long serialVersionUID = 1L;
		private JEditorPane demoTextArea;
		private JButton setColorForEnemyTextButton;
		private JButton setBackgroundColorButton;
		private JButton setColorForItemTextButton;
		private JButton setOtherPlayerTextButton;
		private JColorChooser colorChooser;
		private String demoString;

		public FontPreferencesForm(JFrame frame) {
			super(frame);
			initGUI();
		}

		private void initGUI() {
			cssString = "<html><head><style type=\"text/css\">" + fontString
					+ backgroundString + normalString + playerString
					+ itemString + enemyString + friendlyString
					+ " </style><body>";
			demoString = cssString
					+ "<span class =\"normal\">this is an </span><span class=\"item\">item </span>"
					+ "<br /><span class =\"normal\">this is an </span><span class=\"enemy\">enemy</span>"
					+ "<br /><span class=\"normal\">this is a </span><span class=\"player\">player</span>"
					+ "<br /><span class=\"normal\">this is a </span><span class=\"friendly\">friendly npc</span>";
			getContentPane().setLayout(null);
			this.setTitle("Font Preferences");
			{
				demoTextArea = new JEditorPane();
				getContentPane().add(demoTextArea);
				demoTextArea.setContentType("text/html");
				demoTextArea.setEditable(false);
				demoTextArea.setText(demoString);
				demoTextArea.setBounds(12, 251, 460, 161);
				demoTextArea.setBorder(BorderFactory.createEtchedBorder());
			}
			{
				colorChooser = new JColorChooser();
				getContentPane().add(colorChooser);
				colorChooser.setBounds(0, 0, 484, 239);
			}
			{
				setOtherPlayerTextButton = new JButton();
				setOtherPlayerTextButton.setBorder(BorderFactory
						.createEtchedBorder());
				getContentPane().add(setOtherPlayerTextButton);
				setOtherPlayerTextButton.setText("Set Player Color");
				setOtherPlayerTextButton.setBounds(12, 456, 146, 21);
				setOtherPlayerTextButton
						.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent arg0) {
								int r, g, b;
								r = colorChooser.getColor().getRed();
								b = colorChooser.getColor().getBlue();
								g = colorChooser.getColor().getGreen();

								String hexString = toHex(r) + toHex(g)
										+ toHex(b);
								playerString = " .player { color:#" + hexString
										+ "; }";
								updateDemo();
							}
						});
			}
			{
				setColorForEnemyTextButton = new JButton();
				getContentPane().add(setColorForEnemyTextButton);
				setColorForEnemyTextButton.setBorder(BorderFactory
						.createEtchedBorder());
				setColorForEnemyTextButton.setText("Set Enemy Color");
				setColorForEnemyTextButton.setBounds(326, 424, 146, 21);
				setColorForEnemyTextButton
						.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent arg0) {
								int r, g, b;
								r = colorChooser.getColor().getRed();
								b = colorChooser.getColor().getBlue();
								g = colorChooser.getColor().getGreen();

								String hexString = toHex(r) + toHex(g)
										+ toHex(b);
								enemyString = " .enemy { color:#" + hexString
										+ "; }";
								updateDemo();
							}
						});
			}
			{
				setColorForItemTextButton = new JButton();
				getContentPane().add(setColorForItemTextButton);
				setColorForItemTextButton.setBorder(BorderFactory
						.createEtchedBorder());
				setColorForItemTextButton.setText("Set Item Color");
				setColorForItemTextButton.setBounds(169, 424, 146, 21);
				setColorForItemTextButton
						.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent arg0) {
								int r, g, b;
								r = colorChooser.getColor().getRed();
								b = colorChooser.getColor().getBlue();
								g = colorChooser.getColor().getGreen();

								String hexString = toHex(r) + toHex(g)
										+ toHex(b);
								itemString = " .item { color:#" + hexString
										+ "; }";
								updateDemo();
							}
						});
			}
			{
				setBackgroundColorButton = new JButton();
				setBackgroundColorButton.setBorder(BorderFactory
						.createEtchedBorder());
				getContentPane().add(setBackgroundColorButton);
				setBackgroundColorButton.setText("Set NPC Color");
				setBackgroundColorButton.setBounds(169, 456, 146, 21);
				setBackgroundColorButton
						.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent arg0) {
								int r, g, b;
								r = colorChooser.getColor().getRed();
								b = colorChooser.getColor().getBlue();
								g = colorChooser.getColor().getGreen();

								String hexString = toHex(r) + toHex(g)
										+ toHex(b);
								friendlyString = " .friendly { color : #"
										+ hexString + "; } ";
								updateDemo();
							}
						});
			}
			this.setResizable(false);
			this.setSize(492, 523);
			cssString = "<html><head><style type=\"text/css\">" + fontString
					+ backgroundString + normalString + playerString
					+ itemString + enemyString + friendlyString
					+ " </style><body>";
			demoString = cssString
					+ "<span class =\"normal\">this is an </span><span class=\"item\">item</span>"
					+ "<br /><span class =\"normal\">this is an </span><span class=\"enemy\">enemy</span>"
					+ "<br /><span class=\"normal\">this is a </span><span class=\"player\">player</span>"
					+ "<br /><span class=\"normal\">this is a </span><span class=\"friendly\">friendly npc</span></head></html>";
			demoTextArea.setText(demoString);
		}

		private void updateDemo() {
			cssString = "<html><head><style type=\"text/css\">" + fontString
					+ backgroundString + normalString + playerString
					+ itemString + enemyString + friendlyString
					+ " </style><body>";
			demoString = cssString
					+ "<span class =\"normal\">this is an </span><span class=\"item\">item</span>"
					+ "<br /><span class =\"normal\">this is an </span><span class=\"enemy\">enemy</span>"
					+ "<br /><span class=\"normal\">this is a </span><span class=\"player\">player</span>"
					+ "<br /><span class=\"normal\">this is a </span><span class=\"friendly\">friendly npc</span></head></html>";
			demoTextArea.setText(demoString);
			mainTextArea.setText(cssString + displayHistory);
			savePrefs();
		}

		private void savePrefs() {
			// TODO Auto-generated method stub
			File file = new File(System.getProperty("user.dir")
					+ "/src/font.pref");
			FileWriter fileWriter = null;
			try {
				fileWriter = new FileWriter(file);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (fileWriter != null) {
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				try {
					bufferedWriter.write(playerString);
					bufferedWriter.newLine();
					bufferedWriter.write(enemyString);
					bufferedWriter.newLine();
					bufferedWriter.write(itemString);
					bufferedWriter.newLine();
					bufferedWriter.write(friendlyString);
					bufferedWriter.close();
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private String toHex(int i) {
			if (i == 0)
				return "00";
			return "0123456789ABCDEF".charAt((i - i % 16) / 16) + ""
					+ "0123456789ABCDEF".charAt(i % 16);
		}
	}

}
