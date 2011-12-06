package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import library.NumberDocument;

public class ServerGUI extends JFrame implements Observer {

	private static final long serialVersionUID = 2090152685347911189L;
	private JTextField commandTextField;
	private JButton commandButton;
	private JEditorPane serverMainTextArea;
	private Server server;
	private PortDialog portDialog;
	private static ServerGUI inst;
	private List<String> log;
	private JScrollPane scrollPane;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				inst = new ServerGUI();
				inst.setLocationRelativeTo(null);
			}
		});
	}

	public ServerGUI() {
		super();
		initGUI();
		portDialog = new PortDialog(this);
	}

	private void initGUI() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		{
			serverMainTextArea = new JEditorPane();
			getContentPane().add(serverMainTextArea);
			serverMainTextArea.setBorder(BorderFactory.createEtchedBorder());
			serverMainTextArea.setEditable(false);

			scrollPane = new JScrollPane(serverMainTextArea);
			scrollPane.setBorder(BorderFactory.createEtchedBorder());
			scrollPane.setBounds(12, 12, 696, 242);
			scrollPane.setAutoscrolls(true);
			scrollPane
					.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			this.add(scrollPane);

		}
		{
			commandTextField = new JTextField();
			getContentPane().add(commandTextField);
			commandTextField.setBorder(BorderFactory.createEtchedBorder());
			commandTextField.setBounds(12, 266, 564, 21);
		}
		{
			commandButton = new JButton();
			getContentPane().add(commandButton);
			commandButton.setText("execute");
			commandButton.setBorder(BorderFactory.createEtchedBorder());
			commandButton.setBounds(582, 266, 126, 21);
			commandButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					server.executeServerCommand(commandTextField.getText());
					commandTextField.setText("");
				}
			});
		}

		this.setResizable(false);
		this.setSize(728, 333);
		this.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				Server.shutdown();
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
			}
		});
	}

	private class PortDialog extends JDialog {

		private static final long serialVersionUID = 1L;
		private JTextField portTextField;
		private JTextField difficultyTextField;
		private JButton startServerButton;

		public PortDialog(JFrame frame) {
			super(frame);
			initGUI();
		}

		private void initGUI() {
			this.setLayout(null);
			this.setLocationRelativeTo(null);
			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			{
				JLabel portLabel = new JLabel();
				getContentPane().add(portLabel);
				portLabel.setText("Port:");
				portLabel.setBounds(12, 12, 102, 14);
			}
			{
				portTextField = new JTextField(new NumberDocument(), "", 0);
				getContentPane().add(portTextField);
				portTextField.setBorder(BorderFactory.createEtchedBorder());
				portTextField.setBounds(12, 32, 287, 20);
			}
			{
				JLabel difficultyLabel = new JLabel();
				getContentPane().add(difficultyLabel);
				difficultyLabel.setText("Difficulty: (2-4 recommended)");
				difficultyLabel.setBounds(12, 58, 242, 14);
			}
			{
				difficultyTextField = new JTextField(new NumberDocument(), "",
						0);
				getContentPane().add(difficultyTextField);
				difficultyTextField.setBorder(BorderFactory
						.createEtchedBorder());
				difficultyTextField.setBounds(12, 78, 287, 20);
			}
			{
				startServerButton = new JButton();
				getContentPane().add(startServerButton);
				startServerButton.setText("Start Server");
				startServerButton.setBorder(BorderFactory.createEtchedBorder());
				startServerButton.setBounds(12, 111, 287, 28);
				startServerButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {

						if (portTextField.getText().length() == 0) {
							JOptionPane.showMessageDialog(null,
									"Please enter a port.");
						} else if (difficultyTextField.getText().length() == 0
								|| Integer.parseInt(difficultyTextField
										.getText()) < 1) {
							JOptionPane.showMessageDialog(null,
									"Please enter a difficulty of at least 1.");
						} else {
							int port = Integer.parseInt(portTextField.getText());
							int difficulty = Integer
									.parseInt(difficultyTextField.getText());
							server = new Server(port, difficulty);
							Thread t = new Thread(server);
							t.start();
							inst.setTitle("UAMUD on port "
									+ portTextField.getText());
							portDialog.dispose();
							inst.setVisible(true);
							server.addObserver(inst);
						}
					}
				});
			}
			this.setResizable(false);
			this.setSize(319, 185);
			this.setVisible(true);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable arg0, Object arg1) {
		this.log = (List<String>) arg1;
		String text = "";
		for (String s : log) {
			text += s + "\n";
		}
		serverMainTextArea.setText(text);
	}
}
