package ch.hesge.csim2.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import ch.hesge.csim2.ui.utils.SwingUtils;
import ch.hesge.csim2.ui.views.MainView;

@SuppressWarnings("serial")
public class AboutDialog extends JDialog implements ActionListener {

	// Private attributes
	JButton btnOK;
	
	/**
	 * Create the dialog with owner.
	 */
	public AboutDialog(Window parent) {
		super(parent);
		initComponents();
	}

	/**
	 * Initialize the view components
	 */
	private void initComponents() {

		// Dialog configuration
		setTitle("About");
		setBounds(0, 0, 476, 246);
		setLocationRelativeTo(getParent());
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);

		// Create layout structure
		getContentPane().setLayout(new BorderLayout());
		JPanel mainPane = new JPanel();
		getContentPane().add(mainPane, BorderLayout.CENTER);
		JPanel btnPane = new JPanel();
		FlowLayout flowLayout = (FlowLayout) btnPane.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		getContentPane().add(btnPane, BorderLayout.SOUTH);
		mainPane.setLayout(null);

		MainView mainView = (MainView) SwingUtilities.getAncestorOfClass(MainView.class, this);

		// Initialize content pane
		JLabel lblTitle = new JLabel("CSIM2 Environment");
		lblTitle.setFont(new Font("Arial", Font.PLAIN, 28));
		lblTitle.setBounds(46, 35, 270, 34);
		mainPane.add(lblTitle);

		JLabel lblVersion = new JLabel("Version: " + mainView.getApplication().getVersion());
		lblVersion.setFont(new Font("Arial", Font.PLAIN, 14));
		lblVersion.setBounds(46, 70, 402, 34);
		mainPane.add(lblVersion);

		JLabel lblCopyright = new JLabel("Copyright \u00A9 HEG Geneva, Switzerland,  2011-2015");
		lblCopyright.setFont(new Font("Arial", Font.PLAIN, 14));
		lblCopyright.setBounds(46, 120, 402, 34);
		mainPane.add(lblCopyright);

		// Initialize button pane
		btnOK = new JButton("OK");
		btnOK.setPreferredSize(new Dimension(100, 25));
		btnOK.addActionListener(this);
		btnPane.add(btnOK);
		
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
				AboutDialog.this.actionPerformed(new ActionEvent(btnOK, e.getID(), null));
			}
		});

		// Replace default ESCAPE action
		SwingUtils.setInputKeyAction(this.getRootPane(), KeyEvent.VK_ESCAPE, "ESCAPE", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AboutDialog.this.actionPerformed(new ActionEvent(btnOK, e.getID(), null));
			}
		});
	}
	
	/**
	 * Handle action generated by the view.
	 */
	public void actionPerformed(ActionEvent e) {
		this.setVisible(false);
	}
}
