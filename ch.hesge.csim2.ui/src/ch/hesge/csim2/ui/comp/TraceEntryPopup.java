package ch.hesge.csim2.ui.comp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

@SuppressWarnings("serial")
public class TraceEntryPopup extends JPopupMenu implements ActionListener {

	// Private attributes
	private JMenuItem mnuOpenFile;
	private ActionListener actionListener;

	/**
	 * Default constructor
	 */
	public TraceEntryPopup(JComponent owner) {
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		mnuOpenFile = new JMenuItem("Open file");
		mnuOpenFile.addActionListener(this);
		add(mnuOpenFile);
	}

	/**
	 * Add an action listener to handle menu selection.
	 * 
	 * @param actionListener
	 */
	public void addActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
	}

	/**
	 * Handle action generated by menu.
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == mnuOpenFile && actionListener != null) {
			actionListener.actionPerformed(new ActionEvent(e.getSource(), e.getID(), "OPEN_FILE"));
		}
	}
}
