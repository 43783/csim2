package ch.hesge.csim2.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import ch.hesge.csim2.ui.utils.SwingUtils;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class ProjectDialog extends JDialog implements ActionListener {

	// Private attributes
	private JButton btnOK;
	private JButton btnCancel;
	private JTextField nameField;
	private boolean dialogResult;

	/**
	 * Create the dialog with owner.
	 */
	public ProjectDialog(Window parent) {
		super(parent);
		initComponents();
	}

	/**
	 * Initialize the view components
	 */
	private void initComponents() {

		// Dialog configuration
		setTitle("New Name");
		setBounds(0, 0, 279, 147);
		setLocationRelativeTo(getParent());
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);

		// Create layout structure
		getContentPane().setLayout(new BorderLayout());
		JPanel mainPane = new JPanel();
		mainPane.setBorder(new TitledBorder(null, "Fields", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(mainPane, BorderLayout.CENTER);
		JPanel btnPane = new JPanel();
		FlowLayout flowLayout = (FlowLayout) btnPane.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		getContentPane().add(btnPane, BorderLayout.SOUTH);
		mainPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Name:");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel.setBounds(10, 25, 81, 25);
		mainPane.add(lblNewLabel);

		nameField = new JTextField();
		nameField.setBounds(101, 25, 141, 25);
		mainPane.add(nameField);
		nameField.setColumns(10);

		// Initialize OK button
		btnOK = new JButton("OK");
		btnOK.setPreferredSize(new Dimension(80, 25));
		btnOK.addActionListener(this);
		btnPane.add(btnOK);

		// Initialize Cancel button
		btnCancel = new JButton("Cancel");
		btnCancel.setPreferredSize(new Dimension(80, 25));
		btnCancel.addActionListener(this);
		btnPane.add(btnCancel);

		initListeners();
	}

	/**
	 * Initialize component listeners
	 */
	private void initListeners() {

		// Replace default ENTER action
		SwingUtils.setInputKeyAction(this.getRootPane(), KeyEvent.VK_ESCAPE, "ENTER", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ProjectDialog.this.actionPerformed(new ActionEvent(btnOK, e.getID(), null));
			}
		});

		// Replace default ESCAPE action
		SwingUtils.setInputKeyAction(this.getRootPane(), KeyEvent.VK_ESCAPE, "ESCAPE", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ProjectDialog.this.actionPerformed(new ActionEvent(btnCancel, e.getID(), null));
			}
		});
	}

	/**
	 * Return dialog result.
	 * true = user clicked on OK
	 * false = use clicked on Cancel or ESC
	 * 
	 * @return the dialogResult
	 */
	public boolean getDialogResult() {
		return dialogResult;
	}

	/**
	 * Return the name field
	 * @return
	 *         the new name
	 */
	public String getNameField() {
		return nameField.getText();
	}

	/**
	 * Sets the name field.
	 * 
	 * @param name
	 *        the name value
	 */
	public void setNameField(String name) {
		nameField.setText(name);
	}

	/**
	 * Handle action generated by the view.
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnOK) {
			dialogResult = true;
			this.setVisible(false);
		}
		else if (e.getSource() == btnCancel) {
			dialogResult = false;
			this.setVisible(false);
		}
	}
}
