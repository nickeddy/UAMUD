package client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JButton;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.UIManager;
import javax.swing.JMenuItem;
import javax.swing.border.EmptyBorder;

public class ClientGUIV2 {

	private JFrame frmUamud;
	private JTextField textField;
	private LoginDialog loginDialog;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager
					.setLookAndFeel("org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUIV2 window = new ClientGUIV2();
					window.frmUamud.setVisible(false);
					window.loginDialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientGUIV2() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmUamud = new JFrame();
		loginDialog = new LoginDialog();
		frmUamud.setVisible(false);
		frmUamud.getContentPane().setFocusTraversalKeysEnabled(false);
		frmUamud.getContentPane().setFocusable(false);
		frmUamud.setTitle("UAMUD 2.0");
		frmUamud.setBounds(100, 100, 743, 475);
		frmUamud.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmUamud.getContentPane().setLayout(
				new FormLayout(new ColumnSpec[] {
						FormFactory.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("500px:grow(3)"),
						FormFactory.RELATED_GAP_COLSPEC,
						new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(
								Sizes.MINIMUM, Sizes.constant("200px", true),
								Sizes.constant("300px", true)), 1),
						FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
						FormFactory.RELATED_GAP_ROWSPEC,
						RowSpec.decode("250px:grow"),
						FormFactory.NARROW_LINE_GAP_ROWSPEC,
						RowSpec.decode("23px"),
						FormFactory.RELATED_GAP_ROWSPEC, }));

		JEditorPane editorPane = new JEditorPane();
		editorPane.setEditable(false);
		frmUamud.getContentPane().add(editorPane, "2, 2, fill, fill");
		editorPane.setBorder(BorderFactory.createEtchedBorder());

		JLabel lblInfo = new JLabel("Information");
		frmUamud.getContentPane().add(lblInfo, "4, 2");

		textField = new JTextField();
		frmUamud.getContentPane().add(textField, "2, 4, fill, bottom");
		textField.setColumns(10);

		JButton btnSend = new JButton("Send");
		frmUamud.getContentPane().add(btnSend, "4, 4, fill, bottom");

		JMenuBar menuBar = new JMenuBar();
		frmUamud.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmQuit = new JMenuItem("Quit");
		mnFile.add(mntmQuit);

	}

	private class LoginDialog extends JDialog {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final JPanel contentPanel = new JPanel();
		private JTextField textField;
		private JPasswordField passwordField;

		/**
		 * Create the dialog.
		 */
		public LoginDialog() {
			setResizable(false);
			setTitle("Login to UAMUD");
			setBounds(100, 100, 450, 150);
			getContentPane().setLayout(new BorderLayout());
			contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(contentPanel, BorderLayout.CENTER);
			contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					ColumnSpec.decode("68px"),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					ColumnSpec.decode("334px:grow"),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC, }, new RowSpec[] {
					FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("22px"),
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.UNRELATED_GAP_ROWSPEC, }));

			JLabel lblUsername = new JLabel("Username");
			contentPanel.add(lblUsername, "2, 2, center, center");

			textField = new JTextField();
			contentPanel.add(textField, "4, 2, fill, top");
			textField.setColumns(10);

			JLabel lblPassword = new JLabel("Password");
			contentPanel.add(lblPassword, "2, 4, center, center");

			passwordField = new JPasswordField();
			contentPanel.add(passwordField, "4, 4, fill, default");
			{
				JPanel buttonPane = new JPanel();
				getContentPane().add(buttonPane, BorderLayout.SOUTH);
				buttonPane.setLayout(new FormLayout(new ColumnSpec[] {
						ColumnSpec.decode("278px"), ColumnSpec.decode("73px"),
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						ColumnSpec.decode("73px"), }, new RowSpec[] {
						FormFactory.RELATED_GAP_ROWSPEC,
						RowSpec.decode("21px"),
						FormFactory.RELATED_GAP_ROWSPEC, }));
				{
					JButton okButton = new JButton("Login");
					okButton.setActionCommand("LOGIN");
					buttonPane.add(okButton, "2, 2, left, top");
					getRootPane().setDefaultButton(okButton);
				}
				{
					JButton newUserButton = new JButton("New User");
					newUserButton.setActionCommand("NEW_USER");
					buttonPane.add(newUserButton, "4, 2, left, top");
				}
				buttonPane.setBorder(BorderFactory.createEtchedBorder());
			}
		}
	}
}
