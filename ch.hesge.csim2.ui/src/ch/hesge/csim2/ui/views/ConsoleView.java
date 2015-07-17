package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import ch.hesge.csim2.ui.popup.ConsolePopup;

@SuppressWarnings("serial")
public class ConsoleView extends JPanel {

	// Private attributes
	private JTextArea logArea;

	/**
	 * Default constructor.
	 */
	public ConsoleView() {
		initComponent();
	}

	/**
	 * Initialize the component
	 */
	private void initComponent() {

		setLayout(new BorderLayout(0, 0));

		// Create the log area
		logArea = new JTextArea();
		logArea.setEditable(true);
		logArea.setFont(new Font("Courier New", Font.PLAIN, 14));
		JScrollPane scrollPanel = new JScrollPane();
		scrollPanel.setViewportView(logArea);

		add(scrollPanel, BorderLayout.CENTER);

		initListeners();
	}

	/**
	 * Initialize component inner listeners
	 */
	private void initListeners() {

		// Listen to mouse click
		logArea.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {

				// Handle single right-click
				if (SwingUtilities.isRightMouseButton(e)) {

					// Show context menu
					ConsolePopup contextMenu = new ConsolePopup();
					contextMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}

	/**
	 * Clear console logs.
	 */
	public void clearLogs() {
		logArea.setText("");
	}

	/**
	 * Return the console log area.
	 * 
	 * @return the JTextArea used to log messages
	 */
	public JTextArea getLogArea() {
		return logArea;
	}

}
