package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.JComboBox;
import javax.swing.JLabel;

public class CharacterDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

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
		try {
			CharacterDialog dialog = new CharacterDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public CharacterDialog() {
		setResizable(false);
		setTitle("Choose Character");
		setBounds(100, 100, 438, 102);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(81, 6, 345, 22);
		contentPanel.add(comboBox);

		JLabel lblCharacters = new JLabel("Characters:");
		lblCharacters.setBounds(6, 9, 68, 16);
		contentPanel.add(lblCharacters);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(0, 40, 434, 42);
			contentPanel.add(buttonPane);
			buttonPane.setLayout(null);
			{
				JButton okButton = new JButton("Select");
				okButton.setBounds(93, 7, 73, 21);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Log out");
				cancelButton.setBounds(171, 7, 73, 21);
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
			buttonPane.setBorder(BorderFactory.createEtchedBorder());
			{
				JButton btnNew = new JButton("New Character");
				btnNew.setBounds(249, 7, 92, 21);
				buttonPane.add(btnNew);
			}
		}
	}
}
