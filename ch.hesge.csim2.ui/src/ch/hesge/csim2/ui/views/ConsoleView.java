package ch.hesge.csim2.ui.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.vldocking.swing.docking.DockKey;
import com.vldocking.swing.docking.Dockable;

@SuppressWarnings("serial")
public class ConsoleView extends JPanel  implements Dockable {

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

	public DockKey getDockKey() {
		DockKey dockey = new DockKey("console");
		dockey.setCloseEnabled(true);
		dockey.setMaximizeEnabled(true);
        return dockey;
    }
	
    public Component getComponent(){
        return this;
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
