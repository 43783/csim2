package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

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

		logArea = new JTextArea();
		logArea.setEditable(true);
		logArea.setFont(new Font("Courier New", Font.PLAIN, 14));
		JScrollPane scrollPanel = new JScrollPane();
		scrollPanel.setViewportView(logArea);
		
		add(scrollPanel, BorderLayout.CENTER);		
	}

	/**
	 * Clear console logs.
	 */
	public void clearLogConsole() {
		logArea.setText("");
	}

	/**
	 * Return the console log area.
	 * 
	 * @return the coJTextArea used to log messages
	 */
	public JTextArea getLogArea() {
		return logArea;
	}

}
