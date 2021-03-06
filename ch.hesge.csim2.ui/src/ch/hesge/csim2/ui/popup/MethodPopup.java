package ch.hesge.csim2.ui.popup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import ch.hesge.csim2.core.model.SourceMethod;
import ch.hesge.csim2.ui.model.ApplicationManager;

@SuppressWarnings("serial")
public class MethodPopup extends JPopupMenu implements ActionListener {

	// Private attributes
	private SourceMethod method;
	private JMenuItem mnuOpenFile;
	private ApplicationManager appManager;

	/**
	 * Default constructor
	 */
	public MethodPopup() {
		appManager = ApplicationManager.UNIQUE_INSTANCE;
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		mnuOpenFile = new JMenuItem("Open source file");
		mnuOpenFile.addActionListener(this);
		add(mnuOpenFile);
	}

	/**
	 * Return the method concerned by this popup.
	 * 
	 * @return the method
	 */
	public SourceMethod getMethod() {
		return method;
	}

	/**
	 * Sets the method concerned by this popup.
	 * 
	 * @param method
	 *            the method to set
	 */
	public void setMethod(SourceMethod method) {
		this.method = method;
	}

	/**
	 * Handle action generated by menu.
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == mnuOpenFile) {
			appManager.showSourceFile(method);
		}
	}
}
