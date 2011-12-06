package client;

import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.UIManager;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JButton;
import java.awt.BorderLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.Component;
import javax.swing.Box;

public class CreateCharacterDialog extends JDialog {
	private JTextField textField;

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
					CreateCharacterDialog dialog = new CreateCharacterDialog();
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the dialog.
	 */
	public CreateCharacterDialog() {
		setTitle("Create Character");
		setBounds(100, 100, 310, 158);
		getContentPane().setLayout(null);

		textField = new JTextField();
		textField.setBounds(53, 13, 229, 22);
		getContentPane().add(textField);
		textField.setColumns(10);

		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(6, 16, 35, 16);
		getContentPane().add(lblName);

		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(6, 44, 276, 22);
		getContentPane().add(comboBox);

		JPanel panel = new JPanel();
		panel.setBounds(0, 78, 294, 44);
		getContentPane().add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		panel.setBorder(BorderFactory.createEtchedBorder());

		Component horizontalStrut = Box.createHorizontalStrut(80);
		panel.add(horizontalStrut, BorderLayout.WEST);

		Component horizontalStrut_1 = Box.createHorizontalStrut(80);
		panel.add(horizontalStrut_1, BorderLayout.EAST);

		Component verticalStrut = Box.createVerticalStrut(7);
		panel.add(verticalStrut, BorderLayout.NORTH);

		Component verticalStrut_1 = Box.createVerticalStrut(7);
		panel.add(verticalStrut_1, BorderLayout.SOUTH);

		JButton btnCreateCharacter = new JButton("Create Character");
		panel.add(btnCreateCharacter, BorderLayout.CENTER);

	}
}
